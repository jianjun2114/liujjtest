<template>
    <div class="cert">
        <van-nav-bar
            title="数字证书"
            right-text="关闭"
            right-arrow
            @click-right="onClickRight"/>
        <!--导航条-->
        <navigation/>
        <div v-if="showPer" style="margin-top: 30px">
            <van-progress :percentage="per" style="width: 80%;margin-left: 10%"/>
        </div>
        <div class="password">
            <van-cell-group v-show="enable">
              <van-field v-model="password"  type="password" maxlength="8" placeholder="请输入6-8位数字" @blur="setPsd()"/>
              <van-field v-model="password2" type="password" maxlength="8" placeholder="请再次输入" @blur="setPsd2()"></van-field>
            </van-cell-group>
          <div class="input-group" v-show="gm_enable">
            <button v-bind:class="{ register_msg_btn: true, registerbag:isLoad }"  v-on:click="getCode" v-if="show === true">{{mesg}}</button>

            <button v-bind:class="{ register_msg_btn: true, registerbag:isLoad , datetcolor: true}" v-else>{{count}}秒后重新获取</button>
            <van-field v-model="messageCode" maxlength="6" class="authCode" placeholder="请输入验证码"/>
            <span class="glyphicon glyphicon-font form-control-feedback"/>
        </div>
        </div>
        <div class="promptTips2">1、您的数字证书不存在，请申请安装数字证书。数字证书是唯一识别的凭证，开立账户需具备完整的验证环节，数字证书类似银行网银验证，提供安全保障</div>
        <div class="promptTips2">2、申请安装证书需要设置密码，您设置的证书密码在之后流程使用，请牢记证书密码并且不要外传。</div>
        <van-button  bottom-action  @click="getCert" class="nextBtn" style="width: 82%" v-if="btn">{{btnText}}</van-button>
      <!-- <van-button  bottom-action  @click="skipVideo" class="nextBtn" style="width: 82%" v-if="btn">测试跳过</van-button> -->
        <popUp :tips="error_content" :showBox="show_content" @notarize="notarize"></popUp>
        <quit :content="content" :quit="quit"></quit>
        <waitPop :loadingbox="loadingbox"></waitPop>
        <div class="quit">
          <van-popup v-model="certdiag_quit" :close-on-click-overlay="false">
              <div class="hint">请输入证书密码</div>
             <van-field v-model="password"  type="password" maxlength="8" placeholder="请输入6-8位数字" @blur="setPsd()"/>
             <van-field v-model="password2" type="password" maxlength="8" placeholder="请再次输入" @blur="setPsd2()"></van-field>
             <div class="ok" @click="certsure">确认</div>
             
          </van-popup>
      </div>
    </div>
</template>
<script type="text/ecmascript-6">
import WaitPop from "../../components/waitPop.vue";
import Utils from '../../util/iphoneOrAndroid';
import Quit from "../../components/quit.vue";
import PopUp from "../../components/popUp.vue";
import Navigation from "../../components/navigation.vue";
  export default {
	components: {
    Quit,
    PopUp,
    WaitPop,
    Navigation
  },
    data(){
      return{
        loadingbox:false,
        btn:true,
        quit:false,
        content:"",
        error_content:"",
        show_content: false,
        password:'',
        password2:'',
        count: "",
        per:0,
        certdiag_quit:false,
        // nextStep:'下一步',
        showPer:false,
        btnText:'申请并安装',
        flag:'1',
        accept_no:"",
        submitFlag:true,
        timer:0,
        mesg:"短信验证码",
        show: true,
        isLoad: true,
        enable_gm:"",
        enable:false,
        gm_enable:false,
        cert_status:"",
        gm_username:"",
        messageCode:"",
        gmSignCode:"",
        realName:"",
      }
    },
    created(){
     // 开始
      var _this = this;
      _this.qrCode ='data:image/png;base64,'+sessionStorage.getItem("qrCode");
      var data = {
        "business_type": sessionStorage.getItem('business_type'),
      };
      _this.axios.post('/flow/qryAcceptInfo',data).then(function (res) {
        if(res.data.error_no == 0){
          _this.accept_no =res.data.accept_no;
          _this.business_flow=res.data.business_flow;
          if(_this.$router.history.current.name != res.data.business_flow){
            _this.$router.push(res.data.business_flow)
          }
        }else if(res.data.error_no == "-9999"){
           _this.quit = true;
           _this.content = res.data.error_info;
        }else{
          _this.show_content = true;
          _this.error_content = res.data.error_info;
        }
      }).catch(function (err) {
          console.log(err)
      });
      var browserVersion = Utils.iphOrAnz();
      _this.$ajax.post('/cert/getSn',{"business_type":sessionStorage.getItem("business_type"),device:browserVersion == "anzuo" ? '1':'2'}).then(function (res) {
        console.log(res)
        _this.enable_gm = res.data.enable_gm;
       _this.gm_username = res.data.gm_username;
        if(_this.enable_gm !== '1' || !_this.enable_gm){
          _this.enable = true;
            return;
        } else {
          _this.gm_enable = true;
         let obj = {
           status:"1",
           callback:"gmcallback",
           appId:res.data.gm_app_id,
           base64HttpsCert:res.data.gm_mAuth_base64_cert,
           mAuthUrl:res.data.gm_mAuth_url,
           appSecret:res.data.gm_secret_key,
           gmUserName:res.data.gm_username
         }
          //调用安卓 ios windows方法
        
        if (browserVersion == "iphone") {
            window.location.href = "objc://certGM/?"+ window.btoa(JSON.stringify(obj));
        } else if (browserVersion == "anzuo") {
            console.log("anzuo");
            jsHandle.certGM(JSON.stringify(obj))
        } else if (browserVersion == "windows") {
            console.log("windows");
        }
        }
        
      });
    // 结束
	  //注册回调
	  var _this = this;
	  window['certCallback'] = function(status,result){
		  _this.certCallback(status,result);
	  };
    window['gmcallback'] = function(status,result,err){
		  _this.gmcallback(status,result,err);
	  };
    },
    
    beforeDestroy(){
	  clearTimeout(this.timer);
    },
    mounted() {
    },
    methods:{
      notarize(){
        this.show_content = false;
      },
      perClose(action,done){
        done();
      },
       //短信验证码
      getCode() {
        var _this = this;
        _this.messageCode = "";
        var postData = {
          "business_type":sessionStorage.getItem("business_type")
        }
        _this.loadingbox = true;
        _this.$ajax.post('/cert/sendSms', postData).then(function (res) {
          _this.loadingbox = false;
          if (res.data.error_no == 0) {
            _this.showTimer();
            _this.isSmsSuccusePhoneTips = true;
            _this.smsSuccusePhoneTip = '您好，动态验证码（切勿告知他人）已发送至您的手机，请注意查收。如未能收到验证码，请使用语音验证码或拨打期货公司电话：'+_this.broker_hotline;
          }else if(res.data.error_no == "-9999"){
            _this.quit = true;
            _this.content = res.data.error_info;
          }else if(res.data.error_no == "-8888"){
            _this.quit = true;
            _this.content = res.data.error_info;
          } else {
            _this.isDisabled = false;
            _this.show_content = true;
            _this.error_content = res.data.error_info;
            return false
          }
        }).catch(function (err) {
          _this.isDisabled = false;
          console.log(err)
        });
      },
      showTimer() {
         var _this = this;
         _this.isDisabled = false;
         _this.isLoad=false;
         _this.show = false;
        const TIME_COUNT = 60;
        if (!_this.timer) {
          _this.count = TIME_COUNT;
          _this.timer = setInterval(() => {
            if (_this.count > 0 && this.count <= TIME_COUNT) {
              _this.count--;
            } else {
              _this.show = true;
              _this.mesg = "重新获取验证码";
              _this.isDisabled = false;
              _this.isLoad=true;
              _this.isCloudSms = true;  // 开启语音验证码
              clearInterval(this.timer);
              _this.timer = null;
            }
          }, 1000)
        }
      },
	  getCert(){
      
      if (this.enable_gm == '1') {
            let _this = this;
            if( this.show === true) {
                this.show_content = true;
                 this.error_content = "请先获取短信验证码";
                return false 
            }
            if (this.messageCode === '') {
                this.show_content = true;
                this.error_content = "短信验证码为空请获取短信验证码";
                return false
            }
               _this.loadingbox = true;
                _this.$ajax.post('/cert/applyCert',{"cert_status":_this.cert_status,"validate_code":_this.messageCode}).then(function (res) {
                _this.loadingbox = false;
                if(res.data.error_no == 0){
                  _this.certdiag_quit = true;
                  _this.gmSignCode = res.data.gm_sign_code;
                  _this.realName = res.data.realName;
                }else{
                  _this.show_content = true;
                  _this.error_content = res.data.error_info;
                  _this.btnText='重新申请';
                  _this.flag = '1';
                  _this.per = 0;
                  }
                }).catch(function (error) {
                  _this.loadingbox = false;
                  _this.show_content = true;
                  _this.error_content = "申请证书失败,请重新申请！"
                  _this.btnText='重新申请';
                  _this.flag = '1'
                  _this.per = 0;
                });

        
      } else {
          var reg =/^[0-9]{6,8}$/;
          if(this.password == "" || !reg.test(this.password)){
            this.show_content = true;
            this.error_content = "请输入6-8位数字";
            return false
          
          }
          if(this.password2 == ""){
            this.show_content = true;
            this.error_content = "请再次输入密码";
            return false
          }
          if(this.password2 != this.password){
            this.show_content = true;
            this.error_content = "两次输入不一致"
            return false
          }
          //防止重复提交    begin   5秒内连续点击只发一次请求
          var _this = this;
          if(!_this.submitFlag){
            return;
          }
          _this.submitFlag = false;
          _this.timer =  setTimeout(function() {
            _this.submitFlag  = true;
          }, 5000);
          
          //防止重复提交    end
            if(this.flag == '1'){
              this.showPer = true;
              this.per=20;
            //调用安卓 ios windows方法
              var browserVersion = Utils.iphOrAnz();
              if (browserVersion == "iphone") {
                console.log("iphone");
                window.location.href = "objc://callIOSCertPass_5/?"+this.password2+"?certCallback";
              } else if (browserVersion == "anzuo") {
                console.log("anzuo");
              jsHandle.certPassword("1","window.certCallback",this.password2);
              } else if (browserVersion == "windows") {
                console.log("windows");

              }
            }
      
          }
     
      },
      certsure() {
          var reg =/^[0-9]{6,8}$/;
            if(this.password == "" || !reg.test(this.password)){
              this.show_content = true;
              this.error_content = "请输入6-8位数字";
              return false
            }
            if(this.password2 == ""){
              this.show_content = true;
              this.error_content = "请再次输入密码";
              return false
            }
            if(this.password2 != this.password){
              this.show_content = true;
              this.error_content = "两次输入不一致"
              return false
            }
            //防止重复提交    begin   5秒内连续点击只发一次请求
            var _this = this;
            if(!_this.submitFlag){
              return;
            }
            _this.submitFlag = false;
            _this.timer =  setTimeout(function() {
              _this.submitFlag  = true;
            }, 5000);
            
            //防止重复提交    end
            if(_this.flag == '1'){
                _this.showPer = true;
                _this.per=20;
            // setTimeout(()=>{
            //     _this.certsure();
            // },500)
            
            let obj = {
                status:"2",
                gmSignCode:_this.gmSignCode,
                realName:_this.realName,
                password:_this.password,
                callback:"gmcallback",
                gmUserName:_this.gm_username
            }
            
            const Base64 = require('js-base64').Base64;
            var browserVersion = Utils.iphOrAnz();
            if (browserVersion == "iphone") {
                window.location.href = "objc://certGM/?"+ Base64.encode(JSON.stringify(obj));
            } else if (browserVersion == "anzuo") {
                jsHandle.certGM(JSON.stringify(obj));
            } else if (browserVersion == "windows") {
              console.log("windows");
            }
                
            }
           
      },
      
      gmcallback(status,result,err) {
         var _this = this;
         switch(status) {
           
           case '1':
             _this.cert_status = result;
             if (result == '0') {
                 _this.certdiag_quit = false;
             }else {
                
             }
           break;
           case '2':
             if (result === '0') {
                this.certdiag_quit = false;
                let err_info = JSON.parse(err);
                this.loadingbox = true;
                let _this = this;
                _this.$ajax.post('/cert/setSn',{
                  "business_type":sessionStorage.getItem("business_type"),
                  "certSn":err_info.certSn,
                  "certDn":err_info.certDn,
                  "certEndTime":err_info.certEndTime,
                  "certBase64":err_info.certBase64
                
                }).then(function (res) {
                     if (res.data.error_no == 0) {
                         _this.certCallback('2','0')
                         
                     } else {
                      _this.loadingbox = false;
                      _this.show_content = true;
                      _this.certdiag_quit = false;
                      _this.error_content = res.data.error_info;
                      _this.password = "";
                      _this.password2= "";
                        return false
                     }
                });
              } else {
                _this.certdiag_quit = false;
                  _this.show_content = true;
                  _this.error_content = err;
                  _this.password = "";
                  _this.password2= "";
                  return false
              }
         }
      },
	  certCallback(status,result){
	    var _this = this;
      _this.per=50;
        switch (status){
          case '1': //csr串
        _this.loadingbox = true;
			  _this.$ajax.post('/cert/applyCert',{"csr":result}).then(function (res) {
        _this.loadingbox = false;
				if(res.data.error_no == 0){
				  var p7_cert = res.data.p7_cert;
				  var ca_sn = res.data.ca_sn;
				  _this.per=70;
           var browserVersion = Utils.iphOrAnz();
            if (browserVersion == "iphone") {
              window.location.href = "objc://callIOSSaveCert_5/?"+ca_sn+"?"+p7_cert+"?certCallback";   //保存证书
            } else if (browserVersion == "anzuo") {
              jsHandle.certSave('2','window.certCallback',ca_sn,p7_cert);
            } else if (browserVersion == "windows") {
                console.log("windows");
            }
				  // jsHandle.certSave('2','window.certCallback',ca_sn,p7_cert);
				}else{
          _this.show_content = true;
          _this.error_content = "申请证书失败,请重新申请!"
				  _this.btnText='重新申请';
				  _this.flag = '1';
				  _this.per = 0;
          }
			  }).catch(function (error) {
          _this.loadingbox = false;
          _this.show_content = true;
          _this.error_content = "申请证书失败,请重新申请！"
          _this.btnText='重新申请';
          _this.flag = '1'
          _this.per = 0;
			  });
        break;
        case '2':
         var versionCode = sessionStorage.getItem('versionCode');
          if(parseInt(versionCode)>=20200101){
            if(result != "null" && result != '0'){ //本地保存证书失败
              _this.show_content = true;
              _this.error_content = "申请证书失败,请重新申请！"
              _this.btnText='重新申请';
              _this.flag = '1'
              _this.per = 0;
              return false;
            }
          }
        this.per=100;
        // this.btnText = '下一步'
        this.flag='0'
        var _this = this;
        _this.btn = false;
        if (_this.accept_no != "" || _this.accept_no != undefined){
        var data = {
          "accept_no": _this.accept_no,
          "business_type":sessionStorage.getItem('business_type'),
          "business_button_type":'1',//下一步
          "business_flow": _this.$router.history.current.name,
          "accept_status":""  //"accept_status":_this.accept_status,当需要更改状态时再传值
        };
          _this.loadingbox = true;
        _this.$ajax.post('/flow/updateAccept',data).then(function (res) {
          _this.loadingbox = false;
          if(res.data.error_no == 0){
              _this.$router.push({name:res.data.business_flow});
          }else if(res.data.error_no == '-9999' ){
              _this.quit = true;
              _this.content = res.data.error_info;
          }else{
              _this.show_content = true;
              _this.error_content = res.data.error_info;
          }
        }).catch(function (err) {
            _this.loadingbox = false;
            console.log(err)
        });
      }
        break;
        case '3':
        break;
        case '4':
        break;
        }
      },
      onClickRight(){
        this.$router.push({name:'MyBussiness'})
      },
      setPsd(){
        var reg =/^[0-9]{6,8}$/;
        if(this.password == "" || !reg.test(this.password)){
          this.show_content = true;
          this.error_content = "请输入6-8位数字";
        }
      },
	  setPsd2(){
      if(this.password2 == ""){
        this.show_content = true;
        this.error_content = "请再次输入密码";
        return false
      }
      if(this.password2 != this.password){
        this.show_content = true;
        this.error_content = "两次输入不一致";

      }
      },
       skipVideo(){
        var data = {
          "accept_no": this.accept_no,
          "business_type": sessionStorage.getItem("business_type"),
          "business_button_type": '1',//上一步
          "business_flow": this.$router.history.current.name,
          "accept_status": ""
        };
        var _this = this;
        _this.loadingbox = true;
        this.$ajax.post('/flow/updateAccept', data).then(function (res) {
          _this.loadingbox = false;
          if (res.data.error_no == 0) {
            _this.$router.push({name: res.data.business_flow});
          } else if (res.data.error_no == "-9999") {
            _this.quit = true;
            _this.content = res.data.error_info;
          } else {
            _this.show_content = true;
            _this.error_content = res.data.error_info;
          }
        }).catch(function (err) {
          console.log(err)
        });
      }
    }
  }
</script>
<style lang="less" scoped>

.cert{
  position: relative;
  .certdiag{
    width: 220px;
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%,-50%);
    padding: 5px;
    p{
      text-align: center;
    }
  }
  .password{
    margin-top: 20px;
    margin-bottom: 5px;
    width: 85%;
    margin-left: 7.5%;
    border-radius: 30px
  }
  .promptTips2{
    width: 85%;
    font-size: 14px;
    color: #acacb1;
    text-align: left;
    margin-top: 5%;
    margin-bottom: 4%;
    margin-left: 7.5%;
  }

}
.register_msg_btn {
      width: 33%;
      height: 69%;
      right: 5%;
      top: 17%;
      position: absolute;
      font-size: 12px;
      border: none;
      border-radius: 3px;
      color: white;
      z-index: 99;
    }
    .registerbag{
      background: #49a5ff;
    }
    .datetcolor{
      background: #cccccc;
    }
.input-group {
      width: 98%;
      border-bottom: 1px solid #eeeeee;
      position: relative;
      display: flex;
    }
    .quit{
      width: 300px;
      padding: 5px;
    .van-popup{
        width: 90%;
    }
    .ok{
        width: 22%;
        color:#ffffff;
        background: #49a5ff;
        text-align: center;
        line-height: 35px;
        border-radius: 4px;
        margin: 13px auto;
        margin-bottom: 12px;
    }
    .hint{
        text-align: center; 
        font-size: 16px; 
        color:#333333; 
        width:100%; 
        background:#49a5ff; 
        line-height: 37px; 
        color:#ffffff;
    }
    
}
</style>
