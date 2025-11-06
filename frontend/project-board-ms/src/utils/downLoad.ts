import { Get, Post } from "@/service";
import { getRoleFilter } from "@/service/strPagedApi";
import { removeInvalidValues } from "@/utils";
import qs from "qs";
import { ElMessage, ElMessageBox } from "element-plus";
import type { Action } from "element-plus";
import router from "@/router";

export const downloadApi = (params: any, options = {}) => {
  return Get(
    {
      url: "/app/file",
      responseType: "blob",
      headers: {
        "Content-Type": "application/json",
      },
      params,
    },
    {
      reductDataFormat: false,
      loading: true,
      codeMessageShow: false,
      ...options,
    }
  );
};

export const downloadMarkApi = (params: any, options = {}) => {
  return Get(
    {
      url: "/app/file/mark",
      responseType: "blob",
      headers: {
        "Content-Type": "application/json",
      },
      params,
    },
    {
      reductDataFormat: false,
      loading: true,
      codeMessageShow: false,
      ...options,
    }
  );
};

export const downLoadFile = (
  result: any,
  defaultFileName = "",
  formatFileName = ""
) => {
  const { headers } = result;
  const blob = new Blob([result.data], {
    type: headers["content-type"],
  });
  let fileName = headers["content-disposition"] as string;
  let name: string = "";
  let format;
  try {
    let contentDispositionHeader = headers["content-disposition"] as string;
    // 解析 'content-disposition' 头部以获取文件名
    let parts = contentDispositionHeader.split(";");
    let filenamePart = parts
      .find((part) => part.trim().startsWith("filename="))
      ?.trim();
    if (filenamePart) {
      // 去除 'filename=' 前缀并解码URL编码的字符
      name = decodeURIComponent(
        filenamePart.substring("filename=".length).trim()
      );
      // 如果需要获取文件扩展名，可以进一步分割
      format = name.split(".").pop(); // 获取文件扩展名
    }
  } catch (error) {
    console.error(error, "文件重命名出错");
  }

  fileName = formatFileName
    ? formatFileName
    : defaultFileName
    ? defaultFileName + "." + format
    : name;
  if (window.navigator && (window as any).navigator.msSaveOrOpenBlob) {
    (navigator as any).msSaveBlob(blob, fileName);
  } else {
    const url = (object: Blob | MediaSource) => {
      return window.URL.createObjectURL
        ? window.URL.createObjectURL(object)
        : window.webkitURL.createObjectURL(object);
    };
    const link = document.createElement("a");
    link.href = url(blob);
    link.download = window.decodeURIComponent(fileName);
    link.click();
  }
};

export const downLoadStrapi = async (
  data: {
    url: string;
    params: any;
    titles: any[];
    fileName: string;
  },
  options: any = {
    reductDataFormat: false,
    loading: false,
    codeMessageShow: false,
  }
) => {
  const _params = getRoleFilter(JSON.parse(data.params), "/fileExport/export");
  const result = await Post(
    {
      url: "/app/fileExport/export",
      responseType: "blob",
      headers: {
        "Content-Type": "application/json",
      },
      data: {
        url: data.url,
        titles: data.titles,
        fileName: data.fileName,
        params: qs.stringify(removeInvalidValues(_params)),
      },
    },
    options
  );
  downLoadFile(result);
};

export const download = (params: any, loadingOptions?: any): Promise<any> => {
  return downloadApi(params, loadingOptions).then((result) => {
    downLoadFile(result, "", params.fileName);
  });
};

export const dowLoadMarkFile = (
  params: any,
  loadingOptions?: any
): Promise<any> => {
  return downloadMarkApi(params, loadingOptions).then((result: any) => {
    downLoadFile(result, "", "");
  });
};

export const exportFiles = (url: string, data: any) => {
  const _params = getRoleFilter(JSON.parse(data.params), "/fileExport/export");
  return new Promise((resolve, reject) => {
    Post({
      url: url,
      data: {
        url: data.url,
        titles: data.titles,
        fileName: data.fileName,
        params: qs.stringify(removeInvalidValues(_params)),
      },
      responseType: "blob",
      headers: {
        "Content-Type": "application/json",
      },
    })
      .then((response: any) => {
        if (response.data.size === 0) {
          ElMessage.warning("暂无文件");
          resolve(null); // Resolve the promise with a message
          return;
        }

        if (response.data.type === "application/json") {
          const reader = new FileReader();
          reader.onload = function () {
            try {
              const jsonObj = JSON.parse(reader.result as string);
              if (jsonObj.code === 0) {
                ElMessageBox.alert(
                  "后台数据处理中，请耐心等待，可前往导入导出记录中查看导出进度，完成后可进行下载。",
                  "提示",
                  {
                    autofocus: false,
                    showCancelButton:true,
                    confirmButtonText: "前往查看进度",
                    callback: (action: Action) => {
                      if (action == "confirm") {
                        router.push("/exportRecord");
                      }
                    },
                  }
                );
              } else {
                ElMessage.warning(jsonObj.message); // Log error message from JSON
              }
              resolve(jsonObj); // Reject the promise with the error object
            } catch (e) {
              resolve(e);
            }
          };
          reader.readAsText(response.data);
        } else {
          const contentDisposition = response.headers["content-disposition"];
          let filename = contentDisposition
            .split(";")[1]
            .split("=")[1]
            .trim()
            .replace(/"/g, "");
          filename = decodeURIComponent(filename); // Decoding the URI component for the filename

          const blob = new Blob([response.data], { type: response.data.type });
          const url = window.URL.createObjectURL(blob);

          const a = document.createElement("a");
          a.href = url;
          a.download = filename;
          document.body.appendChild(a);
          a.click();

          // Clean up
          window.URL.revokeObjectURL(url);
          document.body.removeChild(a);
          resolve(null); // Resolve the promise after the download
        }
      })
      .catch((err) => {
        console.error("Error downloading file:", err);
        resolve(err);
      });
  });
};
