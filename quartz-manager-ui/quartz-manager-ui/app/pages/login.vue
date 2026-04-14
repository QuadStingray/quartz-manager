<script setup lang='ts'>
import { useQuartzApi } from "~/composables/api/quartzApi";
import { ref } from 'vue';
import { useCookie } from '#app';
const { $tokenManager } = useNuxtApp();

definePageMeta({
  layout: 'auth'
});

const { d, t, n, locale, locales, setLocale } = useI18n();
const router = useRouter();
const { loginApi } = useQuartzApi();

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
    const response = await loginApi(username.value, password.value).login();

    // Store token in cookie with expiration date from response
    const authTokenCookie = useCookie('authToken', {
      expires: new Date(response.expiresAt),
      path: '/',
      secure: process.env.NODE_ENV === 'production',
      sameSite: 'strict'
    });

    authTokenCookie.value = response.authToken;

    // Update token manager state
    await $tokenManager.checkToken(true);

    // Redirect to home page
    router.push('/');
  } catch (error) {
    errorMessage.value = t('login.error');
  } finally {
    loading.value = false;
  }
};

// Check if already logged in
onMounted(async () => {
  const isAuthenticated = await $tokenManager.checkToken();
  if (isAuthenticated) {
    // Already logged in, redirect to home
    router.push('/');
  }
});
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <div class="logo-wrapper">
        <img src="/quartz-manager-logo-with-text.svg" alt="Quartz Manager" class="login-logo" />
      </div>

      <form @submit.prevent="handleLogin" class="login-form">
        <div class="field">
          <label for="username" class="field-label">{{ t('login.username') }}</label>
          <div class="input-wrapper">
            <i class="pi pi-user input-icon" />
            <InputText
              id="username"
              v-model="username"
              type="text"
              class="login-input"
              :placeholder="t('login.usernamePlaceholder')"
              :class="{ 'p-invalid': errorMessage }"
              autofocus
            />
          </div>
        </div>

        <div class="field">
          <label for="password" class="field-label">{{ t('login.password') }}</label>
          <div class="input-wrapper">
            <i
              :class="showPassword ? 'pi pi-eye-slash' : 'pi pi-eye'"
              class="input-icon password-toggle"
              @click="showPassword = !showPassword"
            />
            <InputText
              id="password"
              v-model="password"
              :type="showPassword ? 'text' : 'password'"
              class="login-input"
              :placeholder="t('login.passwordPlaceholder')"
              :class="{ 'p-invalid': errorMessage }"
              @keyup.enter="handleLogin"
            />
          </div>
        </div>

        <small v-if="errorMessage" class="error-message">{{ errorMessage }}</small>

        <Button
          type="submit"
          :label="t('login.button')"
          class="login-button"
          :loading="loading"
          :disabled="!formValid || loading"
        />
      </form>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #1a1d27;
  background-image: radial-gradient(ellipse at 60% 20%, rgba(16, 185, 129, 0.06) 0%, transparent 60%),
                    radial-gradient(ellipse at 20% 80%, rgba(94, 200, 90, 0.04) 0%, transparent 50%);
}

.login-card {
  background-color: #22263a;
  border: 1px solid #2e3347;
  border-radius: 12px;
  padding: 2.5rem 2rem;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
}

.logo-wrapper {
  margin-bottom: 2rem;
  padding: 0 0.5rem;
}

.login-logo {
  width: 100%;
  max-width: 280px;
  display: block;
  margin: 0 auto;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 0.375rem;
}

.field-label {
  font-size: 0.8rem;
  font-weight: 500;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.input-wrapper {
  position: relative;
}

.input-icon {
  position: absolute;
  right: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  color: #64748b;
  font-size: 0.875rem;
  z-index: 1;
  pointer-events: none;
}

.password-toggle {
  pointer-events: auto;
  cursor: pointer;
  transition: color 0.2s;
}

.password-toggle:hover {
  color: #10b981;
}

.login-input {
  width: 100%;
  background-color: #1a1d27 !important;
  border-color: #2e3347 !important;
  color: #e2e8f0 !important;
  padding-right: 2.5rem !important;
  border-radius: 6px !important;
  transition: border-color 0.2s !important;
}

.login-input:focus {
  border-color: #10b981 !important;
  box-shadow: 0 0 0 2px rgba(16, 185, 129, 0.15) !important;
}

.login-input::placeholder {
  color: #475569 !important;
}

.error-message {
  color: #f87171;
  font-size: 0.8rem;
  margin-top: -0.5rem;
}

.login-button {
  width: 100%;
  background-color: #10b981 !important;
  border-color: #10b981 !important;
  color: #fff !important;
  font-weight: 600 !important;
  border-radius: 6px !important;
  padding: 0.65rem !important;
  margin-top: 0.25rem;
  transition: background-color 0.2s, box-shadow 0.2s !important;
}

.login-button:not(:disabled):hover {
  background-color: #059669 !important;
  border-color: #059669 !important;
  box-shadow: 0 4px 20px rgba(16, 185, 129, 0.3) !important;
}

.login-button:disabled {
  opacity: 0.5 !important;
  cursor: not-allowed !important;
}
</style>
