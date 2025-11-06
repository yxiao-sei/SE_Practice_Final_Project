import qs from "qs";
import type { CustomOption } from "./config";
import { Delete, Get, Post, Put } from './service';

interface GetParams {
  url: string;
  params: any;
  loadingOptions?: CustomOption;
}
export const GetStrapi = ({ params = {}, url, loadingOptions = { loading: true } }: GetParams): Promise<{ result: any[]; count: number }> => {
  return new Promise((resolve) => {
    const paramsInfo = { ...params };
    if (paramsInfo.sort && Array.isArray(paramsInfo.sort)) {
      paramsInfo.sort.push("updatedAt:desc");
    } else if (paramsInfo.sort) {
      paramsInfo.sort = [paramsInfo.sort, "updatedAt:desc"];
    } else {
      paramsInfo.sort = "updatedAt:desc";
    }
    Get(
      {
        url: url,
        params: paramsInfo,
        paramsSerializer: (_params) => {
          return qs.stringify(_params);
        },
      },
      loadingOptions
    )
      .then((res: any) => {
        const { data, meta } = res;
        if (!data || !Array.isArray(data)) {
          resolve({ result: [], count: 0 });
        } else {
          const result = data.map((item) => {
            return {
              id: item.id,
              ...item.attributes,
            };
          });
          resolve({ result, count: meta.pagination.total });
        }
      })
      .catch(() => {
        resolve({ result: [], count: 0 });
      });
  });
};

interface GetOneParams {
  url: string;
  loadingOptions?: CustomOption;
  id: string;
}
export const GetOneStrapi = ({ id, url, loadingOptions = { loading: true } }: GetOneParams): Promise<any> => {
  return new Promise((resolve, reject) => {
    Get(
      {
        url: `${url}/${id}`
      },
      loadingOptions
    )
      .then((res: any) => {
        const { data } = res;
        if (!data) {
          resolve({});
        } else {
          resolve({
            id: data.id,
            ...data.attributes,
          });
        }
      })
      .catch(() => {
        reject();
      });
  });
};
interface DeleteParams {
  url: string;
  loadingOptions?: CustomOption;
  id: string;
}
export const DeleteStrapi = ({ url, id, loadingOptions = { loading: true } }: DeleteParams) => {
  return new Promise((resolve, reject) => {
    Delete({ url: `${url}/${id}` }, loadingOptions)
      .then(() => {
        resolve(null);
      })
      .catch(() => {
        reject();
      });
  });
};

interface PostParams {
  url: string;
  data: any;
  loadingOptions?: CustomOption;
}
export const PostStrapi = ({ url, data, loadingOptions }: PostParams): Promise<{ [x: string]: any }> => {
  return new Promise((resolve, reject) => {
    Post(
      {
        url: url,
        data: { data },
      },
      loadingOptions
    )
      .then((res: any) => {
        const { data: resData } = res;
        if (!resData) {
          resolve({});
        } else {
          resolve({
            id: resData.id,
            ...resData.attributes,
          });
        }
      })
      .catch(() => {
        reject();
      });
  });
};

interface PutParams {
  url: string;
  data: any;
  loadingOptions?: CustomOption;
  id: string;
}
export const PutStrapi = ({ id, url, data, loadingOptions = { loading: false } }: PutParams): Promise<{ [x: string]: any }> => {
  return new Promise((resolve, reject) => {
    Put({
      url: `${url}/${id}`,
      data: { data },
    }, loadingOptions).then((res: any) => {
      const { data: resData } = res;
      if (!resData) {
        resolve({});
      } else {
        resolve({
          id: resData.id,
          ...resData.attributes,
        });
      }
    })
      .catch(() => {
        reject();
      });
  });
};

export function Strapi({ url }: { url: string }) {
  return {
    Get: (params: any, loadingOptions: CustomOption = {}) => {
      return GetStrapi({ url, params, loadingOptions });
    },
    Post: (data: any, loadingOptions: CustomOption = {}) => {
      return PostStrapi({ url, data, loadingOptions });
    },
    Put: (id: string, data: any, loadingOptions: CustomOption = {}) => {
      return PutStrapi({ url, id, data, loadingOptions });
    },
    Delete: (id: string, loadingOptions: CustomOption = {}) => {
      return DeleteStrapi({ id, url, loadingOptions });
    },
    Find: (params: any, loadingOptions: CustomOption = {}) => {
      return GetStrapi({ url, params, loadingOptions });
    }
  }
}
