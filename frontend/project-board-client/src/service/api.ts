import { Post, Get } from './service'
import { showLoadingToast } from 'vant'
import { useUserInfo } from '@/stores/cacheInfo'
import router from '@/router'
import { showDialog } from 'vant'
export function loginByOauthApi(): Promise<string> {
  return Post({ url: '/project-board-api/board/auth/oauth/login/cas', data: { client: "project-board-client" } })
}
export function getUserPrincipalApi() {
  return Get({ url: '/project-board-api/board/app/user/principal' })
}
export function postChallengeApi() {
  return Post({ url: '/project-board-api/board/auth/oauth/challenge' }, { codeMessageShow: false })
}
export function getTokenApi() {
  return Get({ url: '/project-board-api/board/auth/oauth/challenge' }, { codeMessageShow: false });
}
export const getTeacherRolesApi = () => {
  return Get({ url: "/project-board-api/board/app/config/role/teacher" }, { loading: false });
};
export async function loginApi() {
  const { setToken, reset, setLoginTime, loginTime } = useUserInfo();
  if (loginTime >= 2) {
    setLoginTime(0);
    return showDialog({
      title: '提示',
      message: '获取应用权限失败，请联系管理员，或者点击确定重试！',
    }).then(() => {
      loginApi();
    })
  } else {
    setLoginTime(loginTime + 1);
  }
  const instance = showLoadingToast({
    duration: 0, // 持续展示 toast
    forbidClick: true,
  });
  try {
    const getTokenResult = await getTokenApi()
    if (getTokenResult.code === 0) {
      setToken(getTokenResult.result);
      router.go(0);
      setLoginTime(0);
    } else {
      const loginUrl = await loginByOauthApi()
      reset();
      if (process.env.NODE_ENV === 'production') {
        window.open(loginUrl, '_self');
      }
    }
    instance.close();
  } catch (error) {
    instance.close();
    setLoginTime(0);
  }
}
