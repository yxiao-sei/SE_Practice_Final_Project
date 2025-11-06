<!--
 * @Author: lanseliuyiya
 * @Date: 2024-01-03 17:37:50
 * @LastEditors: laladila1986@163.com
 * @LastEditTime: 2024-02-01 17:43:26
 * @FilePath: \plms-ms\src\components\importDialog\ImportDialog.vue
 * @Description:
 * @Copyright: Copyright 2024, All Rights Reserved.
-->
<template>
  <common-dialog @close="closeModal" :loading="loading">
    <div class="position_button">
      <el-button @click="downloadTemplate" :loading="downLoading" size="large" type="primary" link
        >下载导入模板
        <el-icon class="el-icon--right">
          <Download />
        </el-icon>
      </el-button>
    </div>
    <el-form
      :rules="rules"
      label-position="top"
      ref="ruleFormEl"
      :model="ruleForm"
      class="dialog-form"
      hide-required-asterisk
    >
      <el-form-item label="" prop="attachments">
        <en-upload
          :uploadText="uploadText + '（Excel 工作表）'"
          :multiple="false"
          :limit="1"
          :auto-upload="false"
          @clearFile="clearFile"
          v-model:file-list="ruleForm.attachments"
          accept="application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        />
      </el-form-item>
    </el-form>
  </common-dialog>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import enUpload from '~/dialogUpload/Index.vue';
import { downLoadExcelTemplate, uploadTableDataByFileApi } from '@/service/importFile';
import { Download } from '@element-plus/icons-vue';
import commonDialog from '../enDialog/Index.vue';
import { useValidateForm } from '@/hooks/useValidateForm';
import { ElMessage } from 'element-plus';
const { templateUrl, uploadText, filename } = defineProps([
  'templateUrl',
  'uploadText',
  'filename',
]);
const emits = defineEmits(['closeModal']);

const { ruleForm, ruleFormEl, validateForm, resetForm } = useValidateForm({
  attachments: [],
});

const rules = reactive<any>({
  attachments: [{ required: true, message: '请选择附件', trigger: 'change', type: 'array' }],
});

const clearFile = () => {
  ruleForm.attachments = [];
};
const downLoading = ref(false);
const downloadTemplate = () => {
  downLoading.value = true;
  downLoadExcelTemplate({ url: templateUrl, filename: filename });
  downLoading.value = false;
};

const loading = ref(false);
const closeModal = (status: string) => {
  if (status === 'confirm') {
    validateForm().then(() => {
      loading.value = true;
      const data = new FormData();
      data.append('file', ruleForm.attachments[0].raw);
      uploadTableDataByFileApi(data, `import/${templateUrl}`)
        .then((message) => {
          ElMessage.success(message);
          emits('closeModal', status);
        })
        .finally(() => {
          loading.value = false;
        });
    });
  } else {
    resetForm();
    emits('closeModal', status);
  }
};
</script>

<style scoped lang="scss">
.content {
  width: 100%;
}

:global(.import-dialog-content .el-form-item__content > div:first-child) {
  width: 100%;
}

.position_button {
  text-align: right;
  position: absolute;
  top: calc(var(--el-dialog-padding-primary));
  right: var(--el-dialog-padding-primary);
}

.el-upload__text {
  text-align: center;
}

.import-dialog-content {
  & > div:first-child {
    display: flex;
    justify-content: center;
    flex-direction: column;
  }
}
</style>
