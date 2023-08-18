<template>
    <div class="index">
        <div class="header">
            <van-nav-bar title="选择期货公司" right-text="关闭" @click-right="onClickRight()"/>
        </div>
        <div style="margin-top: 4rem">
            <div class="div_span">方式一: 请输入期货公司的编码,并继续开户</div>
            <van-field type="number" maxlength="4" placeholder="请输入期货公司编号" class="div_input" v-model="broker_id"/>
            <div class="div_span">方式二： 使用二维码扫描进行开户</div>
            <img @click="photo" src="../../assets/QRcode.png" alt="" style="margin-top: 3rem; width: 10rem;">
            <p>点击上面图标进行二维码扫描</p>
            <van-button class="van-button" @click="NextStep()">业务办理</van-button>
        </div>
         <popUp :tips="error_content" :showBox="show_content" @notarize="notarize"></popUp>
        <quit :content="content" :quit="quit"></quit>
        <waitPop :loadingbox="loadingbox"></waitPop>
        <confirm :showContents="showContents" :isShow="isShow" :yesButton="yesButton" :noButton="noButton" @ensure="ensure" @cancel="cancel"></confirm>
        <div class="boxWapr_div" v-if="boxWaprDiv">
            <div class="boxWapr">
                <div class="reminDer">提示</div>
                <div class="conTent">发现更新版本，建议更新后使用</div>
                 <button class="update" @click="updates()">更新</button>
                <button class="update" @click="cancelButtons()" v-if="cancelButton">取消</button>
                <button class="update" v-if="dropOut" @click="dropOuts()">退出</button>
            </div>
        </div>
    </div>
</template>
<script>
import Utils from '../../util/iphoneOrAndroid';
import WaitPop from "../../components/waitPop.vue";
import {Vant, Toast,Loading} from 'vant';
import Quit from "../../components/quit.vue";
import PopUp from "../../components/popUp.vue";
import Confirm from "../../components/confirm.vue";
  export default {
	components: {
       Loading,
       Quit,
       WaitPop,
       PopUp,
       Confirm
    },
    data(){
	  return{
        showContents:"",
        isShow:false,
        yesButton:"确定",
        noButton:"取消",
        cancelButton: true,
        boxWaprDiv: false,
        dropOut: false,
        error_content:"",
        show_content: false,
        quit:false,
        content:"",
        broker_id:"",
		bus_name:'',
        loadingbox: false,
        channel:"",
        downloadUrl:""
      }
    },
    created(){
	    var _this = this;
        window["qrCode"] = function (picStr) {
            _this.qrCode(picStr);
        };
	    if(_this.$route.query.brokerId != undefined){
	      _this.broker_id = _this.$route.query.brokerId;
          if(_this.$route.query.channel != undefined){
            _this.channel = _this.$route.query.channel;
            sessionStorage.setItem('channel',_this.channel);
          }
          console.log('###################################################################')
          console.log('###################################################################')
          console.log('###################################################################')
          console.log(_this.$route.query.clientIP)
          console.log('###################################################################')
          console.log('###################################################################')
          if(_this.$route.query.clientIP != undefined){
            sessionStorage.setItem('_clientIP',_this.$route.query.clientIP);
          }
        sessionStorage.setItem('broker_id',_this.broker_id);
        _this.$router.push({name:'Indexselection',})
      }
    },
	methods:{
        updates(){
            this.downloadUrl;
        },
        cancelButtons(){
           this.boxWaprDiv = false;
       },
       dropOuts(){
            var browserVersion = Utils.iphOrAnz();
            if (browserVersion == "iphone") {
                window.location.href = "objc://callIOSOut_5";
            } else if (browserVersion == "anzuo") {
                jsHandle.logout();
            } else if (browserVersion == "windows") {
                console.log("windows");
            }
       },
        //安卓二维码回调
        qrCode(picStr){
            var _this = this;
            var arr = picStr.split("&");
            for (var i=0;i<arr.length;i++){
                if(arr[i].split("=")[0] == "channel"){
                    _this.channel = arr[i].split("=")[1];
                }
                if(arr[i].split("=")[0] == "brokerId"){
                    _this.broker_id = arr[i].split("=")[1];
                }
            }
            if(picStr != ""){
                if(_this.broker_id == ""){
                    _this.show_content = true;
                    _this.error_content = "二维码无效";
                }else{
                    _this.NextStep();
                    // _this.broker_id = "";
                }
            }
        },
      notarize(){
        this.show_content = false;
       },
        //二维码扫描
        photo(){
          //调用安卓 ios windows方法
          var browserVersion = Utils.iphOrAnz();
            if (browserVersion == "iphone") {
                window.location.href = "objc://QRCode_5?parm1&parm2";
            } else if (browserVersion == "anzuo") {
                console.log("anzuo");
                 jsHandle.qrCode('window.qrCode');
            } else if (browserVersion == "windows") {
                console.log("windows");
            }
        },
        onClickRight(){
            this.showContents = "是否确认退出开户流程？";
            this.isShow = true;
        },
        cancel(){
            this.isShow = false;
        },
        ensure(){
            this.isShow = false;
            var browserVersion = Utils.iphOrAnz();
            if (browserVersion == "iphone") {
            window.location.href = "objc://callIOSOut_5";
            } else if (browserVersion == "anzuo") {
            jsHandle.logout();
            } else if (browserVersion == "windows") {
            console.log("windows");
            }
        },
     //下一步方法
        NextStep(){
            if ( !this.broker_id ) {
               this.show_content = true;
               this.error_content = "请输入期货公司编号";
               return
            }
            this.loadingbox = true;
            var _this = this;
            var _clientIP = sessionStorage.getItem('_clientIP');
            if(_clientIP == undefined || _clientIP == null){
              _clientIP = ''
            }
            //判断期货公司不存在
            var data = {
              "broker_id": _this.broker_id,
              "clientIP":_clientIP
            }
            _this.axios.post('/initAppPage',data).then(function (res) {
                _this.loadingbox = false;
                if(res.data.error_no == 0){
                   sessionStorage.setItem('broker_id',_this.broker_id);
                   sessionStorage.setItem('channel', _this.channel);
                   _this.$router.push({name:'Indexselection'})
                }else if(res.data.error_no == -9999){
                    _this.quit = true;
                    _this.content = res.data.error_info;
                }
                else{
                    _this.show_content = true;
                    _this.error_content = res.data.error_info;
                    _this.loadingbox = false;
                }
            }).catch(function (err) {
                console.log("error:"+err);
            });
        },
    },
  }
</script>

<style lang="less">
 .index{
    background: #F0EFF4;
    height: 100%;
    overflow: hidden;
    .div_input{
        width: 90%;
        margin: 0 auto;
        border: none;
        font-size: 16px;
        padding: 1rem 0 0.7rem 0;
        border-bottom: 0.1rem solid #e5e5e5;
        border-top: 0.1rem solid #e5e5e5;
        text-indent: 2.2rem;
        margin-top: 2rem;
    }
    .van-button{
        width: 90%;
        color: white;
        margin-top: 9%;
        background: #49a5ff;
        font-size: 15px;
        text-align: center;
        line-height: 20px;
        border-radius: 0.3rem;
    }
    input::-webkit-input-placeholder {
         color: #cccccc;
     }
    p{
        font-size: 14px;
        color: #999999;
        text-align: center;
        line-height: 1rem;
        padding-top: 1.4rem;
     }
    .div_span{
        width: 90%;
        text-align: left;
        margin: 0 auto;
        font-size: 1.5rem;
        color: #999999;
        line-height: 24px;
        padding-top: 2rem;
    }
    .van-popup{
        width: 80%;
    }
    .van-field__control{
        border: 0;
        margin: 0;
        padding: 0;
        width: 100%;
        resize: none;
        display: block;
        box-sizing: border-box;
        background-color: transparent;
        text-indent: 12px;
    }
    .boxWapr_div{
        width: 100%;
        height: 100%;
        background: rgba(0,0,0,0.5)
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

}
</style>
