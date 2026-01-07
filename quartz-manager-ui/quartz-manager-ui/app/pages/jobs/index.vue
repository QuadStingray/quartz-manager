<script lang="ts" setup>
import {type JobInformation} from "~/composables/generated/models";
import DataTable from 'primevue/datatable';
import {useQuartzApi} from "~/composables/api/quartzApi";
import {ref, computed, watch} from 'vue';
import {useToast} from '#imports';
import {FilterMatchMode} from '@primevue/core/api';
import cronstrue from 'cronstrue/i18n';
import { useConfirm } from "primevue/useconfirm";
const {d, t, n, locale, locales, setLocale} = useI18n();
const {jobsApi} = useQuartzApi();
// Loading state
const loading = ref(false);
const error = ref(null);
const toast = useToast();
const confirm = useConfirmation();

// Data and pagination state
const jobsData = ref<JobInformation[]>([]);
const totalRecords = ref(0);

// Pagination and filter state
const filters = ref({
  name: { value: null, matchMode: FilterMatchMode.CONTAINS },
  group: { value: null, matchMode: FilterMatchMode.CONTAINS },
  description: { value: null, matchMode: FilterMatchMode.CONTAINS }
});

const lazyParams = ref({
  first: 0,
  rows: 10,
  sortField: null,
  sortOrder: null
});

// Fetch jobs data
const loadJobsData = async () => {
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

    const response = await jobsApi.jobsListRaw({
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

    const jobs = await response.value();
    jobsData.value = jobs.map(job => ({
      ...job,
      cronExpressionHuman: job.cronExpression ? cronstrue.toString(job.cronExpression, {locale: locale.value}) : '-'
    }));
  } catch (err) {
    error.value = err.message || t('jobsPage.retry');
    jobsData.value = [];
  } finally {
    loading.value = false;
  }
};

// Initial load
loadJobsData();

// Watch for changes in lazyParams and filters
watch(lazyParams, () => {
  loadJobsData();
}, { deep: true });

watch(filters, () => {
  loadJobsData();
}, { deep: true });

// Format date for display
const formatDate = (date) => {
  if (!date) return '-';
  return d(new Date(date), 'long');
};

// Execute a job
const executeJob = async (job: JobInformation) => {
  try {
    await jobsApi.executeJob({
      jobGroup: job.group,
      jobName: job.name
    });
    // Show success message
    toast.add({
      severity: 'success',
      summary: t('jobsPage.toast.executed'),
      detail: t('jobsPage.toast.executedDetail', { name: job.name }),
      life: 3000
    });
  } catch (err) {
    // Show error message
    toast.add({
      severity: 'error',
      summary: t('jobsPage.toast.executionFailed'),
      detail: err.message || t('jobsPage.toast.executionFailed'),
      life: 3000
    });
  }
};

// Delete a job
const confirmDeleteJob = (job: JobInformation) => {
  confirm.confirmDelete(
    () => deleteJob(job),
    () => {
    }
  );
};

const deleteJob = async (job: JobInformation) => {
  try {
    await jobsApi.deleteJob({
      jobGroup: job.group,
      jobName: job.name
    });
    refreshJobs();
  } catch (err) {
    // Show error message
    toast.add({
      severity: 'error',
      summary: t('jobsPage.toast.deletionFailed'),
      detail: err.message || t('jobsPage.toast.deleteFailedDetail'),
      life: 3000
    });
  }
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
const refreshJobs = () => {
  loadJobsData();
};

</script>

<template>
  <div class="card surface-0">
    <div class="flex justify-between items-center mb-4">
      <h1>{{ t('jobs') }}</h1>
      <div class="flex gap-2">
        <Button
          icon="pi pi-plus"
          @click="$router.push('/jobs/add')"
          :label="t('add')"
          severity="success"
          :tooltip="t('jobsPage.tooltips.add')"
          tooltipPosition="bottom"
        />
        <Button
          icon="pi pi-bolt"
          @click="$router.push('/jobs/trigger')"
          :label="t('jobsPage.addTrigger')"
          severity="info"
          :tooltip="t('jobsPage.tooltips.addTrigger')"
          tooltipPosition="bottom"
        />
        <Button
          icon="pi pi-refresh"
          :loading="loading"
          @click="refreshJobs"
          :label="t('refresh')"
          severity="secondary"
        />
      </div>
    </div>

    <DataTable
        v-model:filters="filters"
        :value="jobsData"
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
    >
      <template #empty>
        <div v-if="error" class="text-center p-4">
          <i class="pi pi-exclamation-triangle text-yellow-500 text-xl mb-2"></i>
          <p>{{ error }}</p>
          <Button :label="t('retry')" @click="refreshJobs" class="mt-2"/>
        </div>
        <div v-else class="text-center p-4">
          <i class="pi pi-info-circle text-blue-500 text-xl mb-2"></i>
          <p>{{ t('jobsPage.noJobsFound') }}</p>
        </div>
      </template>

      <Column field="name" :header="t('name')" sortable filter :showFilterMenu="false">
        <template #filter="{ filterModel, filterCallback }">
          <InputText v-model="filterModel.value" type="text" @input="filterCallback()" :placeholder="t('search')" />
        </template>
      </Column>

      <Column field="group" :header="t('group')" sortable filter :showFilterMenu="false">
        <template #filter="{ filterModel, filterCallback }">
          <InputText v-model="filterModel.value" type="text" @input="filterCallback()" :placeholder="t('search')" />
        </template>
      </Column>

      <Column field="description" :header="t('description')" sortable filter :showFilterMenu="false">
        <template #body="{ data }">
          {{ data.description || '-' }}
        </template>
        <template #filter="{ filterModel, filterCallback }">
          <InputText v-model="filterModel.value" type="text" @input="filterCallback()" :placeholder="t('search')" />
        </template>
      </Column>

      <Column field="cronExpression" :header="t('cronExpression')" sortable>
        <template #body="{ data }">
          <div v-tooltip.bottom="data.cronExpressionHuman">
            {{ data.cronExpression }}
          </div>
        </template>
      </Column>

      <Column field="nextScheduledFireTime" :header="t('jobsPage.columns.nextFireTime')" sortable>
        <template #body="{ data }">
          {{ formatDate(data.nextScheduledFireTime) }}
        </template>
      </Column>

      <Column field="lastScheduledFireTime" :header="t('jobsPage.columns.lastFireTime')" sortable>
        <template #body="{ data }">
          {{ formatDate(data.lastScheduledFireTime) }}
        </template>
      </Column>

      <Column :header="t('actions')" :exportable="false">
        <template #body="{ data }">
          <div class="flex gap-2">
            <Button
                icon="pi pi-play"
                class="p-button-sm p-button-success"
                @click="executeJob(data)"
                :tooltip="t('jobsPage.tooltips.execute')"
                tooltipPosition="bottom"
            />
            <Button
                icon="pi pi-pencil"
                class="p-button-sm p-button-warning"
                @click="$router.push(`/jobs/${data.group}/${data.name}`)"
                :tooltip="t('jobsPage.tooltips.edit')"
                tooltipPosition="bottom"
            />
            <Button
                icon="pi pi-trash"
                class="p-button-sm p-button-danger"
                @click="confirmDeleteJob(data)"
                :tooltip="t('jobsPage.tooltips.delete')"
                tooltipPosition="bottom"
            />
          </div>
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
</style>
