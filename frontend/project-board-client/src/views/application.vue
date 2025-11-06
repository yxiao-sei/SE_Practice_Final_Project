<template>
<div class="h-full w-full flex flex-col">
  <div class="flex-grow overflow-y-auto">
    <user-card />
    <van-cell-group class="mt-[10px]">
      <van-cell title="手机号" :value="principal.telephone" />
      <van-cell title="邮箱" :value="principal.email" />
    </van-cell-group>
    <template v-if="project">
      <div class="h-[42px] px-[16px] bg-[#F1F1F1] leading-[42px] text-blue-200 text-sm space-x-[26px]">
        <span @click="activeTab = '课题信息'" :class="[activeTab === '课题信息' ? 'active' : '', 'tab']">课题信息</span>
        <span @click="activeTab = '报名岗位'" :class="[activeTab === '报名岗位' ? 'active' : '', 'tab']">报名岗位</span>
      </div>
    </template>
    <template v-if="project">
      <div v-show="activeTab === '课题信息'">
        <project-a :project="project" />
      </div>
    </template>
    <template v-if="requirement">
      <div v-show="activeTab === '报名岗位'">
        <div class="space-y-[5px] bg-white px-[16px] min-h-[221px] py-[10px]">
          <div class="space-x-[17px]">
            <span :span="5" class="lgText">招聘人数</span>
            <span :span="19" class="text-sm text-primary-300">{{ requirement.count }}</span>
          </div>
          <van-row :gutter="10" class="font-sm text-primary-200 mt-[6px]">
            <van-col :align="'left'" :span="8" class="font-bold text-[#9FACCF]">所属课题编号</van-col>
            <van-col :align="'left'" :span="16" class="text-primary-200">{{ requirement.projectCode }}</van-col>
            <van-col :align="'left'" :span="8" class="font-bold text-[#9FACCF]">所属课题名称</van-col>
            <van-col :align="'left'" :span="16" class="text-primary-200">{{ requirement.projectName }}</van-col>
            <van-col :align="'left'" :span="8" class="font-bold text-[#9FACCF]">所需技能</van-col>
            <van-col :align="'left'" :span="16" class="text-primary-200">{{ requirement.skill }}</van-col>
            <van-col :align="'left'" :span="8" class="font-bold text-[#9FACCF]">需掌握的工具</van-col>
            <van-col :align="'left'" :span="16" class="text-primary-200">{{ requirement.tools }}</van-col>
            <van-col :align="'left'" :span="8" class="font-bold text-[#9FACCF]">课题分工</van-col>
            <van-col :align="'left'" :span="16" class="text-primary-200">{{ requirement.role }}</van-col>
            <van-col :align="'left'" :span="8" class="font-bold text-[#9FACCF]">所需实习/研究经历</van-col>
            <van-col :align="'left'" :span="16" class="text-primary-200">{{ requirement.experience }}</van-col>
          </van-row>
          <div class="text-blue-200 flex-grow">
            <van-text-ellipsis class="!leading-tight" rows="3" :content="requirement.comment" expand-text="展开"
              collapse-text="收起" />
          </div>
        </div>
      </div>
    </template>
    <p class="h-[42px] px-[16px] bg-[#F1F1F1] text-primary-100 leading-[42px]">自我推荐</p>
    <van-field v-model="form.selfRecommendContent" :rows="7" autosize type="textarea" placeholder="请填写" />
    <p class="h-[42px] px-[16px] bg-[#F1F1F1] text-primary-100 leading-[42px]">附件上传</p>
    <van-cell-group>
      <van-cell v-for="{ name, code }, index in form.attachments" :title="name" :key="index">
        <template #right-icon>
          <span @click="deleteFile(index)" class="text-[#FF296A]">删除</span>
        </template>
      </van-cell>
    </van-cell-group>
    <div class="px-[16px] h-[44px] leading-[44px] text-[#003FDE]">
      <span @click="handle">
        <van-icon name="plus" />
        新增附件
      </span>
    </div>
  </div>
  <div>
    <div class="fixed-container">
      <div class="fixed-button">
        <van-button type="primary" @click="onSubmit('草稿')" color="#85A3EF" block>保存草稿</van-button>
        <van-button type="primary" @click="onSubmit('待审核')" color="#3F599A" block>确认报名</van-button>
      </div>
    </div>
  </div>
</div>
</template>

<script setup lang="ts">
import { findProjectByIdApi } from '@/service/project';
import UserCard from '@/components/UserCard.vue';
import { createApplicationApi } from '@/service/application';
import { findRequirementByIdApi } from '@/service/requirements';
import { ref, computed, onMounted, reactive } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useUserInfo } from '@/stores/cacheInfo';
import { storeToRefs } from 'pinia';
import ProjectA from '@/components/ProjectA.vue';
import { showConfirmDialog, showNotify } from 'vant';
import { uploadFileApi, type UploadBody } from '@/service/file';
import { useVirtualUpload } from "@/hooks/useVirtualUpload";

const { user: principal } = storeToRefs(useUserInfo());
const route = useRoute();
const router = useRouter();
const project = ref<any>(null);
const requirement = ref<any>(null);
const loading = ref(false)
const project_id = computed(() => route.query?.project_id)
const requirement_id = computed(() => route.query?.requirement_id)
const form = reactive<{
  projectRescuitRole: string,
  selfRecommendContent: string,
  attachments: UploadBody
}>({
  projectRescuitRole: '',
  selfRecommendContent: '',
  attachments: []
})

const deleteFile = (index: number) => {
  form.attachments.splice(index, 1)
}

const accepts = ['application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 'application/pdf'];

function handleFileChange(files: File[]) {
  form.attachments = files.map(file => ({ name: file.name, code: '', file: file }))
}

const { handle, handleDragEnter, handleDragLeave, handleDragOver, handleDrop } = useVirtualUpload({ accept: accepts.join(','), multiple: true, end: handleFileChange })

const activeTab = ref('课题信息');
const onSubmit = (state: any) => {
  showConfirmDialog({
    title: '提示',
    message: `确认${state === '草稿' ? '保存为草稿' : '提交报名'}吗？`,
    confirmButtonColor: '#3F599A'
  })
    .then(() => {
      loading.value = true
      const save = () => {
        const data = {
          ...form,
          projectRescuitRole: requirement.value?.role,
          projectRescuitRoleId: requirement.value?.id,
          studentNumber: principal.value.username,
          studentName: principal.value.realName,
          projectCode: project.value?.code,
          projectName: project.value?.name,
          studentTelephone: principal.value?.telephone,
          studentEmail: principal.value?.email,
          leadingTeacherNumber: project.value?.leadingTeacherNumber,
          leadingTeacherName: project.value?.leadingTeacherName,
          state: state
        }
        createApplicationApi(data).then(() => {
          showNotify({
            type: 'success',
            message: `${state === '草稿' ? '保存成功' : '报名成功'}`
          })
          router.push({
            name: '个人中心',
            replace: true
          })
        }).finally(() => loading.value = false)
      }
      const hasNewFile = form.attachments.find(item => !!item.file)
      if (hasNewFile) {
        uploadFileApi(form.attachments).then((res) => {
          form.attachments = res;
          save()
        })
      } else {
        save()
      }
    })
    .catch(() => { });
}

onMounted(() => {
  const projectApi = findProjectByIdApi(project_id.value as string)
  const requirementApi = findRequirementByIdApi(requirement_id.value as string)
  Promise.all([projectApi, requirementApi]).then(res => {
    project.value = res[0]
    requirement.value = res[1]
  })
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

.fixed-container {
  width: 100%;
  display: block;
  height: calc(122px + constant(safe-area-inset-bottom));
  height: calc(122px + env(safe-area-inset-bottom));
}

.fixed-button {
  padding-bottom: calc(12px + constant(safe-area-inset-bottom));
  padding-bottom: calc(12px + env(safe-area-inset-bottom));
  padding-top: 12px;
  @apply fixed px-[15px] space-y-[10px] z-10 right-0 left-0 bottom-0;
}

.lgText {
  @apply text-sm text-primary-200 font-bold;
}
</style>
