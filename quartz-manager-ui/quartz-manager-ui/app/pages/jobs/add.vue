<script lang="ts" setup>
import {useQuartzApi} from "~/composables/api/quartzApi";
import type {JobConfig} from "~/composables/generated/models";
import {ref, onMounted} from 'vue';
import {useToast} from '#imports';

const {d, t, n, locale, locales, setLocale} = useI18n();
const {jobsApi} = useQuartzApi();
const router = useRouter();
const toast = useToast();

// Form data
const jobConfig = ref<JobConfig>({
  name: '',
  className: '',
  description: '',
  cronExpression: '',
  group: 'DEFAULT',
  priority: 50,
  jobDataMap: {}
});

// Available job classes
const availableJobClasses = ref<string[]>([]);
const loadingClasses = ref(true);

// Form state
const loading = ref(false);
const error = ref<string | null>(null);

// Fetch available job classes
const fetchJobClasses = async () => {
  try {
    loadingClasses.value = true;
    availableJobClasses.value = await jobsApi.possibleJobsList();
  } catch (e: any) {
    console.error('Error fetching job classes:', e);
    error.value = e.message || 'Failed to load job classes';
  } finally {
    loadingClasses.value = false;
  }
};

onMounted(() => {
  fetchJobClasses();
});

// Form validation
const isFormValid = computed(() => {
  return (
    jobConfig.value.name.trim() !== '' &&
    jobConfig.value.className.trim() !== '' &&
    jobConfig.value.cronExpression.trim() !== '' &&
    jobConfig.value.group.trim() !== ''
  );
});

// Handle form submission
const handleSubmit = async () => {
  if (!isFormValid.value) return;

  try {
    loading.value = true;
    error.value = null;

    await jobsApi.registerJob({
      jobConfig: jobConfig.value
    });

    // Show success message
    toast.add({
      severity: 'success',
      summary: t('jobsPage.toast.jobCreated'),
      detail: t('jobsPage.toast.jobCreatedDetail', { name: jobConfig.value.name }),
      life: 3000
    });

    // Navigate back to jobs list
    router.push('/jobs');
  } catch (e: any) {
    console.error('Error creating job:', e);
    error.value = e.message || t('jobsPage.toast.jobCreationFailed');
    toast.add({
      severity: 'error',
      summary: t('jobsPage.toast.jobCreationFailed'),
      detail: e.message || t('jobsPage.toast.jobCreationFailed'),
      life: 5000
    });
  } finally {
    loading.value = false;
  }
};

// Cancel and go back
const handleCancel = () => {
  router.push('/jobs');
};
</script>

<template>
  <div class="card surface-0">
    <!-- Header -->
    <div class="flex items-center gap-3 mb-6">
      <Button
        icon="pi pi-arrow-left"
        @click="handleCancel"
        text
        :tooltip="t('jobsPage.backToJobs')"
        tooltipPosition="bottom"
      />
      <h1 class="text-2xl font-bold">{{ t('jobsPage.addJob') }}</h1>
    </div>

    <!-- Error Message -->
    <Message v-if="error" severity="error" :closable="true" @close="error = null" class="mb-4">
      {{ error }}
    </Message>

    <!-- Form -->
    <form @submit.prevent="handleSubmit" class="space-y-4">
      <!-- Name -->
      <div class="field">
        <label for="name" class="block text-sm font-medium mb-2">
          {{ t('name') }} <span class="text-red-500">*</span>
        </label>
        <InputText
          id="name"
          v-model="jobConfig.name"
          class="w-full"
          :placeholder="t('name')"
          required
        />
      </div>

      <!-- Class Name -->
      <div class="field">
        <label for="className" class="block text-sm font-medium mb-2">
          {{ t('className') }} <span class="text-red-500">*</span>
        </label>
        <Dropdown
          id="className"
          v-model="jobConfig.className"
          :options="availableJobClasses"
          :placeholder="t('className')"
          :loading="loadingClasses"
          class="w-full"
          filter
          showClear
          required
        />
      </div>

      <!-- Group -->
      <div class="field">
        <label for="group" class="block text-sm font-medium mb-2">
          {{ t('group') }} <span class="text-red-500">*</span>
        </label>
        <InputText
          id="group"
          v-model="jobConfig.group"
          class="w-full"
          :placeholder="t('group')"
          required
        />
      </div>

      <!-- Description -->
      <div class="field">
        <label for="description" class="block text-sm font-medium mb-2">
          {{ t('description') }}
        </label>
        <Textarea
          id="description"
          v-model="jobConfig.description"
          rows="3"
          class="w-full"
          :placeholder="t('description')"
        />
      </div>

      <!-- Cron Expression -->
      <div class="field">
        <label for="cronExpression" class="block text-sm font-medium mb-2">
          {{ t('cronExpression') }} <span class="text-red-500">*</span>
        </label>
        <InputText
          id="cronExpression"
          v-model="jobConfig.cronExpression"
          class="w-full"
          :placeholder="t('cronExpression')"
          required
        />
        <small class="text-gray-500">{{ t('jobsPage.hints.cronExample') }}</small>
      </div>

      <!-- Priority -->
      <div class="field">
        <label for="priority" class="block text-sm font-medium mb-2">
          {{ t('priority') }} <span class="text-red-500">*</span>
        </label>
        <InputNumber
          id="priority"
          v-model="jobConfig.priority"
          class="w-full"
          :min="1"
          :max="100"
          showButtons
          required
        />
        <small class="text-gray-500">{{ t('jobsPage.hints.priorityRange') }}</small>
      </div>

      <!-- Form Actions -->
      <div class="flex justify-end gap-2 pt-4">
        <Button
          type="button"
          :label="t('cancel')"
          severity="secondary"
          @click="handleCancel"
          :disabled="loading"
        />
        <Button
          type="submit"
          :label="t('save')"
          severity="success"
          :loading="loading"
          :disabled="!isFormValid || loading"
        />
      </div>
    </form>
  </div>
</template>

<style scoped>
.field {
  margin-bottom: 1.5rem;
}

.field:last-child {
  margin-bottom: 0;
}
</style>
