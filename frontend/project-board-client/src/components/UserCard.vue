<template>
  <div class="flex bg-white px-[15px] py-[10px]">
    <van-image round class="mr-[15px] w-[81px] h-[81px]" src="https://" />
    <div class="flex-grow space-y-[2px] py-[7px] text-blue-200">
      <p class="text-xl text-primary-100">{{ user?.realName }}</p>
      <p class="text-sm">{{ user?.username }}</p>
      <p class="text-sm">{{ user?.departmentName }}</p>
    </div>
    <svg-icon @click="toUserProfile" v-if="props.showNotice" icon-name="icon-a-zu1973"
      class="text-[28px] cursor-pointer" />
  </div>
</template>

<script setup lang="ts">
import { useUserInfo } from '@/stores/cacheInfo'
import router from '@/router';
const { user } = useUserInfo()
const props = defineProps<{ showNotice?: boolean }>()

const toUserProfile = () => {
  router.push({ name: '订阅设置' })
}
</script>

<style scoped></style>
