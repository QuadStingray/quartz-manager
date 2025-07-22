<script setup lang='ts'>
import { useQuartzApi } from "~/composables/api/quartzApi";
import { ref } from 'vue';
import { useCookie } from '#app';
import { useTokenManager } from '~/composables/auth/useTokenManager';

definePageMeta({
  layout: 'auth'
});

const { d, t, n, locale, locales, setLocale } = useI18n();
const router = useRouter();
const { authApi } = useQuartzApi();
const tokenManager = useTokenManager();

// Form data
const username = ref('');
const password = ref('');
const loading = ref(false);
const errorMessage = ref('');
const showPassword = ref(false);

// Form validation
const formValid = computed(() => {
  return username.value.length > 0 && password.value.length > 0;
});

// Handle login
const handleLogin = async () => {
  if (!formValid.value) return;
  
  loading.value = true;
  errorMessage.value = '';
  
  try {
    const response = await authApi.login(username.value, password.value);
    
    // Store token in cookie with expiration date from response
    const authTokenCookie = useCookie('authToken', {
      expires: new Date(response.expiresAt),
      path: '/',
      secure: process.env.NODE_ENV === 'production',
      sameSite: 'strict'
    });
    
    authTokenCookie.value = response.authToken;
    
    // Update token manager state
    await tokenManager.checkToken(true);
    
    // Redirect to home page
    router.push('/');
  } catch (error) {
    console.error('Login error:', error);
    errorMessage.value = 'Invalid username or password. Please try again.';
  } finally {
    loading.value = false;
  }
};

// Check if already logged in
onMounted(async () => {
  const isAuthenticated = await tokenManager.checkToken();
  if (isAuthenticated) {
    // Already logged in, redirect to home
    router.push('/');
  }
});
</script>

<template>
  <div class="flex justify-center items-center min-h-screen bg-gray-100">
    <div class="card surface-0 p-6 shadow-lg rounded-lg w-full max-w-md">
      <div class="text-3xl font-bold mb-6 text-center text-blue-600">
        Quartz Manager
      </div>
      
      <form @submit.prevent="handleLogin" class="space-y-4">
        <div class="field">
          <label for="username" class="block text-sm font-medium text-gray-700 mb-1">Username</label>
          <div class="p-input-icon-right w-full">
            <i class="pi pi-user" />
            <InputText 
              id="username" 
              v-model="username" 
              type="text" 
              class="w-full" 
              placeholder="Enter your username"
              :class="{ 'p-invalid': errorMessage }"
              autofocus
            />
          </div>
        </div>
        
        <div class="field">
          <label for="password" class="block text-sm font-medium text-gray-700 mb-1">Password</label>
          <div class="p-input-icon-right w-full">
            <i 
              :class="showPassword ? 'pi pi-eye-slash' : 'pi pi-eye'" 
              @click="showPassword = !showPassword" 
              style="cursor: pointer;"
            />
            <InputText 
              id="password" 
              v-model="password" 
              :type="showPassword ? 'text' : 'password'" 
              class="w-full" 
              placeholder="Enter your password"
              :class="{ 'p-invalid': errorMessage }"
              @keyup.enter="handleLogin"
            />
          </div>
        </div>
        
        <small v-if="errorMessage" class="p-error block mt-2">{{ errorMessage }}</small>
        
        <Button 
          type="submit" 
          label="Login" 
          class="w-full" 
          :loading="loading"
          :disabled="!formValid || loading"
        />
      </form>
    </div>
  </div>
</template>

<style scoped>
.p-input-icon-right > i {
  margin-top: -0.5rem;
}
</style>
