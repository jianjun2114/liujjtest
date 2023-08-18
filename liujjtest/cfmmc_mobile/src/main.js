// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import App from './App'
import router from './router'
import Vue from 'vue';
import Vuex from 'vuex'
import Vant from 'vant';
import axios from 'axios';
import VueAxios from 'vue-axios';
import 'vant/lib/index.css';
import 'vant/lib/icon/local.css';
import './util/rem'
import './util/iphoneOrAndroid'
import './util/layer'
import './style/app.less'
import './style/icon/iconfont.css'
// import './style/icon/nexStep.css'
import V_validate from './util/validator'

const Base64 = require('js-base64').Base64
Vue.config.productionTip = false;
axios.defaults.withCredentials=true;
Vue.prototype.$ajax = axios;
Vue.use(Vuex);
Vue.use(Vant);
Vue.use(VueAxios, axios);
Vue.use(V_validate);

Vue.prototype.getConfigJson = function () {
  axios.get('static/app-config.json').then((result) => {
    axios.defaults.baseURL = result.data.prefix+window.location.host+result.data.postfix;
    //axios.defaults.baseURL = result.data.baseUrl;
    new Vue({
      el: '#app',
      Base64,
      router,
      components: { App },
      template: '<App/>'
    })
  }).catch((error) => {
    // axios.defaults.baseURL = process.env.API_ROOT;
    axios.defaults.baseURL = '/api';
    new Vue({
      el: '#app',
      Base64,
      router,
      components: { App },
      template: '<App/>'
    })
    console.log('57:'+error)
  });
}
Vue.prototype.getConfigJson();

/* eslint-disable no-new */

