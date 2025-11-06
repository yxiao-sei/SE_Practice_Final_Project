<template>
  <div class="px-[15px] py-[10px] bg-white">
    <div class="flex items-center space-x-[6px]">
      <span :class="[statusClassName, 'whitespace-nowrap flex-shrink-0']">{{ application.state }}</span>
      <span class="text-primary-100 truncate flex-grow text-lg">{{ application.projectName }}</span>
    </div>
    <div class="w-full h-[1px] bg-gray-100 mt-[10px]"></div>
    <van-row :gutter="10" class="font-sm text-primary-200 mt-[6px]">
      <van-col :align="'left'" :span="7" class="font-bold text-[#9FACCF]">应聘课题编号</van-col>
      <van-col :align="'left'" :span="17" class="text-primary-200">{{ application.projectCode }}</van-col>
      <van-col :align="'left'" :span="7" class="font-bold text-[#9FACCF]">岗位分工</van-col>
      <van-col :align="'left'" :span="17" class="text-primary-200">{{ application.projectRescuitRole }}</van-col>
    </van-row>
  </div>
</template>

<script lang="ts" setup>
import { toRef, computed } from 'vue'

const props: any = defineProps(['application'])
const application = toRef(props, 'application')
const statusClassName = computed(() => {
  switch (application.value?.state) {
    case '草稿':
      return 'draft'
    case '待审核':
      return 'pending'
    case '通过':
      return 'passed'
    case '拒绝':
      return 'failed'
    case '已取消':
      return 'draft'
    case '面试通过，已录用':
      return 'passed'
    case '已通过，待面试':
      return 'passed'
    default:
      return ''
  }
})
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

.draft {
  @include status(rgba(159, 172, 207, .16), #9FACCF);
}

.passed {
  @include status(rgba(4, 168, 15, .16), #04A80F);
}

.failed {
  @include status(rgba(255, 41, 160, .16), #FF296A);
}
</style>
