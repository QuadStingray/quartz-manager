<script lang="ts" setup>
import {type LogRecord} from "~/composables/generated/models";
import DataTable from 'primevue/datatable';
import {useQuartzApi} from "~/composables/api/quartzApi";
import {ref, watch} from 'vue';
import {useToast, useAsyncData} from '#imports';
const {d, t, n, locale, locales, setLocale} = useI18n();
const {historyApi} = useQuartzApi();
const router = useRouter();

// Loading state
const loading = ref(false);
const error = ref(null);
const toast = useToast();

// Fetch history data using useAsyncData
const {data: historyData, pending, refresh: refreshHistory, error: asyncError} = useAsyncData('history', async () => {
  const response = await historyApi.historyList();
  return response;
});

// Update loading state when pending changes
watch(pending, (isPending) => {
  loading.value = isPending;
});

// Update error state when asyncError changes
watch(asyncError, (newError) => {
  if (newError) {
    console.error('Error fetching history:', newError);
    error.value = newError.message || t('historyPage.retry');
  } else {
    error.value = null;
  }
});

// Format date for display
const formatDate = (date) => {
  if (!date) return '-';
  return d(new Date(date), 'long');
};

// Navigate to history detail page
const viewHistoryDetail = (record: LogRecord) => {
  router.push(`/history/${record.id}`);
};

</script>

<template>
  <div class="card surface-0">
    <div class="flex justify-between items-center mb-4">
      <h1>{{ t('history') }}</h1>
      <Button
        icon="pi pi-refresh"
        :loading="loading"
        @click="refreshHistory"
        :label="t('refresh')"
        severity="secondary"
      />
    </div>

    <DataTable
        :value="historyData"
        :loading="loading"
        ref="dataTableRef"
        paginator
        :rows="10"
        :rowsPerPageOptions="[5, 10, 20, 50]"
        filterDisplay="menu"
        responsiveLayout="scroll"
        stripedRows
        @row-click="viewHistoryDetail($event.data)"
        class="cursor-pointer"
    >
      <template #empty>
        <div v-if="error" class="text-center p-4">
          <i class="pi pi-exclamation-triangle text-yellow-500 text-xl mb-2"></i>
          <p>{{ error }}</p>
          <Button :label="t('retry')" @click="refreshHistory" class="mt-2"/>
        </div>
        <div v-else class="text-center p-4">
          <i class="pi pi-info-circle text-blue-500 text-xl mb-2"></i>
          <p>{{ t('historyPage.noRecordsFound') }}</p>
        </div>
      </template>

      <template #header>
        <div class="flex justify-between">
          <span class="p-input-icon-left">
            <i class="pi pi-search"/>
            <InputText :placeholder="t('search')"/>
          </span>
        </div>
      </template>

      <Column field="id" :header="t('historyPage.columns.id')" sortable>
      </Column>

      <Column field="className" :header="t('historyPage.columns.className')" sortable>
      </Column>

      <Column field="date" :header="t('date')" sortable>
        <template #body="{ data }">
          {{ formatDate(data.date) }}
        </template>
      </Column>

      <Column :header="t('historyPage.columns.messages')" sortable>
        <template #body="{ data }">
          {{ data.logMessages?.length || 0 }}
        </template>
      </Column>
    </DataTable>
  </div>
</template>

<style scoped lang="scss">
.p-datatable {
  .p-datatable-header {
    padding: 1rem;
    background: var(--surface-0);
  }

  .p-column-filter {
    width: 100%;
  }
}

:deep(.p-datatable-tbody > tr) {
  cursor: pointer;
}
</style>