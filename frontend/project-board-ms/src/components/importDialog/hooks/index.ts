import { ref } from 'vue';

export const useImportDialog = () => {
  const importDialogVisible = ref(false);
  let callBackFn: Function;

  const openDialogVisible = ({ callBack }: any) => {
    importDialogVisible.value = true;
    callBackFn = callBack;
  };

  const closeDialogVisible = (status: string, file?: any) => {
    if (status === 'confirm') {
      callBackFn && callBackFn(file);
    } else {
      importDialogVisible.value = false;
    }
  };

  return {
    openDialogVisible,
    closeDialogVisible,
    importDialogVisible,
  };
};
