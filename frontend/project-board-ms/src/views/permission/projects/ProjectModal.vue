<template>
<common-dialog @close="closeModal" :loading="loading">
  <el-form :rules="rules" label-position="top" ref="ruleFormEl" :model="ruleForm" class="dialog-form">
    <el-row :gutter="10">
      <el-col :span='12'>
        <el-form-item label="课题名称" prop="name">
          <el-input size="large" v-model="ruleForm.name" placeholder="请输入" />
        </el-form-item>
      </el-col>
      <el-col :span='12'>
        <el-form-item label="课题编号" prop="code">
          <el-input size="large" v-model="ruleForm.code" placeholder="请输入" />
        </el-form-item>
      </el-col>
      <el-col :span='12'>
        <el-form-item label="课题负责老师姓名" prop="leadingTeacherName">
          <el-input size="large" v-model="ruleForm.leadingTeacherName" placeholder="请输入" />
        </el-form-item>
      </el-col>
      <el-col :span='12'>
        <el-form-item label="课题负责老师工号" prop="leadingTeacherNumber">
          <el-input size="large" v-model="ruleForm.leadingTeacherNumber" placeholder="请输入" />
        </el-form-item>
      </el-col>
      <el-col :span='12'>
        <el-form-item label="课题开始日期" prop="beginDate">
          <el-date-picker size="large" style="width: 100%;" v-model="ruleForm.beginDate" format="YYYY/MM/DD" type="date"
            placeholder="请选择" clearable />
        </el-form-item>
      </el-col>
      <el-col :span='12'>
        <el-form-item label="课题结束日期" prop="endDate">
          <el-date-picker size="large" style="width: 100%;" v-model="ruleForm.endDate" type="date" format="YYYY/MM/DD"
            placeholder="请选择" clearable />
        </el-form-item>
      </el-col>
      <el-col :span='12'>
        <el-form-item label="研究方向" prop="researchArea">
          <el-select size="large" v-model="ruleForm.researchArea" filterable allow-create clearable>
            <el-option v-for="{ label, value, id } in metadataDirection" :label="label" :value="value"
              :key="id"></el-option>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span='12'>
        <el-form-item label="预期成果" prop="researchOutcome">
          <el-input size="large" v-model="ruleForm.researchOutcome" placeholder="请输入" />
        </el-form-item>
      </el-col>
      <el-col :span='12'>
        <el-form-item label="课题状态" prop="researchStatus">
          <el-select size="large" v-model="ruleForm.researchStatus" clearable>
            <el-option v-for="{ label, value, id } in projectStatus" :label="label" :value="value"
              :key="id"></el-option>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span='12'>
        <el-form-item label="课题成员招募状态" prop="recruitStatus">
          <el-select size="large" v-model="ruleForm.recruitStatus" clearable>
            <el-option v-for="{ label, value, id } in memberRecruitmentStatus" :label="label" :value="value"
              :key="id"></el-option>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span='12'>
        <el-form-item label="课题标签" prop="labels">
          <el-select size="large" v-model="ruleForm.labels" filterable allow-create placeholder="请选择" multiple
            clearable>
            <el-option v-for="{ id, label, value } in projectLabels" :key="id" :label="label" :value="value" />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span='12'>
        <el-form-item label="课题所属院系名称" prop="departmentName">
          <el-select size="large" v-model="ruleForm.departmentName" clearable>
            <el-option v-for="{ label, value, id } in projectOfDepartments" :label="label" :value="value"
              :key="id"></el-option>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span='24'>
        <el-form-item label="附件" prop="attachments">
          <en-upload uploadText="上传附件（Word 文档或PDF 文件）" :multiple="true"
            accept=".doc,.docx,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document,.pdf,application/pdf"
            v-model:file-list="ruleForm.attachments" tag="课题附件" @clearFile="clearFile" />
        </el-form-item>
      </el-col>
      <el-col :span='24'>
        <el-form-item label="课题介绍" prop="description">
          <el-input type="textarea" resize="none" :rows="4" v-model="ruleForm.description" placeholder="请输入" />
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</common-dialog>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue';
import commonDialog from '~/enDialog/Index.vue';
import { useBaseFieldStore, useCacheUserInfoStore } from "@/stores";
import { useValidateForm } from '@/hooks/useValidateForm';
import { storeToRefs } from 'pinia';
import { createMetaDataApi } from '@/service/api/meta-data';
import { fetchBaseFieldEvent } from '@/events';
import enUpload from '@/components/dialogUpload/Index.vue';
import { updateProjectApi, createProjectApi } from "@/service/api/project";
import { uploadFileApi } from '@/service/api/file';

const props = defineProps(['fillData'])
const { username, realName } = useCacheUserInfoStore()
const { projectLabels, subDepartmentList, metadataDirection, projectOfDepartments, memberRecruitmentStatus, projectStatus } = storeToRefs(useBaseFieldStore());
const emits = defineEmits(['closeModal']);
const { ruleForm, ruleFormEl, validateForm, resetForm } = useValidateForm({
  name: "",
  code: '',
  description: '',
  researchStatus: '',
  leadingTeacherName: '',
  leadingTeacherNumber: '',
  departmentName: null,
  departmentCode: '',
  beginDate: '',
  endDate: '',
  researchArea: '',
  researchOutcome: '',
  labels: [],
  recruitStatus: '',
  attachments: [],
  id: '',
});
const clearFile = () => {
  ruleForm.attachments = [];
};

const rules = reactive<any>({
  name: [
    { required: true, message: '请输入名称', trigger: 'blur' }
  ],
  attachments: [{ required: false, message: '请上传附件', trigger: 'change' }],
  code: [
    { required: true, message: '请输入编号', trigger: 'blur' }
  ],
  departmentName: [
    { required: true, message: '请选择院系', trigger: 'change' }
  ],
  departmentCode: [
    { required: false, message: '请选择院系', trigger: 'change' }
  ],
  beginDate: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endDate: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ],
  description: [
    { required: false, message: '请输入介绍', trigger: 'blur' }
  ],
  researchStatus: [
    { required: false, message: '请输入课题状态', trigger: 'blur' }
  ],
  leadingTeacherName: [
    { required: false, message: '请输入课题负责老师姓名', trigger: 'blur' }
  ],
  leadingTeacherNumber: [
    { required: true, message: '请输入课题负责老师工号', trigger: 'blur' }
  ],
  researchArea: [
    { required: false, message: '请选择研究方向', trigger: 'change' }
  ],
  researchOutcome: [
    { required: false, message: '请输入预期成果', trigger: 'blur' }
  ],
  labels: [
    { required: false, type: 'array', message: '请选择课题标签', trigger: 'change' }
  ],
  recruitStatus: [
    { required: false, message: '请输入课题成员招募状态', trigger: 'blur' }
  ],
  id: [
    { required: false, message: '请输入', trigger: 'blur' }
  ]
});
const findDepart = (name: any) => {
  if (name) {
    const target = subDepartmentList.value.find((item: any) => item.departmentName == name);
    if (target) {
      return {
        departmentName: target.departmentName,
        departmentCode: target.departmentCode
      }
    }
  }
  return {};
};
const loading = ref(false);
const save = () => {
  const { departmentName, labels, researchArea, id, ...rest } = ruleForm;
  const targetName = Array.isArray(departmentName) ? departmentName[1] : departmentName;
  const departInfo = findDepart(targetName);
  const params = {
    ...rest,
    labels: labels.join('，'),
    ...departInfo,
    researchArea
  };

  const api = id ? updateProjectApi(id, params) : createProjectApi(params);
  Promise.resolve(api)
    .then(() => {
      closeModal('refresh');
      const notExistLabels = labels.filter((label: any) => !projectLabels.value.find((item: any) => item.value === label));
      if (notExistLabels.length > 0) {
        Promise.all(notExistLabels.map((label: any) => createMetaDataApi({
          name: label,
          isModifiable: true,
          category: '课题标签',
          creatorName: realName,
          creatorNumber: username
        }))).then(() => {
          fetchBaseFieldEvent('projectLabels', true);
        })
      }
      const notExistResearchArea = metadataDirection.value.find((item: any) => researchArea === item.value);
      if (!notExistResearchArea) {
        createMetaDataApi({
          name: researchArea,
          isModifiable: true,
          category: '研究方向',
          creatorName: realName,
          creatorNumber: username
        }).then(() => {
          fetchBaseFieldEvent('metadataDirection', true);
        })
      }
    }).finally(() => {
      loading.value = false;
    })
}
const submit = () => {
  validateForm().then(() => {
    loading.value = true;
    const hasNewFile = ruleForm.attachments.find((item: any) => !!item.file)
    if (hasNewFile) {
      uploadFileApi(ruleForm.attachments).then((res) => {
        ruleForm.attachments = res;
        save()
      }).catch(() => {
        loading.value = false;
      })
    } else {
      save()
    }
  });
}

watch(() => props.fillData, (fillValue) => {
  if (fillValue) {
    Object.keys(ruleForm).map((key) => {
      if (key === 'labels') {
        ruleForm.labels = fillValue.labels ? fillValue.labels?.split('，') : []
      } else if (key === 'attachments') {
        ruleForm.attachments = fillValue.attachments || []
      } else {
        ruleForm[key] = fillValue[key]
      }
    })
  }
})

const closeModal = (status: string) => {
  if (status === 'confirm') {
    submit();
  } else {
    emits('closeModal', status);
    resetForm();
  }
};
</script>

<style scoped lang="scss"></style>
