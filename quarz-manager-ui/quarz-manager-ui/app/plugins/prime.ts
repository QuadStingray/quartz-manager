import PrimeVue from 'primevue/config'
import ToastService from 'primevue/toastservice'
import ConfirmationService from 'primevue/confirmationservice';

export default defineNuxtPlugin((nuxtApp) => {
    const app: any = nuxtApp.vueApp as any
    const installed: Set<any> | undefined = app?._context?.plugins

    const safeUse = (plugin: any, ...args: any[]) => {
        try {
            if (!installed || !installed.has(plugin)) {
                app.use(plugin, ...args)
            }
        } catch {
            // fall back in case structure changes; try once
            try { app.use(plugin, ...args) } catch {}
        }
    }

    // Install PrimeVue and related services only if not already installed
    safeUse(PrimeVue)
    safeUse(ToastService)
    safeUse(ConfirmationService)
})
