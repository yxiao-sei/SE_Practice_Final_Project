<script setup lang="ts">
import { getUserPrincipalApi } from '@/service/api'
import { useUserInfo } from '@/stores/cacheInfo'
import { defineAsyncComponent } from 'vue'
import { RouterView } from 'vue-router'
import { findAdministratorListApi } from '@/service/administrator'

const { setUserInfo } = useUserInfo()
const appendError = (error: any) => {
  const targetEl = document.querySelector('#app')
  while (targetEl?.firstChild) {
    targetEl.removeChild(targetEl.firstChild);
  }
  const element = document.createElement('div');
  const shadow = element.attachShadow({ mode: 'open' });
  shadow.innerHTML = typeof error.response.data === 'string' ? error.response.data : JSON.stringify(error.response.data);
  targetEl!.appendChild(element);
}

const AsyncLayout = defineAsyncComponent(() => {
  return new Promise(resolve => {
    getUserPrincipalApi().then(async (res) => {
      const { result } = await findAdministratorListApi({
        'filters[number][$eq]': res.details.username,
      }, {loading: false})
      if (result.length) {
        setUserInfo({ ...res.details, ...result[0] })
      }else {
        setUserInfo(res.details)
      }
      resolve(RouterView as any)
    }).catch((error: any) => {
      console.error(error, "error")
      // appendError(error);
    })
  })
})

</script>

<template>
  <AsyncLayout />
</template>

<style scoped></style>
