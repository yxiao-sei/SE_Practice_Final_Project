<template>
  <canvas ref="canvasInstance" :width="commentsTrulyValue().contentWidth" :height="commentsTrulyValue().contentHeight"
    @click="refreshCode"></canvas>
</template>

<script setup lang="ts">
import { debounce } from 'hqit-plugin-plus';
import { toRefs, ref, watch, onMounted, nextTick, onUnmounted } from 'vue';
const props = defineProps({
  identifyCode: {
    type: String,
    default: '1234',
  },
  fontSizeMin: {
    type: Number,
    default: 40,
  },
  fontSizeMax: {
    type: Number,
    default: 40,
  },
  backgroundColorMin: {
    type: Number,
    default: 180,
  },
  backgroundColorMax: {
    type: Number,
    default: 240,
  },
  colorMin: {
    type: Number,
    default: 50,
  },
  colorMax: {
    type: Number,
    default: 160,
  },
  lineColorMin: {
    type: Number,
    default: 40,
  },
  lineColorMax: {
    type: Number,
    default: 180,
  },
  contentWidth: {
    type: Number,
    default: 150,
  },
  contentHeight: {
    type: Number,
    default: 40,
  },
  rootValue: {
    type: Number,
    default: 192,
  },
});

const transformTrulyValue = (value: number) => {
  const rootSize = Number(document.documentElement.style.fontSize.replace('px', ''));
  return Number(((value / rootValue.value) * rootSize).toFixed(2));
};

const {
  identifyCode,
  fontSizeMin,
  fontSizeMax,
  backgroundColorMin,
  backgroundColorMax,
  colorMin,
  colorMax,
  lineColorMin,
  lineColorMax,
  contentWidth,
  contentHeight,
  rootValue,
} = toRefs(props);

const commentsTrulyValue = () => {
  return {
    fontSizeMin: transformTrulyValue(fontSizeMin.value),
    fontSizeMax: transformTrulyValue(fontSizeMax.value),
    contentHeight: transformTrulyValue(contentHeight.value),
    contentWidth: transformTrulyValue(contentWidth.value),
  };
};

const emits = defineEmits(['refreshCode']);
const refreshCode = () => {
  emits('refreshCode');
};
const randomNum = (min: number, max: number) => {
  return Math.floor(Math.random() * (max - min) + min);
};
const randomColor = (min: number, max: number) => {
  let r = randomNum(min, max);
  let g = randomNum(min, max);
  let b = randomNum(min, max);
  return `rgb(${r},${g},${b})`;
};
const drawArc = (
  ctx: any,
  options: { fontSizeMin?: number; fontSizeMax?: number; contentHeight: any; contentWidth: any }
) => {
  ctx.beginPath();
  ctx.fillStyle = randomColor(0, 255);
  ctx.arc(
    randomNum(0, options.contentWidth),
    randomNum(0, options.contentHeight),
    1,
    0,
    2 * Math.PI
  );
  ctx.fill();
};
const drawLine = (
  ctx: any,
  options: { fontSizeMin?: number; fontSizeMax?: number; contentHeight: any; contentWidth: any }
) => {
  ctx.strokeStyle = randomColor(lineColorMin.value, lineColorMax.value);
  ctx.beginPath();
  ctx.moveTo(randomNum(0, options.contentWidth), randomNum(0, options.contentHeight));
  ctx.lineTo(randomNum(0, options.contentWidth), randomNum(0, options.contentHeight));
  ctx.stroke();
};

const drawText = (
  ctx: any,
  text: string,
  index: number,
  options: { fontSizeMin: any; fontSizeMax: any; contentHeight: any; contentWidth: any }
) => {
  ctx.fillStyle = randomColor(colorMin.value, colorMax.value);
  ctx.font = randomNum(options.fontSizeMin, options.fontSizeMax) + 'px SimHei';
  ctx.font = fontSizeMax + 'px SimHei';
  const x = (index + 1) * (options.contentWidth / (identifyCode.value.length + 1.6));
  // var y = randomNum(fontSizeMax, contentHeight - 5);
  // var deg = randomNum(-5, 5);
  // 修改坐标原点和旋转角度
  // ctx.translate(x, y);
  // ctx.rotate((deg * Math.PI) / 180);
  ctx.fillText(text, x, options.contentHeight - 5);
};
const canvasInstance = ref();
const drawPic = async () => {
  const options = commentsTrulyValue();
  let canvas = canvasInstance.value;
  canvas.height = options.contentHeight;
  canvas.width = options.contentWidth;
  await nextTick();
  let ctx = canvas.getContext('2d');
  ctx.fillStyle = randomColor(backgroundColorMin.value, backgroundColorMax.value);
  ctx.fillRect(0, 0, options.contentWidth, options.contentHeight);
  identifyCode.value.split('').forEach((text, index) => {
    drawText(ctx, text, index, options);
  });
  for (let i = 0; i < 5; i++) {
    drawLine(ctx, options);
  }
  for (let i = 0; i < 50; i++) {
    drawArc(ctx, options);
  }
};

const debounceDrawPic = debounce(drawPic, 500);
watch(identifyCode, () => {
  drawPic();
});

onMounted(() => {
  window.addEventListener('resize', debounceDrawPic);
});

onUnmounted(() => {
  window.removeEventListener('resize', debounceDrawPic);
});
</script>

<style scoped></style>
