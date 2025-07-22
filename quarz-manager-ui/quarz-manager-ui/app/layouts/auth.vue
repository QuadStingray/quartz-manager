<script setup lang='ts'>
// No state needed for auth layout
</script>

<template>
  <div class="auth-layout">
    <ConfirmDialog />
    <Toast />
    <div class="auth-content">
      <slot />
    </div>
  </div>
</template>

<style lang="scss">
.auth-layout {
  min-height: 100vh;
  width: 100%;
  
  .auth-content {
    min-height: 100vh;
    width: 100%;
  }
}
</style>