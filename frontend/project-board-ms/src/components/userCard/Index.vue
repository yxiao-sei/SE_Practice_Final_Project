<!--
 * @Author: laladila1986@163.com
 * @Date: 2023-10-30 17:30:24
 * @LastEditors: lanseliuyiya laladila1986@163.com
 * @LastEditTime: 2024-12-11 21:56:06
 * @FilePath: /project-board-ms/src/components/userCard/Index.vue
 * @Description:
 * @Copyright: Copyright 2023, All Rights Reserved.
-->
<template>
<div class="user-card">
  <div class="detail-card">
    <span>欢迎，{{ userInfo.realName }}</span>
    <el-menu popper-class="reset_as_menu_pop" class="reset_as_menu" mode="horizontal" style="max-width: 100px"
      @select="handleSelect" :ellipsis="false" :collapse="true">
      <el-sub-menu index="0">
        <template #title>
          <el-icon :size="20">
            <MoreFilled />
          </el-icon></template>
        <el-menu-item index="2-1">登出系统</el-menu-item>
      </el-sub-menu>
    </el-menu>
  </div>
  <el-avatar shape="circle" class="avatar" :src="userInfo.avatar" />
  <en-dialog v-model="isVisible" header="退出" confirmText="继续注销" :loading="loading" @close="closeDialog">
    <p style="font-size: var(--el-font-size-base)">
      注意：本系统已启用ECNU公共数据库统一认证自动登录，如果您使用的是公共数据库登录，您现在退出的只是{{ Locale.SystemTitle }}系统，公共数据库登录仍然有效！如需切换账号，请前往公共数据库主页进行注销并重新登录！
    </p>
  </en-dialog>
  <en-dialog v-model="isReloadVisible" header="提示" cancelText="忽略" confirmText="立即重新加载" :loading="loading"
    @close="closeReloadDialog">
    <p style="font-size: var(--el-font-size-base)">
      即将对系统的字号进行调整，请注意，这可能会影响部分布局，为了确保您的界面正常显示，建议您立即重新加载页面，您也可以忽略之后手动刷新页面。
    </p>
  </en-dialog>
</div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { storeToRefs } from 'pinia';
import EnDialog from '~/enDialog/Index.vue';
import { logoutApi } from '@/service/api/user';
import { useCacheUserInfoStore } from '@/stores';
import { MoreFilled } from "@element-plus/icons-vue";
import Locale from '@/locales'

const loading = ref(false);
const { userInfo } = storeToRefs(useCacheUserInfoStore());

const isVisible = ref(false);
const isReloadVisible = ref(false);
const enlargementFactor = ref(Object.hasOwnProperty.call(
  localStorage,
  "_enlargementFactor",
) ? Number(localStorage.getItem("_enlargementFactor"))
  : 0.1)

const handleLogOut = () => {
  isVisible.value = true;
};

const closeReloadDialog = (status: string) => {
  if (status === 'confirm') {
    window.history.go(0)
  } else {
    setFontSize(192)
  }
  isReloadVisible.value = false
}

const closeDialog = (status: string) => {
  if (status === 'confirm') {
    loading.value = true;
    logoutApi()
      .then((res) => {
        if (res.code === 0) {
          isVisible.value = false;
          window.location.replace(res.result);
        }
      })
      .finally(() => {
        loading.value = false;
      });
  } else {
    isVisible.value = false;
  }
};

function setFontSize(baseSize: number) {
  const _enlargementFactor = Object.hasOwnProperty.call(
    localStorage,
    "_enlargementFactor",
  ) ? Number(localStorage.getItem("_enlargementFactor"))
    : 0;
  enlargementFactor.value = _enlargementFactor
  let scale =
    (document.documentElement.clientWidth <= 1080
      ? 1080
      : document.documentElement.clientWidth) / 1920;
  // 设置页面根节点字体大小，最高放大比例为2）
  document.documentElement.style.fontSize =
    baseSize * (Math.min(scale, 2) * (1 + _enlargementFactor)) + "px";
}

const setEnlargementFactor = (_enlargementFactor: number) => {
  if (enlargementFactor.value === _enlargementFactor) return
  localStorage.setItem("_enlargementFactor", String(_enlargementFactor))
  isReloadVisible.value = true
}

const handleSelect = (key: string, keyPath: string[]) => {
  switch (key) {
    case '2-1':
      handleLogOut()
      break
    case "1-1":
      setEnlargementFactor(0.3)
      break;
    case "1-2":
      setEnlargementFactor(0.1)
      break;
    case "1-3":
      setEnlargementFactor(-0.1)
      break;
    default:
      break;
  }
};
</script>

<style scoped>
.user-card {
  /* background: #2f3235; */
  display: flex;
  font-size: 16px;
}

:global(.user-card .el-avatar) {
  flex-shrink: 0;
}

.detail-card {
  padding-right: 20px;
  display: flex;
  flex-grow: 1;
  justify-content: center;
  flex-direction: column;
  align-items: flex-end;
  color: #9BB7FF;
  font-size: var(--el-font-size-medium);
}

.detail-card span {
  line-height: 20px;
}

:global(.user-card .el-dialog__body) {
  padding-bottom: 15px;
}

.big_font {
  font-size: 18px;
}

.middle_font {
  font-size: 16px;
}

.small_font {
  font-size: 14px;
}

.active {
  color: #212224 !important;
}
.avatar {
  :global(.el-avatar) {
    --el-avatar-size: 56px;
  }
}
</style>
