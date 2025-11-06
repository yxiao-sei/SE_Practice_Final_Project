import router from '@/router';
import { loginByOauthApi, getUserApi } from '@/service/api/user';
import { useBaseFieldStore, useCacheUserInfoStore } from '@/stores';

export const useLogin = () => {
  const { path } = router.currentRoute.value;
  const allowManualLogin = window.Public.ALLOW_MANUAL_LOGIN;
  const { setUserInfo } = useCacheUserInfoStore();
  const clearCacheState = () => {
    try {
      useBaseFieldStore().$reset();
      useCacheUserInfoStore().$reset();
      sessionStorage.removeItem('authorities');
      sessionStorage.removeItem('_store_user');
    } catch (e) {
      // eslint-disable-next-line no-console
      console.log(e);
    }
  };

  const getUserInfo = () => {
    getUserApi({ errorMessageShow: false, codeMessageShow: false, loading: true }).then(
      (res: any) => {
        if (res.code === 0) {
          router.push('/home');
        }
      },
    );
  };

  const loginUtil = () => {
    clearCacheState();
    if (allowManualLogin && path === '/login') {
      getUserInfo();
    } else if (allowManualLogin) {
      router.push({ path: '/login', replace: true });
    } else {
      loginByOauthApi({ loading: true }).then((res) => {
        if (res.code === 0) {
          clearCacheState();
          window.open(res.result, '_self');
        }
      });
    }
  };

  return {
    loginUtil,
  };
};
