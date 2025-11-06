<template>
  <div class="flex-grow overflow-y-auto">
    <user-card :showContract="true" />
    <van-form @submit="onSubmit" class="mt-[10px]">
      <van-cell-group class="bg-white">
        <van-field class="field" is-link readonly @click="openPicker('xb')" v-model="form.xb" type="text" name="性别"
          label="性别" placeholder="请选择性别" :rules="rules.xb" />
        <van-field class="field" v-model="form.telephone" type="tel" name="手机号" label="手机号" placeholder="请输入手机号"
          :rules="rules.telephone" :maxlength="11" :minlength="11" />
        <van-field class="field" v-model="form.email" type="email" name="邮箱" label="邮箱" placeholder="请输入邮箱"
          :rules="rules.email" />
        <van-field class="field" v-model="form.zymc" name="专业" label="专业" placeholder="请输入专业" />
        <van-field class="field" readonly label="学生类别" v-model="form.userType" name="学生类别" is-link
          @click="openPicker('userType')" placeholder="请选择学生类别" />
        <van-field class="field" readonly label="学习形式" v-model="form.xxxs" name="学习形式" is-link
          @click="openPicker('xxxs')" placeholder="请选择学习形式" />
        <van-field class="field" v-model="form.xq" is-link name="校区" label="校区" placeholder="请选择校区" readonly
          @click="openPicker('xq')" />
        <van-field class="field" v-model="form.xwlb" is-link name="学位类别" label="学位类别" placeholder="请选择学位类别" readonly
          @click="openPicker('xwlb')" />
        <van-field class="field" v-model="form.nj" is-link name="年级" label="年级" placeholder="请选择年级" readonly
          @click="openPicker('nj')" />
      </van-cell-group>
    </van-form>
  </div>
  <div class="fixed-button">
    <van-button type="primary" @click="onSubmit" color="#3F599A" block>保存</van-button>
  </div>

  <van-popup round v-model:show="showUserTypePicker" position="bottom">
    <van-picker show-toolbar v-model:modelValue="placeholderForm.userType" :columns="userTypeColumns"
      @confirm="onUserTypeConfirm" @cancel="showUserTypePicker = false" />
  </van-popup>
  <van-popup round v-model:show="showXqPicker" position="bottom">
    <van-picker show-toolbar v-model:modelValue="placeholderForm.xq" :columns="xqColumns" @confirm="onXqConfirm"
      @cancel="showXqPicker = false" />
  </van-popup>
  <van-popup round v-model:show="showXwlbPicker" position="bottom">
    <van-picker show-toolbar v-model:modelValue="placeholderForm.xwlb" :columns="xwlbColumns" @confirm="onXwlbConfirm"
      @cancel="showXwlbPicker = false" />
  </van-popup>
  <van-popup round v-model:show="showNjPicker" position="bottom">
    <van-picker show-toolbar v-model:modelValue="placeholderForm.nj" :columns="njColumns" @confirm="onNjConfirm"
      @cancel="showNjPicker = false" />
  </van-popup>
  <van-popup round v-model:show="showXbPicker" position="bottom">
    <van-picker show-toolbar v-model:modelValue="placeholderForm.xb" :columns="xbColumns" @confirm="onXbConfirm"
      @cancel="showXbPicker = false" />
  </van-popup>
  <van-popup round v-model:show="showXxxsPicker" position="bottom">
    <van-picker show-toolbar v-model:modelValue="placeholderForm.xxxs" :columns="xxxsColumns" @confirm="onXxxsConfirm"
      @cancel="showXxxsPicker = false" />
  </van-popup>
</template>

<script setup lang="ts">
import UserCard from '@/components/UserCard.vue';
import { ref, reactive, onMounted, computed, shallowRef } from 'vue';
import { updateAdministratorApi } from '@/service/administrator';
import { useUserInfo } from '@/stores/cacheInfo';
import { storeToRefs } from 'pinia';
import { showConfirmDialog, showNotify, Picker, Popup } from 'vant';
import { findMetaDataListApi } from '@/service/meta-data'

const { user: principal } = storeToRefs(useUserInfo());
const { setUserInfo } = useUserInfo();
const loading = ref(false)
const templateJSON = ref<string | null>(null)

const rules = {
  telephone: [{ required: true, message: '请填写手机号' }],
  email: [{ required: true, message: '请填写邮箱' }],
  xb: [{ required: true, message: '请选择性别' }],
  userType: [{ required: true, message: '请选择学生类别' }],
  xq: [{ required: true, message: '请选择校区' }],
  xwlb: [{ required: true, message: '请选择学位类别' }],
  nj: [{ required: true, message: '请选择年级' }],
}
const form = reactive<any>({
  telephone: '',
  email: '',
  id: '',
  xb: '',
  zymc: '',
  userType: '',
  xxxs: '',
  xq: '',
  xwlb: '',
  nj: '',
  photo: ''
})
const placeholderForm = reactive<any>({
  xb: [],
  zymc: [],
  userType: [],
  xxxs: [],
  xq: [],
  xwlb: [],
  nj: [],
});
const onSubmit = () => {
  showConfirmDialog({
    title: '提示',
    message: `是否确定保存？`,
    confirmButtonColor: '#3F599A'
  })
    .then(() => {
      loading.value = true
      const { id, ...rest } = form
      updateAdministratorApi(id, rest).then((res) => {
        setUserInfo({ ...principal.value, ...res })
        showNotify({ type: 'success', message: '保存成功' })
      }).finally(() => loading.value = false)
    })
}
const openPicker = (key: keyof typeof placeholderForm) => {
  switch (key) {
    case 'xb':
      showXbPicker.value = true
      placeholderForm.xb = form.xb ? [form.xb] : []
      break;
    case 'userType':
      showUserTypePicker.value = true
      placeholderForm.userType = form.userType ? [form.userType] : []
      break;
    case 'xq':
      showXqPicker.value = true
      placeholderForm.xq = form.xq ? [form.xq] : []
      break;
    case 'xwlb':
      showXwlbPicker.value = true
      placeholderForm.xwlb = form.xwlb ? [form.xwlb] : []
      break;
    case 'nj':
      showNjPicker.value = true
      placeholderForm.nj = form.nj ? [form.nj] : []
      break;
    case 'xxxs':
      placeholderForm.xxxs = form.xxxs ? [form.xxxs] : []
      showXxxsPicker.value = true
      break;
  }
}
const generateNjColumns = () => {
  const columns: any[] = []
  const currentYear = new Date().getFullYear();
  for (let i = 0; i <= 6; i++) {
    const year = currentYear - i;
    columns.push({ text: `${year}级`, value: `${year}` });
  }
  return columns
}

onMounted(() => {
  Object.keys(form).forEach((key) => {
    form[key] = principal.value[key]
  })
  templateJSON.value = JSON.stringify({ ...form })
  njColumns.value = generateNjColumns()
  const campusApi = findMetaDataListApi({
    pagination: { pageSize: 10000 },
    fields: ['id', 'name'],
    filters: {
      isModifiable: {
        $eq: 1,
      },
      category: {
        $eq: '校区',
      },
    }
  })
  const userTypeApi = findMetaDataListApi({
    pagination: { pageSize: 10000 },
    fields: ['id', 'name'],
    filters: {
      isModifiable: {
        $eq: 1,
      },
      category: {
        $eq: '学生类别',
      },
    }
  })
  const xwlbApi = findMetaDataListApi({
    pagination: { pageSize: 10000 },
    fields: ['id', 'name'],
    filters: {
      isModifiable: {
        $eq: 1,
      },
      category: {
        $eq: '学位类别',
      },
    }
  })
  const xxxsApi = findMetaDataListApi({
    pagination: { pageSize: 10000 },
    fields: ['id', 'name'],
    filters: {
      isModifiable: {
        $eq: 1,
      },
      category: {
        $eq: '学习形式',
      },
    }
  })
  Promise.all([campusApi, userTypeApi, xwlbApi, xxxsApi]).then((res: [{ result: any[] }, { result: any[] }, { result: any[] }, { result: any[] }]) => {
    xqColumns.value = res[0].result.map((item: any) => ({ text: item.name, value: item.name }))
    userTypeColumns.value = res[1].result.map((item: any) => ({ text: item.name, value: item.name }))
    xwlbColumns.value = res[2].result.map((item: any) => ({ text: item.name, value: item.name }))
    xxxsColumns.value = res[3].result.map((item: any) => ({ text: item.name, value: item.name }))
  })
})

// 选择器相关数据
const showUserTypePicker = ref(false)
const showXqPicker = ref(false)
const showXwlbPicker = ref(false)
const showNjPicker = ref(false)
const showXbPicker = ref(false)
const showXxxsPicker = ref(false)

type Column = { text: string, value: string }
const userTypeColumns = shallowRef<Column[]>([])
const xqColumns = shallowRef<Column[]>([])
const xwlbColumns = shallowRef<Column[]>([])
const njColumns = shallowRef<Column[]>([])
const xbColumns = shallowRef<Column[]>([
  { text: '男', value: '男' },
  { text: '女', value: '女' },
])
const xxxsColumns = shallowRef<Column[]>([])

const onUserTypeConfirm = () => {
  form.userType = placeholderForm.userType[0]
  showUserTypePicker.value = false
}

const onXqConfirm = () => {
  form.xq = placeholderForm.xq[0]
  showXqPicker.value = false
}

const onXwlbConfirm = () => {
  form.xwlb = placeholderForm.xwlb[0]
  showXwlbPicker.value = false
}

const onNjConfirm = () => {
  form.nj = placeholderForm.nj[0]
  showNjPicker.value = false
}

const onXbConfirm = () => {
  form.xb = placeholderForm.xb[0]
  showXbPicker.value = false
}
const onXxxsConfirm = () => {
  form.xxxs = placeholderForm.xxxs[0]
  showXxxsPicker.value = false
}
</script>

<style scoped>
:global(.field .van-cell__title) {
  @apply text-[#9FACCF] font-bold
}

.fixed-button {
  padding-bottom: calc(12px + constant(safe-area-inset-bottom));
  padding-bottom: calc(12px + env(safe-area-inset-bottom));
  padding-top: 12px;
  @apply px-[15px];
}
</style>
