<template>
  <div class="indexselection" style="position: relative">
    <van-nav-bar class="van-nav-bar__title" title="业务选择" right-text="关闭" right-arrow @click-right="onClickRight"/>
    <div class="highly"></div>
    <div class="van-col" v-for="(item,index) in menuList" :key="index"  @click="goInfo(item.business_type)" v-if="item.business_status==8 && (item.business_type=='11' || item.business_type=='13' || item.business_type=='14'|| item.business_type=='20'|| item.business_type=='15' || item.business_type=='12' || item.business_type=='19')">
      <div class="bus_type" :id="getId(item.business_type)" >
        <span class="bus_type_name">{{item.business_name}}</span>
      </div>
    </div>
    <div v-if="ipv6Flag == 1" style="overflow: hidden;font-size: 12px;color:lightgrey;width: 100%;text-align: center;position: absolute;bottom:5px">
      本系统支持IPv6
    </div>
    <quit :content="content" :quit="quit"></quit>
     <confirm :showContents="showContents" :isShow="isShow" :yesButton="yesButton" :noButton="noButton" @ensure="ensureone" @cancel="cancel"></confirm>
     <div class="boxWapr_div" v-if="boxWaprDiv">
            <div class="boxWapr">
                <div class="reminDer">提示</div>
                <p class="conTent">{{contentwork}}</p>
                <button class="update" @click="updates()">更新</button>
                <button class="update" @click="cancelButtons()" v-if="cancelButton">取消</button>
                <button class="update" v-if="dropOut" @click="ensureone()">退出</button>
            </div>
        </div>
        <div class="boxWapr_div" v-if="oneboxWaprDiv">
            <div class="boxWapr">
                <div class="reminDer">提示</div>
                <p class="conTent">{{contentworks}}</p>
                <button class="update" @click="ensureone()">确定</button>
            </div>
        </div>
  </div>
</template>

<script>
  import { Row, Col } from 'vant';
  import { Tabbar, TabbarItem, Group, Cell, } from 'vux'
  import Quit from "../../components/quit.vue";
  import Utils from '../../util/iphoneOrAndroid';
  import Confirm from "../../components/confirm.vue";
  export default {
    components: {Tabbar,TabbarItem,Group,Cell,Row,Col,Quit,Confirm},
    data(){
        return{
          oneboxWaprDiv: false,
          contentworks:"",
          content:"",
          contentwork:"",
          boxWaprDiv: false,
          menuList:[],
          quit:false,
          showContents:"",
          isShow:false,
          yesButton:"确定",
          noButton:"取消",
          cancelButton: false,
          dropOut: false,
          exitapp: false,
          resgisterWay:"",
          ipv6Flag:0
        }
    },
    created(){
      var  _this = this;
      window["callbackOne"] = function (resgisterWay,versionCode) {
          _this.callbackOne(resgisterWay,versionCode)
      };

      var _clientIP = sessionStorage.getItem('_clientIP');
      if(_clientIP == undefined || _clientIP == null){
        _clientIP = ''
      }
      _this.ipv6Flag = 0;
      //判断期货公司不存在
      var data = {
        "broker_id":sessionStorage.getItem('broker_id'),
        "clientIP":_clientIP
      }
      _this.$ajax.post('/initAppPage', data).then(function(res){
        if(res.data.error_no == 0){
          _this.menuList = res.data.menu_list;
          _this.ipv6Flag = res.data.ipv6flag;
          sessionStorage.setItem("broker_hotline",res.data.broker_hotline);
          sessionStorage.setItem('enable_write_sign',res.data.enable_write_sign)
        }else{
          _this.quit = true;
          _this.content = res.data.error_info;
          return false
        }
      }).catch(function (err) {
        console.log(err);
      });
      _this.getAppVersion();
    },
    methods:{
      dropOuts(){
        var browserVersion = Utils.iphOrAnz();
        if (browserVersion == "iphone") {
          window.location.href = "objc://callIOSOut_5";
        } else if (browserVersion == "anzuo") {
          jsHandle.logoutRelease();
        } else if (browserVersion == "windows") {
          console.log("windows");
        }
      },
        updates(){
          //this.boxWaprDiv = false;
          var browserVersion = Utils.iphOrAnz();
          if (browserVersion == "iphone") {
            window.location.href = "objc://updateApp/$?"+ this.downloadUrl
          } else if (browserVersion == "anzuo") {
            jsHandle.updateApp("0", this.downloadUrl)
          } else if (browserVersion == "windows") {
            console.log("windows");
          }
        },
        cancelButtons(){
           this.boxWaprDiv = false;
       },
        openAccount(){
        },
        goInfo(index){
            sessionStorage.setItem("business_type",index);
            if(index == '11' || index == '12'){
              this.$router.push('/canahartaLogin')
            }else{
               this.$router.push('/register')
            }
        },
        getId(index){
            return "div_"+index
        },
        onClickRight(){
         var browserVersion = Utils.iphOrAnz();
              if (browserVersion == "iphone"){
                 this.showContents = "是否确认退出开户流程？";
                 this.exitapp = true;
                  this.isShow = true;
              } else if (browserVersion == "anzuo") {
                jsHandle.logout();
              } else if (browserVersion == "windows") {
                console.log("windows");
              }
        },
        cancel(){
          this.exitapp = false;
          this.isShow = false;
          // this.updates();
        },
        ensureone(){
          this.isShow = false;
          this.oneboxWaprDiv = false;
          if(this.exitapp == true){
              var browserVersion = Utils.iphOrAnz();
              if (browserVersion == "iphone"){
                this.exitapp = false;
                window.location.href = "objc://callIOSOut_5";
              } else if (browserVersion == "anzuo") {
                jsHandle.logoutRelease();
              } else if (browserVersion == "windows") {
                console.log("windows");
              }
          }
        },
        ensure(){
          this.isShow = false;
          var browserVersion = Utils.iphOrAnz();
          if (browserVersion == "iphone"){
            window.location.href = "objc://callIOSOut_5";
          } else if (browserVersion == "anzuo") {
            jsHandle.logoutRelease();
          } else if (browserVersion == "windows") {
            console.log("windows");
          }
        },
         getAppVersion(){
            var browserVersion = Utils.iphOrAnz();
            if (browserVersion == "iphone") {
              window.location.href = "objc://getVersionCode_5?callbackOne"
            } else if (browserVersion == "anzuo") {
                jsHandle.getAppVersion("window.callbackOne");
            } else if (browserVersion == "windows") {
                console.log("windows");
            }
        },
        callbackOne(resgisterWay,versionCode){
          console.log(resgisterWay,'resgisterWay')
            var _this = this;
         if(resgisterWay == '4' || resgisterWay === '(null)' || resgisterWay === (null)){ //ios skd
            resgisterWay = '6';
          }
            sessionStorage.setItem('versionCode',versionCode);
            _this.axios.post('/app/checkVersion', {"clientId":resgisterWay, "versionCode":versionCode}).then(function (res) {
              if(res.data.error_no == 0){
                   _this.exitapp = false;
                    _this.downloadUrl = res.data.downloadUrl;
                    if(resgisterWay == '1'){ // app
                      resgisterWay = '2';
                    }
                    sessionStorage.setItem('resgisterWay',resgisterWay);
                    if(res.data.versionStatus == "1"){//versionStatus  0:无更新  1非强制更新 2 强制更新
                        _this.boxWaprDiv = true;
                        _this.cancelButton = true;
                        _this.contentwork = "发现更新版本，建议更新后使用";
                    }else if(res.data.versionStatus == "2"){
                        _this.boxWaprDiv = true;
                        _this.contentwork = "发现更新版本，请更新后使用";
                        _this.cancelButton = false;
                        _this.dropOut = true;
                        _this.exitapp = true;
                    }
                }else if(res.data.error_no == -2){ //  error_no -2  表示sdk版本不符合规范
                  _this.cancelButton = false;
                  _this.oneboxWaprDiv = true;
                  _this.contentworks = res.data.error_info;
                  if(res.data.update_flag){
                    _this.exitapp = true;
                    return;
                  }
                  _this.exitapp = false;
                }else{
                   _this.cancelButton = false;
                   _this.exitapp = false;
                    _this.isShow = true;
                    _this.showContents = res.data.error_info;
                }
            }).catch(function (err) {
                console.log("error:"+err);
            });
        },

    }
  }
</script>

<style lang="less">
  .indexselection{
    background: #F6F6F6;
    height: 100%;

    .bus_type{
        width: 96%;
        height: auto;
        overflow: hidden;
            span.bus_type_name {
                display: inline-block;
                margin-top: 36%;
                line-height: 55px;
            }
    }
    .van-hairline--bottom.van-nav-bar {
        background: #49A5FF;
    }
    #div_11{
        background: url("../../assets/qihuozh.svg") center 30% no-repeat;
    }
    #div_12{
        background: url("../../assets/ziguanzh.svg") center 30% no-repeat;
    }
    #div_14{
        background: url("../../assets/shidanxpg.svg") center 30% no-repeat;
    }
    #div_13{
        background: url("../../assets/jiaoyibm.svg") center 30% no-repeat;
    }
    #div_20{
      background: url("../../assets/xieyixz.svg") center 30% no-repeat;
    }
    #div_19{
      background: url("../../assets/xiumianhjh.svg") center 30% no-repeat; //修改结算账户
    }
    #div_15{
        background: url("../../assets/jibenzl.svg") center 30% no-repeat;
    }
    i.van-icon.van-icon-records{
        color:#56C2FE;
        line-height: 36px;
        padding-top: 17%;
        font-size: 2.3rem;
    }
    .van-nav-bar__title{
        color: #ffffff;
        max-width: 100%;
        font-size: 16px;
    }
    .vancol_account{
        font-size:14px;
        color:#999999;
        text-align:center;
        line-height: 30px;
    }
    .van-col{
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
    .boxWapr_div {
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.5);
        position: absolute;
        top: 0;
        z-index: 999;
    }
    .boxWapr{
        width: 80%;
        height: auto;
        background: white;
        margin: 65% auto;
    }
    .reminDer{
        background: #49a5ff;
        line-height: 34px;
        color: white;
        font-size: 16px;
    }
    .conTent{
        line-height: 64px;
        font-size: 16px;
    }
    .update{
        width: 20%;
        background: #49a5ff;
        border: none;
        line-height: 29px;
        color: white;
        border-radius: 3px;
        margin-bottom: 5%;
    }
    .highly{
      padding-top: 12%;
    }
    @media only screen and (min-device-width : 768px) and (max-device-width : 1024px){
      .highly{
        padding-top: 6%;
      }
      .bus_type_name{
        font-size: 23px;
      }
      .van-nav-bar__title{
        font-size: 24px;
      }
      .van-nav-bar__text{
        font-size: 24px;
      }
    }
    @media only screen and (min-device-width: 1366px) and (orientation: landscape) {
       .highly{
        padding-top: 3%;
      }
      .bus_type span.bus_type_name{
        margin-top: 17%;
        line-height: 102px;
      }
    }



  }
</style>
