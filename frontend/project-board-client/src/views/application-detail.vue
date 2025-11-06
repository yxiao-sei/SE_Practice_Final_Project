<template>
  <div class="flex flex-col h-full">
    <div class="flex-grow overflow-y-auto">
      <application-card-b v-if="application" :application="application" />
      <template v-if="application.state === '草稿'">
        <p class="h-[42px] px-[15px] bg-[#F1F1F1] text-primary-100 leading-[42px]">
          自我推荐
        </p>
        <van-field v-model="form.selfRecommendContent" :rows="7" autosize type="textarea" placeholder="请填写" />
        <p class="h-[42px] px-[16px] bg-[#F1F1F1] text-primary-100 leading-[42px]">
          附件上传
        </p>
        <van-cell-group>
          <van-cell v-for="({ name, code }, index) in form.attachments" :title="name" :key="index">
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
      </template>
      <template v-else>
        <p class="h-[42px] px-[15px] bg-[#F1F1F1] text-primary-100 leading-[42px]">
          自我推荐
        </p>
        <div class="bg-white text-[#9FACCF] px-[15px] py-[10px] !leading-tight">
          {{ application.selfRecommendContent }}
        </div>
        <p class="h-[42px] px-[16px] bg-[#F1F1F1] text-primary-100 leading-[42px]">
          附件上传
        </p>
        <van-cell-group>
          <van-cell v-for="({ name, code }, index) in form.attachments" :title="name" :key="index">
            <template #right-icon>
              <span @click="downloadFileApi(code)" class="text-[#FF296A]">下载</span>
            </template>
          </van-cell>
        </van-cell-group>
      </template>
    </div>
    <div class="fixed-button space-y-[10px]" v-if="application.state === '草稿'">
      <van-button type="primary" @click="onSubmit('待审核')" color="#3F599A" block>提交报名</van-button>
      <van-button type="primary" @click="cancel" color="#85A3EF" block>删除报名</van-button>
    </div>
    <div class="fixed-single-button space-y-[10px]" v-else-if="application.state === '已取消'">
      <van-button type="primary" @click="cancel" color="#85A3EF" block>删除报名</van-button>
    </div>
    <div class="fixed-single-button space-y-[10px]"
      v-else-if="application.state == '待审核' || application.state == '通过' || application.state == '拒绝' || application.state == '已通过，待面试'">
      <van-button type="primary" @click="onSubmit('草稿')" color="#85A3EF" block>撤回</van-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive } from "vue";
import { useRoute, useRouter } from "vue-router";
import { showConfirmDialog, showNotify } from "vant";
import ApplicationCardB from "@/components/ApplicationCardB.vue";
import { uploadFileApi, downloadFileApi, type UploadBody } from "@/service/file";
import { useVirtualUpload } from "@/hooks/useVirtualUpload";
import {
  findApplicationByIdApi,
  updateApplicationApi,
  deleteApplicationApi,
} from "@/service/application";

const route = useRoute();
const router = useRouter();
const application = ref<any>({});
const application_id = computed(() => route.query?.application_id);
const form = reactive<{ selfRecommendContent: string; attachments: UploadBody }>(
  {
    selfRecommendContent: "",
    attachments: [],
  }
);
const deleteFile = (index: number) => {
  form.attachments.splice(index, 1);
};

const accepts = [
  "application/msword",
  "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
  "application/pdf",
];

function handleFileChange(files: File[]) {
  form.attachments = files.map((file) => ({
    name: file.name,
    code: "",
    file: file,
  }));
}

const { handle, handleDragEnter, handleDragLeave, handleDragOver, handleDrop } =
  useVirtualUpload({
    accept: accepts.join(","),
    multiple: true,
    end: handleFileChange,
  });

const onSubmit = (status: string) => {
  showConfirmDialog({
    title: "提示",
    message: status === "草稿" ? "确认撤回报名吗？" : "确认提交报名吗？",
    confirmButtonColor: "#3F599A",
  })
    .then(() => {
      const save = () => {
        const data = {
          state: status,
          selfRecommendContent: form.selfRecommendContent,
          attachments: form.attachments
        };
        updateApplicationApi(application_id.value as string, data).then((res) => {
          showNotify({ type: "success", message: "提交成功" });
          getDetail(res);
        });
      };
      const hasNewFile = form.attachments.find((item) => !!item.file);
      if (hasNewFile) {
        uploadFileApi(form.attachments).then((res) => {
          form.attachments = res;
          save();
        });
      } else {
        save();
      }
    })
    .catch(() => { });
};
const cancel = () => {
  showConfirmDialog({
    title: "提示",
    message: "确认删除报名吗？",
    confirmButtonColor: "#3F599A",
  })
    .then(() => {
      deleteApplicationApi(application_id.value as string).then((result) => {
        router.back();
      });
    })
    .catch(() => { });
};

const getDetail = async (result?: any) => {
  const fillData = result ? result : await findApplicationByIdApi(application_id.value as string)
  if (fillData) {
    application.value = fillData;
    form.selfRecommendContent = fillData.selfRecommendContent;
    form.attachments = fillData.attachments ?? [];
  }
};
onMounted(() => {
  getDetail()
});
</script>

<style scoped>
.fixed-button {
  padding-bottom: calc(12px + constant(safe-area-inset-bottom));
  padding-bottom: calc(12px + env(safe-area-inset-bottom));
  height: calc(122px + constant(safe-area-inset-bottom));
  height: calc(122px + env(safe-area-inset-bottom));
  padding-top: 12px;
  padding-left: 15px;
  padding-right: 15px;
}

.fixed-single-button {
  padding-bottom: calc(12px + constant(safe-area-inset-bottom));
  padding-bottom: calc(12px + env(safe-area-inset-bottom));
  height: calc(56 + constant(safe-area-inset-bottom));
  height: calc(56 + env(safe-area-inset-bottom));
  padding-top: 12px;
  padding-left: 15px;
  padding-right: 15px;
}

.active {
  @apply text-blue-100;
}

.active::after {
  position: absolute;
  content: " ";
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
  content: " ";
  left: 15px;
  right: 15px;
  height: 1px;
  background-color: #cfdae5;
  top: 0;
}
</style>
