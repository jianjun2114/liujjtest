<template>
  <div class="addInfo">
    <van-nav-bar
      title="基本信息资料"
      right-text="关闭"
      right-arrow
      left-arrow
      @click-left="onClickLeft"
      @click-right="onClickRight"/>
    <!--导航条-->
    <navigation/>
    <div class="title_title">&nbsp;&nbsp;&nbsp;<span class="title_name"></span>&nbsp;身份信息</div>
    <van-cell-group>
      <van-field v-model="ruleForm3.name" clearable maxlength="20" label="姓名" placeholder="请填写" required
                 :error-message="errorMessage.name" :disabled="nciis_flag=='1'"></van-field>
    </van-cell-group>
    <van-cell-group>
      <van-field v-model="ruleForm3.idNumber" clearable maxlength="18" label="身份证号" placeholder="请填写" ref="idNumber"
                 required :error-message="errorMessage.idNumber" :disabled="nciis_flag=='1'"></van-field>
    </van-cell-group>

    <van-cell-group>
      <van-field maxlength="100"
                 v-model="ruleForm3.idAddress" clearable label="身份证地址" placeholder="请填写" required
                 :error-message="errorMessage.idAddress"></van-field>
    </van-cell-group>
    <p style="height: 25px; background: white; text-align: left; padding-top: 2%; text-indent: 9px"><span style="color:red">*&nbsp;</span>身份证有效期</p>
    <group class="form-group">
    <div class="formDiv">
      <datetime class="dtLeftAddInfo" :min-year="1900" v-model="ruleForm3.startTime" @on-change="startTimeChange"
                @on-confirm="onConfirm" placeholder="请选择">
      </datetime>
      <div class="nullDiv">-</div>
      <datetime class="dtRightAddInfo" :max-year="2100" v-model="ruleForm3.endTime" @on-change="endTimeChange" :readonly="isLongTerm"
                @on-confirm="onConfirm" placeholder="请选择">
      </datetime>
    </div>
      <van-checkbox style="position: absolute;right: 8%; top: 29%;" v-model="longTerm" @change="longTermCheck()">长期</van-checkbox>
  </group>


    <div class="title_title">&nbsp;&nbsp;&nbsp;<span class="title_name"></span>&nbsp;联系地址</div>
    <van-cell-group>
      <span class="sign">*&nbsp;</span>
      <popup-picker title="&nbsp;联系地址省" :show-name="true" :data="proviceListSelect" v-model="ruleForm3.sheng"
                    placeholder="请选择" @on-change="provinceChange" required
                    :error-message="errorMessage.sheng"></popup-picker>
    </van-cell-group>
    <van-cell-group>
      <span class="sign">*&nbsp;</span>
      <popup-picker required title="&nbsp;联系地址市" :show-name="true" :data="cityListSelect" v-model="ruleForm3.shi"
                    placeholder="请选择" required :error-message="errorMessage.shi"></popup-picker>
    </van-cell-group>

    <!--省市
    <van-picker :columns="columns" @change="onChange" />-->
    <van-cell-group>
      <van-field maxlength="50"
                 v-model="ruleForm3.detailAddress" clearable label="联系地址" placeholder="请填写" required
                 :error-message="errorMessage.detailAddress"></van-field>
    </van-cell-group>
    <van-cell-group>
      <van-field v-model="ruleForm3.zipCode" clearable label="邮政编码" placeholder="请填写" maxlength="6"
                 required :error-message="errorMessage.zipCode" type="textarea" rows="1" autosize></van-field>
    </van-cell-group>

    <div class="title_title">&nbsp;&nbsp;&nbsp;<span class="title_name"></span>&nbsp;开户营业部</div>
    <van-cell-group>
      <span class="sign">*&nbsp;</span>
      <popup-picker
        title="&nbsp;请选择省"
        :show-name="true"
        :data="buss1ListSelect"
        v-model="ruleForm3.buss1"
        placeholder="请选择"
        @on-change="buss1Change"
        :disabled="channel"
      >
      </popup-picker>
    </van-cell-group>

    <van-cell-group>
      <span class="sign">*&nbsp;</span>
      <popup-picker
        title="&nbsp;请选择市"
        :show-name="true"
        :data="buss2ListSelect"
        v-model="ruleForm3.buss2"
        placeholder="请选择"
        @on-change="buss2Change"
        :disabled="channel"
      >
      </popup-picker>
    </van-cell-group>

    <van-cell-group>
      <span class="sign">*&nbsp;</span>
      <popup-picker
        title="&nbsp;营业部类别"
        :show-name="true"
        :data="buss3ListSelect"
        v-model="ruleForm3.buss3"
        placeholder="请选择"
        @on-change="buss3Change"
        :disabled="channel"
      >
      </popup-picker>
    </van-cell-group>

    <van-cell-group>
      <span class="sign">*&nbsp;</span>
      <popup-picker
        title="&nbsp;开户营业部"
        :show-name="true"
        :data="buss4ListSelect"
        v-model="ruleForm3.buss4"
        placeholder="请选择"
        :disabled="channel"
      >
      </popup-picker>
    </van-cell-group>
    <div class="title_title">&nbsp;&nbsp;&nbsp;<span class="title_name"></span>&nbsp;其他信息</div>
    <van-cell-group>
      <van-field v-model="ruleForm3.phoneNumber" clearable label="联系电话" placeholder="请填写" maxlength="13" required
                 :error-message="errorMessage.phoneNumber"></van-field>
    </van-cell-group>
    <van-cell-group>
      <van-field v-model="ruleForm3.contact_mobile" :readonly="true"  label="联系手机" placeholder="请填写" maxlength="11"
                 :error-message="errorMessage.contact_mobile"></van-field>
    </van-cell-group>
    <van-cell-group>
      <van-field v-model="ruleForm3.email" clearable label="电子邮箱" placeholder="请填写" maxlength="64"></van-field>
    </van-cell-group>

    <van-cell-group>
      <span class="sign">*&nbsp;</span>
      <popup-picker title="&nbsp;职业" :show-name="true" :data="professionListSelect"
                    v-model="ruleForm3.profession"
                    placeholder="请选择" required :error-message="errorMessage.profession"></popup-picker>
    </van-cell-group>
    <van-cell-group>
      <span class="sign">*&nbsp;</span>
      <popup-picker title="&nbsp;行业" :show-name="true" :data="industyListSelect" v-model="ruleForm3.industy"
                    placeholder="请选择"
                    v-if="industry_flag == '10000'"></popup-picker>
    </van-cell-group>
    <van-cell-group>
      <span class="sign">*&nbsp;</span>
      <popup-picker title="&nbsp;民族" :show-name="true" :data="minzuListSelect" v-model="ruleForm3.minzu"
                    placeholder="请选择"
                    v-if="minzu_visible_flag == '10000'"></popup-picker>
    </van-cell-group>
    <van-cell-group>
      <span class="sign">*&nbsp;</span>
      <popup-picker title="&nbsp;学历" :show-name="true" :data="educationalSelect" v-model="ruleForm3.educational"
                    placeholder="请选择" required :error-message="errorMessage.educational">
                    </popup-picker>
    </van-cell-group>
    <div class="addInfos" style="display:flex" v-if="enable_broker !== 'invisible'">
       <p style="font-size:14px;color:#000;"><span>*&nbsp;</span>是否存在居间人</p>
      <van-radio-group v-model="radio" @change="radioClick(radio)" :disabled="disable">
        <van-radio name="Y" icon-size="14px">是</van-radio>
        <van-radio name="N" icon-size="14px">否</van-radio>
      </van-radio-group>
    </div>
    <div class="radioFlag" v-show="radioFlag">
      <van-cell-group>
      <van-field v-model="intermediaryname" clearable label="居间人姓名" placeholder="请填写" maxlength="20"
                   type="textarea" rows="1" autosize  :disabled="disable"></van-field>
      </van-cell-group>
      <van-cell-group>
        <van-field v-model="intermediarynum" clearable label="居间人登记编号" placeholder="请填写" maxlength="8"
                   type="textarea" rows="1" autosize :disabled="disable"></van-field>
      </van-cell-group>
    </div>
    <!--<van-cell-group>
      <div v-model="other" @click="goChoicePage">
        <cell title="其他选项" is-link></cell>
      </div>
    </van-cell-group>-->
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
    
    
    <div size="large" class="van-button" @click="nextStep('0')">下一步</div>
    <popUp :tips="error_content" :showBox="show_content" @notarize="notarize"></popUp>
    <quit :content="content" :quit="quit"></quit>
    <waitPop :loadingbox="loadingbox"></waitPop>
    <confirm :showContents="showContents" :isShow="isShow" :yesButton="yesButton" :noButton="noButton" @ensure="ensure"
             @cancel="cancel"></confirm>
  </div>
</template>

<script>
  import NextStep from "../../components/nextStep.vue";
  import WaitPop from "../../components/waitPop.vue";
  import Confirm from "../../components/confirm.vue";
  import Utils from '../../util/iphoneOrAndroid';
  import Navigation from "../../components/navigation.vue";
  import {
    GroupTitle,
    Group,
    Cell,
    XInput,
    Selector,
    PopupPicker,
    Datetime,
    XNumber,
    XAddress,
    XTextarea,
    XSwitch,
    PopupRadio,
    Picker,
  } from 'vux';
  import {Dialog, } from 'vant';
  import PopUp from "../../components/popUp.vue";
  import Quit from "../../components/quit.vue";
  // const citys = {
  //   '浙江': ['杭州', '宁波', '温州', '嘉兴', '湖州'],
  //   '福建': ['福州', '厦门', '莆田', '三明', '泉州']
  // };
  export default {
    components: {
      Group,
      Dialog,
      GroupTitle,
      Cell,
      XInput,
      Selector,
      PopupPicker,
      XAddress,
      Datetime,
      XNumber,
      XTextarea,
      XSwitch,
      PopupRadio,
      NextStep,
      PopUp,
      Quit,
      WaitPop,
      Confirm,
      Picker,
      Navigation
    },
    data() {
      return {
        isLongTerm: false,
        longTerm:false,
        hideBottomClass: false,
        loadingbox: false,
        acceptNo: "",
        indexselection:false,   //保存用户信息返回-80000时，返回到业务选择页面
        radio: "",
        radioFlag:false,
        intermediaryname:"",
        intermediarynum:"",
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
          phoneNumber: "",
          contact_mobile: "",
          email: "",
          profession: [],
          industy: [],
          minzu: [],
          educational: [],
          buss1: [],
          buss2: [],
          buss3: [],
          buss4: [],
          tax_type: [],
          platform_type: [],
          term_investment: [],
          proceeds_type: [],
          con_relation: [],
          beneficiary: [],
          investment_variety: [],
          bad_record: [],
          id_kind: "",
          issued_depart: "",
          client_gender: "",
          val1: "",
          val2: "",
          val3: "",
          val1select: [],
          val2select: [],
          val3select: [],
        },
        proviceListSelect: [],
        cityListSelect: [],
        professionListSelect: [],
        industyListSelect: [],
        minzuListSelect: [],
        educationalSelect: [],
        buss1ListSelect: [],
        buss2ListSelect: [],
        buss3ListSelect: [],
        buss4ListSelect: [],
        city: "",
        cityTz: "",
        error_content: "",
        show_content: false,
        minzu_visible_flag: "",
        industry_flag: "",
        bad_record_flag: "",
        investment_variety_flag: "",
        beneficiary_flag: "",
        con_relation_flag: "",
        proceeds_type_flag: "",
        term_investment_flag: "",
        platform_type_flag: "",
        tax_type_flag: "",
        other: "",
        errorMessage: {name: ""},
        reg_phone: '',
        val1_name: "",
        val2_name: "",
        val3_name: "",
        val1_flag: "",
        val2_flag: "",
        val3_flag: "",

        val1options: [],
        val1text: '',
        val2options: [],
        val2text: '',
        val3options: [],
        val3text: '',

        channel: false,
        quit: false,
        content: "",
        nciis_flag: "0",//是否公安认证过
        yesButton: "是",
        noButton: "重新登录",
        isShow: false,
        showContents: "",
        buss1Flag: true,
        buss2Flag: true,
        buss3Flag: true,
        end:"",
        error_no:"",
        accept_status:"",
        disable: false,
        //居间人开关
        enable_broker:""
      }
    },
    created() {
     
      // 定义 this
      var _this = this;
      // 方法：开始 通用配置获取
      _this.$ajax.post('/userInfo/qryBaseInfoParam', {"broker_id": sessionStorage.getItem('broker_id')}, {}).then(function (res) {
        if (res.data.error_no == 0) {
          _this.minzu_visible_flag = res.data.minzu_visible_flag;
          _this.industry_flag = res.data.industry_flag;
          _this.bad_record_flag = res.data.bad_record_flag;
          _this.investment_variety_flag = res.data.investment_variety_flag;
          _this.beneficiary_flag = res.data.beneficiary_flag;
          _this.con_relation_flag = res.data.con_relation_flag;
          _this.proceeds_type_flag = res.data.proceeds_type_flag;
          _this.term_investment_flag = res.data.term_investment_flag;
          _this.platform_type_flag = res.data.platform_type_flag;
          _this.tax_type_flag = res.data.tax_type_flag;
          _this.reg_phone = res.data.phone_limit_Num;
          console.log("--------------------------------");
          console.log(_this.val1_name);
          _this.val1_name = res.data.val1;
          console.log(_this.val1_name);
          _this.val2_name = res.data.val2;
          _this.val3_name = res.data.val3;
          _this.val1text = res.data.val1_select;
          _this.val2text = res.data.val2_select;
          _this.val3text = res.data.val3_select;

          _this.val1options = [res.data.val1_list.map((item) => {
            return {
              name: item.name,
              value: item.name
            }
          })];
          _this.val2options = [res.data.val2_list.map((item) => {
            return {
              name: item.name,
              value: item.name
            }
          })];

          _this.val3options = [res.data.val3_list.map((item) => {
            return {
              name: item.name,
              value: item.name
            }
          })];

          _this.val1_flag = _this.extfield(_this.val1_name, _this.val1text);
          _this.val2_flag = _this.extfield(_this.val2_name, _this.val2text);
          _this.val3_flag = _this.extfield(_this.val3_name, _this.val3text);
          if (_this.val1text == undefined)
            _this.val1text = '';
          if (_this.val2text == undefined)
            _this.val2text = '';
          if (_this.val3text == undefined)
            _this.val3text = '';

          // 方法：开始 资料 获取客户信息
          _this.$ajax.post('/userInfo/showUserInfo', {"systype": sessionStorage.getItem('systype'),'business_type':sessionStorage.getItem('business_type')}).then(function (res) {
            if (res.data.error_no == 0) {
              // 初始化 下拉列表 开始
              // 联系地址省
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
              // 行业
              var industyList = res.data.industy_list;
              var a_industyList = [];
              var b_industyList = [];
              for (var i = 0; i < industyList.length; i++) {
                var obj_industyList = {};
                obj_industyList.name = industyList[i].name;
                obj_industyList.value = industyList[i].entryType;
                b_industyList.push(obj_industyList);
              }
              a_industyList.push(b_industyList);
              _this.industyListSelect = a_industyList;
              // 民族
              var minzuList = res.data.minzu_list;
              var a_minzuList = [];
              var b_minzuList = [];
              for (var i = 0; i < minzuList.length; i++) {
                var obj_minzuList = {};
                obj_minzuList.name = minzuList[i].name;
                obj_minzuList.value = minzuList[i].entryType;
                b_minzuList.push(obj_minzuList);
              }
              a_minzuList.push(b_minzuList);
              _this.minzuListSelect = a_minzuList;
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
              // 营业部 省
              var buss1List = res.data.branch_province_list;
              var a_buss1List = [];
              var b_buss1List = [];
              if (buss1List.length != "0") {
                for (var i = 0; i < buss1List.length; i++) {
                  var obj_buss1List = {};
                  obj_buss1List.name = buss1List[i].name;
                  obj_buss1List.value = buss1List[i].entryType;
                  b_buss1List.push(obj_buss1List);
                }
                a_buss1List.push(b_buss1List);
                _this.buss1ListSelect = a_buss1List;
              }
              // 营业部 市
              var buss2List = res.data.branch_city_list;
              var a_buss2List = [];
              var b_buss2List = [];
              if (buss2List.length != "0") {
                for (var i = 0; i < buss2List.length; i++) {
                  var obj_buss2List = {};
                  obj_buss2List.name = buss2List[i].name;
                  obj_buss2List.value = buss2List[i].entryType;
                  b_buss2List.push(obj_buss2List);
                }
                a_buss2List.push(b_buss2List);
                _this.buss2ListSelect = a_buss2List;
              }
              // 营业部 类型
              var buss3List = res.data.branch_type_list;
              var a_buss3List = [];
              var b_buss3List = [];
              if (buss3List.length != "0") {
                for (var i = 0; i < buss3List.length; i++) {
                  var obj_buss3List = {};
                  obj_buss3List.name = buss3List[i].name;
                  obj_buss3List.value = buss3List[i].entryType;
                  b_buss3List.push(obj_buss3List);
                }
                a_buss3List.push(b_buss3List);
                _this.buss3ListSelect = a_buss3List;
              }
              // 营业部
              var buss4List = res.data.branch_list;
              var a_buss4List = [];
              var b_buss4List = [];
              if (buss4List.length != "0") {
                for (var i = 0; i < buss4List.length; i++) {
                  var obj_buss4List = {};
                  obj_buss4List.name = buss4List[i].name;
                  obj_buss4List.value = buss4List[i].entryType;
                  b_buss4List.push(obj_buss4List);
                }
                a_buss4List.push(b_buss4List);
                _this.buss4ListSelect = a_buss4List;
              }
              // 初始化 下拉列表 结束
              //回显居间人
               if (_this.accept_status === "7" && res.data.is_broker !== 'Y') {
                      _this.disable = true;
                } else {
                 // _this.disable = false;
                }
              _this.radio = res.data.is_broker;
              _this.enable_broker = res.data.enable_broker;
              _this.intermediaryname = res.data.fbroker_name;
              _this.intermediarynum = res.data.broker_code;

              // 回显数据 开始
              _this.ruleForm3.name = res.data.client_name;
              _this.ruleForm3.idNumber = res.data.id_no;
              _this.ruleForm3.idAddress = res.data.id_address;
             // var time = res.data.id_begindate;
             // _this.ruleForm3.startTime = time.substring(0, 4) + '-' + time.substring(4, 6) + '-' + time.substring(6, 8);
             // var time2 = res.data.id_enddate;
             // _this.ruleForm3.endTime = time2.substring(0, 4) + '-' + time2.substring(4, 6) + '-' + time2.substring(6, 8);
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

              if (res.data.contact_number == '') {
                _this.ruleForm3.phoneNumber = res.data.mobile;
              } else {
                _this.ruleForm3.phoneNumber = res.data.contact_number;
              }
              _this.ruleForm3.contact_mobile = res.data.mobile;
              _this.ruleForm3.email = res.data.email;
              if (res.data.profession_code != "") {
                _this.ruleForm3.profession = [res.data.profession_code];
              }
              if (res.data.industy_code != "") {
                _this.ruleForm3.industy = [res.data.industy_code];
              }
              if (res.data.minzu_code != "") {
                _this.ruleForm3.minzu = [res.data.minzu_code];
              }
              if (res.data.degree_code != "") {
                _this.ruleForm3.educational = [res.data.degree_code];
              }
              _this.ruleForm3.issued_depart = res.data.issued_depart;
              _this.ruleForm3.client_gender = res.data.client_gender;
              if (res.data.branch_province != "") {
                _this.ruleForm3.buss1 = [res.data.branch_province + ""];
              }
              // console.log(res.data.branch_city+"============================");
              if (res.data.branch_city != "") {
                _this.ruleForm3.buss2 = [res.data.branch_city + ""];
              }
              if (res.data.branch_type != "") {
                _this.ruleForm3.buss3 = [res.data.branch_type + ""];
              }
              if (res.data.branch_no != "") {
                _this.ruleForm3.buss4 = [res.data.branch_no + ""];
                _this.buss1Flag = false;
                _this.buss2Flag = false;
                _this.buss3Flag = false;
              }
              if (res.data.tax_type != "") {
                _this.ruleForm3.tax_type = [res.data.tax_type];
              }
              if (res.data.platform_type != "") {
                _this.ruleForm3.platform_type = [res.data.platform_type];
              }
              if (res.data.term_investment != "") {
                _this.ruleForm3.term_investment = [res.data.term_investment];
              }
              if (res.data.proceeds_type != "") {
                _this.ruleForm3.proceeds_type = [res.data.proceeds_type];
              }
              if (res.data.con_relation != "") {
                _this.ruleForm3.con_relation = [res.data.con_relation];
              }
              if (res.data.beneficiary != "") {
                _this.ruleForm3.beneficiary = [res.data.beneficiary];
              }
              if (res.data.investment_variety != "") {
                var a_variety = res.data.investment_variety;
                var b_variety = a_variety.split(",").join("");
                _this.ruleForm3.investment_variety = b_variety.split('');
              }
              if (res.data.bad_record != "") {
                var a = res.data.bad_record;
                var b = a.split(",").join("");
                _this.ruleForm3.bad_record = b.split('');
              }

              var _val1 = res.data.val1;
              if (_val1 != undefined && _val1 != "") {
                console.log("++++++++++++++++++++++++++++++++++++++++++");
                _this.val1_name = _val1.split("$$")[0];
                console.log(_this.val1_name);
                if (_val1.split("$$").length == 2) {//输入域
                  _this.val1_flag = 'true';
                  _this.ruleForm3.val1 = _val1.split("$$")[1];
                } else if (_val1.split("$$").length == 3) { //下拉框
                  _this.val1text = _val1.split("$$")[2];
                  _this.val1_flag = _this.extfield(_this.val1_name, _this.val1text);
                  if (_this.val1_flag == 'true') {
                    _this.ruleForm3.val1 = _val1.split("$$")[1];
                  } else if (_this.val1_flag == 'options') {
                    _this.ruleForm3.val1select = [_val1.split("$$")[1]];
                    _this.val1options = [res.data.val1_list.map((item) => {
                      return {
                        name: item.name,
                        value: item.name
                      }
                    })];
                  }
                }
              }
              _val1 = res.data.val2;
              if (_val1 != undefined && _val1 != "") {
                _this.val2_name = _val1.split("$$")[0];
                if (_val1.split("$$").length == 2) {//输入域
                  _this.val2_flag = 'true';
                  _this.ruleForm3.val2 = _val1.split("$$")[1];
                } else if (_val1.split("$$").length == 3) { //下拉框
                  _this.val2text = _val1.split("$$")[2];
                  _this.val2_flag = _this.extfield(_this.val2_name, _this.val2text);
                  if (_this.val2_flag == 'true') {
                    _this.ruleForm3.val2 = _val1.split("$$")[1];
                  } else if (_this.val2_flag == 'options') {
                    _this.ruleForm3.val2select = [_val1.split("$$")[1]];
                    _this.val2options = [res.data.val2_list.map((item) => {
                      return {
                        name: item.name,
                        value: item.name
                      }
                    })];
                  }
                }
              }
              _val1 = res.data.val3;
              if (_val1 != undefined && _val1 != "") {
                _this.val3_name = _val1.split("$$")[0];
                if (_val1.split("$$").length == 2) {//输入域
                  _this.val3_flag = 'true';
                  _this.ruleForm3.val3 = _val1.split("$$")[1];
                } else if (_val1.split("$$").length == 3) { //下拉框
                  _this.val3text = _val1.split("$$")[2];
                  _this.val3_flag = _this.extfield(_this.val3_name, _this.val3text);
                  if (_this.val3_flag == 'true') {
                    _this.ruleForm3.val3 = _val1.split("$$")[1];
                  } else if (_this.val3_flag == 'options') {
                    _this.ruleForm3.val3select = [_val1.split("$$")[1]];
                    _this.val3options = [res.data.val3_list.map((item) => {
                      return {
                        name: item.name,
                        value: item.name
                      }
                    })];
                  }
                }
              }

              if (_this.val1text == undefined)
                _this.val1text = '';
              if (_this.val2text == undefined)
                _this.val2text = '';
              if (_this.val3text == undefined)
                _this.val3text = '';

              //是否已公安认证
              _this.nciis_flag = res.data.nciis_flag;
              //渠道标识
              if (res.data.channel == "1" && res.data.branch_no != "") {
                _this.channel = true;
              }
              // 回显数据 结束
              // 获取ocr数据 开始
              _this.axios.post('/userInfo/qryOcrInfo', {"systype": sessionStorage.getItem('systype')}).then(function (res) {
                if (res.data.error_no == 0) {
                  _this.ruleForm3.name = res.data.client_name;
                  _this.ruleForm3.idNumber = res.data.id_no;
                  _this.ruleForm3.idAddress = res.data.id_address;
                  _this.ruleForm3.startTime = res.data.id_begindate_string;
                  //_this.ruleForm3.endTime = res.data.id_enddate_string;
                  console.log("===================11")
                  if (res.data.id_enddate_string == '2099-12-31') {
                    _this.isLongTerm = true;
                    _this.longTerm = true;
                    _this.ruleForm3.endTime = '2099-12-31';
                  } else {
                    _this.isLongTerm = false;
                    _this.ruleForm3.endTime = _this.GMTtoStr(+res.data.id_enddate);
                  }
                  _this.end = res.data.id_enddate;
                  // 民族
                  _this.ruleForm3.minzu = [res.data.user_nation];
                  // 证件类型
                  _this.ruleForm3.id_kind = res.data.id_kind;
                  // 签发机关
                  _this.ruleForm3.issued_depart = res.data.issued_depart;
                  // 性别
                  _this.ruleForm3.client_gender = res.data.client_gender;
                  console.log(_this.ruleForm3.client_gender+"===================11")
                  // 跳转页面带回来的数据 开始
                  // _this.getParams();
                  // 跳转页面带回来的数据 结束
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
              // 获取ocr数据 结束
            } else if (res.data.error_no == '-9999') {
              _this.quit = true;
              _this.content = res.data.error_info;
            } else {
              _this.error_content = res.data.error_info;
              _this.show_content = true;
            }
          }).catch(function (error) {
            console.log(error);
          });
          // 方法：结束 资料 获取客户信息

        } else if (res.dta.error_no == "-9999") {
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

      // 方法：开始 流程 控制页面不能相互跳页面
      var data = {
        "business_type": sessionStorage.getItem('business_type')
      };
      _this.axios.post('/flow/qryAcceptInfo', data).then(function (res) {

        if (res.data.error_no == 0) {
          _this.acceptNo = res.data.accept_no;
           _this.accept_status = res.data.accept_status
          
          if (res.data.accept_status !== "6" || res.data.pre_audit_status == "2" || res.data.pre_audit_status == "3") {
               _this.disable = false;
              
          } else{
               _this.disable = true;
          }
          if (_this.$router.history.current.name != res.data.business_flow) {
            _this.$router.push({
              name: res.data.business_flow
            });
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
      // if(_this.accept_status !== "1") {
      //      
      // } else {
      //   this.disable = false;
      // }
      // 方法：结束 流程 控制页面不能相互跳页面
      
    },
    methods: {
      radioClick(radio) {
         console.log(radio,'radio')
         if (radio === 'Y') {
           this.radioFlag = true;
           this.radio = 'Y'
         } else if (radio === 'N') {
           this.radioFlag = false;
           this.radio = 'N'
         }
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
      extfield(label, options) {
        if (label == undefined || label == '') {
          return 'false';
        }
        if (options == undefined || options == '') {
          return 'true';
        }
        return 'options';
      },
      //级联省市
      ensure() {
        this.nextStep('keepon');
      },
      cancel() {
        this.isShow = false;
        if(this.error_no == "-32"){
          var browserVersion = Utils.iphOrAnz();
          if (browserVersion == "iphone") {
            window.location.href = "objc://callIOSOut_5";
          } else if (browserVersion == "anzuo") {
            jsHandle.logoutRelease();
          } else if (browserVersion == "windows") {
            console.log("windows");
          }
          return;
        }
        this.$router.push({name: "Indexselection"});
      },
      onEnter() {
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
      nextStep(iscontinue) {
        var _this = this;
        // 验证 提交 保存数据
        var startTime = _this.GMTtoStr1(_this.ruleForm3.startTime);
        var endTime = _this.GMTtoStr1(_this.ruleForm3.endTime);
        console.log(_this.ruleForm3.endTime);
        if(_this.ruleForm3.profession[0]< 500){
          _this.error_content = "请选择其他职业";
          _this.show_content = true;
          return;
        }
        if (_this.radio === "" && _this.enable_broker === 'visible') {
                _this.error_content = "请选择居间人";
                _this.show_content = true;
                return;
        }
        if (_this.radio === 'Y') {
            if(_this.intermediaryname === "") {
              _this.error_content = "请输入居间人姓名";
                _this.show_content = true;
                return;
            }
            var reg = /^([\u4e00-\u9fa5+\·?\u4e00-\u9fa5+]{2,20}$)/;
            if (reg.test(_this.intermediaryname)=== false) {
                _this.error_content = "居间人姓名格式不正确";
                _this.show_content = true;
                return;
            }
            var reg = /^(J|j)\d{7}$/;
            if (reg.test(_this.intermediarynum) == false) {
              _this.error_content ='居间人登记编号有误(格式:J+7位数字)';
              _this.show_content = true;
                return;
            }
            if(_this.intermediarynum === "") {
              _this.error_content = "请输入居间人登记编号有误(格式:J+7位数字)";
                _this.show_content = true;
                return;
            }
        }
        if (_this.enable_broker === 'invisible') {
            _this.radio = "";
        }
       
        
        //调校验方法
        if (!_this.vAddInfo()) {
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

        var idAddress = _this.idAddressFormat(_this.ruleForm3.idAddress);
        var detailAddress = _this.idAddressFormat( _this.ruleForm3.detailAddress);
        var saveData = {
          "systype": sessionStorage.getItem('systype'),
          "client_name": _this.ruleForm3.name,
          "id_no": _this.ruleForm3.idNumber,
          "id_address": idAddress,  //身份证地址
          "id_begindate": startTime,
          "id_enddate": endTime,
          "contact_addr_province": _this.ruleForm3.sheng[0],
          "contact_addr_city": _this.ruleForm3.shi[0],
          "contact_addr_detail": detailAddress,   //联系地址
          "post_code": _this.ruleForm3.zipCode,
          "contact_number": _this.ruleForm3.phoneNumber,
          "contact_mobile": _this.ruleForm3.contact_mobile,
          "email": _this.ruleForm3.email,
          "profession_code": _this.ruleForm3.profession[0],
          "industy_code": _this.ruleForm3.industy[0],
          "minzu_code": _this.ruleForm3.minzu[0],
          "degree_code": _this.ruleForm3.educational[0],
          "branch_no": _this.ruleForm3.buss4[0],
          "tax_type": _this.ruleForm3.tax_type[0],
          "platform_type": _this.ruleForm3.platform_type[0],
          "term_investment": _this.ruleForm3.term_investment[0],
          "proceeds_type": _this.ruleForm3.proceeds_type[0],
          "con_relation": _this.ruleForm3.con_relation[0],
          "beneficiary": _this.ruleForm3.beneficiary[0],
          "investment_variety": _this.ruleForm3.investment_variety.join(','),
          "bad_record": _this.ruleForm3.bad_record.join(','),
          "id_kind": '1',
          "issued_depart": _this.ruleForm3.issued_depart,
          "client_gender": _this.ruleForm3.client_gender,
          "val1":  _this.val1_name + "$$" + _val1_value,
          "val2":  _this.val2_name + "$$" + _val2_value,
          "val3":  _this.val3_name + "$$" + _val3_value,
          "isContinue": iscontinue,
          "business_type":sessionStorage.getItem('business_type'),
          "is_broker":_this.radio,
          "fbroker_name":_this.intermediaryname,
          "broker_code": _this.intermediarynum,
          "accept_status": _this.accept_status
        };
        _this.loadingbox = true;
        _this.$ajax.post('/userInfo/saveUserInfo', saveData).then(function (res) {
          _this.loadingbox = false;
          var error_no = res.data.error_no;
          if (error_no == 0) {
            if (_this.acceptNo != "" && _this.acceptNo != undefined) {
              // 方法：开始 流程 跳转页面
              var data = {
                "accept_no": _this.acceptNo,
                "business_type": sessionStorage.getItem('business_type'),
                "business_button_type": "1", // 下一步 按钮类型
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
          } else if (error_no == '-9999') {
            _this.quit = true;
            _this.content = res.data.error_info;
          } else if (error_no == '-33') {//身份证号重复
            _this.isShow = true,
            _this.showContents = res.data.error_info ;
            _this.error_no  = error_no ;
            _this.noButton = "重新登录";
          } else if(error_no == '-32'){
            _this.isShow = true;
            _this.noButton = "否";
            _this.error_no  = error_no ;
            _this.showContents = res.data.error_info ;
          }else if (error_no == '-6047' || error_no == '-6048') {//从业人员、市场禁入
            _this.content = res.data.error_info;
            _this.quit = true;
          } else if(error_no == '-80000'){
            _this.error_content = res.data.error_info;
            _this.show_content = true;
            _this.indexselection = true;
          }else {
            _this.error_content = res.data.error_info;
            _this.show_content = true;
          }
        }).catch(function (err) {
          console.log(err);
        });
      },
      getParams: function () {
        // 判断是否第一次进入页面
        if (this.$route.query.isCC == "0") {
          // 获取 路由 传递过来的数据
          var name = this.$route.query.name;
          var idNumber = this.$route.query.idNumber;
          var idAddress = this.$route.query.idAddress;
          var startTime = this.$route.query.startTime;
          var endTime = this.$route.query.endTime;
          var sheng = this.$route.query.sheng;
          var cityListSelect = this.$route.query.cityListSelect;
          var shi = this.$route.query.shi;
          var detailAddress = this.$route.query.detailAddress;
          var zipCode = this.$route.query.zipCode;
          var phoneNumber = this.$route.query.phoneNumber;
          var contact_mobile = this.$route.query.contact_mobile;
          var email = this.$route.query.email;
          var profession = this.$route.query.profession;
          var industy = this.$route.query.industy;
          var minzu = this.$route.query.minzu;
          var educational = this.$route.query.educational;
          var buss1ListSelect = this.$route.query.buss1ListSelect;
          var buss1 = this.$route.query.buss1;
          var buss2ListSelect = this.$route.query.buss2ListSelect;
          var buss2 = this.$route.query.buss2;
          var buss3ListSelect = this.$route.query.buss3ListSelect;
          var buss3 = this.$route.query.buss3;
          var buss4ListSelect = this.$route.query.buss4ListSelect;
          var buss4 = this.$route.query.buss4;
          var tax_type = this.$route.query.tax_type;
          var platform_type = this.$route.query.platform_type;
          var term_investment = this.$route.query.term_investment;
          var proceeds_type = this.$route.query.proceeds_type;
          var con_relation = this.$route.query.con_relation;
          var beneficiary = this.$route.query.beneficiary;
          var investment_variety = this.$route.query.investment_variety;
          var bad_record = this.$route.query.bad_record;
          var val1 = this.$route.query.val1;
          var val2 = this.$route.query.val2;
          var val3 = this.$route.query.val3;
          var channel = this.$route.query.channel;
          // 存储 路由 传递过来的数据
          this.ruleForm3.name = name;
          this.ruleForm3.idNumber = idNumber;
          this.ruleForm3.idAddress = idAddress;
          this.ruleForm3.startTime = startTime;
          this.ruleForm3.endTime = endTime;
          this.ruleForm3.sheng = sheng;
          this.cityListSelect = cityListSelect;
          this.ruleForm3.shi = shi;
          this.ruleForm3.detailAddress = detailAddress;
          this.ruleForm3.zipCode = zipCode;
          this.ruleForm3.phoneNumber = phoneNumber;
          this.ruleForm3.contact_mobile = contact_mobile;
          this.ruleForm3.email = email;
          this.ruleForm3.profession = profession;
          this.ruleForm3.industy = industy;
          this.ruleForm3.minzu = minzu;
          this.ruleForm3.educational = educational;
          this.buss1ListSelect = buss1ListSelect;
          this.ruleForm3.buss1 = buss1;
          this.buss2ListSelect = buss2ListSelect;
          this.ruleForm3.buss2 = buss2;
          this.buss3ListSelect = buss3ListSelect;
          this.ruleForm3.buss3 = buss3;
          this.buss4ListSelect = buss4ListSelect;
          this.ruleForm3.buss4 = buss4;
          this.ruleForm3.tax_type = tax_type;
          this.ruleForm3.platform_type = platform_type;
          this.ruleForm3.term_investment = term_investment;
          this.ruleForm3.proceeds_type = proceeds_type;
          this.ruleForm3.con_relation = con_relation;
          this.ruleForm3.beneficiary = beneficiary;
          this.ruleForm3.investment_variety = investment_variety;
          this.ruleForm3.bad_record = bad_record;
          this.cityTz = shi;
          this.ruleForm3.val1 = val1;
          this.ruleForm3.val2 = val2;
          this.ruleForm3.val3 = val3;
          this.channel = channel;
        }
      },
      startTimeChange() {
        var _this = this;
        if (new Date(_this.ruleForm3.startTime) > new Date(_this.ruleForm3.endTime)) {
          _this.error_content = "开始日期大于结束日期";
          _this.show_content = true;
        }
        /* if(new Date().getTime() - Date.parse(new Date(_this.ruleForm3.startTime)) <= -100000000){
           _this.error_content = "开始日期大于当前日期";
           _this.show_content = true;
         }*/
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
      onConfirm() {
      },
      provinceChange() {
        var _this = this;
        const getCity = {
          "province_code": _this.ruleForm3.sheng[0]
        };
        _this.$ajax.post('/userInfo/chooseLinkCity', getCity).then(function (res) {
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
      notarize() {
        this.show_content = false;
        if(this.indexselection){
          this.$router.push({name: "Indexselection"});
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
      // goChoicePage() {    其他选项
      //   var _this = this;
      //   if (_this.ruleForm3.name == "" || _this.ruleForm3.name == undefined) {
      //     _this.error_content = "请填写姓名";
      //     _this.show_content = true;
      //     return
      //   }
      //   if (_this.ruleForm3.idNumber == "" || _this.ruleForm3.idNumber == undefined) {
      //     _this.error_content = "请填身份证号";
      //     _this.show_content = true;
      //     return
      //   }
      //   if (_this.ruleForm3.idAddress == "" || _this.ruleForm3.idAddress == undefined) {
      //     _this.error_content = "请填写身份证地址";
      //     _this.show_content = true;
      //     return
      //   }
      //   if (_this.ruleForm3.startTime == "" || _this.ruleForm3.startTime == undefined) {
      //     _this.error_content = "请选择开始日期";
      //     _this.show_content = true;
      //     return
      //   }
      //   if (_this.ruleForm3.endTime == "" || _this.ruleForm3.endTime == undefined) {
      //     _this.error_content = "请选择结束日期";
      //     _this.show_content = true;
      //     return
      //   }
      //   if (_this.ruleForm3.sheng.length == 0 || _this.ruleForm3.sheng == undefined) {
      //     _this.error_content = "请选择联系地址省";
      //     _this.show_content = true;
      //     return
      //   }
      //   if (_this.ruleForm3.shi.length == 0 || _this.ruleForm3.shi == undefined) {
      //     _this.error_content = "请选择联系地址市";
      //     _this.show_content = true;
      //     return
      //   }
      //   _this.$router.push({
      //     path: '/choicePage',
      //     query: {
      //       isCC: "0",
      //       name: _this.ruleForm3.name,
      //       idNumber: _this.ruleForm3.idNumber,
      //       idAddress: _this.ruleForm3.idAddress,
      //       startTime: _this.ruleForm3.startTime,
      //       endTime: _this.ruleForm3.endTime,
      //       sheng: _this.ruleForm3.sheng,
      //       cityListSelect: _this.cityListSelect,
      //       shi: _this.ruleForm3.shi,
      //       detailAddress: _this.ruleForm3.detailAddress,
      //       zipCode: _this.ruleForm3.zipCode,
      //       phoneNumber: _this.ruleForm3.phoneNumber,
      //       contact_mobile: _this.ruleForm3.contact_mobile,
      //       email: _this.ruleForm3.email,
      //       profession: _this.ruleForm3.profession,
      //       industy: _this.ruleForm3.industy,
      //       minzu: _this.ruleForm3.minzu,
      //       educational: _this.ruleForm3.educational,
      //       buss1ListSelect: _this.buss1ListSelect,
      //       buss1: _this.ruleForm3.buss1,
      //       buss2ListSelect: _this.buss2ListSelect,
      //       buss2: _this.ruleForm3.buss2,
      //       buss3ListSelect: _this.buss3ListSelect,
      //       buss3: _this.ruleForm3.buss3,
      //       buss4ListSelect: _this.buss4ListSelect,
      //       buss4: _this.ruleForm3.buss4,
      //       tax_type: _this.ruleForm3.tax_type,
      //       platform_type: _this.ruleForm3.platform_type,
      //       term_investment: _this.ruleForm3.term_investment,
      //       proceeds_type: _this.ruleForm3.proceeds_type,
      //       con_relation: _this.ruleForm3.con_relation,
      //       beneficiary: _this.ruleForm3.beneficiary,
      //       investment_variety: _this.ruleForm3.investment_variety,
      //       bad_record: _this.ruleForm3.bad_record,
      //       bad_record_flag: _this.bad_record_flag,
      //       investment_variety_flag: _this.investment_variety_flag,
      //       beneficiary_flag: _this.beneficiary_flag,
      //       con_relation_flag: _this.con_relation_flag,
      //       proceeds_type_flag: _this.proceeds_type_flag,
      //       term_investment_flag: _this.term_investment_flag,
      //       platform_type_flag: _this.platform_type_flag,
      //       tax_type_flag: _this.tax_type_flag,
      //       val1:_this.val1,
      //       val2:_this.val2,
      //       val3:_this.val3,
      //       val1_name:_this.val1_name,
      //       val2_name:_this.val2_name,
      //       val3_name:_this.val3_name,
      //       channel:_this.channel
      //     },
      //   });
      // },
      vAddInfo() {
        var _this = this;
        //姓名
        console.log(_this.ruleForm3.idAddress);
        var v_info = _this.v_custname(_this.ruleForm3.name);
        if (!_this.vvalidate(v_info)) {
          return false;
        }
        v_info = _this.v_idno(_this.ruleForm3.idNumber);
        if (!_this.vvalidate(v_info)) {
          return false;
        }
        //身份证号
        v_info = _this.v_idno(_this.ruleForm3.idNumber);
        if (!_this.vvalidate(v_info)) {
          return false;
        }
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
        //联系电话
        v_info = _this.v_telPhone(_this.ruleForm3.phoneNumber, _this.reg_phone, 'telPhone');
        if (!_this.vvalidate(v_info)) {
          return false;
        }
        //联系手机
        v_info = _this.v_telPhone(_this.ruleForm3.contact_mobile, _this.reg_phone, 'phone');
        if (!_this.vvalidate(v_info)) {
          return false;
        }
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

        //开户营业部
        v_info = _this.v_buss3(_this.ruleForm3.buss3);
        if (!_this.vvalidate(v_info)) {
          return false;
        }
        //开户营业厅
        v_info = _this.v_buss4(_this.ruleForm3.buss4);
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
        return true;
      },
      vvalidate(v_info) {
        var _this = this;
        if (v_info != 'truetrue') {
          //_this.errorMessage.name = v_info;
          _this.error_content = v_info;
          _this.show_content = true;
          return false;
        }
        return true;
      },
      //选择省市
      buss1Change() {
        var _this = this;
        var ruleForm3Buss1 = "";
        if (_this.ruleForm3.buss1.length != 0) {
          ruleForm3Buss1 = _this.ruleForm3.buss1[0];
        }
        if (_this.buss1Flag) {
          const getCity = {
            "systype": sessionStorage.getItem('systype'),
            "branch_province": ruleForm3Buss1
          };
          this.$ajax.post('/userInfo/chooseBranchProvince', getCity).then(function (res) {
            if (res.data.error_no == 0) {
              var buss2List = res.data.branch_city_list;
              var a_buss2List = [];
              var b_buss2List = [];
              for (var i = 0; i < buss2List.length; i++) {
                var obj_buss2List = {};
                obj_buss2List.name = buss2List[i].name;
                obj_buss2List.value = buss2List[i].entryType;
                b_buss2List.push(obj_buss2List);
              }
              a_buss2List.push(b_buss2List);
              _this.buss2ListSelect = a_buss2List;
              var buss3ListSelect = [];
              _this.buss3ListSelect = buss3ListSelect;
              var buss4ListSelect = [];
              _this.buss4ListSelect = buss4ListSelect;

              var buss2 = [];
              _this.ruleForm3.buss2 = buss2;
              var buss3 = [];
              _this.ruleForm3.buss3 = [];
              var buss4 = [];
              _this.ruleForm3.buss4 = [];

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
        } else {
          _this.buss1Flag = true;
        }
      },
      buss2Change() {
        var _this = this;
        var ruleForm3Buss1 = "";
        var ruleForm3Buss2 = "";
        if (_this.ruleForm3.buss1.length == 0 || _this.ruleForm3.buss2.length == 0) {

        } else {
          if (_this.ruleForm3.buss1.length != 0) {
            ruleForm3Buss1 = _this.ruleForm3.buss1[0];
          }
          if (_this.ruleForm3.buss2.length != 0) {
            ruleForm3Buss2 = _this.ruleForm3.buss2[0];
          }
          if (_this.buss2Flag) {
            const getshi = {
              "systype": sessionStorage.getItem('systype'),
              "branch_province": ruleForm3Buss1,
              "branch_city": ruleForm3Buss2
            };
            this.$ajax.post('/userInfo/chooseBranchCity', getshi).then(function (res) {
              if (res.data.error_no == 0) {
                var buss3List = res.data.branch_type_list;
                var a_buss3List = [];
                var b_buss3List = [];
                for (var i = 0; i < buss3List.length; i++) {
                  var obj_buss3List = {};
                  obj_buss3List.name = buss3List[i].name;
                  obj_buss3List.value = buss3List[i].entryType;
                  b_buss3List.push(obj_buss3List);
                }
                a_buss3List.push(b_buss3List);
                _this.buss3ListSelect = a_buss3List;

                var buss3 = [];
                _this.ruleForm3.buss3 = [];
                var buss4 = [];
                _this.ruleForm3.buss4 = [];

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
          } else {
            _this.buss2Flag = true;
          }
        }
      },
      buss3Change() {
        var _this = this;
        var ruleForm3Buss1 = "";
        var ruleForm3Buss2 = "";
        var ruleForm3Buss3 = "";
        if (_this.ruleForm3.buss1.length == 0 || _this.ruleForm3.buss2.length == 0 || _this.ruleForm3.buss3.length == 0) {
        } else {
          if (_this.ruleForm3.buss1.length != 0) {
            ruleForm3Buss1 = _this.ruleForm3.buss1[0];
          }
          if (_this.ruleForm3.buss2.length != 0) {
            ruleForm3Buss2 = _this.ruleForm3.buss2[0];
          }
          if (_this.ruleForm3.buss3.length != 0) {
            ruleForm3Buss3 = _this.ruleForm3.buss3[0];
          }
          if (_this.buss3Flag) {
            const getbuss3 = {
              "systype": sessionStorage.getItem('systype'),
              "branch_province": ruleForm3Buss1,
              "branch_city": ruleForm3Buss2,
              "branch_type": ruleForm3Buss3
            };
            this.$ajax.post('/userInfo/chooseBranchType', getbuss3).then(function (res) {
              if (res.data.error_no == 0) {
                var buss4List = res.data.branch_list;
                var a_buss4List = [];
                var b_buss4List = [];
                for (var i = 0; i < buss4List.length; i++) {
                  var obj_buss4List = {};
                  obj_buss4List.name = buss4List[i].name;
                  obj_buss4List.value = buss4List[i].entryType;
                  b_buss4List.push(obj_buss4List);
                }
                a_buss4List.push(b_buss4List);
                _this.buss4ListSelect = a_buss4List;

                var buss4 = [];
                _this.ruleForm3.buss4 = [];
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
          } else {
            _this.buss3Flag = true;
          }
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
    },
  }
</script>

<style lang="less">
.addInfos{
  display: flex;
  justify-content: space-between;
  height: 30px;
  line-height: 30px;
  background: #fff;
  padding: 0 9px;
  p{
    span{
      color: red;
    }
  }
}
  .addInfo {
    //body {font-size: 20px !important;}
    // height: 100vh;
    overflow:hidden;
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
    .weui-cell {
      padding: 10px 15px;
      position: relative;
      display: -webkit-box;
      display: -ms-flexbox;
      display: flex;
      -webkit-box-align: center;
      -ms-flex-align: center;
      align-items: center;
      line-height: 25px;
    }
    .vux-label {
      display: inline-block;
      word-wrap: break-word;
      word-break: break-all;
      float: left;
    }
    .weui-input {
      text-align: right;
      float: right;
    }
    .form-group label:after {
      content: ' *';
      color: #999;
      font-size: 150%;
    }
    .vux-no-group-title{
        margin-top: -0.23em;
        font-size: 14px;
    }
    .color {
      color: red;
    }
    .sign {
      color: red;
      float: left;
      position: absolute;
      left: 2%;
      top: 30%;
      display: flex;
    }
    .van-radio-group{
      display: flex;
      // margin-left: 150px;
      // margin-top: 5px;
      height:30px;
      // position: absolute;
      right: 15px;
    }
    .van-radio__label{
      line-height: 43px !important;
    }
    .van-radio .van-icon{
      font-size: 15px !important;
      margin-top: 0 !important;
    }
    .van-radio__label{
      margin-left: 5px !important;
      margin-right: 5px !important;
    }
    @media only screen and (min-device-width: 1366px) and (orientation: landscape) {
      .sign{
        left: 0.5%;
      }
    }
    .nullDiv{
        float: left;
        margin-top: 4.5%;
        margin-left:-9px;
      }
     @media only screen and (min-device-width: 768px) and (max-device-width: 1024px) and (orientation: portrait) {
       .sign{
         left: 1.2%;
       }
       .nullDiv{
         margin-top: 1.2%;
       }
     }
    // @media only screen and (min-device-width: 768px) and (max-device-width: 1024px) and (orientation: portrait) {
    //    .sign{
    //      left: 1%;
    //    }
    // }
    .scroller-item {
      font-size: 12px;
    }
    .vux-cell-box:not(:first-child):before {
      content: " ";
      position: absolute;
      top: 0;
      width: 100%;
      height: 1px;
      border-top: none;
      color: #D9D9D9;
      -webkit-transform-origin: 0 0;
      transform-origin: 0 0;
      -webkit-transform: scaleY(0.5);
      transform: scaleY(0.5);
      left: 15px;
    }
    .title_title {
      font-size: 10pt;
      text-align: left;
      font-weight: bold;
      margin-bottom: 5px;
      margin-top: 5px;
    }
    .title_name {
      border-left: 5px #02a7ff solid;
    }
    .vux-flexbox-item {
      height: 240px;
    }
    .vux-picker-item {
      height: 8px;
    }
    .weui-cell_access .weui-cell__ft:after{
      content: none;
      display: inline-block;
      height: 6px;
      width: 6px;
      border-width: 2px 2px 0 0;
      border-color: #C8C8CD;
      border-style: solid;
      -webkit-transform: matrix(0.71, 0.71, -0.71, 0.71, 0, 0);
      transform: matrix(0.71, 0.71, -0.71, 0.71, 0, 0);
      position: relative;
      top: -2px;
      position: absolute;
      top: 50%;
      margin-top: -4px;
      right: 2px;
    }
    .van-icon-success:before{
      content: "\F0B1";
      line-height: 17px;
    }
    .van-checkbox__icon--round .van-icon{
      border-radius: 0px;
      width: 18px;
      height: 18px;
    }
    .van-checkbox__label{
      color: #323233;
      margin-left: 7px;
      font-size: 14px;
    }
    h6{
      color: #333333;
      float: right;
    }
    .dtLeftAddInfo{
      float: left;
      padding-top: 11px;
    }
    .dtRightAddInfo{
      float: left;
      padding-top: 11px;
    }
    .weui-cells:before{
      border-top: none;
    }
    .weui-cell:before{
       border-top: none;
    }
     @media screen and (min-width:320px) and (max-width:568px){
       .formDiv{
         margin-left: 5px;
       }
    }
     @media screen and (min-width:375px) and (max-width:667px){
       .formDiv{
         margin-left: 15px;
       }
    }
    .formDiv{
      width: 90%;
      height: auto;
      overflow: hidden;
      font-size: 15px;
    }
  }
</style>
