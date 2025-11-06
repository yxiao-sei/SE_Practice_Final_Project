<template>
<div class="w-full">
  <search-bar @search="search" class="mb-[10px]" />
  <van-pull-refresh class="flow-container" :loading="refreshing" v-model="refreshing" @refresh="refresh">
    <van-list :finished="finished" v-model:loading="loading" @load="onLoad" v-model:error="error"
      error-text="请求失败，点击重新加载" finished-text="没有更多了" class="space-y-[10px]">
      <template v-for="item in list" :key="item.id">
        <project-card :link="true" :project="item" />
      </template>
    </van-list>
  </van-pull-refresh>
</div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import SearchBar from '~/SearchBar.vue'
import ProjectCard from '~/ProjectCard.vue'
import { findProjectsApi } from '@/service/project'

export default defineComponent({
  components: {
    SearchBar
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
    onLoad() {
      this.loading = true;
      this.filters.pagination.pageSize += 10;
      const { pagination } = this.filters;
      findProjectsApi({ pagination, 'filters[name][$contains]': this.filters.name }, { loading: false }).then(({ result, count }) => {
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
  },
  setup() {

    return {
    }
  }
})
</script>

<style scoped>
.flow-container {
  height: calc(100% - 74px);
  overflow-y: auto;
}
</style>
