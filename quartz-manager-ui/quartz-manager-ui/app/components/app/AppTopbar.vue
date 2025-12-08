<script setup lang="ts">
import { useRouter } from 'vue-router';

const { t } = useI18n();
const { $tokenManager } = useNuxtApp();
const router = useRouter();

const logout = () => {
  $tokenManager.clearToken();
  router.push('/login');
};
</script>

<template>
  <nav>
    <Toolbar>
      <template #start >
        {{ t('topbar.title') }}
      </template>

      <template #end>
        <AppColorMode class="ml-6 mr-2" />
        <Button
          icon="pi pi-sign-out"
          severity="danger"
          @click="logout"
          :tooltip="t('topbar.logout')"
          tooltipPosition="bottom"
        />
      </template>
    </Toolbar>
  </nav>
</template>

<style scoped lang="scss">

</style>
