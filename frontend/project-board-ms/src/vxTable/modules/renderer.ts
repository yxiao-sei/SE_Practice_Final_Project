import { h } from "vue";
import { formats, emptyRendererValue } from "./formats";
import { triggerEmit } from "./command";

const isEmpty = (cellValue: any) => {
  return typeof cellValue === "number"
    ? false
    : typeof cellValue === "boolean"
      ? false
      : !cellValue;
};

export const useRenderer = (VXETable: any) => {
  VXETable.renderer.add("hyperlink", {
    renderDefault(renderOpts: any, params: any) {
      const { command } = renderOpts;
      const { row, column } = params;
      const formatterValue = column.formatter
        ? Array.isArray(column.formatter)
          ? formats[column.formatter[0] as string](
            { cellValue: row[column.field] },
            column.formatter[1]
          )
          : formats[column.formatter as string]({
            cellValue: row[column.field],
          })
        : row[column.field];
      if (formatterValue === emptyRendererValue) return formatterValue;
      return h(
        "span",
        {
          class: "hyperlink",
          onClick: () =>
            triggerEmit(command, { row, column }),
        },
        formatterValue
      );
    },
  });
}