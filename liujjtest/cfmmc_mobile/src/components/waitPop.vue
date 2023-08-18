<template>
   <div class="wait_pop">
       <div class="loading" v-if="loadingbox">
        <div class="div_box">
            <van-loading class="loading-van" type="spinner" color="white" size="40px"/>
            <p>请稍后</p>
        </div>
    </div>
    </div>
</template>
<script>
import {Loading} from 'vant';
export default {
    components:{
        Loading,
    },
    data() {
        return{
          
        }
    },
    props: {
        // 定义 调用组件输入参数
        // content: String,
        loadingbox: Boolean
    },
    mounted(){
        
    },
    methods: {
       
    },
}
</script>
<style lang="less" scoped>
.wait_pop{
   .div_box{
       width:100px; 
       height:100px; 
       margin: 0 auto;
       position: fixed;
       top: 38%;
       left: 37%;
       background: rgba(0,0,0,0.5);
       border-radius: 4px;
   }
    @media only screen and (min-device-width: 768px) and (max-device-width: 1024px) {
        .div_box{
            left: 44%;
        }
    }
   .loading-van{
       margin: 19% auto;
       color: white;
       width: 30px; 
       height: 34px;
   }
   .loading{
      width: 100%;
      height: 100%;
      background: rgba(0, 0, 0, 0);
      margin: 0 auto;
      position: fixed;
      top: 0;
      left: 0;
      z-index:999;
        p{
          text-align: center;
          font-size: 16px;
          color:white;
          margin-top: -26%;
          line-height: 25px;
        }
    }
}
</style>