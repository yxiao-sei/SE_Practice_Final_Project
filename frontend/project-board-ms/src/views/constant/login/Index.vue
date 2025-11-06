<template>
  <el-container class="login">
    <el-header>
      <primary-header :readonly="true"></primary-header>
    </el-header>
    <el-container class="container" v-if="allowManualLogin">
      <div class="login_card">
        <p class="title">登录</p>
        <template v-if="pageStatus === 'initial'">
          <div class="logo_img">
            <img src="../../../assets/images/logo.png" alt="" />
          </div>
          <el-button class="login_btn" type="primary" :loading="oauthLoading" @click="loginByOauth" size="large">{{
        casText }}</el-button>
          <el-button class="login_btn_text" :disabled="oauthLoading" type="primary" @click="handleLoginTypeChange" link
            size="large">
            使用系统专用账号登录
            <!-- 或企业微信扫码 -->
          </el-button>
        </template>
        <template v-else-if="pageStatus === 'costing'">
          <el-tabs v-model="activeName" class="login_tabs">
            <!-- <el-tab-pane label="企业微信扫码登录" name="first">暂未开放</el-tab-pane> -->
            <el-tab-pane label="系统专用账号登录" name="second" class="password_panel">
              <!-- <p>常规用户请点击右上角“前往ECNU统一认证”登录系统</p> -->
              <el-form :rules="rules" class="login-form" ref="ruleFormEl" label-position="top" :model="ruleForm"
                hide-required-asterisk @keyup.enter.prevent="login">
                <el-form-item label="用户名" prop="username">
                  <el-input size="large" v-model="ruleForm.username" clearable placeholder="请输入" />
                </el-form-item>
                <el-form-item label="密码" prop="password">
                  <el-input size="large" show-password v-model="ruleForm.password" clearable placeholder="请输入" />
                </el-form-item>
                <el-row :gutter="20">
                  <el-col :span="14">
                    <el-form-item label="验证码" prop="code">
                      <el-input size="large" v-model="ruleForm.code" clearable placeholder="请输入" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="10" class="code-container">
                    <hl-code :identifyCode="identifyCode" @refreshCode="refreshCode" />
                  </el-col>
                </el-row>
              </el-form>
              <el-button style="width: 100%" :loading="formLoading" type="primary" @click="login"
                size="large">登录</el-button>
            </el-tab-pane>
          </el-tabs>
        </template>
        <div class="version">
          <span> Powered by HQIT ! &nbsp; </span>
          {{ pckJson.version }}
        </div>
        <div class="quick-cas">
          <el-button v-show="pageStatus === 'costing'" :loading="oauthLoading" @click="loginByOauth" size="large"
            type="primary">{{ casText }}</el-button>
        </div>
      </div>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { md5 } from 'js-md5';
import { useRouter } from 'vue-router';
import { useLogin } from '@/hooks/login';
import hlCode from '~/identifyCode/Index.vue';
import pckJson from '../../../../package.json';
import { useCacheUserInfoStore } from '@/stores';
import { nextTick, onMounted, reactive, ref, onBeforeMount } from 'vue';
import { useValidateForm } from '@/hooks/useValidateForm';
import primaryHeader from '@/layout/components/primaryHeader.vue';
import { getCaptchaCodeApi, loginApi, loginByOauthApi } from '@/service/api/user';

const allowManualLogin = window.Public.ALLOW_MANUAL_LOGIN;
const casText = '前往ECNU统一认证';
const pageStatus = ref('initial');
const activeName = ref('second');
const router = useRouter();
const identifyCode = ref();
const refreshCode = () => {
  getCaptchaCodeApi().then((res) => {
    identifyCode.value = res.result;
  });
};
const { ruleForm, ruleFormEl, validateForm, resetForm } = useValidateForm({
  username: '',
  password: '',
  code: '',
});
const { setToken } = useCacheUserInfoStore();
const availableCode = (rule: any, value: string, callback: (arg0?: Error | undefined) => void) => {
  if (value && value.toUpperCase() !== identifyCode.value.toUpperCase()) {
    callback(new Error('验证码错误'));
  } else {
    callback();
  }
};
// , { validator: availableNumber, trigger: "blur" }
const rules = reactive<any>({
  password: [{ required: true, message: '请输入', trigger: 'blur' }],
  code: [
    { required: true, message: '请输入', trigger: 'blur' },
    { validator: availableCode, trigger: 'blur' },
  ],
  username: [{ required: true, message: '请输入', trigger: 'blur' }],
});

const formLoading = ref(false);
const login = async () => {
  try {
    await validateForm();
    formLoading.value = true;
    const data = {
      username: ruleForm.username,
      password: md5(ruleForm.password),
      code: ruleForm.code,
    };
    loginApi(data)
      .then((res) => {
        if (res.code === 0) {
          setToken(res.result);
          router.push('/layout');
        } else {
          refreshCode();
        }
        formLoading.value = false;
      })
      .catch(() => {
        formLoading.value = false;
        refreshCode();
      });
  } catch (error) {
    console.log(error);
  }
};

const oauthLoading = ref(false);

const loginByOauth = () => {
  oauthLoading.value = true;
  loginByOauthApi({ loading: false })
    .then((res) => {
      if (res.code === 0) {
        window.open(res.result, '_self');
      }
    })
    .finally(() => (oauthLoading.value = false));
};

const handleLoginTypeChange = async () => {
  pageStatus.value = 'costing';
  await nextTick();
  resetForm();
  refreshCode();
};

onBeforeMount(() => {
  try {
    const { loginUtil } = useLogin();
    loginUtil();
  } catch (error) {
    console.log(error);
  }
});
</script>

<style scoped lang="scss">
:global(.login .el-header) {
  --el-header-padding: 0 0 0 100px;
}

:global(.login-form .el-form-item) {
  --el-text-color-regular: #797979;
  --el-form-label-font-size: var(--el-font-size-base);
  line-height: 2;
  // margin-bottom: 10px;

  .el-form-item__error--inline {
    margin-left: 0;
  }
}

:global(.login-form .el-form-item .el-form-item__label) {
  color: var(--el-color-primary);
}

.container {
  background: url('../../../assets/images/login_bg.png');
  background-repeat: no-repeat;
  background-size: cover;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login_card {
  width: 536px;
  height: 700px;
  min-width: 331px;
  background: white;
  padding: 80px 80px 80px 80px;
  position: relative;
  display: flex;
  flex-direction: column;
}

.quick-cas {
  position: absolute;
  top: 0;
  right: 0;
  color: #fff;
  background-color: var(--el-color-primary);
  border-radius: 0;

  .el-button {
    --el-button-size: 60px;
    --el-font-size-base: var(--el-font-size-medium);
  }
}

.title {
  text-align: left;
  font-size: 36px;
  letter-spacing: 5px;
  color: var(--el-color-primary);
  font-weight: bold;
  margin-bottom: 20px;
}

.logo_img {
  text-align: center;
  margin: 50px 0;

  img {
    width: 145px;
    height: 145px;
  }
}

.login_btn {
  width: 100%;
  margin-bottom: 20px;
}

.login_btn_text {
  width: 100%;
}

.version {
  position: absolute;
  width: 100%;
  color: rgba(207, 218, 229, 1);
  left: 0;
  bottom: 20px;
  text-align: center;

  span {
    font-style: italic;
  }
}

.tab-container {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.login_tabs,
:global(.login_tabs .el-tabs__content),
.password_panel {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

.password_panel {
  justify-content: space-between;
}

.code-container {
  margin-top: 30px;
}

.login {
  --el-color-primary: #b70031;
  --el-color-danger: #b70031;
  --el-text-color-primary: #b70031;
  --el-color-primary-light-3: #cd4d6f;
  --el-color-primary-light-5: #db8098;
  --el-color-primary-light-7: #e9b3c1;
  --el-color-primary-light-8: #f1ccd6;
  --el-color-primary-light-9: #f8e6ea;
}
</style>
