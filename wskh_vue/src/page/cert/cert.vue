<template>
  <div class="cert">
    <Header :showOut='showOut'></Header>
    <Menu :activeIndex="activeIndex"></Menu>
    <div style="">
      <div class="titleCommon">业务办理/{{bus_name}}
        <span style="font-size: 12px;color: #6a6a6a;">「受理单号:{{accept_no}}」</span>
      </div>
      <div class="uploadContain" :style="{width:screenWidth, height:screenHeight}">
        <div class="step uploadStep" v-if="this.type == this.navbar.openFuturesAccount">
          <el-steps :active="active" align-center process-status="finish" finish-status="success">
            <el-step :title="ntile.title" :key="index" class="stepTitle" v-for="(ntile,index) in navtitles"></el-step>
          </el-steps>
          <!--<el-button style="margin-top: 12px;" @click="next">下一步</el-button>-->
        </div>
        <div class="circleContain">
          <span class="circle"></span>
          <span>数字证书申请</span>
        </div>
        <div class="idcardUploadTips" style="margin-left: 10%">
          <img src="~@a/other/Certification.png" alt="" class="tips_img">
          <div class="promptTips2">1、您的数字证书不存在，请申请安装数字证书。数字证书是唯一识别的凭证，开立账户需具备完整的验证环节，数字证书类似银行网银验证，提供安全保障</div>
          <div class="promptTips2">2、申请安装证书需要设置密码，您设置的证书密码在之后流程使用，请牢记证书密码并且不要外传。</div>
          <!--<div style="width: 60%;margin-left: 20%;margin-top: 40px">
            <el-progress :text-inside="true" :stroke-width="18" :percentage='jdt' status="success"></el-progress>
          </div>-->
        </div>
        <div class="btnGroup" style="margin-bottom: 10px" v-if="promptFlag!='true'">
          <el-button
            style="margin-left: 30%;margin-top: 16px;display: inline-block"
            class="nextStep"
            @click="submitCertForm('certForm')">申请并安装
          </el-button>

           <el-button
              style="background:#169BD5;color: white;width: 120px;margin-left: 44%;margin-top: 16px;display: inline-block"
              @click="go">测试跳转
            </el-button>
          </div>
        <div class="btnGroup" style="margin-bottom: 10px" v-if="promptFlag=='true'">数字证书安装失败，请重新安装
          <el-button
            style="margin-left: 30%;margin-top: 16px;display: inline-block"
            class="nextStep"
            @click="submitCertForm('certForm')">
          </el-button>
        </div>
      </div>
      <!--      <div
              type="primary"
              v-loading.fullscreen.lock="fullscreenLoading"
              element-loading-spinner="el-icon-loading"
              element-loading-background="rgba(0, 0, 0, 0.8)"
              element-loading-text="证书申请中..."
              style="width: 100%"
            >
            </div>-->
      <ScreenLoading :fullscreenLoading="fullscreenLoading" :loadingText="loadingText"></ScreenLoading>
      <el-dialog
        :close-on-click-modal="false"
        :close-on-press-escape="false"
        title="提示"
        center
        :lock-scroll="false"
        :visible.sync="caApplyFlag"
        :show-close="false"
        width="30%">
        <span>证书申请安装中，请稍后......</span>
        <span slot="footer" class="dialog-footer">
         <el-progress :text-inside="true" :stroke-width="18" :percentage="jdt"></el-progress>
        </span>
      </el-dialog>

      <!--      <el-dialog
              :close-on-click-modal="false"
              :close-on-press-escape="false"
              :lock-scroll="false"
              title="提示"
              :visible.sync="timeOut"
              :show-close="false"
              width="30%"
              center>
              <span>登录超时，请重新登录！</span>
              <span slot="footer" class="dialog-footer">
                <el-button type="primary" @click="goOut">确 定</el-button>
              </span>
            </el-dialog>-->
      <PopDialog :popShowDialog="popPShow" :popTitle="popPTitle" :popContext="popPText"
                 :outSystem="outSystem" :outSysName="outSysName"></PopDialog>
    </div>

  </div>
</template>

<script>
    import Header from '../header.vue'
    import {isvalidPassword} from '../../util/validate'
    import Menu from '@c/menu.vue'
    import ScreenLoading from "@c/screenLoading.vue";
    import PopDialog from '@c/popupDialog.vue';

    var validPassword = (rule, value, callback) => {
        if (!value) {
            callback(new Error('请输入证书密码'))
        } else if (!isvalidPassword(value)) {
            callback(new Error('请输入正确的6位数字密码'))
        } else if (value.length != 6) {
            callback(new Error('请输入正确的6位数字密码'))
        } else {
            callback()
        }
    }
    //certPasswordConfirm

    var certPasswordConfirm = (rule, value, callback) => {
        var temp = document.getElementById("con").value;
        if (value != temp) {
            callback(new Error('两次输入的密码不一致，请重新输入'))
        } else {
            callback()
        }
    }

    export default {
        components: {
            PopDialog,
            ScreenLoading,
            Header, Menu
        },
        data() {
            return {
                popPShow: false,
                outSystem: false,
                popPText: '',
                outSysName: '',
                popPTitle: '提示',
                btnConfirm: '确 定',
                timeOutTips: '登录超时，请重新登录！',
                fullscreenLoading: false,
                loadingText: '证书申请中...',

                type: '',
                bus_name: '',
                jdt: 1,
                activeIndex: '11',
                showOut: true,
                active: 6,
                accept_no: '',
                centerDialogVisible: false,
                isEqual: false,
                certForm: {
                    certPassword: '',
                    certPasswordConfirm: ''
                },
                rules: {
                    certPassword: [
                        {required: true, trigger: 'change', validator: validPassword}//这里需要用到全局变量
                    ],
                    certPasswordConfirm: [
                        {required: true, trigger: 'blur', validator: certPasswordConfirm}//这里需要用到全局变量
                    ],
                },
                navtitles: this.navbar.navtitles,
                timeOut: false,
                screenWidth: '1000px',
                screenHeight: '530px',
                caApplyFlag: false,
                promptFlag: 'false',
                isGm:''
            }
        },
        mounted() {
            if (this.navbar.screenWidth > 1000) {
                this.screenWidth = this.navbar.screenWidth
            }
            if (this.navbar.screenHeight > 530) {
                this.screenHeight = this.navbar.screenHeight
            }
            // this.screenWidth = sessionStorage.getItem('screenWidth');
            // this.screenHeight = sessionStorage.getItem('screenHeight');
        },
        created() {
            this.type = sessionStorage.getItem('business_type');
            this.bus_name = this.navbar.menu_map.get('menu' + this.type);
            this.activeIndex = this.type;

            var _this = this;
            var data = {
                "business_type": sessionStorage.getItem('business_type'),
                "register_way": '1',
            };
            this.axios.post('/flow/qryAcceptInfo', data).then(function (res) {
                _this.accept_no = res.data.accept_no;
                _this.business_flow = res.data.business_flow;
                _this.isGm = res.data.is_gm;
                // if(res.data.is_gm === '1'){
                //     _this.popPShow = true;
                //     _this.popPText = '目前国密不支持pc证书请到app操作';
                //     _this.outSysName = _this.btnConfirm;
                //     _this.outSystem = true;
                // }
                if (_this.$router.history.current.name != res.data.business_flow) {
                    _this.$router.push('/error')
                }
            }).catch(function (err) {
                console.log(err)
            });
        },
        methods: {
            go() {
                var data = {
                    "certificate_sign": '1',
                    "accept_no": this.accept_no,
                    "business_type": sessionStorage.getItem("business_type"),
                    "business_button_type": '1',//上一步
                    "business_flow": this.$router.history.current.name,
                    "accept_status": ""     //"accept_status":_this.accept_status,当需要更改状态时再传值
                };
                var _this = this;
                this.axios.post('/flow/updateAccept', data).then(function (res) {
                    _this.$router.push({
                        name: res.data.business_flow,
                    });
                });
            },
            scrollCount() {
                var _this = this;
                setTimeout(() => {
                    _this.jdt = this.jdt + 10;
                    if (_this.jdt != 90) {
                        _this.scrollCount();
                    }
                }, 600)
            },
            applyCert(csr) {
                var _this = this;
                console.log('csr', csr);

                _this.scrollCount();

                this.axios.post('/cert/applyCert', {"csr": csr}).then(function (res) {
                    //_this.jdt = 50;
                    if (res.data.error_no == 0) {
                        var p7_cert = res.data.p7_cert;
                        var ca_sn = res.data.ca_sn;
                        if (window.devDetectCert(ca_sn)) {
                            console.log("证书已安装");
                            _this.jdt = 100;
                            setTimeout(() => {
                                _this.goNext();
                            }, 500);
                        } else {
                            //安装失败后重试一次
                            var insatllResult = false;
                            try {
                                insatllResult = window.installCert(p7_cert);
                                // _this.jdt = 80;
                                if (!window.devDetectCert(ca_sn)) {
                                    insatllResult = false;
                                }
                                if (!insatllResult) {
                                    insatllResult = window.installCert(p7_cert);
                                }
                            } catch (e) {
                                _this.fosCaApplyFlag();
                                insatllResult = false;
                            }
                            if (insatllResult) {
                                //完成安装
                                _this.jdt = 100;
                                console.log("证书安装完成");
                                _this.goNext();
                            } else {
                                var errorInfo = '数字证书安装失败，请<a href="javascript:void(0);" onclick="devCertInstall()" class="btn btn-default-yellow">重新安装</a>';
                                _this.promptFlag = 'true';
                                _this.fosCaApplyFlag();
                                console.log(errorInfo);
                            }
                        }
                    } else if (res.data.error_no == -9999) {
                        _this.initDialogParam();
                    } else {
                        _this.$message.error(res.data.error_info)
                    }
                    _this.fosCaApplyFlag();
                }).catch(function (err) {
                    _this.fosCaApplyFlag();
                    console.log(err)
                });
            },
            goNext() {
                var _this = this;
                var data = {
                    "accept_no": _this.accept_no,
                    "business_type": _this.type,
                    "business_button_type": '1',//下一步
                    "business_flow": this.$router.history.current.name,
                    "accept_status": ""     //"accept_status":_this.accept_status,当需要更改状态时再传值
                };
                _this.axios.post('/flow/updateAccept', data).then(function (res) {
                    if (res.data.error_no == 0) {
                        _this.$router.push({
                            name: res.data.business_flow,
                        });
                    } else {
                        _this.$message.error(res.data.error_info);
                    }
                    _this.fosCaApplyFlag();
                })
            },
            certGoHead() {
                var data = {
                    "accept_no": this.accept_no,
                    "business_type": _this.type,
                    "business_button_type": '0',//上一步
                    "business_flow": this.$router.history.current.name,
                    "accept_status": ""     //"accept_status":_this.accept_status,当需要更改状态时再传值
                };
                var _this = this;
                this.axios.post('/flow/updateAccept', data).then(function (res) {
                    if (res.data.error_no == 0) {
                        _this.$router.push({
                            name: res.data.business_flow,
                        });
                    } else {
                        _this.$message.error(res.data.error_info);
                    }
                });

            },
            goOut() {
                var _this = this;
                _this.loginOut();
            },
            submitCertForm(formName) {
                // this.fullscreenLoading = true;
                if (this.caApplyFlag) return;
                //1:获取csr
                this.jdt = 10;
                var csr = window.devCreateCertReqStr(2);
                console.log(csr,'csr')
                if (csr == "") {
                    this.fosCaApplyFlag();
                    this.jdt = 1;
                    return;
                }
                if (csr == "error") {
                    this.$message.error('证书请求出错,请重试！');
                    this.fosCaApplyFlag();
                    this.jdt = 1;
                    return;
                }
                this.toCaApplyFlag();
                this.jdt = 10;

                //2:申请证书
                this.applyCert(csr);
            },
            confirmPsd() {

            },
            tempPsd(value) {

            },
            resetForm(formName) {
                this.$refs[formName].resetFields();
            },
            initDialogParam() {
                var _this = this;
                _this.popPShow = true;
                _this.outSystem = true;
                _this.outSysName = _this.btnConfirm;
                _this.popPText = _this.timeOutTips;
            },
            toParamScreenLoading(lText) {
                this.fullscreenLoading = true;
                this.loadingText = lText;
            },
            fosParamScreenLoading() {
                this.fullscreenLoading = false;
            },
            toCaApplyFlag() {
                this.caApplyFlag = true;
            },
            fosCaApplyFlag() {
                this.caApplyFlag = false;
            }
        }
    }
</script>

<style lang="less" scoped>
  .cert {
    background: #F6F6F6;
    height: 100%;

    .el-input {
      width: 70%;
    }

    .certDiv {
      margin-top: 20px;
      width: 60%;
      margin-left: 20%;
    }

    .tips_img {
      margin-left: 30%;
      padding: 10px 0;
    }

    .cetr_tips {
      width: 60%;
      margin-left: 20%;
    }
  }

</style>
