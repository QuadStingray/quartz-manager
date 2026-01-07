<script setup lang="ts">
import { useRouter } from 'vue-router';
import { computed } from 'vue';

const { t } = useI18n();
const { $tokenManager } = useNuxtApp();
const router = useRouter();

const isAuthEnabled = computed(() => !$tokenManager.authDisabled.value);

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
          v-if="isAuthEnabled"
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
