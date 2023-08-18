<template>
  <div class="information">
    <van-nav-bar
      title="基本信息资料"
      right-text="关闭"
      right-arrow
      :left-arrow="leftArrow"
      @click-left="onClickLeft('0')"
      @click-right="onClickRight"/>
    <!--导航条-->
    <navigation/>
    <div class="title_title">&nbsp;&nbsp;&nbsp;<span class="title_name"></span>&nbsp;身份信息<span v-if="showLaunderingBack" class="warning">(修改身份证有效期)</span><img v-if="showLaunderingBack" class="img" :src="artboard.img" @click="editCustInfo('16')"/></div>
    <van-cell-group>
      <van-field
        v-model="ruleForm3.name"
        clearable
        label="姓名"
        placeholder="未填写"
        :readonly="disabled"
      />
    </van-cell-group>
    <van-cell-group>
      <van-field
        v-model="ruleForm3.idNumber"
        clearable
        label="身份证号"
        placeholder="未填写"
        :readonly="disabled"
      />
    </van-cell-group>
    <van-cell-group>
      <van-field
        v-model="ruleForm3.idAddress"
        clearable
        label="身份证地址"
        placeholder="未填写"
        :readonly="disabled"/>
    </van-cell-group>
    <p style="height: 31px; background: white; text-align: left; padding-top: 2%; text-indent: 9px">&nbsp;&nbsp;身份证有效期</p>
    <group class="form-group">
      <datetime
        :readonly="disabled"
        class="datetimeLeft"
        :min-year="1900"
        v-model="ruleForm3.startTime"
        @on-confirm="onConfirm"
        placeholder="请选择">
      </datetime>
      <div class="nullDiv">-</div>
      <datetime
        :readonly="disabled"
        class="datetimeRight"
        :max-year="2100"
        v-model="ruleForm3.endingTime"
        @on-confirm="onConfirm"
        placeholder="请选择">
      </datetime>
    </group>
    <div class="title_title">&nbsp;&nbsp;&nbsp;<span class="title_name"></span>&nbsp;联系电话 <span v-if="showLaunderingBack" class="warning">(修改联系电话)</span><img v-if="showLaunderingBack" class="img" :src="artboard.img" @click="editCustInfo('17')"/></div>
    <van-cell-group>
      <van-field
        v-model="ruleForm3.phoneNumber"
        clearable
        label="联系电话"
        placeholder="未填写"
        :readonly="disabled"
      />
    </van-cell-group>
    <van-cell-group>
      <van-field
        v-model="ruleForm3.contact_mobile"
        clearable
        label="联系手机"
        placeholder="未填写"
        :readonly="disabled"
      />
    </van-cell-group>
    <div v-if="!showLaunderingBack">
      <div class="title_title">&nbsp;&nbsp;&nbsp;<span class="title_name"></span>&nbsp;联系地址</div>
      <van-cell-group>
        <span class="sign">*</span>
        <popup-picker
          title="联系地址省"
          :show-name="true"
          :data="proviceListSelect"
          v-model="ruleForm3.sheng"
          placeholder="请选择"
          @on-change="provinceChange"
          :disabled="isDisabled"
        />
      </van-cell-group>
      <van-cell-group>
        <span class="sign">*</span>
        <popup-picker
          title="联系地址市"
          :show-name="true"
          :data="cityListSelect"
          v-model="ruleForm3.shi"
          placeholder="请选择"
          :disabled="isDisabled"
        />
      </van-cell-group>
      <van-cell-group>
        <van-field
          v-model="ruleForm3.detailAddress"
          clearable label="联系地址"
          placeholder="请填写"
          :readonly="isDisabled"
          maxlength="50"
          required
        />
      </van-cell-group>
      <van-cell-group>
        <van-field
          v-model="ruleForm3.zipCode"
          clearable label="邮政编码"
          placeholder="请填写"
          maxlength="6"
          :readonly="isDisabled"
          required
        />
      </van-cell-group>
    </div>
    <div class="title_title">&nbsp;&nbsp;&nbsp;<span class="title_name"></span>&nbsp;其他信息 <span v-if="showLaunderingBack" class="warning">(修改联系地址、邮编、学历等信息)</span><img v-if="showLaunderingBack" class="img" :src="artboard.img" @click="editCustInfo('18')"/></div>
    <div v-if="showLaunderingBack">
      <van-cell-group>
        <van-field
          v-model="sheng"
          clearable
          label="联系地址省"
          placeholder="未填写"
          :readonly="isDisabled"/>
      </van-cell-group>
      <van-cell-group>
        <van-field
          v-model="shi"
          clearable
          label="联系地址市"
          placeholder="未填写"
          :readonly="isDisabled"
        />
      </van-cell-group>
      <van-cell-group>
        <van-field
          v-model="detailAddress"
          clearable label="联系地址"
          placeholder="请填写"
          :readonly="isDisabled"
          maxlength="50"
        />
      </van-cell-group>
      <van-cell-group>
        <van-field
          v-model="ruleForm3.zipCode"
          clearable label="邮政编码"
          placeholder="请填写"
          maxlength="6"
          :readonly="isDisabled"/>
      </van-cell-group>
    </div>
    <van-cell-group>
      <van-field
        v-model="ruleForm3.email"
        clearable
        label="电子邮箱"
        placeholder="请填写"
        maxlength="64"
        :readonly="isDisabled"/>
    </van-cell-group>
    <div  v-if="!showLaunderingBack">
      <van-cell-group>
        <span class="sign">*&nbsp;</span>
        <popup-picker
          title="学历"
          :show-name="true"
          :data="educationalSelect"
          v-model="ruleForm3.educational"
          placeholder="请选择"
          :disabled="isDisabled"
        />
      </van-cell-group>
      <van-cell-group>
        <span class="sign">*&nbsp;</span>
        <popup-picker
          title="职业"
          :show-name="true"
          :data="professionListSelect"
          v-model="ruleForm3.profession"
          placeholder="请选择"
          :disabled="isDisabled"
        />
      </van-cell-group>
      <!--扩展字段-->
      <div>
        <van-cell-group>
          <van-field
            v-model="ruleForm3.val1"
            clearable
            :label="val1_name"
            placeholder="请填写"
            v-if="val1_flag == 'true'"
            maxlength="25"
            :readonly="isDisabled"
          />
        </van-cell-group>
        <van-cell-group v-if="val1_flag == 'options'">
          <popup-picker :title="val1_name" :data="val1options" :show-name="true" v-model="ruleForm3.val1select"
                        placeholder="请选择" :disabled="isDisabled"></popup-picker>
        </van-cell-group>

        <van-cell-group>
          <van-field
            v-model="ruleForm3.val2"
            clearable
            :label="val2_name"
            placeholder="请填写"
            v-if="val2_flag == 'true'"
            maxlength="25"
            :readonly="isDisabled"
          />
        </van-cell-group>
        <van-cell-group v-if="val2_flag == 'options'">
          <popup-picker :title="val2_name" :data="val2options" v-model="ruleForm3.val2select"
                        placeholder="请选择" :disabled="isDisabled"></popup-picker>
        </van-cell-group>

        <van-cell-group>
          <van-field
            v-model="ruleForm3.val3"
            clearable
            :label="val3_name"
            placeholder="请填写"
            v-if="val3_flag == 'true'"
            maxlength="25"
            :readonly="isDisabled"
          />
        </van-cell-group>
        <van-cell-group v-if="val3_flag == 'options'">
          <popup-picker :title="val3_name" :data="val3options" v-model="ruleForm3.val3select"
                        placeholder="请选择" :disabled="isDisabled"></popup-picker>
        </van-cell-group>
      </div>
    </div>
    <!--反洗钱-->
    <div v-if="showLaunderingBack">
      <van-cell-group>
        <van-field
          v-model="educational"
          clearable
          label="学历"
          placeholder="请填写"
          :readonly="isDisabled"/>
      </van-cell-group>
      <van-cell-group>
        <van-field
          v-model="profession"
          clearable label="职业"
          placeholder="请填写"
          :readonly="isDisabled"/>
      </van-cell-group>
      <!--扩展字段-->
      <div>
        <van-cell-group>
          <van-field
            v-model="ruleForm3.val1"
            clearable
            :label="val1_name"
            placeholder="请填写"
            v-if="val1_flag == 'true'"
            maxlength="25"
            :readonly="isDisabled"
          />
        </van-cell-group>
        <van-cell-group v-if="val1_flag == 'options'">
          <popup-picker :title="val1_name" :data="val1options" :show-name="true" v-model="ruleForm3.val1select"
                        placeholder="请选择" :disabled="isDisabled"></popup-picker>
        </van-cell-group>

        <van-cell-group>
          <van-field
            v-model="ruleForm3.val2"
            clearable
            :label="val2_name"
            placeholder="请填写"
            v-if="val2_flag == 'true'"
            maxlength="25"
            :readonly="isDisabled"
          />
        </van-cell-group>
        <van-cell-group v-if="val2_flag == 'options'">
          <popup-picker :title="val2_name" :data="val2options" v-model="ruleForm3.val2select"
                        placeholder="请选择" :disabled="isDisabled"></popup-picker>
        </van-cell-group>

        <van-cell-group>
          <van-field
            v-model="ruleForm3.val3"
            clearable
            :label="val3_name"
            placeholder="请填写"
            v-if="val3_flag == 'true'"
            maxlength="25"
            :readonly="isDisabled"
          />
        </van-cell-group>
        <van-cell-group v-if="val3_flag == 'options'">
          <popup-picker :title="val3_name" :data="val3options" v-model="ruleForm3.val3select"
                        placeholder="请选择" :disabled="isDisabled"></popup-picker>
        </van-cell-group>
      </div>
      <p v-if="tax_type_flag == 1" class="monicker">您的税收居民身份为：</p>
      <checklist
        :options="commonList1"
        v-model="ruleForm3.tax_type"
        :max="1"
        v-if="tax_type_flag == 1"
        :disabled="disabled">
      </checklist>
      <p v-if="platform_type_flag == '1'" class="monicker">您的政要人物关系为：</p>
      <checklist
        :options="commonList2"
        v-model="ruleForm3.platform_type"
        :max="1"
        v-if="platform_type_flag == '1'"
        :disabled="disabled">
      </checklist>

      <p v-if="term_investment_flag == '1'" class="monicker">您的投资期限为：</p>
      <checklist
        :options="commonList3"
        v-model="ruleForm3.term_investment"
        :max="1"
        v-if="term_investment_flag == '1'"
        :disabled="disabled">
      </checklist>

      <p v-if="proceeds_type_flag == '1'" class="monicker">您的期望收益为：</p>
      <checklist
        :options="commonList4"
        v-model="ruleForm3.proceeds_type"
        :max="1"
        v-if="proceeds_type_flag == '1'"
        :disabled="disabled">
      </checklist>

      <p v-if="con_relation_flag == '1'" class="monicker">您的账户是否存在被他人控制或存在控制他人账户的情况：</p>
      <checklist
        :options="commonList5"
        v-model="ruleForm3.con_relation"
        :max="1"
        v-if="con_relation_flag == '1'"
        :disabled="disabled">
      </checklist>

      <p v-if="beneficiary_flag == '1'" class="monicker">您的账户交易实际受益人为：</p>
      <checklist
        :options="commonList6"
        v-model="ruleForm3.beneficiary"
        :max="1"
        v-if="beneficiary_flag == '1'"
        :disabled="disabled">
      </checklist>

      <p v-if="investment_variety_flag == '1'" class="monicker">您想投资的品种为：</p>
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

      <p v-if="bad_record_flag == '1'" class="monicker">您是否有来源以下机构不良诚信记录：</p>
      <checklist
        :max="1"
        :options="commonList8"
        v-model="ruleForm3.bad_record"
        v-if="bad_record_flag == '1'"
        :disabled="disabled">
      </checklist>
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
    <van-button size="large" @click="nextStep" class="van-button" v-if="!showLaunderingBack">下一步</van-button>
    <popUp :tips="error_content" :showBox="show_content" @notarize="notarize"></popUp>
    <quit :content="content" :quit="quit"></quit>
    <waitPop :loadingbox="loadingbox"></waitPop>
    <confirm :showContents="showContents" :isShow="isShow" :yesButton="yesButton" @ensure="ensure"></confirm>
  </div>
</template>

<script>
  import { GroupTitle, Group, Cell, XInput, Selector, PopupPicker, Datetime, XNumber, ChinaAddressV2Data , XAddress, XTextarea, XSwitch, PopupRadio,Checklist} from 'vux';
  import { Toast,Dialog } from 'vant';
  import PopUp from "../../components/popUp.vue";
  import Quit from "../../components/quit.vue";
  import WaitPop from "../../components/waitPop.vue";
  import Confirm from "../../components/confirm.vue";
  import Navigation from "../../components/navigation.vue";
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
        PopUp,
        Checklist,
        Quit,
        WaitPop,
        Confirm,
        Navigation
    },
    data() {
      return {
        leftArrow: true,
        longTerm:"",
        // isLongTerm: false,
        showContents:"",
        isShow: false,
        yesButton:"确定",
        error_content: "",
        //show_content: false,
        content:"",
        quit:false,
        loadingbox: false,
        accept_no:'',
        educationalSelect:[],//学历
        professionListSelect:[],//职业
        proviceListSelect:[],//省
        cityListSelect:[],//市
        ruleForm3: {
          name: '',//姓名
          id_kind: '',
          idNumber: '',
          idAddress: '',
          endingTime: '',
          startTime: '',
          sheng: [],
          shi: [],
          detailAddress: '',
          educational: [],
          profession: [],
          phoneNumber: '',
          email: '',
          zipCode: '',
          tax_type: [],
          platform_type: [],
          term_investment: [],
          proceeds_type: [],
          con_relation:[] ,
          beneficiary: [],
          investment_variety: [],
          bad_record: [],
          deal_type:[],
          val1: "",
          val2: "",
          val3: "",
          val1select: [],
          val2select: [],
          val3select: [],
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
        minzu_visible_flag:"",
        industry_flag:"",
        bad_record_flag:"",
        investment_variety_flag:"",
        beneficiary_flag:"",
        con_relation_flag:"",
        proceeds_type_flag:"",
        term_investment_flag:"",
        platform_type_flag:"",
        tax_type_flag:"",
        deal_type_flag:"",
        error_content:"",
        show_content:false,
        broker_hotline:"",
        city:"",
        cityTz:"",
        disabled:true,
        isDisabled:false,
        flag:false,//适当性非空校验标识
        business_flag:"",//0证件有效期为空   1：其他信息校验为空
        isOut:false,
        business_type:"",
        showLaunderingBack:false,
        artboard: { img: require("../../assets/artboard.png") },
        sheng:"",
        shi: "",
        detailAddress:"",
        educational:"",
        profession: "",
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
      var _this = this;
      _this.business_type = sessionStorage.getItem('business_type');
      /* if(sessionStorage.getItem('business_type') == '13' || sessionStorage.getItem('business_type') == '14'){
         _this.disabled = true;
       }*/
      if(_this.business_type == '15'){
        _this.showLaunderingBack = true;
        _this.isDisabled = true;
        _this.leftArrow = false;
      }else if(_this.business_type == '13'){
        _this.isDisabled = true;
      }
      _this.broker_hotline = sessionStorage.getItem('broker_hotline');
      // 方法：开始 通用配置获取
      _this.$ajax.post('/userInfo/qryBaseInfoParam', {"broker_id":sessionStorage.getItem('broker_id')},{}).then(function(res){
        if(res.data.error_no == 0){
       /*   _this.minzu_visible_flag = res.data.minzu_visible_flag;
          _this.industry_flag = res.data.industry_flag;*/
          _this.bad_record_flag = res.data.bad_record_flag;
          _this.investment_variety_flag = res.data.investment_variety_flag;
          _this.beneficiary_flag = res.data.beneficiary_flag;
          _this.con_relation_flag = res.data.con_relation_flag;
          _this.proceeds_type_flag = res.data.proceeds_type_flag;
          _this.term_investment_flag = res.data.term_investment_flag;
          _this.platform_type_flag = res.data.platform_type_flag;
          _this.tax_type_flag = res.data.tax_type_flag;
          _this.deal_type_flag = res.data.deal_type_flag;
        }else{
          _this.error_content = res.data.error_info;
          _this.show_content = true;
          _this.isOut = false;
        }
      }).catch(function (err) {
        console.log(err);
      });
      // 方法：结束 通用配置获取


      var data = {
        "business_type": sessionStorage.getItem('business_type'),
        "register_way": '2',
      };
      this.axios.post('/flow/qryAcceptInfo', data).then(function (res) {
        _this.accept_no = res.data.accept_no;
        //_this.business_flow = res.data.business_flow;
        if (_this.$router.history.current.name != res.data.business_flow) {
          _this.$router.push('/error')
        }
        var thdData = {
          "systype": sessionStorage.getItem('systype'),
          "business_type": sessionStorage.getItem('business_type'),
          "accept_no": _this.accept_no
        }
        _this.axios.post('/userInfo/qryThdClientInfo', thdData).then(function (res) {   //同步三方信息
          if (res.data.error_no == '0') {
            if (_this.business_type =='15' ) {//我的基本资料
              //身份信息
              _this.ruleForm3.name = res.data.client_name;
              _this.ruleForm3.idNumber = res.data.id_no;//身份证号
              _this.ruleForm3.idAddress = res.data.id_addr;//身份证地址
              var time = res.data.id_begindate;
              var time2 = res.data.id_enddate;
              _this.ruleForm3.startTime = time.substring(0,4)+'-'+time.substring(4,6)+'-'+time.substring(6,8);
              _this.ruleForm3.endingTime = time2.substring(0,4)+'-'+time2.substring(4,6)+'-'+time2.substring(6,8);

              _this.ruleForm3.phoneNumber = res.data.telphone;//联系电话
              _this.ruleForm3.contact_mobile = res.data.contact_moblie;//联系手机

              //其他信息
              _this.sheng = res.data.addr_province_name;
              _this.shi = res.data.addr_city_name;
              _this.detailAddress = res.data.addr_address;
              _this.ruleForm3.zipCode = res.data.addr_zipcode;
              _this.ruleForm3.email = res.data.email;
              _this.educational = res.data.degree_name;//education;
              _this.profession = res.data.profession_name;//occupation;
              //_this.industy = res.data.industy_code;
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
              if(res.data.deal_type !== ""){
                _this.ruleForm3.deal_type = res.data.deal_type.split(",")
              }

              var val1 =  res.data.val1;
              var val2 =  res.data.val2;
              var val3 =  res.data.val3;
              _this.extfield(val1,"1");
              _this.extfield(val2,"2");
              _this.extfield(val3,"3");
            }else{//其他业务
              const getBankData = {
                "systype": sessionStorage.getItem('systype')
              };
              _this.$ajax.post('/userInfo/showUserInfo', getBankData).then(function (res) {
                if(res.data.error_no == 0){
                  // 初始化 下拉列表 开始
                  // 联系地址省
                  var proviceList = res.data.provice_list;
                  var a_proviceList = [];
                  var b_proviceList = [];
                  for(var i= 0;i<proviceList.length;i++){
                    var obj_proviceList = {};
                    obj_proviceList.name = proviceList[i].name;
                    obj_proviceList.value = proviceList[i].entryType;
                    b_proviceList.push(obj_proviceList);
                  }
                  a_proviceList.push(b_proviceList);
                  _this.proviceListSelect = a_proviceList;

                  var degreeList = res.data.degree_list;
                  var a_degreeList = [];
                  var b_degreeList = [];
                  for(var i= 0;i<degreeList.length;i++){
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
                  for(var i= 0;i<professionList.length;i++){
                    if (professionList[i].entryType >= 500 || professionList[i].entryType == res.data.profession_code) {
                      var obj_professionList = {};
                      obj_professionList.name = professionList[i].name;
                      obj_professionList.value = professionList[i].entryType;
                      b_professionList.push(obj_professionList);
                    }
                  }
                  a_professionList.push(b_professionList);
                  _this.professionListSelect = a_professionList;

                  // 回显数据 开始
                  _this.ruleForm3.name = res.data.client_name;
                  var str1 = res.data.client_name;
                  _this.ruleForm3.idNumber = res.data.id_no;
                  _this.ruleForm3.idAddress = res.data.id_address;
                  var time = res.data.id_begindate;
                  var time2 = res.data.id_enddate;
                  if(time == "" || time2 == "" || str1 ==""){
                    _this.isShow = true;
                    _this.business_flag = "0";
                    _this.showContents = "您的身份信息获取失败，请联系期货公司办理相关业务，联系电话：" + _this.broker_hotline;
                  }
                  _this.ruleForm3.startTime = time.substring(0,4)+'-'+time.substring(4,6)+'-'+time.substring(6,8);
                  _this.ruleForm3.endingTime = time2.substring(0,4)+'-'+time2.substring(4,6)+'-'+time2.substring(6,8);
                  var sheng = [];
                  if(res.data.contact_addr_province != ""){
                    sheng[0] = res.data.contact_addr_province;
                  }
                  _this.ruleForm3.sheng = sheng;
                  var shi = [];
                  if(res.data.contact_addr_city != ""){
                    shi[0] = res.data.contact_addr_city;
                    _this.city = res.data.contact_addr_city;
                  }
                  _this.ruleForm3.shi = shi;
                  _this.ruleForm3.detailAddress = res.data.contact_addr_detail;
                  _this.ruleForm3.zipCode = res.data.post_code;
                  if(res.data.contact_number == ''){    //联系电话
                    _this.ruleForm3.phoneNumber = res.data.mobile;
                  }else{
                    _this.ruleForm3.phoneNumber = res.data.contact_number;
                  }
                  _this.ruleForm3.contact_mobile = res.data.contact_moblie;  //联系手机
                  _this.ruleForm3.email = res.data.email;
                  if(res.data.profession_code != ""){
                    _this.ruleForm3.profession = [res.data.profession_code];
                  }
                  if(res.data.degree_code != ""){
                    _this.ruleForm3.educational = [res.data.degree_code];
                  }
                  if(res.data.tax_type != ""){
                    _this.ruleForm3.tax_type=[res.data.tax_type];
                  }
                  if(res.data.platform_type != ""){
                    _this.ruleForm3.platform_type=[res.data.platform_type];
                  }
                  if(res.data.term_investment != ""){
                    _this.ruleForm3.term_investment=[res.data.term_investment];
                  }
                  if(res.data.proceeds_type != ""){
                    _this.ruleForm3.proceeds_type=[res.data.proceeds_type];
                  }
                  if(res.data.con_relation != ""){
                    _this.ruleForm3.con_relation=[res.data.con_relation];
                  }
                  if(res.data.beneficiary != ""){
                    _this.ruleForm3.beneficiary=[res.data.beneficiary];
                  }
                  if(res.data.investment_variety != ""){
                    var a_variety = res.data.investment_variety;
                    var b_variety = a_variety.split(",").join("");
                    _this.ruleForm3.investment_variety = b_variety.split('');
                  }
                  if(res.data.bad_record != ""){
                    var a = res.data.bad_record;
                    var b = a.split(",").join("");
                    _this.ruleForm3.bad_record = b.split('');
                  }
                  if(res.data.deal_type !== ""){
                    _this.ruleForm3.deal_type = res.data.deal_type.split(",")
                  }
                  //扩展字段
                  var val1 =  res.data.val1;
                  var val2 =  res.data.val2;
                  var val3 =  res.data.val3;
                  _this.extfield(val1,"1");
                  _this.extfield(val2,"2");
                  _this.extfield(val3,"3");
                  // 回显数据 结束
                }else if(res.data.error_no == '-9999'){
                  _this.quit = true;
                  _this.content = res.data.error_info;
                }else{
                  _this.error_content = res.data.error_info;
                  _this.show_content = true;
                  _this.isOut = false;
                }
              }).catch(function (error) {
                console.log(err)
              });
            }
          }else if(res.data.error_no == '-9999'){
            _this.quit = true;
            _this.content = res.data.error_info;
          }else {
            _this.isOut = false;
            _this.quit = true;
            _this.content = res.data.error_info;
          }
        }).catch(function (err) {
          console.log(err)
        });
      }).catch(function (err) {
        console.log(err)
      });
    },
    methods: {
      GMTtoStr(time) {
        var now = new Date(time);
        var year = now.getFullYear();
        var mon = now.getMonth() + 1;
        var date = now.getDate();
        if (mon < 10) {
          mon = '0' + mon;
        };
        if (date < 10) {
          date = '0' + date;
        }
        var postDate = year + '-' + mon + '-' + date;
        return postDate;
      },
      //  longTermCheck() {  //长期勾选
      //   if (this.longTerm == true) {
      //     this.ruleForm3.endingTime = '2099-12-31';
      //     this.isLongTerm = true;
      //   } else {
      //     this.ruleForm3.endingTime = this.GMTtoStr(new Date()) ;
      //     this.isLongTerm = false;
      //   }
      // },
      ensure(){
        var _this = this;
        _this.isShow = false;
          var data = {
            "accept_no": _this.accept_no,
            "business_type": sessionStorage.getItem('business_type')
          }
          _this.axios.post('/myBussiness/abandonBusiness',data).then(function (res) {
            if(res.data.error_no == 0){
              _this.axios.post('/myBussiness/getBusiness', {
                'business_type': sessionStorage.getItem('business_type'),
                'systype': sessionStorage.getItem('systype'),
                "supplement_type":sessionStorage.getItem('systype')
        }).then(function (res) {
                if(res.data.error_no == 0){
                  if(_this.business_flag == "0"){
                    sessionStorage.setItem('business_type',"16");
                  }else if(_this.business_flag == "1"){
                    sessionStorage.setItem('business_type',"18");
                  }
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
       notarize() {
        var _this = this;
        _this.show_content = false;
        if(_this.isOut){
          if(_this.business_flag == '0'){
            sessionStorage.setItem('business_type',"16");
          }else if(_this.business_flag == '1'){
            sessionStorage.setItem('business_type',"18");
          }
          _this.$router.push({name: "Register"});
        }
       },
      onClickLeft(type){
        var _this = this;
        if (_this.accept_no!= "" && _this.accept_no != undefined) {
          // 方法：开始 流程 跳转页面
          var data = {
            "accept_no": _this.accept_no,
            "business_type": sessionStorage.getItem('business_type'),
            "business_button_type": type,
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
              _this.isOut = false;
            }
          }).catch(function (err) {
            console.log(err);
          });
          // 方法：结束 流程 跳转页面
        }
      },
      nextStep(){
        var  _this = this;

        if(sessionStorage.getItem('business_type')=='13'){
          _this.flag = true;
        }else {
          if(_this.ruleForm3.profession[0] < 500){
            _this.error_content = "请选择其他职业";
            _this.show_content = true;
            return;
          }
        }
        if (!_this.vAddInfo()) {
          return;
        }
        if(sessionStorage.getItem('business_type')=='14' || sessionStorage.getItem('business_type')=='22' || sessionStorage.getItem('business_type')=='24'){
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
          var submitData={
            "business_type":sessionStorage.getItem('business_type'),
            "systype":sessionStorage.getItem('systype'),
            "accept_no":_this.accept_no,
            "contact_mobile": _this.ruleForm3.contact_mobile,
            "ruleForm3.phoneNumber": _this.ruleForm3.phoneNumber,
            "contact_addr_province":_this.ruleForm3.sheng[0],
            "contact_addr_city":_this.ruleForm3.shi[0],
            "contact_addr_detail":_this.ruleForm3.detailAddress,
            "degree_code":_this.ruleForm3.educational[0],
            "profession_code":_this.ruleForm3.profession[0],
            "post_code":_this.ruleForm3.zipCode,
            "email":_this.ruleForm3.email,
            "tax_type":_this.ruleForm3.tax_type[0],
            "platform_type":_this.ruleForm3.platform_type[0],
            "term_investment":_this.ruleForm3.term_investment[0],
            "proceeds_type":_this.ruleForm3.proceeds_type[0],
            "con_relation":_this.ruleForm3.con_relation[0],
            "beneficiary":_this.ruleForm3.beneficiary[0],
            "investment_variety":_this.ruleForm3.investment_variety.join(','),
            "bad_record":_this.ruleForm3.bad_record.join(','),
            "deal_type":_this.ruleForm3.deal_type.toString(),
            "val1": _this.val1_name + "$$" + _val1_value,
            "val2": _this.val2_name + "$$" + _val2_value,
            "val3": _this.val3_name + "$$" + _val3_value,
          };
          _this.$ajax.post('/userInfo/modifyUserInfo',submitData).then(function (res) {
            if(res.data.error_no == 0){
              _this.onClickLeft('1');
            }else if(res.data.error_no == '-33'){
              Dialog.alert({
                message:res.data.error_info
              }).then(function () {
                _this.$router.push('/index')
              });
            }else{
              _this.error_content = res.data.error_info;
              _this.show_content = true;
              _this.isOut = false;
            }
          }).catch(function (err) {
            console.log(err);
          })
        }else if(sessionStorage.getItem('business_type')=='13'){
          var dataCard = {
            "id_no": _this.ruleForm3.idNumber,
          };
          _this.$ajax.post('/userInfo/validateUserInfo', dataCard).then(function (res) {
            if (res.data.error_no == 0) {
              var data = {
                "accept_no": _this.accept_no,
                "business_type": sessionStorage.getItem('business_type'),
                "accept_status": "",//不更改时不传值
                "business_button_type": '1',
                "business_flow": _this.$router.history.current.name
              };
              _this.$ajax.post('/flow/updateAccept', data).then(function (res) {  //更改流程步骤
                if (res.data.error_no == '0') {
                  _this.$router.push({
                    name: res.data.business_flow,
                   /* params: {
                      accept_status: res.data.accept_status,//
                      accept_no: _this.accept_no,
                    }*/
                  });
                } else if (res.data.error_no == '-9999') {
                  _this.quit = true;
                  _this.content = res.data.error_info;
                }
              }).catch(function (err) {
                console.log(err);
              });

            } else if (res.data.error_no == '-9999') {
              _this.quit = true;
              _this.content = res.data.error_info;
            } else {
              if (res.data.error_no == '-33') {
                Dialog.alert({
                  message:res.data.error_info
                }).then(function () {
                  _this.$router.push('/index')
                });
              } else {
                _this.error_content = res.data.error_info;
                _this.show_content = true;
                _this.isOut = false;
              }
            }
          }).catch(function (err) {
            console.log(err);
          });
        }
      },
      onConfirm(){

      },
      provinceChange(){
        var _this = this;
        const getCity = {
          "province_code":  _this.ruleForm3.sheng[0]
        };
        _this.$ajax.post('/userInfo/chooseLinkCity', getCity).then(function (res) {
          if(res.data.error_no == 0){
            var cityList1 = res.data.city_list;
            var a_cityList1 = [];
            var b_cityList1 = [];
            for(var i= 0;i<cityList1.length;i++){
              var obj_cityList1 = {};
              obj_cityList1.name = cityList1[i].name;
              obj_cityList1.value = cityList1[i].entryType;
              b_cityList1.push(obj_cityList1);
            }
            a_cityList1.push(b_cityList1);
            _this.cityListSelect = a_cityList1;
           if(_this.city == _this.ruleForm3.shi ){
              _this.city = "";
            }else{
              var city_ = _this.ruleForm3.shi+"";
              var cityTz_ = _this.cityTz+"";
              if(city_ == cityTz_){
                _this.cityTz = "";
              }else{
                var shi = [];
                _this.ruleForm3.shi = shi;
              }
            }
          }else if(res.data.error_no == '-9999'){
              _this.quit = true;
              _this.content = res.data.error_info;
          }else{
            _this.error_content = res.data.error_info;
            _this.show_content = true;
            _this.isOut = false;
          }
        }).catch(function (err) {
          console.log(err);
        });
      },
      onClickRight(){
        this.$router.push({name:'MyBussiness'});
      },
      vAddInfo() {
        var _this = this;
        //联系地址
        var v_info = _this.v_custAddressIsBlank(_this.ruleForm3.sheng, _this.ruleForm3.shi, _this.ruleForm3.detailAddress,_this.flag);
        if (!_this.vvalidate(v_info)) {
          return false;
        }
        //邮编
        v_info = _this.v_zipCode(_this.ruleForm3.zipCode);
        if (!_this.vvalidate(v_info)) {
          return false;
        }
        //电子邮箱
        v_info = _this.v_email(_this.ruleForm3.email);
        if (!_this.vvalidate(v_info)) {
          return false;
        }
        //学历
        v_info = _this.v_educational(_this.ruleForm3.educational);
        if (!_this.vvalidate(v_info)) {
          return false;
        }
        //职业
        v_info = _this.v_profession(_this.ruleForm3.profession);
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
          if(_this.flag){
            _this.showContents = "您的基本信息不完整，请先完成信息修改后再进行增开交易编码业务！";
            _this.isShow = true;
            _this.business_flag = "1";
            return false;
          }
          _this.error_content = v_info;
          _this.show_content = true;
          _this.isOut = false;
          return false;
        }
        return true;
      },
      editCustInfo(pb_type) {
        var _this = this;
        _this.$ajax.post('/myBussiness/getBusiness', {
          'business_type': pb_type,
          'systype': sessionStorage.getItem('systype'),
          "supplement_type":sessionStorage.getItem('systype')
        }).then(function (res) {
          if (res.data.error_no == 0) {
            sessionStorage.setItem('business_type', pb_type);
            _this.$router.push({name: res.data.business_flow});
          } else if (res.data.error_no == '-9999') {
            _this.quit = true;
            _this.content = res.data.error_info;
          }else{
            _this.error_content = res.data.error_info;
            _this.show_content = true;
            _this.isOut = false;
          }
        }).catch(function (error) {
          console.log(error);
        });
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
      }
    }
  }
</script>

<style lang="less">
  .information {
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
      margin-bottom: 6%;
    }
    .weui-cell{
      padding: 8px 19px;
      position: relative;
      display: -webkit-box;
      display: -ms-flexbox;
      display: flex;
      -webkit-box-align: center;
      -ms-flex-align: center;
      align-items: center;
      line-height: 25px;
      background-color: #fff
    }
    .vux-label{
      display: inline-block;
      word-wrap: break-word;
      word-break: break-all;
      float: left;
    }
    .weui-cells_checkbox > label > *{
      pointer-events: none;
      font-size: 14px;
      line-height: 28px;
      p{
        text-align: left;
      }
    }
    .weui-cells__title{
      margin-top: 0.77em;
      margin-bottom: 0.3em;
      padding-left: 15px;
      padding-right: 15px;
      color: #999999;
      font-size: 15px;
      text-align: left;
      color: #333333;
    }
    .weui-cells_checkbox .weui-check:checked + .vux-checklist-icon-checked:before{
      color: #49A5FF;
      font-size: 20px;
    }
    .weui-cells_checkbox .weui-icon-checked:before{
      content: '\EA01';
      color: #C9C9C9;
      font-size: 20px;
      display: block;
    }
    .weui-cell:before{
      border-top: none;
      border-bottom: none;
    }
    .title_name {
      border-left: 5px #02a7ff solid;
    }
    .title_title {
      font-size: 13pt;
      text-align: left;
      font-weight: bold;
      margin-bottom: 10px;
      margin-top: 10px;
    }
    .datetimeLeft{
      float: left;
      padding-top: 11px;
      margin-left: 18%;
    }
    .datetimeRight{
      float: left;
      padding-top: 11px;
    }
     @media screen and (min-width:320px) and (max-width:568px){
       .datetimeLeft{
         margin-left: 18%;
       }
    }
     @media screen and (min-width:375px) and (max-width:667px){
       .datetimeLeft{
         margin-left: 35%;
       }
    }
    .nullDiv{
        float: left;
        margin-top: 3.4%;
        margin-left: 0;
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
    .van-checkbox__icon--round .van-icon{
      border-radius: 0px;
      width: 18px;
      height: 18px;
    }
    .van-checkbox__label{
      color: #323233;
      margin-left: 7px;
    }
    .weui-cells:before{
      border: none;
    }
    .sign {
      color: red;
      position: absolute;
      left:1.6%;
      z-index:9;
      top: 18%;
    }
    .monicker{
      font-size: 15px;
      line-height: 34px;
      text-align: left;
      text-indent: 2%;
    }
    .van-checkbox {
      text-align: left;
      margin-left: 6%;
      line-height: 31px;
    }
    .van-checkbox__icon--disabled.van-checkbox__icon--checked .van-icon{
      color: white;
      background: #A4D2FF;
    }
    .img{
      width: 27px;
      height: 27px;
      float: right;
      margin-right: 4%
    }
    .warning{
      margin-left: 1%;
      font-size: 10pt;
      color: #aaaaaa;
    }
    // .weui-cell__hd{
    //   margin-left: -5.5%;
    // }
    .weui-cells__title + .weui-cells{
      margin-top: 0;
      margin-bottom: 1%;
    }
    .vux-cell-value{
      //font-size: 15px;
    }
    .weui-cell_access{
      margin-right: -3%;
    }
    .van-checkbox__icon--disabled .van-icon {
      background-color: white;
    }
     .vux-popup-picker-select{
        width: 100%;
      }
     //ipad竖屏
    @media only screen and (min-device-width : 768px) and (max-device-width : 1024px){
      .nullDiv{
        margin-top: 2%;
      }
      .sign{
        left: 1%;
      }
       .vux-popup-picker-select{
        width: 99%;
      }
      .weui-cell_access{
        margin-right: -1%;
      }
      .van-checkbox{
         margin-left: 3%;
       }
    }
     //ipad横屏
    @media only screen and (min-width: 1349px) and (orientation : landscape){
      .nullDiv{
        margin-top: 1%;
      }
    }
    @media only screen and (min-width: 1024px) and (max-device-width : 1366px){
      .nullDiv{
        margin-top: 1.4%;
      }
      // .weui-cell_access{
      //   margin-right: 0;
      // }
      // .vux-cell-value{
      //   margin-right: 2%;
      // }
       .van-checkbox{
         margin-left: 3%;
       }
    }
   /* .van-checkbox__icon, .van-checkbox__label{
      line-height: 18px
    }*/
  }
</style>
