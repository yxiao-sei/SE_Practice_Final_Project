/** @type {import('tailwindcss').Config} */

const colors = require('tailwindcss/colors')
const config = {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        white: colors.white,
        gray: {
          '100': '#CFDAE5',
          '200': 'rgba(0, 169, 222, 0.16)',
        },
        green: {
          '100': '#04A80F',
          '200': '#00A9DE'
        },
        blue: {
          '100': '#003FDE',
          '200': "#9FACCF",
          '400': 'rgba(159, 172, 207, .16)',
          '700': '#45C656',
        },
        primary: {
          '100': '#3D4348',
          '200': '#021C5D',
          '300': '#405078',
          '400': '#606F92',
        }
      },
      fontSize: {
        base: ['13px', '14px'],
        sm: ['14px', '19px'],
        lg: ['16px', '21px'],
        xl: ['19px', '26px']
      },
      screens: {
        'sm': '640PX',
        'md': '800PX', // 修改了md断点
        'lg': '1024PX', // 修改了lg断点
        'xl': '1280PX',
        '2xl': '1536PX',
      },
    },
  },
  plugins: []
};
export default config;
