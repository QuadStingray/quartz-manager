<script lang="ts" setup>
import {JobsApi} from "~/api";
import {useQuartzApi} from "~/composables/api/quartzApi";

const { d, t, n, locale, locales, setLocale } = useI18n()

const { jobsApi } = useQuartzApi()

</script>

<template>
  <div class="card surface-0 text-center">
    <h1>{{ t('jobs') }}</h1>
    <div class="gap-2 grid grid-cols-2 lg:grid-cols-3">

    </div>
  </div>
</template>

<style scoped lang="scss">

</style>