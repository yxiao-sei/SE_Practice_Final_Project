export interface EditRender {
  span: number,
  label: string,
  element: [string, { [x: string]: any }],
  enabled: boolean | 'remote',
  formItem?: { name: string, config: any },
  updateKeys: string[],
  defaultValue: string,
  options?: { label: string, value: string }[]
}