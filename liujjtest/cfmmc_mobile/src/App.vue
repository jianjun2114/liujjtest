<template>
  <div id="app">
      <keep-alive>
          <router-view v-if="$route.meta.keepAlive && isReload">
              <!-- 这里是会被缓存的视图组件，比如 page1,page2 -->
          </router-view>
      </keep-alive>

      <router-view v-if="!$route.meta.keepAlive && isReload">
          <!-- 这里是不被缓存的视图组件，比如 page3 -->
      </router-view>
  </div>
</template>

<script>
   export default {
  name: 'App',
  components: {},
  provide() { // 注册一个方法
	return {
    reload:this.reload
	}
  },
  data() {
	return {
	  isRouterAlive: false,
    isReload:true
	}
  },
  methods:{
    reload(){
      this.isReload = false;
      this.$nextTick(function(){
        this.isReload = true;
      })
    }
  }
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  height: 100%;

}
</style>
