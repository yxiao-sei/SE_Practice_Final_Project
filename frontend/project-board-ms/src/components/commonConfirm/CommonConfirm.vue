<template>
  <common-dialog>
    <div class="content">
      <p v-html="content"></p>
    </div>
  </common-dialog>
</template>

<script setup lang="ts">
import { toRefs } from 'vue';
import commonDialog from '../enDialog/Index.vue';
const props = defineProps(['content']);
const { content } = toRefs(props);
</script>

<style scoped>
.content {
  font-size: var(--el-font-size-medium);
  margin-bottom: -20px;
}
</style>
