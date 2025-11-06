<template>
  <PageMenuBar class="page-menu-bar" :pageMenus="pageMenus" />
  <router-view />
</template>

<script setup lang="ts">
import PageMenuBar from "~/PageMenuBar/Index.vue";
import { pageMenus } from "@/router/routes/projects";
</script>

<style scoped></style>