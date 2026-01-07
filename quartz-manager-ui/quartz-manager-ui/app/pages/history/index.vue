<script lang="ts" setup>
import {type LogRecord} from "~/composables/generated/models";
import DataTable from 'primevue/datatable';
import {useQuartzApi} from "~/composables/api/quartzApi";
import {ref, watch} from 'vue';
import {useToast} from '#imports';
import {FilterMatchMode} from '@primevue/core/api';
const {d, t, n, locale, locales, setLocale} = useI18n();
const {historyApi} = useQuartzApi();
const router = useRouter();

// Loading state
const loading = ref(false);
const error = ref(null);
const toast = useToast();

// Data and pagination state
const historyData = ref<LogRecord[]>([]);
const totalRecords = ref(0);

// Pagination and filter state
const filters = ref({
  id: { value: null, matchMode: FilterMatchMode.CONTAINS },
  jobName: { value: null, matchMode: FilterMatchMode.CONTAINS },
  jobGroup: { value: null, matchMode: FilterMatchMode.CONTAINS }
});

const lazyParams = ref({
  first: 0,
  rows: 10,
  sortField: null,
  sortOrder: null
});

// Fetch history data
const loadHistoryData = async () => {
  loading.value = true;
  error.value = null;

  try {
    const page = Math.floor(lazyParams.value.first / lazyParams.value.rows) + 1;
    const sort = lazyParams.value.sortField
      ? `${lazyParams.value.sortOrder === 1 ? '' : '-'}${lazyParams.value.sortField}`
      : undefined;

    // Build query from filters
    let query = undefined;
    if (filters.value && Object.keys(filters.value).length > 0) {
      const queryParts = [];
      for (const [field, filterValue] of Object.entries(filters.value)) {
        if (filterValue && filterValue.value) {
          queryParts.push(`${field}:${filterValue.value}`);
        }
      }
      if (queryParts.length > 0) {
        query = queryParts.join(' AND ');
      }
    }

    const response = await historyApi.historyListRaw({
      page: page,
      rowsPerPage: lazyParams.value.rows,
      sort: sort,
      query: query
    });

    // Extract pagination info from headers
    const headers = response.raw.headers;
    const countRows = headers.get('x-pagination-count-rows');
    if (countRows) {
      totalRecords.value = parseInt(countRows, 10);
    }

    historyData.value = await response.value();
  } catch (err) {
    error.value = err.message || t('historyPage.retry');
    historyData.value = [];
  } finally {
    loading.value = false;
  }
};

// Initial load
loadHistoryData();

// Watch for changes in lazyParams and filters
watch(lazyParams, () => {
  loadHistoryData();
}, { deep: true });

watch(filters, () => {
  loadHistoryData();
}, { deep: true });

// Format date for display
const formatDate = (date) => {
  if (!date) return '-';
  return d(new Date(date), 'long');
};

// Navigate to history detail page
const viewHistoryDetail = (record: LogRecord) => {
  router.push(`/history/${record.id}`);
};

// Handle table events
const onPage = (event) => {
  lazyParams.value.first = event.first;
  lazyParams.value.rows = event.rows;
};

const onSort = (event) => {
  lazyParams.value.sortField = event.sortField;
  lazyParams.value.sortOrder = event.sortOrder;
};

const onFilter = (event) => {
  lazyParams.value.first = 0;
};

// Refresh data
const refreshHistory = () => {
  loadHistoryData();
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
        v-model:filters="filters"
        :value="historyData"
        :loading="loading"
        ref="dataTableRef"
        lazy
        paginator
        :totalRecords="totalRecords"
        :first="lazyParams.first"
        :rows="lazyParams.rows"
        :rowsPerPageOptions="[5, 10, 20, 50]"
        @page="onPage($event)"
        @sort="onSort($event)"
        @filter="onFilter($event)"
        filterDisplay="row"
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

      <Column field="id" :header="t('historyPage.columns.id')" sortable filter :showFilterMenu="false">
        <template #filter="{ filterModel, filterCallback }">
          <InputText v-model="filterModel.value" type="text" @input="filterCallback()" :placeholder="t('search')" />
        </template>
      </Column>

      <Column field="jobName" :header="t('name')" sortable filter :showFilterMenu="false">
        <template #body="{ data }">
          {{ data.jobName || '-' }}
        </template>
        <template #filter="{ filterModel, filterCallback }">
          <InputText v-model="filterModel.value" type="text" @input="filterCallback()" :placeholder="t('search')" />
        </template>
      </Column>

      <Column field="jobGroup" :header="t('group')" sortable filter :showFilterMenu="false">
        <template #body="{ data }">
          {{ data.jobGroup || '-' }}
        </template>
        <template #filter="{ filterModel, filterCallback }">
          <InputText v-model="filterModel.value" type="text" @input="filterCallback()" :placeholder="t('search')" />
        </template>
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