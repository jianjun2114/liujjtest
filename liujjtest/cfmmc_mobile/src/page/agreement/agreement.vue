<template>
    <div class="agreement">
      <van-nav-bar
        title="阅读协议"
        left-text=""
        right-text="关闭"
        left-arrow
        right-arrow
        @click-left="onClickLeft"
        @click-right="onClickRight"/>
      <!--导航条-->
      <navigation/>
        <div class="content" id="agreementWrap" :style="{height:contentHeight+'px'}">
          <div v-html="html" ></div>
        </div>
        <van-checkbox style="margin-top:4%" v-model="checked"><span class="read">我已阅读并同意&nbsp;</span>
            <span style="color:orange">以上所有协议和业务规则内容</span>
        </van-checkbox>
         <van-button size="large" class="nextBtn" @click="nextAgree()" v-if="is_bloker === 'N'">{{contents}}</van-button>
         <van-button size="large" :class="[pointerEvents !== 0 ? 'disabled' : 'nextBtn']" @click="nextAgrees()" v-else>{{contents}}</van-button>
         
        <popUp :tips="error_content" :showBox="show_content" @notarize="notarize"></popUp>
        <quit :content="content" :quit="quit"></quit>
        <waitPop :loadingbox="loadingbox"></waitPop>
    </div>
</template>

<script type="text/ecmascript-6">
import WaitPop from "../../components/waitPop.vue";
import Quit from "../../components/quit.vue";
import Router from 'vue-router'
import PopUp from "../../components/popUp.vue";
import Navigation from "../../components/navigation.vue";
    export default {
    components: {
        PopUp,
        Quit,
        WaitPop,
        Navigation
    },
      data(){
        return{
            loadingbox: false,
            quit:false,
            content:"",
            contents: "下一步",  // 按钮里显示的内容
            totalTime: 0,    //记录具体倒计时时间
            error_content:"",
            show_content: false,
            checked:"",
            html:'',
            end:false,
            checked:false,
            accept_no:"",
            isOut:false,
            isContent: true,
            contentHeight:"",
            text:"下一步",
            is_bloker: "",
            pointerEvents:0,
            alive:true,
            scrollTop:0
            // num:0,
        }
      },
    mounted() {

      var _this = this;
      var dom = document.getElementById("agreementWrap");
      console.log(dom.scrollHeight, dom.offsetHeight, dom.scrollTop);
      dom.onscroll = function () {
        var scrollTop, maxScroll, minScroll = 0;
        scrollTop = dom.scrollTop;
        maxScroll = dom.scrollHeight - dom.offsetHeight;
        if (scrollTop >= maxScroll-5) {
          return _this.end = true;
        } else {
          return _this.end = false;
        }
      };
    },
      created(){
        var _this = this;
        var clientHeight = document.documentElement.clientHeight;
        _this.contentHeight = clientHeight- 240;
        // 开始
            var data = {
                "business_type":sessionStorage.getItem('business_type'),
            };
            _this.axios.post('/flow/qryAcceptInfo',data).then(function (res) {
                if(res.data.error_no == 0){
                    _this.accept_no = res.data.accept_no;
                    _this.business_flow = res.data.business_flow;
                    if(_this.$router.history.current.name != res.data.business_flow){
                    _this.$router.push(res.data.business_flow)
                    }
                    if(sessionStorage.getItem('business_type') == "21"){
                      //同步三方信息
                      var thdData = {
                        "systype": sessionStorage.getItem('systype'),
                        "business_type": sessionStorage.getItem('business_type'),
                        "accept_no": _this.accept_no
                      }
                      _this.axios.post('/userInfo/qryThdClientInfo', thdData).then(function (res) {
                        if(res.data.error_no != '0'){
                          _this.quit = true;
                          _this.content = res.data.error_info;
                        }
                      }).catch(function (err){
                        console.log(err);
                      });
                    }
                }else if(res.data.error_no == "-9999"){
                    _this.quit = true;
                    _this.content = res.data.error_info;
                }else{
                    _this.show_content = true;
                    _this.error_content = res.data.error_info;
                }
                }).catch(function (err) {
                    console.log(err);
                });
     // 结束
        var _supplementStatusAgreement = '0'
        if(sessionStorage.getItem('business_type') == '21'){
          _supplementStatusAgreement = sessionStorage.getItem('_supplementStatusAgreement');
        }
        var data = {
          "business_type":sessionStorage.getItem('business_type'),
          "supplementStatus":_supplementStatusAgreement,
          "supplement_type":sessionStorage.getItem('systype')
        };
        _this.$ajax.post('/agreement/content',data).then(function (res) {
             _this.isContent = false;
          if(res.data.errorNo == 0){
              _this.isContent = true;
                 _this.html = res.data.content;
                 _this.is_bloker = res.data.is_broker;
                  if ( _this.is_bloker == 'Y') {
                      _this.pointerEvents = 1;
                      _this.text = '阅读下一项'
                  } else {
                      _this.text = '下一步'
                  }
                  //时间读秒
              var data = {
                  "broker_id":sessionStorage.getItem('broker_id'),
              }
              _this.axios.post('/index/qryCmpInfoParam',data).then(function (res) {
                  if(res.data.error_no == 0){
                      _this.totalTime = res.data.agreement_read_time;
                      _this.contents = "(" + _this.totalTime + ") " + _this.text;
                      let clock = window.setInterval(() => {
                          _this.totalTime--
                          if (_this.totalTime <= 0) {
                            //当倒计时小于0时清除定时器
                              window.clearInterval(clock)
                              _this.pointerEvents = 0;
                              _this.contents = _this.text;
                              return;
                          }
                        _this.contents = "(" + _this.totalTime+") " + _this.text;
                      },1000)
                  }else if(error_no == "-9999"){
                    _this.quit = true;
                    _this.content = res.data.error_info;
                  }else{
                      _this.error_content = res.data.error_info;
                      _this.show_content = true;
                  }
              }).catch(function (err) {
                  console.log(err)
              });
                  }else if(res.data.error_no == "-9999"){
                      _this.quit = true;
                      _this.content = res.data.error_info;
                  }else if(res.data.errorNo == '-1706'){
                      _this.show_content = true;
                      _this.error_content = "协议内容获取失败，请联系我司客服人员，联系电话"+sessionStorage.getItem('broker_hotline')+"。";
                      _this.isOut = true;
                  }else{
                      _this.show_content = true;
                      _this.error_content = res.data.errorInfo;
                  }
          }).catch(function (error) {
              console.log(error)
          });
       
      },
      methods:{
          notarize(){
              this.show_content = false;
              if(this.isOut){
                this.$router.push({name:"Index"});
              }
          },
          
        onClickLeft(){
          var _this = this;
          // if (_this.is_bloker !== '') {
          //      var _supplementStatusAgreement = '0'
          //       if(sessionStorage.getItem('business_type') == '21'){
          //         _supplementStatusAgreement = sessionStorage.getItem('_supplementStatusAgreement');
          //       }
          //       var data = {
          //         "business_type":sessionStorage.getItem('business_type'),
          //         "supplementStatus":_supplementStatusAgreement,
          //         "supplement_type":sessionStorage.getItem('systype')
          //       };
          //       _this.$ajax.post('/agreement/content',data).then(function (res) {
          //           _this.isContent = false;
          //         if(res.data.errorNo == 0){
          //             _this.isContent = true;
          //               _this.html = res.data.content;
          //               // _this.pointerEvents = 0;
          //               _this.is_bloker = res.data.is_bloker;
          //               _this.text = '阅读下一项';
          //               _this.time();
          //           }else if(res.data.error_no == "-9999"){
          //               _this.quit = true;
          //               _this.content = res.data.error_info;
          //           }else if(res.data.errorNo == '-1706'){
          //               _this.show_content = true;
          //               _this.error_content = "协议内容获取失败，请联系我司客服人员，联系电话"+sessionStorage.getItem('broker_hotline')+"。";
          //               _this.isOut = true;
          //           }else{
          //               _this.show_content = true;
          //               _this.error_content = res.data.errorInfo;
          //           }
          //   }).catch(function (error) {
          //       console.log(error)
          //   });
                  
          // } else {
          //      if (_this.accept_no != "" && _this.accept_no != undefined) {
          //     // 方法：开始 流程 跳转页面
          //     var data = {
          //       "accept_no": _this.accept_no,
          //       "business_type": sessionStorage.getItem('business_type'),
          //       "business_button_type": "0", // 上一步 按钮类型
          //       "business_flow": _this.$router.history.current.name
          //     };
          //     _this.$ajax.post('/flow/updateAccept', data).then(function (res) {
          //       if (res.data.error_no == 0) {
          //         _this.$router.push({
          //           name: res.data.business_flow
          //         });
          //       } else if (res.data.error_no == '-9999') {
          //         _this.quit = true;
          //         _this.content = res.data.error_info;
          //       } else {
          //         _this.error_content = res.data.error_info;
          //         _this.show_content = true;
          //       }
          //     }).catch(function (err) {
          //       console.log(err);
          //     });
          //     // 方法：结束 流程 跳转页面
          //   }
         
          // }
           if (_this.accept_no != "" && _this.accept_no != undefined) {
              // 方法：开始 流程 跳转页面
              var data = {
                "accept_no": _this.accept_no,
                "business_type": sessionStorage.getItem('business_type'),
                "business_button_type": "0", // 上一步 按钮类型
                "business_flow": _this.$router.history.current.name
              };
              _this.$ajax.post('/flow/updateAccept', data).then(function (res) {
                if (res.data.error_no == 0) {
                  _this.$router.push({
                    name: res.data.business_flow
                  });
                } else if (res.data.error_no == '-9999') {
                  _this.quit = true;
                  _this.content = res.data.error_info;
                } else {
                  _this.error_content = res.data.error_info;
                  _this.show_content = true;
                }
              }).catch(function (err) {
                console.log(err);
              });
           }
              // 方法：结束 流程 跳转页面
        },
        time() {
          var _this = this;
          _this.totalTime = 0;
          var data = {
                      "broker_id":sessionStorage.getItem('broker_id'),
                  }
              _this.axios.post('/index/qryCmpInfoParam',data).then(function (res) {
                  if(res.data.error_no == 0){
                      _this.totalTime = res.data.agreement_read_time;
                      _this.content = "(" + _this.totalTime + ") " + _this.text;
                      let clock = window.setInterval(() => {
                          _this.totalTime--
                          if (_this.totalTime <= 0) {
                            //当倒计时小于0时清除定时器
                              window.clearInterval(clock)
                              _this.pointerEvents = 0;
                              _this.content = _this.text;
                              return;
                          }
                        _this.content = "(" + _this.totalTime+") " + _this.text;
                      },1000)
                  }else if(error_no == "-9999"){
                    _this.quit = true;
                    _this.content = res.data.error_info;
                  }else{
                      _this.error_content = res.data.error_info;
                      _this.show_content = true;
                  }
              }).catch(function (err) {
                  console.log(err)
              });
        },
        onClickRight(){
          this.$router.push({name:'MyBussiness'});
        },
      nextAgrees() {
        let _this = this;
        
         var _supplementStatusAgreement = '0'
              if(sessionStorage.getItem('business_type') == '21'){
                _supplementStatusAgreement = sessionStorage.getItem('_supplementStatusAgreement');
              }
             
             if(_this.totalTime>0){
                 return
             }
             var dom = document.getElementById("agreementWrap");
             dom.scrollTop = 0;
             dom.onscroll=function () {
                var scrollTop,maxScroll,minScroll = 0;
                scrollTop = dom.scrollTop;
                maxScroll = dom.scrollHeight - dom.offsetHeight;
                if(scrollTop >= maxScroll-5){
                    return _this.end = true;
                }else{
                    return _this.end=false;
                }
            };
            if (dom.scrollHeight - dom.offsetHeight < 10 && _this.isContent) {
            _this.end = true;
            }
            if(_this.end == true && _this.checked == true){
                 var data = {
                "business_type":sessionStorage.getItem('business_type'),
                "supplementStatus":_supplementStatusAgreement,
                "supplement_type":sessionStorage.getItem('systype')
              };
              _this.$ajax.post('/agreement/brokerContent',data).then(function (res) {
                  _this.isContent = false;
                if(res.data.errorNo == 0){
                    _this.isContent = true;
                      _this.html = res.data.content;
                      _this.is_bloker = 'N';
                      _this.text = '下一步';
                      _this.checked=false;
                      var data = {
                            "broker_id":sessionStorage.getItem('broker_id'),
                        }
                        _this.axios.post('/index/qryCmpInfoParam',data).then(function (res) {
                            if(res.data.error_no == 0){
                                _this.totalTime = res.data.agreement_read_time;
                                _this.content = "(" + _this.totalTime + ") " + _this.text;
                                let clock = window.setInterval(() => {
                                    _this.totalTime--
                                    if (_this.totalTime <= 0) {
                                      //当倒计时小于0时清除定时器
                                        window.clearInterval(clock)
                                        _this.contents = _this.text;
                                        return;
                                    }
                                  _this.contents = "(" + _this.totalTime+") " + _this.text;
                                },1000)
                            }else if(error_no == "-9999"){
                              _this.quit = true;
                              _this.content = res.data.error_info;
                            }else{
                                _this.error_content = res.data.error_info;
                                _this.show_content = true;
                            }
                        }).catch(function (err) {
                            console.log(err)
                        });
                                      
                  }else if(res.data.error_no == "-9999"){
                      _this.quit = true;
                      _this.content = res.data.error_info;
                  }else if(res.data.errorNo == '-1706'){
                      _this.show_content = true;
                      _this.error_content = "协议内容获取失败，请联系我司客服人员，联系电话"+sessionStorage.getItem('broker_hotline')+"。";
                      _this.isOut = true;
                  }else{
                      _this.show_content = true;
                      _this.error_content = res.data.errorInfo;
                  }
          }).catch(function (error) {
              console.log(error)
          });
        }else if(_this.end != true){
                _this.show_content = true;
                _this.error_content = "请完整阅读该协议内容！";
          } else if(_this.checked != true){
                _this.show_content = true;
                _this.error_content = "您需要仔细阅读并同意协议内容！";
          }

             
      },
		nextAgree(){
             var _this = this;
             
             if(_this.totalTime>0){
                 return
             }
             var dom = document.getElementById("agreementWrap");
             dom.onscroll=function () {
                var scrollTop,maxScroll,minScroll = 0;
                scrollTop = dom.scrollTop;
                console.log(scrollTop,'scrollTop')
                maxScroll = dom.scrollHeight - dom.offsetHeight;
                if(scrollTop >= maxScroll-5){
                    return _this.end = true;
                }else{
                    return _this.end=false;
                }
            };
            if (dom.scrollHeight - dom.offsetHeight < 10 && _this.isContent) {
            _this.end = true;
            }
            if(_this.end == true && _this.checked == true){
                var databot = {
                    "accept_no": _this.accept_no,
                    "business_type": sessionStorage.getItem('business_type'),
                    "business_button_type":'1',//下一步
                    "business_flow": _this.$router.history.current.name,
                    "accept_status":""     //"accept_status":_this.accept_status,当需要更改状态时再传值
                };
                _this.loadingbox = true;
                _this.$ajax.post('/flow/updateAccept',databot).then(function (res) {
                    _this.loadingbox = false;
                    if(res.data.error_no == 0){
                    _this.$router.push({
                        name: res.data.business_flow,
                    });
                    }else if(res.data.error_no == '-9999' ){
                       _this.quit = true;
                       _this.content = res.data.error_info;
                    }else{
                        _this.show_content = true;
                        _this.error_content = res.data.error_info;
                    }
                });
            } else if(_this.end != true){
                _this.show_content = true;
                _this.error_content = "请完整阅读该协议内容！";
            } else if(_this.checked != true){
                _this.show_content = true;
                _this.error_content = "您需要仔细阅读并同意协议内容！";
            }
            
            },
          },
        }
</script>
<style lang="less" scoped>
.agreement{
    // overflow: hidden;
    background:white;
    height: 100%;
    .content{
        margin-top: 20px;
        font-size: 14px;
        width: 90%;
        margin-left: 3%;
        text-align: justify;
        text-justify: inter-ideograph;
        height: 620px;
        overflow: auto;
        margin: 0 auto;
        -webkit-overflow-scrolling: touch
    }
    .nextAgreement{
        position: fixed;
        bottom: 0;
        left: 0;
        background: white;
        height: 40px;
        width: 100%;
        line-height: 40px;
        font-size: 1.6rem;
        color: #3db2ee;
        border-top:1px solid lavender;
    }
    .nextBtn{
        width: 80%;
        height: 45px;
        background: #00a8ff;
        text-align: center;
        line-height: 20px;
        color: white;
        bottom:0;
        
    }
    .disabled {
        pointer-events: none;
        cursor: default;
        width: 80%;
        height: 45px;
        background: #00a8ff;
        text-align: center;
        line-height: 20px;
        color: white;
        bottom:2%;
        margin-top: 30px;
    }
    @media screen and (min-width:320px) and (max-width:568px){
       .content{
         height: 400px;
       }
    }
    @media screen and (min-width:375px) and (max-width:667px){
       .content{
         height: 485px;
       }
    }
    @media only screen and (min-width: 1349px) and (orientation : landscape){
      .content{
        height:300px;
        width: 80%;
        margin: 0 auto;
        background:#eeeeee;
      }
    }
    //  @media only screen and (min-device-width : 768px) and (max-device-width : 1024px){
    //    .nextBtn{
    //      width: 28%;
    //    }
    //  }
}
</style>
