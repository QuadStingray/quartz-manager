import {useCookie} from '#app'
import {useSessionStorage} from '@vueuse/core'
import {Configuration} from '~/api'
import {AuthApi} from '~/api/apis/AuthApi'
import {HistoryApi} from '~/api/apis/HistoryApi'
import {JobsApi} from '~/api/apis/JobsApi'
import {SchedulerApi} from '~/api/apis/SchedulerApi'
import {useQuartzUrl} from "~/composables/api/quartzUrl";

export function useQuartzApi() {
    const url = useQuartzUrl()

    const token = () => new Promise<string>((resolve) => {
        const cookie = useCookie(cookieKey).value
        const sessionStorage = useSessionStorage(cookieKey, '').value

        const token = ((cookie?.length ?? 0) > 0) ? cookie : sessionStorage

        return resolve(`${token}`)
    })

    let configuration =  new Configuration({basePath: url, accessToken: token})
    const cookieKey = 'authToken'

    const authApi = new AuthApi(configuration)
    const historyApi = new HistoryApi(configuration)
    const jobsApi = new JobsApi(configuration)
    const schedulerApi = new SchedulerApi(configuration)

    return {
        authApi,
        historyApi,
        jobsApi,
        schedulerApi
    }
}
