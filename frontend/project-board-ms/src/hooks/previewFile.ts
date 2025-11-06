/*
 * @Author: laladila1986@163.com
 * @Date: 2024-03-02 19:57:04
 * @LastEditors: laladila1986@163.com
 * @LastEditTime: 2024-03-02 23:34:05
 * @FilePath: \plms-ms\src\hooks\previewFile.ts
 * @Description:
 * @Copyright: Copyright 2024, All Rights Reserved.
 */

import { ElMessage } from 'element-plus';

export const usePreviewFile = () => {
  const allowExtensions = ['xlsx', 'docx', 'pdf', 'doc'];
  const preview = (filename: string) => {
    const fileExtension = filename.split('.').pop() as string;
    if (!allowExtensions.includes(fileExtension)) return ElMessage.warning(`暂不支持.${fileExtension}文件预览，请下载查看`);
    const { origin, pathname } = window.location;
    window.open(`${origin + pathname}#/preview?filename=${filename}`, '_blank');
  };
  const previewMark = (filename: string) => {
    const fileExtension = filename.split('.').pop() as string;
    if (!allowExtensions.includes(fileExtension)) return ElMessage.warning(`暂不支持.${fileExtension}文件预览，请下载查看`);
    const { origin, pathname } = window.location;
    window.open(`${origin + pathname}#/preview-mark?filename=${filename}`, '_blank');
  };
  const previewContract = (filename: string, isFinalVersion: boolean) => {
    const _allowExtensions = ['docx', 'pdf', 'doc'];
    const fileExtension = filename.split('.').pop() as string;
    if (!_allowExtensions.includes(fileExtension)) return ElMessage.warning(`暂不支持.${fileExtension}文件预览，请下载查看`);
    const { origin, pathname } = window.location;
    window.open(
      `${origin + pathname}#/preview-contract?filename=${filename}${
        isFinalVersion ? '&isFinalVersion=true' : ''
      }`,
      '_blank',
    );
  };
  return {
    preview,
    previewMark,
    allowExtensions,
    previewContract,
  };
};
