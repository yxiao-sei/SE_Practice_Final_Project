<template>
  <common-dialog id="loadingEl" @close="closeModal" width="min(45%,644px)" :loading="loading" close-on-click-modal
    close-on-press-escape show-close>
    <p class="large">{{ fillData?.projectName }}</p>
    <p class="large">{{ fillData?.projectRescuitRole }}</p>
    <el-row :gutter="10">
      <el-col :span="5" class="bold">预约时间</el-col>
      <el-col :span="19">{{ fillData?.appointmentDate.replaceAll('-', '/') }}&nbsp;&nbsp;{{
        fillData?.appointmentTime
      }}</el-col>
      <el-col :span="5" class="bold">预约校区</el-col>
      <el-col :span="19">{{ fillData?.campus }}</el-col>
      <el-col :span="5" class="bold">详细地址</el-col>
      <el-col :span="19">{{ fillData?.address }}</el-col>
      <el-col :span="5" class="bold">备注</el-col>
      <el-col :span="19">{{ fillData?.comment }}</el-col>
    </el-row>
    <p class="subtitle">应聘人员信息</p>
    <el-row :gutter="10">
      <el-col :span="5" class="bold">姓名</el-col>
      <el-col :span="19">{{ fillData?.studentName }}</el-col>
      <el-col :span="5" class="bold">学号</el-col>
      <el-col :span="19">{{ fillData?.studentNumber }}</el-col>
    </el-row>
    <el-form :rules="rules" label-position="top" ref="ruleFormEl" :model="ruleForm" class="dialog-form">
      <el-form-item class="bold-label" label="拒绝理由" prop="approvalMessage">
        <el-input type="textarea" :rows="4" resize="none" placeholder="非必填，选择通过无需填写" v-model="ruleForm.approvalMessage"
          clearable />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-row :gutter="10">
        <el-col :span="14"></el-col>
        <el-col :span="5">
          <el-button @click="submit('拒绝')" type="danger">拒绝</el-button>
        </el-col>
        <el-col :span="5">
          <el-button type="primary" @click="submit('通过')" color="#04A80F">通过</el-button>
        </el-col>
      </el-row>
    </template>
  </common-dialog>
</template>

<script setup lang="ts">
import { reactive, ref, toRef } from 'vue';
import commonDialog from '~/enDialog/Index.vue';
import { useValidateForm } from '@/hooks/useValidateForm';
import { ElLoading } from 'element-plus'
import { updateAppointmentApi } from "@/service/api/appointment";

const props = defineProps(['fillData', 'visible'])
const fillData = toRef(props, 'fillData');
const emits = defineEmits(['closeModal']);
const { ruleForm, ruleFormEl, validateForm, resetForm } = useValidateForm({
  approvalMessage: "",
});

const rules = reactive<any>({
  approvalMessage: [
    { required: false, message: '请输入', trigger: 'blur' }
  ],
});
const loading = ref(false);

const submit = (state: string) => {
  validateForm().then(() => {
    const instance = ElLoading.service({
      lock: true,
      target: '#loadingEl',
    });
    updateAppointmentApi(fillData.value.id, {
      approvalMessage: ruleForm.approvalMessage,
      state: state
    }).then(() => {
      emits('closeModal', 'refresh');
      resetForm();
    }).finally(() => {
      instance.close();
    })
  })
}

const closeModal = (status: string) => {
  emits('closeModal', status);
  resetForm();
};
</script>

<style scoped lang="scss">
.title {
  color: #405078;
  font-size: 20px;
  font-weight: bold;
  margin: 16px 0 21px 0;
}

.subtitle {
  color: #405078;
  font-size: 14px;
  font-weight: bold;
  margin: 10px 0;
}

.user-card {
  padding: 10px;
  background-color: rgb(242, 245, 254);
  margin: 10px 0;
}

.flex {
  display: flex;
  align-items: center;
}

.user {
  margin-left: 13px;
}

:global(.bold-label .el-form-item__label) {
  color: #405078;
  font-size: 14px;
  font-weight: bold;
  margin-top: 10px;
}

.bold {
  color: #021C5D;
  font-weight: bold;
  margin: 2px 0;
}

.large {
  color: #405078;
  font-size: 20px;
  font-weight: bold;
}
</style>
