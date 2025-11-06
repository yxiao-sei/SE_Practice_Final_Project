export const replaceTemplateValue = (template: string, data: any) => {
  const regex = /\{\{([^}]+)\}\}/g;
  let result = template;
  let match;
  while ((match = regex.exec(template)) !== null) {
    const variableName = match[1].trim();
    const replacement = data[variableName];
    if (replacement !== undefined) {
      result = result.replace(match[0], replacement);
    }
  };
  return result;
};

export const queryStringToObject = (queryString: string, row?: any) => {
  const newString = row ? replaceTemplateValue(queryString, row) : queryString;
  const searchParams = new URLSearchParams(newString);
  const route = { name: "", menuName: "" };
  const params = {};
  for (const [key, value] of searchParams.entries()) {
    if (key === "_name") {
      route.name = value;
    } else if (key === "_menuName") {
      route.menuName = value;
    } else {
      params[key] = value;
    }
  };
  return { route, params };
}