/* eslint-env node */
require('@rushstack/eslint-patch/modern-module-resolution');

module.exports = {
  parser: '@typescript-eslint/parser',
  extends: [
    'plugin:vue/vue3-recommended',
    'eslint:recommended',
    'eslint-config-ali/typescript/vue',
  ],
  rules: {
    'vue/multi-word-component-names': 'off',
  },
  plugins: ['@typescript-eslint'],
  parserOptions: {
    ecmaVersion: 'latest',
    project: './tsconfig.app.json',
  },
};
