<template>
  <van-form class="h-full flex flex-col" ref="formRef" validate-trigger="onSubmit" @submit="onSubmit"
    input-align="right" error-message-align="right" scroll-to-error-position="center" show-error>
    <div class="flex-grow">
      <div class="px-[16px] h-[42px] text-[#3D4348] text-sm flex items-center">
        预约信息
      </div>
      <van-cell-group class="cell" v-if="!loading">
        <template v-if="allowEdit">
          <van-field v-model="availableData.appointmentDate" title="面试日期" is-link readonly name="date" label="面试日期"
            placeholder="点击选择日期" @click="openCalendar" :rules="rules.appointmentDate" />
          <van-field v-model="availableData.appointmentTime" :rules="rules.appointmentTime" name="time" label="面试时间"
            placeholder="选择时间" is-link readonly @click="openTimePicker" />
          <van-field v-model="availableData.campus" :rules="rules.campus" name="campus" label="面试校区" placeholder="请选择校区"
            is-link readonly @click="openPicker" />
          <van-field v-model="availableData.address" :rules="rules.address" name="address" label="详细地址"
            placeholder="请输入详细地址" />
          <van-field v-model="availableData.comment" :rules="rules.comment" name="comment" label="备注"
            placeholder="请输入备注" autosize type="textarea" />
        </template>
        <template v-else>
          <van-cell title="面试日期" :value="availableData.appointmentDate"></van-cell>
          <van-cell title="面试时间" :value="availableData.appointmentTime"></van-cell>
          <van-cell title="面试校区" :value="availableData.campus"></van-cell>
          <van-cell title="详细地址" :value="availableData.address"></van-cell>
          <van-cell title="备注" :value="availableData.comment"></van-cell>
        </template>
      </van-cell-group>
      <div class="px-[16px] h-[42px] text-[#3D4348] text-sm flex items-center">
        报名信息
      </div>
      <div class="flex px-[16px] bg-white flex-col space-y-[6px] pt-[10px] pb-[20px]">
        <van-row :gutter="10" class="font-sm text-primary-200">
          <van-col :align="'left'" :span="5" class="text-primary-200 font-bold">招聘人数</van-col>
          <van-col :align="'left'" :span="19" class="text-sm text-primary-300">{{ requirement?.count }}</van-col>
          <van-col :align="'left'" :span="8" class="font-bold text-[#9FACCF]">所属课题编号</van-col>
          <van-col :align="'left'" :span="16" class="text-primary-200">{{ requirement?.projectCode }}</van-col>
          <van-col :align="'left'" :span="8" class="font-bold text-[#9FACCF]">所属课题名称</van-col>
          <van-col :align="'left'" :span="16" class="text-primary-200">{{ requirement?.projectName }}</van-col>
          <van-col :align="'left'" :span="8" class="font-bold text-[#9FACCF]">所需技能</van-col>
          <van-col :align="'left'" :span="16" class="text-primary-200">{{ requirement?.skill }}</van-col>
          <van-col :align="'left'" :span="8" class="font-bold text-[#9FACCF]">需掌握的工具</van-col>
          <van-col :align="'left'" :span="16" class="text-primary-200">{{ requirement?.tools }}</van-col>
          <van-col :align="'left'" :span="8" class="font-bold text-[#9FACCF]">课题分工</van-col>
          <van-col :align="'left'" :span="16" class="text-primary-200">{{ requirement?.role }}</van-col>
          <van-col :align="'left'" :span="8" class="font-bold text-[#9FACCF]">所需实习/研究经历</van-col>
          <van-col :align="'left'" :span="16" class="text-primary-200">{{ requirement?.experience }}</van-col>
        </van-row>
        <div class="text-blue-200 flex-grow !leading-tight">
          <van-text-ellipsis class="!leading-tight" rows="3" :content="requirement?.comment" expand-text="展开"
            collapse-text="收起" />
        </div>
      </div>
    </div>
    <template v-if="!loading">
      <div class="fixed-button space-y-[10px]" v-if="!availableData.state">
        <van-button type="primary" @click="onSubmit('待审核')" color="#3F599A" block>提交预约</van-button>
        <van-button type="primary" @click="onSubmit('草稿', 'draft')" color="#85A3EF" block>保存草稿</van-button>
      </div>
      <div class="fixed-button space-y-[10px]" v-else-if="availableData.state === '草稿'">
        <van-button type="primary" @click="onSubmit('待审核')" color="#3F599A" block>提交预约</van-button>
        <van-button type="primary" @click="cancel" color="#85A3EF" block>删除预约</van-button>
      </div>
      <div class="fixed-single-button"
        v-else-if="availableData.state == '待审核' || availableData.state == '通过' || availableData.state == '拒绝'">
        <van-button type="primary" @click="onSubmit('草稿', 'retract')" color="#85A3EF" block>撤回</van-button>
      </div>
    </template>
  </van-form>
  <van-popup v-model:show="showTimePicker" round position="bottom">
    <van-picker-group v-model:active-tab="activeTimeTab" title="预约时间" :tabs="['开始时间', '结束时间']" @confirm="onTimeConfirm"
      @cancel="showTimePicker = false">
      <van-time-picker :min-hour="minStartTime.hour" :min-minute="minStartTime.minute"
        v-model="placeholderForm.startTime" :filter="filterStartTime" @change="startTimeChange" />
      <van-time-picker :min-hour="minEndTime.hour" :min-minute="minEndTime.minute" v-model="placeholderForm.endTime"
        :filter="endTimeFilter" />
    </van-picker-group>
  </van-popup>
  <van-popup v-model:show="showPicker" round position="bottom">
    <van-picker v-model="placeholderForm.campus" title="选择校区" @cancel="showPicker = false" :columns="campusOptions"
      @confirm="onCampusConfirm" />
  </van-popup>
  <van-calendar color="#3F599A" :formatter="formatterCalendar" :default-date="placeholderForm.date" :min-date="minDate"
    v-model:show="showCalendar" @confirm="onConfirm" />
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, shallowRef, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { findMetaDataListApi } from '@/service/meta-data'
import { findRequirementByIdApi } from '@/service/requirements'
import { findProjectsApi } from '@/service/project'
import { findAppointmentsApi, findAppointmentByIdApi, deleteAppointmentApi, createAppointmentApi, updateAppointmentApi } from '@/service/appointment'
import { useUserInfo } from '@/stores/cacheInfo'
import { showNotify, showConfirmDialog } from 'vant'

const { user: principal } = useUserInfo();
const formRef = ref()
const rules: any = {
  appointmentDate: [{ required: true, message: '请选择日期' }],
  appointmentTime: [{ required: true, message: '请选择时间' }],
  campus: [{ required: true, message: '请选择校区' }],
  address: [{ required: true, message: '请输入详细地址' }],
  comment: [{ required: false, message: '请输入备注' }],
}
const minDate = new Date()
const route = useRoute()
const activeId = route.query?.id
const router = useRouter()
const showCalendar = ref(false)
const showTimePicker = ref(false)
const activeTimeTab = ref(0)
const showPicker = ref(false)
const requirement = ref<any>({})
const project = ref<any>({})
const project_code = route.query?.project_code
const role_id = route.query?.role_id
const approveDate = shallowRef<string[]>([])
const successDate = shallowRef<string[]>([])
const formatterCalendar = (day: any) => {
  const month = (day.date.getMonth() + 1).toString().padStart(2, '0');
  const date = (day.date.getDate()).toString().padStart(2, '0');
  const year = day.date.getFullYear();
  const currentDate = `${year}/${month}/${date}`;
  if (approveDate.value.includes(currentDate)) {
    day.className = 'approve-date'
    day.bottomInfo = '审核中'
  } else if (successDate.value.includes(currentDate)) {
    day.className = 'success-date'
    day.bottomInfo = '预约成功'
  }
  return day;
}
const minStartTime = computed(() => {
  const today = new Date()
  const todayString = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`
  if (availableData.appointmentDate === todayString) {
    const now = new Date()
    return {
      hour: now.getHours(),
      minute: now.getMinutes()
    }
  } else {
    return {
      hour: 0,
      minute: 0
    }
  }
})
const minEndTime = computed(() => {
  if (Number(placeholderForm.endTime[0]) === (Number(placeholderForm.startTime[0]) + 1)) {
    return {
      hour: (Number(placeholderForm.startTime[0]) + 1).toString(),
      minute: placeholderForm.startTime[1]
    }
  } else {
    return {
      hour: (Number(placeholderForm.startTime[0]) + 1).toString(),
      minute: 0
    }
  }
})
const availableData = reactive({
  appointmentDate: '',
  comment: '',
  appointmentTime: '',
  campus: '',
  address: '',
  state: ''
})
const allowEdit = computed(() => {
  return availableData.state === '草稿' || !availableData.state
})
const filterStartTime = (type: any, options: any, values: any) => {
  const hour = +values[0];
  if (type === 'hour') {
    return options.filter(
      (option: any) => (Number(option.value) >= 8 && Number(option.value) <= 10) ||
        (Number(option.value) >= 13 && Number(option.value) <= 16),
    );
  }
  if (type === 'minute') {
    if (hour === 8) {
      return options.filter((option: any) => Number(option.value) >= 30);
    }
    if (hour === 10) {
      return options.filter((option: any) => Number(option.value) <= 30);
    }
    if (hour === 16) {
      return options.filter((option: any) => Number(option.value) <= 30);
    }
    if (hour === 18) {
      return options.filter((option: any) => Number(option.value) <= 30);
    }
  }
  return options;
}
const endTimeFilter = (type: any, options: any, values: any) => {
  const hour = +values[0];
  if (type === 'hour') {
    return options.filter(
      (option: any) => (Number(option.value) >= 9 && Number(option.value) <= 11) ||
        (Number(option.value) >= 13 && Number(option.value) <= 17),
    );
  }
  if (type === 'minute') {
    if (hour === 9) {
      return options.filter((option: any) => Number(option.value) >= 30);
    }
    if (hour === 11) {
      return options.filter((option: any) => Number(option.value) <= 30);
    }
    if (hour === 17) {
      return options.filter((option: any) => Number(option.value) <= 30);
    }
  }
  return options;
}
const placeholderForm = reactive<any>({
  date: null,
  startTime: [],
  endTime: [],
  campus: [],
})
const startTimeChange = () => {
  placeholderForm.endTime = [(Number(placeholderForm.startTime[0]) + 1).toString(), placeholderForm.startTime[1]]
}
const timeFormate = () => {
  availableData.appointmentTime = `${placeholderForm.startTime.join(':')} - ${placeholderForm.endTime.join(':')}`
}
const onConfirm = (date: Date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  availableData.appointmentDate = `${year}-${month}-${day}`
  showCalendar.value = false
}
const onTimeConfirm = () => {
  showTimePicker.value = false
  timeFormate()
}
const onCampusConfirm = () => {
  showPicker.value = false
  availableData.campus = placeholderForm.campus[0]
}
const openPicker = () => {
  placeholderForm.campus = [availableData.campus]
  showPicker.value = true
}
const openCalendar = () => {
  placeholderForm.date = availableData.appointmentDate ? new Date(availableData.appointmentDate) : new Date()
  showCalendar.value = true
}
const onSubmit = async (status: any, action: string = '') => {
  let message = '确认提交面试预约吗？'
  let validateResult = []
  if (action === 'draft') {
    message = '确认保存为草稿吗？'
  } else if (action === 'retract') {
    message = '确认撤回预约吗？'
  } else {
    await formRef.value?.validate()
    validateResult = formRef.value?.getValidationStatus()
  }
  if (!validateResult.length || Object.values(validateResult).every(item => item === 'passed')) {
    showConfirmDialog({
      title: '提示',
      message: message,
      confirmButtonColor: '#3F599A'
    }).then(() => {
      const formData = {
        ...availableData,
        appointmentDate: availableData.appointmentDate || null,
        projectName: project.value?.name,
        projectCode: project.value?.code,
        studentName: principal.realName,
        studentNumber: principal.username,
        projectRescuitRole: requirement.value?.role,
        projectRescuitRoleId: requirement.value?.id?.toString(),
        leadingTeacherNumber: project.value?.leadingTeacherNumber,
        leadingTeacherName: project.value?.leadingTeacherName,
        state: status,
        studentTelephone: principal.value?.telephone,
        studentEmail: principal.value?.email,
      }
      if (activeId) {
        updateAppointmentApi(activeId as string, formData, { loading: true }).then((res) => {
          showNotify({
            type: 'success',
            message: '操作成功！'
          })
          getDetail(res)
        })
      } else {
        createAppointmentApi(formData, { loading: true }).then((res) => {
          showNotify({
            type: 'success',
            message: '操作成功！'
          })
          getDetail(res)
        })
      }
    }).catch(() => { })
  }
}
const cancel = () => {
  showConfirmDialog({
    title: '提示',
    message: `确认取消预约吗？`,
    confirmButtonColor: '#3F599A'
  }).then(() => {
    deleteAppointmentApi(activeId as string, { loading: true }).then(() => {
      showNotify({
        type: 'success',
        message: '取消成功！'
      })
      router.push({
        name: '面试预约',
        replace: true
      })
    })
  }).catch(() => { })
}
const openTimePicker = () => {
  if (availableData.appointmentTime) {
    const times = availableData.appointmentTime.split('-')
    placeholderForm.startTime = times[0].split(':') as string[]
    placeholderForm.endTime = times[1].split(':') as string[]
  } else {
    placeholderForm.startTime = ['08', '30']
    placeholderForm.endTime = ['09', '30']
  }
  showTimePicker.value = true
}
const campusOptions = shallowRef<{ text: string, value: string }[]>([])
const loading = ref(false)
const getDetail = async (result?: any) => {
  loading.value = true
  const fillData = result ? result : await findAppointmentByIdApi(activeId as string)
  if (fillData) {
    availableData.appointmentDate = fillData.appointmentDate
    availableData.appointmentTime = fillData.appointmentTime
    availableData.campus = fillData.campus
    availableData.address = fillData.address
    availableData.comment = fillData.comment
    availableData.state = fillData.state
  }
  loading.value = false
}
onMounted(() => {
  if (activeId) {
    getDetail()
  }
  const projectApi = findProjectsApi({ filters: { code: { $eq: project_code } } })
  const appointmentsApi = findAppointmentsApi({ filters: { studentNumber: { $eq: principal.username } } })
  const requirementApi = findRequirementByIdApi(role_id as string)
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
  Promise.all([projectApi, requirementApi, campusApi, appointmentsApi]).then((res: [{ result: any[] }, any, { result: any[] }, { result: any[] }]) => {
    project.value = res[0]?.result?.[0]
    requirement.value = res[1]
    campusOptions.value = res[2].result?.map((i: any) => ({ text: i.name, value: i.name }))
  })
})
</script>

<style scoped>
:global(.app-tab .van-tabs__content) {
  @apply bg-white px-[20px] pt-[10px] pb-[20px] mt-0 relative;
}

:global(.app-tab .van-tabs__nav) {
  background-color: #F1F1F1;
}

:global(.app-tab .van-tabs__content) {
  padding-top: 0;
  padding-bottom: 0;
}

:deep(.cell .van-field__label) {
  color: #9FACCF;
  font-weight: bold;
}

:deep(.cell .van-field__value) {
  color: #3D4348;
}

.fixed-single-button {
  padding-bottom: calc(12px + constant(safe-area-inset-bottom));
  padding-bottom: calc(12px + env(safe-area-inset-bottom));
  height: calc(56 + constant(safe-area-inset-bottom));
  height: calc(56 + env(safe-area-inset-bottom));
  padding-top: 12px;
  padding-left: 16px;
  padding-right: 16px;
}

.fixed-button {
  padding-bottom: calc(12px + constant(safe-area-inset-bottom));
  padding-bottom: calc(12px + env(safe-area-inset-bottom));
  height: calc(122px + constant(safe-area-inset-bottom));
  height: calc(122px + env(safe-area-inset-bottom));
  padding-top: 12px;
  padding-left: 15px;
  padding-right: 15px;
}

:global(.approve-date:not(.van-calendar__day--selected)::after) {
  background: rgba(255, 146, 100, .31);
  content: ' ';
  height: var(--van-calendar-selected-day-size);
  width: var(--van-calendar-selected-day-size);
  position: absolute;
  border-radius: var(--van-radius-md);
}

:global(.success-date:not(.van-calendar__day--selected)::after) {
  background: rgba(0, 63, 222, .19);
  content: ' ';
  height: var(--van-calendar-selected-day-size);
  width: var(--van-calendar-selected-day-size);
  position: absolute;
  border-radius: var(--van-radius-md);
}
</style>
