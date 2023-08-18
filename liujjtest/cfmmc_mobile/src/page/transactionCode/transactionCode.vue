<template>
    <div class="accountList">
      <van-nav-bar
        title="账户类型选择"
        left-text=""
        right-text="关闭"
        left-arrow
        right-arrow
        @click-left="onClickLeft('0')"
        @click-right="onClickRight"
      />
      <!-- 弹窗 -->
      <div style="width: 100%; height: 100%; background: rgba(0,0,0,0.7); position: fixed; z-index: 999; top:0;" v-if="show_first">
          <div class="pop-up">
              <div class="hint">提示</div>
              <div style="width:86%; margin: 4% auto; height: 87px;overflow: auto; background: #eeeeee;"><span class="character">{{tipsLeft}}<span style="font-weight:bold">{{risk_level}}型</span>{{tipsRight}}</span></div>
              <div class="push-button">
                 <van-button size="small" @click="goNext()">坚持开立交易编码</van-button>
                <van-button size="small" @click="redoChoose()" v-if="this.risk_level =='C3'">重新选择交易所</van-button>
                <van-button size="small" @click="abandon()" v-if="this.risk_level !='C3'">放弃开户</van-button>
              </div>
          </div>
      </div>
      <!--导航条-->
      <navigation/>
        <div class="accountListCommon">
          <div class="accounlCommon">商品期货</div>
          <van-checkbox-group
            v-model="radio1"
            v-for="(goodsFuture,index) in futuresLeft"
            :key="goodsFuture.name">
            <van-checkbox
              :name="goodsFuture.entryType"
              :disabled="test('S',goodsFuture)"
            >
              {{goodsFuture.name}}
            </van-checkbox>
            </van-checkbox-group>
        </div>

        <div class="accountListCommon">
          <div class="accounlCommon">金融期货</div>
            <van-checkbox-group v-model="radio2" v-for="(good,index) in futures_J" :key="good.name">
              <van-checkbox :name="good.entryType"  :disabled="test('J',good)">{{good.name}}</van-checkbox>
            </van-checkbox-group>
         </div>

        <div class="accountListCommon">
            <div class="accounlCommon">原油期货</div>
              <van-checkbox-group v-model="radio3" v-for="(future,index) in futures_N" :key="future.name">
                <van-checkbox :name="future.entryType"  :disabled="test('N',future)">{{future.name}}</van-checkbox>
              </van-checkbox-group>
          </div>
        <div style="overflow: hidden;margin-bottom: 2rem" v-if="showPic_J">
            <div class="reportKind" v-text="jinrong_name"></div>
            <div class="tips" style="margin-top: 20px">
              <span>温馨提示</span>：可访问链接{{financeAddr}}完成金融期货投资者适当性在线测试，并上传成绩报告单
            </div>
            <div>
                <img class="upload_a" v-if="isShow_J" @click="getImage('finance')" id="img_finance" src="../../assets/photo.png"/>
                <a v-for="(item,index) in upload" style="position: relative" class="contain_a" >
                    <img :src='item.url' alt="" class="inner_img">
                    <van-icon name="close" class="del" @click="delItem_J(index)"/>
                </a>
            </div>
        </div>
        <div v-if="showPic_N">
            <div class="reportKind" v-text="nengyuan_name"></div>
            <div class="tips">
              <span>温馨提示</span>：可访问链接{{ineAddr}}完成原油期货投资者适当性在线测试，并上传成绩报告单
            </div>
            <div>
                 <img class="upload_a" v-if="isShow_N" @click="getImage('ine')" id="img_ine" src="../../assets/photo.png"/>
                <a v-for="(item,index) in upload2" style="position: relative" class="contain_a" >
                    <img :src='item.url' alt="" class="inner_img">
                    <van-icon name="close" class="del" @click="delItem(index)"/>
                </a>
            </div>
        </div>
        <div style="height:265px; float: left;"><span>&nbsp;</span></div>
        <div style="width: 100%; background:white; position: fixed; bottom:0;"><van-button style="margin-bottom:3%" size="large" class="nextBtn" @click="next()">下一步</van-button></div>
      <popUp :tips="error_content" :showBox="show_content" @notarize="notarize"></popUp>
      <waitPop :loadingbox="loadingbox"></waitPop>
      <quit :content="content" :quit="quit"></quit>
    </div>
</template>
<script>
import { Group, Dialog,Toast,Icon} from 'vant';
import Quit from "../../components/quit.vue";
import PopUp from "../../components/popUp.vue";
import Utils from '../../util/iphoneOrAndroid';
import WaitPop from "../../components/waitPop.vue";
import Navigation from "../../components/navigation.vue";
    export default {
        components: {
          PopUp,
          Utils,
          WaitPop,
          Quit,
          Navigation
        },
        data(){
            return {
              quit:false,
              content:"",
              loadingbox: false,
              accept_no:'',
              error_content:"",
              show_content:false,
              tipsLeft:"",
              tipsRight:"",
             /* business_button_show_other:false,*/
              radio1:[],
              radio2:[],
              radio3:[],
              marketTypeArr:[],//全市场金融或原油
              futures_J:[],//金融
              futures_N:[],//原油
              futuresLeft:[],//商品
              canChoose_J:false,
              canChoose_N:false,
              choosed:[],//已选择
              /*  futuresRight:[],*/
              jinrong_name: '',
              nengyuan_name: '',
              openType_J:false,//全市场是否已开金融
              openType_N:false,//全市场是否已开原油
              risk_level:'',
              adeType:'',//账户类型（普通、专业、未分类）
              show_first: false,    //弹窗
              isSecond:false,
              upload:[], //上传的文件列表
              upload2:[],  //原油
              financeAddr:"",
              ineAddr:"",
              /*radio1Disabled:false,
              radio2Disabled:true,
              radio3Disabled:true,*/
              imgs:[],
              imgs_J:[],
              size:60000,
              uploadHide_N:true,
              uploadHide_J:true,
            /*  showRight:false,*/
            /*  chooseN:'',
              chooseJ:'',*/
             /* canchoose:'gray',*/
            /*  activeIndex:'1',*/

             /* paper_score:'',*/
             /* Jinrong:false,
              Nengyuan:false,*/

             /* isFirst:'',*/
            /*  showUploadDiv:false,*/
           /*   image_J:'',
              image_Y:'',*/

             /* dialogImageUrl: '',
              dialogVisible: false,
              dialogImageUrl2: '',
              dialogVisible2: false,
              showOut:true,
              dialogVisibleOut:false,*/

             /* choosedLeft:[],
              choosedRight:[],*/
            }
        },
        mounted(){
         /* var self = this;
          //获取回显结果
          this.$ajax.post('/account/qryAccountInfo', {"time":new Date().getTime()}).then(function (res) {
            if(res.data.error_no == 0){
              var accounts = JSON.parse(res.data.accounts);
             /!* if(accounts!=null && accounts.length>0  && self.openType_J){//全市场

              }*!/
              if(self.openType_J){//全市场已开金融
                _this.canChoose_J = true;
              }else{//全市场未开金融
                if(accounts!=null && accounts.length>0){//二次开户
                  _this.canChoose_J = true;
                }else{//一次开户
                  _this.canChoose_J = false;
                }
              }
              if(self.openType_N){//市场已开原油
                _this.canChoose_N = true;
              }else{
                if(accounts!=null && accounts.length>0){//二次开户
                  _this.canChoose_N = true;
                }else{//一次开户
                  _this.canChoose_N = false;
                }
              }
              console.log(accounts)
              for(var i=0;i<accounts.length;i++){
                if(accounts[i].id.indexOf('J')>-1 ){
                  self.radio2.push(accounts[i].id)
                }else if(accounts[i].id.indexOf('N')>-1 ){
                  self.radio3.push(accounts[i].id)
                }else{
                  self.radio1.push(accounts[i].id)
                }
                if(accounts[i].applySuccessFlag=="1"){
                  self.choosed.push(accounts[i].id)
                }
              }
            }else if(res.data.error_no == "-9999"){
              _this.$router.push({name:"Index"});
            }else{
              _this.show_content = true;
              _this.error_content = res.data.error_info;
            }
          }).catch(function (error) {
            console.log(error)
        });*/
      },
    created(){
		  var _this = this;
      window['uploadReport'] = function(picNo,img){
        _this.uploadReport(picNo,img);
      }
		  var data = {
			"business_type":sessionStorage.getItem('business_type'),
			"register_way":'2',
		  };
		  this.axios.post('/flow/qryAcceptInfo',data).then(function (res) {
        if(res.data.error_no == 0){
           /* _this.business_button_show_other = res.data.business_button_show_other;*/
			      _this.accept_no =res.data.accept_no;
          if (_this.$router.history.current.name != res.data.business_flow) {
            _this.$router.push('/error');
          }
          var checkMarketAccountData = {
            "systype": sessionStorage.getItem('systype'),
            "market_type": 'J,N',
          };
          //全市场是否已开金融或原油
          _this.$ajax.post('/account/checkMarketAccount', checkMarketAccountData).then(function (res) {
            if (res.data.resultList[0].error_no == 0) {
              var arr = res.data.resultList;
              console.log(arr)
              for (var i = 0; i < arr.length; i++) {
                //全市场是否开金融
                if(arr[i].market_type == 'J' && arr[i].open_type == 'true'){
                    _this.openType_J = true;
                }
                //全市场是否开原油
                if(arr[i].market_type == 'N' && arr[i].open_type == 'true'){
                  _this.openType_N = true;
                }
              }
              // _this.getBrandAccountInfo();
            } else {
               _this.error_content = res.data.error_info;
               _this.show_content = true;
            }
          }).catch(function (err) {
            console.log(err)
          });
          // 同步三方适当性、交易所信息数据
          var syncdata = {
            "accept_no": _this.accept_no,
            "business_type": sessionStorage.getItem('business_type'),
          }
          _this.axios.post('/account/synchroInfo', syncdata).then(function (res) {
            if (res.data.error_no == 0) {
              //获取期货公司数据
              _this.$ajax.post('/account/qryAccountList', {"time": new Date().getTime()}).then(function (res) {
                if (res.data.error_no == 0) {
                  var arr = JSON.parse(res.data.qry_account_list);
                  _this.financeAddr = res.data.finance_addr;
                  _this.ineAddr = res.data.ine_addr;
                  for (var i = 0; i < arr.length; i++) {
                    if (arr[i].entryType.indexOf('J') > -1) {//金融
                      _this.futures_J.push(arr[i]);
                     _this.jinrong_name = arr[i].name;
                    } else if (arr[i].entryType.indexOf('N') > -1) {//原油
                      _this.futures_N.push(arr[i]);
                      _this.nengyuan_name = arr[i].name;
                    } else { //商品
                      _this.futuresLeft.push(arr[i]);
                    }
                  }
                  _this.getBrandAccountInfo();
                } else {
                   _this.error_content = res.data.error_info;
                   _this.show_content = true;
                  return;
                }
              }).catch(function () {
              });
              // 请求2
              //查询风险等级();
              _this.$ajax.post('/adequacy/qryRiskInfo', {}).then(function (res) {
                if (res.data.error_no == 0) {
                  _this.adeType = res.data.adeType;  //1 专业  2普通  0未分类
                  _this.risk_level = res.data.risk_level;
                  if (res.data.risk_level == "C1" || res.data.risk_level == "C2") {
                    _this.show_first = true;
                    _this.tipsLeft = "尊敬的投资者您好，经评估，您属于";
                    _this.tipsRight = "风险承受能力投资者，该类型投资者仅可购买相关资管产品，不能开立期货或期权账户。";
                  } else if(res.data.risk_level == "C3"){
                    _this.tipsLeft = "尊敬的投资者您好，经评估，您属于";
                    _this.tipsRight = "风险承受能力投资者，该类型投资者仅可购买相关资管产品和开立商品期货账户,不能开立金融、原油期货账户。";
                  }else if (res.data.adeType == '1') {
                    _this.show_first = false;
                  } else {
                    _this.show_first = false;
                  }
                }
              }).catch(function (error) {
                console.log(error)
              });
              //获取金融初始化图片
              var majorData_J = {
                "image_no": 'finance',
                "systype": sessionStorage.getItem('systype')
              };
              _this.$ajax.post('/record/getArchivesInfo', majorData_J).then(function (res) {
                var arr = res.data.record_data;
                for (var i = 0; i < arr.length; i++) {
                  _this.upload.push({
                    "id": arr[i].record_id,
                    "url": "data:image/jpeg;base64," + arr[i].image_data
                  });
                }
              }).catch(function (error) {
              });
              //获取原油图片列表
              var majorData_Y = {
                "image_no": 'ine',
                "systype": sessionStorage.getItem('systype')
              };
              _this.$ajax.post('/record/getArchivesInfo', majorData_Y).then(function (res) {
                var arr = res.data.record_data;
                for (var i = 0; i < arr.length; i++) {
                  _this.upload2.push({
                    "id": arr[i].record_id,
                    "url": "data:image/jpeg;base64," + arr[i].image_data
                  })
                }
              }).catch(function (error) {
              });
            } else {
              _this.$message.error(res.data.error_info);
            }
          }).catch(function (err) {
            console.log(err)
          });
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
        },
        computed:{
      /*  isChooseRight:function(){
          if(this.choosedRight.length>0 || this.showUploadDiv == true){
            return  true
          }else{
            return false
          }
        },*/
        isShow_J(){
          if(this.upload.length>9){
            return this.uploadHide_N = false;
          }else{
            return this.uploadHide_N =true;
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
          if(!this.openType_J && this.canChoose_J && this.radio2.length != 0){
            if(this.choosed.indexOf("J_3")>=0){
              return false;
            }else{
              return true;
            }
          } else{
            return false;
          }
        },
        showPic_N(){
          if(!this.openType_N && this.canChoose_N && this.radio3.length != 0){
            if(this.choosed.indexOf("N")>=0){
              return false;
            }else{
              return true;
            }
          }else{
            return false;
          }
        },
      },
        methods:{
          test(type,val){
            if(type=="S" && this.choosed.indexOf(val.entryType)>=0){
              return true;
            }else if(type=="J" && (!this.canChoose_J || this.choosed.indexOf(val.entryType)>=0)){
                return true;
            }else if(type=="N" && (!this.canChoose_N || this.choosed.indexOf(val.entryType)>=0)){
              return true;
            }else{
              return false;
            }
          },
          redoChoose() {
            this.tipsLeft = '尊敬的投资者您好，经评估，您属于';
            if(this.risk_level == "C3"){
              this.tipsRight = '风险承受投资者，该类型投资者仅可购买相关资管产品和开立商品期货账户，不能开立金融、原油期货账户。';
            }else{
              this.tipsRight = '风险承受投资者，该类型投资者仅可购买相关资管产品，不能开立期货或期权账户。';
            }
            this.isSecond = false;
            this.show_first = false;
          },
           notarize(){
            this.show_content = false;
           },
            //坚持开户(二次告知)
            goNext(){
            /*  if (this.risk_level == "C3" && this.isSecond == false) {
                this.chooseSuccess();
              }
              if(this.isSecond == false){
                this.isSecond = true;

              }else{
                this.show_first = false;
              }*/
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
         /*   //重新风险测评
            goQuestion(){
             this.onClickLeft('2');
            },*/
            //放弃开户
            abandon(){
              this.$router.push('/index')
            },
            getImage(kind){
              var width = document.getElementById("img_"+kind).getBoundingClientRect().width + "";
              var height = document.getElementById("img_"+kind).getBoundingClientRect().height + "";
              console.log(width,height,kind)
              //调用安卓 ios windows方法
              var browserVersion = Utils.iphOrAnz();
              if (browserVersion == "iphone") {
                var aaa = "";
                window.location.href = "objc://takePicture_5/?"+kind+"?"+aaa+"?uploadReport";
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
                    self.upload.unshift({"url":"data:image/jpeg;base64,"+img,"id":res.data.record_id});
                  }else{
                    self.upload2.unshift({"url":"data:image/jpeg;base64,"+img,"id":res.data.record_id});
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

          /*  /!*金融图片*!/
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
                  "systype":"1"
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
                  //self.imageUrlFm ="data:image/jpeg;base64,"+ res.data.cropped_image;
                }).catch(function (error) {
                  console.log(error)
                });
            },*/
          /*  oversize_J(){
              // Toast.fail('文件过大,请重新上传!');
              _this.show_content = true;
              _this.error_content = "文件过大,请重新上传!";
            },*/
            delItem_J(index){
              console.log('删除',index,this.upload[index].id);
              this.loadingbox = true;
              var delData_J = {
                "image_no":'finance',
                "record_id":this.upload[index].id,
                "systype":sessionStorage.getItem('systype')
              };
              var _this = this;
              this.$ajax.post('/record/delArchives',delData_J).then(function (res) {
                if(res.data.error_no == 0){
                  _this.loadingbox = false;
                  _this.upload.splice(index,1)
                }else if(res.data.error_no == "-9999"){
                  _this.quit = true;
                  _this.content = res.data.error_info;
                  _this.loadingbox = false;
                }else{
                  _this.loadingbox = false;
                  _this.show_content = true;
                  _this.error_content = res.data.error_info;
                }
              }).catch(function (error) {
              });
            },
          /*  oversize(){
              // Toast.fail('文件过大,请重新上传!');
               _this.show_content = true;
              _this.error_content = "文件过大,请重新上传!";
            },*/
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
                  _this.quit = true;
                  _this.content = res.data.error_info;
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
            next(){
                var _this = this;
                if (_this.upload.length <= 1 && _this.radio2.length != 0 && _this.openType_J == false && _this.canChoose_J) {    //金融
                  if(this.choosed.indexOf("J_3")>=0){

                  }else{
                    _this.show_content = true;
                    _this.error_content = '请上传至少2张影像资料！';
                    return false
                  }
                }
                if (_this.upload2.length <= 1 && _this.radio3.length != 0 && _this.openType_N == false && _this.canChoose_N) {  // 原油
                  if(this.choosed.indexOf("N")>=0){

                  }else{
                    _this.show_content = true;
                    _this.error_content = '请上传至少2张影像资料！';
                    return false
                  }
                }
               //下一步告知(普通投资者、选金融或原油 C3等级)
                if(_this.adeType=="2" && _this.risk_level == 'C3'&&((_this.radio2.length != 0 && !_this.openType_J && _this.choosed.indexOf('J_3')<0) || (_this.radio3.length != 0 &&!_this.openType_N && _this.choosed.indexOf('N')<0))){
                  _this.show_first = true;
                  return;
                }
                _this.chooseSuccess();
              },
            chooseSuccess(){
              var _this = this;
              if(_this.radio1.length == 0 && _this.radio2.length == 0 && _this.radio3.length == 0){
                _this.show_content = true;
                _this.error_content = "请选择交易所！";
                return;
              }
              var account_flag = "";
              //页面可以选择  在期货公司没有开过户  并且选择了金融或原油（满足条件认为本次有勾选金融或原油）
              if(_this.canChoose_J && !(_this.choosed.indexOf(_this.radio2[0])>=0) && _this.radio2.length > 0){
                account_flag+="J,";
              }
              if(_this.canChoose_N && !(_this.choosed.indexOf(_this.radio3[0])>=0) && _this.radio3.length > 0){
                account_flag+="N,";
              }
              account_flag = account_flag.substring(0,account_flag.length-1);
              var arr = this.radio1.concat(this.radio2).concat(this.radio3);
              var savaData={
                "time":new Date().getTime(),
                "account_types":arr.join(','),
                "adequacy_type": _this.adeType,
                "risk_level": _this.risk_level,
                "account_flag":account_flag
              };
              _this.$ajax.post('/account/saveAccountInfo', savaData).then(function (res) {   //保存
                if(res.data.error_no == 0){
                  _this.onClickLeft('1');//1 下一步
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
            onClickLeft(type){
              var _this = this;
              if(_this.accept_no != "" && _this.accept_no != undefined){
                var data = {
                  "accept_no": _this.accept_no,
                  "business_type":sessionStorage.getItem('business_type'),
                  "business_button_type":type, //上一步 按钮类型
                  "business_flow": _this.$router.history.current.name,
                  "accept_status":""   //"accept_status":_this.accept_status,当需要更改状态时再传值
                };
                this.$ajax.post('/flow/updateAccept',data).then(function (res) {
                  if(res.data.error_no == 0){
                      _this.$router.push({name: res.data.business_flow});
                  }else if(res.data.error_no == "-9999"){
                    _this.quit = true;
                    _this.content = res.data.error_info;
                  }else{
                    _this.show_content = true;
                    _this.error_content = res.data.error_info;
                  }
                });
              }
            },
            onClickRight(){
              this.$router.push({name:'MyBussiness'})
            },
            getBrandAccountInfo(){
              var self = this;
              //获取回显结果
              self.$ajax.post('/account/qryAccountInfo', {"time":new Date().getTime()}).then(function (res) {
                if(res.data.error_no == 0){
                /*  var canChoose_J = false;
                  var canChoose_N = false;*/
                  var accounts = JSON.parse(res.data.accounts);
                  if(self.openType_J){//全市场已开金融
                    self.canChoose_J = true;
                  }else{//全市场未开金融
                    if(accounts!=null && accounts.length>0){//二次开户
                      // 判断是否二次开户
                      var twoOpen = false;
                      for (var i = 0; i < accounts.length; i++) {
                        if(accounts[i].applySuccessFlag=="1"){
                           twoOpen = true;
                           break;
                        }
                      }
                      //
                      if(twoOpen){
                        self.canChoose_J = true;
                      }else{
                        self.canChoose_J = false;
                      }
                      // for (var i = 0; i < accounts.length; i++) {
                      //   if(accounts[i].id.indexOf("J")>-1){
                      //     self.canChoose_J = false;
                      //     break;
                      //   }else{
                      //     self.canChoose_J = true;
                      //   }
                      // }
                    }else{//一次开户
                      self.canChoose_J = false;
                    }
                  }
                  if(self.openType_N){//市场已开原油
                    self.canChoose_N = true;
                  }else{
                    if(accounts!=null && accounts.length>0){//二次开户
                      // 判断是否二次开户
                      var twoOpen = false;
                      for (var i = 0; i < accounts.length; i++) {
                        if(accounts[i].applySuccessFlag=="1"){
                           twoOpen = true;
                           break;
                        }
                      }
                      //
                      if(twoOpen){
                        self.canChoose_N = true;
                      }else{
                        self.canChoose_N = false;
                      }
                      // for (var i = 0; i < accounts.length; i++) {
                      //   if(accounts[i].id.indexOf("N")>-1){
                      //     self.canChoose_N = false;
                      //     break;
                      //   }else{
                      //     self.canChoose_N = true;
                      //   }
                      // }
                    }else{//一次开户
                      self.canChoose_N = false;
                    }
                  }
                  for(var i=0;i<accounts.length;i++){
                    if(accounts[i].id.indexOf('J')>-1 ){
                      self.radio2.push(accounts[i].id)
                    }else if(accounts[i].id.indexOf('N')>-1 ){
                      self.radio3.push(accounts[i].id)
                    }else{
                      self.radio1.push(accounts[i].id)
                    }
                    if(accounts[i].applySuccessFlag=="1"){
                      self.choosed.push(accounts[i].id)
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
            }
          }
</script>
<style lang="less">
.accountList{
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
    height: 33%;
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
  .push-button{
    //  top: 76%;
    //  width: 100%;
    //  height: auto;
    //  position: absolute;
      padding-bottom: 5%;
  }
  .van-button--small{
    height: 35px;
    font-size: 12px;
    line-height: 28px;
    background: #49a5ff;
    color: white;
    border-radius: 4px;
    margin-left: 10px;
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
    padding-top: 8px;
  }
    //图片样式
    .contain_a{
        float: left;
        width: 150px;
        height: 146px;
        margin-left: 5%;
        margin-top: 12px;
        img{
            height: 100%;
        }
    }
    .upload_a{
        float: left;
        width: 150px;
        height: 150px;
        margin-left: 5%;
        margin-top: 8px;
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
          width: 100%;
        }
    }
    .del{
        position: absolute;
        top: -0.8rem;
        left: 92%;
        color: gray;
        font-size: 2rem;
    }
    .reportKind{
        text-align: left;
        height: 48px;
        font-size: 16px;
        line-height: 49px;
        text-indent: 20px;
        margin-top: 10px;
        background: #f6f6f6;
    }
    .van-uploader {
        float: left;
        width: 43%;
        height: 118px;
        border: 1px solid #999999;
        background: white;
        margin-top: 3%;
        margin-left: 5%;
    }
    .uploadReport_next{
        background: white;
        color:#49a5ff;
        width: 100%;
        left: 0%;
        position: fixed;
        bottom: 0;
        margin-top: 30px;
    }
    .character{
      line-height: 24px;
      float: left;
      text-align: left;
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
    .ok{
        width: 22%;
        color:#ffffff;
        background: #49a5ff;
        text-align: center;
        line-height: 35px;
        border-radius: 4px;
        margin: 13px auto;
        margin-bottom: 12px;
    }
    .tips{
      margin-top: 10px
    }
    a{
      color: #989ea4;
    }
}
</style>
