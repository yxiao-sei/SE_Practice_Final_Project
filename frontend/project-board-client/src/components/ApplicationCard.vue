<template>
  <div class="bg-white px-[15px] min-h-[129px] text-sm py-[10px]">
    <div class="flex items-center space-x-[6px]" @click="toDetail">
      <span :class="[statusClassName, 'whitespace-nowrap flex-shrink-0']">{{ application.state }}</span>
      <span class="text-primary-100 truncate flex-grow text-lg">{{ application.projectName }}</span>
      <span><svg-icon icon-name="icon-a-lujing905" class="color-[#62696F]"></svg-icon></span>
    </div>
    <div class="w-full h-[1px] bg-gray-100 mt-[10px]"></div>
    <div v-if="application.state === '拒绝'">
      <p class="font-sm text-[#FF296A] font-bold my-[7px]">拒绝理由</p>
      <p class="text-blue-200">
        <van-text-ellipsis class="!leading-tight" rows="3" :content="application.approvalMessage" expand-text="展开"
          collapse-text="收起" />
      </p>
    </div>
    <div v-else>
      <p class="font-sm text-[#9FACCF] font-bold my-[7px]">自我推荐</p>
      <p class="text-blue-200">
        <van-text-ellipsis class="!leading-tight" rows="3" :content="application.selfRecommendContent" expand-text="展开"
          collapse-text="收起" />
      </p>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { toRef, computed } from 'vue';
import { useRouter } from 'vue-router';
const router = useRouter()
const props = defineProps<{
  application: any
}>()
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
const toDetail = () => {
  router.push({ name: '课题报名详情', query: { application_id: application.value.id } })
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
