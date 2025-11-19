<script setup lang='ts'>
import {useQuartzApi} from "~/composables/api/quartzApi";
import type {Overview} from "~/composables/generated/models/Overview";

const { d, t, n, locale, locales, setLocale } = useI18n()
const { systemApi } = useQuartzApi()

const overview = ref<Overview | null>(null)
const loading = ref(true)
const error = ref<string | null>(null)

const fetchOverview = async () => {
  try {
    loading.value = true
    error.value = null
    overview.value = await systemApi.systemOverview()
  } catch (e: any) {
    error.value = e.message || 'Failed to fetch system overview'
  } finally {
    loading.value = false
  }
}

// Initial fetch
onMounted(() => {
  fetchOverview()
})

// Auto-refresh every 30 seconds
const { pause, resume } = useIntervalFn(() => {
  fetchOverview()
}, 30000)

onUnmounted(() => {
  pause()
})

const formatBytes = (bytes: number) => {
  const gb = bytes / (1024 * 1024 * 1024)
  return `${gb.toFixed(2)} GB`
}

const formatPercentage = (value: number) => {
  return `${(value * 100).toFixed(1)}%`
}

const getMemoryUsedPercentage = (total: number, free: number) => {
  return ((total - free) / total) * 100
}

const getStatusColor = (status: string) => {
  switch (status) {
    case 'STARTED':
      return 'success'
    case 'SHUTDOWN':
      return 'danger'
    case 'STANDBY':
      return 'warn'
    default:
      return 'info'
  }
}
</script>

<template>
  <div class="p-4">
    <div v-if="loading && !overview" class="flex justify-center items-center py-8">
      <ProgressSpinner />
    </div>

    <div v-else-if="error" class="card surface-0 p-4">
      <Message severity="error" :closable="false">{{ error }}</Message>
    </div>

    <div v-else-if="overview" class="space-y-4">
      <!-- Header -->
      <div class="flex justify-between items-center mb-4">
        <h1 class="text-3xl font-bold">System Dashboard</h1>
        <Button
          icon="pi pi-refresh"
          :loading="loading"
          @click="fetchOverview"
          label="Refresh"
          severity="secondary"
        />
      </div>

      <!-- Scheduler Status -->
      <Card>
        <template #title>
          <div class="flex items-center gap-2">
            <i class="pi pi-clock"></i>
            Scheduler Status
          </div>
        </template>
        <template #content>
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            <div class="flex flex-col gap-2">
              <span class="text-sm text-gray-500">Status</span>
              <Tag :severity="getStatusColor(overview.scheduler.status)" :value="overview.scheduler.status" />
            </div>
            <div class="flex flex-col gap-2">
              <span class="text-sm text-gray-500">Currently Executing Jobs</span>
              <span class="text-2xl font-semibold">{{ overview.scheduler.currentlyExecutingJobs }}</span>
            </div>
            <div class="flex flex-col gap-2">
              <span class="text-sm text-gray-500">Thread Pool Size</span>
              <span class="text-2xl font-semibold">{{ overview.scheduler.threadPoolSize }}</span>
            </div>
            <div class="flex flex-col gap-2">
              <span class="text-sm text-gray-500">Version</span>
              <span class="text-lg font-semibold">{{ overview.scheduler.version }}</span>
            </div>
          </div>
          <Divider />
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="flex flex-col gap-2">
              <span class="text-sm text-gray-500">Scheduler ID</span>
              <span class="text-sm font-mono">{{ overview.scheduler.id }}</span>
            </div>
            <div class="flex flex-col gap-2">
              <span class="text-sm text-gray-500">Name</span>
              <span class="text-sm">{{ overview.scheduler.name }}</span>
            </div>
            <div class="flex flex-col gap-2">
              <span class="text-sm text-gray-500">Scheduler Class</span>
              <span class="text-sm font-mono">{{ overview.scheduler.schedulerClass }}</span>
            </div>
            <div class="flex flex-col gap-2">
              <span class="text-sm text-gray-500">Job Store Class</span>
              <span class="text-sm font-mono">{{ overview.scheduler.jobStoreClass }}</span>
            </div>
          </div>
        </template>
      </Card>

      <!-- System Information -->
      <Card>
        <template #title>
          <div class="flex items-center gap-2">
            <i class="pi pi-server"></i>
            System Information
          </div>
        </template>
        <template #content>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <!-- Memory Usage -->
            <div class="flex flex-col gap-4">
              <div class="flex items-center gap-2">
                <i class="pi pi-database"></i>
                <span class="font-semibold">Memory Usage</span>
              </div>
              <div class="space-y-3">
                <div>
                  <div class="flex justify-between text-sm mb-1">
                    <span>Used Memory</span>
                    <span>{{ formatBytes(overview.system.totalMemory - overview.system.freeMemory) }} / {{ formatBytes(overview.system.totalMemory) }}</span>
                  </div>
                  <ProgressBar
                    :value="getMemoryUsedPercentage(overview.system.totalMemory, overview.system.freeMemory)"
                    :showValue="false"
                  />
                </div>
                <div class="grid grid-cols-3 gap-2 text-sm">
                  <div>
                    <div class="text-gray-500">Total</div>
                    <div class="font-semibold">{{ formatBytes(overview.system.totalMemory) }}</div>
                  </div>
                  <div>
                    <div class="text-gray-500">Max</div>
                    <div class="font-semibold">{{ formatBytes(overview.system.maxMemory) }}</div>
                  </div>
                  <div>
                    <div class="text-gray-500">Free</div>
                    <div class="font-semibold">{{ formatBytes(overview.system.freeMemory) }}</div>
                  </div>
                </div>
              </div>
            </div>

            <!-- CPU Usage -->
            <div class="flex flex-col gap-4">
              <div class="flex items-center gap-2">
                <i class="pi pi-chart-line"></i>
                <span class="font-semibold">CPU Usage</span>
              </div>
              <div class="space-y-3">
                <div>
                  <div class="flex justify-between text-sm mb-1">
                    <span>System CPU Load</span>
                    <span>{{ formatPercentage(overview.system.systemCpuLoad) }}</span>
                  </div>
                  <ProgressBar
                    :value="overview.system.systemCpuLoad * 100"
                    :showValue="false"
                  />
                </div>
                <div>
                  <div class="flex justify-between text-sm mb-1">
                    <span>Process CPU Load</span>
                    <span>{{ formatPercentage(overview.system.processCpuLoad) }}</span>
                  </div>
                  <ProgressBar
                    :value="overview.system.processCpuLoad * 100"
                    :showValue="false"
                  />
                </div>
              </div>
            </div>
          </div>

          <Divider />

          <div class="flex items-center gap-2">
            <i class="pi pi-desktop"></i>
            <span class="text-sm text-gray-500">Hostname:</span>
            <span class="font-semibold">{{ overview.system.hostname }}</span>
          </div>
        </template>
      </Card>

      <!-- Auto-refresh indicator -->
      <div class="text-center text-sm text-gray-500">
        <i class="pi pi-sync"></i>
        Auto-refreshing every 30 seconds
      </div>
    </div>
  </div>
</template>

<style scoped></style>
