/*
 * @Author: laladila1986@163.com
 * @Date: 2024-01-03 17:37:50
 * @LastEditors: laladila1986@163.com
 * @LastEditTime: 2024-01-11 16:53:57
 * @FilePath: \plms-ms\src\hooks\uploadFiles.ts
 * @Description:
 * @Copyright: Copyright 2024, All Rights Reserved.
 */
import { Put } from '@/service';

const uploadApi = (data: any) => {
  return Put(
    {
      url: '/app/file',
      data,
    },
    { reductDataFormat: true, loading: false, successMessageShow: false },
  );
};

export const useUploadFiles = () => {
  const uploadFn = (files: any[]): Promise<any[] | null> => {
    return new Promise((resolve, reject) => {
      if (!files.length) resolve(null);
      const uploadList: any[] = [];
      const uploadedList: any[] = [];
      files.forEach((file) => {
        if (file.status === 'uploaded') {
          uploadedList.push(file.name);
        } else {
          uploadList.push(file);
        }
      });
      const promiseArr = uploadList.map((file) => {
        const formData = new FormData();
        formData.append('file', file.raw);
        return uploadApi(formData);
      });
      Promise.all(promiseArr)
        .then((resultArr) => {
          let targets: any[] = [];
          resultArr.forEach((i) => {
            targets = [...targets, i];
          });
          resolve([...uploadedList, ...targets]);
        })
        .catch(() => reject());
    });
  };
  return {
    uploadFn,
  };
};
