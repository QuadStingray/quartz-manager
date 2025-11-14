<script setup lang="ts">
import { useRouter } from 'vue-router';

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
        Quarz Manager UI
      </template>

      <template #end>
        <AppColorMode class="ml-6 mr-2" />
        <Button 
          icon="pi pi-sign-out" 
          severity="danger" 
          @click="logout" 
          tooltip="Logout" 
          tooltipPosition="bottom"
        />
      </template>
    </Toolbar>
  </nav>
</template>

<style scoped lang="scss">

</style>
