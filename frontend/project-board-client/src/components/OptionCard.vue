<template>
  <div class="px-[14px] py-[10px] bg-white">
    <div class="flex items-center space-x-[6px]">
      <span class="truncate text-primary-100 text-lg w-full text-left">{{ project.name }}</span>
    </div>
    <!-- 标签 -->
    <div class="flex flex-wrap mt-[3px] mb-[5px]" v-if="labels.length">
      <span class="label" v-for="i in labels" :key="i">{{ i }}</span>
    </div>
    <van-row :gutter="10">
      <van-col :align="'left'" class="text-primary-200 font-[600]" :span="8">课题预期起止日期</van-col>
      <van-col :align="'left'" :span="16" class="text-['#9FACCF']">{{ dateRange }}</van-col>
    </van-row>
    <van-row :gutter="10" class="mt-[5px]">
      <van-col :align="'left'" class="text-primary-200 font-[600]" :span="8">面试岗位</van-col>
      <van-col :align="'left'" :span="16" class="text-['#9FACCF']">{{ project.role }}</van-col>
    </van-row>
    <p class="mt-[5px] text-blue-200">
      <van-text-ellipsis class="!leading-tight" rows="3" :content="project.description" expand-text="展开"
        collapse-text="收起" />
    </p>
    <div class="flex justify-end mt-[10px]">
      <van-button @click="toNew" color="#3F599A" size="small" class="w-[78px]">预约</van-button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { toRef, computed } from 'vue'
import { useRouter } from 'vue-router';
import { useUserInfo } from '@/stores/cacheInfo';

const props: any = defineProps(['project', 'link'])
const project = toRef(props, 'project')
const router = useRouter()

const labels = computed(() => {
  return project.value?.labels ? project.value.labels?.split('，') : []
})
const dateRange = computed(() => {
  const startDate = project.value?.beginDate
  const endDte = project.value?.endDate
  return `${startDate ?? ''} - ${endDte ?? ''}`
})
const toNew = async () => {
  router.push({
    name: "新增预约",
    query: {
      project_code: project.value.code,
      role_id: project.value.roleId
    }
  })
}
</script>

<style scoped lang="scss">
.open {
  @apply text-green-200 flex-shrink-0 whitespace-nowrap h-[28px] px-[8px] bg-green-200/15 leading-[28px] text-center rounded-[3px];
}

.joined {
  @apply text-blue-200 flex-shrink-0 whitespace-nowrap h-[28px] px-[8px] bg-green-200/15 leading-[28px] text-center rounded-[3px];
}

.label {
  @apply text-blue-100 whitespace-nowrap mt-[5px] mr-[5px] px-[7px] h-[28px] bg-blue-100/15 leading-[28px] text-center rounded-[3px];
}
</style>
