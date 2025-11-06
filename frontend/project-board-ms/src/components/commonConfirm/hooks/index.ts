import { ref, reactive } from 'vue';

interface ConfirmConfig {
  deleteModalHeader?: string;
  deleteModalContent?: any;
  action?: any;
  ids: string[] | string;
}

export const useDeleteConfirm = () => {
  const config = reactive<ConfirmConfig>({
    deleteModalHeader: '删除',
    deleteModalContent: '删除确认？',
    ids: [],
    action: '',
  });
  const deleteModalVisible = ref(false);
  const openDeleteModal = (confirmConfig: ConfirmConfig) => {
    config.deleteModalContent = Array.isArray(confirmConfig.ids)
      ? `<p>已选中<span style="color:var(--el-color-danger)">${confirmConfig.ids.length}</span>条数据，确认删除？</p>`
      : confirmConfig.deleteModalContent || '删除确认？';
    config.deleteModalHeader = confirmConfig.deleteModalHeader || '删除';
    config.ids = confirmConfig.ids;
    deleteModalVisible.value = true;
    config.action = confirmConfig.action;
  };
  const closeDeleteModal = () => {
    deleteModalVisible.value = false;
  };
  return {
    deleteModalVisible,
    deleteModalConfig: config,
    closeDeleteModal,
    openDeleteModal,
  };
};

interface SecondaryConfirmation {
  header: string;
  content: string;
  callBack: Function;
}

export const useSecondaryConfirmation = () => {
  const config = reactive<SecondaryConfirmation>({
    header: '提示',
    content: '提示',
    callBack: () => {},
  });
  const confirmation_visible = ref(false);
  const open_confirmation = (confirmConfig: SecondaryConfirmation) => {
    config.header = confirmConfig.header || '提示';
    config.content = confirmConfig.content || '删除';
    config.callBack = confirmConfig.callBack;
    confirmation_visible.value = true;
  };
  const close_confirmation = (type: string) => {
    if (type === 'confirm') {
      config.callBack();
    }
    confirmation_visible.value = false;
  };
  return {
    confirmation_visible,
    close_confirmation,
    confirmation_config: config,
    open_confirmation,
  };
};
