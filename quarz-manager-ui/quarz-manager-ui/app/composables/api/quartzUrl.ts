import { useRuntimeConfig } from '#app'

export function useQuartzUrl(): string {
    const config = useRuntimeConfig()
    return config.public.quartz?.url
}

