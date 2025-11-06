/* eslint-env node */

module.exports = {
  plugins: {
    'postcss-preset-env': {
      browsers: ['last 2 versions', 'ie >= 11'],
    },
    autoprefixer: {
      overrideBrowserslist: [
        'Android 4.1',
        'iOS 7.1',
        'Chrome > 31',
        'ff > 31',
        'ie >= 11',
        'last 10 versions',
      ],
      grid: true,
    },
    // 'postcss-pxtorem': {
    //   rootValue: 37.5,
    //   mediaQuery: true,
    //   propList: ['*'], // 仅转换需要的属性
    // },
    tailwindcss: {},
    ...(process.env.NODE_ENV === 'production'
      ? {
        cssnano: {
          preset: [
            'default',
            {
              discardComments: { removeAll: true },
              mergeLonghand: false,
            },
          ],
        },
      }
      : {}),
  },
};
