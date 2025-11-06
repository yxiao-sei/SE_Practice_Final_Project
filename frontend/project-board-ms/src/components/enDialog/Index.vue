<template>
<el-dialog class="ecnu-dialog" align-center @close="close" :width="width" :show-close="false"
  :close-on-click-modal="false" :close-on-press-escape="false">
  <template #header>
    <span class="dialog-header no-select-text">
      <span class="header-padding"></span>
      <span>{{ props.header }}</span>
      <span class="subheader">{{ props.subheader }}</span>
    </span>
  </template>
  <slot></slot>
  <template #footer>
    <slot name="footer">
      <el-row :gutter="10" class="dialog-footer">
        <template v-if="Number(width.split('%')[0]) >= 50">
          <el-col :span="12"></el-col>
          <el-col :span="6" align="right">
            <el-button type="info" @click="close" auto-insert-space>{{ props.cancelText }}</el-button>
          </el-col>
          <el-col :span="6">
            <el-button :disabled="disabled" auto-insert-space type="primary" :loading="loading" @click="confirm">
              {{ props.confirmText }}
            </el-button>
          </el-col>
        </template>
        <template v-else>
          <el-col :span="12">
            <el-button type="info" @click="close" auto-insert-space>{{ props.cancelText }}</el-button>
          </el-col>
          <el-col :span="12">
            <el-button :disabled="disabled" auto-insert-space type="primary" :loading="loading" @click="confirm">
              {{ props.confirmText }}
            </el-button>
          </el-col>
        </template>
      </el-row>
    </slot>
  </template>
</el-dialog>
</template>
<script lang="ts" setup>
import { toRefs } from 'vue';
const props = defineProps({
  header: String,
  subheader: String,
  disabled: Boolean,
  confirmText: {
    type: String,
    required: false,
    default: '确定',
  },
  cancelText: {
    type: String,
    required: false,
    default: '取消',
  },
  loading: Boolean,
  width: {
    type: String,
    required: false,
    default: '32%',
  },
});
const { disabled } = toRefs(props);
const emits = defineEmits(['close']);
const close = () => {
  emits('close', 'cancel');
};
const confirm = () => {
  emits('close', 'confirm');
};

const { loading, width } = toRefs(props);
</script>
<style scoped lang="scss">
.dialog-header {
  font-size: 28px;
  position: relative;
  display: flex;
  flex-direction: column;

  .header-padding {
    width: 5px;
    display: inline-block;
    position: absolute;
    left: -15px;
    height: 28px;
    background: var(--el-color-primary);
    top: 6px;
  }

  .subheader {
    color: #889199;
    font-size: var(--el-font-size-base);
  }
}
</style>
