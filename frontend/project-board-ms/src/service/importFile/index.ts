/*
 * @Author: lanseliuyiya
 * @Date: 2024-02-01 17:10:06
 * @LastEditors: laladila1986@163.com
 * @LastEditTime: 2024-02-01 17:18:50
 * @FilePath: \plms-ms\src\service\importFile\index.ts
 * @Description:
 * @Copyright: Copyright 2024, All Rights Reserved.
 */
import { Get, Put } from '@/service';
import { downLoadFile } from '@/utils/downLoad';

const baseUrl = '/app/batch';
export const downLoadExcelTemplate = async (params: { url: string; filename?: string }) => {
  const result = await Get(
    {
      url: `${baseUrl}/template/${params.url}`,
      responseType: 'blob',
      headers: {
        'Content-Type': 'application/json',
      },
    },
    { reductDataFormat: false, loading: false, codeMessageShow: false },
  );
  downLoadFile(result, params.filename);
};

/**
 * 导入文件的 API
 * @param data 导入的数据
 * @param url 导入的 URL
 * @returns Promise
 */
export const uploadTableDataByFileApi = (data: any, url: string) => {
  return Put(
    {
      url: `${baseUrl}/${url}`,
      data,
    },
    { reductDataFormat: true, loading: false, successMessageShow: false },
  );
};
