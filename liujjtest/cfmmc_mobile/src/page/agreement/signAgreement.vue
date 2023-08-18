<template>
  <div class="threeAgreement">
    <van-nav-bar
      title="签署协议"
      right-text="关闭"
      right-arrow
      @click-right="onClickRight"/>
    <!--导航条-->
    <navigation/>
    <div class="chooseAccountCommon">请阅读相关协议</div>
    <div class="agreement" v-for="(item,index) in agreementList" :key='index' @click="readIt(item)">
      <div>
        <a :id="item.id">《{{item.name}}》</a>
      </div>
      <van-icon name="arrow"/>
    </div>
    <van-checkbox v-model="checked" @change="agree" ><span class="aaa">我已阅读并同意以上所有协议和业务规则内容</span></van-checkbox>
    <div class="chooseAccountCommon" v-if="isbroker === 'Y'">期货居间投资者风险告知书</div>
    <div class="agreement" v-for="(item,index) in agreementbrokerList" :key='index' @click="readIt(item)">
      <div>
        <a :id="item.id">《{{item.name}}》</a>
      </div>
      <van-icon name="arrow"/>
    </div>
    <van-checkbox v-model="checkeds" @change="agrees" v-if="isbroker === 'Y'"><span class="aaa" style="margin-left:-10px" v-if="isbroker === 'Y'">我已阅读并同意期货居间投资者风险告知书内容和规则</span></van-checkbox>
    <van-button bottom-action @click="threeAgreement" class="nextBtn">签署协议</van-button>
   <!-- <van-button bottom-action @click="goTest" class="nextBtn">测试跳过</van-button> -->
       <div class="pop_up">
      <van-popup v-model="showBox" :close-on-click-overlay="false">
        <div class="hint">提示</div>
        <p style="text-align: left; font-size: 16px; color:#333333; line-height: 37px;">{{tips}}</p>
        <div class="ok" @click="acknowledgement">确定</div>
      </van-popup>
    </div>
    <popUp :tips="error_content" :showBox="show_content" @notarize="notarize"></popUp>
    <waitPop :loadingbox="loadingbox"></waitPop>
    <quit :content="content" :quit="quit"></quit>

    <div class="pop_up">
      <van-popup v-model="tanchuang" :close-on-click-overlay="false">
        <div class="hint">请输入数字证书密码</div>
        <input maxlength="8" placeholder="请输入6-8位数字证书密码" type="password" v-model="passwordText" style="margin-top: 34px;margin-bottom: 31px; width: 70%;text-indent: 9px;"/>
        <div class="ok getCert" @click="reapplication()">重新申请</div>
        <div class="ok ensure" @click="queren">确&nbsp;&nbsp;认</div>
      </van-popup>
    </div>
    <div class="testCert">
      <van-popup v-model="agreementflag" :close-on-click-overlay="false">
        <div class="hint">期货居间投资者声明</div>
        <p class="character">{{agreementname}}</p>
        <div class="buttongroup">
              <div class="ok" @click="agreementsure">确认</div>
              <div class="ok" @click="agreementclose">放弃</div>
        </div>
        
      </van-popup>
    </div>
    <!--跳转视频弹窗-->
    <div class="testCert">
      <van-popup v-model="testShow" :close-on-click-overlay="false">
        <div class="hint">{{reminder}}</div>
        <p class="character">{{textcenter}}</p>
        <div class="ok" @click="goVideo">确认</div>
      </van-popup>
    </div>

    <!--查看协议内容-->
    <div class="packageDiv">
      <van-popup class="SpringBlk" v-model="countersignShow" :close-on-click-overlay="false">
        <div class="agrename">{{agrename}}</div>
        <div class="location" ref="content">
          <div>{{agrenames}}</div>
          <p class="character" v-html="html">{{writngType}}</p>
        </div>
        <div class="shuts" @click="countersign">关闭</div>
      </van-popup>
    </div>
  </div>
</template>
<script>
  import Quit from "../../components/quit.vue";
  import Utils from '../../util/iphoneOrAndroid';
  import PopUp from "../../components/popUp.vue";
  import WaitPop from "../../components/waitPop.vue";
  import {Toast, Dialog, Loading} from 'vant';
  import Navigation from "../../components/navigation.vue";

  export default {
    components: {
      Loading,
      PopUp,
      WaitPop,
      Quit,
      Navigation
    },
    data() {
      return {
        datalist:[],
        agrenames:"",
        passwordBox: false,
        agrename: "提示",
        writngType: "",
        countersignShow: false,
        agreementflag:false,
        agreementname:'',
        reminder: "提示",
        html: "",
        testShow: false,
        textcenter: "没有检测到证书,点击确定安装证书",
        password: "",
        passwordText: "",
        tanchuang: false,
        quit: false,
        content: "",
        error_content: "",
        show_content: false,
        loadingbox: false,
        showBox: false,
        tips: "",
        form: '',
        checked: false,
        checkeds: false,
        txt: '已阅读',
        agreementList: [],
        agreementbrokerList:[],
        agreementDialog: false,
        agreement_content: '',
        broker_agreement_content:'',
        agreement_title: '',
        accept_no: "",
        sn: "",
        created: false,
        getInfoDes: false,
        isOut:false,
        isbroker:"",
        agrenum:0,
        agreementcontent: "",
        results:"",
        enable_gm :"",
        gm_username:""
      }
    },
    beforeRouteEnter(to, from, next) {
      if (from.name == "DetailAgreement") {
        window.name = 'DetailAgreement'
        next()
      } else {
        next()
      }
    },
    created() {
      if (window.name == 'DetailAgreement') {
        /*不按照步骤控制跳转*/
      } else {
        /*按照步骤ajax控制跳转*/
      }
      var _this = this;
      
      window['certCallback'] = function (status, result) {
        _this.certCallback(status, result);
      };
      window['gmCallback'] = function (status, result,err) {
        _this.gmCallback(status, result,err);
      };
      window['certCallbackbroker'] = function(status,result) {
        _this.certCallbackbroker(status, result);
      }
      var _supplementStatusAgreement = '0'
      if(sessionStorage.getItem('business_type') == '21'){
        _supplementStatusAgreement = sessionStorage.getItem('_supplementStatusAgreement');
      }
      var data = {
        "business_type": sessionStorage.getItem('business_type'),
        "supplementStatus":_supplementStatusAgreement,
        "supplement_type":sessionStorage.getItem('systype')
      };
      _this.$ajax.post('/agreement/getViewList', data).then(function (res) {
        if (res.data.error_no == 0) {
          if (res.data.is_broker === 'Y') {
            _this.isbroker = res.data.is_broker;
              let arrlist = JSON.parse(res.data.agreement_broker);
               for (var i = 0; i < arrlist.length; i++) {
                  _this.agreementbrokerList.push({
                    "name": arrlist[i].agreementName,
                    "id": arrlist[i].agreementID
                  })
                }
          }
          
          var arr = JSON.parse(res.data.agreement);
          _this.sn = res.data.sn;
          for (var i = 0; i < arr.length; i++) {
            _this.agreementList.push({
              "name": arr[i].agreementName,
              "id": arr[i].agreementID
            })
          }
         
          //初始化判断有没有证书
          const Base64 = require('js-base64').Base64;
          console.log(res.data.enable_gm,'ssssssssss')
          var browserVersion = Utils.iphOrAnz();
           if(res.data.enable_gm === '1') {
              _this.enable_gm = res.data.enable_gm;
              _this.gm_username = res.data.gm_username;
              if(_this.enable_gm == '0'){
                _this.enable = true;
                  return;
              } else {
                _this.gm_enable = true;
              let obj = {
                status:"1",
                callback:"gmCallback",
                appId:res.data.gm_app_id,
                base64HttpsCert:res.data.gm_mAuth_base64_cert,
                mAuthUrl:res.data.gm_mAuth_url,
                appSecret:res.data.gm_secret_key,
                gmUserName:res.data.gm_username
              }
                //调用安卓 ios windows方法
              
              if (browserVersion == "iphone") {
                
                 window.location.href = "objc://certGM/?"+ Base64.encode(JSON.stringify(obj));
              } else if (browserVersion == "anzuo") {
                  console.log("anzuo");
                  jsHandle.certGM(JSON.stringify(obj))
              } else if (browserVersion == "windows") {
                  console.log("windows");
              }
            }
              return;
           }
          if (browserVersion == "iphone") {   //ios
            console.log("iphone");
            window.location.href = "objc://callIOSCheckLocalSn_5/?"+_this.sn+"?certCallback";
          } else if (browserVersion == "anzuo") {  //安卓
            jsHandle.certCheck("3","window.certCallback",_this.sn);
            console.log("anzhuo");
          } else if (browserVersion == "windows") {    //windows
            console.log("windows");
          }
          _this.getInfoDes = false;
        } else if (res.data.error_no == "-9999") {
          _this.quit = true;
          _this.content = res.data.error_info;
        } else {
          _this.show_content = true;
          _this.isOut = true;
          _this.error_content = res.data.error_info;
        }
      }).catch(function (error) {
        console.log(error)
      });

      //控制流程相互不跳转页面
      var data = {
        "business_type": sessionStorage.getItem('business_type'),
      };
      _this.axios.post('/flow/qryAcceptInfo', data).then(function (res) {
        if (res.data.error_no == 0) {
          _this.accept_no = res.data.accept_no;
          _this.business_flow = res.data.business_flow;
          if (_this.$router.history.current.name != res.data.business_flow) {
            _this.$router.push(res.data.business_flow)
          }
        } else if (res.data.error_no == "-9999") {
          _this.quit = true;
          _this.content = res.data.error_info;
        }
      }).catch(function (err) {
        console.log(err)
      });
      //结束
     
    },
    methods: {
      countersign() {
        this.countersignShow = false;
        this.$refs.content.scrollTop = 0;
      },
     
      queren() {
        var _this = this;
        if(_this.passwordText == ""){
          _this.show_content = true;
          _this.isOut = false;
          _this.error_content = "请输入密码";
          return false;
        }
        var password = _this.passwordText;
        _this.tanchuang = false;
       
        var browserVersion = Utils.iphOrAnz();
        const Base64 = require('js-base64').Base64;
        //有居间人
        if (_this.isbroker === 'Y') {
          // 居间人中的国密
          if (_this.enable_gm === '1') {
            let obj = {
                  status:"5",
                  callback:"gmCallback",
                  text:_this.broker_agreement_content,
                  password:password,
                  gmUserName:_this.gm_username
                }
               if (browserVersion == "iphone") {   //ios
                     window.location.href = "objc://certGM/?"+ Base64.encode(JSON.stringify(obj));
                  } else if (browserVersion == "anzuo") {  //安卓
                    
                      jsHandle.certGM(JSON.stringify(obj));  //签署
                  } else if (browserVersion == "windows") {    //windows
                    console.log("windows");
                  }
                  return;
            }
            if (browserVersion == "iphone") {   //ios
                window.location.href = "objc://callIOSSign_5/?" + _this.sn + "?" + _this.broker_agreement_content + "?" + password + "?certCallbackbroker";
              } else if (browserVersion == "anzuo") {  //安卓
                jsHandle.certSign('4', "window.certCallbackbroker", _this.sn, _this.broker_agreement_content, password);  //签署
              } else if (browserVersion == "windows") {    //windowsbr
                console.log("windows");
              }
        } else {

              if (_this.enable_gm === '1') {
                 let obj = {
                      status:"4",
                      callback:"gmCallback",
                      text:_this.agreement_content,
                      password:password,
                      gmUserName:_this.gm_username
                  }
                 if (browserVersion == "iphone") {   //ios
                      window.location.href = "objc://certGM/?"+ Base64.encode(JSON.stringify(obj));
                    } else if (browserVersion == "anzuo") {  //安卓
                       
                        jsHandle.certGM(JSON.stringify(obj));  //签署
                    } else if (browserVersion == "windows") {    //windows
                      console.log("windows");
                    }
                   return; 
                }
              if (browserVersion == "iphone") {   //ios
                window.location.href = "objc://callIOSSign_5/?" + _this.sn + "?" + _this.agreement_content  + "?" + password + "?certCallback";
              } else if (browserVersion == "anzuo") {  //安卓
                jsHandle.certSign('4', "window.certCallback", _this.sn,_this.agreement_content, password);  //签署
              } else if (browserVersion == "windows") {    //windows
                console.log("windows");
              }
        }
      },
      //协议不存在
      goVideo() {
        var _this = this;
        _this.loadingbox = true;
        _this.$ajax.post('/video/noCaVideo', {}).then(function (res) {
          _this.loadingbox = false;
            var data = {
              "accept_no": _this.accept_no,
              "business_type": sessionStorage.getItem('business_type'),
              "business_button_type": '2',//下一步certificate_sign
              "accept_status": "",     //"accept_status":_this.accept_status,当需要更改状态时再传值
              "certificate_sign": '',
              "business_flow": _this.$router.history.current.name,
            };
            _this.loadingbox = true;
            _this.$ajax.post('/flow/updateAccept', data).then(function (res) {
              _this.loadingbox = false;
              if (res.data.error_no == 0) {
                _this.testShow = false;
                _this.$router.push({name: res.data.business_flow});
              } else if (res.data.error_no == "-9999") {
                _this.quit = true;
                _this.content = res.data.error_info;
              } else {
                _this.show_content = true;
                _this.isOut = false;
                _this.error_content = res.data.error_info;
              }
            }).catch(function (err) {
              console.log(err)
            });
        }).catch(function (err) {
          console.log(err)
        });
      },
      notarize() {
        this.show_content = false;
        if(this.isOut){
          this.$router.push({name: "Indexselection"});
        }
      },
      acknowledgement() {
        var _this = this;
        var data = {
          "accept_no": _this.accept_no,
          "business_type": '11',
          "business_button_type": '2', //下一步
          "business_flow": _this.$router.history.current.name,
          "accept_status": ""
        };
        _this.$ajax.post('/flow/updateAccept', data).then(function (res) {
          if (res.data.error_no == 0) {
            _this.$router.push({name: res.data.business_flow});
          } else if (res.data.error_no == "-9999") {
            _this.quit = true;
            _this.content = res.data.error_info;
          } else {
            _this.show_content = true;
            _this.isOut = false;
            _this.error_content = res.data.error_info;
          }
        }).catch(function (err) {
          console.log(err)
        });
      },
      agreementsure(){
        this.agrenum += 1
        if (this.agrenum >= this.datalist[0].length) {
            this.loadingbox = true;
            this.agreementflag = false;
            this.getInfoDes=true;
            this.passwordText ="";
            //调用安卓 ios windows方法
            var browserVersion = Utils.iphOrAnz();
            const Base64 = require('js-base64').Base64;
         
            if(this.enable_gm  === '1') {
                    let obj = {
                      status:"3",
                      callback:"gmCallback",
                      gmUserName:this.gm_username
                    }
                      // 调用安卓 ios windows方法
                    
                    if (browserVersion == "iphone") {
                        window.location.href = "objc://certGM/?"+ Base64.encode(JSON.stringify(obj));
                    } else if (browserVersion == "anzuo") {
                        console.log("anzuo");
                        jsHandle.certGM(JSON.stringify(obj));
                        // zhangyujie
                    } else if (browserVersion == "windows") {
                        console.log("windows");
                    }
            
                  return;
              }
            if (browserVersion == "iphone") {   //ios
              window.location.href = "objc://callIOSCheckLocalSn_5/?" + this.sn + "?certCallback";
            } else if (browserVersion == "anzuo") {  //安卓
              jsHandle.certCheck("3", "window.certCallback", this.sn);  //检测本地有没有证书
            } else if (browserVersion == "windows") {    //windows
              console.log("windows");
            }
            return;
            
        }
        this.agreementname = this.datalist[0][this.agrenum]
      },
      agreementclose() {
         this.agreementflag = false;
         this.$router.push({name: 'MyBussiness'});
      },
      threeAgreement() {
        let _this = this;
        if(this.isbroker !=="") {
           if (this.checkeds == false) {
                this.show_content = true;
                this.isOut = false;
                this.error_content = "请阅读以上所有协议和业务规则内容！"
                this.getInfoDes=false;
                return;
              }
        }
        if (this.checked == false) {
              this.show_content = true;
              this.isOut = false;
              this.error_content = "请阅读以上所有协议和业务规则内容！"
              this.getInfoDes=false;
              return;
        }
        if (this.isbroker !== "") {
              this.$ajax.post('/agreement/statementBroker', {}).then(function (res) {
                if (res.data.error_no == 0) {
                  console.log(_this,'this')
                     _this.datalist.push(res.data.brokerDeclare.split('|+|'));
                     
                     _this.agreementflag = true;
                     _this.agreementname = _this.datalist[0][0]
                } else if (res.data.error_no == "-9999") {
                  this.quit = true;
                  this.content = res.data.error_info;
                  this.checked = false;
                  this.checkeds = false;
                } else {
                  this.show_content = true;
                  this.isOut = false;
                  this.error_content = res.data.error_info;
                  this.checked = false;
                  this.checkeds = false;
                }
                
                }).catch(function (err) {
                  console.log(err)
                });
        } else {
             
              this.loadingbox = true;
              this.getInfoDes=true;
              this.passwordText ="";
               var browserVersion = Utils.iphOrAnz();
               const Base64 = require('js-base64').Base64;
                if(this.enable_gm  === '1') {
                    let obj = {
                      status:"3",
                      callback:"gmCallback",
                      gmUserName:this.gm_username
                    }
                      // 调用安卓 ios windows方法
                    if (browserVersion == "iphone") {
                        window.location.href = "objc://certGM/?"+ Base64.encode(JSON.stringify(obj));
                    } else if (browserVersion == "anzuo") {
                        console.log("anzuo");
                        jsHandle.certGM(JSON.stringify(obj));
                        // zhangyujie
                    } else if (browserVersion == "windows") {
                        console.log("windows");
                    }
            
                  return;
              }
              //调用安卓 ios windows方法
             
              if (browserVersion == "iphone") {   //ios
                window.location.href = "objc://callIOSCheckLocalSn_5/?" + this.sn + "?certCallback";
              } else if (browserVersion == "anzuo") {  //安卓
                jsHandle.certCheck("3", "window.certCallback", this.sn);  //检测本地有没有证书
              } else if (browserVersion == "windows") {    //windows
                console.log("windows");
              }
        }
        
      },
      //ios
      certCallbackbroker(status, result){
          var _this = this;
        _this.agrenum = 0;
        switch (status) {
            case '4':
               if (result == "-1") {
                _this.checked = false;
                _this.checkeds = false;
                _this.show_content = true;
                _this.isOut = false;
                _this.error_content = "密码输入错误";
            } else if(result == "-2"){
                _this.checked = false;
                _this.checkeds = false;
                _this.show_content = true;
                _this.isOut = false;
                _this.error_content = "协议摘要为空";
            } else if (result == "-103") {
                _this.checked = false;
                _this.checkeds = false;
                  _this.show_content = true;
                  _this.isOut = false;
                  _this.error_content = "证书已过期";
            } else if (result == "-101" || result == "-102") {
                _this.checked = false;
                _this.checkeds = false;
                _this.show_content = true;
                _this.isOut = false;
                _this.error_content = "证书错误";
            }
            else {
                _this.results = result;
                var password = _this.passwordText;
                var browserVersion = Utils.iphOrAnz();
                if (browserVersion == "iphone") {   //ios
                    window.location.href = "objc://callIOSSign_5/?" + _this.sn + "?" + _this.agreement_content  + "?" + password + "?certCallback";
                  } else if (browserVersion == "anzuo") {  //安卓
                    jsHandle.certSign('4', "window.certCallback", _this.sn,_this.agreement_content, password);  //签署
                  } else if (browserVersion == "windows") {    //windows
                    console.log("windows");
                }
            }
            
        }
      },
      
      //国密
      gmCallback(status,result,err) {
          let _this = this;
          switch(status) {
            case '1'://检测证书
              if (result == '0') {
                if(_this.getInfoDes == false){
                  _this.loadingbox = false;
                  return;
                }    //签署协议摘要
              
              } else {
                _this.agrenum = 0;
                _this.testShow = true;
                _this.textcenter = err;
                _this.loadingbox = false;
              }
              break;
            case '3'://检测证书
            if (result == '0') {
              if(_this.getInfoDes == false){
                _this.loadingbox = false;
                return;
              }    //签署协议摘要
            _this.$ajax.post('/agreement/pdfDigestInfo', {"business_type": sessionStorage.getItem('business_type')}).then(function (res) {
                _this.loadingbox = false;
                if (res.data.error_no == 0) {
                  _this.agrenum = 0;
                  _this.agreement_content = res.data.agreement;
                  _this.broker_agreement_content = res.data.broker_agreement;
                  _this.loadingbox = false;
                  _this.tanchuang = true;
                } else {
                  _this.agrenum = 0;
                  _this.show_content = true;
                  _this.isOut = false;
                  _this.error_content = res.data.error_info;
                  _this.loadingbox = false;
                  _this.checked = false;
                  _this.checkeds = false;
                  return;
                }
              }).catch(function (error) {
                console.log(error)
              })
            } else {
              _this.agrenum = 0;
              _this.testShow = true;
              _this.textcenter = err;
              _this.loadingbox = false;
            }
            break;
          case '4':  //签署成功
            if (result !== "0") {
              _this.checked = false;
              _this.checkeds = false;
              _this.show_content = true;
              _this.isOut = false;
              _this.error_content = err;
            } else if (result === '-30') {
              _this.loadingbox = false;
            }
            else {
              
              // console.log(result,reslut1)
              _this.loadingbox = true;   
                                     //result 签署的密文
              _this.$ajax.post('/agreement/saveSignValue', {
                'signValue': err,
                'signBrokerValue': _this.results,
                "business_type": sessionStorage.getItem('business_type')
              }).then(function (res) {
                if (res.data.error_no == 0) {
                  _this.loadingbox = false;
                  var data = {
                    "accept_no": _this.accept_no,
                    "business_type": sessionStorage.getItem('business_type'),
                    "business_button_type": '1', //下一步
                    "business_flow": _this.$router.history.current.name,
                    "accept_status": ""
                  };
                  _this.$ajax.post('/flow/updateAccept', data).then(function (res) {
                    if (res.data.error_no == 0) {
                      _this.$router.push({name: res.data.business_flow});
                    } else if (res.data.error_no == "-9999") {
                      _this.quit = true;
                      _this.content = res.data.error_info;
                      _this.loadingbox = false;
                    } else {
                      _this.show_content = true;
                      _this.error_content = res.data.error_info;
                      _this.loadingbox = false;
                      _this.isOut = false;
                    }
                  }).catch(function (err) {
                    console.log(err)
                  });
                } else if (res.data.error_no == "-9999") {
                  _this.quit = true;
                  _this.content = res.data.error_info;
                  _this.loadingbox = false;
                  _this.checked = false;
                  _this.checkeds = false;
                } else {
                  _this.show_content = true;
                  _this.error_content = res.data.error_info;
                  _this.loadingbox = false;
                  _this.checked = false;
                  _this.checkeds = false;
                  _this.isOut = false;
                }
              }).catch(function (err) {
                console.log(err)
              });
            }
            break;
          case '5':
              _this.results = err;
              var password = _this.passwordText;
              let obj = {
                  status:"4",
                  callback:"gmCallback",
                  text:_this.agreement_content,
                  password:password,
                  gmUserName:_this.gm_username
              }
              const Base64 = require('js-base64').Base64
              var browserVersion = Utils.iphOrAnz();
              
              if (browserVersion == "iphone") {   //ios
                    window.location.href = "objc://certGM/?"+ Base64.encode(JSON.stringify(obj));
              } else if (browserVersion == "anzuo") {  //安卓
                  jsHandle.certGM(JSON.stringify(obj));  //签署
              } else if (browserVersion == "windows") {    //windows
                console.log("windows");
               
              }
               
             break;
          }
      },
      certCallback(status, result) {
        console.log('输出到控制台==result=' + result);
        var _this = this;
        _this.agrenum = 0;
        switch (status) {
          case '3'://检测证书
            if (result == '0') {
              if(_this.getInfoDes == false){
                _this.loadingbox = false;
                return;
              }    //签署协议摘要
              _this.$ajax.post('/agreement/pdfDigestInfo', {"business_type": sessionStorage.getItem('business_type')}).then(function (res) {
                _this.loadingbox = false;
                if (res.data.error_no == 0) {
                  //调用安卓 ios windows方法
                  //  _this.tanchuang = true;
                  //  _this.passwordText = res.data.error_info;
                  //  _this.loadingbox = false;

                  // var browserVersion = Utils.iphOrAnz();
                  // if (browserVersion == "iphone") {   //ios
                  //   window.location.href = "objc://callIOSSign_5/?" + _this.sn + "?" + res.data.agreement + "?certCallback";
                  // } else if (browserVersion == "anzuo") {  //安卓
                  //   console.log('输出到控制台==browserVersion=' + browserVersion);

                  //   jsHandle.certSign('4', "window.certCallback", _this.sn, res.data.agreement,);  //签署
                  // } else if (browserVersion == "windows") {    //windows
                  //   console.log("windows");
                  // }
                  _this.agrenum = 0;
                  _this.agreement_content = res.data.agreement;
                  _this.broker_agreement_content = res.data.broker_agreement;
                  _this.loadingbox = false;
                  _this.tanchuang = true;
                } else {
                  _this.agrenum = 0;
                  _this.show_content = true;
                  _this.isOut = false;
                  _this.error_content = res.data.error_info;
                  _this.loadingbox = false;
                  _this.checked = false;
                  _this.checkeds = false;
                  return;
                }
              }).catch(function (error) {
                console.log(error)
              })
            } else {
              _this.agrenum = 0;
              _this.testShow = true;
              _this.textcenter = "没有检测到证书,点击确定安装证书";
              _this.loadingbox = false;
            }
          break;
          case '4':  //签署成功
            if (result == "-1") {
              _this.checked = false;
              _this.checkeds = false;
              _this.show_content = true;
              _this.isOut = false;
              _this.error_content = "密码输入错误";
            } else if(result == "-2"){
              _this.checked = false;
              _this.checkeds = false;
              _this.show_content = true;
              _this.isOut = false;
              _this.error_content = "协议摘要为空";
            } else if (result == "-103") {
               _this.checked = false;
               _this.checkeds = false;
                _this.show_content = true;
                _this.isOut = false;
                _this.error_content = "证书已过期";
            } else if (result == "-101" || result == "-102") {
                _this.checked = false;
                _this.checkeds = false;
                _this.show_content = true;
                _this.isOut = false;
                _this.error_content = "证书错误";
            } else if (result == "-30") {
               _this.loadingbox = false;
            }
            else {
              // console.log(result,reslut1)
              _this.loadingbox = true;                          //result 签署的密文
              _this.$ajax.post('/agreement/saveSignValue', {
                'signValue': result,
                'signBrokerValue': _this.results,
                "business_type": sessionStorage.getItem('business_type')
              }).then(function (res) {
                if (res.data.error_no == 0) {
                  _this.loadingbox = false;
                  var data = {
                    "accept_no": _this.accept_no,
                    "business_type": sessionStorage.getItem('business_type'),
                    "business_button_type": '1', //下一步
                    "business_flow": _this.$router.history.current.name,
                    "accept_status": ""
                  };
                  _this.$ajax.post('/flow/updateAccept', data).then(function (res) {
                    if (res.data.error_no == 0) {
                      _this.$router.push({name: res.data.business_flow});
                    } else if (res.data.error_no == "-9999") {
                      _this.quit = true;
                      _this.content = res.data.error_info;
                      _this.loadingbox = false;
                    } else {
                      _this.show_content = true;
                      _this.error_content = res.data.error_info;
                      _this.loadingbox = false;
                      _this.isOut = false;
                    }
                  }).catch(function (err) {
                    console.log(err)
                  });
                } else if (res.data.errthreeAgreemenor_no == "-9999") {
                  _this.quit = true;
                  _this.content = res.data.error_info;
                  _this.loadingbox = false;
                  _this.checked = false;
                  _this.checkeds = false;
                } else {
                  _this.show_content = true;
                  _this.error_content = res.data.error_info;
                  _this.loadingbox = false;
                  _this.checked = false;
                  _this.checkeds = false;
                  _this.isOut = false;
                }
              }).catch(function (err) {
                console.log(err)
              });
            }
            break;
            
        }
      },
      agrees() {
         var _this = this;
        if (_this.checkeds != true) {
          return
        }
        var _supplementStatusAgreement = '0'
        if(sessionStorage.getItem('business_type') == '21'){
          _supplementStatusAgreement = sessionStorage.getItem('_supplementStatusAgreement');
        }
        var data = {"business_type": sessionStorage.getItem('business_type'),
          "supplementStatus":_supplementStatusAgreement,
          "accept_no":_this.accept_no,
          "supplement_type":sessionStorage.getItem('systype')
        };
        _this.loadingbox = true;
        if (_this.isbroker === 'Y') {
              _this.$ajax.post('/agreement/getViewBrokerPdf',data).then(function (res) {
                _this.loadingbox = false;
                if (res.data.error_no == "0") {
                } else if (res.data.error_no == "-9999") {
                  _this.quit = true;
                  _this.content = res.data.error_info;
                  _this.loadingbox = false;
                } else {
                  _this.show_content = true;
                  _this.checkeds = false;
                  _this.checked = false;
                  _this.error_content = res.data.error_info;
                  _this.loadingbox = false;
                  _this.isOut = false;
                }
              }).catch(function (err) {
                console.log(err)
              });
        }
      },
      agree() {   //勾选阅读协议
        var _this = this;
        if (_this.checked != true) {
          return
        }
        var _supplementStatusAgreement = '0'
        if(sessionStorage.getItem('business_type') == '21'){
          _supplementStatusAgreement = sessionStorage.getItem('_supplementStatusAgreement');
        }
        var data = {"business_type": sessionStorage.getItem('business_type'),
          "supplementStatus":_supplementStatusAgreement,
          "accept_no":_this.accept_no,
          "supplement_type":sessionStorage.getItem('systype')
        };
        _this.loadingbox = true;
        _this.$ajax.post('/agreement/getViewPdf',data).then(function (res) {
          _this.loadingbox = false;
          if (res.data.error_no == "0") {
          } else if (res.data.error_no == "-9999") {
            _this.quit = true;
            _this.content = res.data.error_info;
            _this.loadingbox = false;
          } else {
            _this.show_content = true;
            _this.checked = false;
            _this.checkeds = false;
            _this.error_content = res.data.error_info;
            _this.loadingbox = false;
            _this.isOut = false;
          }
        }).catch(function (err) {
          console.log(err)
        });
      },
      readIt(event) {
        //阅读协议
        var _this = this;
        _this.$ajax.post('/agreement/contentById', {'agreementId': event.id + ""}).then(function (res) {
          if (res.data.errorNo == 0) {
            _this.countersignShow = true;
            _this.agrenames = event.name;
            _this.html = res.data.content;
          } else if (res.data.errorNo == "-1") {
            _this.show_content = true;
            _this.error_content = res.data.errorInfo;
            _this.isOut = false;
          } else if (res.data.error_no == "-9999") {
            _this.quit = true;
            _this.contentr = res.data.errorInfo;
          } else {
            _this.show_content = true;
            _this.error_content = res.data.errorInfo;
            _this.isOut = false;
          }
        }).catch(function (error) {
          console.log(error)
        });
      },
      goTest() {
        var data = {
          "certificate_sign": '1',
          "accept_no": this.accept_no,
          "business_type": sessionStorage.getItem("business_type"),
          "business_button_type": '1',//上一步
          "business_flow": this.$router.history.current.name,
          "accept_status": ""
        };
        var _this = this;
        this.$ajax.post('/flow/updateAccept', data).then(function (res) {
          _this.$router.push({
            name: res.data.business_flow,
          });
        });
      },
      onClickRight() {
        this.$router.push({name: 'MyBussiness'});
      },
      readAgreement() {
        this.$router.push('/agreement')
      },
      reapplication(){
        var _this = this;
        _this.$ajax.post('/cert/setSn',{}).then(function (res) {
          if (res.data.error_no == 0) {
            _this.goVideo();
          } else if (res.data.error_no == "-9999") {
            _this.quit = true;
            _this.contentr = res.data.error_info;
          } else {
            _this.show_content = true;
            _this.error_content = res.data.error_info;
            _this.isOut = false;
          }
        }).catch(function (error) {
          console.log(error)
        });
      }
    }
  }
</script>
<style lang="less" scoped>
  .threeAgreement {
    .chooseAccountCommon {
      height: 45px;
      line-height: 45px;
      text-align: left;
      text-indent: 18px;
      font-size: 16px;
      background: #e8e8e8;
      border-bottom: 1px solid lavender;
      border-top: 1px solid lavender;
    }
    .agreement div {
      line-height: 40px;
      text-align: left;
      width: 93%;
      margin: 0 auto;
      a {
        color: #333333;
      }
    }
    .readAgreement.van-radio-group {
      text-align: left;
      margin-left: 17px;
      margin-top: 20px;
    }
    .van-checkbox {
      text-align: left;
      margin-left: 15px;
      height: 40px;
      line-height: 40px;
      margin-top: 10px;
    }
    .nextBtn {
      width: 86%;
      margin: 0 auto;
      background: #00a8ff;
      color: white;
      text-align: center;
      line-height: 20px;
      height: 42px;
      margin: 3% auto;
      margin-bottom: 8%;
    }
    .van-popup {
      width: 80%;
    }
    .buttongroup{
      display: flex;
    }
    // .ok {
    //   width: 31%;
    //   color: #ffffff;
    //   background: #49a5ff;
    //   text-align: center;
    //   line-height: 35px;
    //   border-radius: 4px;
    //   margin: 3px auto;
    //   margin-bottom: 12px;
    // }
    .hint {
      text-align: center;
      font-size: 16px;
      color: #333333;
      width: 100%;
      background: #49a5ff;
      line-height: 30px;
      color: #ffffff;
    }
    .loading {
      width: 100%;
      height: 100%;
      background: rgba(0, 0, 0, 0.7);
      margin: 0 auto;
      position: fixed;
      top: 0;
      left: 0;
      z-index: 999;
      p {
        text-align: center;
        font-size: 16px;
        color: white;
      }
    }
    .van-popup {
      width: 80%;
    }
    .testCert {
      width: 80%;
    }
    .ok {
      width: 31%;
      color: #ffffff;
      background: #49a5ff;
      text-align: center;
      line-height: 35px;
      border-radius: 4px;
      margin: 0 auto;
      margin-bottom: 24px;
    }
    .hint {
      text-align: center;
      font-size: 16px;
      color: #333333;
      width: 100%;
      background: #49a5ff;
      line-height: 37px;
      color: #ffffff;
    }
    .van-cell {
      width: 76%;
      display: flex;
      padding: 2px 10px;
      box-sizing: border-box;
      line-height: 24px;
      position: relative;
      background-color: #fff;
      color: #333;
      font-size: 14px;
      overflow: hidden;
      border: 1px solid #eeeeee;
      margin: 0 auto;
    }
    .agreement {
      border-bottom: 1px solid #eeeeee;
      position: relative;
    }
    .character {
      text-align: left;
      font-size: 16px;
      color: #333333;
      // margin-left: 4%;
      // padding-top: 5%;
      line-height: 34px;
      // margin-bottom: 10px;
      padding:10px;
      // margin-top: 12%;
    }
    .SpringBlk {
      width: 84%;
      height: 62%;
    }
    .agrename {
      position: fixed;
      color: white;
      top: 0;
      background: #00a8ff;
      line-height: 33px;
      width: 100%;
    }
    .location {
      width: 100%;
      height: 70%;
      overflow: auto;
      background: #eeeeee;
      margin-top: 16%;
    }
    .shuts {
      width: 22%;
      line-height: 35px;
      color: white;
      text-align: center;
      border-radius: 3px;
      position: absolute;
      background: #00a8ff;
      margin: 0 40%;
      bottom: 4%;
    }
    .getCert{
       position: absolute;
      margin-left:16%;
    }
    .ensure{
      margin-right:18%;
    }
    @media screen and (min-width:320px) and (max-width:568px){
      .aaa{
        color: #323233;
        margin-left: 0%;
        float: left;
        margin-top: 2%;
        font-size: 12px;
      }
    }
     @media screen and (min-width:375px) and (max-width:667px){
       .aaa{
          color: #323233;
          margin-left: 0%;
          float: left;
          margin-top: 0%;
          font-size: 14px;
       }
    }
  }

</style>
