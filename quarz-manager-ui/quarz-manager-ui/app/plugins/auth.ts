import { defineNuxtPlugin } from '#app';
import { useTokenManager } from '~/composables/auth/useTokenManager';

export default defineNuxtPlugin(nuxtApp => {
  // Initialize token manager
  const tokenManager = useTokenManager();
  
  // Make token manager available globally
  nuxtApp.provide('tokenManager', tokenManager);
  
  // Add navigation guard to check authentication for protected routes
  nuxtApp.hook('page:start', async () => {
    const route = useRoute();
    const router = useRouter();
    
    // For login page, check if user is already authenticated and redirect to home if they are
    if (route.path === '/login') {
      const isAuthenticated = await tokenManager.checkToken();
      if (isAuthenticated) {
        // User is already logged in, redirect to home page
        router.push('/');
      }
      return;
    }
    
    // For other pages, check if token is valid
    const isValid = await tokenManager.checkToken();
    
    // If not valid, the checkToken method will handle redirection to login
    if (isValid) {
      // If token is valid but will expire soon, refresh it
      await tokenManager.refreshTokenIfNeeded();
    }
  });
  
  console.log('Auth plugin initialized');
});