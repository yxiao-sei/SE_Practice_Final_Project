<template>
  <en-dialog @close="closeModal" :loading="loading" destroy-on-close>
    <el-form label-position="top" :inline-message="true" :rules="rules" class="dialog-form" ref="ruleFormEl"
      :model="ruleForm">
      <el-row :gutter="10">
        <el-col :span="12">
          <el-form-item prop="name" label="姓名">
            <el-input size="large" v-model="ruleForm.name" placeholder="请输入" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="number" label="工号">
            <el-input size="large" v-model="ruleForm.number" placeholder="请输入" />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item prop="roles" label="系统角色">
            <el-select size="large" class="multiple-select" tag-type="" filterable clearable v-model="ruleForm.roles"
              placeholder="请选择" multiple style="width: 100%">
              <el-option v-for="item in roleList" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="登录方式" prop="authenticationMethods">
            <el-select size="large" class="multiple-select" tag-type="" collapse-tags collapse-tags-tooltip
              v-model="ruleForm.authenticationMethods" style="width: 100%">
              <el-option :value="method.value" v-for="method in authenticationMethods" :key="method.value"
                :label="method.label"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item prop="password" :rules="[
            {
              required: fillData && fillData.id && fillData.password ? false : true,
              message: '请输入',
              trigger: 'blur',
            },
            {
              validator: validatePassword,
              trigger: 'blur',
            },
          ]" v-if="needPassword">
            <template #label>
              <span>密码
                <el-tooltip effect="dark" content="长度至少8个字符，包含大写字母、小写字母、数字和特殊字符" placement="top">
                  <el-icon>
                    <QuestionFilled />
                  </el-icon>
                </el-tooltip>
              </span>
            </template>
            <el-input size="large" show-password v-model="ruleForm.password" clearable placeholder="请输入" />
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="hasDepartment">
          <el-form-item label="选择院系" size="large" prop="departmentName">
            <el-cascader v-if="!ruleForm.id" @change="handleChange" class="cascader-info" tag-type="info"
              style="width: 100%" multiple show-checkbox filterable clearable v-model="ruleForm.departmentName"
              :props="cascaderProps" size="large" :options="departmentTreeList" />
            <el-cascader v-else filterable clearable class="cascader-info" tag-type="info" style="width: 100%"
              v-model="ruleForm.departmentName" size="large" :options="departmentTreeList" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </en-dialog>
</template>

<script setup lang="ts">
import enDialog from '~/enDialog/Index.vue'
import { useBaseFieldStore } from '@/stores'
import { ref, reactive, toRefs, computed, watch } from 'vue'
import { validatePassword } from '@/hooks/useValidateForm'
import { QuestionFilled } from '@element-plus/icons-vue'
import { storeToRefs } from 'pinia'
import { md5 } from 'js-md5';
import { addAdministratorsBatchApi } from '@/service/api/user';
import { updateAdministratorApi} from "@/service/api/administrator";

const cascaderProps = {
  multiple: true,
}
const needPassword = computed(() => {
  return ruleForm.authenticationMethods.includes('USERNAME_PASSWORD');
})
const props = defineProps(['fillData', 'role'])
const { fillData, role } = toRefs(props)
const { authenticationMethods } = useBaseFieldStore();
const { departmentTreeList, subDepartmentList, roleDepartmentList, roleTeacherList, roleStudentList }: any = storeToRefs(useBaseFieldStore());
const roleList = computed(() => {
  return role?.value === 'system' ? roleDepartmentList.value : role?.value === 'student' ? roleStudentList.value : role?.value === 'teacher' ? roleTeacherList.value : [];
})
const hasDepartment = computed(() => {
  return true
  // return role?.value === 'roleDepartment'
})
const emits = defineEmits(['closeModal'])
const loading = ref(false)
const ruleFormEl = ref()
const ruleForm = reactive({
  name: '',
  number: '',
  roles: [],
  departmentName: [],
  authenticationMethods: '',
  password: '',
  id: null
})
const rules: any = {
  name: [{ required: true, message: '请输入', trigger: 'blur' }],
  number: [{ required: true, message: '请输入', trigger: 'blur' }],
  roles: [{ required: true, message: '请选择', type: 'array', trigger: 'change' }],
  departmentName: [{ required: true, message: '请选择', trigger: 'change' }],
  isOnDuty: [{ required: false, message: '请选择', trigger: 'change' }],
  isLogin: [{ required: false, message: '请选择', trigger: 'change' }],
  authenticationMethods: [{ required: true, message: '请选择', trigger: 'change' }],
  mobilePhone: [{ required: false, message: '请输入', trigger: 'blur' }],
  workDuty: [{ required: false, message: '请输入', trigger: 'blur' }],
  officeLocation: [{ required: false, message: '请输入', trigger: 'blur' }],
  email: [{ required: false, message: '请输入', trigger: 'blur' }],
  remark: [{ required: false, message: '请输入', trigger: 'blur' }],
  officePhone: [{ required: false, message: '请输入', trigger: 'blur' }],
  id: [{ required: false, message: '请输入', trigger: 'blur' }]
}

const resetForm = () => {
  ruleFormEl.value?.resetFields()
}
const confirm = () => {
  loading.value = true;
  const { roles, password, id, departmentName, ...reset } = ruleForm;
  const formatPassword = needPassword.value
    ? password
      ? md5(password)
      : fillData?.value.password
    : null;
  const formatRoles = roles.join(',');
  if (!id) {
    const dataArr = departmentName.map((item: Array<string>) => {
      const department = findDepart(item[1]);
      return {
        roles: formatRoles,
        password: formatPassword,
        ...reset,
        ...department,
      };
    });
    addAdministratorsBatchApi({ administrators: dataArr })
      .then(() => {
        loading.value = false;
        closeModal('refresh');
      })
      .finally(() => (loading.value = false));
  } else {
    let department = {};
    if (departmentName && departmentName.length > 0) {
      department = findDepart(departmentName[departmentName.length - 1]);
    }
    updateAdministratorApi(id, {
      roles: formatRoles,
      password: formatPassword,
      ...reset,
      ...department
    })
      .then(() => {
        closeModal('refresh');
      })
      .finally(() => {
        loading.value = false;
      });
  }

};
const findDepart = (name: any) => {
  if (name) {
    const target = subDepartmentList.value.find((item: any) => item.departmentName == name);
    return target
      ? {
        performanceDepartmentName: target.performanceDepartmentName,
        buinCode: target.buinCode,
        departmentName: target.departmentName,
        departmentCode: target.departmentCode,
      }
      : {};
  }
  return {};
};
const handleChange = (value: any) => {
  if (value.length > 1) {
    const departmentName = value[value.length - 1][0];
    ruleForm.departmentName = value.filter((item: any) => item[0] == departmentName) as any;
  }
};
const closeModal = (status = '') => {
  if (status === 'confirm') {
    ruleFormEl.value?.validate((valid: any) => {
      if (valid) {
        confirm()
      }
    });
  } else {
    emits('closeModal', status);
    resetForm()
  }
}

watch(() => props.fillData,
  (value) => {
    if (value) {
      Object.keys(ruleForm).map((key) => {
        ruleForm.id = value.id
        ruleForm.number = value.number
        ruleForm.name = value.name
        ruleForm.departmentName = value.departmentName
        ruleForm.authenticationMethods = value.authenticationMethods
        ruleForm.roles = value.roles.split(',')
      })
    } else {
      resetForm();
    }
  }
);
</script>

<style scoped></style>