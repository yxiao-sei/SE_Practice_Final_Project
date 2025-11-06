<template>
  <div class="px-[14px] py-[10px] bg-white">
    <div class="flex items-center space-x-[6px]" :style="{ cursor: allowLink ? 'pointer' : '' }" @click="toDetail">
      <span :class="statusClassName" v-show="project.recruitStatus">{{ project.recruitStatus }}</span>
      <span class="truncate text-primary-100 text-lg flex-grow text-left">{{ project.name }}</span>
      <span v-if="allowLink"><svg-icon icon-name="icon-a-lujing905" class="color-[#62696F]"></svg-icon></span>
    </div>
    <div class="w-full h-[1px] bg-gray-100 mt-[8px]"></div>
    <!-- 标签 -->
    <div class="flex flex-wrap mt-[3px]" v-if="labels.length">
      <span class="label" v-for="i in labels" :key="i">{{ i }}</span>
    </div>
    <div class="text-left text-primary-300 mt-[8px]">
      <p>
        <span class="text-primary-200 font-[600] mr-[10px]">
          课题预期起止日期
        </span> {{ dateRange }}
      </p>
      <p class="mt-[5px] text-blue-200">
        <van-text-ellipsis class="!leading-tight" rows="3" :content="project.description" expand-text="展开"
          collapse-text="收起" />
      </p>
    </div>
    <van-row :gutter="10" class="text-primary-200 font-[600] mt-[8px]">
      <van-col :align="'left'" :span="12">预期成果</van-col>
      <van-col :align="'left'" :span="12">研究方向</van-col>
    </van-row>
    <van-row :gutter="10" class="text-primary-400">
      <van-col :align="'left'" :span="12">{{ project.researchOutcome }}</van-col>
      <van-col :align="'left'" :span="12">{{ project.researchArea }}</van-col>
    </van-row>
    <van-row :gutter="10" class="text-primary-200 font-[600] mt-[8px]">
      <van-col :align="'left'" :span="12">课题负责老师姓名</van-col>
      <van-col :align="'left'" :span="12">课题负责老师工号</van-col>
    </van-row>
    <van-row :gutter="10" class="text-primary-400">
      <van-col :align="'left'" :span="12">{{ project.leadingTeacherName }}</van-col>
      <van-col :align="'left'" :span="12">{{ project.leadingTeacherNumber }}</van-col>
    </van-row>
  </div>
</template>

<script lang="ts" setup>
import { toRef, computed } from 'vue'
import { useRouter } from 'vue-router';
import { findApplicationsListApi } from '@/service/application';
import { useUserInfo } from '@/stores/cacheInfo';

const props: any = defineProps(['project', 'link'])
const project = toRef(props, 'project')
const isLink = toRef(props, 'link')
const router = useRouter()
const allowLink = computed(() => {
  return true
  // return isLink.value && project.value?.recruitStatus === '成员招募中'
})

const statusClassName = computed(() => {
  return project.value?.recruitStatus === '成员招募中' ? 'open' : 'joined'
})
const labels = computed(() => {
  // return ['前端', '后端', '产品','前端', '后端', '产品','前端', '后端', '产品']
  return project.value?.labels ? project.value.labels?.split('，') : []
})
const dateRange = computed(() => {
  const startDate = project.value?.beginDate
  const endDte = project.value?.endDate
  return `${startDate ?? ''} - ${endDte ?? ''}`
})
const { username } = useUserInfo()
const toDetail = async () => {
  const { count, result } = await findApplicationsListApi({
    filters: {
      projectCode: {
        $eq: project.value?.code
      },
      studentNumber: {
        $eq: username
      },
    }
  })
  if (count) {
    router.push({ name: '课题报名详情', query: { application_id: result?.[0]?.id } })
  } else {
    router.push({ name: '课题详情', query: { project_id: project.value?.id, project_code: project.value?.code } })
  }
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
