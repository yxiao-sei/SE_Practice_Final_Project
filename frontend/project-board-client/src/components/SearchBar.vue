<template>
<div class="mb-[10px]">
  <van-search v-model="searchValue" @search="searchChange" shape="round" @clear="searchChange" clear-trigger="always"
    placeholder="请输入项目名称搜索" />
</div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
const emits = defineEmits(['search'])
const searchValue = ref('')
const searchChange = () => {
  emits('search', searchValue.value)
}
</script>

<style scoped></style>