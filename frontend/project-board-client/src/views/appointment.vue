<template>
  <div class="w-full">
    <van-row class="px-[14px] flex items-center py-[8px]">
      <van-col :span="8" class="text-[#3D4348]">面试预约</van-col>
      <van-col :span="16" :align="'right'">
        <van-button icon="plus" color="#3F599A" size="small" type="primary" @click="toNew">新增预约</van-button>
      </van-col>
    </van-row>
    <van-pull-refresh :loading="refreshing" v-model="refreshing" @refresh="refresh">
      <van-list :finished="finished" v-model:loading="loading" @load="onLoad" v-model:error="error"
        error-text="请求失败，点击重新加载" finished-text="没有更多了" class="space-y-[10px]">
        <template v-for="item in list" :key="item.id">
          <appointment-card :link="true" :appointment="item" />
        </template>
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import SearchBar from '~/SearchBar.vue'
import AppointmentCard from '~/appointmentCard.vue'
import { useUserInfo } from '@/stores/cacheInfo'
import { findAppointmentsApi } from '@/service/appointment'

export default defineComponent({
  components: {
    SearchBar,
    AppointmentCard
  },
  data() {
    return {
      filters: {
        filters: {
          studentNumber: {
            $eq: ''
          }
        },
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
      this.filters.pagination.pageSize = 0;
      this.onLoad()
    },
    toNew() {
      this.$router.push({ name: '选择项目课题' })
    },
    onLoad() {
      this.loading = true;
      this.filters.pagination.pageSize += 10;
      const { pagination } = this.filters;
      const { username } = useUserInfo();
      this.filters.filters.studentNumber.$eq = username;
      findAppointmentsApi({ pagination, filters: this.filters.filters }).then(({ result, count }) => {
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
      this.filters.pagination.page = 1;
      this.filters.pagination.pageSize = 10;
      this.onLoad();
    },
  },
  setup() {

    return {
    }
  }
})
</script>

<style scoped></style>
