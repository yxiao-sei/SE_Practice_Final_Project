import { ElMessage } from 'element-plus';
declare module '@vue/runtime-core' {
  interface ComponentCustomProperties {
    $message: ElMessage;
  }
}

declare module 'vue-router' {
  interface RouteMeta {
    roles?: Array<{ key: string; value: string }>; //权限控制
    isMenu?: boolean; //控制是否为菜单
    icon?: string; //菜单图标
    menuName?: string; //菜单名称
    order?: number; //菜单顺序
    level?: number; //指定高亮的菜单
    isLocal?: boolean
  }
}

declare global {
  interface Window {
    Public: any; // 或者具体的 YJSYXT 类型声明
  }
}
