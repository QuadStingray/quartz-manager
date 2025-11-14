<script lang="ts" setup>
import {type JobInformation} from "~/composables/generated/models";
import DataTable from 'primevue/datatable';
import {useQuartzApi} from "~/composables/api/quartzApi";
import {ref, computed, watch} from 'vue';
import {useToast, useAsyncData} from '#imports';
import cronstrue from 'cronstrue/i18n';
import { useConfirm } from "primevue/useconfirm";
const {d, t, n, locale, locales, setLocale} = useI18n();
const {jobsApi} = useQuartzApi();
// Loading state
const loading = ref(false);
const error = ref(null);
const toast = useToast();
const confirm = useConfirmation();

// Fetch jobs data using useAsyncData
const {data: jobsData, pending, refresh: refreshJobs, error: asyncError} = useAsyncData('jobs', async () => {
  const response = await jobsApi.jobsList();
  return response.map(job => ({
    ...job,
    cronExpressionHuman: job.cronExpression ? cronstrue.toString(job.cronExpression, {locale: locale.value}) : '-'
  }));
});

// Update loading state when pending changes
watch(pending, (isPending) => {
  loading.value = isPending;
});

// Update error state when asyncError changes
watch(asyncError, (newError) => {
  if (newError) {
    console.error('Error fetching jobs:', newError);
    error.value = newError.message || 'Failed to load jobs';
  } else {
    error.value = null;
  }
});

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
      summary: 'Job Executed',
      detail: `Job ${job.name} has been executed successfully`,
      life: 3000
    });
  } catch (err) {
    console.error('Error executing job:', err);
    // Show error message
    toast.add({
      severity: 'error',
      summary: 'Execution Failed',
      detail: err.message || 'Failed to execute job',
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
    console.error('Error deleting job:', err);
    // Show error message
    toast.add({
      severity: 'error',
      summary: 'Deletion Failed',
      detail: err.message || 'Failed to delete job',
      life: 3000
    });
  }
};

</script>

<template>
  <div class="card surface-0">
    <div class="flex justify-between items-center mb-4">
      <h1>{{ t('jobs') }}</h1>
      <div>
        <Button
            icon="pi pi-refresh"
            @click="refreshJobs"
            :loading="loading"
            class="mr-2"
            tooltip="Refresh"
            tooltipPosition="bottom"
        />
      </div>
    </div>

    <DataTable
        :value="jobsData"
        :loading="loading"
        ref="dataTableRef"
        paginator
        :rows="10"
        :rowsPerPageOptions="[5, 10, 20, 50]"
        filterDisplay="menu"
        responsiveLayout="scroll"
        stripedRows
    >
      <template #empty>
        <div v-if="error" class="text-center p-4">
          <i class="pi pi-exclamation-triangle text-yellow-500 text-xl mb-2"></i>
          <p>{{ error }}</p>
          <Button label="Retry" @click="refreshJobs" class="mt-2"/>
        </div>
        <div v-else class="text-center p-4">
          <i class="pi pi-info-circle text-blue-500 text-xl mb-2"></i>
          <p>No jobs found</p>
        </div>
      </template>

      <template #header>
        <div class="flex justify-between">
          <span class="p-input-icon-left">
            <i class="pi pi-search"/>
            <InputText placeholder="Search..."/>
          </span>
        </div>
      </template>

      <Column field="name" header="Name" sortable>
      </Column>

      <Column field="group" header="Group" sortable>
      </Column>

      <Column field="description" header="Description" sortable>
        <template #body="{ data }">
          {{ data.description || '-' }}
        </template>
      </Column>

      <Column field="cronExpression" header="Cron Expression" sortable>
        <template #body="{ data }">
          <div v-tooltip.bottom="data.cronExpressionHuman">
            {{ data.cronExpression }}
          </div>
        </template>
      </Column>

      <Column field="nextScheduledFireTime" header="Next Fire Time" sortable>
        <template #body="{ data }">
          {{ formatDate(data.nextScheduledFireTime) }}
        </template>
      </Column>

      <Column field="lastScheduledFireTime" header="Last Fire Time" sortable>
        <template #body="{ data }">
          {{ formatDate(data.lastScheduledFireTime) }}
        </template>
      </Column>

      <Column header="Actions" :exportable="false">
        <template #body="{ data }">
          <div class="flex gap-2">
            <Button
                icon="pi pi-play"
                class="p-button-sm p-button-success"
                @click="executeJob(data)"
                tooltip="Execute Job"
                tooltipPosition="bottom"
            />
            <Button
                icon="pi pi-trash"
                class="p-button-sm p-button-danger"
                @click="confirmDeleteJob(data)"
                tooltip="Delete Job"
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