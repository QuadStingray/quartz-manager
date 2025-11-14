import { defineNuxtPlugin } from '#app';
import { useTokenManager } from '~/composables/auth/useTokenManager';

const AUTH_PLUGIN_FLAG = '__auth_plugin_installed__';
const TOKEN_MANAGER_KEY = '__token_manager_singleton__';

export default defineNuxtPlugin((nuxtApp) => {
    const appAny: any = nuxtApp.vueApp as any;

    // If plugin already installed (e.g., due to HMR), avoid duplicate hook registration
    if ((appAny as any)[AUTH_PLUGIN_FLAG]) {
        const existing = (globalThis as any)[TOKEN_MANAGER_KEY];
        if (existing) {
            nuxtApp.provide('tokenManager', existing);
        }
        return;
    }
    (appAny as any)[AUTH_PLUGIN_FLAG] = true;

    // Create or reuse a singleton token manager instance
    const tokenManager = (globalThis as any)[TOKEN_MANAGER_KEY] || useTokenManager();
    (globalThis as any)[TOKEN_MANAGER_KEY] = tokenManager;

    // Make token manager available globally
    nuxtApp.provide('tokenManager', tokenManager);
    // Register hooks once
    nuxtApp.hook('app:mounted', async () => {
        try {
            await tokenManager.initialize();
            console.log('Token manager initialized successfully');
        } catch (error) {
            console.error('Failed to initialize token manager:', error);
        }
        // After initialization, check auth and handle redirects
        await checkAndHandleLogin();
    });

    nuxtApp.hook('page:start', checkAndHandleLogin);

    async function checkAndHandleLogin() {
        try {
            const route = useRoute();
            const router = useRouter();

            const isAuthenticated = await tokenManager.checkToken();

            // For login page, check if user is already authenticated and redirect to home if they are
            if (route.path === '/login') {
                if (isAuthenticated) {
                    router.push('/');
                }
                return;
            } else {
                if (!isAuthenticated) {
                    router.push({ path: '/login' }).then(() => {
                        window.location.reload();
                    });
                }
            }

            // For other pages, check if token is valid
            try {
                const isValid = await tokenManager.checkToken();

                // If not valid, the checkToken method will handle redirection to login
                if (isValid) {
                    // If token is valid but will expire soon, refresh it
                    try {
                        await tokenManager.refreshTokenIfNeeded();
                    } catch (refreshError) {
                        console.error('Error refreshing token:', refreshError);
                        // Continue even if refresh fails
                    }
                }
            } catch (error) {
                console.error('Error checking token validity:', error);
                // If token check fails, we'll assume it's invalid and let the user continue
                // They'll be redirected to login if they try to access a protected resource
            }
        } catch (error) {
            console.error('Unexpected error in navigation guard:', error);
            // Continue with navigation even if there's an error
        }
    }

});