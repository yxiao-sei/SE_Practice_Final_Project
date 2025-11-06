<template>
<div id="nofound">
  <div class="body">
    <div class="left">
      <div>
        <div class="logo">
          <img src="../../../assets/images/logo.png" />
          <div class="logo-text">
            <div>{{ Lang.SystemTitle }}</div>
            <!-- <div>Student Affairs Office</div> -->
          </div>
        </div>
        <div class="text">
          <span>404</span>
          <span>No Found</span>
        </div>
        <div class="link-text">
          <span class="text2">
            抱歉，找不到该页面，请
            <span class="goHome" @click="back"> 返回 </span>
          </span>
        </div>
      </div>
    </div>
    <div class="right">
      <img src="../../../assets/images/yiban.png" />
    </div>
  </div>
</div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { useRouter } from "vue-router";
import Lang from '@/locales'
export default defineComponent({
  name: "404",
  setup() {
    const router = useRouter();
    const back = () => {
      router.go(-1)
    };
    return {
      back,
      Lang
    }
  },
});
</script>

<style scoped lang="scss">
#nofound {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background: #f1f1f1;

  .body {
    width: 100%;
    height: 80%;
    background: white;
    display: flex;
    flex-direction: row;
    text-align: center;

    div {
      flex-basis: 0;
      flex-grow: 1;
    }

    .left {
      display: flex;
      align-items: center;
      justify-content: center;

      .logo {
        .logo-text {
          display: inline-block;
          line-height: normal;
          padding-left: 10px;
          font-size: 20px;
          vertical-align: middle;
          color: #b70031;
          text-align: left;

          div:nth-of-type(1) {
            font-weight: bold;
            font-size: 36px;
          }

          div:nth-of-type(2) {
            font-size: 36px;
            font-weight: 400;
          }
        }

        img {
          vertical-align: middle;
          width: 89px;
          height: 89px;
        }
      }

      .text {
        span:first-child {
          color: #e3738d;
          font-size: 140px;
          font-weight: 900;
          margin-left: 170px;
        }

        span:last-child {
          margin-left: 30px;
          color: #e3738d;
          font-size: 55px;
          font-weight: 400;
        }
      }

      .link-text {
        font-size: 28px;
        font-weight: 400;
        color: #62696f;

        .text2 {
          margin-left: 100px;

          .goHome {
            cursor: pointer;
            font-size: 28px;
            font-weight: 400;
            color: #e17791;
            text-decoration: underline;
          }

          .goHome:hover {
            opacity: 0.8;
          }
        }
      }
    }

    .right {
      position: relative;
      width: 100%;
      overflow: hidden;

      img {
        position: absolute;
        bottom: 0;
        width: 90%;
        right: 80px;
        opacity: 0.2;
      }
    }
  }
}
</style>
