export interface VxeColumn {
  title: string; // 标题
  field: string; // 字段
  align: string; // 是否剧中
  formatter?: any[]; //formatter
  slots?: any; //是否使用插槽
}

// Table Column配置
export interface Column {
  slot?: string; // 是否使用插槽
  isChecked?: boolean; // 是否初始化选中
  unit?: string; // 字段单位，用于金额等
  type?: string; // 字段类型
  colSpan?: number; // Col的span属性，promptField的列宽
  isDefault?: boolean; // 是否为默认展示字段，默认选中，不可取消
  isPromptField?: boolean; // 是否展示在提示弹窗
  props: {
    // ElementUI的props属性
    fixed?: string;
    label: string;
    prop: string;
    width?: number | string;
    minWidth?: number;
    [x: string]: any;
  };
}

export type PromptColumns = Column[];

export type Columns = Column[];

export interface MenuButton {
  command: string;
  privilege: string;
  isCollapse: boolean;
  label: string;
  attributes: {
    type:
      | "default"
      | "success"
      | "warning"
      | "info"
      | "text"
      | "primary"
      | "danger";
    [x: string]: any;
  };
  resourceId: string;
}
export type MenuButtons = MenuButton[];

export interface TableConfig {
  columns: Columns;
  actions: MenuButtons;
}

export interface SearchItem {
  elementType: string;
  span: number;
  label: string;
  optionKey?: string;
  paramsKey: string;
  defaultValue: string | null | number | Array<string | null | number>;
  className?: string;
  props: {
    [x: string]: any;
  };
}

export type SearchItems = SearchItem[];

export interface TablePageProps {
  tableColumns: Columns;
  api: string; // Strapi，自动生成findListApi,findOne,deleteApi,createApi,updateApi,在servece>api>strapi中
  pageButtons: MenuButtons; // 页面按钮
  tableActions: MenuButtons; // 表格按钮
  resourceId: string; // 权限的resourceId
  promptColumns: PromptColumns; // 展示在提示弹窗中的Columns
  searchItems: SearchItems; // 筛选项
  noCacheQuery?: boolean; // 是否记住并回填筛选
  immediate?: boolean; // 是否在页面初始化加载表格数据
  handleDynamicButton?: Function; // 处理表格动态按钮，按钮需要跟随数据变化时使用，return Boolean
  beforeBatchDeletion?: Function; // 删除前调用，return Boolean
  defaultSearchParams?: { [x: string]: any }; // Strapi规则的默认筛选
  exportConfig?: {
    // 导出配置
    url: string; // 导出url
    fileName: string; // 文件名称
    header?: string; // 导出窗的标题
    subheader?: string; // 导出弹窗的副标题
  };
}
