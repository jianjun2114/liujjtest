<template>
  <div class="footer_bar">
    <router-link :to="{name:'Bussiness'}">
      <van-icon name="wap-home"/>
      <p><span>业务办理</span></p>
    </router-link>
    <router-link :to="{name:'MyBussiness'}">
      <van-icon name="contact"/>
      <p><span>我的业务</span></p>
    </router-link>
  </div>
</template>

<script>

    export default {
        data() {
            return {}
        },
        methods: {},
        components: {}
    }
</script>

<style lang="less" scoped>
  .footer_bar {
    font-size: 1.4rem;
    position: fixed;
    display: flex;
    z-index: 9;
    bottom: 0;
    width: 100%;
    background-color: #fafafa;;

    .router-link-active {
      color: #49A5FF;
    }

    a {
      display: block;
      -webkit-box-flex: 1;
      -webkit-flex: 1;
      flex: 1;
      padding: 20px 0 0;
      font-size: 1.4rem;
      color: #999;
      text-align: center;
    }

    i {
      font-size: 2rem;
    }
  }
</style>
