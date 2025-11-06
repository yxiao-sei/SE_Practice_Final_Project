<template>
  <div class="px-[15px] py-[10px] bg-white">
    <div class="flex items-center space-x-[6px]">
      <span :class="statusClassName" v-show="project.recruitStatus">{{ project.recruitStatus }}</span>
      <span class="truncate text-lg text-blue-200 flex-grow text-left">{{ project.name }}</span>
    </div>
    <div class="w-full h-[1px] bg-gray-100 mt-[10px]"></div>
    <div class="flex flex-wrap mt-[3px]" v-if="labels.length">
      <span class="label" v-for="i in labels" :key="i">{{ i }}</span>
    </div>
    <van-row :gutter="10" class="font-sm text-blue-200 mt-[6px]">
      <van-col :align="'left'" :span="6" class="font-bold">时间</van-col>
      <van-col :align="'left'" :span="18">{{ dateRange }}</van-col>
      <van-col :align="'left'" :span="6" class="font-bold">预期成果</van-col>
      <van-col :align="'left'" :span="18">{{ project.researchOutcome }}</van-col>
      <van-col :align="'left'" :span="6" class="font-bold">研究方向</van-col>
      <van-col :align="'left'" :span="18">{{ project.researchArea }}</van-col>
    </van-row>
    <div class="text-left text-primary-300">
      <p class="mt-[5px] text-blue-200">
        <van-text-ellipsis class="!leading-tight" rows="3" :content="project.description" expand-text="展开"
          collapse-text="收起" />
      </p>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { toRef, computed } from 'vue'
import router from '@/router'

const props: any = defineProps(['project', 'link'])
const project = toRef(props, 'project')
const isLink = toRef(props, 'link')
const labels = computed(() => {
  return project.value?.labels ? project.value.labels?.split('，') : []
})
const statusClassName = computed(() => {
  return project.value?.recruitStatus === '成员招募中' ? 'open' : 'joined'
})
const dateRange = computed(() => {
  const startDate = project.value?.beginDate
  const endDte = project.value?.endDate
  return `${startDate ?? ''} - ${endDte ?? ''}`
})
const toApplication = () => {
  if (project.value?.recruitStatus === '成员招募中') {
    router.push({ path: '/application', query: { project_id: project.value?.id } })
  }
}
</script>

<style scoped lang="scss">
.open {
  @apply text-green-200 whitespace-nowrap flex-shrink-0 h-[28px] px-[8px] bg-green-200/15 leading-[28px] text-center rounded-[3px];
}

.joined {
  @apply text-blue-200 whitespace-nowrap flex-shrink-0 h-[28px] px-[8px] bg-green-200/15 leading-[28px] text-center rounded-[3px];
}

.label {
  @apply text-blue-100 mt-[5px] mr-[5px] px-[7px] h-[28px] bg-blue-100/15 leading-[28px] text-center rounded-[3px];
}
</style>
