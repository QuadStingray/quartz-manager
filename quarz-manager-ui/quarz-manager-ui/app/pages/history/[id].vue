<script lang="ts" setup>
import {type LogRecord, type LogMessage} from "~/composables/generated/models";
import {useQuartzApi} from "~/composables/api/quartzApi";
import {ref, watch, computed} from 'vue';
import {useAsyncData} from '#imports';
const {d, t, n, locale, locales, setLocale} = useI18n();
const {historyApi} = useQuartzApi();
const route = useRoute();
const router = useRouter();

// Get ID from route
const historyId = computed(() => route.params.id as string);

// Loading state
const loading = ref(false);
const error = ref(null);

// Fetch history detail using useAsyncData
const {data: historyDetail, pending, refresh: refreshHistory, error: asyncError} = useAsyncData(
  `history-${historyId.value}`,
  async () => {
    const response = await historyApi.historyById({id: historyId.value});
    return response;
  }
);

// Update loading state when pending changes
watch(pending, (isPending) => {
  loading.value = isPending;
});

// Update error state when asyncError changes
watch(asyncError, (newError) => {
  if (newError) {
    console.error('Error fetching history detail:', newError);
    error.value = newError.message || t('historyPage.retry');
  } else {
    error.value = null;
  }
});

// Format date for display
const formatDateTime = (date) => {
  if (!date) return '-';
  return new Date(date).toLocaleString(locale.value);
};

// Get log level class based on level
const getLogLevelClass = (level: string) => {
  const levelUpper = level.toUpperCase();
  switch (levelUpper) {
    case 'ERROR':
      return 'log-error';
    case 'WARN':
    case 'WARNING':
      return 'log-warn';
    case 'INFO':
      return 'log-info';
    default:
      return 'log-default';
  }
};

// Go back to history list
const goBack = () => {
  router.push('/history/');
};

</script>

<template>
  <div class="card surface-0">
    <!-- Header -->
    <div class="flex justify-between items-center mb-4">
      <div class="flex items-center gap-3">
        <Button
            icon="pi pi-arrow-left"
            @click="goBack"
            text
            :tooltip="t('historyPage.backToHistory')"
            tooltipPosition="bottom"
        />
        <h1>{{ t('history') }} - {{ historyId }}</h1>
      </div>
      <Button
        icon="pi pi-refresh"
        :loading="loading"
        @click="refreshHistory"
        :label="t('refresh')"
        severity="secondary"
      />
    </div>

    <!-- Loading State -->
    <div v-if="loading && !historyDetail" class="text-center p-8">
      <ProgressSpinner/>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="text-center p-8">
      <i class="pi pi-exclamation-triangle text-yellow-500 text-4xl mb-4"></i>
      <h2>{{ error }}</h2>
      <Button :label="t('retry')" @click="refreshHistory" class="mt-4"/>
    </div>

    <!-- History Detail -->
    <div v-else-if="historyDetail" class="terminal-container">
      <!-- Terminal Header -->
      <div class="terminal-header">
        <div class="terminal-title">
          <i class="pi pi-terminal mr-2"></i>
          <span>{{ historyDetail.className }}</span>
        </div>
        <div class="terminal-meta">
          <span>{{ formatDateTime(historyDetail.date) }}</span>
        </div>
      </div>

      <!-- Terminal Body -->
      <div class="terminal-body">
        <div v-if="!historyDetail.logMessages || historyDetail.logMessages.length === 0" class="terminal-empty">
          <i class="pi pi-info-circle mr-2"></i>
          {{ t('historyPage.noLogMessages') }}
        </div>
        <div v-else class="log-messages">
          <div
              v-for="(message, index) in historyDetail.logMessages"
              :key="index"
              class="log-line"
              :class="getLogLevelClass(message.level)"
          >
            <span class="log-timestamp">{{ formatDateTime(message.date) }}</span>
            <span class="log-level">[{{ message.level.toUpperCase() }}]</span>
            <span class="log-message">{{ message.logMessage }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.terminal-container {
  background-color: #1e1e1e;
  border-radius: 8px;
  overflow: hidden;
  font-family: 'Courier New', monospace;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.3);
}

.terminal-header {
  background-color: #2d2d2d;
  padding: 12px 16px;
  border-bottom: 1px solid #3e3e3e;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.terminal-title {
  color: #d4d4d4;
  font-weight: bold;
  display: flex;
  align-items: center;
  font-size: 14px;
}

.terminal-meta {
  color: #808080;
  font-size: 12px;
}

.terminal-body {
  background-color: #1e1e1e;
  padding: 16px;
  min-height: 400px;
  max-height: 70vh;
  overflow-y: auto;
  color: #d4d4d4;
}

.terminal-empty {
  color: #808080;
  text-align: center;
  padding: 40px;
  font-size: 14px;
}

.log-messages {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.log-line {
  display: flex;
  gap: 8px;
  padding: 4px 8px;
  border-radius: 2px;
  transition: background-color 0.15s;
  font-size: 13px;
  line-height: 1.6;

  &:hover {
    background-color: rgba(255, 255, 255, 0.05);
  }
}

.log-timestamp {
  color: #808080;
  flex-shrink: 0;
  font-size: 12px;
}

.log-level {
  font-weight: bold;
  flex-shrink: 0;
  min-width: 70px;
}

.log-message {
  flex: 1;
  word-break: break-word;
}

// Log Level Colors
.log-error {
  .log-level {
    color: #f44336;
  }
  .log-message {
    color: #ffcdd2;
  }
}

.log-warn {
  .log-level {
    color: #ffc107;
  }
  .log-message {
    color: #fff9c4;
  }
}

.log-info {
  .log-level {
    color: #2196f3;
  }
  .log-message {
    color: #bbdefb;
  }
}

.log-default {
  .log-level {
    color: #9e9e9e;
  }
  .log-message {
    color: #d4d4d4;
  }
}

// Scrollbar styling for terminal
.terminal-body::-webkit-scrollbar {
  width: 10px;
}

.terminal-body::-webkit-scrollbar-track {
  background: #2d2d2d;
}

.terminal-body::-webkit-scrollbar-thumb {
  background: #555;
  border-radius: 5px;

  &:hover {
    background: #666;
  }
}
</style>