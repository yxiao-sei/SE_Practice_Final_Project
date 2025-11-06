import { ref, shallowReactive } from 'vue';
import type { Columns } from '@/model/pageTable';

interface ExportConfig {
  url: string;
  fileName: string;
  header?: string;
  subheader?: string;
  params?: string;
  columns: Columns;
}

export const useExportModal = () => {
  const exportConfig: ExportConfig | any = shallowReactive({
    url: '',
    fileName: '下载文件',
    header: '导出',
    subheader: '选择导出字段',
    params: '',
    columns: [],
  });

  const exportVisible = ref(false);

  const openExportModal = ({ params, columns, header, subheader, fileName, url }: ExportConfig) => {
    exportConfig.params = params;
    exportConfig.columns = columns;
    exportConfig.url = url;
    exportConfig.fileName = fileName;
    exportConfig.header = header || exportConfig.header;
    exportConfig.subheader = subheader || exportConfig.subheader;
    exportVisible.value = true;
  };

  const closeExportModal = () => {
    exportVisible.value = false;
    setTimeout(() => {
      exportConfig.params = '';
      exportConfig.columns = [];
    }, 200);
  };

  return {
    exportVisible,
    exportConfig,
    openExportModal,
    closeExportModal,
  };
};
