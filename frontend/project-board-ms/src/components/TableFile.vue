<template>
  <template v-if="attachments?.length">
    <el-dropdown type="info" class="file-dropdown" popper-class="file-dropdown">
      <span class="el-dropdown-link">
        <span class="file-name-tooltip">{{ attachments[0]?.name }}</span>
        <el-icon class="el-icon--right" v-if="attachments.length > 1">
          <arrow-down />
        </el-icon>
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item v-for="(file, index) in attachments" :key="index">
            <el-tooltip :content="file?.name" placement="top">
              <span class="drop-item-filename">{{ file?.name }}</span>
            </el-tooltip>
            <!-- <el-button link style="margin-left: 15px" type="primary" size="small" :icon="View"
              @click="() => handlePreview(file)" /> -->
            <el-button style="margin-left: 5px" link type="primary" :icon="Download" size="small"
              @click="() => handleDownload(file)" />
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </template>
  <template v-else>-</template>
</template>

<script lang="ts" setup>
import { downloadFileApi } from '@/service/api/file';
import { Download, View, ArrowDown } from '@element-plus/icons-vue';
import { toRefs } from 'vue';

const props = defineProps(['attachments']);

const { attachments } = toRefs(props);
const handleDownload = (file: any): any => {
  downloadFileApi(file.code)
};
</script>

<style scoped></style>