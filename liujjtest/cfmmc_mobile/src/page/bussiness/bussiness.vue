<template>
  <div class="bussiness">
    <van-nav-bar  title="业务选择" right-text="关闭" right-arrow @click-right="onClickRight"/>
    <div class="highly"></div>
   <footer-bar></footer-bar>
    <div class="van-col" v-for="(item,index) in menuList" :key="index"  @click="goInfo(item.business_type)" v-if="item.business_status==8 && (item.business_type=='11' || item.business_type=='13' || item.business_type=='14'|| item.business_type=='20'|| item.business_type=='15' || item.business_type=='12' || item.business_type=='19')">
      <div  class="bus_type" :id="getId(item.business_type)">
        <span class="bus_type_name">{{item.business_name}}</span>
      </div>
    </div>
    <popUp :tips="error_content" :showBox="show_content" @notarize="notarize"></popUp>
    <quit :content="content" :quit="quit"></quit>
    <confirm :showContents="showContents" :isShow="isShow" :yesButton="yesButton" :noButton="noButton" @ensure="ensure" @cancel="cancel"></confirm>
     <showDiaLog :ShowDisplay="ShowDisplay" :titleName="titleName" :workCenter="workCenter"
              @buttonOneClick="buttonOneClick"  :buttonOne="buttonOneName"
              @buttonTwoClick="buttonTwoClick"  :buttonTwo="buttonTwoName"
              @buttonThreeClick="buttonThreeClick"  :buttonThree="buttonThreeName"></showDiaLog>
  </div>
</template>
<script>
import Quit from "../../components/quit.vue";
import ShowDiaLog from "../../components/showDiaLog.vue";
import { Row, Col } from 'vant';
import { Tabbar, TabbarItem, Group, Cell } from 'vux'
import FooterBar from '../../components/footerBar.vue'
import PopUp from "../../components/popUp.vue";
import Confirm from "../../components/confirm.vue";
import Utils from '../../util/iphoneOrAndroid';
  export default {
    components: {
      Tabbar,
      TabbarItem,
      Group,
      Cell,
      FooterBar,
      PopUp,
      Quit,
      Confirm,
      ShowDiaLog
    },
    data(){
      return{
        buttonOneName:"",
        buttonTwoName:"",
        buttonThreeName:"",
        workCenter:"",
        ShowDisplay: false,
        titleName:"",
        menuList:[],
        error_content:"",
        show_content: false,
        quit:false,
        content:"",
        tips:"",
        isShow:false,
        showContents:"",
        yesButton:"确定",
        noButton:"取消",
        type:"",
        closeFlag:false
      }
    },
    created() {
      var  _this = this;

      var _clientIP = sessionStorage.getItem('_clientIP');
      if(_clientIP == undefined || _clientIP == null){
        _clientIP = ''
      }
      //判断期货公司不存在
      var data = {
        "broker_id":sessionStorage.getItem('broker_id'),
        "clientIP":_clientIP
      }
      _this.$ajax.post('/initAppPage', data,{}).then(function(res){
        if(res.data.error_no == 0){
           _this.menuList = res.data.menu_list;
        }else if(res.data.error_no == "-9999"){
          _this.quit = true;
          _this.content = res.data.error_info;
        }else{
          _this.show_content = true;
          _this.error_content = res.data.error_info;
        }
      }).catch(function (err) {
        console.log("error:"+err);
      });
    },
    mounted() {

    },
    methods:{
       buttonOneClick() {
        var _this = this;
        sessionStorage.setItem("business_type", _this.isExpireFlag);
        this.$ajax.post('/myBussiness/getBusiness', {
          'business_type': _this.isExpireFlag,
          'systype': sessionStorage.getItem('systype'),
          "supplement_type":sessionStorage.getItem('systype')
        }).then(function (res) {
          if (res.data.error_no == 0) {
            sessionStorage.setItem('login_type',"2");
            //若是前置任务   强制协议  协议阅读或签署加载强制协议
            if(sessionStorage.getItem('_supplementStatus')=="2" && _this.isExpireFlag == "21" ){
              sessionStorage.setItem('_supplementStatusAgreement', "2");//0:非补签 1:普通补签 2: 强制补签
            }
            _this.$router.push({name: res.data.business_flow, query: {id: Math.random()}});
          } else {
            _this.error_content = res.data.error_info;
            _this.show_content = true;
          }
        }).catch(function (error) {
          alert('前置任务：' + error);
          console.log(error);
        });
      },
       buttonTwoClick(){

      },
      buttonThreeClick(){

      },
      ensure(){
        this.isShow = false;
        if(this.closeFlag){
          window.location.href = "objc://callIOSOut_5";
          return;
        }
        this.$router.push({name: "Indexselection"});
      },
      cancel(){
        this.isShow = false;
        this.closeFlag = false;
      },
       notarize(){
        this.show_content = false;
      },
      goInfo(index){
        sessionStorage.setItem("business_type",index);
        var systype = sessionStorage.getItem("systype_login");
          if(index == "11"|| index == "13" || index == "19"){
              systype = "1";
          }else if(index == "12"){
              systype = "2";
          }
        var _this = this;
        var data = {
          "business_type": index,   //业务选择类型
          'systype': systype,
          "supplement_type":systype
        }
        _this.axios.post('/myBussiness/getBusiness',data).then(function (res) {
          if (res.data.error_no == 0) {
            if(!res.data.business_flow){
              alert('业务功能开发中...');
              return;
            }
            sessionStorage.setItem("systype",systype);
            //非强制签署   这块入口就默认为0  后面协议阅读  协议签署获取普通协议
            sessionStorage.setItem('_supplementStatusAgreement',"0");//0:非补签 1:普通补签 2: 强制补签
            //协议下载不检查前置任务
            if(index == "20" || res.data.to_be_handle_flag == "1" || res.data.id_type=="0"){// to_be_handle_flag 1:有待办业务  0没有待办业务  id_type:0 手机登录  1 身份证登录（手机号登录不做前置检查）
              _this.$router.push({name: res.data.business_flow});
              return;
            }
            sessionStorage.setItem('_idcardFlag', res.data.idcard_flag);//1：过期
            sessionStorage.setItem('_adequacyType', res.data.adequacy_type);//0：未完成
            sessionStorage.setItem('_adequacyFlag', res.data.adequacy_flag);//1：过期
            sessionStorage.setItem('_otherFlag', res.data.other_flag);//1：其他资料不全
            sessionStorage.setItem('_supplementStatus', res.data.supplement_status);//0:非补签 1:普通补签 2: 强制补签
            sessionStorage.setItem('_otherAndSupplement', res.data.other_and_supplement);//1:既有信息不完整又有强制补签
            _this.isExpireFlag = '';
            var idcard = sessionStorage.getItem('_idcardFlag');
            var adeType = sessionStorage.getItem('_adequacyType');
            var adeFlag = sessionStorage.getItem('_adequacyFlag');
            var otherFlag = sessionStorage.getItem('_otherFlag');
            var supplementStatus = sessionStorage.getItem('_supplementStatus');
            var otherAndSupplement = sessionStorage.getItem('_otherAndSupplement');

            if (idcard == '1') {
              _this.isExpireFlag = '23';
              _this.ShowDisplay = true;
              _this.workCenter = "由于您的身份证有效期已过期或有效期缺失，请办理完毕身份证有效期修改后，方可进行其他操作";
              _this.titleName = "提示";
              _this.buttonOneName = "立即处理"
              return;
            }
            if (adeType == '0') {
              _this.isExpireFlag = '24';
              _this.ShowDisplay = true;
              _this.workCenter = "由于您的投资者适当性状态无效，请完成投资者适当性评估后，方可进行其他操作";
              _this.titleName = "提示";
              _this.buttonOneName = "立即处理";
              return;
            }
            if (adeFlag == '1') {
              _this.ShowDisplay = true;
              _this.workCenter = "由于您的投资者适当性已过期，请完成投资者适当性评估后，方可进行其他操作";
              _this.titleName = "提示";
              _this.isExpireFlag = '22';
              _this.buttonOneName = "立即处理"
              return;
            }
            if (otherFlag == '1') {
              _this.ShowDisplay = true;
              _this.workCenter = "由于您的资料信息不完整，请先完成资料信息修改后，方可进行其他操作";
              if(otherAndSupplement ==='1'){
                _this.workCenter = "由于您的资料信息或协议不完整，请先完成资料修改并补签协议后，方可进行其他操作";
              }
              _this.titleName = "提示";
              _this.isExpireFlag = '18';
              _this.buttonOneName = "立即处理";
              return;
            }
            if (supplementStatus == '2') {
              _this.ShowDisplay = true;
              _this.workCenter = "由于您的协议需要签署，请先完成签署协议后，方可进行其他操作";
              _this.titleName = "提示";
              _this.isExpireFlag = '21';
              _this.buttonOneName = "立即处理";
              return;
            }
            _this.$router.push({name: res.data.business_flow});
          } else if(res.data.error_no == "-9999"){
            _this.quit = true;
            _this.content = res.data.error_info;
          }else if (res.data.error_no == -80001) {
            setTimeout(function () {
              _this.show_content = true;
              _this.error_content = res.data.error_info;
            }, 500)
          } else if (res.data.error_no == -80000) {
            setTimeout(function () {
              _this.show_content = true;
              _this.error_content = res.data.error_info;
            }, 500)
          } else if (res.data.error_no == -80003) {
            setTimeout(function () {
              _this.show_content = true;
              _this.error_content = res.data.error_info;
            }, 500)
          } else if (res.data.error_no == -80004) {
            if (res.data.id_type == 0) {
              setTimeout(function () {
                _this.isShow = true;
                _this.closeFlag = false;
                _this.showContents = '该业务需要使用证件号登录，确定使用证件号码登录吗？';
              }, 500)
            } else if (res.data.id_type == 1) {
              setTimeout(function () {
                _this.isShow = true;
                _this.closeFlag = false;
                _this.showContents = '您的账户为休眠状态，不允许使用系统增开交易编码，如有疑问请联系期货公司咨询';
              }, 500)
            }else{
              _this.isShow = true;
              _this.closeFlag = false;
              _this.showContents = res.data.error_info;
            }
          } else if (res.data.error_no == -80005) {
            _this.isShow = true;
            _this.closeFlag = false;
            _this.showContents = res.data.error_info;
          }else{
            _this.show_content = true;
            _this.error_content = res.data.error_info;
          }
        }).catch(function (err) {
          console.log(err)
        });
      },
      getId(index){
        return "div_"+index
      },
      onClickRight(){
         var browserVersion = Utils.iphOrAnz();
         if (browserVersion == "iphone") {
            this.showContents = "是否确认退出开户流程？";
            this.isShow = true;
            this.closeFlag = true;//关闭标识
         }else if (browserVersion == "anzuo"){
           jsHandle.logout();
         }

      }
    },
  }
</script>
<style lang="less">
  .bussiness{
    background: #F6F6F6;
    min-height: 100%;
    .van-hairline--bottom.van-nav-bar {
      background: #49A5FF;
    }
    .bus_type{
      width: 96%;
      height: auto;
      overflow: hidden;
      span.bus_type_name {
        // padding-top:53%;
        display: inline-block;
        margin-top: 36%;
        line-height: 55px;
      }
    }
    .van-col-8 {
      padding: 1rem 0;
      width: 33.33333%;
      height: 33.33%;
      border-right: 1px solid lavender;
      border-bottom: 1px solid lavender;
      i{
        font-size: 30px;
      }
      div{
        font-size: 16px;
        color: #888686;
      }
    }
    .van-nav-bar__title{
       color:white;
     }
    .vancol_account{
      font-size:14px;
      color:#999999;
      text-align:center;
      line-height: 30px;
    }
    // .van-col{
    //   width: 33%;
    // }
    #div_11{
      margin-top: 1%;
      background: url("../../assets/qihuozh.svg") center 28% no-repeat;
    }
    #div_12{
      margin-top: 1%;
      background: url("../../assets/ziguanzh.svg") center 28% no-repeat;
    }
    #div_14{
      margin-top: 1%;
      background: url("../../assets/shidanxpg.svg") center 28% no-repeat;
    }
    #div_13{
      margin-top: 1%;
      background: url("../../assets/jiaoyibm.svg") center 28% no-repeat;
    }
    #div_20{
      margin-top: 1%;
      background: url("../../assets/xieyixz.svg") center 28% no-repeat;
    }
    #div_19{
      margin-top: 1%;
      background: url("../../assets/xiumianhjh.svg") center 28% no-repeat;
    }
    #div_15{
      margin-top: 1%;
      background: url("../../assets/jibenzl.svg") center 28% no-repeat;
    }
  .van-col{
    // width: 33%;
		border: 1px solid #eeeeee;
		border-top: none;
		border-left: none;
		height: auto;
    width: 33.33%;
    overflow: hidden;
	}
  .van-nav-bar__text{
    color:white;
  }
  .highly{
      padding-top: 12%;
    }
  @media only screen and (min-device-width : 768px) and (max-device-width : 1024px){
    .highly{
      padding-top: 6%;
    }
    .bus_type span.bus_type_name{
      margin-top: 28%;
      line-height: 84px;
      font-size: 18px;
    }

  }
  @media only screen and (min-width: 1349px) and (orientation : landscape){
     .highly{
       padding-top: 3%;
     }
     .bus_type span.bus_type_name{
       margin-top: 18%;
       line-height: 105px;
     }
     .uploadImg{
      display: inline-block;
      width: 32%;
      height: auto;
      display: block;
      margin: 0 auto;
     }
   }

}

</style>
