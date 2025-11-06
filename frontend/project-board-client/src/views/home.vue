<template>
<router-view class="h-[calc(100vh-74px)] overflow-auto"></router-view>
<van-config-provider :theme-vars="tabbarVars">
  <van-tabbar v-model="active" safe-area-inset-bottom>
    <van-tabbar-item v-for="item in menus" :key="item.name" :name="(item?.name as string)" :to="item.path">
      <template #icon>
        <svg-icon class="text-[22px]" :iconName="item?.meta?.iconName"></svg-icon>
      </template>
      <span class="text-base">{{ item.name }}</span>
    </van-tabbar-item>
  </van-tabbar>
</van-config-provider>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue'
import router, { tabBarList } from '../router'

export default defineComponent({
  setup() {
    const tabbarVars = {
      'tabbar-height': '74px',
      'tabbar-item-font-size': '13px',
      'tabbar-item-icon-margin-bottom': '10px',
      'tabbar-item-text-color': '#62696F',
      'tabbar-item-active-color': '#003FDE'
    }
    const menus = tabBarList
    const active = ref<string>(router.currentRoute.value.name as string || '')
    return {
      menus,
      active,
      tabbarVars
    }
  }
})
</script>

<style scoped></style>
