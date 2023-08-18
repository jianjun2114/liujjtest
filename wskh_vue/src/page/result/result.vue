<template>
  <div class="result">
    <Header :showOut="showOut"></Header>
    <Menu :activeIndex="activeIndex"></Menu>

    <div style="">
      <div class="titleCommon">业务办理/{{bus_name}}
        <span style="font-size: 12px;color: #6a6a6a;">「受理单号:{{accept_no}}」</span>
      </div>
      <div class="uploadContain" :style="{width:screenWidth, height:screenHeight}">
        <div class="step uploadStep" v-if="this.type == this.navbar.openFuturesAccount">
          <el-steps :active="active" align-center process-status="success" finish-status="success">
            <el-step :title="ntile.title" :key="index" class="stepTitle" v-for="(ntile,index) in navtitles"></el-step>
          </el-steps>
          <!--<el-button @click="next">下一步</el-button>-->
        </div>
        <div class="circleContain">
          <span class="circle"></span>
          <span>申请提交</span>
        </div>
        <div class="warppIcture">
          <div class="ictureImg">
            <img src="~@a/other/SuccessIcon.png">
          </div>
          <p v-if="this.type == this.navbar.fEditIdCardDate || this.type == this.navbar.preposeIdCard"
             class="promptTips2">尊敬的{{client_name}}{{client_sex}}，您的身份证有效期修改已成功提交，正在等待复核；您可在我的基本资料中查看结果。</p>
          <p v-if="this.type == this.navbar.fEditOtherInfo" class="promptTips2">
            尊敬的{{client_name}}{{client_sex}}，您的其他信息修改已成功提交，正在等待复核；您可在我的基本资料中查看结果。</p>
          <p v-if="this.type == this.navbar.fEditPhone" class="promptTips2">
            尊敬的{{client_name}}{{client_sex}}，您的联系电话修改已成功提交，正在等待复核；您可在我的基本资料中查看结果。</p>
          <p
            v-if="this.type == this.navbar.riskAssessment || this.type == this.navbar.preposeRiskExpires || this.type == this.navbar.preposeRisk"
            class="promptTips2">尊敬的{{client_name}}{{client_sex}}，您的适当性评估已成功提交，正在等待复核；您可在系统的适当性功能中查看结果。</p>

          <p v-if="this.type == this.navbar.openFuturesAccount" class="promptTips2">
            尊敬的{{client_name}}{{client_sex}}，您的开立期货账户已成功提交！请在下一交易时间或收到通知短信后登录网上开户系统查看。如有疑问请拨打客服热线：<span>{{broker_hotline}}</span>。
          </p>
          <p v-if="this.type == this.navbar.openAssetAccount" class="promptTips2">
            尊敬的{{client_name}}{{client_sex}}，开立资管线上申请提交，复核完成后请线下办理剩余手续。如有疑问请拨打客服热线：<span>{{broker_hotline}}</span>。
          </p>
          <p v-if="this.type == this.navbar.addTransactionCode" class="promptTips2">
            尊敬的{{client_name}}{{client_sex}}，您的增开交易编码申请已成功提交！请在下一交易时间或收到通知短信后登录网上开户系统查看。如有疑问请拨打客服热线：<span>{{broker_hotline}}</span>。
          </p>
          <p v-if="this.type == this.navbar.preposeAgreement" class="promptTips2">
            尊敬的{{client_name}}{{client_sex}}，您的补签协议已成功提交，正在等待复核。
          </p>
          <p v-if="this.type == this.navbar.bankAccount" class="promptTips2">
            尊敬的{{client_name}}{{client_sex}}，您办理完毕银行卡变更后，还需联系对应银行通过手机银行或电脑端网银系统或银行柜台等方式办理银期签约后才能使用银期转账功能，各银行银期签约具体办理指引详询热线电话：<span>{{broker_hotline}}</span>。
          </p>
        </div>
        <!-- 表格 -->
        <table border="1" v-if="this.type == this.navbar.openFuturesAccount">
          <tr v-for="(item,index) in resultList">
            <th><span>{{item.name}}</span></th>
            <th>{{item.value}}</th>
          </tr>
        </table>
        <div class="primaryButton">
          <el-button type="primary" @click="goIndex">{{rstimetxt}}返回我的业务</el-button>
        </div>
      </div>
      <el-dialog
        :close-on-click-modal="false"
        :close-on-press-escape="false"
        :lock-scroll="false"
        title="提示"
        :show-close="false"
        :visible.sync="timeOut"
        width="30%"
        center>
        <span>登录超时，请重新登录！</span>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="goOut()" style="width: 80px;height:40px;">确 定</el-button>
        </span>
      </el-dialog>
      <el-dialog
        :close-on-click-modal="false"
        :close-on-press-escape="false"
        :lock-scroll="false"
        title="提示"
        :show-close="false"
        :visible.sync="rejected"
        width="40%"
        center>
        <span><b>{{rejectinfo}}</b></span>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="goHandle()" style="width: 100px;height:40px;">立即处理</el-button>
        </span>
      </el-dialog>
    </div>
  </div>
</template>

<script>
    import Header from '../header.vue'
    import Menu from '@c/menu.vue'

    export default {
        components: {Header, Menu},
        data() {
            return {
                resultList: [],
                type: '',
                bus_name: '',
                activeIndex: '11',
                showOut: true,
                accept_no: '',
                active: 8,
                client_name: '',
                navtitles: this.navbar.navtitles,
                timeOut: false,
                screenWidth: '1000px',
                screenHeight: '530px',
                broker_hotline: '',
                client_sex: '',
                rejected: false,
                rejectinfo: '',
                rstimetxt: '',
            }
        },
        created() {
            this.type = sessionStorage.getItem('business_type');
            this.broker_hotline = sessionStorage.getItem('broker_hotline');

            this.bus_name = this.navbar.menu_map.get('menu' + this.type);
            this.activeIndex = this.type;

            var _this = this;
            var data = {
                "business_type": sessionStorage.getItem('business_type'),
                "register_way": '1',
            };
            this.axios.post('/flow/qryAcceptInfo', data).then(function (res) {
                if (res.data.error_no == 0) {
                    _this.accept_no = res.data.accept_no;
                    _this.business_flow = res.data.business_flow;

                    if (res.data.accept_status == '12' || res.data.accept_status == '5') {
                        _this.countdown();
                        return;
                    }
                    if (res.data.accept_status == '13' || res.data.accept_status == '15') {
                        _this.countdown();
                        return;
                    }
                    if (_this.$router.history.current.name != res.data.business_flow) {
                        _this.$router.push('/error');
                        return
                    }
                    if (res.data.accept_status == '6') {
                        _this.rejected = true;
                        _this.rejectinfo = res.data.remark;
                        return;
                    }
                    _this.countdown();
                } else if (res.data.error_no == -9999) {

                } else {
                    _this.$message.error(res.data.error_info);
                }
            }).catch(function (err) {
                console.log(err)
            });

            var resultData = {
                "systype": sessionStorage.getItem('systype'),
                "business_type": sessionStorage.getItem("business_type")
            };
            this.axios.post('/result/resultPage', resultData).then(function (res) {
                if (res.data.error_no == 0) {
                    _this.client_sex = res.data.client_gender + '';
                    if (_this.client_sex == '1') {
                        _this.client_sex = '先生';
                    } else if (_this.client_sex == '2') {
                        _this.client_sex = '女士';
                    } else {
                        _this.client_sex = '先生/女士';
                    }
                    _this.client_name = res.data.client_name;
                    _this.resultList = res.data.resultList;
                    console.log(res.data.resultList)
                } else if (res.data.error_no == -9999) {
                    _this.timeOut = true;
                } else {
                    _this.$message.error(res.data.error_info);
                }
            }).catch(function (err) {
                alert('回显结果：' + err);
                console.log(err)
            });
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
        methods: {
            goOut() {
                var _this = this;
                _this.loginOut();
            },
            goIndex() {
                var _this = this;
                this.axios.post('/myBussiness/getBusiness', {
                    business_type: '0',
                    systype: sessionStorage.getItem('__sys_type'),
                    supplement_type: sessionStorage.getItem('__sys_type'),
                }).then(function (res) {
                    if (res.data.error_no == 0) {
                        sessionStorage.setItem('business_type', '0');
                        sessionStorage.setItem('menuIndex', '0');
                        _this.$router.push({name: res.data.business_flow,});
                    } else if (res.data.error_no == -9999) {
                        _this.timeOut = true;
                    } else {
                        _this.$message.error(res.data.error_info);
                    }
                }).catch(function (error) {
                    console.log(error)
                });
            },
            goHandle() {
                var _this = this;
                var data = {
                    business_type: sessionStorage.getItem('business_type'),
                    accept_no: _this.accept_no,
                };
                this.axios.post('/myBussiness/operationBusiness', data).then(function (res) {
                    if (res.data.error_no == 0) {
                        _this.$router.push({'name': res.data.business_flow})
                    } else if (res.data.error_no == -9999) {
                        _this.timeOut = true;
                    } else {
                        _this.$message.error(res.data.error_info)
                    }
                });
            },
            countdown() {
                var _this = this;
                var count = 5;
                var interval = setInterval(function () {
                    if (count > 1) {
                        _this.rstimetxt = count-- + '秒后 ';
                    } else {
                        _this.rstimetxt = '1秒后';
                        _this.goIndex();
                        clearInterval(interval);
                    }
                }, 1000)
            }
        },
    }
</script>

<style lang="less" scoped>
  .result {
    background: #F6F6F6;
    height: 100%;

    form#questionForm {
      margin-top: 11px;
      background: white;
      overflow-x: hidden;
      overflow-y: scroll;
    }

    .el-checkbox {
      width: 100%;
    }

    .el-checkbox-group {
      font-size: 0;
      margin-left: 60px;
    }

    .el-form-item {
      width: 100%;
    }

    .el-radio-group {
      display: inline-block;
      line-height: 1;
      vertical-align: middle;
      margin-top: 10px;
      font-size: 0;
      width: 100%;
      margin-left: 60px;
    }

    .warningText {
      display: inline-block;
      width: 90%;
      font-size: 16px;
      text-align: justify;
      text-justify: inter-ideograph; /*IE*/
    }

    .dialog_title {
      //background: cornflowerblue;
    }

    .warppIcture {
      width: 60%;
      margin: 4% auto;

      .ictureImg {
        width: 7%;
        margin: 20px auto;

        img {
          width: 100%;
        }
      }

      p {
        font-size: 15px;
        color: #848586;
        line-height: 26px;
        padding-top: 2%;

        span {
          font-weight: bold;
        }
      }
    }

    table {
      border: 1px solid #E5E6E7;
      width: 60%;
      margin: 4% auto;
    }

    table tr th {
      border: 1px solid #E5E6E7;
      text-align: center;
      height: 46px;
      font-size: 15px;

      span {
        font-weight: bold;
      }
    }

    .primaryButton {
      width: 20%;
      margin: 0 auto;
      padding-bottom: 4%;
    }

    .el-button--primary {
      width: 200px;
    }

  }

</style>
