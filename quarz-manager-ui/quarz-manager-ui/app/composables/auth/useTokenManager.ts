import { ref, onMounted, onUnmounted } from 'vue';
import { useCookie } from '#app';
import { useQuartzApi } from '../api/quartzApi';

export function useTokenManager() {
  const { authApi } = useQuartzApi();
  const router = useRouter();
  
  // State
  const isAuthenticated = ref(false);
  const isCheckingToken = ref(false);
  const tokenExpiresAt = ref<Date | null>(null);
  const refreshInterval = ref<NodeJS.Timeout | null>(null);
  
  // Constants
  const TOKEN_COOKIE_NAME = 'authToken';
  const REFRESH_BEFORE_EXPIRY_MS = 5 * 60 * 1000; // Refresh 5 minutes before expiry
  const CHECK_INTERVAL_MS = 60 * 1000; // Check token every minute
  
  // Get token expiration from cookie
  const getTokenExpiration = (): Date | null => {
    const authTokenCookie = useCookie(TOKEN_COOKIE_NAME);
    if (!authTokenCookie.value) return null;
    
    try {
      // Try to get expiration from token check
      return tokenExpiresAt.value;
    } catch (error) {
      console.error('Error parsing token expiration:', error);
      return null;
    }
  };
  
  // Check if token is valid and update state
  const checkToken = async (force = false): Promise<boolean> => {
    if (isCheckingToken.value && !force) return isAuthenticated.value;
    
    const authTokenCookie = useCookie(TOKEN_COOKIE_NAME);
    if (!authTokenCookie.value) {
      isAuthenticated.value = false;
      return false;
    }
    
    isCheckingToken.value = true;
    
    try {
      const response = await authApi.checkToken();
      tokenExpiresAt.value = new Date(response.expiresAt);
      isAuthenticated.value = true;
      return true;
    } catch (error) {
      console.error('Token check failed:', error);
      isAuthenticated.value = false;
      
      // If token is invalid, redirect to login
      if (error.response?.status === 401) {
        clearToken();
        router.push('/login');
      }
      
      return false;
    } finally {
      isCheckingToken.value = false;
    }
  };
  
  // Refresh token if needed
  const refreshTokenIfNeeded = async (): Promise<boolean> => {
    const expiration = getTokenExpiration();
    if (!expiration) return false;
    
    const now = new Date();
    const timeUntilExpiry = expiration.getTime() - now.getTime();
    
    // If token will expire soon, refresh it
    if (timeUntilExpiry < REFRESH_BEFORE_EXPIRY_MS) {
      try {
        const response = await authApi.extendToken();
        
        // Update cookie with new token
        const authTokenCookie = useCookie(TOKEN_COOKIE_NAME, {
          expires: new Date(response.expiresAt),
          path: '/',
          secure: process.env.NODE_ENV === 'production',
          sameSite: 'strict'
        });
        
        authTokenCookie.value = response.authToken;
        tokenExpiresAt.value = new Date(response.expiresAt);
        isAuthenticated.value = true;
        
        console.log('Token refreshed successfully');
        return true;
      } catch (error) {
        console.error('Token refresh failed:', error);
        
        // If refresh fails due to invalid token, redirect to login
        if (error.response?.status === 401) {
          clearToken();
          router.push('/login');
        }
        
        return false;
      }
    }
    
    return true; // Token is still valid
  };
  
  // Clear token
  const clearToken = () => {
    const authTokenCookie = useCookie(TOKEN_COOKIE_NAME);
    authTokenCookie.value = null;
    isAuthenticated.value = false;
    tokenExpiresAt.value = null;
  };
  
  // Start periodic token check and refresh
  const startTokenRefresh = () => {
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
  onMounted(async () => {
    await checkToken();
    startTokenRefresh();
  });
  
  // Clean up on unmount
  onUnmounted(() => {
    stopTokenRefresh();
  });
  
  return {
    isAuthenticated,
    isCheckingToken,
    tokenExpiresAt,
    checkToken,
    refreshTokenIfNeeded,
    clearToken,
    startTokenRefresh,
    stopTokenRefresh
  };
}