function setFontSize(baseSize: number) {
  // const enlargementFactor = Object.hasOwnProperty.call(
  //   localStorage,
  //   "_enlargementFactor",
  // ) ? Number(localStorage.getItem("_enlargementFactor"))
  //   : 0;
  let scale =
    (document.documentElement.clientWidth <= 1080
      ? 1080
      : document.documentElement.clientWidth) / 1920;
  // 设置页面根节点字体大小，最高放大比例为2）
  document.documentElement.style.fontSize =
    baseSize * (Math.min(scale, 2)) + "px";
}

export default function initRem(baseSize = 192) {
  if (!baseSize) {
    return;
  }
  // 初始化
  setFontSize(baseSize);

  // 监听窗口变化
  window.onresize = function () {
    setFontSize(baseSize);
  };
}
