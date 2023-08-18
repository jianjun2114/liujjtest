<template>
  <div class="choicePage">
    <div class="header">
      <van-nav-bar
        title="基本信息资料"
        right-text="关闭"
        right-arrow
        left-arrow
        @click-right="onClickRight"
        @click-left="onClickLeft"
      ></van-nav-bar>
    </div>
    <div class="title">
      <span class="step1">1.资料报审</span>
      <van-icon name="arrow"/>
      <span class="step2">2.签署协议</span>
      <van-icon name="arrow"/>
      <span class="step3">3.在线回访</span>
    </div>
    <div>
      <van-cell-group>
      <span class="sign">*&nbsp;</span>
      <popup-picker
        title="&nbsp;请选择省"
        :show-name="true"
        :data="buss1ListSelect"
        v-model="ruleForm3.buss1"
        placeholder="请选择"
        @on-change="buss1Change"
        :disabled = "channel"
      >
      </popup-picker>
      </span>
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
        :disabled = "channel"
      >
      </popup-picker>
      </span>
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
          :disabled = "channel"
        >
        </popup-picker>
        </span>
      </van-cell-group>

      <van-cell-group>
      <span class="sign">*&nbsp;</span>
        <popup-picker
          title="&nbsp;开户营业部"
          :show-name="true"
          :data="buss4ListSelect"
          v-model="ruleForm3.buss4"
          placeholder="请选择"
          :disabled = "channel"
        >
        </popup-picker>
        </span>
      </van-cell-group>

      <van-cell-group>
          <van-field
            v-model="val1"
            clearable
            :label="val1_name"
            placeholder="请填写"
            v-if="val1_name != ''"
          />
      </van-cell-group>

      <van-cell-group>
        <van-field
          v-model="val2"
          clearable
          :label="val2_name"
          placeholder="请填写"
          v-if="val2_name != ''"
        />
      </van-cell-group>

      <van-cell-group>
        <van-field
          v-model="val3"
          clearable
          :label="val3_name"
          placeholder="请填写"
          v-if="val3_name != ''"
       />
      </van-cell-group>
    </div>
    <p v-if="tax_type_flag == 1">您的税收居民身份为：</p>
      <checklist
        :options="commonList1"
        v-model="ruleForm3.tax_type"
        :max="1"
        v-if="tax_type_flag == 1"
        @on-change="changeTaxType">
      </checklist>

    <p v-if="platform_type_flag == 1">您的政要人物关系为：</p>
    <checklist
      :options="commonList2"
      v-model="ruleForm3.platform_type"
      :max="1"
      v-if="platform_type_flag == 1"
      @on-change="changePlatformType">
    </checklist>

    <p v-if="term_investment_flag == 1">您的投资期限为：</p>
    <checklist
      :options="commonList3"
      v-model="ruleForm3.term_investment"
      :max="1"
      v-if="term_investment_flag == 1"
      @on-change="change">
    </checklist>

    <p v-if="proceeds_type_flag == 1"您的期望收益为：</p>
    <checklist
      :options="commonList4"
      v-model="ruleForm3.proceeds_type"
      :max="1"
      v-if="proceeds_type_flag == 1"
      @on-change="change">
    </checklist>

    <p v-if="con_relation_flag == 1">您的账户是否存在实际控制关系：</p>
    <checklist
      :options="commonList5"
      v-model="ruleForm3.con_relation"
      :max="1"
      v-if="con_relation_flag == 1"
      @on-change="changeConRelation">
    </checklist>

    <p v-if="beneficiary_flag == 1">您的账户交易实际受益人为：</p>
    <checklist
      :options="commonList6"
      v-model="ruleForm3.beneficiary"
      :max="1"
      v-if="beneficiary_flag == 1"
      @on-change="changeBeneficiary">
    </checklist>

    <p v-if="investment_variety_flag == 1">您想投资者的品种为：</p>
    <checklist
      :max="3"
      :options="commonList7"
      v-model="ruleForm3.investment_variety"
      v-if="investment_variety_flag == 1"
      @on-change="change">
    </checklist>

    <p v-if="bad_record_flag == 1">您是否有来源以下机构不良诚信记录：</p>
    <checklist
      :max="9"
      :options="commonList8"
      v-model="ruleForm3.bad_record"
      v-if="bad_record_flag == 1"
      @on-change="changeBadRecord">
    </checklist>
    <div size="large" class="van-button" @click="nextStep()">完成</div>
    <popUp :tips="error_content" :showBox="show_content" @notarize="notarize"></popUp>
  </div>
</template>

<script>
  import {  Group, PopupRadio, Checklist, PopupPicker} from 'vux';
  import { Toast } from 'vant';
  import PopUp from "../../components/popUp.vue";
  export default {
    components: {
      Group,
      PopupRadio,
      Checklist,
      PopupPicker,
      PopUp
    },
    // watch: {
    //   // 监测路由变化,只要变化了就调用获取路由参数方法将数据存储本组件即可
    //   '$route': 'getParams'
    // },
    data(){
      return{
        channel:false,
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
          broker_hotline:""
        },
        cityListSelect:[],
        buss1ListSelect:[],
        buss2ListSelect:[],
        buss3ListSelect:[],
        buss4ListSelect:[],
        error_content:"",
        show_content:false,
        bad_record_flag:"",
        investment_variety_flag:"",
        beneficiary_flag:"",
        con_relation_flag:"",
        proceeds_type_flag:"",
        term_investment_flag:"",
        platform_type_flag:"",
        tax_type_flag:"",
        val1:"",
        val2:"",
        val3:"",
        val1_name:"",
        val2_name:"",
        val3_name:"",
        commonList1: [{value:'仅为中国税收居民', key:'A'}, {value:'仅为非居民', key:'B'}, {value:'既是中国税收居民又是其他国家(地区)税收居民', key:'C'}],
        commonList2: [{value:'外国政要', key:'A'}, {value:'外国政要家庭成员', key:'B'}, {value:'与外国政要关系密切', key:'C'},{value:'无关系', key:'D'}],
        commonList3: [{value:'0年-1年', key:'A'}, {value:'1年-5年', key:'B'}, {value:'5年以上', key:'C'}],
        commonList4: [{value:'稳健', key:'A'}, {value:'成长', key:'B'}, {value:'激进', key:'C'}],
        commonList5: [{value:'不存在', key:'A'}, {value:'存在', key:'B'}],
        commonList6: [{value:'本人', key:'A'}, {value:'其他', key:'B'}],
        commonList7: [{value:'期货、期权',key:'A'},{value:'资管产品',key:'B'}, {value:'其他',key:'C'}],
        commonList8: [{value:'中国人民银行征信中心', key:'A'}, {value:'最高人民法院失信被执行人名单', key:'B'}, {value:'工商行政管理机构', key:'C'}, {value:'税务管理机构', key:'D'}, {value:'监管机制、自律组织', key:'E'}, {value:'投资者在期货经营机构从事投资活动时产生的违约行为记录', key:'F'}, {value:'过度维权等不当行为信息', key:'G'}, {value:'其他组织', key:'H'}, {value:'无', key:'I'}]
      }
    },
    created(){
      var _this = this;
      _this.broker_hotline = sessionStorage.getItem('broker_hotline');
      // 跳转页面带回来的数据 开始
      _this.getParams();
      // 跳转页面带回来的数据 结束
    },
    methods:{
      onClickLeft(){
        var _this = this;
        _this.$router.push({
          path:'/AddInfo',
          query:{
            isCC:"0",
            name: _this.ruleForm3.name,
            idNumber: _this.ruleForm3.idNumber,
            idAddress: _this.ruleForm3.idAddress,
            startTime: _this.ruleForm3.startTime,
            endTime: _this.ruleForm3.endTime,
            sheng: _this.ruleForm3.sheng,
            cityListSelect:_this.cityListSelect,
            shi: _this.ruleForm3.shi,
            detailAddress: _this.ruleForm3.detailAddress,
            zipCode: _this.ruleForm3.zipCode,
            phoneNumber: _this.ruleForm3.phoneNumber,
            contact_mobile: _this.ruleForm3.contact_mobile,
            email: _this.ruleForm3.email,
            profession: _this.ruleForm3.profession,
            industy: _this.ruleForm3.industy,
            minzu: _this.ruleForm3.minzu,
            educational: _this.ruleForm3.educational,
            buss1ListSelect:_this.buss1ListSelect,
            buss1:_this.ruleForm3.buss1,
            buss2ListSelect:_this.buss2ListSelect,
            buss2:_this.ruleForm3.buss2,
            buss3ListSelect:_this.buss3ListSelect,
            buss3:_this.ruleForm3.buss3,
            buss4ListSelect:_this.buss4ListSelect,
            buss4:_this.ruleForm3.buss4,
            tax_type:_this.ruleForm3.tax_type,
            platform_type:_this.ruleForm3.platform_type,
            term_investment:_this.ruleForm3.term_investment,
            proceeds_type:_this.ruleForm3.proceeds_type,
            con_relation:_this.ruleForm3.con_relation,
            beneficiary:_this.ruleForm3.beneficiary,
            investment_variety:_this.ruleForm3.investment_variety,
            bad_record:_this.ruleForm3.bad_record,
            val1:_this.val1,
            val2:_this.val2,
            val3:_this.val3,
            channel:_this.channel
          },
        });
      },
      onClickRight(){
        this.$router.push({name: 'MyBussiness'});
      },
      nextStep(){
        var _this = this;
        _this.$router.push({
          path:'/AddInfo',
          query:{
            isCC:"0",
            name: _this.ruleForm3.name,
            idNumber: _this.ruleForm3.idNumber,
            idAddress: _this.ruleForm3.idAddress,
            startTime: _this.ruleForm3.startTime,
            endTime: _this.ruleForm3.endTime,
            sheng: _this.ruleForm3.sheng,
            cityListSelect:_this.cityListSelect,
            shi: _this.ruleForm3.shi,
            detailAddress: _this.ruleForm3.detailAddress,
            zipCode: _this.ruleForm3.zipCode,
            phoneNumber: _this.ruleForm3.phoneNumber,
            contact_mobile: _this.ruleForm3.contact_mobile,
            email: _this.ruleForm3.email,
            profession: _this.ruleForm3.profession,
            industy: _this.ruleForm3.industy,
            minzu: _this.ruleForm3.minzu,
            educational: _this.ruleForm3.educational,
            buss1ListSelect:_this.buss1ListSelect,
            buss1:_this.ruleForm3.buss1,
            buss2ListSelect:_this.buss2ListSelect,
            buss2:_this.ruleForm3.buss2,
            buss3ListSelect:_this.buss3ListSelect,
            buss3:_this.ruleForm3.buss3,
            buss4ListSelect:_this.buss4ListSelect,
            buss4:_this.ruleForm3.buss4,
            tax_type:_this.ruleForm3.tax_type,
            platform_type:_this.ruleForm3.platform_type,
            term_investment:_this.ruleForm3.term_investment,
            proceeds_type:_this.ruleForm3.proceeds_type,
            con_relation:_this.ruleForm3.con_relation,
            beneficiary:_this.ruleForm3.beneficiary,
            investment_variety:_this.ruleForm3.investment_variety,
            bad_record:_this.ruleForm3.bad_record,
            val1:_this.val1,
            val2:_this.val2,
            val3:_this.val3,
            channel:_this.channel
          },
        });
      },
      getParams:function(){
        // 判断是否第一次进入页面
        if(this.$route.query.isCC == "0"){
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
          var bad_record_flag = this.$route.query.bad_record_flag;
          var investment_variety_flag = this.$route.query.investment_variety_flag;
          var beneficiary_flag = this.$route.query.beneficiary_flag;
          var con_relation_flag = this.$route.query.con_relation_flag;
          var proceeds_type_flag = this.$route.query.proceeds_type_flag;
          var term_investment_flag = this.$route.query.term_investment_flag;
          var platform_type_flag = this.$route.query.platform_type_flag;
          var tax_type_flag = this.$route.query.tax_type_flag;
          var val1 = this.$route.query.val1;
          var val2 = this.$route.query.val2;
          var val3 = this.$route.query.val3;
          var val1_name = this.$route.query.val1_name;
          var val2_name = this.$route.query.val2_name;
          var val3_name = this.$route.query.val3_name;
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
          this.buss2ListSelect = buss2ListSelect;
          this.buss3ListSelect = buss3ListSelect;
          this.buss4ListSelect = buss4ListSelect;
          this.ruleForm3.tax_type = tax_type;
          this.ruleForm3.platform_type = platform_type;
          this.ruleForm3.term_investment = term_investment;
          this.ruleForm3.proceeds_type = proceeds_type;
          this.ruleForm3.con_relation = con_relation;
          this.ruleForm3.beneficiary = beneficiary;
          this.ruleForm3.investment_variety = investment_variety;
          this.ruleForm3.bad_record = bad_record;
          if(buss1.length != 0){
            this.ruleForm3.buss1 = buss1;
          }
          if(buss2.length != 0){
            this.ruleForm3.buss2 = buss2;
          }
          if(buss3.length != 0){
            this.ruleForm3.buss3 = buss3;
          }
          if(buss4.length != 0){
            this.ruleForm3.buss4 = buss4;
          }
          this.bad_record_flag = bad_record_flag;
          this.investment_variety_flag = investment_variety_flag;
          this.beneficiary_flag = beneficiary_flag;
          this.con_relation_flag = con_relation_flag;
          this.proceeds_type_flag = proceeds_type_flag;
          this.term_investment_flag = term_investment_flag;
          this.platform_type_flag = platform_type_flag;
          this.tax_type_flag = tax_type_flag;
          this.val1 = val1;
          this.val2 = val2;
          this.val3 = val3;
          this.val1_name = val1_name;
          this.val2_name = val2_name;
          this.val3_name = val3_name;
          this.channel = channel;
        }
      },
      buss1Change(){
        var _this = this;
        var ruleForm3Buss1 = "";
        if(_this.ruleForm3.buss1.length != 0){
          ruleForm3Buss1 = _this.ruleForm3.buss1[0];
        }
        const getCity = {
          "systype": sessionStorage.getItem('systype'),
          "branch_province": ruleForm3Buss1
        };
        this.$ajax.post('/userInfo/chooseBranchProvince', getCity).then(function (res) {
          if(res.data.error_no == 0){
            var buss2List = res.data.branch_city_list;
            var a_buss2List = [];
            var b_buss2List = [];
            for(var i= 0;i<buss2List.length;i++){
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
          }else if(res.data.error_no == '-9999'){
            _this.$router.push({
              name: "Index"
            });
          }else{
            _this.error_content = res.data.error_info;
            _this.show_content = true;
          }
        }).catch(function (err) {
          console.log(err);
        });
      },
      buss2Change(){
        var _this = this;
        var ruleForm3Buss1 = "";
        var ruleForm3Buss2 = "";
        if(_this.ruleForm3.buss1.length == 0 || _this.ruleForm3.buss2.length == 0){

        }else {
          if (_this.ruleForm3.buss1.length != 0) {
            ruleForm3Buss1 = _this.ruleForm3.buss1[0];
          }
          if (_this.ruleForm3.buss2.length != 0) {
            ruleForm3Buss2 = _this.ruleForm3.buss2[0];
          }
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
              _this.$router.push({
                name: "Index"
              });
            } else {
              _this.error_content = res.data.error_info;
              _this.show_content = true;
            }
          }).catch(function (err) {
            console.log(err);
          });
        }
      },
      buss3Change(){
        var _this = this;
        var ruleForm3Buss1 = "";
        var ruleForm3Buss2 = "";
        var ruleForm3Buss3 = "";
        if(_this.ruleForm3.buss1.length == 0 || _this.ruleForm3.buss2.length == 0 || _this.ruleForm3.buss3.length == 0){
        }else {
          if (_this.ruleForm3.buss1.length != 0) {
            ruleForm3Buss1 = _this.ruleForm3.buss1[0];
          }
          if (_this.ruleForm3.buss2.length != 0) {
            ruleForm3Buss2 = _this.ruleForm3.buss2[0];
          }
          if (_this.ruleForm3.buss3.length != 0) {
            ruleForm3Buss3 = _this.ruleForm3.buss3[0];
          }
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
              _this.$router.push({
                name: "Index"
              });
            } else {
              _this.error_content = res.data.error_info;
              _this.show_content = true;
            }
          }).catch(function (err) {
            console.log(err);
          });
        }
      },
      change(){

      },
      changeTaxType(value, label){
        if(value[0]=="B" || value[0]=="C"){
          this.ruleForm3.tax_type = [];
          this.error_content = "请您联系我公司客服人员临柜办理开立期货账户事宜，联系电话" + this.broker_hotline + "。";
          this.show_content = true;
        }
      },
      changePlatformType(value, label){
        if(value[0]=="A" || value[0]=="B" || value[0]=="C"){
          this.ruleForm3.platform_type = [];
          this.error_content = "请您联系我公司客服人员临柜办理开立期货账户事宜，联系电话" + this.broker_hotline + "。";
          this.show_content = true;
        }
      },
      changeConRelation(value, label){
        if(value[0]=="B"){
          this.ruleForm3.con_relation = [];
          this.error_content = "请您联系我公司客服人员临柜办理开立期货账户事宜，联系电话" + this.broker_hotline + "。";
          this.show_content = true;
        }
      },
      changeBeneficiary(value, label){
        if(value[0]=="B"){
          this.ruleForm3.beneficiary = [];
          this.error_content = "请您联系我公司客服人员临柜办理开立期货账户事宜，联系电话" + this.broker_hotline + "。";
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
          this.error_content = "请您联系我公司客服人员临柜办理开立期货账户事宜，联系电话" + this.broker_hotline + "。";
          this.show_content = true;
        }
      },
      notarize(){
        this.show_content = false;
      }
    }
  }
</script>

<style lang="less">
  .choicePage{
    overflow: hidden;
    p{
      text-align: left;
      text-indent: 3%;
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
       top: 30%;
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
    .weui-cell__bd p{
      // line-height: 26px;
    }
    .weui-cell {
      padding: 5px 15px;
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
  }
</style>
