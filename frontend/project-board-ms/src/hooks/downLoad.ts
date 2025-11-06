import { reactive } from 'vue';
import { download as downloadApi, dowLoadMarkFile as downloadMarkApi } from '@/utils/downLoad';

export const useDownLoadFile = () => {
  const loadingReactive = reactive({});
  const download = async (params: any, key?: string) => {
    key && (loadingReactive[key] = true);
    try {
      await downloadApi(params, { loading: !key });
    } catch (error) {
      key && (loadingReactive[key] = false);
    }
    key && (loadingReactive[key] = false);
  };
  const downloadMark = async (params: any, key?: string) => {
    key && (loadingReactive[key] = true);
    try {
      await downloadMarkApi(params, { loading: !key });
    } catch (error) {
      key && (loadingReactive[key] = false);
    }
    key && (loadingReactive[key] = false);
  };
  return {
    loadingReactive,
    download,
    downloadMark,
  };
};
