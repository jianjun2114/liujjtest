<template>
  <div class="myBussiness">
    <van-nav-bar title="我的业务" right-text="关闭" right-arrow @click-right="onClickRight" style="position: fixed; width:100%; top:0; left:0;"/>
    <div :class="{divHeight:true,changeDivHeight:!reservationFlag}"></div>
    <div class="reservation" v-if="reservationFlag && !showReservation">
      您已预约于 <span class="font">{{reservationTime}}</span>  视频见证办理 <span class="font">{{businessName}}</span> 业务 ，请在此段时间内登录开户云平台进行视频见证。
      <button @click="isNotcancel">取消预约</button>
    </div>
    <van-tabs v-model="active" :class="{hiddenReservation:showReservation,showReservation:!showReservation}">
      <van-tab title="待完成业务">
        <div v-for="(todolist,index) in todolists" class="todolist_div">
          <van-row>
            <van-col span="15">{{todolist.business_type_name}}</van-col>
            <van-col span="9">{{todolist.last_update_time}}</van-col>
          </van-row>
          <div v-if="todolist.status!='9'" class="todolist_content">
          <p>{{todolist.status_name}}</p>
           <p>{{todolist.remark}}</p>
          </div>
          <div v-if="todolist.status=='9'" class="todolist_content">复核中，提交成功，等待审核</div>
          <!-- <div v-if="todolist.status=='2'" class="todolist_content">待复核，提交成功，等待审核</div>
          <div v-if="todolist.status=='3'" class="todolist_content">复核中，提交成功，等待审核</div> -->
          <!-- status 2/3  待复核、复核中  status 9 视频打回  显示 复核中   隐藏操作按钮-->
          <div style="width: 90%; margin: 0 auto;">
          <van-button :class="{todolist_btnValue:true,abandonLeft:todolist.accept_no.trim()==''}" v-if="!((id_type=='1' && (todolist.business_type=='11' || todolist.business_type=='12'))|| todolist.status=='9' ||(todolist.status=='2'|| todolist.status=='3'||todolist.status=='11'))" @click="operation(todolist)" v-show="must_forfeit">操作</van-button>
          <van-button size="mini" :class="{abandon:((id_type=='1' && (todolist.business_type=='11'||todolist.business_type=='12'))|| todolist.status=='9' ||(todolist.status=='2'|| todolist.status=='3' || todolist.status=='11'))}" @click="abandon(todolist,index)" v-if="todolist.accept_no.trim()!=''">放弃</van-button>
          </div>
        </div>
      </van-tab>
      <van-tab title="已完成业务">
        <div v-for="dolist in dolists" class="todolist_div">
          <van-row>
            <van-col span="15">{{dolist.business_type_name}}</van-col>
            <van-col span="9">{{dolist.last_update_time}}</van-col>
          </van-row>
          <van-row>
            <van-col span="15" class="accept_no" v-if="dolist.business_type == 11 || dolist.business_type == 13">资金账号:{{dolist.account_no}}</van-col>
            <van-col span="9" :class="{isSuccess:dolist.status==12,isError:dolist.status==4,isWaiting:dolist.status==5}">{{dolist.statusName}}</van-col>
          </van-row>
          <div class="todolist_content" v-if="dolist.status == 5">已提交成功，等待柜台复核，复核完成短信通知。</div>
           <div class="todolist_content" v-if="dolist.status == 12">办理成功!</div>
            <div class="todolist_content" v-if="dolist.status == 4">办理失败！</div>
        </div>
      </van-tab>
    </van-tabs>
    <div style="height: 8rem"></div>
    <footer-bar></footer-bar>
    <popUp :tips="error_content" :showBox="show_content" @notarize="notarize"></popUp>
    	<van-popup v-model="tabsNav" :close-on-click-overlay="false">
					<div class="hint">提示</div>
					<div style="width:93%; margin: 0 auto; padding-botom: 3%">
						<p style="text-align: left; font-size: 16px; color:#333333; line-height: 24px; padding-top: 5%;">{{tips}}</p>
					</div>
					<div class="boxes_button">
						<div class="cancel" @click="cancel">取消</div>
						<div class="cancel abolish" @click="ensure">{{buttonText}}</div>
					</div>
				</van-popup>
    <quit :content="content" :quit="quit"></quit>
     <confirm :showContents="showContents" :isShow="isShow" :yesButton="yesButton" :noButton="noButton" @ensure="ascertain" @cancel="countermand"></confirm>
    <showDiaLog :ShowDisplay="ShowDisplay" :titleName="titleName" :workCenter="workCenter"
              @buttonOneClick="buttonOneClick"  :buttonOne="buttonOneName"
              @buttonTwoClick="buttonTwoClick"  :buttonTwo="buttonTwoName"
              @buttonThreeClick="buttonThreeClick"  :buttonThree="buttonThreeName">
    </showDiaLog>
  </div>
</template>
<script>
  import {Tab, Tabs, Pagination} from 'vant';
  import FooterBar from '../../components/footerBar.vue';
  import Utils from '../../util/iphoneOrAndroid';
  import PopUp from "../../components/popUp.vue";
  import Quit from "../../components/quit.vue";
  import ShowDiaLog from "../../components/showDiaLog.vue";
  import Confirm from "../../components/confirm.vue";
  export default {
    inject:['reload'], //注入依赖
    components: {
      FooterBar,
      PopUp,
      Quit,
      ShowDiaLog,
      Confirm
    },
    data(){
      return{
        showContents:"",
        isShow:false,
        yesButton:"确定",
        noButton:"取消",
        buttonText:"",
        text:"确定",
        totalTime:5,
        timer:0,
        buttonOneName:"",
        buttonTwoName:"",
        buttonThreeName:"",
        workCenter:"",
        ShowDisplay: false,
        titleName:"",
        tips:"",
        tabsNav:false,
        accept_no:"",
        error_content:"",
        show_content:false,
        active:'',
        todolists:[],
        dolists:[],
        todolist:{},
        index:"",
        content:"",
        quit:false,
        popPText:'',
        isExpireFlag:'',
        id_type:'',
        showReservation:true,
        businessName:"",
        reservationTime:"",
        isOut:false, //表示是否退出  区分弹框确定回调事件
        reservationFlag:false, //预约视频配置,
        must_forfeit:true,
        forfeit:true
      }
    },
    created() {
      var _this = this;
      _this.broker_hotline = sessionStorage.getItem('broker_hotline');
      _this.axios.post('/myBussiness/getToBeHandleBusiness',{"supplement_type":sessionStorage.getItem('systype')}).then(function (res) {
        if (res.data.error_no==0 || res.data.error_no == -6061) {
          var result = JSON.parse(res.data.bussiness_info);
          _this.todolists = result;
          if (res.data.must_forfeit === '1') {
                _this.must_forfeit = false;
                _this.show_content = true;
                _this.error_content = res.data.error_info;
            }
          _this.id_type = res.data.id_type;
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

      //办结/申请业务
      _this.axios.post('/myBussiness/getFinishBusiness',{}).then(function (res) {
        if(res.data.error_no == 0){
          var resultdata = JSON.parse(res.data.bussiness_info);
          _this.dolists = resultdata;
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

        //视频预约信息获取
        _this.axios.post('/video/qryOrderQueue',{}).then(function (res) {
          if(res.data.error_no == 0){
            var queue_status = res.data.queue_status;
            _this.reservationFlag = queue_status == "1" ? true : false;
            if(queue_status == "1"){
              if(res.data.queueMap){
                _this.showReservation = false;
                _this.businessName = res.data.queueMap.business_name;
                var orderTime = "";
                if(res.data.queueMap.order_time == "0"){
                  orderTime = "上午";
                }else{
                  orderTime = "下午";
                }
                _this.reservationTime = res.data.queueMap.reservation_time+orderTime;
              }
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

    },
    mounted() {
      //business_type==20 协议下载   协议下载不检查前置任务
      if (sessionStorage.getItem('login_type') == '1' && sessionStorage.getItem('business_type')!="20"){
        this.expireFlag(sessionStorage.getItem('_idcardFlag'), sessionStorage.getItem('_adequacyType'), sessionStorage.getItem('_adequacyFlag'),sessionStorage.getItem('_otherFlag'),sessionStorage.getItem('_supplementStatus'));
      }
    },
    methods:{
      buttonTwoClick(){

      },
      buttonThreeClick(){

      },
      cancel(){
        this.tabsNav = false;   //取消
        this.totalTime = 5;
        if(this.timer != 0){
          clearInterval(this.timer);
        }
      },
      ensure(){
        if(this.totalTime > 0){
          return;
        }
        var _this = this;
        _this.tabsNav = false;
        var todolist = _this.todolist;
        var index = _this.index;
        var data = {
          "accept_no": todolist.accept_no,
          "business_type": todolist.business_type
        }
        _this.axios.post('/myBussiness/abandonBusiness',data).then(function (res) {
          if(res.data.error_no == 0){
            _this.todolists.splice(index,1);   //放弃删除数据
            // _this.$router.push({name:'Index'});
            _this.showReservation = true; //取消已有预约
          } else if(res.data.error_no == "-9999"){
            _this.quit = true;
            _this.content = res.data.error_info;
          } else{
            _this.show_content = true;
            _this.error_content = res.data.error_info;
          }
          _this.todolist = {};
          _this.index = "";
        }).catch(function (err) {
            console.log(err)
        });
      },
      abandon(todolist,index){   //放弃
        var _this = this;
        _this.tabsNav = true;
        _this.tips = "放弃后将清除您的数据，需要重新办理业务，是否确认放弃？"
        _this.todolist = todolist;
        _this.index = index;
        _this.buttonText = "("+_this.totalTime +") "+ _this.text
        _this.timer = window.setInterval(() => {
          _this.totalTime--
          if (_this.totalTime <= 0) {     //当倒计时小于0时清除定时器
            clearInterval(_this.timer)
            _this.buttonText = _this.text;
            return;
          }
          _this.buttonText = "("+_this.totalTime +") "+ _this.text
        },1000)
      },
      notarize(){
         this.show_content = false;
        //  this.reload();
      },
      buttonOneClick() {
        var _this = this;
        _this.ShowDisplay = false;
        sessionStorage.setItem("business_type", _this.isExpireFlag);
        var data = {
          'business_type': _this.isExpireFlag,
          'systype': sessionStorage.getItem('systype'),
          "supplement_type":sessionStorage.getItem('systype')
        };
        this.$ajax.post('/myBussiness/getBusiness', data).then(function (res) {
          if (res.data.error_no == 0) {
            sessionStorage.setItem('login_type',"2");
            //若是前置任务   强制协议  协议阅读或签署加载强制协议
            sessionStorage.setItem('_supplementStatusAgreement', "0");
            if(sessionStorage.getItem('_supplementStatus')=="2" && _this.isExpireFlag == "21"){
              sessionStorage.setItem('_supplementStatusAgreement', "2");//0:非补签 1:普通补签 2: 强制补签
            }
            _this.$router.push({name: res.data.business_flow, query: {id: Math.random()}});
          } else {
            _this.show_content = true;
            _this.error_content = res.data.error_info;
          }
        }).catch(function (error) {
          alert('前置任务：' + error);
          console.log(error);
        });
      },
      onClickRight(){
        var browserVersion = Utils.iphOrAnz();
        if (browserVersion == "iphone") {
          this.showContents = "是否确认退出开户流程？";
          this.isShow = true;
          this.isOut = true;
        } else if (browserVersion == "anzuo") {
          jsHandle.logout();
        } else if (browserVersion == "windows") {
          console.log("windows");
        }
      },
      countermand(){
        this.isShow = false;
        this.isOut = false;
      },
      ascertain(){
        this.isShow = false;
        if(this.isOut){
          window.location.href = "objc://callIOSOut_5";
          return;
        }
        this.cancelReservation();
       /* var browserVersion = Utils.iphOrAnz();
        if (browserVersion == "iphone") {
          window.location.href = "objc://callIOSOut_5";
        } else if (browserVersion == "anzuo") {
          jsHandle.logout();
        } else if (browserVersion == "windows") {
          console.log("windows");
        }*/
      },
      operation(item){
        var _this = this;
        if(item.accept_no.trim() == ""){//普通补签
          //验证前置任务
          var data = {
            "business_type":item.business_type,
            "accept_no":item.accept_no,
            "supplement_sign_type":"1",
            "supplement_type":sessionStorage.getItem('systype_login')
          }
          _this.axios.post('/myBussiness/getBusiness',data).then(function (res) {
            if (res.data.error_no== 0) {
             /* if(item.business_type == "12"){
                sessionStorage.setItem("systype","2");
              }else{
                sessionStorage.setItem("systype","1");
              }*/
             //受理单为空，取登录的账户类型
              sessionStorage.setItem("systype", sessionStorage.getItem('systype_login'));

              if(res.data.to_be_handle_flag != "1"){//1:有待办业务  0没有待办业务
                sessionStorage.setItem('_idcardFlag', res.data.idcard_flag);
                sessionStorage.setItem('_adequacyType', res.data.adequacy_type);
                sessionStorage.setItem('_adequacyFlag', res.data.adequacy_flag);
                sessionStorage.setItem('_otherFlag', res.data.other_flag);//1：其他资料不全
                sessionStorage.setItem('_supplementStatus', res.data.supplement_status);//0:非补签 1:普通补签 2: 强制补签
                sessionStorage.setItem('_supplementStatusAgreement', "1");//0:非补签 1:普通补签 2: 强制补签
                sessionStorage.setItem('_otherAndSupplement', res.data.other_and_supplement);//1:既有信息不完整又有强制补签
              }
                _this.isExpireFlag = '';
              var idcard = sessionStorage.getItem('_idcardFlag');
              var adeType = sessionStorage.getItem('_adequacyType');
              var adeFlag = sessionStorage.getItem('_adequacyFlag');
              var otherFlag = sessionStorage.getItem('_otherFlag');
              var supplementStatus = sessionStorage.getItem('_supplementStatus');
              var otherAndSupplement = sessionStorage.getItem('_otherAndSupplement');

              if (idcard == '1') {
                _this.isExpireFlag = '23';
                _this.ShowDisplay = true;
                _this.workCenter = "由于您的身份证有效期已过期或有效期缺失，请办理完毕身份证有效期修改后，方可进行其他操作";
                _this.titleName = "提示";
                _this.buttonOneName = "立即处理"
                return;
              }
              if (adeType == '0') {
                // this.popPText = '由于您的投资者适当性状态无效，请完成投资者适当性评估后，方可进行其他操作';
                _this.isExpireFlag = '24';
                _this.ShowDisplay = true;
                _this.workCenter = "由于您的投资者适当性状态无效，请完成投资者适当性评估后，方可进行其他操作";
                _this.titleName = "提示";
                _this.buttonOneName = "立即处理";
                return;
              }
              if (adeFlag == '1') {
                // this.popPText = '由于您的投资者适当性已过期，请完成投资者适当性评估后，方可进行其他操作';
                _this.ShowDisplay = true;
                _this.workCenter = "由于您的投资者适当性已过期，请完成投资者适当性评估后，方可进行其他操作";
                _this.titleName = "提示";
                _this.isExpireFlag = '22';
                _this.buttonOneName = "立即处理"
                return;
              }
              if (otherFlag == '1') {
                _this.ShowDisplay = true;
                _this.workCenter = "由于您的资料信息不完整，请先完成资料信息修改后，方可进行其他操作";
                if(otherAndSupplement === '1'){
                  _this.workCenter = "由于您的资料信息或协议不完整，请先完成资料修改并补签协议后，方可进行其他操作";
                }
                _this.titleName = "提示";
                _this.isExpireFlag = '18';
                _this.buttonOneName = "立即处理";
                return;
              }
              if (supplementStatus == '2') {
                _this.ShowDisplay = true;
                _this.workCenter = "由于您的协议需要签署，请先完成签署协议后，方可进行其他操作";
                _this.titleName = "提示";
                _this.isExpireFlag = '21';
                _this.buttonOneName = "立即处理";
                return;
              }
              sessionStorage.setItem('business_type',item.business_type);
              sessionStorage.setItem('login_type',"2");
              _this.$router.push({name: res.data.business_flow, query: {id: Math.random()}});
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
        }else if(item.accept_no.trim().substr(0,2)=="21"){
          sessionStorage.setItem('login_type',"2");
          sessionStorage.setItem('_supplementStatusAgreement', "1");//0:非补签 1:普通补签 2: 强制补签
         _this.operationBusiness(item);
        }else if(item.accept_no.trim().substr(0,2)=="31"){
          sessionStorage.setItem('login_type',"2");
          sessionStorage.setItem('_supplementStatusAgreement', "2");//0:非补签 1:普通补签 2: 强制补签
          _this.operationBusiness(item);
        }else{
          sessionStorage.setItem('login_type',"2");
          sessionStorage.setItem('_supplementStatusAgreement', "0");
          _this.operationBusiness(item);
        }
      },
      expireFlag(idcard, adeType, adeFlag,otherFlag,supplementStatus) {//1：过期 0：未完成 1：过期 1:其他资料不全 2:强制补签
        this.isExpireFlag = '';
        if (idcard == '1') {
          this.isExpireFlag = '23';
          this.ShowDisplay = true;
          this.workCenter = "由于您的身份证有效期已过期或有效期缺失，请办理完毕身份证有效期修改后，方可进行其他操作：";
          this.titleName = "提示";
          this.buttonOneName = "立即处理";
          return;
        }
        if (adeType == '0') {
          this.isExpireFlag = '24';
          this.ShowDisplay = true;
          this.workCenter = "由于您的投资者适当性状态无效，请完成投资者适当性评估后，方可进行其他操作";
          this.titleName = "提示";
          this.buttonOneName = "立即处理";
          return;
        }
        if (adeFlag == '1') {
          this.ShowDisplay = true;
          this.workCenter = "由于您的投资者适当性已过期，请完成投资者适当性评估后，方可进行其他操作";
          this.titleName = "提示";
          this.isExpireFlag = '22';
          this.buttonOneName = "立即处理";
          return;
        }
        if (otherFlag == '1') {
          this.ShowDisplay = true;
          this.workCenter = "由于您的资料信息不完整，请先完成资料信息修改后，方可进行其他操作";
          if( "1" === sessionStorage.getItem('_otherAndSupplement')){
            this.workCenter = "由于您的资料信息或协议不完整，请先完成资料修改并补签协议后，方可进行其他操作";
          }
          this.titleName = "提示";
          this.isExpireFlag = '18';
          this.buttonOneName = "立即处理";
          return;
        }
        if (supplementStatus == '2') {
          this.ShowDisplay = true;
          this.workCenter = " 由于您的协议需要签署，请先完成签署协议后，方可进行其他操作";
          this.titleName = "提示";
          this.isExpireFlag = '21';
          this.buttonOneName = "立即处理";
          return;
        }
        return true;
      },
      operationBusiness(item){
        var _this = this;
        var data = {
          "business_type":item.business_type,
          "accept_no":item.accept_no,
        }
        _this.axios.post('/myBussiness/operationBusiness',data).then(function (res) {
          if (res.data.error_no== 0) {
           /* if(item.business_type == "12"){
              sessionStorage.setItem("systype","2");
            }else{
              sessionStorage.setItem("systype","1");
            }*/
           //受理单不为空，按受理单中的账户类型处理
            sessionStorage.setItem("systype",item.accept_no.trim().substring(7,8));
            sessionStorage.setItem('business_type',item.business_type);
            _this.$router.push({name:res.data.business_flow});
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
      isNotcancel(){
        this.isShow = true;
        this.showContents = "您确定取消当前预约排队";
      },
      cancelReservation(){
        var _this = this;
        _this.axios.post('/video/cancleOrderQueue',{"accpet_no":_this.accpet_no}).then(function (res) {
          if(res.data.error_no == 0){
            _this.showReservation = true;
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
      }
    },

  }
</script>
<style lang="less">
  .myBussiness{
    background: #F6F6F6;
    min-height: 100%;
    .van-hairline--bottom.van-nav-bar {
      background: #49A5FF;
    }
    .reservation{
      width: 86%;
      margin: 0 auto;
      text-align: left;
        button{
          background: #FF9501;
          border: none;
          border-radius: 5px;
        }
    }
    .todolist_div{
      margin-top: 1.5rem;
    }
    .todolist_div{
      background: white;
      padding: 1.2rem 0;
      width: 94%;
      margin-left: 3%;
      border-radius: 12px;
    }
    .van-col-15 {
      width: 67%;
      text-align: left;
      padding-left: 3rem;
      font-size: 19px;
      height: 32px;
      line-height: 32px;
      color: black;
    }
    .van-col-9 {
      width: 33%;
      height: 32px;
      line-height: 41px;
      padding-right: 2rem;
    }
    .todolist_content{
        width: 92%;
        padding-top: 0.7rem;
        border-top:1px solid #e8dede;
        margin: 0 auto;
        margin-top: 3%;
        text-align: left;
        padding-left: 1%;
        word-wrap: break-word;
    }
    .todolist_btnValue{
      width: 19%;
      margin-top: 1rem;
      color: #49a5ff;
      border:1px solid #49a5ff;
      margin-right: 31%;
    }
    .isSuccess{
      color: green;
      margin-top: 2.5%;
    }
    .isWaiting{
      color: dodgerblue;
      margin-top: 2.5%;
    }
    .isError{
      color: red;
      margin-top: 2.5%;
    }
    .accept_no{
      font-size: 14px;
      line-height: 33px;
    }
    .van-tabs__nav-bar{
      background-color: #49a5ff;
    }
    .van-tab--active{
      color: #49a5ff;
    }
    .van-nav-bar__title{
      color: white;
    }
    .van-button{
      position: relative;
      padding: 0;
      display: inline-block;
      height: 33px;
      line-height: 33px;
      border-radius: 2px;
      -webkit-box-sizing: border-box;
      box-sizing: border-box;
      font-size: 16px;
      text-align: center;
      -webkit-appearance: none;
      -webkit-text-size-adjust: 100%;
      font-size: 14px;
    }
    .van-nav-bar__text{
      color:white;
    }
    .van-col--15{
      width: 53%;
      text-align: left;
      padding-left: 5%;
      font-size: 15px;
    }
    .van-tabs__line{
      z-index: 1;
      left: 0;
      bottom: 15px;
      height: 2px;
      position: absolute;
      border-radius: 2px 0;
      background-color: #49a5ff;
    }
    .van-tabs__wrap {
      top: 7%;
      left: 0;
      right: 0;
      z-index: 0;
      overflow: hidden;
      position: fixed;
    }
    .van-pagination{
      display: -ms-flexbox;
      display: flex;
      text-align: center;
      line-height: 40px;
      font-size: 14px;
      position: fixed;
      bottom: 9%;
      width: 90%;
      left: 5%;
    }
    .van-col--9{
      width: 41%;
      text-align: right;
    }
    .cancel {
					width: 45%;
					color: #ffffff;
					background: #49a5ff;
					text-align: center;
					line-height: 35px;
					border-radius: 4px;
					margin: 4% auto;
					margin-bottom: 12px;
					float: left;
    		}
				.hint {
					text-align: center;
					font-size: 16px;
					color: #333333;
					width: 100%;
					background: #49a5ff;
					line-height: 37px;
					color: #ffffff;
				}
				.van-popup {
					width: 80%;
				}
				.abolish{
					float: right;
				}
				.boxes_button{
					width: 53%;
					margin: 0 auto;
          padding-top: 3%;
				}
        .abandon{
          margin-left: 47%;
          //float: right;
        }
        .abandonLeft{
          margin-left: -17%;
        }

        .avtiveHeader{
          padding-top: 23%;
        }
        .van-tabs--line{
          padding-top: 0%;
        }
      @media only screen and (min-device-width : 768px) and (max-device-width : 1024px){
          .van-tab span{
            font-size: 20px;
          }
          .avtiveHeader{
            padding-top: 18%;
          }
          .van-col--15{
            font-size: 20px;
          }
          .van-col--9{
            font-size: 20px;
          }
          .todolist_content{
            font-size: 20px;
          }
          .van-button{
            font-size: 20px;
            height: 48px;
          }
          .todolist_div{
            width: 88%;
            margin-left: 6%;
          }
          .van-tabs__wrap{
            height: 60px;
            top: 4.7%;
          }
          .reservation{
            padding-top: 2%;
          }
      }
      //ipad横屏
      @media only screen and (min-width: 1349px) and (orientation : landscape){
        .avtiveHeader{
          padding-top: 8%;
        }
        .van-button{
          height: 32px;width: 9%;
        }
        .todolist_div{
          width: 74%;
          margin: 0 auto;
          height: 170px;
        }
        .todolist_btnValue{
          margin-left: 30%;
        }
        .hiddenReservation{
          padding-top: 23%;
        }
        .showReservation{
          padding-top: 1%;
        }
      }
      @media only screen and (min-device-width : 1024px) and (max-device-width : 1366px){
        .reservation{
          padding-top: 4%;
        }
      }
      .divHeight{
        height: 105px;
      }
      .changeDivHeight{
        height: 90px;
      }
  }

</style>

