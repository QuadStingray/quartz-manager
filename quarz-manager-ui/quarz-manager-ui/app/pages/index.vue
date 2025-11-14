<script setup lang='ts'>
import {useQuartzApi} from "~/composables/api/quartzApi";
const { d, t, n, locale, locales, setLocale } = useI18n()

// Capture router during setup to avoid calling composable in async context
const router = useRouter()

const { authApi } = useQuartzApi()

authApi.checkToken().catch((e) => {
  if (e.response?.status === 401) {
    router.push('/login')
  } else {
    throw e;
  }
})


</script>

<template>
  <div class="card surface-0 text-center">
    <div class="text-4xl font-bold mb-3">
      <span class="text-green-600">PrimeVue </span>
      <span class="text-blue-400"> & TypeScript</span>
      <span class="text-green-400"> & Nuxt 3</span>
    </div>
    <h2 class="text-2xl pb-6">
      Starter for Vue.js Development with Nuxt 3.
    </h2>
    <div class="gap-2 grid grid-cols-2 lg:grid-cols-3">

    </div>
  </div>
</template>

<style scoped></style>
