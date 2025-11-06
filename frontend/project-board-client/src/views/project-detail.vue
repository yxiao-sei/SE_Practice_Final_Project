<template>
<project-b v-if="project" :project="project" />
<p class="h-[42px] px-[15px] bg-[#F1F1F1] text-primary-100 leading-[42px]">岗位报名</p>
<van-tabs v-if="requirements.length && !loading" class="tab" v-model:active="active" scrollspy swipeable
  v-bind="tabProps">
  <van-tab v-for="re in requirements" :title="re.role" :key="re.id">
    <div class="flex flex-col space-y-[5px]">
      <div class="space-x-[17px]">
        <span :span="5" class="lgText">招聘人数</span>
        <span :span="19" class="text-sm text-primary-300">{{ re.count }}</span>
      </div>
      <van-row :gutter="10" class="font-sm text-primary-200 mt-[6px]">
        <van-col :align="'left'" :span="8" class="font-bold text-[#9FACCF]">所属课题编号</van-col>
        <van-col :align="'left'" :span="16" class="text-primary-200">{{ re.projectCode }}</van-col>
        <van-col :align="'left'" :span="8" class="font-bold text-[#9FACCF]">所属课题名称</van-col>
        <van-col :align="'left'" :span="16" class="text-primary-200">{{ re.projectName }}</van-col>
        <van-col :align="'left'" :span="8" class="font-bold text-[#9FACCF]">所需技能</van-col>
        <van-col :align="'left'" :span="16" class="text-primary-200">{{ re.skill }}</van-col>
        <van-col :align="'left'" :span="8" class="font-bold text-[#9FACCF]">需掌握的工具</van-col>
        <van-col :align="'left'" :span="16" class="text-primary-200">{{ re.tools }}</van-col>
        <van-col :align="'left'" :span="8" class="font-bold text-[#9FACCF]">课题分工</van-col>
        <van-col :align="'left'" :span="16" class="text-primary-200">{{ re.role }}</van-col>
        <van-col :align="'left'" :span="8" class="font-bold text-[#9FACCF]">所需实习/研究经历</van-col>
        <van-col :align="'left'" :span="16" class="text-primary-200">{{ re.experience }}</van-col>
      </van-row>
      <div class="text-blue-200 flex-grow !leading-tight">
        {{ re.comment }}
      </div>
      <div class="flex justify-end">
        <van-button color="#3F599A" type="primary" @click="toApplication(re.id)">报名</van-button>
      </div>
    </div>
  </van-tab>
</van-tabs>
<div v-else-if="!loading" class="h-[176px] bg-white w-full flex flex-col items-center justify-center">
  <svg-icon class="text-[55px]" icon-name="icon-a-bijibendiannao2"></svg-icon>
  <p class="text-[#CCD8F9] mt-[12px]">暂无岗位可以进行报名</p>
</div>
</template>

<script setup lang="ts">
import { findProjectByIdApi } from '@/service/project';
import { ref, computed, onMounted, shallowRef } from 'vue';
import { findRequirementsListApi } from '@/service/requirements'
import { useRoute, useRouter } from 'vue-router';
import ProjectB from '@/components/ProjectB.vue'

const tabProps = {
  'title-active-color': '#003FDE',
  'title-inactive-color': '#9FACCF',
  animated: true,
  shrink: true,
  color: '#003FDE'
}
const loading = ref(false)
const active = ref(0)
const route = useRoute();
const router = useRouter();
const project = ref<any>({});
const requirements = shallowRef<any[]>([])
const project_id = computed(() => route.query?.project_id)
const project_code = computed(() => route.query?.project_code)

const toApplication = (requirementId: string) => {
  router.push({
    name: '课题报名',
    query: {
      project_id: project_id.value,
      requirement_id: requirementId
    }
  })
}
onMounted(() => {
  loading.value = true
  const projectApi = findProjectByIdApi(project_id.value as string)
  const requirementsApi = findRequirementsListApi({
    filters: {
      projectCode: {
        $eq: project_code.value
      }
    },
    pagination: {
      page: 1,
      pageSize: 1000000000
    }
  })
  Promise.all([projectApi, requirementsApi]).then((result) => {
    project.value = result[0]
    requirements.value = result[1].result
  }).finally(() => loading.value = false)
})
</script>

<style scoped>
.active {
  @apply text-blue-100;
}

.active::after {
  position: absolute;
  content: ' ';
  @apply absolute h-[2px] bg-blue-100 w-full bottom-0 left-0;
}

.tab {
  @apply relative h-full inline-block transition-all ease-in-out cursor-pointer;
}

.tab {
  --van-padding-sm: 15px;
  --van-padding-xs: 15px;
}

.lgText {
  @apply text-sm text-primary-200 font-bold;
}

:global(.tab .van-tabs__nav--line) {
  padding-right: 0;
  padding-left: 0;
}

:global(.tab .van-tabs__content) {
  @apply bg-white px-[20px] pt-[10px] pb-[20px] mt-0 relative;
}

:global(.tab .van-tabs__content::after) {
  position: absolute;
  content: ' ';
  left: 15px;
  right: 15px;
  height: 1px;
  background-color: #CFDAE5;
  top: 0;
}
</style>
