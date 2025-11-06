<template>
  <common-dialog @close="closeModal" :loading="loading">
    <el-form :rules="rules" label-position="top" ref="ruleFormEl" :model="ruleForm" class="dialog-form">
      <el-row :gutter="10">
        <el-col :span='12'>
          <el-form-item label="姓名" prop="studentNumber">
            <el-select size="large" v-model="ruleForm.studentNumber" filterable>
              <el-option v-for="{ id, name, number } in students" :key="id" :label="name" :value="number" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span='12'>
          <el-form-item label="应聘课题" prop="projectCode">
            <el-select :disabled="!!projectCode" @change="getRequirementList" size="large"
              v-model="ruleForm.projectCode" filterable>
              <el-option v-for="{ id, code, name } in projects" :key="id" :label="name" :value="code" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="应聘课题分工" prop="projectRescuitRole">
            <el-select size="large" v-model="ruleForm.projectRescuitRole" clearable filterable>
              <el-option v-for="{ id, role } in requirementList" :key="id" :label="role" :value="role" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span='24'>
          <el-form-item label="自我推荐" prop="selfRecommendContent">
            <el-input type="textarea" resize="none" :rows="4" v-model="ruleForm.selfRecommendContent"
              placeholder="请输入" />
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
import { findProjectsApi } from '@/service/api/project'
import { findAdministratorListApi } from "@/service/api/administrator";
import { useBaseFieldStore } from "@/stores";
import { useRoute } from 'vue-router';
import { findRequirementListApi } from "@/service/api/requirement";
import { createApplicationApi, updateApplicationApi } from "@/service/api/application";

const route = useRoute();
const projectCode = computed(() => {
  return route.query.project_code;
})
const { dispatchRoleList } = useBaseFieldStore();
const props = defineProps(['fillData', 'visible'])
const emits = defineEmits(['closeModal']);
const { ruleForm, ruleFormEl, validateForm, resetForm } = useValidateForm({
  studentNumber: "",
  projectCode: projectCode.value ?? '',
  selfRecommendContent: '',
  projectRescuitRole: '',
  id: '',
});

const rules = reactive<any>({
  studentNumber: [
    { required: true, message: '请输入', trigger: 'blur' }
  ],
  projectRescuitRole: [
    { required: true, message: '请选择', trigger: 'change' }
  ],
  studentName: [
    { required: true, message: '请输入', trigger: 'blur' }
  ],
  projectCode: [
    { required: true, message: '请选择', trigger: 'change' }
  ],
  selfRecommendContent: [
    { required: false, message: '请输入', trigger: 'blur' }
  ],
  id: [
    { required: false, message: '请输入', trigger: 'blur' }
  ]
});
const loading = ref(false);
const projects = shallowRef<any>([]);
const students = shallowRef<any>([]);
const requirementList = shallowRef<any>([])
const getRequirementList = () => {
  if (!ruleForm.projectCode) return ruleForm.projectRescuitRole = ''
  findRequirementListApi({
    pagination: {
      page: 1,
      pageSize: 2000000
    },
    filters: {
      projectCode: {
        $eq: ruleForm.projectCode
      }
    }
  }).then(({ result }) => {
    if(!result.find(item => item.role === ruleForm.projectRescuitRole)) ruleForm.projectRescuitRole = ''
    requirementList.value = result
  })
}
const getProjectList = () => {
  findProjectsApi({
    pagination: {
      page: 1,
      pageSize: 2000000
    },
    filters: {
      recruitStatus: {
        $eq: '成员招募中'
      }
    }
  }).then(({ result }) => {
    projects.value = result
  })
}

const getStudentList = async () => {
  let filters = {}
  const { roleStudentList }: any = await dispatchRoleList()
  filters = {
    "filters": {
      "$or": roleStudentList.map((item: any) => {
        return {
          "roles": {
            "$contains": item.value
          }
        }
      })
    },
    pagination: {
      page: 1,
      pageSize: 2000000
    }
  }
  findAdministratorListApi(filters).then(({ result }) => {
    students.value = result
  })
}
const submit = () => {
  validateForm().then(() => {
    loading.value = true;
    const { projectCode, studentNumber, id, ...rest } = ruleForm;
    const student = students.value.find((item: any) => item.number === studentNumber);
    const project = projects.value.find((item: any) => item.code === projectCode);
    const params = {
      ...rest,
      studentNumber,
      studentEmail: student.email,
      studentTelephone: student.studentTelephone,
      studentName: student?.name,
      projectCode,
      projectName: project?.name,
      leadingTeacherNumber: project?.leadingTeacherNumber,
      leadingTeacherName: project?.leadingTeacherName,
    };
    const api = id ? updateApplicationApi(id, params) : createApplicationApi({ ...params, state: '待审核' });
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
    getStudentList();
    getRequirementList();
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
