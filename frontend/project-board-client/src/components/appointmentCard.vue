<template>
  <div class="px-[14px] py-[10px] bg-white">
    <div class="flex items-center space-x-[6px]" :style="{ cursor: 'pointer' }" @click="toDetail">
      <span :class="statusClassName">{{ appointment.state }}</span>
      <span class="truncate text-primary-100 text-lg flex-grow text-left">{{ appointment.projectName }}</span>
      <span><svg-icon icon-name="icon-a-lujing905" class="color-[#62696F]"></svg-icon></span>
    </div>
    <div class="w-full h-[1px] bg-gray-100 my-[8px]"></div>
    <van-row :gutter="10" class="space-y-[6px]">
      <van-col class="text-primary-200 font-[600]" :span="6">面试时间</van-col>
      <van-col :span="18">{{ appointment.appointmentDate?.replaceAll('-', '/') }}&nbsp;&nbsp;{{
        appointment.appointmentTime
      }}</van-col>
      <van-col class="text-primary-200 font-[600]" :span="6">面试校区</van-col>
      <van-col :span="18">{{ appointment.campus }}</van-col>
      <van-col class="text-primary-200 font-[600]" :span="6">面试地点</van-col>
      <van-col :span="18">{{ appointment.address }}</van-col>
      <van-col class="text-primary-200 font-[600]" :span="6">备注</van-col>
      <van-col :span="18">{{ appointment.comment }}</van-col>
    </van-row>
  </div>
</template>

<script lang="ts" setup>
import { toRef, computed } from 'vue'
import { useRouter } from 'vue-router';

const props: any = defineProps(['appointment', 'link'])
const appointment = toRef(props, 'appointment')
const router = useRouter()

const statusClassName = computed(() => {
  if (appointment.value?.state === '通过') return 'passed'
  if (appointment.value?.state === '拒绝') return 'failed'
  if (appointment.value?.state === '待审核') return 'pending'
  if (appointment.value?.state === '草稿') return 'draft'
  return ''
})
const toDetail = async () => {
  router.push({
    name: '新增预约',
    query: {
      id: appointment.value?.id,
      project_code: appointment.value?.projectCode,
      role_id: appointment.value?.projectRescuitRoleId
    }
  })
}
</script>

<style scoped lang="scss">
@mixin status($bg, $color) {
  background-color: $bg;
  color: $color;
  @apply h-[28px] leading-[28px] px-[9px] rounded-[3px];
}

.pending {
  @include status(rgba(255, 146, 90, .16), #FF925A);
}

.passed {
  @include status(rgba(0, 63, 222, .16), #003FDE);
}

.failed {
  @include status(rgba(255, 41, 160, .16), #FF296A);
}

.draft {
  @include status(rgba(159, 172, 207, .16), #9FACCF);
}
</style>
