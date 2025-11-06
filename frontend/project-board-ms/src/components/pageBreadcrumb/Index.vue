<template>
  <div class="en-breadcrumb">
    <el-breadcrumb :separator-icon="ArrowRight">
      <el-breadcrumb-item
        v-for="(r, index) in routeList"
        :key="r.path"
        :to="r.path && { path: r.path } ? { path: r.path } : undefined"
      >
        <template v-if="index === routeList.length - 1">
          <span style="color: var(--el-text-color-primary)">
            <slot>
              {{ r.meta.menuName }}
            </slot>
          </span>
        </template>
        <template v-else>
          <span style="color: #405078;">
            {{ r.meta.menuName }}
          </span>
        </template>
      </el-breadcrumb-item>
    </el-breadcrumb>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ArrowRight } from '@element-plus/icons-vue';
const route = useRoute();
const router = useRouter();
const routeList = computed(() => {
  let targetRecord: any[] = [];
  if (route.meta?.isRoot) {
    targetRecord = [route];
  } else {
    targetRecord = route.matched
      .filter((i) => i.path !== '/home')
      .map((i: any) => {
        return {
          ...i,
          path: i.meta?.isSub ? null : i.path,
        };
      });
    if (route.meta.showTarget) {
      const middle = router.getRoutes().find((i) => i.path === route.meta.target);
      targetRecord = [
        ...targetRecord.slice(0, targetRecord.length - 1),
        middle,
        targetRecord[targetRecord.length - 1],
      ];
    }
  }
  // return targetRecord;
  return targetRecord.slice(targetRecord.length-1,targetRecord.length);
});
</script>

<style scoped lang="scss">
.en-breadcrumb {
  --el-text-color-regular: #405078;
  padding-bottom: 20px;

  :deep(.el-breadcrumb) {
    font-size: var(--el-font-size-base);
    color: var(--el-text-color-regular);

    .el-breadcrumb__inner.is-link,
    .el-breadcrumb__inner a {
      font-weight: normal;
    }

    .el-breadcrumb__item[aria-current='page'] {
      .el-breadcrumb__inner {
        color: var(--el-text-color-regular);
      }
    }
  }
}
</style>
