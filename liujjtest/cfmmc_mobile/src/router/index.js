import Vue from 'vue'
import Router from 'vue-router'
import HelloWorld from '../components/HelloWorld.vue'
import IdcardUpload from '../page/upload/IdcardUpload.vue'
import LaunderingBack from '../page/launderingBack/LaunderingBack.vue'
import AddInfo from '../page/information/AddInfo.vue'
import Question  from '../page/question/question.vue'
import FuturesBank  from '../page/futuresBank/futuresBank.vue'
import Appropriate from '../page/appropriate/appropriate.vue'
import ChooseFutures from '../page/chooseAccount/ChooseFutures.vue'
import SignAgreement from '../page/agreement/signAgreement.vue'
import Agreement from '../page/agreement/agreement.vue'
import Video from '../page/video/video.vue'
import Index from '../page/index/index.vue'
import SettleBank from '../page/settleBank/settleBank.vue'
import Cert from '../page/cert/cert.vue'
import Echarts from '../page/echarts/echarts.vue'
import MajorUpload from '../page/majorUpload/majorUpload.vue'
import OnlineBack from '../page/onlineBack/onlineBack.vue'
import Result from '../page/result/result.vue'
import Bussiness from '../page/bussiness/bussiness.vue'
import MyBussiness from '../page/bussiness/myBussiness.vue'
import Indexselection from '../page/businessSelection/indexselection.vue'
import Register from '../page/register/register.vue'
import ChoicePage from '../page/choicePage/choicePage.vue'
import DoneSuitably from '../page/suitably/doneSuitably.vue'
import BasicInfo from '../page/information/basicInfo.vue'
import QuestionMajor from '../page/questionMajor/questionMajor.vue'
import TransactionCode from '../page/transactionCode/transactionCode.vue'
import AgreementDownload from '../page/agreement/agreementDownload.vue'
import UpIdCardInfo from '../page/information/upIdCardInfo.vue'
import CanahartaLogin from '../page/canahartaLogin/canahartalogin.vue'
import Product from '../page/product/Product.vue'
Vue.use(Router);
export default new Router({
	 mode: 'history',
  routes: [
	{
	  path: '/index',
	  name: 'Index',
	  component: Index,
	},{
		path: '/',
    redirect: '/index'   //路由匹配初始化页面
	},{
      path: '/',
      name: 'HelloWorld',
      component: HelloWorld,
	  meta: {
		keepAlive: false, //此组件不需要被缓存
	  }
    }, {
      path: '/idcardUpload',
      name: 'IdcardUpload',
      component: IdcardUpload,
	  meta: {
		keepAlive: false, //此组件不需要被缓存
	  }
    },{
      path: '/addInfo',
      name: 'AddInfo',
      component: AddInfo,
	  meta: {
		keepAlive: false, //此组件不需要被缓存
	  }
    },{
      path: '/launderingBack',
      name: 'LaunderingBack',
      component: LaunderingBack,
      meta: {
        keepAlive: false, //此组件不需要被缓存
      }
    },{
      path: '/question',
      name: 'Question',
      component: Question,
	  meta: {
		keepAlive: false, //此组件不需要被缓存
	  }
    },{
      path: '/futuresBank',
      name: 'FuturesBank',
      component: FuturesBank,
	  meta: {
		keepAlive: false, //此组件不需要被缓存
	  }
    },{
      path: '/appropriate',
      name: 'Appropriateness',
      component: Appropriate,
	  meta: {
		keepAlive: false, //此组件不需要被缓存
	  }
    },{
      path: '/chooseFutures',
      name: 'ChooseFutures',
      component: ChooseFutures,
	  meta: {
		keepAlive: false, //此组件不需要被缓存
	  }
    },{
      path: '/signAgreement',
      name: 'SignAgreement',
      component: SignAgreement,
	  meta: {
		keepAlive: false, //此组件不需要被缓存
	  }
    },{
      path: '/agreement',
      name: 'Agreement',
      component: Agreement,
	  meta: {
		keepAlive: false, //此组件不需要被缓存
	  }
    },{
      path: '/video',
      name: 'Video',
      component: Video,
	  meta: {
		keepAlive: false, //此组件不需要被缓存
	  }
	 },{
	  path: '/settleBank',
	  name: 'SettleBank',
	  component: SettleBank,
	  meta: {
		  keepAlive: false, //此组件不需要被缓存    /////
	  }
	},{
	  path: '/cert',
	  name: 'Cert',
	  component: Cert,
	},{
	  path: '/echarts',
	  name: 'Echarts',
	  component: Echarts,
	},{
	  path: '/majorUpload',
	  name: 'MajorUpload',
	  component: MajorUpload,
	},{
	  path: '/onlineBack',
	  name: 'OnlineBack',
	  component: OnlineBack,
	},{
	  path: '/result',
	  name: 'Result',
	  component: Result,
	},{
	  path: '/bussiness',
	  name: 'Bussiness',
	  component: Bussiness,
	},{
	  path: '/myBussiness',
	  name: 'MyBussiness',
	  component: MyBussiness,
	},{
	  path: '/register',
	  name: 'Register',
	  component: Register,
	},{
	  path: '/indexselection',
	  name: 'Indexselection',
	  component: Indexselection,
	},{
	  path: '/choicePage',
	  name: 'ChoicePage',
	  component: ChoicePage,
	},{
	  path: '/doneSuitably',
	  name: 'DoneSuitably',
	  component: DoneSuitably,
	},{
	  path: '/basicInfo',
	  name: 'BasicInfo',
	  component: BasicInfo,
	},{
		path: '/questionMajor',
	  name: 'QuestionMajor',
	  component: QuestionMajor,
	},{
		path: '/transactionCode',
	  name: 'TransactionCode',
	  component: TransactionCode,
	},{
      path: '/agreementDownload',
      name: 'AgreementDownload',
      component: AgreementDownload,
  },{
      path: '/upIdCardInfo',
      name: 'UpIdCardInfo',
      component: UpIdCardInfo,
    },{
	  path: '/canahartaLogin',
      name: 'CanahartaLogin',
      component: CanahartaLogin,
	},{
	  path: '/product',
      name: 'Product',
      component: Product,
	}
  ]
})
