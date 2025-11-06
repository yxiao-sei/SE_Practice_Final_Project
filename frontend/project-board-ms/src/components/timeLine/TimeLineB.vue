<template>
  <div :class="'custom-steps--' + direction" :style="rootStyle">
    <template v-if="stepsList && stepsList.length">
      <div :class="['custom-step__center']" v-for="(item, index) in stepsList" :key="index">
        <!-- 图标 -->
        <div :class="'custom-step__head__' + direction">
          <slot name="icon">
            <span :class="['custom-status__icon', item.status]"></span>
            <!-- 线 -->
            <div :class="['custom-step__line', item.status, direction]"></div>
          </slot>
        </div>
        <slot name="title">
          <div :class="['custom-step__title', item.status]">
            {{ item.content }}
          </div>
        </slot>
        <!-- 图标标题 -->
        <slot name="timestamp">
          <div :class="['custom-step__title', 'custom-step__timestamp', item.status]">
            {{ item.timestamp }}
          </div>
        </slot>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

interface Column {
  content: string;
  timestamp: number;
  status: string;
}

const {
  stepsList,
  rackHeight,
  direction,
  textActiveColor,
  iconSize,
  rackBgColor,
  iconBgColor,
  rackFinishedColor,
  iconFinishedColor,
  textFinishedColor,
  textColor,
  background,
} = defineProps<{
  stepsList: Array<Column>;
  rackHeight: string;
  iconSize: string;
  rackBgColor: string;
  iconBgColor: string;
  rackFinishedColor: string;
  iconFinishedColor: string;
  textFinishedColor: string;
  textColor: string;
  background: string;
  direction: string;
  textActiveColor: string;
}>();

const rootStyle = computed(() => {
  return {
    '--rack-height': rackHeight,
    '--icon-size': iconSize,
    '--rack-bg-color': rackBgColor,
    '--icon-bg-color': iconBgColor,
    '--rack-finished-color': rackFinishedColor,
    '--icon-finished-color': iconFinishedColor,
    '--text-finished-color': textFinishedColor,
    '--text-color': textColor,
    '--text-active-color': textActiveColor,
    'background-color': background,
  };
});
</script>

<style lang="scss" scoped>
.custom-steps--horizontal {
  display: flex;
  align-items: center;
  // width: 100%;
  height: 100%;
}

.custom-steps--vertical {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.custom-step__title {
  font-size: var(--el-font-size-base);
  color: var(--text-color);
}

.custom-step__title.finished {
  color: var(--text-finished-color);
}

.custom-step__title.active {
  color: var(--text-active-color);
}

.custom-step__center {
  flex-basis: 50%;
}

.custom-step__line {
  position: absolute;
  right: -50%;
  left: 50%;
  height: 1px;
  z-index: 0;
  border-top: var(--rack-height) solid var(--rack-bg-color);
}

.custom-step__line.horizontal.finished {
  border-top: var(--rack-height) solid var(--rack-finished-color);
}

.custom-step__center:last-of-type .custom-step__line {
  display: none;
}

.custom-step__center .custom-step__title {
  text-align: center;
  height: 20px;
  line-height: 20px;
}

.custom-step__center .custom-step__head__horizontal {
  margin-top: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}

.custom-status__icon {
  width: var(--icon-size);
  height: var(--icon-size);
  z-index: 1;
  border-radius: 50%;
  background-color: var(--icon-bg-color);
}

.custom-status__icon.finished {
  background-color: var(--icon-finished-color);
}

.custom-status__icon.active {
  background-color: var(--icon-finished-color);
}
</style>
