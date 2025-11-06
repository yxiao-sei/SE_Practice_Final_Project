<template>
  <div class="h-full overflow-y-auto">
    <van-pull-refresh :loading="refreshing" v-model="refreshing" @refresh="refresh">
      <van-list :finished="finished" v-model:loading="loading" @load="onLoad" v-model:error="error"
        error-text="请求失败，点击重新加载" finished-text="没有更多了" class="space-y-[10px] mt-[10px]">
        <template v-for="item in list" :key="item.id">
          <option-card :link="true" :project="item" />
        </template>
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import SearchBar from '~/SearchBar.vue'
import OptionCard from '~/OptionCard.vue'
import { findProjectsApi } from '@/service/project'
import { useUserInfo } from '@/stores/cacheInfo'
import { findAppointmentsApi } from '@/service/appointment'
import { findApplicationsListApi } from '@/service/application'

export default defineComponent({
  components: {
    SearchBar,
    OptionCard
  },
  data() {
    return {
      filters: <any>{
        filters: {
          code: {
            $in: []
          }
        },
        pagination: {
          page: 1,
          pageSize: 0,
        }
      },
      applications: <any[]>([]),
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
    async onLoad() {
      this.loading = true;
      const codes = await findApplicationsListApi({
        filters: {
          studentNumber: { $eq: useUserInfo().username },
          state: { $eq: "已通过，待面试" }
        },
        fields: ['projectCode', 'projectRescuitRole', 'projectRescuitRoleId'],
        pagination: { page: 1, pageSize: 99999999999 }
      }, { loading: false })
      const appRes = await findAppointmentsApi({
        filters: {
          studentNumber: { $eq: useUserInfo().username }
        },
        fields: ['projectCode'],
        pagination: { page: 1, pageSize: 99999999999 }
      })
      const existCodes = appRes.result.map((item: any) => item.projectCode)
      codes.result = codes.result.filter((item: any) => !existCodes.includes(item.projectCode))
      this.applications = codes.result;
      if (!codes.result.length) {
        this.loading = false
        this.finished = true
        return
      };
      this.filters.filters.code.$in = codes.result.map((item: any) => item.projectCode)
      this.filters.pagination.pageSize += 10;
      findProjectsApi({ ...this.filters }, { loading: false }).then(({ result, count }) => {
        this.list = result.map((item: any) => {
          const application = this.applications.find((item2: any) => item2.projectCode === item.code)
          if (application) {
            item.role = application.projectRescuitRole
            item.roleId = application.projectRescuitRoleId
          }
          return item
        })
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
