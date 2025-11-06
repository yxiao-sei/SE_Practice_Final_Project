import { getHeightOverflowByVirtualDom } from '@/utils';
import { ref, onMounted, onBeforeUnmount } from 'vue';

export const useResponsiveModal = () => {
  const VirtualHeight = 574;
  const isCrossrange = ref(false);
  const updateOverFlow = () => {
    isCrossrange.value = getHeightOverflowByVirtualDom(VirtualHeight);
  };

  onMounted(() => {
    updateOverFlow();
    window.addEventListener('resize', () => {
      updateOverFlow();
    });
  });

  onBeforeUnmount(() => {
    window.removeEventListener('resize', () => {
      updateOverFlow();
    });
  });

  return {
    isCrossrange,
  };
};
