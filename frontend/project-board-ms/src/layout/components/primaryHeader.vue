<template>
<div class="primaryHeader">
  <!-- <img :src="longLogo" class="logoImg" alt="logo" /> -->
  <!-- <img :src="logoImg" class="logoImg" alt="logo" /> -->
  <div class="logo-text">
    <span class="cn">{{ Lang.SystemTitle }}</span>
    <span class="en">Software Engineering Institute</span>
  </div>
  <div class="logo-text">
    <span class="cn">{{ Lang.SystemSubTitle }}</span>
    <span class="en"></span>
  </div>
  <div class="icon-cursor" v-if="!props.readonly">
    <user-card />
  </div>
</div>
<el-drawer v-model="drawerVisible" size="25%" @close="closeDrawer" title="I am the title" :with-header="false">
  <div class="flex-container">
    <div class="header">
      <span class="title">消息通知</span>
      <span class="num">{{ messageCount }}</span>
    </div>
    <el-row>
      <el-col :span="10">
        <search-block label="显示范围">
          <el-select size="large" v-model="searchInfo.ackStatus" @change="refreshList" clearable placeholder="请选择">
            <el-option label="未读通知" value="unread" />
            <el-option label="已读通知" value="read" />
          </el-select>
        </search-block>
      </el-col>
      <el-col :span="14" :align="'right'" style="display: flex; align-items: flex-end; justify-content: flex-end">
        <el-dropdown :disabled="!list || !list.length" size="large" @command="handleCommand($event)"
          @click="handleCommand(actions[0])" split-button class="info-button-dropdown" type="info"
          placement="bottom-end">
          {{ actions[0] }}
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item :command="command" v-for="command in actions.slice(1, actions.length)" :key="command">{{
                command }}</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-col>
    </el-row>
    <div class="infinite-list-wrapper" style="overflow: auto">
      <el-scrollbar>
        <ul :infinite-scroll-distance="50" v-infinite-scroll="load" class="list" :infinite-scroll-disabled="disabled">
          <li v-for="i in list" :key="i" class="list-item">
            <div class="flex-header">
              <span>{{ i.title }}</span>
              <span style="font-weight: normal; color: #ced2d6">{{ i.state }}</span>
              <el-popconfirm @confirm="deleteMessage(i.id)" width="220" confirm-button-text="确定" cancel-button-text="取消"
                title="删除消息确认?">
                <template #reference>
                  <el-icon class="closeIcon" color="var(--el-color-primary)">
                    <CloseBold />
                  </el-icon>
                </template>
              </el-popconfirm>
            </div>
            <p class="time">{{ i.lastUpdateTime }}</p>
            <div class="bottom">
              <el-popover placement="bottom" effect="light" width="25%" trigger="hover" :content="i.content">
                <template #reference>
                  <span @click="toMessageDetail(i)">{{ i.content }}</span>
                </template>
              </el-popover>
              <span v-show="i.directUrl" @click="toDirectUrl(i)"
                style="color: var(--el-color-primary); cursor: pointer">前往查看 <svg-icon icon-name="icon-a-lujing724"
                  :size="14"></svg-icon></span>
            </div>
            <el-divider />
          </li>
        </ul>
        <p v-if="loading">
          <el-icon class="loading-icon" :size="24" color="#889199">
            <Loading />
          </el-icon>
        </p>
        <p v-if="noMore" style="color: #889199; font-size: var(--el-font-size-base)">
          暂无更多消息
        </p>
      </el-scrollbar>
    </div>
  </div>
</el-drawer>
</template>

<script lang="ts" setup>
import UserCard from '@/components/userCard/Index.vue';
import longLogo from '@/assets/images/long-logo.svg'
import {
  deleteAllMsgApi,
  deleteMessageApi,
  getMessagePollListApi,
  readAllMsgApi,
  readMessageApi,
} from '@/service/api/message';
import { parseTime } from '@/utils';
import { CloseBold, Loading } from '@element-plus/icons-vue';
import { computed, reactive, ref } from 'vue';
import Lang from '@/locales'

const props = defineProps(['readonly']);
const actions = ['标记全部为已读', '删除已读消息'];
const list = ref();
const total = ref(0);
const loading = ref(false);
const drawerVisible = ref(false);
const searchInfo = reactive({
  page: 0,
  size: 0,
  pushMethods: 'system',
  ackStatus: 'unread',
});
const noMore = computed(() => searchInfo.size >= total.value);
const disabled = computed(() => loading.value || noMore.value);

const refreshList = () => {
  searchInfo.size = searchInfo.size - 10 || 0;
  load();
};

const readAllMessage = () => {
  readAllMsgApi().then(() => {
    searchInfo.size = 0;
    load();
  });
};

const deleteAllMessage = () => {
  deleteAllMsgApi().then(() => {
    searchInfo.size = 0;
    load();
  });
};

const deleteMessage = (id: string) => {
  deleteMessageApi(id).then((res) => {
    if (res.code === 0) refreshList();
  });
};

const handleCommand = (command: string) => {
  switch (command) {
    case '标记全部为已读':
      readAllMessage();
      break;
    case '删除已读消息':
      deleteAllMessage();
      break;
    default:
      break;
  }
};

const load = () => {
  loading.value = true;
  searchInfo.size += 10;
  getMessagePollListApi(searchInfo)
    .then((res) => {
      const { result, count } = res;
      list.value = result.map((i: any) => {
        return {
          ...i,
          lastUpdateTime: parseTime(i.lastUpdateTime, '{y}/{m}/{d} {h}:{i}:{s}'),
        };
      });
      total.value = count;
    })
    .finally(() => {
      loading.value = false;
    });
};

const openDrawer = () => {
  load();
  drawerVisible.value = true;
};

const closeDrawer = () => {
  drawerVisible.value = false;
};

const toDirectUrl = (row: any) => {
  if (row.ackStatus === 'unread') {
    readMessageApi(row.id).then(() => {
      refreshList();
    });
  }
  const { origin, pathname } = window.location;
  window.open(origin + pathname + row.directUrl);
};

const toMessageDetail = (row: any) => {
  if (row.ackStatus === 'unread') {
    readMessageApi(row.id, { successMessageShow: false });
    refreshList();
  }
  // router.push(`/message-detail/${row.id}`)
};

const timer = ref();
const messageCount = ref(0);
const clearTimer = () => clearInterval(timer.value);
const initFetchMessageCount = () => {
  timer.value = setInterval(() => {
    getMessagePollListApi(
      {
        ackStatus: 'unread',
        pushMethods: 'system',
        size: 1,
      },
      { codeMessageShow: false, errorMessageShow: false }
    )
      .then((res) => {
        if (res.code === 0) {
          messageCount.value = res.count;
        } else {
          clearTimer();
        }
      })
      .catch(() => clearTimer());
  }, 3000);
};

// onMounted(() => {
//   clearTimer();
//   !props.readonly && initFetchMessageCount();
// });

// onBeforeUnmount(() => clearTimer());
</script>

<style scoped lang="scss">
.primaryHeader {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  height: 100%;
  position: relative;
}

.icon-cursor {
  position: absolute;
  right: 0;

  .message-icon-container {
    line-height: 25px;
  }

  &>span {
    display: inline-block;
    line-height: 59px;
    width: 78px;
    border-left: 1px solid #d3e0ec;
  }

  img {
    vertical-align: middle;
    cursor: pointer;
  }
}

.logoImg {
  height: 47px;
  margin-right: 20px;
}

.logo-text {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  font-size: 20px;
  color: var(--el-color-white);
}

.logo-text>span:nth-child(1) {
  font-weight: 400;
  font-size: 23px;
}

.logo-text>span:nth-child(2) {
  font-weight: 400;
  font-size: 13px;
}

.header {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
}

.title {
  font-size: 23px;
  font-weight: bold;
  color: var(--main-color);
}

.num {
  font-size: 17px;
  background-color: var(--el-color-primary);
  border-radius: 17.5px;
  color: #fff;
  line-height: 25px;
  height: 25px;
  padding: 0 7.5px;
}

.flex-container {
  height: 100%;
  display: flex;
  flex-direction: column;

  .infinite-list-wrapper {
    padding-top: 20px;
  }

  .flex-header {
    display: flex;
    justify-content: space-between;
    font-size: var(--el-font-size-medium);
    color: var(--main-color);
    font-weight: bold;
    position: relative;

    .closeIcon {
      position: absolute;
      right: 0;
      top: calc(50% - 8px);
      cursor: pointer;
    }

    span {
      flex-grow: 1;
      @include text-ellipsis;
      max-width: 50%;

      &:first-child {
        text-align: left;
        max-width: 70%;
      }

      &:last-child {
        text-align: right;
        max-width: 30%;
      }
    }
  }

  .bottom {
    display: flex;
    justify-content: space-between;
    font-size: var(--el-font-size-base);
    color: #889199;

    span {
      flex-grow: 1;
      @include text-ellipsis;

      &:first-child {
        text-align: left;
        max-width: 70%;
      }

      &:last-child {
        text-align: right;
        max-width: 30%;
      }
    }
  }

  .time {
    color: #889199;
    font-size: var(--el-font-size-base);
    text-align: left;
    padding: 9px 0;
  }
}

:global(.infinite-list-wrapper .el-divider--horizontal) {
  margin: 10px 0;
}

.loading-icon {
  animation: circle infinite 1s linear;
}

// 转转转动画
@keyframes circle {
  0% {
    transform: rotate(0);
  }

  100% {
    transform: rotate(360deg);
  }
}
</style>
