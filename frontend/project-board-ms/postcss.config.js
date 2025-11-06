// eslint-disable-next-line no-undef
module.exports = {
  plugins: {
    autoprefixer: {
      overrideBrowserslist: [
        'Android 4.1',
        'iOS 7.1',
        'Chrome > 31',
        'ff > 31',
        'ie >= 8',
        'last 10 versions',
      ],
      grid: true,
    },
    // 'postcss-pxtorem': {
    //   rootValue: 192,
    //   minPixelValue: 3,
    //   mediaQuery: true,
    //   // propList: ['font-size', '--el-font-size-*'], // 只转换 font-size 属性
    //   // ignoreProperties: ['width', 'height'], // 忽略这些属性
    //   propList: ['*', '!border'],
    //   selectorBlackList: ['.sort-caret', '.caret-wrapper'],
    // },
  },
};
