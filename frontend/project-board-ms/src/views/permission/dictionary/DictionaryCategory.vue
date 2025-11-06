<template>
  <en-dialog @close="closeModal" :loading="loading">
    <el-form :rules="rules" label-position="top" hide-required-asterisk inline-message class="dialog-form"
      ref="ruleFormEl" :model="ruleForm">
      <el-row :gutter="10">
        <el-col :span="12">
          <el-form-item :label="activeLabelName" prop="name">
            <el-input size="large" v-model="ruleForm.name" placeholder="请输入" />
          </el-form-item>
        </el-col>
        <el-divider content-position="left">
          <span style="color: var(--el-color-primary)">扩展信息</span>
        </el-divider>
        <template :gutter="10" v-for="(jsonItem, index) in ruleForm.jsonData" :key="index">
          <el-col :span="12">
            <el-form-item :rules="{
              required: true,
              message: '请输入',
              trigger: 'blur',
            }" label="值类型" :prop="'jsonData.' + index + '.type'">
              <el-select size="large" v-model="jsonItem.type" placeholder="请选择">
                <el-option value="文本">文本</el-option>
                <el-option value="数字">数字</el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :rules="{
              required: true,
              message: '请输入',
              trigger: 'blur',
            }" label="名称" :prop="'jsonData.' + index + '.label'">
              <el-input size="large" v-model.number="jsonItem.label" placeholder="请输入" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :rules="{
              required: true,
              message: '请输入',
              trigger: 'blur',
            }" label="是否必须填" :prop="'jsonData.' + index + '.required'">
              <el-select size="large" v-model="jsonItem.required" placeholder="请选择">
                <el-option value="是">是</el-option>
                <el-option value="否">否</el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-divider></el-divider>
        </template>
        <el-col :span="24" :align="'right'">
          <el-button @click="addJsonData" type="primary">添加扩展</el-button>
        </el-col>
      </el-row>
      <el-form-item label="描述" prop="description">
        <el-input :autosize="{ minRows: 4, maxRows: 4 }" type="textarea" resize="none" v-model="ruleForm.description"
          placeholder="请输入" />
      </el-form-item>
    </el-form>
  </en-dialog>
</template>

<script setup lang="ts">
import { useValidateForm } from '@/hooks/useValidateForm';
import { findMetaDataListApi, createMetaDataApi, updateMetaDataApi } from '@/service/api/meta-data';
import { useCacheUserInfoStore } from '@/stores';
import { computed, ref, watch } from 'vue';
import enDialog from '~/enDialog/Index.vue';

const labelName: any = {};
const activeLabelName = computed(() => {
  return ruleForm.isModifiable && ruleForm.category
    ? labelName[ruleForm.category]
      ? labelName[ruleForm.category]
      : ruleForm.category
    : '名称';
});
const { username, realName } = useCacheUserInfoStore();
const emits = defineEmits(['closeModal']);
const props = defineProps(['fillData', "title"]);
const loading = ref(false);
const { ruleForm, ruleFormEl, validateForm, resetForm } = useValidateForm({
  name: '',
  category: '',
  description: '',
  isModifiable: false,
  code: '',
  jsonData: [],
  id: '',
});
const handleValidate = async (value: string) => {
  const params = {
    filters: {
      name: { $eq: value },
      ...(ruleForm.id ? { id: { $ne: ruleForm.id } } : {}),
      isModifiable: {
        $eq: 0,
      },
    },
  };
  const { result } = await findMetaDataListApi(params);
  return !result || !result.length;
};

const addJsonData = () => {
  ruleForm.jsonData.push({ label: '', required: '是', value: '', type: '文本' })
}

const validateTheUnique = async (rule: any, value: string, callback: any) => {
  if (!value) {
    callback(new Error('请输入'));
  } else {
    const validateResult = await handleValidate(value);
    if (!validateResult) {
      callback(
        new Error(
          `${ruleForm.isModifiable ? ruleForm.category + '名称已存在，请重新输入' : '类别已存在'}`
        )
      );
    } else {
      callback();
    }
  }
};

let rules: any = {
  name: [{ validator: validateTheUnique, trigger: 'blur' }],
  code: [{ required: true, message: '请输入', trigger: 'blur' }],
  category: [{ required: true, message: '请选择', trigger: 'change' }],
  isModifiable: [{ required: true, type: 'boolean', message: '请输入', trigger: 'change' }],
  description: [{ required: false, message: '请输入', trigger: 'blur' }],
};

const cacheJsonContrast: any = {};
const changeCategory = async (val: string) => {
  if (!val) {
    ruleForm.jsonData = [];
    return;
  }
  if (!cacheJsonContrast[val]) {
    const { result } = await findMetaDataListApi({ filters: { category: { $eq: null }, name: { $eq: val } } });
    cacheJsonContrast[val] = result[0]?.jsonData || [];
    const arr = cacheJsonContrast[val];
    ruleForm.jsonData = arr;
  } else {
    ruleForm.jsonData = cacheJsonContrast[val];
  }
};

const parseJsonData = () => {
  if (!ruleForm.jsonData || !ruleForm.jsonData.length) return '';
  const obj: any = {};
  ruleForm.jsonData.forEach((item: any) => {
    obj[item.prop] = item.value;
  });
  return obj;
};

const confirm = () => {
  loading.value = true;
  const { id } = ruleForm;
  const data = {
    name: ruleForm.name,
    category: ruleForm.isModifiable ? ruleForm.category : '',
    description: ruleForm.description,
    isModifiable: ruleForm.isModifiable,
    jsonData: ruleForm.jsonData,
  };
  const savaFn = id
    ? updateMetaDataApi(id, data, { loading: false })
    : createMetaDataApi({
      ...data,
      creatorName: realName,
      creatorNumber: username,
      createdTime: new Date(),
    },
      { loading: false }
    );
  Promise.resolve(savaFn)
    .then(() => {
      loading.value = false;
      emits('closeModal', 'success');
    })
    .finally(() => {
      loading.value = false;
    });
};

const closeModal = (type?: string) => {
  if (type === 'confirm') {
    validateForm().then(() => confirm());
  } else {
    emits('closeModal', type);
    resetForm();
    ruleForm.jsonData = [];
    ruleForm.id = null;
    ruleForm.category = '';
  }
};

watch(
  () => props.fillData,
  (val) => {
    if (val) {
      Object.keys(ruleForm).forEach((key) => {
        ruleForm[key] = val[key];
      });
    } else {
      resetForm();
      ruleForm.category = '';
      ruleForm.jsonData = [];
    }
  }
);
</script>
