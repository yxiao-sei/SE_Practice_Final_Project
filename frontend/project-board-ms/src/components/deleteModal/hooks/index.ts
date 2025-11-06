import type { PromptColumns } from '@/model/pageTable';
import { reactive, ref } from 'vue';

interface DeleteLibrary {
  successCallBack?: Function;
}

export const useDelete = ({
  fields,
  header,
  subheader,
  successCallBack,
}: {
  header?: string;
  subheader?: string;
  fields: PromptColumns;
  successCallBack: Function;
}) => {
  const deleteProps = reactive<{
    header: string;
    subheader: string;
    fields: PromptColumns;
    items: Array<{ [x: string]: any; id: string | number }>;
  }>({
    header: '删除',
    subheader: '请确认删除信息',
    fields: [],
    items: [],
  });

  const deleteLibrary = reactive<DeleteLibrary>({
    successCallBack: () => {},
  });

  if (header) deleteProps.header = header;
  if (subheader) deleteProps.subheader = subheader;
  deleteProps.fields = fields;
  deleteLibrary.successCallBack = successCallBack || (() => {});

  const deleteVisible = ref(false);

  const openDeleteModal = (props: {
    items: Array<{ [x: string]: any; id: string | number }>;
    header?: string;
    subheader?: string;
    fields?: PromptColumns;
  }) => {
    if (props.header) deleteProps.header = props.header;
    if (props.subheader) deleteProps.subheader = props.subheader;
    if (props.fields) deleteProps.fields = fields;
    deleteProps.items = props.items;
    deleteVisible.value = true;
  };

  const closeDeleteModal = (action?: string) => {
    if (action === 'confirm') {
      const ids = deleteProps.items ? deleteProps.items.map((i) => i.id) : [];
      deleteLibrary.successCallBack && deleteLibrary.successCallBack(ids, deleteProps.items);
    } else {
      deleteVisible.value = false;
    }
  };

  return {
    deleteLibrary,
    deleteVisible,
    closeDeleteModal,
    openDeleteModal,
    deleteProps,
  };
};
