<template>
<template v-if="actions.length" :key="JSON.stringify(actions)">
  <el-button v-for="btn in commonButtons" :key="btn.command" @click="handleCommand(btn.command)"
    v-bind="btn.attributes">{{ btn.label }}
  </el-button>
  <div class="button-group" v-if="collapseButtons.length">
    <template v-if="allowCollapsed">
      <el-dropdown v-if="collapseButtons.length > 1" @command="handleCommand"
        @click.stop="handleCommand(firstButton.command)" split-button size="large"
        :class="firstButton.type === 'info' ? 'info-button-dropdown' : 'success-button-dropdown'" placement="bottom-end"
        v-bind="firstButton.attributes">
        {{ firstButton.label }}
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item v-for="menu in multipleActions" :command="menu.command" :key="menu.command">
              {{ menu.label }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      <el-button v-else @click="handleCommand(firstButton.command)" v-bind="firstButton.attributes">{{ firstButton.label
      }}
      </el-button>
    </template>
  </div>
</template>
</template>

<script setup lang="ts">
import { computed, toRefs } from 'vue';

const emits = defineEmits(['handleCommand']);

const handleCommand = (command: string) => {
  emits('handleCommand', command);
};

const props = defineProps<{ actions: any[] }>();

const { actions } = toRefs(props);

const allowCollapsed = computed(() => {
  const _actions = actions.value.filter((action) => !!action.isCollapse);
  return _actions.length > 1;
});

const collapseButtons = computed(() => {
  return allowCollapsed.value ? actions.value.filter((action) => !!action.isCollapse) : [];
});

const commonButtons = computed(() => {
  return allowCollapsed.value
    ? actions.value.filter((action) => !action.isCollapse)
    : actions.value;
});

const multipleActions = computed(() => {
  return collapseButtons.value.slice(1, actions.value.length);
});

const firstButton = computed(() => {
  return collapseButtons.value[0];
});
</script>

<style scoped>
.button-group {
  display: inline-block;
  vertical-align: middle;
}
</style>
