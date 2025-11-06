/*
 * @Author: laladila1986@163.com
 * @Date: 2023-10-16 10:18:44
 * @LastEditors: laladila1986@163.com
 * @LastEditTime: 2023-10-16 10:31:34
 * @FilePath: \llbxt-web\src\hooks\approve.ts
 * @Description:
 * @Copyright: Copyright 2023, All Rights Reserved.
 */
import { reactive, ref } from 'vue';

// interface Approve {
//   header: string,
//   subheader: string,
//   filedList: any[],
//   row: any[],
// }

export function useApprove({ translateToCN }: { translateToCN: Function }) {
  const approve_info = reactive<any>({
    header: '',
    subheader: '',
    filedList: [],
    row: [],
  });
  const approve_visible = ref(false);
  const open_approve_dialog = (row: any, action: string) => {
    if (action === '同意') {
      approve_info.header = '同意';
      approve_info.subheader = '请确认修改信息';
    } else {
      approve_info.header = '拒绝';
      approve_info.subheader = '请确认修改信息';
    }
    approve_info.filedList = [
      {
        label: '修改字段',
        prop: 'fieldName',
      },
      {
        label: '修改前',
        prop: 'oldValueDescription',
      },
      {
        label: '修改后',
        prop: 'newValueDescription',
      },
    ];
    approve_info.rows = [{ ...row, fieldName: translateToCN(row.fieldName) }];
    approve_visible.value = true;
  };
  const close_approve_dialog = () => {
    approve_visible.value = false;
  };
  return {
    approve_info,
    open_approve_dialog,
    approve_visible,
    close_approve_dialog,
  };
}
