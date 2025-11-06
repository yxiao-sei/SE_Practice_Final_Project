import XEUtils from "xe-utils";
import { type App } from "vue";
import "./index.scss";
import { useFormats, emptyRendererValue as _emptyRendererValue } from "./modules/formats";
import { useRenderer } from "./modules/renderer";
import {
  // 全局实例对象
  VXETable,
  Edit,

  // 可选表格模块
  VxeTableFilterModule,
  // VxeTableEditModule,
  VxeTableMenuModule,
  // VxeTableExportModule,
  VxeTableKeyboardModule,
  // VxeTableValidatorModule,
  VxeTableCustomModule,

  // 可选组件
  VxeIcon,
  VxeTable,
  VxeColumn,
  VxeColgroup,
  VxeGrid,
  VxeTooltip,
  VxeToolbar,
  VxePager,
  VxeForm,
  VxeFormItem,
  // VxeFormGather,
  // VxeCheckbox,
  // VxeCheckboxGroup,
  // VxeRadio,
  // VxeRadioGroup,
  // VxeRadioButton,
  // VxeSwitch,
  VxeInput,
  VxeSelect,
  // VxeOptgroup,
  VxeOption,
  // VxeTextarea,
  // VxeButton,
  // VxeButtonGroup,
  // VxeModal,
  // VxeList,
  // VxePulldown
} from "vxe-table";
import zhCN from "vxe-table/es/locale/lang/zh-CN";
VXETable.setConfig({
  i18n: (key, args) => XEUtils.toFormatString(XEUtils.get(zhCN, key), args),
  theme: 'default'
});

useFormats(VXETable);
useRenderer(VXETable);

export const emptyRendererValue = _emptyRendererValue;
export { globalEmits, findParentPath, triggerEmit } from "./modules/command";
export function useTable(app: App) {
  // 可选表格模块
  app.use(VxeTableFilterModule);
  // app.use(VxeTableEditModule)
  app.use(VxeTableMenuModule);
  // app.use(VxeTableExportModule)
  app.use(VxeTableKeyboardModule);
  // app.use(VxeTableValidatorModule)
  app.use(VxeTableCustomModule);

  // 可选组件
  app.use(VxeIcon);
  app.use(VxeTable);
  app.use(Edit);
  app.use(VxeColumn);
  app.use(VxeColgroup);
  app.use(VxeGrid);
  app.use(VxeTooltip);
  app.use(VxeToolbar);
  app.use(VxePager);
  app.use(VxeForm);
  app.use(VxeFormItem);
  // app.use(VxeFormGather)
  // app.use(VxeCheckbox)
  // app.use(VxeCheckboxGroup)
  // app.use(VxeRadio)
  // app.use(VxeRadioGroup)
  // app.use(VxeRadioButton)
  // app.use(VxeSwitch)
  app.use(VxeInput);
  app.use(VxeSelect);
  // app.use(VxeOptgroup)
  app.use(VxeOption);
  // app.use(VxeTextarea)
  // app.use(VxeButton)
  // app.use(VxeButtonGroup)
  // app.use(VxeModal)
  // app.use(VxeList)
  // app.use(VxePulldown)
}
