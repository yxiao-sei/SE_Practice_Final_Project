<template>
    <el-dialog class="rules-modal" :fullscreen="true" v-model="rulesVisible" align-center :show-close="false"
        :close-on-click-modal="false" :close-on-press-escape="false">
        <div style="width: 50%; margin: 0 auto; line-height: 30px; font-size: 18px">
            <p style="text-align: center">
                <strong>《研究生教育质量数据使用与保密条款》</strong>
            </p>
            <p style="text-indent: 2em">
                华东师范大学研究生教育质量管理系统用于华东师范大学学生的信息采集、管理及学工部信息化业务开展。其学生数据来源分为两部分：（1）第三方学校部门，包括信息化治理办公室、本科生教务处、研究生院、后勤保障部等（2）学生自填数据。其中，第三方数据解释权归第三方学校部门，<span
                    style="color: red">本条款适用于学生自填数据</span>。
            </p>
            <p><strong>一、学生自填数据范围</strong></p>
            <p style="text-indent: 2em">（1）个人信息</p>
            <p style="text-indent: 4em">
                包括基本信息、本人经历、本人特长、自我评价。
            </p>
            <p style="text-indent: 2em">（2）家庭信息</p>
            <p style="text-indent: 4em">
                包括家庭情况、家庭成员情况、家庭情况备注。
            </p>
            <p><strong>二、数据使用人群</strong></p>
            <p style="text-indent: 2em">（1）在职辅导员</p>
            <p style="text-indent: 2em">（2）学院负责学生工作的副书记</p>
            <p style="text-indent: 2em">（3）大学工范围教职工</p>

            <p style="text-indent: 2em">
                若有除上述人群以外的人员需要使用学生数据，需<span
                    style="color: red">以文字形式向学工部声明用途和使用时间范围</span>，经过学工部检验和授权后使用学生数据，并在使用期间遵守本条款。
            </p>

            <p><strong>三、数据使用及保密条款</strong></p>
            <p style="text-indent: 2em">
                （1）学生数据使用者应自觉维护校方与学生利益，严格遵守保密规定。
            </p>
            <p style="text-indent: 2em">
                （2）学生数据使用者<span style="color: red">不得向任何第三方单位和个人泄露学生数据</span>。
            </p>
            <p style="text-indent: 2em">
                （3）学生数据使用者不得利用所掌握的学生数据牟取私利。
            </p>
            <p style="text-indent: 2em">
                （4）学生数据使用者不得将学生数据据为己有，有关文档不得私自留存，待使用事项结束后必须全部删除。
            </p>
            <p style="text-indent: 2em">
                （5）学生数据使用者应在结束使用事项后8小时内，<span style="color: red">彻底删除本地保存的所有学生数据</span>。
            </p>
            <div class="footer" style="text-align: center; margin-top: 30px">
                <p style="text-align: right">研究生院</p>
                <p style="text-align: right">2024年7月12日</p>
            </div>
            <div slot="footer" class="ruler_footer">
                <el-button type="primary" size='large' @click="confirm">关闭</el-button>
            </div>
        </div>
    </el-dialog>
</template>
<script lang="ts" setup>
import { toRefs } from 'vue';
const props = defineProps({
    rulesVisible: Boolean,
});
const { rulesVisible } = toRefs(props);
console.log("rulesVisible", rulesVisible.value);

const emits = defineEmits(['closeRulesMoadl']);

const confirm = () => {
    emits('closeRulesMoadl');
};

// const { loading, width } = toRefs(props);
</script>
<style scoped lang="scss">
.ruler_footer {
  text-align: center;
  margin-top: 20px;
}
</style>