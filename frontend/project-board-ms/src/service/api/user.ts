import { type User } from "@/model/user";
import { Get, Post, Put } from "@/service";
import { useCacheUserInfoStore } from "@/stores";
import { ElMessageBox } from "element-plus";

const generateChartCode = () => {
  const characters = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789";
  let captchaCode = "";
  for (let i = 0; i < 4; i++) {
    const randomIndex = Math.floor(Math.random() * characters.length);
    const randomChar = characters[randomIndex];
    const randomCase =
      Math.random() < 0.5 ? randomChar.toLowerCase() : randomChar.toUpperCase();
    captchaCode += randomCase;
  }
  return captchaCode;
};

const baseURL = "/project-board-api/board/auth";

/**
 * 获取挑战令牌的 API 方法。
 * @returns 返回一个 Promise，该 Promise 在成功时解析为挑战令牌，失败时拒绝为错误对象。
 */
export const getChallengeTokenApi = (options = {}) => {
  return Get(
    {
      url: `${baseURL}/oauth/challenge`,
    },
    {
      loading: false,
      successMessageShow: false,
      errorMessageShow: true,
      codeMessageShow: true,
      ...options,
    }
  );
};

/**
 * 获取验证码接口
 * @returns 返回一个 Promise 对象，包含 result 字段
 */
export const getCaptchaCodeApi = (): Promise<{ result: any }> => {
  return new Promise((resolve) => {
    resolve({ result: generateChartCode() });
  });
  // return Get({
  //   url: `${baseURL}/user/captcha`,
  // });
};

/**
 * 使用 OAuth 登录的 API 方法。
 * @returns 返回一个 Promise，该 Promise 在成功时解析为登录结果，失败时拒绝为错误对象。
 */
export const loginByOauthApi = (options = {}) => {
  return Post(
    {
      url: `${baseURL}/oauth/login/cas`,
      data: {
        client: "project-board-ms"
      }
    },
    {
      loading: false,
      codeMessageShow: true,
      successMessageShow: false,
      reductDataFormat: false,
      ...options,
    }
  );
};

/**
 * 注销登录的 API 方法。
 * @returns 返回一个 Promise，该 Promise 在成功时解析为注销结果，失败时拒绝为错误对象。
 */
export const logoutApi = () => {
  return Post(
    {
      url: "/project-board-api/board/auth/oauth/logout",
      data: {},
    },
    { loading: false, codeMessageShow: true, reductDataFormat: false }
  );
};

/**
 * 获取角色的 API 方法。
 * @returns 返回一个 Promise，该 Promise 在成功时解析为办公室角色列表，失败时拒绝为错误对象。
 */
export const getOfficeRolesApi = () => {
  return Get(
    {
      url: "/project-board-api/board/app/config/role/management",
    },
    { loading: false }
  );
};

/**
 * 获取院系角色的 API 方法。
 * @returns 返回一个 Promise，该 Promise 在成功时解析为办公室角色列表，失败时拒绝为错误对象。
 */
export const getTeacherRoles = () => {
  return Get({
    url: "/project-board-api/board/app/config/role/teacher",
  });
};
/**
 * 获取院系角色的 API 方法。
 * @returns 返回一个 Promise，该 Promise 在成功时解析为办公室角色列表，失败时拒绝为错误对象。
 */
export const getStudentRoles = () => {
  return Get({
    url: "/project-board-api/board/app/config/role/student",
  });
};

/**
 * 使用用户名和密码登录的 API 方法。
 * @param data 登录信息对象，包含用户名和密码。
 * @returns 返回一个 Promise，该 Promise 在成功时解析为登录结果，失败时拒绝为错误对象。
 */
export const loginApi = (data: any) => {
  return Put(
    {
      url: `${baseURL}/oauth/login`,
      data,
    },
    { loading: false, codeMessageShow: true, reductDataFormat: false },
    { target: "body" }
  );
};

/**
 * 获取用户信息的 API 方法。
 * @returns 返回一个 Promise，该 Promise 在成功时解析为用户信息对象，失败时拒绝为错误对象。
 */
export const getUserApi = (options = {}) => {
  // eslint-disable-next-line no-async-promise-executor
  return new Promise(async (resolve, reject) => {
    const cacheInfoStore = useCacheUserInfoStore();
    if (!cacheInfoStore.access_token) {
      const tokenResult = await getChallengeTokenApi({
        errorMessageShow: true,
        codeMessageShow: true,
        ...options,
      });
      if (tokenResult.code) {
        return reject(tokenResult);
      }
      cacheInfoStore.setToken(tokenResult);
    }
    Get<User>(
      {
        url: "/project-board-api/board/app/user/principal",
      },
      { loading: false, codeMessageShow: false, ...options },
      { target: "body" }
    )
      .then((res: any) => {
        const roles = res.result.details.roles;
        if (roles.includes('ROLE_SEI_STUDENT') || roles.includes('ROLE_SEI_GUEST')) {
          window.sessionStorage.clear()
          reject(null);
          return ElMessageBox.confirm(
            '暂未开放学生访问管理后台，请勿操作。',
            '提示：',
            {
              confirmButtonText: '退出页面',
              showCancelButton: false,
              type: 'warning',
              showClose:false
            }
          )
            .then(() => {
              if (
                navigator.userAgent.indexOf("Firefox") != -1 ||
                navigator.userAgent.indexOf("Chrome") != -1
              ) {
                window.location.href = "about:blank";
                window.close();
              } else {
                window.opener = null;
                window.open("", "_self");
                window.close();
              }
            })
            .catch(() => { })
        }
        cacheInfoStore.setUserInfo({
          ...(res.result.details || {}),
          departmentWithRoles: res.result.departmentWithRoles,
        });
        if (roles.includes('ROLE_SEI_ADMIN') || roles.includes('ROLE_SEI_SYSTEM_ADMIN')) {
          res.result.authorities = {
            'ROLE_USER': {
              read: 1
            },
            'ROLE_SETTINGS': {
              read: 1
            }
          }
        }
        resolve(res);
      })
      .catch((err) => {
        reject(err);
      });
  });
};

/**
 * 新增绩效规则的方法 API 方法。
 * @returns 返回一个 Promise，该 Promise 在成功时解析为办公室角色列表，失败时拒绝为错误对象。
 */
export const addRulSchemeApi = (data: any) => {
  return Post(
    {
      url: "/project-board-api/board/app/performanceSetting",
      data,
    },
    { loading: false }
  );
};

export const addAdministratorsBatchApi = (data: any) => {
  return Post(
    {
      url: `/project-board-api/board/app/user/batch`,
      data,
    },
    { loading: false, codeMessageShow: true, reductDataFormat: false },
  );
};
