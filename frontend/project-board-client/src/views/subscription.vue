<template>
  <div class="flex-grow overflow-auto">
    <van-cell center class="!text-blue-200 font-bold" title="订阅开启">
      <template #right-icon>
        <van-switch v-model="form.messageOn" active-color="#3F599A" size="20px" />
      </template>
    </van-cell>
    <p class="h-[42px] px-[15px] bg-[#F1F1F1] text-primary-100 leading-[42px]">岗位报名</p>
    <van-cell-group>
      <van-cell title="订阅教师" @click="open('订阅教师')" class="!text-blue-200 font-bold tag-cell" is-link value="内容">
        <div class="pl-[15px] justify-end flex flex-wrap" v-if="subscribeTopics['订阅教师'].length > 0">
          <span class="mr-[5px] mb-[5px] rounded-[3px] h-[28px] px-[7px] text-blue-200 bg-[rgba(159,172,207,.16)]"
            v-for="number, index in subscribeTopics['订阅教师']" :key="index">{{ teacherName(number) }}</span>
        </div>
      </van-cell>
      <van-cell title="研究方向" @click="open('研究方向')" class="!text-blue-200 font-bold" is-link value="内容">
        <div class="pl-[15px] justify-end flex flex-wrap" v-if="subscribeTopics['订阅教师'].length > 0">
          <span class="mr-[5px] mb-[5px] rounded-[3px] h-[28px] px-[7px] text-blue-200 bg-[rgba(159,172,207,.16)]"
            v-for="label, index in subscribeTopics['研究方向']" :key="index">{{ label }}</span>
        </div>
      </van-cell>
      <van-cell title="课题标签" @click="open('课题标签')" class="!text-blue-200 font-bold" is-link value="内容">
        <div class="pl-[15px] justify-end flex flex-wrap" v-if="subscribeTopics['课题标签'].length > 0">
          <span class="mr-[5px] mb-[5px] rounded-[3px] h-[28px] px-[7px] text-blue-200 bg-[rgba(159,172,207,.16)]"
            v-for="label, index in subscribeTopics['课题标签']" :key="index">{{ label }}</span>
        </div>
      </van-cell>
    </van-cell-group>
  </div>
  <div class="fixed-container">
    <van-button type="primary" @click="onSubmit" color="#3F599A" block>保存设置</van-button>
  </div>
  <van-popup v-model:show="showDialog" teleport="#app" round safe-area-inset-bottom position="bottom"
    :style="{ height: '45%', display: 'flex', flexDirection: 'column' }">
    <div class="flex justify-between items-center h-[44px]">
      <van-button @click="cancel" style="border: none"><span class="text-[#9FACCF]">取消</span></van-button>
      <span class="text-[#323233] font-bold text-[16px]">选择{{ activeOp }}</span>
      <van-button @click="onConfirm" style="border: none"><span class="text-[#3F599A]">确认</span></van-button>
    </div>
    <div class="space-x-[10px] px-[15px]">
      <van-checkbox class="h-[44px]" v-model="isCheckAll" :indeterminate="isIndeterminate" @change="checkAllChange">
        全选
      </van-checkbox>
    </div>
    <van-checkbox-group teleport="#app" @change="checkedResultChange" class="flex-grow overflow-y-auto"
      v-model="checkResult">
      <van-cell-group>
        <van-cell @click="toggle(index)" v-for="op, index in option[activeOp]" :key="op.value"
          :title="op.label" clickable>
          <template #right-icon>
            <van-checkbox :name="op.value" @click.stop :ref="el => checkboxRefs[index] = el" />
          </template>
        </van-cell>
      </van-cell-group>
    </van-checkbox-group>
  </van-popup>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, shallowRef, toRef, computed } from 'vue';
import { getTeacherRolesApi } from '@/service/api';
import { findMetaDataListApi } from '@/service/meta-data';
import { findSubscribeListApi, updateSubscribeApi, createSubscribeApi } from '@/service/subscribe';
import { findAdministratorListApi } from '@/service/administrator';
import { useOptions } from '@/stores/useOptions';
import { useUserInfo } from '@/stores/cacheInfo';
import { showConfirmDialog, showNotify } from 'vant';

const { username, realName } = useUserInfo()
const form = reactive<any>({
  messageOn: false,
  id: '',
  subscribeTopics: {
    '研究方向': [],
    '课题标签': [],
    '订阅教师': []
  }
})
const onSubmit = () => {
  const data = {
    messageOn: form.messageOn,
    number: username,
    name: realName,
    subscribeTopics: [
      {
        "category": "leadingTeacherName",
        "categoryDescription": "订阅教师",
        "subjects": subscribeTopics.value['订阅教师']
      },
      {
        "category": "researchArea",
        "categoryDescription": "研究方向",
        "subjects": subscribeTopics.value['研究方向']
      },
      {
        "category": "labels",
        "categoryDescription": "课题标签",
        "subjects": subscribeTopics.value['课题标签']
      }
    ]
  }
  showConfirmDialog({
    title: '提示',
    message: `确认保存当前设置吗？`,
    confirmButtonColor: '#3F599A',
    width: '320px'
  })
    .then(() => {
      const promiseApi = form.id ? updateSubscribeApi(form.id, data) : createSubscribeApi(data)
      promiseApi.then(() => {
        showNotify({
          type: 'success',
          message: '保存成功',
          background: 'var(--van-notify-success-background)',
          color: 'var(--van-notify-text-color)',
        });
      })
    })
    .catch(() => { });
}

const subscribeTopics = toRef(form, 'subscribeTopics')
const checkResult = ref<any[]>([])
const teacherName = computed(() => {
  return (number: string) => {
    const teacher = option['订阅教师'].find((item: any) => item.value === number)
    return teacher ? teacher.label : ''
  }
})
const cancel = () => {
  showDialog.value = false
  checkResult.value = []
}
const checkboxRefs = ref<any[]>([]);
const toggle = (index: number) => {
  checkboxRefs.value[index].toggle();
};
const onConfirm = () => {
  showDialog.value = false
  subscribeTopics.value[activeOp.value] = [...checkResult.value]
  checkResult.value = []
}
const showDialog = ref(false)
const open = (active: keyof typeof option) => {
  checkResult.value = [...subscribeTopics.value[active]]
  activeOp.value = active
  showDialog.value = true
}
const option = reactive<{ '研究方向': any[], '课题标签': any[], '订阅教师': any[] }>({
  '研究方向': [],
  '课题标签': [],
  '订阅教师': []
})
const activeOp = ref<keyof typeof option>('研究方向')
const isCheckAll = ref(false);
const isIndeterminate = ref(true);

const checkAllChange = (val: boolean) => {
  checkResult.value = val ? option[activeOp.value].map((item: any) => item.value) : []
  isIndeterminate.value = false
}

const checkedResultChange = (value: string[]) => {
  const checkedCount = value.length
  isCheckAll.value = checkedCount === option[activeOp.value].length
  isIndeterminate.value = checkedCount > 0 && checkedCount < option[activeOp.value].length
}
const roles = shallowRef<any[]>([])
const { teacherRoles, setTeacherRoles } = useOptions()
const findSubscribeList = async () => {
  findSubscribeListApi({
    filters: {
      number: {
        $eq: username
      }
    }
  }).then((res) => {
    if (res) {
      const result = res.result[0]
      form.id = result.id
      form.messageOn = result.messageOn
      Object.keys(subscribeTopics.value).forEach((key: string) => {
        const data = result.subscribeTopics.find((item: any) => item.categoryDescription === key)
        if (data) {
          subscribeTopics.value[key] = data.subjects
        }
      })
    }
  })
}
onMounted(async () => {
  if (!teacherRoles.length) {
    const roleResult = await getTeacherRolesApi()
    const list = roleResult.roles.map((role: any) => {
      const key = Object.keys(role)[0];
      return {
        label: role[key],
        value: key,
      };
    });
    roles.value = list
    setTeacherRoles(list)
  }
  const promises = [
    findMetaDataListApi({
      pagination: { pageSize: 10000 },
      fields: ['id', 'name', 'jsonData'],
      filters: {
        isModifiable: {
          $eq: 1,
        },
        category: {
          $eq: '课题标签',
        },
      }
    }),
    findMetaDataListApi({
      pagination: { pageSize: 10000 },
      fields: ['id', 'name', 'jsonData'],
      filters: {
        isModifiable: {
          $eq: 1,
        },
        category: {
          $eq: '研究方向',
        },
      }
    }),
    findAdministratorListApi({
      pagination: { pageSize: 10000 },
      filters: {
        "$or": roles.value.map((item: any) => {
          return {
            "roles": {
              "$contains": item.value
            }
          }
        })
      }
    })
  ]
  Promise.all(promises).then((results) => {
    option['课题标签'] = results[0].result.map((item: any) => {
      return {
        label: item.name,
        value: item.name,
      };
    });
    option['研究方向'] = results[1].result.map((item: any) => {
      return {
        label: item.name,
        value: item.name,
      };
    });
    option['订阅教师'] = results[2].result.map((item: any) => {
      return {
        label: item.name,
        value: item.number,
      };
    });
  })
  findSubscribeList()
})
</script>

<style scoped>
:global(.tag-cell .van-cell__title) {
  white-space: nowrap;
  flex-grow: 0;
}

.fixed-container {
  padding-bottom: calc(12px + constant(safe-area-inset-bottom));
  padding-bottom: calc(12px + env(safe-area-inset-bottom));
  padding-top: 12px;
  @apply px-[15px] z-10;
}
</style>
