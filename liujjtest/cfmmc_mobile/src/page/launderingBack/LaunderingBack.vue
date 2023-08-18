<template>
  <div class="launderingBack">
    <van-nav-bar
      title="基本信息资料"
      right-text="关闭"
      right-arrow
      left-arrow
      @click-left="onClickLeft"
      @click-right="onClickRight"
    />
    <!--导航条-->
    <navigation/>
   <!--反洗钱-->
   <span v-if="tax_type_flag == 1" class="sign">*&nbsp;</span>
    <p v-if="tax_type_flag == 1" class="monicker">您的税收居民身份为：</p>
    <checklist
      :options="commonList1"
      v-model="ruleForm3.tax_type"
      :max="1"
      v-if="tax_type_flag == 1"
      :disabled="disabled"
      @on-change="changeTaxType">
    </checklist>

   <span v-if="platform_type_flag == '1'" class="sign">*&nbsp;</span>
    <p v-if="platform_type_flag == '1'" class="monicker">您的政要人物关系为：</p>
    <checklist
      :options="commonList2"
      v-model="ruleForm3.platform_type"
      :max="1"
      v-if="platform_type_flag == '1'"
      :disabled="disabled"
      @on-change="changePlatformType">
    </checklist>

  <span v-if="term_investment_flag == '1'" class="sign">*&nbsp;</span>
    <p v-if="term_investment_flag == '1'" class="monicker">您的投资期限为：</p>
    <checklist
      :options="commonList3"
      v-model="ruleForm3.term_investment"
      :max="1"
      v-if="term_investment_flag == '1'"
      :disabled="disabled"
      @on-change="change">
    </checklist>

  <span v-if="proceeds_type_flag == '1'" class="sign">*&nbsp;</span>
    <p v-if="proceeds_type_flag == '1'" class="monicker">您的期望收益为：</p>
    <checklist
      :options="commonList4"
      v-model="ruleForm3.proceeds_type"
      :max="1"
      v-if="proceeds_type_flag == '1'"
      :disabled="disabled"
      @on-change="change">
    </checklist>

  <span v-if="con_relation_flag == '1'" class="sign">*&nbsp;</span>
    <p v-if="con_relation_flag == '1'" class="monicker">您的账户是否存在被他人控制或存在控制他人账户的情况：</p>
    <checklist
      :options="commonList5"
      v-model="ruleForm3.con_relation"
      :max="1"
      v-if="con_relation_flag == '1'"
      :disabled="disabled"
      @on-change="changeConRelation">
    </checklist>

  <span v-if="beneficiary_flag == '1'" class="sign">*&nbsp;</span>
    <p v-if="beneficiary_flag == '1'" class="monicker">您的账户交易实际受益人为：</p>
    <checklist
      :options="commonList6"
      v-model="ruleForm3.beneficiary"
      :max="1"
      v-if="beneficiary_flag == '1'"
      :disabled="disabled"
      @on-change="changeBeneficiary">
    </checklist>

  <span v-if="investment_variety_flag == '1'" class="sign">*&nbsp;</span>
    <p v-if="investment_variety_flag == '1'" class="monicker">您想投资的品种为：</p>
    <!--<checklist
      :max="3"
      :options="commonList7"
      v-model="ruleForm3.investment_variety"
      v-if="investment_variety_flag == 1"
      :disabled="disabled"
      @on-change="change">
    </checklist>-->
    <van-checkbox-group
    v-model="ruleForm3.investment_variety"
    v-if="investment_variety_flag == '1'"
    style="background: white; border-top: 1px solid #eeeeee; border-bottom: 1px solid #eeeeee;">
    <van-checkbox
      v-for="(item, index) in commonList7"
      :disabled="disabled"
      :key="item.key"
      :name="item.key">
      {{ item.value }}
      </van-checkbox>
    </van-checkbox-group>

  <span v-if="bad_record_flag == '1'" class="sign">*&nbsp;</span>
    <p v-if="bad_record_flag == '1'" class="monicker">您是否有来源以下机构不良诚信记录：</p>
    <checklist
      :max="1"
      :options="commonList8"
      v-model="ruleForm3.bad_record"
      v-if="bad_record_flag == '1'"
      :disabled="disabled"
      @on-change="changeBadRecord">
    </checklist>

    <span v-if="deal_type_flag === '1'" class="sign">*&nbsp;</span>
    <p v-if="deal_type_flag === '1'" class="monicker">您参与期货的主要交易类型：</p>
    <van-checkbox-group
      v-model="ruleForm3.deal_type"
      v-if="deal_type_flag === '1'"
      style="background: white; border-top: 1px solid #eeeeee; border-bottom: 1px solid #eeeeee;">
      <van-checkbox
        v-for="(item, index) in commonList9"
        :disabled="disabled"
        :key="item.key"
        :name="item.key">
        {{ item.value }}
      </van-checkbox>
    </van-checkbox-group>

   <!-- <div size="large" class="nextclick" @click="nextStep()">下一步</div>-->
    <van-button size="large" @click="nextStep" class="van-button">下一步</van-button>
    <popUp :tips="error_content" :showBox="show_content" @notarize="notarize"></popUp>
    <quit :content="content" :quit="quit"></quit>
    <waitPop :loadingbox="loadingbox"></waitPop>
    <confirm :showContents="showContents" :isShow="isShow" :yesButton="yesButton" @ensure="ensure"></confirm>

  </div>
</template>

<script>
  import { Group, PopupRadio, Checklist, PopupPicker} from 'vux';
  import PopUp from "../../components/popUp.vue";
  import Quit from "../../components/quit.vue";
  import WaitPop from "../../components/waitPop.vue";
  import Confirm from "../../components/confirm.vue";
  import Navigation from "../../components/navigation.vue";
  export default {
      components: {
        Group,
        PopupRadio,
        Checklist,
        PopupPicker,
        PopUp,
        Quit,
        WaitPop,
        Confirm,
        Navigation
      },
      data() {
        return {
          broker_hotline:"",
          isOut:false,
          showContents:"",
          isShow: false,
          yesButton:"确定",
          disabled:false,
          broker_id:"",
          acceptNo:"",
          error_content: "",
          show_content: false,
          content:"",
          quit:false,
          loadingbox: false,
          bad_record_flag:"",
          investment_variety_flag:"",
          beneficiary_flag:"",
          con_relation_flag:"",
          proceeds_type_flag:"",
          term_investment_flag:"",
          platform_type_flag:"",
          tax_type_flag:"",
          deal_type_flag:"",
          ruleForm3: {
            name: "",
            idNumber: "",
            idAddress: "",
            startTime:"",
            endTime:"",
            sheng:[],
            shi:[],
            detailAddress:"",
            zipCode:"",
            phoneNumber:"",
            contact_mobile:"",
            email:"",
            profession:[],
            industy:[],
            minzu:[],
            educational:[],
            buss1:[],
            buss2:[],
            buss3:[],
            buss4:[],
            tax_type:[],
            platform_type:[],
            term_investment:[],
            proceeds_type:[],
            con_relation:[],
            beneficiary:[],
            investment_variety:[],
            bad_record:[],
            deal_type:[]
        },
            error_info:"请您联系我公司客服人员临柜办理，联系电话" + sessionStorage.getItem('broker_hotline') + "。",
            //反洗钱
            commonList1: [{value:'仅为中国税收居民', key:'A'}, {value:'仅为非居民', key:'B'}, {value:'既是中国税收居民又是其他国家(地区)税收居民', key:'C'}],
            commonList2: [{value:'外国政要', key:'A'}, {value:'外国政要家庭成员', key:'B'}, {value:'与外国政要关系密切', key:'C'},{value:'国际组织的高级管理人员', key:'E'},{value:'无关系', key:'D'}],
            commonList3: [{value:'0年-1年', key:'A'}, {value:'1年-5年', key:'B'}, {value:'5年以上', key:'C'}],
            commonList4: [{value:'稳健', key:'A'}, {value:'成长', key:'B'}, {value:'激进', key:'C'}],
            commonList5: [{value:'不存在', key:'A'}, {value:'存在', key:'B'}],
            commonList6: [{value:'本人', key:'A'}, {value:'其他', key:'B'}],
            commonList7: [{value:'期货、期权',key:'A'},{value:'资管产品',key:'B'}, {value:'其他',key:'C'}],
            commonList8: [{value:'中国人民银行征信中心', key:'A'}, {value:'最高人民法院失信被执行人名单', key:'B'}, {value:'工商行政管理机构', key:'C'}, {value:'税务管理机构', key:'D'}, {value:'监管机制、自律组织', key:'E'}, {value:'投资者在期货经营机构从事投资活动时产生的违约行为记录', key:'F'}, {value:'过度维权等不当行为信息', key:'G'}, {value:'其他组织', key:'H'}, {value:'无', key:'I'}],
            commonList9: [{value:'投机', key:'A'}, {value:'套利', key:'B'}, {value:'套保', key:'C'}]
        }
      },
      created() {
        // 定义 this
        var _this = this;
         _this.broker_hotline = sessionStorage.getItem('broker_hotline');
         if(sessionStorage.getItem('business_type') == '13'){
           _this.disabled = true;
         }
         //
         if(sessionStorage.getItem('business_type') == '14'){
          _this.disabled = false;
          _this.readonly = false;
          _this.platform_type_flag == 0;
         }
        // 方法：开始 通用配置获取
        _this.$ajax.post('/userInfo/qryBaseInfoParam', {"broker_id": sessionStorage.getItem('broker_id')}, {}).then(function (res) {
          if (res.data.error_no == 0) {
            // _this.industry_flag = res.data.industry_flag;  行业
            _this.bad_record_flag = res.data.bad_record_flag;
            _this.investment_variety_flag = res.data.investment_variety_flag;
            _this.beneficiary_flag = res.data.beneficiary_flag;
            _this.con_relation_flag = res.data.con_relation_flag;
            _this.proceeds_type_flag = res.data.proceeds_type_flag;
            _this.term_investment_flag = res.data.term_investment_flag;
            _this.platform_type_flag = res.data.platform_type_flag;
            _this.tax_type_flag = res.data.tax_type_flag;
            _this.deal_type_flag = res.data.deal_type_flag;
          }else if(res.dta.error_no == "-9999"){
            _this.quit = true;
            _this.content = res.data.error_info;
          } else {
            _this.error_content = res.data.error_info;
            _this.show_content = true;
          }
        }).catch(function (err) {
          console.log(err);
        });
        // 方法：结束 通用配置获取

        var _this = this;
        // 方法：开始 流程 控制页面不能相互跳页面
        var data = {
          "business_type": sessionStorage.getItem('business_type')
        };
        _this.axios.post('/flow/qryAcceptInfo', data).then(function (res) {
          if (res.data.error_no == 0) {
            _this.acceptNo = res.data.accept_no;
            if (_this.$router.history.current.name != res.data.business_flow) {
              _this.$router.push({name: res.data.business_flow});
            }else{
              if(sessionStorage.getItem('business_type') == '13' || sessionStorage.getItem('business_type') == '14' || sessionStorage.getItem('business_type') == '22' || sessionStorage.getItem('business_type') == '24') {
                //回线数据
                var thdData = {
                  "systype": sessionStorage.getItem('systype'),
                  "business_type": sessionStorage.getItem('business_type'),
                  "accept_no": _this.acceptNo
                }
                _this.axios.post('/userInfo/qryThdClientInfo', thdData).then(function (res) {   //同步三方信息
                  if (res.data.error_no == 0) {
                    var queryItem = {
                      "systype": sessionStorage.getItem('systype')
                    }
                    _this.axios.post('/userInfo/getLaunderingBack', queryItem).then(function (res) {
                      if (res.data.error_no == 0 || res.data.error_no == -9013) {  // -9013数据字典信息为空
                        _this.ruleForm3.tax_type = [res.data.tax_type];
                        _this.ruleForm3.platform_type =[res.data.platform_type];
                        _this.ruleForm3.term_investment = [res.data.term_investment];
                        _this.ruleForm3.proceeds_type = [res.data.proceeds_type];
                        _this.ruleForm3.con_relation = [res.data.con_relation];
                        _this.ruleForm3.beneficiary = [res.data.beneficiary];
                        if (res.data.investment_variety != "") {
                          var a_variety = res.data.investment_variety;
                          var b_variety = a_variety.split(",");
                          _this.ruleForm3.investment_variety = b_variety;
                        }
                        if(res.data.deal_type !== ""){
                          _this.ruleForm3.deal_type = res.data.deal_type.split(",")
                        }
                        _this.ruleForm3.bad_record = [res.data.bad_record];
                      } else if (res.data.error_no == '-9999') {
                        _this.quit = true;
                        _this.content = res.data.error_info;
                      } else {
                        _this.error_content = res.data.error_info;
                        _this.show_content = true;
                      }
                    }).catch(function (err) {
                      console.log(err)
                    });
                  } else if (res.data.error_no == "-9999") {
                    _this.quit = true;
                    _this.content = res.data.error_info;
                  } else {
                    _this.quit = true;
                    _this.content = res.data.error_info;
                  }
                }).catch(function (err) {
                  console.log(err)
                });
                //回线数据结束
              }
            }
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
        // 方法：结束 流程 控制页面不能相互跳页面

        //查询试题
        if(sessionStorage.getItem('business_type') == '11' || sessionStorage.getItem('business_type') == '12'){
          var queryItem = {
            "systype": sessionStorage.getItem('systype'),
          }
          _this.axios.post('/userInfo/getLaunderingBack', queryItem).then(function (res) {
            if(res.data.error_no == 0){
              if(res.data.tax_type != ""){
                 _this.ruleForm3.tax_type = [res.data.tax_type];
              }
               if(res.data.platform_type != ""){
                _this.ruleForm3.platform_type = [res.data.platform_type];
               }
               if(res.data.term_investment != ""){
                _this.ruleForm3.term_investment = [res.data.term_investment];
               }
               if(res.data.proceeds_type != ""){
                _this.ruleForm3.proceeds_type = [res.data.proceeds_type];
               }
              if(res.data.con_relation != ""){
                _this.ruleForm3.con_relation = [res.data.con_relation];
              }
              if(res.data.beneficiary != ""){
                _this.ruleForm3.beneficiary = [res.data.beneficiary];
              }
              if (res.data.investment_variety != "") {
                var a_variety = res.data.investment_variety;
                var b_variety = a_variety.split(",");
                _this.ruleForm3.investment_variety = b_variety;
              }
              if(res.data.bad_record != ""){
                _this.ruleForm3.bad_record = [res.data.bad_record];
              }
              if(res.data.deal_type !== ""){
                _this.ruleForm3.deal_type = res.data.deal_type.split(",")
              }
            }else if(res.data.error_no == '-9999'){
              _this.quit = true;
              _this.content = res.data.error_info;
            }else{
              _this.error_content = res.data.error_info;
              _this.show_content = true;
            }
          }).catch(function (err) {
            console.log(err)
          });
        }
      },
      mounted(){},
      methods: {
         ensure(){
          var _this = this;
          _this.isShow = false;
            var data = {
              "accept_no": _this.acceptNo,
              "business_type": sessionStorage.getItem('business_type')
            }
            _this.axios.post('/myBussiness/abandonBusiness',data).then(function (res) {
              if(res.data.error_no == 0){
               // sessionStorage.setItem('business_type',"14");
               // _this.$router.push({name: 'DoneSuitably'});
                data.supplement_type = sessionStorage.getItem('systype');
                _this.axios.post('/myBussiness/getBusiness',data).then(function (res) {
                  if(res.data.error_no == 0){
                    sessionStorage.setItem('business_type',"18");
                    _this.$router.push({name: res.data.business_flow});
                  }else if(res.data.error_no == "-9999"){
                    _this.quit = true;
                    _this.content = res.data.error_info;
                  } else{
                    _this.error_content = res.data.error_info;
                    _this.show_content = true;
                    _this.isOut = true;
                  }
                }).catch(function (err) {
                  console.log(err)
                });
              } else if(res.data.error_no == "-9999"){
                _this.quit = true;
                _this.content = res.data.error_info;
              } else{
                _this.error_content = res.data.error_info;
                _this.show_content = true;
                _this.isOut = false;
              }
            }).catch(function (err) {
              console.log(err)
            });
      },


        onClickLeft() {
          var _this = this;
          if (_this.acceptNo != "" && _this.acceptNo != undefined) {
            // 方法：开始 流程 跳转页面
            var data = {
              "accept_no": _this.acceptNo,
              "business_type": sessionStorage.getItem('business_type'),
              "business_button_type": "0", // 上一步 按钮类型
              "business_flow": _this.$router.history.current.name
            };
            _this.loadingbox = true;
            _this.$ajax.post('/flow/updateAccept', data).then(function (res) {
              _this.loadingbox = false;
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
            // 方法：结束 流程 跳转页面
          }
        },
        onClickRight() {
          this.$router.push({name: 'MyBussiness'});
        },
        notarize() {
          this.show_content = false;
          if(this.isOut){
            sessionStorage.setItem('business_type',"18");
            this.$router.push({name: "Register"});
          }
        },
        nextStep() {
          //保存试题
          var _this = this;
          if(_this.tax_type_flag == 1 && _this.ruleForm3.tax_type == ""){
              if(sessionStorage.getItem('business_type') == 13){
                 _this.isShow = true;
                 _this.showContents = "您的适当性信息不完整，请先完成信息修改后再进行增开交易编码业务";
                 return false;
              }else{
                _this.error_content = "税收居民不能为空";
                _this.show_content = true;
                return false;
              }
          }
          if(_this.ruleForm3.platform_type == "" && _this.platform_type_flag == 1){
            if(sessionStorage.getItem('business_type') == 13){
               _this.isShow = true;
               _this.showContents = "您的适当性信息不完整，请先完成信息修改后再进行增开交易编码业务";
               return false;
            }else{
             _this.error_content = "政要人物关系不能为空";
             _this.show_content = true;
             return false
            }
          }
          if(_this.ruleForm3.term_investment == "" && _this.term_investment_flag == 1){
             if(sessionStorage.getItem('business_type') == 13){
               _this.isShow = true;
               _this.showContents = "您的适当性信息不完整，请先完成信息修改后再进行增开交易编码业务";
               return false;
              }else{
              _this.error_content = "投资期限不能为空";
              _this.show_content = true;
              return false
            }
          }
          if(_this.ruleForm3.proceeds_type == "" && _this.proceeds_type_flag == 1 ){
            if(sessionStorage.getItem('business_type') == 13){
              _this.isShow = true;
              _this.showContents = "您的适当性信息不完整，请先完成信息修改后再进行增开交易编码业务";
              return false;
            }else{
              _this.error_content = "期望收益不能为空";
              _this.show_content = true;
              return false
            }
          }
          if(_this.ruleForm3.con_relation == "" && _this.con_relation_flag == 1){
            if(sessionStorage.getItem('business_type') == 13){
              _this.isShow = true;
              _this.showContents = "您的适当性信息不完整，请先完成信息修改后再进行增开交易编码业务";
              return false;
            }else{
              _this.error_content = "账户是否存在实际控制关系不能为空";
              _this.show_content = true;
              return false
            }
          }
          if(_this.ruleForm3.beneficiary == "" && _this.beneficiary_flag == 1){
             if(sessionStorage.getItem('business_type') == 13){
              _this.isShow = true;
              _this.showContents = "您的适当性信息不完整，请先完成信息修改后再进行增开交易编码业务";
              return false;
             }else{
              _this.error_content = "账户交易实际受益人不能为空";
              _this.show_content = true;
              return false
            }
          }
          if(_this.ruleForm3.investment_variety == "" && _this.investment_variety_flag == 1){
            if(sessionStorage.getItem('business_type') == 13){
              _this.isShow = true;
              _this.showContents = "您的适当性信息不完整，请先完成信息修改后再进行增开交易编码业务";
              return false;
            }else{
            _this.error_content = "投资者的品种不能为空";
            _this.show_content = true;
             return false
            }
          }
          if(_this.ruleForm3.bad_record == "" && _this.bad_record_flag == 1){
            if(sessionStorage.getItem('business_type') == 13){
              _this.isShow = true;
              _this.showContents = "您的适当性信息不完整，请先完成信息修改后再进行增开交易编码业务";
              return false;
            }else{
              _this.error_content = "机构不良诚信记录不能为空";
              _this.show_content = true;
              return false
          }
          }
          if(_this.ruleForm3.deal_type == "" && _this.deal_type_flag == 1){
            if(sessionStorage.getItem('business_type') == 13){
              _this.isShow = true;
              _this.showContents = "您的适当性信息不完整，请先完成信息修改后再进行增开交易编码业务";
              return false;
            }else{
              _this.error_content = "交易类型不能为空";
              _this.show_content = true;
              return false
            }
          }
          var savePaper = {
            "systype": sessionStorage.getItem('systype'),
            "tax_type": _this.ruleForm3.tax_type.toString(),
            "platform_type": _this.ruleForm3.platform_type.toString(),
            "term_investment": _this.ruleForm3.term_investment.toString(),
            "proceeds_type": _this.ruleForm3.proceeds_type.toString(),
            "con_relation": _this.ruleForm3.con_relation.toString(),
            "beneficiary": _this.ruleForm3.beneficiary.toString(),
            "investment_variety": _this.ruleForm3.investment_variety.toString(),
            "bad_record": _this.ruleForm3.bad_record.toString(),
            "deal_type":_this.ruleForm3.deal_type.toString(),
            "business_type": sessionStorage.getItem('business_type'),
             "accept_no":_this.acceptNo
          }
          _this.axios.post('/userInfo/saveLaunderingBack', savePaper).then(function (res) {
            if(res.data.error_no == 0){
              // 方法：开始 流程 跳转页面
              var data = {
                "accept_no": _this.acceptNo,
                "business_type": sessionStorage.getItem('business_type'),
                "business_button_type": "1", // 下一步 按钮类型
                "business_flow": _this.$router.history.current.name
              }
              _this.loadingbox = true;
              _this.$ajax.post('/flow/updateAccept', data).then(function (res) {
                if (res.data.error_no == 0) {
                   _this.loadingbox = false;
                  _this.$router.push({name: res.data.business_flow});
                } else if (res.data.error_no == '-9999') {
                  _this.quit = true;
                  _this.content = res.data.error_info;
                   _this.loadingbox = false;
                } else {
                  _this.error_content = res.data.error_info;
                  _this.show_content = true;
                   _this.loadingbox = false;
                }
              }).catch(function (err) {
                console.log(err);
              });
              // 方法：结束 流程 跳转页面
            }else if(res.data.error_no == '-9999'){
               _this.quit = true;
              _this.content = res.data.error_info;
              _this.loadingbox = false;
            }else{
              _this.error_content = res.data.error_info;
              _this.show_content = true;
              _this.loadingbox = false;
            }
          }).catch(function (err) {
             console.log(err)
          });
        },
        change(){},
        changeTaxType(value, label){
          if(value[0]=="B" || value[0]=="C"){
            this.ruleForm3.tax_type = [];
            this.error_content = this.error_info;
            this.show_content = true;
          }
        },
        changePlatformType(value, label){
          if(value[0]=="A" || value[0]=="B" || value[0]=="C" || value[0]=="E"){
            this.ruleForm3.platform_type = [];
            this.error_content = this.error_info;
            this.show_content = true;
          }
        },
        changeConRelation(value, label){
          if(value[0]=="B"){
            this.ruleForm3.con_relation = [];
            this.error_content = this.error_info;
            this.show_content = true;
          }
        },
        changeBeneficiary(value, label){
          if(value[0]=="B"){
            this.ruleForm3.beneficiary = [];
            this.error_content =this.error_info;
            this.show_content = true;
          }
        },
        changeBadRecord(value, label){
          var xI = false;
          for(var i=0; i<value.length; i++){
            if(value[i] != "I") {
              xI = true;
              break;
            }
          }
          if(xI){
            this.ruleForm3.bad_record = [];
            this.error_content =this.error_info;
            this.show_content = true;
          }
        },
      }
    }
</script>

<style lang="less">
.launderingBack{
  .nextclick{
    width:82%;
    background: #00a8ff;
    line-height: 42px;
    color: white;
    text-align: center;
    margin: 4% auto;
    border-radius: 3px;
  }
    label{
      font-size: 14px;
      text-align: left;
      line-height: 36px;
    }
    .weui-cells_checkbox > label > *{
      padding-left: 4%;
      margin-top: -5px;
    }
    .weui-cells_checkbox .weui-check:checked + .vux-checklist-icon-checked:before{
      color: #49a5ff;
      width: 20px;
      height: 20px;
    }
    .weui-cells_checkbox .weui-icon-checked:before{
      font-size: 15px;
      position: absolute;
      left: 5%;
      top: 23%;
    }
    .weui-cells__title{
      text-align: left;
      font-size: 16px;
      color: #000000;
    }
    .weui-cell:before{
      border-top: none;
    }
    .van-button{
      width: 84%;
      height: 45px;
      background-color: #49A5FF;
      margin: 26px auto;
      font-size: 15px;
      color: #ffffff;
      text-align: center;
      line-height: 40px;
    }
    .van-field__control{
      text-align: right;
      margin-right: 5%;
    }
    .van-field .van-cell__title{
       max-width: 90px;
       text-align: left;
    }
    .sign{
       color:red;
       float: left;
       position: absolute;
       left: 2%;
      //  top: 30%;
      margin-top: 5px;
    }
    .identity{
      text-align: left;
      color: #000000;
      font-size: 15px;
      padding-left: 2%;
      line-height: 37px;
        span{
          color: red;
        }
    }
    .weui-cell {
      padding: 3px 15px;
      display: -webkit-box;
      display: -ms-flexbox;
      display: flex;
      -webkit-box-align: center;
      -ms-flex-align: center;
      align-items: center;
    }
    .van-cell-group{
      background-color: #fff;
      height: 42px;
    }
    .monicker{
      font-size: 15px;
      line-height: 34px;
      text-align: left;
      text-indent: 5%;
    }
    @media only screen and (min-device-width : 768px) and (max-device-width : 1024px){
      .monicker{
        text-indent: 3.5%;
      }

  }
    @media only screen and (min-device-width: 1366px) and (orientation: landscape) {
      .monicker{
        text-indent: 3%;
      }
      .weui-cell{
        padding: 3px 0px;
      }
    }
    .van-checkbox__icon, .van-checkbox__label {
      display: inline;
      vertical-align: middle;
      line-height: 20px;
      color: #333333;
    }
    .van-checkbox__icon--round .van-icon {
      border-radius:0;
    }
    .van-checkbox__icon .van-icon {
      line-height: 13px;
      width: 16px;
      height: 16px;
    }
  .van-checkbox {
    text-align: left;
    margin-left: 6%;
    line-height: 31px;
  }
  .van-checkbox__icon--disabled .van-icon{
    border-color: #c8c9cc;
    background-color: white;
  }
  .van-checkbox__icon--disabled.van-checkbox__icon--checked .van-icon{
    color: white;
    background: #A4D2FF;
  }
  .van-button {
    width: 84%;
    height: 45px;
    background-color: #49A5FF;
    margin: 23px auto;
    font-size: 15px;
    color: #ffffff;
    text-align: center;
    line-height: 44px;
    margin-bottom: 6%;
  }
}
</style>
