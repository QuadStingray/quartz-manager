import { useI18n } from 'vue-i18n'

export function useNavigationMenu() {
  const { d, t, n, locale, locales, setLocale } = useI18n()

  const separator = h('hr')

  const menu = computed(() => {
    return [
      {
        href: '/',
        title: t('home'),
        icon: 'pi pi-fw pi-home',
      },
      {
        component: markRaw(separator),
      },
      {
        title: t('jobs'),
        icon: 'pi pi-server',
        href: '/jobs',
      },
      {
        title: t('history'),
        icon: 'pi pi-history',
        href: '/history/',
      },
    ]
  })

  return { menu }
}
