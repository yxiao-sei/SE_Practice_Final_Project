<template>
  <common-dialog id="loadingEl" @close="closeModal" width="min(45%,644px)" :loading="loading" close-on-click-modal
    close-on-press-escape show-close>
    <p class="title">{{ fillData?.projectRescuitRole }}</p>
    <p class="subtitle">应聘人员信息</p>
    <div class="user-card">
      <div class="flex">
        <el-avatar src="https://cube.daselemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
        <div class="user">
          <p>{{ fillData?.studentName }}</p>
          <p>{{ fillData?.studentNumber }}</p>
        </div>
      </div>
      <div class="mt-20">
        <el-row :gutter="10">
          <el-col :span="4">手机</el-col>
          <el-col :span="20">{{ fillData?.studentTelephone }}</el-col>
          <el-col :span="4">邮箱</el-col>
          <el-col :span="20">{{ fillData?.studentEmail }}</el-col>
        </el-row>
      </div>
    </div>
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
        <el-col :span="5" v-if="fillData.state === '待审核'">
          <el-button type="primary" @click="submit('已通过，待面试')" color="#04A80F">通过</el-button>
        </el-col>
        <el-col :span="5" v-else-if="fillData.state === '已通过，待面试'">
          <el-button type="primary" @click="submit('面试通过，已录用')" color="#04A80F">确认录用</el-button>
        </el-col>
      </el-row>
    </template>
  </common-dialog>
</template>

<script setup lang="ts">
import { reactive, ref, toRef, computed } from 'vue';
import commonDialog from '~/enDialog/Index.vue';
import { useValidateForm } from '@/hooks/useValidateForm';
import { useRoute } from 'vue-router';
import { ElLoading } from 'element-plus'
import { updateApplicationApi } from "@/service/api/application";

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
    updateApplicationApi(fillData.value.id, {
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
}

.mt-20 {
  margin-top: 20px;

  .el-col:nth-child(odd) {
    color: #9FACCF;
    font-weight: bold;
    margin-top: 5px;
  }

  .el-col:nth-child(even) {
    color: #405078;
  }
}
</style>
