<template>
    <div class="notFound">
        <div class="error_div" :style="{height:screenHeight}">
            <img src="~@a/other/404.png" alt="" class="error_img">
        </div>
        <el-button class="goIndex"><router-link to="/">返回首页</router-link></el-button>
    </div>

</template>

<script type="text/ecmascript-6">
  export default {
    data(){
      return{
        screenWidth: '1000px',
        screenHeight: '600px',
      }
    },
    mounted() {
      this.screenWidth = sessionStorage.getItem('screenWidth');
      this.screenHeight = sessionStorage.getItem('screenHeightT');
    },
  }

</script>

<style lang="less" scoped>
    .error{
        .error_div{
            background: #ECEEF3;
            height: 100%;
            display: flex;
            justify-content: center;
            align-items: center;
        }
        .goIndex{
            position: absolute;
            left:47%;
            bottom: 13%;
            a{
                color: lightgrey;
            }
        }

    }

</style>
