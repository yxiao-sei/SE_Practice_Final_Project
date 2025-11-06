<template>
  <div class="content dialog-upload">
    <input type="file" ref="fileInput" style="display: none;" :multiple="Boolean(limit && limit > 1) || multiple"
      :accept="accept" @change="handleFileChange" />
    <div class="files">
      <div v-for="file, index in fileList" :key="index" class="file-container">
        <span class="file-name" :title="file?.name">{{ file?.name }}</span>
        <div style="display: flex;">
          <template v-if="showPreview && file.status === 'uploaded'">
            <!-- <el-button link type="primary" size="large" @click="() => handlePreview(file)">
              <el-icon class="el-icon--close" title="预览" :size="20">
                <View />
              </el-icon>
            </el-button> -->
            <el-button link type="primary" size="large" @click="() => handleDownload(file)">
              <el-icon class="el-icon--close" title="下载" :size="20">
                <Download />
              </el-icon>
            </el-button>
          </template>
          <el-button size="large" type="danger" link @click="clearFile(index)">
            <el-icon class="el-icon--close" title="移除" :size="20">
              <Close />
            </el-icon>
          </el-button>
        </div>
      </div>
      <el-row v-if="needTag" :gutter="10" class="tag-content">
        <el-col :span="12">
          <el-select filterable v-model="activeFileTag" size="large" placeholder="选择附件标签">
            <el-option v-for="tag in fileTypes" :key="tag.label" :label="tag.label" :value="tag.label"></el-option>
          </el-select>
        </el-col>
        <el-col :span="12" style="display: flex">
          <el-button class="tag-icon large-button" type="primary" @click="toggleFileInput" link :icon="Plus">
            {{ uploadText }}
          </el-button>
        </el-col>
      </el-row>
      <el-button v-else class="large-button" type="primary" @click="toggleFileInput" link :icon="Plus">
        {{ uploadText }}
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, toRef } from 'vue';
import { ElMessage } from 'element-plus';
import { useBaseFieldStore } from '@/stores';
import { Plus, Close } from '@element-plus/icons-vue';
import { usePreviewFile } from '@/hooks/previewFile';
import { Download, View } from '@element-plus/icons-vue';
import { download, dowLoadMarkFile } from '@/utils/downLoad';
import { downloadFileApi } from '@/service/api/file';

const { fileTypes } = useBaseFieldStore();
const activeFileTag = ref()
const props = defineProps({
  uploadText: {
    type: String,
    default: '上传附件',
  },
  limit: {
    type: Number,
    required: false,
    default: 0,
  },
  accept: {
    type: String,
    default: undefined,
  },
  needTag: {
    type: Boolean,
    default: false,
  },
  showPreview: {
    type: Boolean,
    default: false,
  },
  tag: {
    type: String,
    default: '',
  },
  fileList: {
    type: Array<{ name: string, tag?: string, status: string }>,
    default: () => []
  },
  multiple: {
    type: Boolean,
    default: false,
  }
});

const { uploadText, limit, accept, needTag } = props;
const fileList = toRef(props, 'fileList');
const multiple = toRef(props, 'multiple');
const tag = toRef(props, 'tag');
const showPreview = toRef(props, 'showPreview');

const emits = defineEmits(['clearFile', 'update:fileList']);
const clearFile = (index: number) => {
  const newFile = [...fileList.value];
  newFile.splice(index, 1);
  emits('update:fileList', newFile);
}
const fileInput = ref();
function toggleFileInput() {
  if (needTag && !activeFileTag.value) {
    ElMessage.warning('请先选择附件标签');
    return false;
  }
  fileInput.value.click();
}
function handleFileChange(event: any) {
  const uploadFile = event?.target?.files;
  let newFiles: any[] = [...fileList.value];
  for (let i = 0; i < uploadFile.length; i++) {
    const file = uploadFile[i];
    if (file && accept) {
      const types = accept.split(',');
      if (!types.includes(file.type)) {
        ElMessage.warning('所选文件类型暂不支持上传');
      } else {
        if (tag.value) file.tag = tag.value;
        if (activeFileTag.value && needTag) file.tag = activeFileTag.value;
        newFiles.push({ file: file, name: file.name, code: '' })
      }
    }
  }
  newFiles = newFiles.slice(-limit);
  if ((limit && fileList.value.length >= limit) || multiple.value) {
    emits('update:fileList', [...newFiles])
  } else {
    emits('update:fileList', newFiles)
  }
  fileInput.value.value = '';
  activeFileTag.value = '';
}
const { preview, previewContract } = usePreviewFile();
const handleDownload = (file: any): any => {
  downloadFileApi(file.code)
};
const handlePreview = (file: any) => {
  if (file && file.tag === '合同附件' || file.tag === '最终版合同') {
    previewContract(file, file.tag === '最终版合同');
  } else {
    preview(file)
  }
};
</script>

<style scoped lang="scss">
.content {
  width: 100%;
}

.tag-icon {
  margin-top: 0 !important;
}

.tag-content {}

.file-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #EFF2F5;
  height: var(--el-component-size-large);
  padding-left: 10px;
  padding-right: 5px;
  color: #62696F;
  margin-bottom: 10px;
}

.file-container+.file-container {
  // margin-top: 10px;
}

.file-close {
  margin-left: 10px;
  cursor: pointer;
  color: var(--el-color-danger);
}

.large-button {
  height: var(--el-component-size-large);
  // margin-top: 10px;
}

.files {
  width: 100%;
}
</style>