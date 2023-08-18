<template>
  <div class="navigation">
    <div class="title" v-if="isLoad">
      <span :class="step1load">1.资料报审</span>
      <van-icon name="arrow"/>
      <span  :class="step2load">2.签署协议</span>
      <van-icon name="arrow"/>
      <span :class="step3load">3.申请提交</span>
    </div>
    <div class="title1" v-if="!isLoad">

    </div>
  </div>
</template>

<script>
    export default {
      components: {},
      props: {},
      data() {
        return {
          step1load:"",
          step2load:"",
          step3load:"",
          isLoad:true
        };
      },
      created(){
        var business_type = sessionStorage.getItem('business_type');
        if(!(business_type == "11" || business_type == "13")){
         this.isLoad =  false;
         return;
        }
        var flow = this.$router.history.current.name;
        if(flow == "SignAgreement"){
          this.step2load = "step1";
        } else if(flow == "OnlineBack"){
          this.step3load = "step1";
        } else {
          this.step1load = "step1";
        }
      },
      methods:{
      },
    }
</script>
<style lang="less" scoped>
  .navigation {
    .title1 {
    font-size: 14px;
    letter-spacing: 1.8px;
    background: white;
    line-height: 44px;
    padding-top: 12%;
    .step1{
      color: #49a5ff;
    }
    span{
      margin-right: 1rem;
      color: #b8b8b8;
    }
    .van-icon{
      font-size: 1rem;
    }
  }
    .title {
      font-size: 14px;
      letter-spacing: 1.8px;
      background: white;
      line-height: 44px;
      padding-top: 12%;
      .step1{
        color: #49a5ff;
      }
      span{
        margin-right: 1rem;
        color: #b8b8b8;
      }
      .van-icon{
        font-size: 1rem;
      }
    }
    @media only screen and (min-device-width : 768px) and (max-device-width : 1024px){
      .title{
        line-height: 50px;
        padding-top: 7%;
        font-size: 17px;
      }
    }
    @media only screen and (min-device-width: 1366px) and (orientation: landscape) {
      .title{
        padding-top: 5%;
      }
    }
  }
</style>
