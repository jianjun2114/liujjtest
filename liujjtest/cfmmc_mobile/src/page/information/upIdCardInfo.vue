<template>
    <div class="upIdCardInfo">
      <van-nav-bar
        title="基本信息资料"
        right-text="关闭"
        right-arrow
        left-arrow
        @click-left="onClickLeft('0')"
        @click-right="onClickRight"/>
      <!--导航条-->
      <navigation/>
      <div v-if="business_type=='16'||business_type=='23'">
      <van-cell-group>
        <van-field v-model="ruleForm3.name" clearable maxlength="20" label=" 姓名" placeholder="请填写"
                   :error-message="errorMessage.name" :readonly="isDisabled"></van-field>
      </van-cell-group>
      <van-cell-group>
        <van-field v-model="ruleForm3.idNumber" clearable maxlength="18" label=" 身份证号" placeholder="请填写" ref="idNumber"
                    :error-message="errorMessage.idNumber" :readonly="isDisabled"></van-field>
      </van-cell-group>
        <van-cell-group>
          <van-field v-model="ruleForm3.idAddress" clearable label="身份证地址" placeholder="未填写" required/>
        </van-cell-group>
      <p style="line-height: 39px; background: white; text-align: left; padding-top: 2%; text-indent: 4px; margin-left: 2px;"><span style="color:red">*&nbsp;</span><span  style="margin-left: 5px;">身份证有效期</span></p>
      <group class="form-group">
        <div class="formDiv">
          <datetime class="datetimeLeft" :min-year="1900" v-model="ruleForm3.startTime" @on-change="startTimeChange"
                     placeholder="请选择">
          </datetime>
          <div class="nullDiv">-</div>
          <datetime class="datetimeRight" :max-year="2100" v-model="ruleForm3.endTime" @on-change="endTimeChange" :readonly="isLongTerm"
                    placeholder="请选择">
          </datetime>
        </div>
        <van-checkbox style="position: absolute;right: 8%; top: 16%; font-size: 14px;" v-model="longTerm" @change="longTermCheck()">长期</van-checkbox>
      </group>
      </div>
      <div v-if="business_type=='17'" class="modifyMobile">
      <!--  <van-cell-group>
          <van-field v-model="ruleForm3.phoneNumber" clearable maxlength="11" label=" 原联系电话" placeholder="请填写"
                                       :error-message="errorMessage.name" :readonly="isDisabled"></van-field>
        </van-cell-group>-->
        <van-cell-group>
          <van-field v-model="new_mobile" placeholder="请输入联系电话" label=" 联系电话"maxlength="13" required/>
        </van-cell-group>
        <van-cell-group>
          <van-field maxlength="4" placeholder="请输入图形验证码" class="group-x-input" v-model="identifyingCode" style="line-height: 40px;">
          </van-field>
          <img :src="src" class="vilidate_code_img" @click="VerifyCode()">
        </van-cell-group>
        <div class="input-group">
          <button v-bind:class="{ register_msg_btn: true, registerbag:isLoad }" v-show="show" v-on:click="getCode">{{mesg}}</button>
          <button v-bind:class="{ register_msg_btn: true, registerbag:isLoad , datetcolor: true}" v-show="!show">{{count}}秒后重新获取</button>
          <x-input title="验证码" :max="6" v-model="messageCode"  placeholder="请输入验证码" text-align="left"></x-input>
        </div>
        <p class="remark">手机号需要获取验证码，座机号不需要获取验证码</p>
      </div>
      <div v-if="business_type=='18'">
        <van-cell-group>
        <span class="sign">*&nbsp;</span>
        <popup-picker title="联系地址省" :show-name="true" :data="proviceListSelect" v-model="ruleForm3.sheng"
                      placeholder="请选择" @on-change="provinceChange" required
                      :error-message="errorMessage.sheng"></popup-picker>
        </van-cell-group>
        <van-cell-group>
          <span class="sign">*&nbsp;</span>
          <popup-picker required title="联系地址市" :show-name="true" :data="cityListSelect" v-model="ruleForm3.shi"
                        placeholder="请选择" required :error-message="errorMessage.shi"></popup-picker>
        </van-cell-group>
        <van-cell-group>
          <van-field maxlength="50"
                     v-model="ruleForm3.detailAddress" clearable label="联系地址" placeholder="请填写" required
                     :error-message="errorMessage.detailAddress"></van-field>
        </van-cell-group>
        <van-cell-group>
          <van-field v-model="ruleForm3.zipCode" clearable label="邮政编码" placeholder="请填写" maxlength="6"
                     required :error-message="errorMessage.zipCode" type="textarea" rows="1" autosize></van-field>
        </van-cell-group>
        <van-cell-group>
          <van-field v-model="ruleForm3.email" clearable label="电子邮箱" placeholder="请填写" maxlength="64"></van-field>
        </van-cell-group>
        <van-cell-group>
          <span class="sign">*&nbsp;</span>
          <popup-picker title="学历" :show-name="true" :data="educationalSelect" v-model="ruleForm3.educational"
                        placeholder="请选择" required :error-message="errorMessage.educational">
          </popup-picker>
        </van-cell-group>
        <van-cell-group>
          <span class="sign">*&nbsp;</span>
          <popup-picker title="职业" :show-name="true" :data="professionListSelect"
                        v-model="ruleForm3.profession"
                        placeholder="请选择" required :error-message="errorMessage.profession"></popup-picker>
        </van-cell-group>
       <!-- 扩展字段-->
        <van-cell-group>
          <van-field
            v-model="ruleForm3.val1"
            clearable
            :label="val1_name"
            placeholder="请填写"
            v-if="val1_flag == 'true'"
            maxlength="25"
          />
        </van-cell-group>
        <van-cell-group v-if="val1_flag == 'options'">
          <popup-picker :title="val1_name" :data="val1options" :show-name="true" v-model="ruleForm3.val1select"
                        placeholder="请选择"></popup-picker>
        </van-cell-group>

        <van-cell-group>
          <van-field
            v-model="ruleForm3.val2"
            clearable
            :label="val2_name"
            placeholder="请填写"
            v-if="val2_flag == 'true'"
            maxlength="25"
          />
        </van-cell-group>
        <van-cell-group v-if="val2_flag == 'options'">
          <popup-picker :title="val2_name" :data="val2options" v-model="ruleForm3.val2select"
                        placeholder="请选择"></popup-picker>
        </van-cell-group>

        <van-cell-group>
          <van-field
            v-model="ruleForm3.val3"
            clearable
            :label="val3_name"
            placeholder="请填写"
            v-if="val3_flag == 'true'"
            maxlength="25"
          />
        </van-cell-group>
        <van-cell-group v-if="val3_flag == 'options'">
          <popup-picker :title="val3_name" :data="val3options" v-model="ruleForm3.val3select"
                        placeholder="请选择"></popup-picker>
        </van-cell-group>

        <span v-if="tax_type_flag == '1'" class="sign">*&nbsp;</span>
        <p v-if="tax_type_flag == '1'" class="monicker">您的税收居民身份为：</p>
        <checklist
          :options="commonList1"
          v-model="ruleForm3.tax_type"
          :max="1"
          v-if="tax_type_flag == '1'"
          @on-change="changeTaxType">
        </checklist>

        <span v-if="platform_type_flag == '1'" class="sign">*&nbsp;</span>
        <p v-if="platform_type_flag == '1'" class="monicker">您的政要人物关系为：</p>
        <checklist
          :options="commonList2"
          v-model="ruleForm3.platform_type"
          :max="1"
          v-if="platform_type_flag == '1'"
          @on-change="changePlatformType">
        </checklist>

        <span v-if="term_investment_flag == '1'" class="sign">*&nbsp;</span>
        <p v-if="term_investment_flag == '1'" class="monicker">您的投资期限为：</p>
        <checklist
          :options="commonList3"
          v-model="ruleForm3.term_investment"
          :max="1"
          v-if="term_investment_flag == '1'">
        </checklist>

        <span v-if="proceeds_type_flag == '1'" class="sign">*&nbsp;</span>
        <p v-if="proceeds_type_flag == '1'" class="monicker">您的期望收益为：</p>
        <checklist
          :options="commonList4"
          v-model="ruleForm3.proceeds_type"
          :max="1"
          v-if="proceeds_type_flag == '1'">
        </checklist>

        <span v-if="con_relation_flag == '1'" class="sign">*&nbsp;</span>
        <p v-if="con_relation_flag == '1'" class="monicker">您的账户是否存在被他人控制或存在控制他人账户的情况：</p>
        <checklist
          :options="commonList5"
          v-model="ruleForm3.con_relation"
          :max="1"
          v-if="con_relation_flag == '1'"
          @on-change="changeConRelation">
        </checklist>

        <span v-if="beneficiary_flag == '1'" class="sign">*&nbsp;</span>
        <p v-if="beneficiary_flag == '1'" class="monicker">您的账户交易实际受益人为：</p>
        <checklist
          :options="commonList6"
          v-model="ruleForm3.beneficiary"
          :max="1"
          v-if="beneficiary_flag == '1'"
          @on-change="changeBeneficiary">
        </checklist>

        <span v-if="investment_variety_flag == '1'" class="sign">*&nbsp;</span>
        <p v-if="investment_variety_flag == '1'" class="monicker">您想投资的品种为：</p>
        <van-checkbox-group
          v-model="ruleForm3.investment_variety"
          v-if="investment_variety_flag == '1'"
          style="background: white; border-top: 1px solid #eeeeee; border-bottom: 1px solid #eeeeee;">
          <van-checkbox
            v-for="(item, index) in commonList7"
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
      </div>
      <van-button size="large" @click="nextStep" class="van-button">下一步</van-button>
      <quit :content="content" :quit="quit"></quit>
      <popUp :tips="error_content" :showBox="show_content" @notarize="notarize"></popUp>
      <waitPop :loadingbox="loadingbox"></waitPop>
</div>
</template>
<script>
  import { GroupTitle, Group, Cell, XInput, Selector, PopupPicker, Datetime, XNumber, ChinaAddressV2Data , XAddress, XTextarea, Checklist, PopupRadio} from 'vux'
  import Quit from "../../components/quit.vue";
  import PopUp from "../../components/popUp.vue";
  import WaitPop from "../../components/waitPop.vue";
  import Navigation from "../../components/navigation.vue";
    export default {
    components: {
      Group,
  		GroupTitle,
  		Cell,
  		XInput,
  		Selector,
  		PopupPicker,
  		XAddress,
  		Datetime,
  		XNumber,
  		XTextarea,
      Checklist,
      PopupRadio,
      PopUp,
      Quit,
      WaitPop,
      Navigation
    },
    data(){
      return {
        title:"身份信息修改",
        accept_no:"",
        error_content: "",
        show_content:false,
        content:"",
        quit:false,
        isLongTerm: false,
        longTerm:false,
        loadingbox: false,
        isDisabled:true,
        business_type:"",
        proviceListSelect: [],
        cityListSelect: [],
        professionListSelect:[],
        educationalSelect:[],
        reg_phone:"",
        new_mobile:"",
        messageCode:"",
        mesg:"获取验证码",
        count: "",
        isLoad: true,
        show: true,
        clientName:"",//修改联系电话 需传用户姓名
        ruleForm3: {
          name: "",
          idNumber: "",
          idAddress: "",
          startTime: "",
          endTime: "",
          sheng: [],
          shi: [],
          detailAddress: "",
          zipCode: "",
          email: "",
          profession: [],
          educational: [],
          tax_type: [],
          platform_type: [],
          term_investment: [],
          proceeds_type: [],
          con_relation: [],
          beneficiary: [],
          investment_variety: [],
          bad_record: [],
          deal_type:[],
          phoneNumber: "",
          val1: "",
          val2: "",
          val3: "",
          val1select: [],
          val2select: [],
          val3select: []
          /*
           contact_mobile: "",

           buss1: [],
           buss2: [],
           buss3: [],
           buss4: [],
           id_kind: "",
           issued_depart: "",
           client_gender: "",
           */
        },
        val1options: [],//下来选项值
        val1text: '',//记录下拉的值  方便保存
        val2options: [],
        val2text: '',
        val3options: [],
        val3text: '',
        val1_name: "",//扩展字段显示名称
        val2_name: "",
        val3_name: "",
        val1_flag: "",//根据返回结果标识是输入框还是下拉框
        val2_flag: "",
        val3_flag: "",
       //minzu_visible_flag:"",
        //industry_flag:"",
        bad_record_flag:"",
        investment_variety_flag:"",
        beneficiary_flag:"",
        con_relation_flag:"",
        proceeds_type_flag:"",
        term_investment_flag:"",
        platform_type_flag:"",
        tax_type_flag:"",
        deal_type_flag:"",
        identifyingCode:"",//图形验证码
        src: "",
        error_info:"请您联系我公司客服人员临柜办理，联系电话" + sessionStorage.getItem('broker_hotline') + "。",
        commonList1: [{value:'仅为中国税收居民', key:'A'}, {value:'仅为非居民', key:'B'}, {value:'既是中国税收居民又是其他国家(地区)税收居民', key:'C'}],
        commonList2: [{value:'外国政要', key:'A'}, {value:'外国政要家庭成员', key:'B'}, {value:'与外国政要关系密切', key:'C'},{value:'国际组织的高级管理人员', key:'E'},{value:'无关系', key:'D'}],
        commonList3: [{value:'0年-1年', key:'A'}, {value:'1年-5年', key:'B'}, {value:'5年以上', key:'C'}],
        commonList4: [{value:'稳健', key:'A'}, {value:'成长', key:'B'}, {value:'激进', key:'C'}],
        commonList5: [{value:'不存在', key:'A'}, {value:'存在', key:'B'}],
        commonList6: [{value:'本人', key:'A'}, {value:'其他', key:'B'}],
        commonList7: [{value:'期货、期权',key:'A'},{value:'资管产品',key:'B'}, {value:'其他',key:'C'}],
        commonList8: [{value:'中国人民银行征信中心', key:'A'}, {value:'最高人民法院失信被执行人名单', key:'B'}, {value:'工商行政管理机构', key:'C'}, {value:'税务管理机构', key:'D'}, {value:'监管机制、自律组织', key:'E'}, {value:'投资者在期货经营机构从事投资活动时产生的违约行为记录', key:'F'}, {value:'过度维权等不当行为信息', key:'G'}, {value:'其他组织', key:'H'}, {value:'无', key:'I'}],
        commonList9: [{value:'投机', key:'A'}, {value:'套利', key:'B'}, {value:'套保', key:'C'}],
        errorMessage: {name: ""}
  	  }
    },
    mounted() {
      this.VerifyCode();
    },
    created(){
      var _this = this;
      _this.business_type = sessionStorage.getItem('business_type');
      //获取受理单
      var data = {
        "business_type": _this.business_type,
        "register_way": '2',
      };
      _this.loadingbox = true;
      _this.axios.post('/flow/qryAcceptInfo', data).then(function (res) {
        _this.loadingbox = false;
        if (res.data.error_no == '0'){
          _this.accept_no = res.data.accept_no;
          if (_this.$router.history.current.name != res.data.business_flow) {
            _this.$router.push({name: res.data.business_flow});
          }
          var thdData = {
            "systype": sessionStorage.getItem('systype'),
            "business_type": sessionStorage.getItem('business_type'),
            "accept_no": _this.accept_no
          }
          _this.axios.post('/userInfo/qryThdClientInfo', thdData).then(function (res) {   //同步三方信息
            if (res.data.error_no == '0') {
              _this.clientName = res.data.client_name;
              _this.ruleForm3.idAddress = res.data.id_addr;//身份证地址
              if(_this.business_type == "16" ||_this.business_type == "23" ){
                _this.getOcrInfo();
              }else{ // 17 或者 18
                _this.initHome();
              }
            }else if(res.data.error_no == '-9999'){
              _this.quit = true;
              _this.content = res.data.error_info;
            }else {
              _this.quit = true;
              _this.content = res.data.error_info;
            }
          }).catch(function (err) {
            console.log(err)
          });
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
    },
    methods:{
      onClickRight(){
        this.$router.push({name:'MyBussiness'});
      },
      onClickLeft(type){
        var _this = this;
        if (_this.accept_no!= "" && _this.accept_no != undefined) {
          // 方法：开始 流程 跳转页面
          var data = {
            "accept_no": _this.accept_no,
            "business_type": sessionStorage.getItem("business_type"),
            "business_button_type": type,
            "business_flow": _this.$router.history.current.name
          };
          _this.loadingbox = true;
          _this.$ajax.post('/flow/updateAccept', data).then(function (res) {
            _this.loadingbox = false;
            if (res.data.error_no == 0) {
              _this.$router.push({name: res.data.business_flow});
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
      notarize() {
        this.show_content = false;
      },
      //证件有效期修改
      startTimeChange() {
        var _this = this;
        if (new Date(_this.ruleForm3.startTime) > new Date(_this.ruleForm3.endTime)) {
          _this.error_content = "开始日期大于结束日期";
          _this.show_content = true;
        }
      },
      endTimeChange() {
        var _this = this;
        if (new Date(_this.ruleForm3.endTime) < new Date(_this.ruleForm3.startTime)) {
          _this.error_content = "开始日期大于结束日期";
          _this.show_content = true;
        }
        if (Date.parse(new Date(_this.ruleForm3.endTime)) - new Date().getTime() <= -100000000) {
          _this.error_content = "身份证已过期";
          _this.show_content = true;
        }
      },
      getOcrInfo() {
        var _this = this;
        //获取ocr数据
        _this.loadingbox = true;
        this.$ajax.post('/userInfo/qryOcrInfo', {"systype": sessionStorage.getItem('systype')}).then(function (res) {
          _this.loadingbox = false;
          if (res.data.error_no == '0') {
            _this.ruleForm3.name = res.data.client_name;
            _this.ruleForm3.startTime = res.data.id_begindate_string;
            if (res.data.id_enddate_string == '2099-12-31') {
              _this.isLongTerm = true;
              _this.longTerm = true;
              _this.ruleForm3.endTime = res.data.id_enddate_string;
            } else {
              _this.isLongTerm = false;
              _this.ruleForm3.endTime = res.data.id_enddate_string;
            }
            _this.end = res.data.id_enddate;
           // _this.ruleForm3.id_kind = res.data.id_kind;
            _this.ruleForm3.idNumber = res.data.id_no;
            _this.ruleForm3.idAddress = res.data.id_address;
          } else if (res.data.error_no == -9999 || res.data.error_no == -8888) {
            _this.quit = true;
            _this.content = res.data.error_info;
          } else {
            _this.error_content = res.data.error_info;
            _this.show_content = true;
          }
        }).catch(function (error) {
          console.log(error)
        })
      },
      longTermCheck() {  //长期勾选
        if (this.longTerm) {
          this.ruleForm3.endTime = '2099-12-31';
          this.isLongTerm = true;
        } else {
          this.ruleForm3.endTime = this.GMTtoStr(new Date());
          this.isLongTerm = false;
        }
      },
      //其他资料修改方法
      getCustInfo() {
        var _this = this;
        _this.loadingbox = true;
        this.axios.post('/userInfo/showUserInfo', {"systype": sessionStorage.getItem('systype')}).then(function (res) {
          _this.loadingbox = false;
          if (res.data.error_no == 0) {
            //---------加载列表项  开始-------------
            //联系地址省
            var proviceList = res.data.provice_list;
            var a_proviceList = [];
            var b_proviceList = [];
            for (var i = 0; i < proviceList.length; i++) {
              var obj_proviceList = {};
              obj_proviceList.name = proviceList[i].name;
              obj_proviceList.value = proviceList[i].entryType;
              b_proviceList.push(obj_proviceList);
            }
            a_proviceList.push(b_proviceList);
            _this.proviceListSelect = a_proviceList;
            // 联系地址市
            var cityList = res.data.city_list;
            var a_cityList = [];
            var b_cityList = [];
            if (cityList.length != 0) {
              for (var i = 0; i < cityList.length; i++) {
                var obj_cityList = {};
                obj_cityList.name = cityList[i].name;
                obj_cityList.value = cityList[i].entryType;
                b_cityList.push(obj_cityList);
              }
              a_cityList.push(b_cityList);
              _this.cityListSelect = a_cityList;
            }
            // 学历
            var degreeList = res.data.degree_list;
            var a_degreeList = [];
            var b_degreeList = [];
            for (var i = 0; i < degreeList.length; i++) {
              var obj_degreeList = {};
              obj_degreeList.name = degreeList[i].name;
              obj_degreeList.value = degreeList[i].entryType;
              b_degreeList.push(obj_degreeList);
            }
            a_degreeList.push(b_degreeList);
            _this.educationalSelect = a_degreeList;
            // 职业
            var professionList = res.data.profession_list;
            var a_professionList = [];
            var b_professionList = [];
            for (var i = 0; i < professionList.length; i++) {
              if (professionList[i].entryType >= 500 || professionList[i].entryType == res.data.profession_code) {
                var obj_professionList = {};
                obj_professionList.name = professionList[i].name;
                obj_professionList.value = professionList[i].entryType;
                b_professionList.push(obj_professionList);
              }
            }
            a_professionList.push(b_professionList);
            _this.professionListSelect = a_professionList;
            //---------加载列表项  结束-------------
            //---------数据回显  开始-------------
            var sheng = [];
            if (res.data.contact_addr_province != "") {
              sheng[0] = res.data.contact_addr_province;
            }
            _this.ruleForm3.sheng = sheng;
            var shi = [];
            if (res.data.contact_addr_city != "") {
              shi[0] = res.data.contact_addr_city;
              _this.city = res.data.contact_addr_city;
            }
            _this.ruleForm3.shi = shi;
            _this.ruleForm3.detailAddress = res.data.contact_addr_detail;
            _this.ruleForm3.zipCode = res.data.post_code;
            _this.ruleForm3.email = res.data.email;
            if (res.data.profession_code != "") {
              _this.ruleForm3.profession = [res.data.profession_code];
            }
            if (res.data.degree_code != "") {
              _this.ruleForm3.educational = [res.data.degree_code];
            }
            //其他信息-反洗钱等信息
            _this.ruleForm3.tax_type = [res.data.tax_type];
            _this.ruleForm3.platform_type = [res.data.platform_type];
            _this.ruleForm3.term_investment = [res.data.term_investment];
            _this.ruleForm3.proceeds_type = [res.data.proceeds_type];
            _this.ruleForm3.con_relation = [res.data.con_relation];
            _this.ruleForm3.beneficiary = [res.data.beneficiary];
            if (res.data.bad_record != "" && res.data.bad_record != undefined) {
              _this.ruleForm3.bad_record = res.data.bad_record.split(',');
            }
            if (res.data.investment_variety != "" && res.data.investment_variety != undefined) {
              _this.ruleForm3.investment_variety = res.data.investment_variety.split(',');
            }
            if(res.data.deal_type !== "" && res.data.deal_type != undefined){
              _this.ruleForm3.deal_type = res.data.deal_type.split(",")
            }
            //扩展字段
            var val1 =  res.data.val1;
            var val2 =  res.data.val2;
            var val3 =  res.data.val3;
            _this.extfield(val1,"1");
            _this.extfield(val2,"2");
            _this.extfield(val3,"3");
            //---------数据回显  结束-------------
          }
        }).catch((error) => {
          console.log(error)
        });
      },
      provinceChange() {
        var _this = this;
        const getCity = {
          "province_code": _this.ruleForm3.sheng[0]
        };
        _this.loadingbox = true;
        _this.$ajax.post('/userInfo/chooseLinkCity', getCity).then(function (res) {
          _this.loadingbox = false;
          if (res.data.error_no == 0) {
            var cityList1 = res.data.city_list;
            var a_cityList1 = [];
            var b_cityList1 = [];
            for (var i = 0; i < cityList1.length; i++) {
              var obj_cityList1 = {};
              obj_cityList1.name = cityList1[i].name;
              obj_cityList1.value = cityList1[i].entryType;
              b_cityList1.push(obj_cityList1);
            }
            a_cityList1.push(b_cityList1);
            _this.cityListSelect = a_cityList1;
            if (_this.city == _this.ruleForm3.shi) {
              _this.city = "";
            } else {
              var city_ = _this.ruleForm3.shi + "";
              var cityTz_ = _this.cityTz + "";
              if (city_ == cityTz_) {
                _this.cityTz = "";
              } else {
                var shi = [];
                _this.ruleForm3.shi = shi;
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
      },
      initHome(){
        // 方法：开始 通用配置获取
        var _this = this;
        _this.loadingbox = true;
        _this.$ajax.post('/userInfo/qryBaseInfoParam', {"broker_id":sessionStorage.getItem('broker_id')},{}).then(function(res){
          _this.loadingbox = false;
          if(res.data.error_no == 0){
            if(_this.business_type == "17"){
              _this.reg_phone = res.data.phone_limit_Num;
              //_this.getPhoneInfo();//之前显示原联系电话
            }else if(_this.business_type == "18"){
              _this.bad_record_flag = res.data.bad_record_flag;
              _this.investment_variety_flag = res.data.investment_variety_flag;
              _this.beneficiary_flag = res.data.beneficiary_flag;
              _this.con_relation_flag = res.data.con_relation_flag;
              _this.proceeds_type_flag = res.data.proceeds_type_flag;
              _this.term_investment_flag = res.data.term_investment_flag;
              _this.platform_type_flag = res.data.platform_type_flag;
              _this.tax_type_flag = res.data.tax_type_flag;
              _this.deal_type_flag = res.data.deal_type_flag;

              _this.getCustInfo();
            }
          }else if (res.data.error_no == '-9999') {
            _this.quit = true;
            _this.content = res.data.error_info;
          }else{
            _this.error_content = res.data.error_info;
            _this.show_content = true;
          }
        }).catch(function (err) {
          console.log(err);
        });
        // 方法：结束 通用配置获取
      },
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
      GMTtoStr(time) {
        var now = new Date(time);
        var year = now.getFullYear();
        var mon = now.getMonth() + 1;
        var date = now.getDate();
        if (mon < 10) {
          mon = '0' + mon;
        }
        ;
        if (date < 10) {
          date = '0' + date;
        }
        var postDate = year + '-' + mon + '-' + date;
        return postDate;
      },
      GMTtoStr1(time) {
        var now = new Date(time);
        var year = now.getFullYear();
        var mon = now.getMonth() + 1;
        var date = now.getDate();
        if (mon < 10) {
          mon = '0' + mon;
        }
        ;
        if (date < 10) {
          date = '0' + date;
        }
        var postDate = year + '' + mon + '' + date;
        return postDate;
      },
      nextStep() {
        var _this = this;
       if(_this.business_type == "16" || _this.business_type == "23"){
         _this.modifyUserIDcard();
       }else if(_this.business_type == "17"){
         _this.modifyMobile();
       }else if(_this.business_type == "18"){
         _this.otherInfoSubmit();
       }
      },
      modifyUserIDcard(){
        var _this = this;
        //提交校验
        if (!_this.vAddInfo()) {
          return;
        }
        var startTime = _this.GMTtoStr1(_this.ruleForm3.startTime);
        var endingTime = _this.GMTtoStr1(_this.ruleForm3.endTime);
        var idAddress = _this.idAddressFormat(_this.ruleForm3.idAddress);
        var saveData = {
          "accept_no": _this.accept_no,
          "systype": sessionStorage.getItem('systype'),
          "business_type": sessionStorage.getItem('business_type'),
          "id_kind": '1',
          "id_no": this.ruleForm3.idNumber,
          "client_name": this.ruleForm3.name,
          "id_begindate": startTime,
          "id_enddate": endingTime,
          "id_address": idAddress
        };
        _this.loadingbox = true;
        _this.$ajax.post('/userInfo/modifyUserIDcard', saveData).then(function (res) {
          var error_no = res.data.error_no;
          _this.loadingbox = false;
          if (error_no == 0) {
            var data = {
              "accept_no": _this.accept_no,
              "business_type": sessionStorage.getItem('business_type'),
              "accept_status": "",//不更改时不传值
              "business_button_type": '1',
              "business_flow": _this.$router.history.current.name
            };
            _this.loadingbox = true;
            _this.$ajax.post('/flow/updateAccept', data).then(function (res) {
              _this.loadingbox = false;
              if (res.data.error_no == 0) {
                _this.$router.push({ name: res.data.business_flow});
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
          } else if (error_no == -9999) {
            _this.quit = true;
            _this.content = res.data.error_info;
          } else {
            _this.error_content = res.data.error_info;
            _this.show_content = true;
          }
        }).catch(function (error) {
          console.log(error)
        });
      },
      otherInfoSubmit() {
        var _this = this;
        if(_this.ruleForm3.profession[0] < 500){
          _this.error_content = "请选择其他职业";
          _this.show_content = true;
          return;
        }
        //校验表单信息
        if (!_this.vAddInfo()) {
          return;
        }
        //校验反洗钱信息是否完整
        if(!_this.validateLaundering()){
          return;
        }
        var _val1_value = _this.ruleForm3.val1;
        var _val2_value = _this.ruleForm3.val2;
        var _val3_value = _this.ruleForm3.val3;
        if (_this.val1_flag == 'options') {
          _val1_value = _this.ruleForm3.val1select + '';
        }
        if (_this.val2_flag == 'options') {
          _val2_value = _this.ruleForm3.val2select;
        }
        if (_this.val3_flag == 'options') {
          _val3_value = _this.ruleForm3.val3select;
        }
        if (_val1_value == undefined) {
          _val1_value = '';
        }
        if (_val2_value == undefined) {
          _val2_value = '';
        }
        if (_val3_value == undefined) {
          _val3_value = '';
        }
        _val1_value = _val1_value + "$$" + _this.val1text;
        _val2_value = _val2_value + "$$" + _this.val2text;
        _val3_value = _val3_value + "$$" + _this.val3text;

        var otherInfoData = {
          "business_type": sessionStorage.getItem('business_type'),
          "systype": sessionStorage.getItem('systype'),
          "accept_no": _this.accept_no,
          "contact_addr_province": _this.ruleForm3.sheng[0],
          "contact_addr_city": _this.ruleForm3.shi[0],
          "contact_addr_detail": _this.ruleForm3.detailAddress,
          "post_code": _this.ruleForm3.zipCode,
          "email": _this.ruleForm3.email,
          "degree_code": _this.ruleForm3.educational[0],
          "profession_code": _this.ruleForm3.profession[0],
          "tax_type": _this.ruleForm3.tax_type[0],
          "platform_type": _this.ruleForm3.platform_type[0],
          "bad_record": _this.ruleForm3.bad_record.join(','),
          "term_investment": _this.ruleForm3.term_investment[0],
          "investment_variety": _this.ruleForm3.investment_variety.join(','),
          "proceeds_type": _this.ruleForm3.proceeds_type[0],
          "con_relation": _this.ruleForm3.con_relation[0],
          "beneficiary": _this.ruleForm3.beneficiary[0],
          "deal_type": _this.ruleForm3.deal_type.join(','),
          "val1":  _this.val1_name + "$$" + _val1_value,
          "val2":  _this.val2_name + "$$" + _val2_value,
          "val3":  _this.val3_name + "$$" + _val3_value
        };
        _this.loadingbox = true;
        _this.$ajax.post('/userInfo/modifyUserInfo', otherInfoData).then(function (res){
          _this.loadingbox = false;
          if (res.data.error_no == 0) {
            var data = {
              "accept_no": _this.accept_no,
              "business_type": sessionStorage.getItem('business_type'),
              "accept_status": "",//不更改时不传值
              "business_button_type": '1',
              "business_flow": _this.$router.history.current.name
            };
            _this.loadingbox = true;
            _this.$ajax.post('/flow/updateAccept', data).then(function (res1){
              _this.loadingbox = false;
              if (res1.data.error_no == 0) {
                _this.$router.push({name: res1.data.business_flow});
              } else if (res1.data.error_no == -9999) {
                _this.quit = true;
                _this.content = res.data.error_info;
              } else {
                _this.error_content = res.data.error_info;
                _this.show_content = true;
              }
            }).catch((err) => {
              console.log(err);
            })
          } else if (res.data.error_no == -9999) {
            _this.quit = true;
            _this.content = res.data.error_info;
          } else {
            _this.error_content = res.data.error_info;
            _this.show_content = true;
          }
        }).catch((err) => {
          console.log(err);
        })
      },
      modifyMobile(){
        var _this = this;
        if (!_this.new_mobile) {
          _this.show_content = true;
          _this.error_content = "请输入联系电话！";
          return false
        }
        //验证手机号还是座机号
        var v_info = _this.v_mobile(_this.new_mobile, _this.reg_phone,"phone");
        if(v_info == "truetrue"){
          if (!_this.identifyingCode) {
            _this.show_content = true;
            _this.error_content = "请输入图形验证码！";
            return false;
          }
          if(!_this.messageCode){
            _this.show_content = true;
            _this.error_content = "请输入验证码！";
            return false
          }
        }else{
           v_info = _this.v_mobile(_this.new_mobile, _this.reg_phone,"telPhone");
            if(!_this.vvalidate(v_info)){
              return;
            }
        }
        var saveData = {
          "accept_no": _this.accept_no,
          "systype": sessionStorage.getItem('systype'),
          "business_type": sessionStorage.getItem('business_type'),
          "validate_code":_this.messageCode,
          "contact_number":_this.new_mobile,
          "user_name":_this.clientName
        };
        _this.loadingbox = true;
        _this.$ajax.post('/userInfo/modifyUserTelphone', saveData).then(function (res) {
          _this.loadingbox = false;
          if(res.data.error_no==0) {
            var data = {
              "accept_no": _this.accept_no,
              "business_type": sessionStorage.getItem('business_type'),
              "accept_status": "",//不更改时不传值
              "business_button_type": '1',
              "business_flow": _this.$router.history.current.name
            };
            _this.loadingbox = true;
            _this.$ajax.post('/flow/updateAccept', data).then(function (res) {
              _this.loadingbox = false;
              if (res.data.error_no == 0) {
                _this.$router.push({ name: res.data.business_flow});
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
          }else if (res.data.error_no == '-9999') {
            _this.quit = true;
            _this.content = res.data.error_info;
          } else {
            _this.error_content = res.data.error_info;
            _this.show_content = true;
          }
        }).catch(function (err) {
          console.log(err);
        });
      },
      /*mobileFiled(){
        if(!this.new_mobile){
          this.error_content = "请输入新联系电话！";
          this.show_content = true;
          return;
        }
        //验证手机或座机的格式
        var v_info = this.v_telPhone(this.new_mobile, this.reg_phone,"phone");
        if(v_info == "truetrue"){
         this.isPhone = false;
          return;
        }
        var v_info1 = this.v_telPhone(this.new_mobile, this.reg_phone,"telPhone");
        if(v_info1=="truetrue"){
          this.isPhone = true;
          return
        };
        this.error_content = "新联系电话格式不正确！";
        this.show_content = true;
      },*/
      //短信验证码
      getCode() {
        var _this = this;
        if (!_this.new_mobile) {
          _this.show_content = true;
          _this.error_content = "请输入联系电话！"
          return false
        }
        var v_info = _this.v_mobile(_this.new_mobile, _this.reg_phone,"telPhone");
        if(v_info == "truetrue"){
          return;
        }
         v_info = _this.v_mobile(_this.new_mobile, _this.reg_phone,"phone");
        if (!_this.vvalidate(v_info)) {
          return;
        }
        if (!_this.identifyingCode) {
          _this.show_content = true;
          _this.error_content = "请输入图形验证码！";
          return false;
        }
        var postData = {
          "phone": _this.new_mobile,
          "business_type": sessionStorage.getItem('business_type'),
          "systype":sessionStorage.getItem('systype'),
          "verify_code":_this.identifyingCode
        }
        _this.loadingbox = true;
        _this.$ajax.post('/index/sendMassage', postData).then(function (res) {
          _this.loadingbox = false;
          if (res.data.error_no == 0) {
            _this.showTimer();
          }else if(res.data.error_no == "-9999"){
            _this.quit = true;
            _this.content = res.data.error_info;
          }else if(res.data.error_no == "-8888"){
            _this.quit = true;
            _this.content = res.data.error_info;
          } else {
            _this.show_content = true;
            _this.error_content = res.data.error_info;
          }
        }).catch(function (err) {
          console.log(err)
        });
      },
      showTimer() {
        var _this = this;
        _this.isLoad=false;
        const TIME_COUNT = 60;
        if (!_this.timer) {
          _this.count = TIME_COUNT;
          _this.show = false;
          _this.timer = setInterval(() => {
            if (_this.count > 0 && this.count <= TIME_COUNT) {
              _this.count--;
            } else {
              _this.show = true;
              _this.mesg = "重新获取验证码";
              _this.isLoad=true;
              clearInterval(this.timer);
              _this.timer = null;
            }
          }, 1000)
        }
      },
   /*   getPhoneInfo() {
        var _this = this;
        _this.loadingbox = true;
        this.axios.post('/userInfo/showUserInfo', {"systype": sessionStorage.getItem('systype')}).then(function (res) {
          _this.loadingbox = false;
          if (res.data.error_no == 0) {
            //---------数据回显  开始-------------
            if(res.data.mobile != undefined && res.data.mobile.trim()!=""){
              //判断若是手机中间4位为“*”，若是座机后4位为“*”
              var v_info = _this.v_mobile(res.data.contact_number, _this.reg_phone,"phone");
              if(v_info == "truetrue"){
                _this.ruleForm3.phoneNumber = res.data.contact_number.substr(0, 3) + '****' + res.data.contact_number.substr(7);
                return;
              }
              _this.ruleForm3.phoneNumber = res.data.contact_number.substr(0, res.data.contact_number.length-4) + '****';
            }
            //---------数据回显  结束-------------
          }
        }).catch((error) => {
          console.log(error)
        });
      },*/
      vAddInfo() {
        var _this = this;
        var v_info = "";
        if(_this.business_type == "16"||_this.business_type == "23"){
        /*  //姓名
          var v_info = _this.v_custname(_this.ruleForm3.name);
          if (!_this.vvalidate(v_info)) {
            return false;
          }
          //身份证号
          v_info = _this.v_idno(_this.ruleForm3.idNumber);
          if (!_this.vvalidate(v_info)) {
            return false;
          }*/
            //证件照地址
            v_info = _this.v_idAddress(_this.ruleForm3.idAddress);
            if (!_this.vvalidate(v_info)) {
              return false;
            }
          //身份证日期校验
          v_info = _this.v_CompDate(_this.ruleForm3.startTime, _this.ruleForm3.endTime);
          if (!_this.vvalidate(v_info)) {
            return false;
          }
        }else if(_this.business_type == "18"){
          //联系地址
          v_info = _this.v_custAddress(_this.ruleForm3.sheng, _this.ruleForm3.shi, _this.ruleForm3.detailAddress);
          if (!_this.vvalidate(v_info)) {
            return false;
          }
          //邮编
          v_info = _this.v_zipCode(_this.ruleForm3.zipCode);
          if (!_this.vvalidate(v_info)) {
            return false;
          }
        /*  //联系电话
          v_info = _this.v_telPhone(_this.ruleForm3.phoneNumber, _this.reg_phone, 'telPhone');
          if (!_this.vvalidate(v_info)) {
            return false;
          }
          //联系手机
          v_info = _this.v_telPhone(_this.ruleForm3.contact_mobile, _this.reg_phone, 'phone');
          if (!_this.vvalidate(v_info)) {
            return false;
          }*/
          //电子邮箱
          v_info = _this.v_email(_this.ruleForm3.email);
          if (!_this.vvalidate(v_info)) {
            return false;
          }
          //职业
          v_info = _this.v_profession(_this.ruleForm3.profession);
          if (!_this.vvalidate(v_info)) {
            return false;
          }
          //学历
          v_info = _this.v_educational(_this.ruleForm3.educational);
          if (!_this.vvalidate(v_info)) {
            return false;
          }
          //扩展字段1(不包含下拉)
          v_info = _this.v_val(_this.val1_name,_this.ruleForm3.val1);
          if (!_this.vvalidate(v_info)) {
            return false;
          }
          //扩展字段2(不包含下拉）
          v_info = _this.v_val(_this.val2_name,_this.ruleForm3.val2);
          if (!_this.vvalidate(v_info)) {
            return false;
          }
          //扩展字段3(不包含下拉）
          v_info = _this.v_val(_this.val3_name,_this.ruleForm3.val3);
          if (!_this.vvalidate(v_info)) {
            return false;
          }
        }
        return true;
      },
      vvalidate(v_info) {
        var _this = this;
        if (v_info != 'truetrue') {
          _this.error_content = v_info;
          _this.show_content = true;
          return false;
        }
        return true;
      },
      validateLaundering(){
        var _this = this;
        if(_this.tax_type_flag == 1 && _this.ruleForm3.tax_type == ""){
            _this.error_content = "税收居民不能为空";
            _this.show_content = true;
            return false;
        }
        if(_this.ruleForm3.platform_type == "" && _this.platform_type_flag == 1){
            _this.error_content = "政要人物关系不能为空";
            _this.show_content = true;
            return false;
        }
        if(_this.ruleForm3.term_investment == "" && _this.term_investment_flag == 1){
            _this.error_content = "投资期限不能为空";
            _this.show_content = true;
            return false;
        }
        if(_this.ruleForm3.proceeds_type == "" && _this.proceeds_type_flag == 1 ){
            _this.error_content = "期望收益不能为空";
            _this.show_content = true;
            return false;
        }
        if(_this.ruleForm3.con_relation == "" && _this.con_relation_flag == 1){
            _this.error_content = "账户是否存在实际控制关系不能为空";
            _this.show_content = true;
            return false;
        }
        if(_this.ruleForm3.beneficiary == "" && _this.beneficiary_flag == 1){
            _this.error_content = "账户交易实际受益人不能为空";
            _this.show_content = true;
            return false;
        }
        if(_this.ruleForm3.investment_variety == "" && _this.investment_variety_flag == 1){
            _this.error_content = "投资者的品种不能为空";
            _this.show_content = true;
            return false;
        }
        if(_this.ruleForm3.bad_record == "" && _this.bad_record_flag == 1) {
          _this.error_content = "机构不良诚信记录不能为空";
          _this.show_content = true;
          return false;
        }
        if(_this.ruleForm3.deal_type == "" && _this.deal_type_flag == 1) {
          _this.error_content = "交易类型不能为空";
          _this.show_content = true;
          return false;
        }
        return true;
      },
      extfield(value,flag){
        var isSelect = "";
        if(value!=undefined && value!=''){
          var arr = value.split("$$");
          if(arr[0]==""){
            return;
          }
          if(arr[1]!="" && arr[2]!=""){
            isSelect = "3";
          }else if(arr[1]!="" && arr[2]==""){
            isSelect = "2";
          }else if(arr[1]==""){
            isSelect = "2";
          }
          if(isSelect == "2"){//输入域
            if(flag == "1"){
              this.val1_flag = "true";
              this.val1_name = arr[0];
              this.ruleForm3.val1 = arr[1];
            }else if(flag == "2"){
              this.val2_flag = "true";
              this.val2_name = arr[0];
              this.ruleForm3.val2 = arr[1];
            }else if(flag == "3"){
              this.val3_flag = "true";
              this.val3_name = arr[0];
              this.ruleForm3.val3 = arr[1];
            }
          }else if(isSelect == "3"){//下拉
            var array = arr[2].split("/");
            var options = [];
            for(let index in array) {
              var map = {};
              map.name= array[index];
              map.value = array[index];
              options .push(map);
            };
            if(flag == "1"){
              this.val1_flag = 'options'
              this.val1_name = arr[0];
              this.ruleForm3.val1select = [arr[1]];
              this.val1options = [options];
              this.val1text = arr[2];
            }else if(flag == "2"){
              this.val2_flag = 'options'
              this.val2_name = arr[0];
              this.ruleForm3.val2select = [arr[1]];
              this.val2options = [options];
              this.val2text = arr[2];
            }else if(flag == "3"){
              this.val3_flag = 'options'
              this.val3_name = arr[0];
              this.ruleForm3.val3select = [arr[1]];
              this.val3options = [options];
              this.val3text = arr[2];
            }
          }
        }
      },
      VerifyCode() {
        var _this = this;
        this.$ajax.post('/index/verifyCode', {})
          .then(function (res) {
            _this.verify_code = res.data.verify_code;
            _this.src = "data:image/jpeg;base64," + res.data.verify_code_img;
          })
      }
    }
}
</script>
<style lang="less">
  .upIdCardInfo {
    overflow: hidden;
    .van-button {
      width: 84%;
      height: 45px;
      background-color: #49A5FF;
      margin: 23px auto;
      font-size: 15px;
      color: #ffffff;
      text-align: center;
      line-height: 44px;
    }
    // .vux-popup-picker-select{
    //   width: 100%;
    //   position: relative;
    //   margin-right: 1%;
    // }
    .van-cell__title{
      text-align: left;
      margin-left: 5px;
    }
    .van-field__control{
      text-align: right;
    }
    .vux-cell-value{
      color: #323233;
      //margin-right:5%;
    }
    /*.van-cell__value{
      margin-right:7.5%;
    }*/
    .monicker{
      font-size: 15px;
      line-height: 34px;
      text-align: left;
      text-indent: 5%;
    }
    .weui-cells_checkbox > label > *{
      pointer-events: none;
      font-size: 14px;
      line-height: 18px;
      p{
        text-align: left;
      }
    }
    .weui-cells_checkbox .weui-check:checked + .vux-checklist-icon-checked:before{
      color: #49A5FF;
      font-size: 18px;
    }
    .weui-cells_checkbox .weui-icon-checked:before{
      content: '\EA01';
      color: #C9C9C9;
      font-size: 18px;
      display: block;
    }
    .van-checkbox {
      text-align: left;
      margin-left: 6%;
      line-height: 31px;
    }
    .van-checkbox__icon--disabled.van-checkbox__icon--checked .van-icon{
      color: white;
      background: #49A5FF;
    }
    .van-checkbox__icon--round .van-icon{
      border-radius: 0px;
      width: 18px;
      height: 18px;
    }
    .van-checkbox__icon--checked .van-icon{
      color: #fff;
      border-color: #49A5FF;
      background-color: #49A5FF;
    }
    .van-icon-success:before{
      content: "\F0B1";
      line-height: 17px;
    }
    .van-checkbox__icon .van-icon{
      line-height: 18px;
    }
    .sign {
      color: red;
      float: left;
      position: absolute;
      left: 2%;
      line-height: 37px;
    }
    .weui-cell_access .weui-cell__ft:after{
      content: none;
      display: inline-block;
    }
    .nullDiv{
      float: left;
      margin-top: 2%;
      margin-left:-9px;
    }
    .datetimeLeft{
      float: left;
      padding-top: 11px;
      //font-size: 15px;
      //width: 30%;
    }
    .datetimeRight{
      float: left;
      padding-top: 11px;
      //font-size: 15px;
      //width: 18%;
    }
    @media screen and (min-width:320px) and (max-width:568px){
      .formDiv{
        margin-left: 3%;
      }
     }
    // @media screen and (min-width:375px) and (max-width:667px){
    //   .formDiv{
    //     margin-left: 15%;
    //   }
    // }
    .formDiv{
      width: 100%;
      height: auto;
      overflow: hidden;
    }
    .weui-cells:before{
      border: none;
    }
    .weui-cell:before{
      border: none;
    }
    .vux-no-group-title {
      margin-top: -0.23em;
      font-size: 14px;
    }


    .input-group {
      width: 100%;
      border-bottom: 1px solid #eeeeee;
      position: relative;
      background-color: #fff
    }
    .register_msg_btn {
      width: 33%;
      height: 69%;
      right: 3%;
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
    .authCode{
      text-align: center;
    }
    .weui-label{
      color: #323233;
    }
    .weui-input{
      margin-left: 4%;
    }
    .remark{
      margin-top: 4%;
    }
    .weui-cell__hd{
      /*margin-left: 2%*/
      text-align: left;
      margin-left: 5px;
    }
    @media only screen and (min-device-width : 768px) and (max-device-width : 1024px){
     .formDiv{
        margin-left: 37%;
      }
      .nullDiv{
        margin-top: 1%;
        margin-left: 2%;
        margin-right: 3%;
      }
    }
  .modifyMobile{
    .group-x-input {
      color: #333333;
      line-height: 47px;
      width: 91%;
      border: none;
      .van-field__control {
        text-align: left;
        margin-left: 5px;
      }
    }
    @media only screen and (min-device-width : 1024px) and (max-device-width : 1366px){
      .group-x-input{
        width: 96%;
      }
      .null button{
        padding-top: 3.5%;
      }
    }
    @media only screen and (min-device-width : 768px) and (max-device-width : 1024px){
      .group-x-input{
        width: 95%;
      }
      .null button{
        padding-top: 4.5%;
      }
    }
    .vilidate_code_img {
      width: 33%;
      height: 69%;
      right: 3%;
      top: 17%;
      position: absolute;
    }
    .van-field__control {
      text-align: right;
    }
    .weui-cell__hd{
      text-align: left;
      margin-left: 5px;
    }
    .van-cell{
      line-height: 38px;
    }
    .weui-cell{
      line-height: 38px;
    }
  }
  .weui-cell_access .weui-cell__ft {
    padding-right: 0px;
  }
  }

</style>
