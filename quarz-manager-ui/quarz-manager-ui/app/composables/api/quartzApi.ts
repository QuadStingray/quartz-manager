import {useCookie} from '#app'
import {useSessionStorage} from '@vueuse/core'
import {Configuration} from '~/composables/generated'
import {AuthApi} from '~/composables/generated/apis/AuthApi'
import {HistoryApi} from '~/composables/generated/apis/HistoryApi'
import {JobsApi} from '~/composables/generated/apis/JobsApi'
import {SchedulerApi} from '~/composables/generated/apis/SchedulerApi'
import {SystemApi} from '~/composables/generated/apis/SystemApi'
import {useQuartzUrl} from "~/composables/api/quartzUrl";

export function useQuartzApi() {
    const url = useQuartzUrl()
    const cookieKey = 'authToken'

    const token = () => new Promise<string>((resolve) => {
        const cookie = useCookie(cookieKey).value
        const sessionStorage = useSessionStorage(cookieKey, '').value

        const token = ((cookie?.length ?? 0) > 0) ? cookie : sessionStorage

        return resolve(`${token}`)
    })

    let configuration = new Configuration({basePath: url, accessToken: token})

    const loginApi = (username: string, password: string) => new AuthApi(new Configuration({basePath: url, username: username, password: password}))
    const authApi = new AuthApi(configuration)
    const historyApi = new HistoryApi(configuration)
    const jobsApi = new JobsApi(configuration)
    const schedulerApi = new SchedulerApi(configuration)
    const systemApi = new SystemApi(configuration)

    return {
        loginApi,
        authApi,
        historyApi,
        jobsApi,
        schedulerApi,
        systemApi
    }
}
