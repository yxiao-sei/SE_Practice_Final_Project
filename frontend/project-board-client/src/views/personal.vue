<template>
<div class="w-full">
  <user-card :showNotice="true" />
  <div class="card-menu">
    <div @click="toAppointment" class="flex-grow flex flex-col justify-center items-center">
      <svg-icon icon-name="icon-richenganpai" class="text-[37px]" color="#9FACCF"></svg-icon>
      <p class="mt-[8px] text-blue-200">面试预约</p>
    </div>
    <div class="w-[1px] bg-gray-100 h-full"></div>
    <div @click="openInformation" class="flex-grow flex flex-col justify-center items-center">
      <svg-icon icon-name="icon-jianli" class="text-[36px]" color="#9FACCF"></svg-icon>
      <p class="mt-[8px] text-blue-200">个人简历</p>
    </div>
  </div>
  <van-pull-refresh :loading="refreshing" v-model="refreshing" @refresh="refresh">
    <van-list :finished="finished" v-model:loading="loading" @load="onLoad" v-model:error="error"
      error-text="请求失败，点击重新加载" finished-text="没有更多了" class="space-y-[10px]">
      <template v-for="item in list" :key="item.id">
        <application-card :application="item" />
      </template>
    </van-list>
  </van-pull-refresh>
</div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import { useUserInfo } from '@/stores/cacheInfo';
import UserCard from '@/components/UserCard.vue';
import ApplicationCard from '@/components/ApplicationCard.vue';
import { findApplicationsListApi } from '@/service/application';

export default defineComponent({
  components: {
    UserCard,
    ApplicationCard
  },
  data() {
    return {
      filters: {
        name: '',
        pagination: {
          page: 1,
          pageSize: 0
        }
      },
      loading: false,
      refreshing: false,
      finished: false,
      error: false,
      list: <any[]>([]),
      count: 0
    }
  },
  methods: {
    search(name: string) {
      this.filters.name = name;
      this.filters.pagination.pageSize = 0;
      this.onLoad()
    },
    openInformation() {
      this.$router.push({
        name: "个人简历"
      })
    },
    toAppointment() {
      this.$router.push({
        name: "面试预约"
      })
    },
    onLoad() {
      this.loading = true;
      this.filters.pagination.pageSize += 10;
      const { pagination } = this.filters;
      const { user: principal } = useUserInfo();
      findApplicationsListApi({ pagination, 'filters[studentNumber][$eq]': principal.username }, { loading: false }).then(({ result, count }) => {
        this.list = result
        this.refreshing = false;
        this.loading = false;
        this.count = count;
        if (this.count <= this.filters.pagination.pageSize) {
          this.finished = true;
        } else {
          this.finished = false;
        }
      }).catch(() => {
        this.error = true;
        this.refreshing = false;
      })
    },
    refresh() {
      this.filters.name = "";
      this.filters.pagination.page = 1;
      this.filters.pagination.pageSize = 10;
      this.onLoad();
    },
  }
})
</script>

<style scoped>
.card-menu {
  @apply flex h-[105px] bg-white px-[15px] relative py-[16px] mb-[10px];
}

.card-menu::after {
  content: '';
  @apply absolute top-0 left-[15px] right-[15px] h-[1px] bg-gray-100;
}
</style>
