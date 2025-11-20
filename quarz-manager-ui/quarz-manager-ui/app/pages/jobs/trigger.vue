<script lang="ts" setup>
import {useQuartzApi} from "~/composables/api/quartzApi";
import type {TriggerConfig} from "~/composables/generated/models";
import {ref, onMounted} from 'vue';
import {useToast} from '#imports';

const {d, t, n, locale, locales, setLocale} = useI18n();
const {jobsApi} = useQuartzApi();
const router = useRouter();
const toast = useToast();

// Form data
const triggerConfig = ref<TriggerConfig>({
  className: '',
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
  return triggerConfig.value.className.trim() !== '';
});

// Handle form submission
const handleSubmit = async () => {
  if (!isFormValid.value) return;

  try {
    loading.value = true;
    error.value = null;

    await jobsApi.registerTrigger({
      triggerConfig: triggerConfig.value
    });

    // Show success message
    toast.add({
      severity: 'success',
      summary: t('jobsPage.toast.triggerCreated'),
      detail: t('jobsPage.toast.triggerCreatedDetail'),
      life: 3000
    });

    // Navigate back to jobs list
    router.push('/jobs');
  } catch (e: any) {
    console.error('Error creating trigger:', e);
    error.value = e.message || t('jobsPage.toast.triggerCreationFailed');
    toast.add({
      severity: 'error',
      summary: t('jobsPage.toast.triggerCreationFailed'),
      detail: e.message || t('jobsPage.toast.triggerCreationFailed'),
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
      <h1 class="text-2xl font-bold">{{ t('jobsPage.addTrigger') }}</h1>
    </div>

    <!-- Info Message -->
    <Message severity="info" :closable="false" class="mb-4">
      A one-time trigger schedules a job to execute immediately once.
    </Message>

    <!-- Error Message -->
    <Message v-if="error" severity="error" :closable="true" @close="error = null" class="mb-4">
      {{ error }}
    </Message>

    <!-- Form -->
    <form @submit.prevent="handleSubmit" class="space-y-4">
      <!-- Class Name -->
      <div class="field">
        <label for="className" class="block text-sm font-medium mb-2">
          {{ t('className') }} <span class="text-red-500">*</span>
        </label>
        <Dropdown
          id="className"
          v-model="triggerConfig.className"
          :options="availableJobClasses"
          :placeholder="t('className')"
          :loading="loadingClasses"
          class="w-full"
          filter
          showClear
          required
        />
        <small class="text-gray-500">{{ t('jobsPage.hints.selectJobClass') }}</small>
      </div>

      <!-- Priority -->
      <div class="field">
        <label for="priority" class="block text-sm font-medium mb-2">
          {{ t('priority') }} <span class="text-red-500">*</span>
        </label>
        <InputNumber
          id="priority"
          v-model="triggerConfig.priority"
          class="w-full"
          :min="1"
          :max="10"
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
          severity="info"
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
