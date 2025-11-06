<template>
<common-dialog @close="closeModal" :loading="loading">
  <el-form :rules="rules" label-position="top" ref="ruleFormEl" :model="ruleForm" class="dialog-form">
    <el-row :gutter="10">
      <el-col :span='12'>
        <el-form-item label="学生年级" prop="grade">
          <el-input size="large" v-model="ruleForm.grade" placeholder="请输入" />
        </el-form-item>
      </el-col>
      <el-col :span='12'>
        <el-form-item label="所需技能" prop="skill">
          <el-input size="large" v-model="ruleForm.skill" placeholder="请输入" />
        </el-form-item>
      </el-col>
      <el-col :span='12'>
        <el-form-item label="需掌握的工具" prop="tools">
          <el-input size="large" v-model="ruleForm.tools" placeholder="请输入" />
        </el-form-item>
      </el-col>
      <el-col :span='12'>
        <el-form-item label="学生专业" prop="major">
          <el-input size="large" v-model="ruleForm.major" placeholder="请输入" />
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item label="选择院系" size="large" prop="departmentName">
          <el-cascader filterable clearable class="cascader-info" tag-type="info" style="width: 100%"
            v-model="ruleForm.departmentName" size="large" :options="departmentTreeList" />
        </el-form-item>
      </el-col>
      <el-col :span='12'>
        <el-form-item label="课题分工" prop="role">
          <el-input size="large" v-model="ruleForm.role" placeholder="请输入" />
        </el-form-item>
      </el-col>
      <el-col :span='12'>
        <el-form-item label="招聘数量" prop="count">
          <el-input size="large" v-model.number="ruleForm.count" @input="validateCount" :min="1" placeholder="请输入" />
        </el-form-item>
      </el-col>
      <el-col :span='12'>
        <el-form-item label="状态" prop="state">
          <el-select size="large" v-model="ruleForm.state" filterable clearable>
            <el-option v-for="{ label, value, id } in postStatus" :label="label" :value="value" :key="id"></el-option>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span='12'>
        <el-form-item label="所需实习/研究经历" prop="experience">
          <el-input size="large" v-model="ruleForm.experience" placeholder="请输入" />
        </el-form-item>
      </el-col>
      <el-col :span='24'>
        <el-form-item label="其他需求" prop="comment">
          <el-input type="textarea" resize="none" :rows="4" v-model="ruleForm.comment" placeholder="请输入" />
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</common-dialog>
</template>

<script setup lang="ts">
import { reactive, ref, watch, shallowRef, computed } from 'vue';
import commonDialog from '~/enDialog/Index.vue';
import { useValidateForm } from '@/hooks/useValidateForm';
import { findProjectsApi } from '@/service/api/project';
import { useBaseFieldStore } from "@/stores";
import { storeToRefs } from 'pinia';
import { useRoute } from 'vue-router';
import { createRequirementApi, updateRequirementApi } from "@/service/api/requirement";

const route = useRoute();
const project_code = computed(() => {
  return route.query.project_code;
})
const { departmentTreeList, postStatus }: any = storeToRefs(useBaseFieldStore());

const props = defineProps(['fillData', 'visible'])
const emits = defineEmits(['closeModal']);
const { ruleForm, ruleFormEl, validateForm, resetForm } = useValidateForm({
  grade: "",
  projectCode: project_code.value ?? '',
  tools: '',
  skill: '',
  major: '',
  role: '',
  comment: '',
  experience: '',
  count: '',
  state: '',
  departmentName: [],
  id: '',
});
const validateCount = (value: string) => {
  const parsedValue = parseInt(value, 10);
  if (isNaN(parsedValue) || parsedValue <= 0) {
    ruleForm.count = 1; // 如果输入无效，重置为1
  } else {
    ruleForm.count = parsedValue;
  }
};
const rules = reactive<any>({
  grade: [
    { required: true, message: '请输入', trigger: 'blur' }
  ],
  count: [
    { required: true, message: '请输入', trigger: 'blur' }
  ],
  state: [
    { required: true, message: '请输入', trigger: 'blur' }
  ],
  tools: [
    { required: true, message: '请输入', trigger: 'blur' }
  ],
  skill: [
    { required: true, message: '请输入', trigger: 'blur' }
  ],
  departmentName: [{ required: true, message: '请选择', trigger: 'change' }],
  major: [
    { required: true, message: '请输入', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请输入', trigger: 'blur' }
  ],
  comment: [
    { required: false, message: '请输入', trigger: 'blur' }
  ],
  experience: [
    { required: false, message: '请输入', trigger: 'blur' }
  ],
  id: [
    { required: false, message: '请输入', trigger: 'blur' }
  ]
});
const loading = ref(false);
const projects = shallowRef<any>([]);
const getProjectList = () => {
  findProjectsApi({
    pagination: {
      page: 1,
      pageSize: 2000000
    },
    filters: {
      code: {
        $eq: project_code.value
      }
    }
  }).then(({ result }) => {
    projects.value = result
  })
}
const submit = () => {
  validateForm().then(() => {
    loading.value = true;
    const { projectCode, studentNumber, count, departmentName, id, ...rest } = ruleForm;
    const project = projects.value.find((item: any) => item.code === projectCode);
    const params = {
      ...rest,
      projectCode,
      projectName: project?.name,
      count: Number(count),
      departmentName: Array.isArray(departmentName) ? departmentName[1] : departmentName,
    };
    const api = id ? updateRequirementApi(id, params) : createRequirementApi(params);
    Promise.resolve(api)
      .then(() => {
        closeModal('refresh');
      }).finally(() => {
        loading.value = false;
      })
  });
}

watch(() => props.fillData, (fillValue) => {
  if (fillValue) {
    Object.keys(ruleForm).map((key) => {
      ruleForm[key] = fillValue[key]
    })
  }
})

watch(() => props.visible, (visible) => {
  if (visible) {
    getProjectList();
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
