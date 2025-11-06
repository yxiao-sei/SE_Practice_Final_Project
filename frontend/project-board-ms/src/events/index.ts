
import { findMetaDataListApi } from '@/service/api/meta-data';
import { useBaseFieldStore } from '@/stores'
const handleResult = (result: any[]) => {
  return result.map((project: any) => ({
    id: project.id,
    label: project.name,
    value: project.code,
  }));
};

const handleMetadatas = (result: any[]) => {
  return result.map((item: any) => {
    return {
      ...item,
      label: item.name,
      value: item.name,
      jsonData: item.jsonData,
    };
  });
};

const handleDepartDataMetadatas = (result: any[]) => {
  return result.map((item: any) => {
    return {
      ...item,
      label: item.name,
      value: item.name,
      code: item.jsonData?.[0]?.value,
    };
  });
};

export const description = {
  projectLabels: {
    params: {
      pagination: { pageSize: 10000 },
      fields: ['id', 'name', 'jsonData'],
      filters: {
        isModifiable: {
          $eq: 1,
        },
        category: {
          $eq: '课题标签',
        },
      },
    },
    target: 'projectLabels',
    handleResult: handleMetadatas,
  },
  projectStatus: {
    params: {
      pagination: { pageSize: 10000 },
      fields: ['id', 'name', 'jsonData'],
      filters: {
        isModifiable: {
          $eq: 1,
        },
        category: {
          $eq: '课题状态',
        },
      },
    },
    target: 'projectStatus',
    handleResult: handleMetadatas,
  },
  memberRecruitmentStatus: {
    params: {
      pagination: { pageSize: 10000 },
      fields: ['id', 'name', 'jsonData'],
      filters: {
        isModifiable: {
          $eq: 1,
        },
        category: {
          $eq: '课题成员招募状态',
        },
      },
    },
    target: 'memberRecruitmentStatus',
    handleResult: handleMetadatas,
  },
  projectOfDepartments: {
    params: {
      pagination: { pageSize: 10000 },
      fields: ['id', 'name', 'jsonData'],
      filters: {
        isModifiable: {
          $eq: 1,
        },
        category: {
          $eq: '课题所属院系',
        },
      },
    },
    target: 'projectOfDepartments',
    handleResult: handleDepartDataMetadatas,
  },
  metadataCategory: {
    params: {
      pagination: { pageSize: 10000 },
      fields: ['id', 'name', 'jsonData'],
      filters: {
        isModifiable: {
          $eq: 0,
        },
      },
    },
    api: 'metadatas',
    target: 'metadataCategories',
    handleResult: handleMetadatas,
  },
  metadataDirection: {
    params: {
      pagination: { pageSize: 10000 },
      fields: ['id', 'name', 'jsonData'],
      filters: {
        isModifiable: {
          $eq: 1,
        },
        category: {
          $eq: '研究方向',
        },
      },
    },
    api: 'metadatas',
    target: 'metadataDirection',
    handleResult: handleMetadatas,
  },
  xqList: {
    params: {
      pagination: { pageSize: 10000 },
      fields: ['id', 'name', 'jsonData'],
      filters: {
        isModifiable: {
          $eq: 1,
        },
        category: {
          $eq: '校区',
        },
      },
    },
    api: 'metadatas',
    target: 'xqList',
    handleResult: handleMetadatas,
  },
  userTypeList: {
    params: {
      pagination: { pageSize: 10000 },
      fields: ['id', 'name', 'jsonData'],
      filters: {
        isModifiable: {
          $eq: 1,
        },
        category: {
          $eq: '学生类别',
        },
      },
    },
    api: 'metadatas',
    target: 'userTypeList',
    handleResult: handleMetadatas,
  },
  xwlbList: {
    params: {
      pagination: { pageSize: 10000 },
      fields: ['id', 'name', 'jsonData'],
      filters: {
        isModifiable: {
          $eq: 1,
        },
        category: {
          $eq: '学位类别',
        },
      },
    },
    api: 'metadatas',
    target: 'xwlbList',
    handleResult: handleMetadatas,
  },
  xxxsList: {
    params: {
      pagination: { pageSize: 10000 },
      fields: ['id', 'name', 'jsonData'],
      filters: {
        isModifiable: {
          $eq: 1,
        },
        category: {
          $eq: '学习形式',
        },
      },
    },
    api: 'metadatas',
    target: 'xxxsList',
    handleResult: handleMetadatas,
  },
  postStatus: {
    params: {
      pagination: { pageSize: 10000 },
      fields: ['id', 'name', 'jsonData'],
      filters: {
        isModifiable: {
          $eq: 1,
        },
        category: {
          $eq: '岗位状态',
        },
      },
    },
    api: 'metadatas',
    target: 'postStatus',
    handleResult: handleMetadatas,
  }
};

export const findDescriptions = (searchItems: any) => {
  const _s = searchItems
    .filter((item: any) => item.elementType === 'Select')
    .map((i: { optionKey: any }) => i.optionKey);
  if (!_s.length) return [];
  const target: string[] = [];
  Object.keys(description).forEach((key) => {
    if (_s.includes(description[key as keyof typeof description].target) && !target.includes(key)) target.push(key);
  });
  return target;
};

export function fetchBaseFieldEvent(key: string, refresh = false) {
  const { reobtainKeys, setFieldCached, updateReobtainKeys, ...state } = useBaseFieldStore()
  const { params, target, handleResult } = description[key as keyof typeof description];
  if (target && (!state[target].length || reobtainKeys.includes(key) || refresh)) {
    findMetaDataListApi(params, { loading: false }).then(async ({ result }) => {
      setFieldCached(target, handleResult ? handleResult(result) : result);
      updateReobtainKeys(key);
    })
  }
}
