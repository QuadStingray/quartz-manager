import { ref } from '#imports'
import { useQuartzApi } from '../api/quartzApi';
import type { Router } from 'vue-router';
import {FetchError} from "~/composables/generated";

export function useTokenManager() {
    const { authApi } = useQuartzApi();

    // State
    const isAuthenticated = ref(false);
    const isCheckingToken = ref(false);
    const tokenExpiresAt = ref<Date | null>(null);
    const refreshInterval = ref<ReturnType<typeof setInterval> | null>(null);
    const cookieToken = ref<string | null>(null);
    const authDisabled = ref<boolean | null>(null); // null = not checked yet, true = auth disabled (404), false = auth enabled

    // Constants
    const TOKEN_COOKIE_NAME = 'authToken';
    const REFRESH_BEFORE_EXPIRY_MS = 5 * 60 * 1000; // Refresh 5 minutes before expiry
    const CHECK_INTERVAL_MS = 60 * 1000; // Check token every minute

    let _cookieAccessor: any = null;

    // Lazy: Router nur auflösen, wenn wirklich benötigt und im gültigen Kontext
    const getRouter = (): Router | null => {
        try {
            return useRouter();
        } catch {
            return null;
        }
    };

    // Initialize cookie accessor (lazy, nur wenn Funktion aufgerufen wird)
    const initCookieAccessor = () => {
        if (!_cookieAccessor) {
            try {
                _cookieAccessor = useCookie(TOKEN_COOKIE_NAME);
            } catch (e) {
                console.warn('Could not initialize cookie accessor:', e);
            }
        }
        return _cookieAccessor;
    };

    // Update the cookieToken ref from the cookie
    const updateCookieToken = () => {
        try {
            const authTokenCookie = initCookieAccessor();
            if (authTokenCookie) {
                cookieToken.value = authTokenCookie.value;
            }
        } catch (e) {
            console.warn('Could not update cookie token:', e);
        }
        return cookieToken.value;
    };

    // Get token expiration from cookie
    const getTokenExpiration = (): Date | null => {
        if (!cookieToken.value) return null;
        try {
            return tokenExpiresAt.value;
        } catch (error) {
            console.error('Error parsing token expiration:', error);
            return null;
        }
    };

    // Check if auth is disabled (returns true if login endpoint returns 404)
    const checkAuthAvailability = async (): Promise<boolean> => {
        if (authDisabled.value !== null) {
            return authDisabled.value;
        }

        try {
            // Try to call the login endpoint without credentials to check availability
            const response = await authApi.loginRaw();
            // If we get here without error, auth is enabled
            authDisabled.value = false;
            return false;
        } catch (error: any) {
            // Check if it's a 404 response - handle both direct response and fetch errors
            const status = error?.response?.status || error?.status || error?.data?.status;

            // Check error message for 404 indication (CORS errors with 404)
            const errorMessage = error?.message || error?.toString() || '';
            const isCorsError = errorMessage.includes('CORS') || errorMessage.includes('NetworkError') || errorMessage === "The request failed and the interceptors did not return an alternative response";

            if (status === 404 || isCorsError || errorMessage.includes('404')) {
                // Auth endpoint not found, disable auth
                authDisabled.value = true;
                return true;
            }
            // Any other error means auth is enabled but failed for other reasons (401, 403, etc.)
            authDisabled.value = false;
            return false;
        }
    };

    // Check if token is valid and update state
    const checkToken = async (force = false): Promise<boolean> => {
        // First check if auth is disabled
        const isAuthDisabled = await checkAuthAvailability();
        if (isAuthDisabled) {
            // Auth is disabled, always return true
            isAuthenticated.value = true;
            return true;
        }

        if (isCheckingToken.value && !force) return isAuthenticated.value;

        try {
            updateCookieToken();
        } catch {
            // Ignorieren, falls Timer-Kontext o. Ä.
        }

        if (!cookieToken.value) {
            isAuthenticated.value = false;
            return false;
        }

        isCheckingToken.value = true;

        try {
            const response = await authApi.checkToken();
            tokenExpiresAt.value = new Date(response.expiresAt);
            isAuthenticated.value = true;
            return true;
        } catch (error: any) {
            console.error('Token check failed:', error);
            isAuthenticated.value = false;

            if (error?.response?.status === 401) {
                clearToken();
                const router = getRouter();
                if (router) {
                    router.push('/login');
                }
            }

            return false;
        } finally {
            isCheckingToken.value = false;
        }
    };

    // Refresh token if needed
    const refreshTokenIfNeeded = async (): Promise<boolean> => {
        // If auth is disabled, no need to refresh
        if (authDisabled.value === true) {
            return true;
        }

        const expiration = getTokenExpiration();
        if (!expiration) return false;

        const now = new Date();
        const timeUntilExpiry = expiration.getTime() - now.getTime();

        if (timeUntilExpiry < REFRESH_BEFORE_EXPIRY_MS) {
            try {
                const response = await authApi.extendToken();

                try {
                    const authTokenCookie = initCookieAccessor();
                    if (authTokenCookie) {
                        authTokenCookie.value = response.authToken;
                    }
                } catch (e) {
                    console.warn('Could not update cookie directly, will update on next component render');
                }

                cookieToken.value = response.authToken;
                tokenExpiresAt.value = new Date(response.expiresAt);
                isAuthenticated.value = true;

                return true;
            } catch (error: any) {
                console.error('Token refresh failed:', error);

                if (error?.response?.status === 401) {
                    clearToken();
                    const router = getRouter();
                    if (router) {
                        router.push('/login');
                    }
                }

                return false;
            }
        }

        return true;
    };

    // Clear token
    const clearToken = () => {
        try {
            const authTokenCookie = initCookieAccessor();
            if (authTokenCookie) {
                authTokenCookie.value = null;
            }
        } catch {
            console.warn('Could not clear cookie directly');
        }

        cookieToken.value = null;
        isAuthenticated.value = false;
        tokenExpiresAt.value = null;
    };

    // Start periodic token check and refresh (nur Client)
    const startTokenRefresh = () => {
        if (!import.meta.client) return;

        if (refreshInterval.value) {
            clearInterval(refreshInterval.value);
        }

        refreshInterval.value = setInterval(async () => {
            const isValid = await checkToken();
            if (isValid) {
                await refreshTokenIfNeeded();
            }
        }, CHECK_INTERVAL_MS);
    };

    // Stop periodic token check
    const stopTokenRefresh = () => {
        if (refreshInterval.value) {
            clearInterval(refreshInterval.value);
            refreshInterval.value = null;
        }
    };

    // Initialize token manager
    const initialize = async () => {
        updateCookieToken();
        await checkToken();
        startTokenRefresh();
    };

    // Clean up
    const cleanup = () => {
        stopTokenRefresh();
    };

    return {
        isAuthenticated,
        isCheckingToken,
        tokenExpiresAt,
        authDisabled,
        checkToken,
        refreshTokenIfNeeded,
        clearToken,
        startTokenRefresh,
        stopTokenRefresh,
        initialize,
        cleanup,
    };
}
