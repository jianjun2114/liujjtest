<template>
  <div class="error">
    <div class="error_div" :style="{height:screenHeight}">
      <img src="~@a/other/buhefa.png">
    </div>
    <div class="goIndex">
      <el-button @click="goLogin()"> 重新登录</el-button>
    </div>
  </div>

</template>

<script type="text/ecmascript-6">

    export default {
        data() {
            return {
                screenWidth: '1000px',
                screenHeight: '600px',
            }
        },
        mounted() {
            this.screenWidth = sessionStorage.getItem('screenWidth');
            this.screenHeight = sessionStorage.getItem('screenHeightT');
        },
        methods: {
            goLogin() {
                this.loginOut();
            }
        }
    }
</script>

<style lang="less" scoped>
  .error {
    .error_div {
      background: #ECEEF3;
      height: 100%;
      display: flex;
      justify-content: center;
      align-items: center;
      //backgorund:url('../../assets/other/buhefa.png');
    }

    .goIndex {
      position: absolute;
      left: 47%;
      bottom: 13%;
      /*margin-top: 50px;*/

      a {
        color: lightgrey;
      }
    }

  }

</style>
