<template>
    <div class="accountList">
      <van-nav-bar
        title="账户类型选择"
        left-text=""
        right-text="关闭"
        left-arrow
        right-arrow
        @click-left="onClickLeft('0')"
        @click-right="onClickRight"/>
      <!--导航条-->
      <navigation/>
      <!-- 弹窗 -->
      <div style="width: 100%; height: 100%; background: rgba(0,0,0,0.7); position: fixed; z-index: 999; top:0;" v-if="show_first">
          <div class="pop-up">
              <div class="hint">提示</div>
              <div style="width:86%; margin: 4% auto;height: 87px;overflow: auto; background: #eeeeee;">
                <span class="character">{{tipsLeft}}<span style="font-weight:bold">{{risk_level}}型</span>{{tipsRight}}</span>
              </div>
              <div class="push-button">
                 <van-button size="small" @click="goNext()">坚持开户</van-button>
                 <van-button size="small" @click="goQuestion()" v-if="this.business_button_show_other == true">重新风险测评</van-button>
                 <van-button size="small" @click="abandon()" v-if="this.risk_level !='C3'">放弃开户</van-button>
                 <van-button size="small" @click="goChooseFutures()" v-if="this.risk_level =='C3'">重新选择交易所</van-button>
                 <van-button size="small" @click="goChooseFutures()" v-if="false">重新选择交易所</van-button>
              </div>
          </div>
      </div>
        <div class="accountListCommon">
          <div class="accounlCommon" v-if="futuresLeft.length>0">商品期货</div>
          <van-checkbox-group
            v-model="radio1"
            v-for="(goodsFuture,index) in futuresLeft"
            :key="goodsFuture.name">
            <van-checkbox
              :name="goodsFuture.entryType">{{goodsFuture.name}}
            </van-checkbox>
            </van-checkbox-group>
        </div>

        <div class="accountListCommon">
          <div class="accounlCommon" v-if="futures_J.length>0">金融期货</div>
            <van-checkbox-group v-model="radio2" v-for="(good,index) in futures_J" :key="good.name">
              <van-checkbox :name="good.entryType" @click="warningTips()" :disabled="!canChoose_J">{{good.name}}</van-checkbox>
            </van-checkbox-group>
         </div>

          <div class="accountListCommon">
            <div class="accounlCommon" v-if="futures_N.length>0">原油期货</div>
            
              <van-checkbox-group v-model="radio3" v-for="(future,index) in futures_N" :key="future.name">
                <van-checkbox :name="future.entryType" @click="warningTips()" :disabled="!canChoose_N">{{future.name}}</van-checkbox>
              </van-checkbox-group>
          </div>

        <!--图片显示-->
        <!--<div class="tips" style="margin-top: 20px">
            <span>温馨提示</span>：可访问链接《链接未配置》完成金融期货投资者适当性在线测试，并上传成绩报告单
        </div>-->

        <div style="overflow: hidden;margin-bottom: 2rem" v-if="showPic_J">
            <div class="reportKind" v-text="jinrong_name"></div>
             <div class="tips" style="margin-top: 20px">
              <span>温馨提示</span>：可访问链接{{financeAddr}}完成金融期货投资者适当性在线测试，并上传成绩报告单...
              <img @click="materIals" style="width: 5%;" src="../../assets/question.svg"/>
            </div>
            <div>
              <img class="upload_a" v-if="isShow_J" @click="getImage('finance')" id="img_finance" src="../../assets/photo.png"/>
                <a v-for="(item,index) in upload" style="position: relative" class="contain_a" id="img_finance">
                    <img :src='item.url' alt="" id="upload_img"  class="inner_img">
                    <van-icon name="close" class="del" @click="delItem_J(index)"/>
                </a>
            </div>
           <!-- <van-button  bottom-action class="uploadReport_next">下一步</van-button>-->
        </div>
        <div style="overflow: hidden;margin-bottom: 2rem" v-if="showPic_N">
            <div class="reportKind"></div>
            <div class="tips">
              <span>温馨提示</span>：可访问链接{{ineAddr}}完成原油期货投资者适当性在线测试，并上传成绩报告单...
              <img @click="materIals" style="width: 5%;" src="../../assets/question.svg"/>
            </div>
            <div class="warpbox">
              <img class="upload_a" v-if="isShow_N" @click="getImage('ine')" id="img_ine" src="../../assets/photo.png"/>
                <a v-for="(item,index) in upload2" style="position: relative" class="contain_a" id="img_ine">
                    <img :src='item.url' alt="" id="upload_img"  class="inner_img">
                    <van-icon name="close" class="del" @click="delItem(index)"/>
                </a>
            </div>
        </div>
            <van-button size="large" class="nextBtn" @click="next()">下一步</van-button>
        <!--  <div size="large" class="van-button-next" @click="next()">下一步</div>
      <div size="large" class="van-button-next" @click="next()">下一步</div>-->

      <popUp :tips="error_content" :showBox="show_content" @notarize="notarize"></popUp>
      <waitPop :loadingbox="loadingbox"></waitPop>
      <quit :content="content" :quit="quit"></quit>
      <div class="materIalsboxWarp" v-if="gaineBox">
      <div class="materIalsbox">
        <div>上传成绩报告单</div>
        <p style="margin-top: 7%">1、您选择了开立金融/原油户，需要完成期货投资者适当性在线测试，并上传成绩报告单。</p>
        <p>2、图片要求清晰可见，大小不要超过10M，仅支持JPG格式。</p>
        <p>3、金融/原油户，根据要求，至少上传2张符合条件的资料照片，最多上传10张资料照片。</p>
        <img @click="photoImg" class="closeTwo" src="../../assets/close2.svg"/>
      </div>
    </div>
    </div>
</template>
<script>
import Quit from "../../components/quit.vue";
import { Group, Dialog,Toast,Icon} from 'vant';
import PopUp from "../../components/popUp.vue";
import WaitPop from "../../components/waitPop.vue";
import Utils from '../../util/iphoneOrAndroid';
import Navigation from "../../components/navigation.vue";
export default {
  components: {
    PopUp,
    WaitPop,
    Quit,
    Navigation
  },
  data(){
    return {
        gaineBox: false,
        ineAddr:"",
        jinrong_name: '',
        financeAddr:"",
        quit:false,
        content:"",
        loadingbox: false,
        error_content:"",
        show_content:false,
        tips:'提示一',
        tipsLeft:"",
        tipsRight:"",
        business_button_show_other:false,
        isFirst:true,
        isSecond:false,
        futures_J:[],
        futures_N:[],
        canChoose_J:false,
        canChoose_N:false,
        imgs:[],
        imgs_J:[],
        size:60000,
        uploadHide:true,
        uploadHide_J:true,
        radio1:[],radio1Disabled:false,
        radio2:[],radio2Disabled:true,
        radio3:[],radio3Disabled:true,
        show_first: false,    //弹窗
        show_second:false,
        showRight:false,
        chooseN:'',
        chooseJ:'',
        canchoose:'gray',
        activeIndex:'1',
        risk_level:'',
        paper_score:'',
        Jinrong:false,
        Nengyuan:false,
        marketTypeArr:[],
        isFirst:'',
        showUploadDiv:false,
        image_J:'',
        image_Y:'',
        upload:[], //上传的文件列表
        upload2:[],
        dialogImageUrl: '',
        dialogVisible: false,
        dialogImageUrl2: '',
        dialogVisible2: false,
        showOut:true,
        dialogVisibleOut:false,
        accept_no:'',
        choosedLeft:[],
        choosedRight:[],
        active:4,
        level:'',
        second:false,
        showModal:false,
        showQRcode:false,
        cfmmc_title:'投资者王强您好,您在办理的是XXX业务，受理单编号：88888888',
        futuresLeft:[],
        futuresRight:[],
        choosedfuturesLeft:[],
        applySuccessFlag:'',
        business_flow:'11',
        allowChoose:false,
        adeType:'',
        isUpload_J:false,
        isUpload_N:false,
        first_select_exchange:""
      }
  },
  mounted(){
    var self = this;
    //获取回显结果
    this.$ajax.post('/account/qryAccountInfo', {"time":new Date().getTime()}).then(function (res) {
      if(res.data.error_no == 0){
        var accounts = JSON.parse(res.data.accounts);
        for(var i=0;i<accounts.length;i++){
          if(accounts[i].id.indexOf('J')>-1 ){
            self.radio2.push(accounts[i].id)
          }else if(accounts[i].id.indexOf('N')>-1 ){
            self.radio3.push(accounts[i].id)
          }else{
            self.radio1.push(accounts[i].id)
          }
        }
      }else if(res.data.error_no == "-9999"){
        self.quit = true;
        self.content = res.data.error_info;
      }else{
        self.show_content = true;
        self.error_content = res.data.error_info;
      }
    }).catch(function (error) {
      console.log(error)
    });
  },
  created(){
    var _this = this;
    window['uploadReport'] = function(picNo,img){
      _this.uploadReport(picNo,img)
    }
    //控制页面流程不能相互跳页面
   // _this.qrCode ='data:image/png;base64,'+sessionStorage.getItem("qrCode");
    var data = {
      "business_type":sessionStorage.getItem('business_type'),
    };
    _this.axios.post('/flow/qryAcceptInfo',data).then(function (res) {
     if(res.data.error_no == 0){
       _this.accept_no =res.data.accept_no;
       _this.business_flow=res.data.business_flow;
       _this.business_button_show_other = res.data.business_button_show_other;
       var first_select_exchange = res.data.first_select_exchange;
       if(_this.$router.history.current.name != res.data.business_flow){
         _this.$router.push(res.data.business_flow)
       }
       //获取期货公司数据
       _this.$ajax.post('/account/qryAccountList', {"time":new Date().getTime()}).then(function (res) {
         if(res.data.error_no == 0){
           var arr = JSON.parse(res.data.qry_account_list);
           _this.financeAddr = res.data.finance_addr;
           _this.ineAddr = res.data.ine_addr;
           if(first_select_exchange == "0"){
             first_select_exchange = res.data.first_select_exchange;
           }
           _this.first_select_exchange = first_select_exchange;
           var checkMarketAccountData = {
             "systype":sessionStorage.getItem('systype'),
             "market_type":'J,N',
           };
           _this.$ajax.post('/account/checkMarketAccount',checkMarketAccountData).then(function (res) {
             if(res.data.resultList[0].error_no == 0){
               var arr = res.data.resultList;
               if(arr[0].market_type == 'J' && arr[0].open_type == 'false' ){
                 if(first_select_exchange.trim() =="2"){
                   _this.canChoose_J = true;
                   _this.isUpload_J = true;
                 }else{
                   _this.canChoose_J = false
                   _this.isUpload_J = true;
                 }
               }else{
                 _this.canChoose_J = true;
               }
               if(arr[1].market_type == 'N' && arr[1].open_type == 'false'){
                 if(first_select_exchange.trim() =="2"){
                   _this.canChoose_N = true;
                   _this.isUpload_N = true;
                 }else {
                   _this.canChoose_N = false;
                   _this.isUpload_N = true;
                 }
               }else{
                 _this.canChoose_N = true;
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
           for(var i=0;i<arr.length;i++){
             if(arr[i].entryType.indexOf('J')>-1){
               _this.futures_J.push(arr[i])
             }else if(arr[i].entryType.indexOf('N')>-1){
               _this.futures_N.push(arr[i])
             }else{
               _this.futuresLeft.push(arr[i])
             }
           }
         }else if(res.data.error_no == "-9999"){
           _this.quit = true;
           _this.content = res.data.error_info;
         }else{
           _this.show_content = true;
           _this.error_content = res.data.error_info;
         }
       }).catch(function (error) {
         console.log(error)
       })
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
     // 结束
    //查询风险等级（）
    this.$ajax.post('/adequacy/qryRiskInfo',{}).then(function (res) {
      if(res.data.error_no == 0){
        _this.adeType = res.data.adeType;  //   风险等级是2普通
        _this.risk_level = res.data.risk_level;
        if(res.data.adeType == '2'){   //1是专业
          if(res.data.risk_level == "C1" || res.data.risk_level == "C2"){
            _this.show_first = true;
            _this.tipsLeft = "尊敬的投资者您好，经评估，您属于";
            _this.tipsRight = "风险承受能力投资者，该类型投资者仅可购买相关资管产品，不能开立期货或期权账户。";
          }else if(res.data.risk_level == "C3"){
              _this.tipsLeft = "尊敬的投资者您好，经评估，您属于";
              _this.tipsRight = "风险承受能力投资者，该类型投资者仅可购买相关资管产品和商品期货账户，不能开立金融、原油期货账户。";
          }
        }else{
          _this.show_first = false;
        }
      }else if(res.data.error_no == "-9999"){
        _this.quit = true;
        _this.content = res.data.error_info;
      }else{
        _this.show_content = true;
        _this.error_content = res.data.error_info;
      }
    }).catch(function (error) {
      console.log(error)
    });
    //获取金融初始化图片
    var majorData_J={
      "image_no":'finance',
      "systype":sessionStorage.getItem('systype')
    };
    this.$ajax.post('/record/getArchivesInfo', majorData_J).then(function (res) {
      if(res.data.error_no == 0){
        var arr = res.data.record_data;
        for(var i=0;i<arr.length;i++){
          _this.upload.push({
            "id":arr[i].record_id,
            "url":"data:image/jpeg;base64,"+arr[i].image_data
          })
        }
      }else if(res.data.error_no == "-9999"){
        _this.quit = true;
        _this.content = res.data.error_info;
      }else{
        _this.show_content = true;
        _this.error_content = res.data.error_info;
      }
    }).catch(function (error) {
      console.log(error)
    });
    //获取原油图片列表
    var majorData_Y={
      "image_no":'ine',
      "systype":sessionStorage.getItem('systype')
    };
    this.$ajax.post('/record/getArchivesInfo', majorData_Y).then(function (res) {
      if(res.data.error_no == 0){
        var arr = res.data.record_data;
        for(var i=0;i<arr.length;i++){
          _this.upload2.push({
            "id":arr[i].record_id,
            "url":"data:image/jpeg;base64,"+arr[i].image_data
          })
        }
      }else if(res.data.error_no == "-9999"){
         _this.quit = true;
         _this.content = res.data.error_info;
      }else{
         _this.show_content = true;
         _this.error_content = res.data.error_info;
      }
    }).catch(function (error) {
      console.log(error)
    });
  },
  computed:{
    isShow_J(){
      if(this.imgs_J.length>9){
        return this.uploadHide_J = false;
      }else{
        return this.uploadHide_J =true
      }
    },
    isShow_N(){
      if(this.upload2.length>9){
        return this.uploadHide_N = false;
      }else{
        return this.uploadHide_N =true;
      }
    },
    showPic_J(){
      if(this.canChoose_J && this.radio2.length != 0 && this.isUpload_J){
        return true
      } else{
        return false
      }
    },
    showPic_N(){
      if(this.canChoose_N && this.radio3.length != 0 && this.isUpload_N){
        return true
      }else{
        return false
      }
    },
  },
  methods:{
      materIals(){
      this.gaineBox = true;
    },
    photoImg(){
      this.gaineBox = false;
    },
    notarize(){
      this.show_content = false;
    },
    //坚持开户
    goNext(){
      if(this.isSecond == false){
        if(this.risk_level=="C3"){
          this.tipsLeft = "尊敬的投资者您好，您属于";
          this.tipsRight = "风险承受能力投资者，该类型投资者仅可购买相关资管产品和商品期货账户，不能开立金融、原油期货账户。" +
            "鉴于您不属于风险承受能力最低类别的投资者，如坚持开立金融、原油期货账户，" +
            "需填写《普通投资者购买高于自身风险承受能力产品或者服务的特别风险警示书》。";
        }else{
          this.tipsLeft = "尊敬的投资者您好，您属于";
          this.tipsRight = "风险承受能力投资者，该类型投资者仅可购买相关资管产品，不能开立期货或期权账户。" +
              "如您不属于风险承受能力最低类别的投资者且坚持开户，" +
              "需填写《普通投资者购买高于自身风险承受能力产品或者服务的特别风险警示书》。";
        }
        this.isSecond = true;
    }else{
      if(this.risk_level=="C3"){
        this.chooseSuccess();
      }
      this.show_first = false;
      }
    },
    //重新风险测评
    goQuestion(){
      var _this = this;
      _this.isSecond = false;
      var data = {
      "accept_no": _this.accept_no,
      "business_type":sessionStorage.getItem('business_type'),
      "business_button_type":'2',//下一步
      "business_flow": _this.$router.history.current.name,
      "accept_status":""   //"accept_status":_this.accept_status,当需要更改状态时再传值
      };
      _this.$ajax.post('/flow/updateAccept',data).then(function (res) {
        if(res.data.error_no == 0){
          _this.$router.push({name: res.data.business_flow});
        } else if(res.data.error_no == '-9999' ){
          _this.quit = true;
          _this.content = res.data.error_info;
        }else{
          _this.show_content = true;
          _this.error_content = res.data.error_info;
        }
      }).catch(function (error) {
        console.log(error)
      })
    },
    //放弃开户
    abandon(){
      this.$router.push('/index')
    },
    //重新选择交易所
    goChooseFutures(){
      this.show_first = false;
      if(this.risk_level == "C3"){
        this.tipsLeft = "尊敬的投资者您好，经评估，您属于";
        this.tipsRight = "风险承受能力投资者，该类型投资者仅可购买相关资管产品和商品期货账户，不能开立金融、原油期货账户。";
      }
      this.isSecond = false;
    },
    getImage(kind){
      var width = document.getElementById("img_"+kind).getBoundingClientRect().width + "";
      var height = document.getElementById("img_"+kind).getBoundingClientRect().height + "";
      var browserVersion = Utils.iphOrAnz();
      if (browserVersion == "iphone") {
        window.location.href = "objc://takePicture_5/?"+kind+"?"+""+"?uploadReport";
      } else if (browserVersion == "anzuo") {
        jsHandle.takePic(kind, "600", "600","uploadReport");
      } else if (browserVersion == "windows") {
        console.log("windows");
      }
    },
    uploadReport(picNo,img){
      this.sendReport(picNo,img)
    },
    sendReport(picNo,img){
      const self = this;  //这个很重要！读取文件base64
      var J_Date = {
        "image_no":picNo,
        "image_data":img,
        "systype":sessionStorage.getItem('systype')
       };
       self.$ajax.post('/record/uploadArchives', J_Date).then(function (res) {
         if(res.data.error_no == 0){
           if(picNo == 'finance'){
             self.upload.push({"url":"data:image/jpeg;base64,"+img,"id":res.data.record_id});
           }else{
             self.upload2.push({"url":"data:image/jpeg;base64,"+img,"id":res.data.record_id});
          }
        }else if(res.data.error_no == "-9999"){
          _this.quit = true;
          _this.content = res.data.error_info;
        }else{
          _this.show_content = true;
          _this.error_content = res.data.error_info;
        }
      }).catch(function (error) {
        console.log(error)
      });
    },
    /*金融图片*/
    onRead_J(file){
      Toast.loading({
        mask: true,
        duration:'500',
        message: '正在上传和识别,请稍等...'
      });
      var self = this;
      var base64_J = file.content.split(',');
      var J_Date = {
        "image_no":'finance',
        "image_data":base64_J[1],
        "systype":sessionStorage.getItem('systype')
      };
      self.$ajax.post('/record/uploadArchives', J_Date).then(function (res) {
        if(res.data.error_no == 0){
          self.upload.push({"url":file.content,"id":res.data.record_id});
        }else if(res.data.error_no == "-9999"){
           _this.$router.push({name:"Index"});
        }else{
          _this.show_content = true;
          _this.error_content = res.data.error_info;
        }
      }).catch(function (error) {
        console.log(error)
      });
    },
    oversize_J(){
      _this.show_content = true;
      _this.error_content = "文件过大,请重新上传!";
    },
    delItem_J(index){
      console.log('删除',index,this.upload[index].id);
      var delData_J = {
        "image_no":'finance',
        "record_id":this.upload[index].id,
        "systype":sessionStorage.getItem('systype')
      };
      var _this = this;
      this.$ajax.post('/record/delArchives',delData_J).then(function (res) {
        if(res.data.error_no == 0){
          _this.upload.splice(index,1)
        }else if(res.data.error_no == "-9999"){
           _this.$router.push({name:"Index"});
        }else{
          _this.show_content = true;
          _this.error_content = res.data.error_info;
        }
      }).catch(function (error) {
        console.log(error)
      });
    },
    oversize(){
      _this.show_content = true;
      _this.error_content = "文件过大,请重新上传!";
    },
    onRead(file){
      Toast.loading({
        mask: true,
        duration:'500',
        message: '正在上传和识别,请稍等...'
      });
      var self = this;
      var base64_J = file.content.split(',');
      var J_Date = {
        "image_no":'ine',
        "image_data":base64_J[1],
        "systype":sessionStorage.getItem('systype')
      };
      self.$ajax.post('/record/uploadArchives', J_Date).then(function (res) {
        if(res.data.error_no == 0){
          self.upload2.push({"url":file.content,"id":res.data.record_id});
        }else if(res,data.error_no == "-9999"){
          _this.$router.push({name:"Index"});
        }else{
          _this.show_content = true;
          _this.error_content = res.data.error_info;
        }
        //self.imageUrlFm ="data:image/jpeg;base64,"+ res.data.cropped_image;
      }).catch(function (error) {
        console.log(error);
      });
    },
    delItem(index){
      console.log('删除2',index,this.upload2[index].id);
      var delData_N = {
        "image_no":'ine',
        "record_id":this.upload2[index].id,
        "systype":sessionStorage.getItem('systype')
      };
      var _this = this;
      this.$ajax.post('/record/delArchives',delData_N).then(function (res) {
        if(res.data.error_no == 0){
          _this.upload2.splice(index,1)
        }else if(res.data.error_no == "-9999"){
          _this.$router.push({name:"Index"});
        }else{
          _this.show_content = true;
          _this.error_content = res.data.error_info;
        }
      }).catch(function (error) {
        console.log(error)
      });
    },
    next(){
      if (this.upload.length <= 1 && this.radio2.length != 0 && this.isUpload_J) {    //金融
          this.show_content = true;
          this.error_content = '请上传至少2张影像资料！';
          return false
      }
      if (this.upload2.length <= 1 && this.radio3.length != 0 && this.isUpload_N) {  // 原油
          this.show_content = true;
          this.error_content = '请上传至少2张影像资料！';
          return false
      }
      if (this.risk_level == "C3" && this.isSecond == false && ((this.radio2.length != 0) || (this.radio3.length != 0))) {
        this.show_first = true;
      }else{
        this.chooseSuccess();
      }
    },
    onClickLeft(type){
      var _this = this;
      if(_this.accept_no != "" && _this.accept_no != undefined){
        var data = {
          "accept_no": _this.accept_no,
          "business_type":sessionStorage.getItem('business_type'),
          "business_button_type":type, //上一步 按钮类型
          "business_flow": _this.$router.history.current.name,
          "accept_status":"",   //"accept_status":_this.accept_status,当需要更改状态时再传值
        };
        //只有下一步的时候才会传此参数
        if(type == "1"){
          data.first_select_exchange = _this.first_select_exchange;
        }
        _this.loadingbox = true;
        this.$ajax.post('/flow/updateAccept',data).then(function (res) {
          _this.loadingbox = false;
          if(res.data.error_no == 0){
              _this.$router.push({name: res.data.business_flow});
          }else if(res.data.error_no == "-9999"){
            _this.quit = true;
            _this.content = res.data.error_info;
          }else{
            _this.show_content = true;
            _this.error_content = res.data.error_info;
          }
        }).catch(function (error) {
          console.log(error)
        });
      }
    },
    onClickRight(){
      this.$router.push({name:'MyBussiness'})
    },
    buttonLeft1(){
      this.showRight = false
      this.showCentre = false
      this.show_first = false
    },
    buttonLeft2(){
      this.showRight = false
      this.showCentre = false
    },
    buttonLeft(){
      this.showRight = false
      this.showCentre = false
      },
    chooseSuccess(){
      var _this = this;
      if(_this.radio1.length == 0 && _this.radio2.length == 0 && _this.radio3.length == 0){
        _this.show_content = true;
        _this.error_content = "请选择交易所！";
        return;
      }
      var account_flag = "";
      if(_this.canChoose_J &&_this.radio2.length>0){
        account_flag+="J,";
      }
      if(_this.canChoose_N && _this.radio3.length>0){
        account_flag+="N,";
      }
      account_flag = account_flag.substring(0,account_flag.length-1);
      var arr = _this.radio1.concat(_this.radio2).concat(_this.radio3);
      var savaData={
        "time":new Date().getTime(),
        "account_types": arr.join(','),
        "adequacy_type": _this.adeType,
        "risk_level": _this.risk_level,
        "account_flag":account_flag
      };
      _this.loadingbox = true;
      _this.$ajax.post('/account/saveAccountInfo', savaData).then(function (res) {   //保存
        _this.loadingbox = false;
        if(res.data.error_no == 0){
          _this.onClickLeft('1');//下一步
        }else if(res.data.error_no == "-9999"){
          _this.quit = true;
          _this.content = res.data.error_info;
        }else{
          _this.show_content = true;
          _this.error_content = res.data.error_info;
        }
      }).catch(function (error) {
        console.log(error)
      });
    }
  },
}
</script>
<style lang="less">
.accountList{
  overflow: hidden;
  .accounlCommon {
    background: #f6f6f6;
    height: 40px;
    font-size: 16px;
    line-height: 40px;
    text-align: left;
    text-indent: 15px;
  }
  .van-radio .van-icon-checked{
    color: #49a5ff;
  }
  .van-nav-bar__text{
    color: #49a5ff;
  }
  .pop-up{
    width: 84%;
    height: auto;
    margin: 0 auto;
    background: white;
    position: absolute;
    left: 8%;
    top: 34%;
      p{
        text-align: left;
        width: 90%;
        margin: 0 auto;
        line-height: 24px;
        padding-top: 3%;
      }
      .hint{
        font-size: 16px;
        color: #666666;
        text-align: center;
        background: #49a5ff;
        line-height: 38px;
        color: white;
      }
  }
  @media only screen and (min-width: 1349px) and (orientation : landscape){
    .pop-up{
      width: 34%;
      left: 33%;
    }
  }
  .push-button{
    padding-bottom: 5%;
  }
  .van-button--small{
    height: 35px;
    font-size: 12px;
    line-height: 28px;
    background: #49a5ff;
    color: white;
    border-radius: 4px;
    margin-left: 7px;
  }
  .van-button::before{
    width: 76px;
  }
  .van-checkbox {
    text-align: left;
    padding-left: 22px;
    overflow: hidden;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
    line-height: 39px;
  }
  .contain_a{
      float: left;
      width: 150px;
      height: 150px;
      margin-left: 5%;
      margin-top: 5%;
      img{
          height: 100%;
          width: 100%;
      }
  }
    .upload_a{
        float: left;
        width: 150px;
        height:150px;
        margin-left: 5%;
        margin-top: 5%;
    }
    .upload{}
    .upload_img{
        width: 100%;
        height: 100%;
    }
    a{
        float: left;
        width: 43%;
        height:120px;
        margin-left: 5%;
        margin-top: 3%;
        img{
          height: 100%;
        }
    }
    .del{
        position: absolute;
        top: -0.8rem;
        left: 92%;
        color: gray;
        font-size: 2rem;
    }
    // .reportKind{
    //     text-align: left;
    //     height: 2.5rem;
    //     font-size: 16px;
    //     line-height: 2.5rem;
    //     text-indent: 20px;
    //     margin-top: 10px;
    // }
    .van-uploader {
        float: left;
        width: 43%;
        height: 118px;
        border: 1px solid #999999;
        background: white;
        margin-top: 3%;
        margin-left: 5%;
    }
    /*.uploadReport_next{
        background: white;
        color:#49a5ff;
        width: 100%;
        left: 0%;
        position: fixed;
        bottom: 0;
        margin-top: 30px;
    }*/
    .character{
      line-height: 24px;
      text-align: left;
      float: left;
      margin-bottom: 5%;
    }
    .reminder{
        text-align: center;
        font-size: 16px;
        color:#333333;
        width:100%;
        background:#49a5ff;
        line-height: 37px;
        color:#ffffff;
    }
    .van-popup{
        width: 80%;
    }
    /*.ok{
        width: 22%;
        color:#ffffff;
        background: #49a5ff;
        text-align: center;
        line-height: 35px;
        border-radius: 4px;
        margin: 13px auto;
        margin-bottom: 12px;
    }*/
    .warpbox{
      width: 100%;
      //height: 500px;
      padding-bottom: 5%;
    }
     @media only screen and (min-device-width : 768px) and (max-device-width : 1024px){
      .warpbox{
        // width: 50%;
        height: auto;
      }
    }
    //ipad横屏
  @media only screen and (min-width: 1349px) and (orientation : landscape){
    .upload_a{
      width: 12%;
    }
    .contain_a img{
      height: auto;
      width: 100%;
      height: 130px;
      margin-top: 3%;
    }
    .accountList a{
      width:0;
    }
    .contain_a{
      width: 168px;
      margin-top: 4%;
    }
    .warpbox{
      height: 400px;
    }
  }
  @media only screen and (min-device-width : 1024px) and (max-device-width : 1366px){
    .upload_a{
      width: 235px;
      height: 235px;
      margin-left: 4%;
    }
    .contain_a{
      width: 235px;
      height: 235px;
      margin-left: 4%;
    }
  }
   .materIalsboxWarp{
      width: 100%;
      height: 100%;
      position: fixed;
      top: 0;
      background: white;
      z-index: 99;
    }
    .materIalsbox{
      position: relative;
      width: 86%;
      float: left;
      margin: 8% 7%;
      height: 85%;
        div{
          width: 100%;
          border-bottom: 1px solid #ededed;
          font-size: 18px;
          font-family: PingFangSC-Regular;
          color: #333333;
          line-height: 68px;
          text-align: center;
        }
        p{
          font-size: 14px;
          color:#666666;
          line-height:29px;
          text-align:left;
        }
    }
     .closeTwo{
      position: absolute;
      bottom: 0;
      left: 46%;
    }
}
</style>
