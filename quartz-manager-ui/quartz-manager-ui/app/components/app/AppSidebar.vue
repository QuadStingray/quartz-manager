<script setup lang='ts'>
const config = useRuntimeConfig()
const { menu } = useNavigationMenu()

const collapsed = useState<boolean>('collapsed')
const isOnMobile = useState<boolean>('isOnMobile')

const { d, t, n, locale, locales, setLocale } = useI18n()

const availableLocales = computed(() => {
  return locales.value?.filter(l => l.code !== locale.value)
})

function onResize() {
  if (window.innerWidth <= 980) {
    collapsed.value = true
    isOnMobile.value = true
  }
  else {
    collapsed.value = false
    isOnMobile.value = false
  }
}

function onToggleCollapse() {
}

function onItemClick() {
}

onMounted(() => {
  onResize()
  window.addEventListener('resize', onResize)
})
</script>

<template>
  <div>
    <sidebar-menu
      v-model:collapsed="collapsed"
      link-component-name="nuxt-sidebar-link"
      :menu="menu"
      :show-one-child="true"
      width="200px"
      width-collapsed="60px"
      @update:collapsed="onToggleCollapse"
      @item-click="onItemClick"
    >
      <template #header>
        <div v-if="!collapsed" class="flex px-3 py-4">
          <img class="w-full" src="/quartz-manager-logo-with-text.svg" alt="Quartz Manager">
        </div>
        <div v-else class="flex justify-center py-3">
          <img class="w-9 h-9" src="/quartz-manager-logo.svg" alt="Quartz Manager">
        </div>
      </template>
      <template #footer>
        <div class="text-xs text-color-primary m-2 text-center">
          <a
              v-for="currentLocale in availableLocales"
              :key="currentLocale.code"
              href="#"
              @click.prevent.stop="setLocale(currentLocale.code)"
          >{{ currentLocale.name }}</a><br/>
          <span v-if="!collapsed">Quartz Manager UI ({{ config.public.APP_VERSION }})</span>
          <span v-if="collapsed">{{ config.public.APP_VERSION }}</span>
        </div>
      </template>
    </sidebar-menu>
    <div
      v-if="isOnMobile && !collapsed"
      class="sidebar-overlay"
      @click="collapsed = true"
    />
  </div>
</template>

<style lang="scss">
.sidebar-overlay {
  position: fixed;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  background-color: #000;
  opacity: 0.5;
  z-index: 900;
}
</style>
