import { ref } from 'vue';

export const useWatcher = () => {
  const timer = ref();

  const openWatcher = (fn: Function, delay: number) => {
    timer.value = setInterval(() => {
      fn();
    }, delay);
  };

  const closeWatcher = () => {
    clearInterval(timer.value);
  };

  return {
    openWatcher,
    closeWatcher,
  };
};
