import { Get, Post } from '..';

const BASE_URL = '/cfms-api/files'
const projectCode = 'project-board-client'

interface FileData {
  name: string,
  code: string,
  file?: File
}
export type UploadBody = FileData[]
export const uploadFileApi = (
  params: UploadBody,
  loadingOptions = { loading: false, showSuccessMessage: false }): Promise<UploadBody> => {
  return new Promise((resolve, reject) => {
    const data = new FormData();
    data.append("projectCode", projectCode);
    const uploadedFile: UploadBody = []
    params.forEach((item: FileData) => {
      if (item.file) {
        data.append("files", item.file);
        data.append("fileNames", item.name);
      } else {
        uploadedFile.push(item)
      }
    })
    Post(
      {
        url: `${BASE_URL}/upload`,
        data
      },
      loadingOptions,
    ).then((result: any) => {
      let targets = result.map((file: any, index: number) => {
        return {
          name: file.fileName,
          code: file.fileCode,
        }
      });
      resolve([...uploadedFile, ...targets])
    }).catch((error: any) => {
      reject(error)
    })
  })
}

export const downLoadFile = (result: any, defaultFileName = '', formatFileName = '') => {
  const { headers } = result;
  const blob = new Blob([result.data], {
    type: headers['content-type'],
  });
  let fileName = headers['content-disposition'] as string;
  let name: string = "";
  let format;
  try {
    let contentDispositionHeader = headers['content-disposition'] as string;
    // 解析 'content-disposition' 头部以获取文件名
    let parts = contentDispositionHeader.split(';');
    let filenamePart = parts.find(part => part.trim().startsWith('filename='))?.trim();
    if (filenamePart) {
      // 去除 'filename=' 前缀并解码URL编码的字符
      name = decodeURIComponent(filenamePart.substring('filename='.length).trim());
      // 如果需要获取文件扩展名，可以进一步分割
      format = name.split('.').pop(); // 获取文件扩展名
    }
  } catch (error) {
    console.error(error, "文件重命名出错");
  }
  fileName = formatFileName ? formatFileName : defaultFileName ? (defaultFileName + '.' + format) : name;
  if (window.navigator && (window as any).navigator.msSaveOrOpenBlob) {
    (navigator as any).msSaveBlob(blob, fileName);
  } else {
    const url = (object: Blob | MediaSource) => {
      return window.URL.createObjectURL
        ? window.URL.createObjectURL(object)
        : window.webkitURL.createObjectURL(object);
    };
    const link = document.createElement('a');
    link.href = url(blob);
    link.download = window.decodeURIComponent(fileName);
    link.click();
  }
};
export const downloadFileApi = (
  fileCode: string,
  loadingOptions = { loading: true }) => {
  Get(
    {
      url: `${BASE_URL}/download/${fileCode}`,
      responseType: 'blob',
      headers: {
        'Content-Type': 'application/json',
      },
    },
    loadingOptions,
  ).then((res) => downLoadFile(res))
}

