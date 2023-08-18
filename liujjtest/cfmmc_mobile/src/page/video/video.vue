<template>
  <div class="queue">
    <van-nav-bar
      title="视频验证"
      right-text="关闭"
      left-text=""
      right-arrow
      left-arrow
      @click-left="onClickLeft"
      @click-right="onClickRight"/>
    <!--导航条-->
    <navigation/>
    <div class="video_img">
      <div class="queue_left queue_block">
        <img src="../../assets/Sun.svg">
        <div>光线充足</div>
      </div>
      <div class="queue_center queue_block">
        <img src="../../assets/Calendar.svg">
        <div>工作日</div>
      </div>
      <div class="queue_right queue_block">
        <img src="../../assets/IDcard.svg">
        <div>相关证件</div>
      </div>
    </div>
    <van-button  bottom-action @click="startVideo" class="nextBtn">开始视频通话</van-button>
    <div class="videoTitle">您将和客服人员进行视频通话,客服人员将对您进行身份确认。</div>
    <div style="width: 85%;font-size: 14px;text-align: left;margin: 0 auto;padding-top: 17px;" v-if="reservationFlag">
      &emsp;&emsp;若当前排队人数过多，您也可点击“<span style="color:blue">预约视频</span>”进行预约；
      预约视频可预约<span style="color:blue">明日起近30日内</span>的指定时间段，您在预约时间段内登录开户云，可优先为您接通视频。
      <span style="margin-left:0px;cursor: pointer;font-size:12pt;color:blue" @click="reservationVideo"><u>预约视频</u></span>
    </div>
    <div v-show="reservationFlag && reservationShow" ref="reservationRef">
      <p class="reservation">预约日期:</p>
      <div class="formDiv">
        <group>
        <datetime class="datetimeLeft" :start-date="startTime"  :end-date="endTime" v-model="reservationTime" placeholder="请选择" @on-confirm="reservationToast">
        </datetime>
        </group>
      </div>
      <div class="appointmentInfo" v-if="appointment">
        <p>上午: 已约<span style="color: #b00000;font-weight:bold;"> {{appointment_SAM}} </span>人 可约<span style="color: #2b542c;font-weight:bold;"> {{appointmentAM}} </span>人</p>
        <p style="margin-top: 3%;">下午: 已约<span style="color: #b00000;font-weight:bold;"> {{appointment_SPM}} </span>人 可约<span style="color: #045408;font-weight:bold;"> {{appointmentPM}}</span>人</p>
      </div>
      <p class="reservation">预约时段:</p>
      <van-radio-group v-model="orderTime">
        <van-radio name="0" :disabled="disabledAM">上午</van-radio>
        <van-radio name="1" :disabled="disabledPM">下午</van-radio>
      </van-radio-group>
      <van-button bottom-action @click="isNotReservation" class="nextBtn reservationBtn">预约视频</van-button>
    </div>
    <div v-if="showCancelReservation">
      <p class="cancelReservation">预约视频时间：</p>
      <p class="cancelReservation reservationTime">{{hasReservationTime}}</p>
      <van-button  bottom-action @click="isNotCancelReservation" class="nextBtn reservationBtn cancelButton">取消预约</van-button>
    </div>
    <!-- <van-button  bottom-action @click="successClose" class="nextBtn">测试跳过</van-button> -->
    <!--排队弹框-->
    <van-popup style="width:80%; margin: 0 auto;" v-model="queueUp" :close-on-click-overlay="false">
       <div class="hint">提示</div>
       <div style="margin-top: 15px;padding-bottom: 15px;text-align: left;margin-left: 6%;">当前排队人数共 <span>{{number}}</span>人,您排在第 <span class="morderCalss">{{morder}}</span>位</div>
       <button class="cancel" @click="cancelqueue">取消排队</button>
     </van-popup>
    <!--失败弹框-->
    <van-dialog
        v-model="fail"
        :showConfirmButton="false"
        :showCancelButton="false"
      >
        <div class="success_tips">提示</div>
        <div style="margin: 20px 0 10px 0px">很抱歉!您的视频见证未通过,原因:</div>
        <div class="reason_list">
          <div>{{reject}}</div>
        </div>
        <div class="cancel" @click="redoMethod">确定</div>
      </van-dialog>
    <!--排队0人-->
    <van-popup style="width:80%; margin: 0 auto;" v-model="queue" :close-on-click-overlay="false">
      <div class="hint">提示</div>
      <div style="margin-top: 15px;padding-bottom: 15px;text-align: left;margin-left: 6%;">由于网络不稳定，视频连接异常，请重新进入视频</div>
      <button class="cancel" @click="reVedio">重新视频</button>
    </van-popup>
    <!--成功弹窗-->
    <van-dialog
      v-model="success"
      :showConfirmButton="true"
      :confirmButtonText="confirmText"
      :beforeClose='successClose'
      :closeOnClickOverlay="false"
    >
      <div class="success_tips">提示</div>
      <div style="margin-top:15px;padding-bottom: 15px; margin-left: 6%;text-align: left;">视频验证通过,请进行下一步！</div>
      </van-dialog>
    <!--后他刷新，重新开始弹窗-->
    <van-dialog
      v-model="redoData"
      :showConfirmButton=false
      :showCancelButton=true
      :cancelButtonText="txt_redo"
      :beforeClose='redoClose'
      :closeOnClickOverlay="false"
      >
      <div class="success_tips">提示</div>
      <div style="width: 84%;text-align: left;margin: 6% auto;">视频中断,请重新开始视频</div>
      </van-dialog>
    <van-popup v-model="specification" class="vanPopup">
       <div>
        <div class="specification">—&nbsp;拍摄规范&nbsp;—</div>
          <ul v-for="(img,index) in video_imgs" :key="index">
            <li class="li-img">
              <img class="img1" :src="'static/shootSpecification/'+img.status+'.svg'" :name='img.status'/>
              <img class="img2" :src="'static/shootSpecification/'+img.name+'.png'" :name='img.name'/>
              <span v-html="img.description" class="word"></span>
            </li>
          </ul>
          <button class="IKonw" @click="iKnow">我知道了</button>
        </div>
    </van-popup>
    <popUp :tips="error_content" :showBox="show_content" @notarize="notarize"></popUp>
    <quit :content="content" :quit="quit"></quit>
    <confirm :showContents="showContents" :isShow="isShow" :yesButton="yesButton" :noButton="noButton" @ensure="ensureReservation" @cancel="cancelReservation"></confirm>
    <waitPop :loadingbox="loadingbox"></waitPop>
  </div>
</template>
<script type="text/ecmascript-6">
import WaitPop from "../../components/waitPop.vue";
import Quit from "../../components/quit.vue";
import PopUp from '../../components/popUp.vue';
import Utils from '../../util/iphoneOrAndroid';
import Navigation from "../../components/navigation.vue";
import {Datetime,Group} from 'vux';
import Confirm from "../../components/confirm.vue";
export default {
  components: {
      PopUp,
      Quit,
      WaitPop,
      Navigation,
      Datetime,
      Group,
      Confirm
  },
  data(){
    return{
      appId:"",
      dwPriority:"",
      anychatAppId:"",
      loadingbox: false,
      quit:false,
      content:"",
      error_content:"",
      show_content:false,
      queueUp:false,
      queue:false, //排队显示0人
      sn:"",
      accept_no:"",
      txt_redo:'重新视频',
      reject:'',
      redoData: false,
      txt:'取消排队',
      morder:'',
      number:'',
      agentName :'', //视频用户
      queueid :'',  //视频队列编号
      hallbuinessNum : '', //视频服务区编号
      vCallback:'window.videoCallback',//视频回调
      ip:'',
      port:'',
      num:1,
      fail: false,
      show:false,
      success: false,
      confirmText:'下一步',
      cancel:'取消排队',
      // redo:'重新开始',
      orderTime:"",//预约时间
      reservationTime:"", //预约日期
      startTime:"",
      endTime:"",
      reason:[{name:'身份证上传有误！'},{name:'银行卡信息有误！'}],
      sigStr: "",
      timestamp:"",
      specification:true,
      showContents:"",
      isShow:false,
      yesButton:"确定",
      noButton:"取消",
      showCancelReservation:false,
      hasReservationTime:"",
      isReservation:false,//true 表示 预约排队  false 取消预约
      succuseReservation:false, //true 预约成功    // false  预约失败
      disabledAM:false,//上午可选 true  不可选
      disabledPM:false,//下午可选  true  不可选
      appointmentAM:0,//上午可预约
      appointmentPM:0,//下午可预约
      appointment_SAM:0,//上午已预约
      appointment_SPM:0,//下午已预约
      appointment:false,//预约信息不显示
      reservationFlag: false, // 预约排队配置
      reservationShow: false,
      video_imgs:[
        {name:"video1",description:"1.</br>请确保正对镜头</br>使脸部完整露出",status:"done"},
        {name:"video2",description:"2.</br>请保持镜头平稳</br>避免镜头的晃动",status:"failed"},
        {name:"video3",description:"3.</br>请保持光线充足</br>避免太强或太弱",status:"failed"}
      ],
      outSystem: false,
      //国密
      is_gm:'',
      gm_username:'',
      enable_gm:"",
      queueFlag:0,
      timer:''
    }
  },
  created(){
    var _this = this;
   //注册回调
    window['videoCallback'] = function(type,param){
      _this.videoCallback(type,param);
    };
    window['anychatVerCallback'] = function(type,param){
      _this.anychatVerCallback(type,param);
    };
    window['videoCallback_iphone'] = function(form, param_1,param_2){
      _this.videoCallback_iphone(form, param_1,param_2);
    };
    window['checkCert'] = function(type,param){
      _this.checkCert(type,param);
    };
    // window['gmcheckCert'] = function(type,param){
    //   _this.gmcheckCert(type,param);
    // };
     window['gmCallback'] = function (status, result,err) {
        _this.gmCallback(status, result,err);
      };
    window['checkNet'] = function(param){
      _this.checkNet(param);
    };
    window['videoCallbackHs'] = function(type,param){
      _this.videoCallbackHs(type,param);
    };
    // 回调结束
    // 开始
    var data = {
      "business_type":sessionStorage.getItem('business_type'),
    };
    this.axios.post('/flow/qryAcceptInfo',data).then(function (res) {
        if(res.data.error_no == 0){
            _this.accept_no =res.data.accept_no;
            _this.business_flow=res.data.business_flow;
            _this.is_gm = res.data.is_gm;
            if(_this.$router.history.current.name != res.data.business_flow){
                _this.$router.push(res.data.business_flow)
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
    // 结束
    //获取预约排队信息
    _this.axios.post('/video/qryOrderQueue',{}).then(function (res) {
      if(res.data.error_no == 0){
        var queue_status = res.data.queue_status;
        _this.reservationFlag = queue_status == "1" ? true : false;
        if(queue_status == "1"){
          var date = new Date();
          date.setDate(date.getDate()+parseInt(1));//预约只能从T+1开始
          var month  = date.getMonth()+1;
          if(month < 10){
            month = "0" + month;
          }
          var day = date.getDate();
          if(day<10){
            day = "0"+day;
          }
          _this.startTime = date.getFullYear()+"-"+month+"-"+day;
          //_this.reservationTime = _this.startTime;
          date.setDate(date.getDate()+parseInt(29));
          month = date.getMonth()+1;
          if((date.getMonth()+1) < 10){
            month = "0" + (date.getMonth()+1);
          }
          day = date.getDate();
          if(day<10){
            day = "0"+day;
          }
          _this.endTime = date.getFullYear()+"-"+month+"-"+day;
          if(res.data.queueMap){
            _this.showCancelReservation = true;
            var orderTime = "";
            if(res.data.queueMap.order_time == "0"){
              orderTime = "上午";
            }else{
              orderTime = "下午";
            }
            _this.hasReservationTime = res.data.queueMap.reservation_time+orderTime;
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
  // 

  methods:{
    queueHeartbeat() {
      var _this = this;
          _this.$ajax.post('/video/queueHeartbeat',{}).then(function () {
          }).catch(function (err) {
              console.log(err)
          });
        
    },
    reservationVideo(){
      this.reservationShow = true;
      this.$refs.reservationRef.scrollIntoView({behavior: "smooth"})
    },
    checkNet(param){
      if(param == "0"){
        this.loadingbox = false;
      }
    },
    notarize(){
        this.show_content = false;
        if(this.succuseReservation){ //预约成功后退出系统
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
        if(this.outSystem){
          this.$router.push({name: "Indexselection"});
        }
      },
    onClickLeft(){
          var _this = this;
          if(_this.accept_no != "" && _this.accept_no != undefined){
          var data = {
              "accept_no": _this.accept_no,
              "business_type":sessionStorage.getItem('business_type'),
              "business_button_type":'0', //上一步 按钮类型
              "business_flow": _this.$router.history.current.name,
              "accept_status":""   //"accept_status":_this.accept_status,当需要更改状态时再传值
          };
          _this.loadingbox = true;
          _this.$ajax.post('/flow/updateAccept',data).then(function (res) {
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
          }).catch(function (err) {
              console.log(err)
          });
          }
      },
    onClickRight(){
      this.$router.push({name:'MyBussiness'})
    },
    //恒生队列
    //恒生排队
    videoCallbackHs(type,param){//视频回调
     let browserVersion = Utils.iphOrAnz();
     console.log(param,'param')
      switch(type){
        
        case 0 :
              if (browserVersion == "iphone") {
                    // jsHandle.queueHs("1","videoCallbackHs",this.userInsert);
                    window.location.href = "objc://queueHs/?"+"1"+"?"+"videoCallbackHs?"+this.userInsert+"?";
                console.log("iphone");
              } else if (browserVersion == "anzuo") {
                 jsHandle.queueHs("1","videoCallbackHs",this.userInsert);
              }
          break;
        case  1 : 
          //排队结果
          // let params = JSON.stringify(param)
          this.morder = param.self;
          this.number = param.total;
          if(this.number === 0){
            this.queue = true
          }else{
            this.queueUp = true;
            this.videoQueueRecord();
          }
          break;
        case  2 :
          //退出队列
          this.queueUp = false;
          this.queue = false; 
          break;
        case  3 :
          //视频呼叫

          break;
        case 4 :
          break;
        case 5 :
          //进入房间
          this.queueUp = false;
          this.queue = false;
          
          
          break;
        case 6 :
          //开始视频
          this.queueUp = false;
          this.queue = false
          break;
        case 7 :
          //视频结束
          this.queueUp = false;
          this.queue = false
          if(param == 1){
            this.getRejct();
          }else if(param == 0){
            //下一步
            this.success = true;
          }else{
            //重新视频弹框
            this.redoData = true;
            this.queueUp = false;
            this.queue = false
            this.fail = false;
            this.success = false;
          }
          break;
        case 8 :
          this.show_content = true;
          this.error_content = param;
          break;
        case 9 :
          this.queueUp = false;
          this.queue = false;
          this.show_content = true;
          this.error_content ='连接异常了请重新连接';
          
        break;
      }
    },
    startVideoAll(wsUrl) {
       
        var _this = this;
        _this.loadingbox = true;
        if(_this.queueFlag == 1){
          _this.loadingbox = false;
            let browserVersion = Utils.iphOrAnz();
            if (browserVersion == "iphone") {
                
                window.location.href = "objc://queueHs/?"+"0"+"?"+"videoCallbackHs?"+wsUrl+"?";
                // jsHandle.queueHs("0","videoCallbackHs",res.data.wsUrl);
            } else if (browserVersion == "anzuo") {
              jsHandle.queueHs("0","videoCallbackHs",wsUrl);
            }

          }
        
       
    },
    startVideo(){//开始排队
   
      var _this = this;
     _this.timer = setInterval(()=>_this.queueHeartbeat(),1000 * 60 * 10)
      _this.loadingbox = true;
      setTimeout(function () {
            var browserVersion = Utils.iphOrAnz();
            if (browserVersion == "iphone") {
              console.log("iphone");
            } else if (browserVersion == "anzuo") {
              jsHandle.checkNet("checkNet");
            } else if (browserVersion == "windows") {
              console.log("windows");
            }
        // jsHandle.checkNet("checkNet");
      },15000);
    //获取队列
   
      _this.$ajax.post('/video/qryQueueInfo',{'date':new Date().getTime(),"business_type":sessionStorage.getItem('business_type'),'systype':sessionStorage.getItem('systype'),'accept_no': _this.accept_no}).then(function (res) {
        _this.loadingbox = false;
        var versionCode = sessionStorage.getItem('versionCode');
        
        if(res.data.queue_flag == 1) {
            if (parseInt(versionCode) < 20230501) {
              _this.quit = true;
              _this.content = "当前版本不支持此功能，请更新版本";
              return
            }
            _this.queueFlag = res.data.queue_flag;
            _this.startVideoAll(res.data.wsUrl);
            _this.userInsert = res.data.user;
          return
        }
      
        if(res.data.error_no == 0){
            _this.anychatAppId = res.data.anychatAppId;
            _this.agentName = res.data.agentName;
            _this.hallbuinessNum= res.data.hallbuinessNum+"";
            _this.queueid = res.data.queueNo+"";
            _this.ip = res.data.anychatServerAddr;
            _this.port = res.data.anychatServerPort;
            _this.sigStr = res.data.sigStr;
            _this.timestamp = res.data.timestamp;
            _this.chatInfo()
        }else if(res.data.error_no == "-9999"){
            //_this.loadingbox = false;
            _this.quit = true;
            _this.content = res.data.error_info;
        }else{
          // _this.loadingbox = false;
            _this.show_content = true;
            _this.error_content = res.data.error_info;
        }
    //   console.log("获取参数： agentName="+_this.agentName + " hallbuinessNum="+_this.hallbuinessNum
    // 	  + " queueid="+_this.queueid+ " ip="+_this.ip+ " port="+_this.port)
    });

  //客户优先级
  
   
  },
  chatInfo(){
    let _this = this;
    _this.loadingbox = true;
    _this.$ajax.post('/video/getAnychatInfo',{'date':new Date().getTime(),"business_type":sessionStorage.getItem('business_type'),'systype':sessionStorage.getItem('systype')}).then(function (res) {
      _this.loadingbox = false;
      if(res.data.error_no == 0){
           _this.agentName = res.data.agentName;
           _this.anychatAppId = res.data.anychatAppId;
           _this.appId = res.data.appId;
           _this.dwPriority = res.data.dwPriority;
           _this.sigStr = res.data.sigStr;
           _this.timestamp = res.data.timestamp;
           _this.IsTrade();
      }else if(res.data.error_no == "-9999") {
          // _this.loadingbox = false;
           _this.quit = true;
           _this.content = res.data.error_info;
       }else{
          _this.loadingbox = false;
          _this.show_content = true;
          _this.error_content = res.data.error_info;
      }
    });
  },
    IsTrade(){
      var _this = this;
      _this.loadingbox = true;
      _this.$ajax.post('/video/isTrade', {"business_type": sessionStorage.getItem('business_type')}).then(function (res) {
      _this.loadingbox = false;
      if (res.data.error_no == 0) {
        _this.videoQueueRecord();
        if (_this.queueFlag == 1) {
          _this.loadingbox = false;
            return;
        }
        var browserVersion = Utils.iphOrAnz();
        if (browserVersion == "iphone") {
          //console.log("iphone");
          var id_type = "0";
          var ip = _this.ip;
          var port = _this.port;
          var agentName = _this.agentName;
          var hallbuinessNum = _this.hallbuinessNum;
          var queue = _this.queueid;
          var anychatAppId = _this.anychatAppId;
          var timestamp = _this.timestamp;
          var sigStr = _this.sigStr;
          var dwPriority = _this.dwPriority;
          window.location.href = "objc://startVideo_5/?"+id_type+"?"+ip+"?"+port+"?"+agentName+"?"+hallbuinessNum+"?"+"videoCallback_iphone?"+queue+"?"+anychatAppId+"?"+timestamp+"?"+sigStr+"?"+dwPriority+"?";
        } else if (browserVersion == "anzuo") {
            //console.log("anzuo");
            jsHandle.videoConnect("0",_this.vCallback,_this.ip,_this.port,_this.agentName,_this.hallbuinessNum,_this.anychatAppId,_this.timestamp,_this.sigStr,_this.dwPriority);
        } else if (browserVersion == "windows") {
            //console.log("windows");
        }
      } else if (res.data.error_no == '-9999') {
          _this.quit = true;
          _this.content = res.data.error_info;
      } else if(res.data.error_no == -1098){
          _this.loadingbox = false;
          _this.outSystem = true;
          _this.show_content = true;
          _this.error_content = res.data.error_info;
      }else {
          _this.show_content = true;
          _this.error_content = res.data.error_info;
      }
    }).catch(function (error) {
        console.log("err:"+error);
      });
    },
    //视频
    videoQueueRecord() {
      var _this = this;
      this.$ajax.post('/video/saveUserVideoNotVerified', {
        'accept_no': _this.accept_no,
        'business_type': sessionStorage.getItem('business_type')
      }).then(function (res) {
        if (res.data.error_no == 0) {
          console.log('video.vue insert videoQueueRecord sucess!!' + new Date());
        } else if (res.data.error_no == '-9999') {
          _this.quit = true;
          _this.content = res.data.error_info;
        } else {
          _this.show_content = true;
          _this.error_content = res.data.error_info;
        }
      }).catch(function (error) {
        console.log("err:"+error);
      });
    },
    anychatVerCallback(type,param){//anychat版本号
      this.axios.post('/video/anychatVersion',{"log_info":param,"accept_no":this.accept_no,"business_type":sessionStorage.getItem('business_type')}).then(function (res) {
      })
    },
    videoCallback(type,param){//视频回调
      switch(type){
        case 0 :
          //视频服务器登录成功
          //调用安卓 ios windows方法
          if(param == "0"){
            jsHandle.video("1",this.vCallback,this.queueid);
          }else{
            this.loadingbox = false;
            this.show_content = true;
            this.error_content = "视频服务器连接失败";
          
            
          }
          break;
        case  1 :
          //排队结果
          this.loadingbox = false;
          this.morder = param.morder;
          this.number = param.number;
          if(this.number === 0){
            this.queue = true
          }else{
            this.queueUp = true;
          }
          break;
        case  2 :
          //退出队列
          this.queueUp = false;
          this.queue = false
          break;
        case  3 :
          //视频呼叫
          break;
        case 4 :
          break;
        case 5 :
          //进入房间
          break;
        case 6 :
          //开始视频
          this.queueUp = false;
          this.queue = false
          break;
        case 7 :
          //视频结束
          this.queueUp = false;
          this.queue = false
          if(param == 1){
            this.getRejct();
          }else if(param == 0){
            //下一步
            this.success = true;
          }else{
            //重新视频弹框
            this.redoData = true;
            this.queueUp = false;
            this.queue = false
            this.fail = false;
            this.success = false;
          }
          break;
        case "8" :
          _this.show_content = true;
          _this.error_content = param;
          break;
      }
  },
    //IOS回调
    videoCallback_iphone(form, param_1,param_2){//视频回调
    switch(form){
      case "0" :
        //视频服务器登录成功
        //调用安卓 ios windows方法
        var browserVersion = Utils.iphOrAnz();
        if (browserVersion == "iphone") {
          console.log("iphone");

        } else if (browserVersion == "anzuo") {
          console.log("anzuo");
          // jsHandle.video("1","window.videoCallback",this.queueid);
        } else if (browserVersion == "windows") {
          console.log("windows");
        }
        break;
      case "1" :
        //排队结果
        this.loadingbox = false;
        this.number = param_1;
        this.morder = param_2;
        if(this.number === 0){
          this.queue = true
        }else{
          this.queueUp = true;
        }
        break;
      case  "2" :
        //退出队列
        this.queueUp = false;
        this.queue = false
        break;
      case  "3" :
        //视频呼叫
        break;
      case "4" :
        break;
      case "5" :
        //进入房间
        break;
      case "6" :
        //开始视频
        this.queueUp = false;
        this.queue = false
        clearInterval(this.timer)
        break;
      case "7" :
        //视频结束
        this.queueUp = false;
        this.queue = false
        //   alert(param);//判断打回还是通过
        if(param_1 == 1){
          this.getRejct();
        }else if(param_1 == 0){
          //下一步
          this.success = true;
        }else{
          //重新视频弹框
          this.redoData = true;
          this.queueUp = false;
          this.queue = false
          this.fail = false;
          this.success = false;
          break;
      }
    }
  },
    //取消排队
    cancelqueue(action, done){
      let _this = this;
      let browserVersion = Utils.iphOrAnz();
      
      if (_this.queueFlag == 1) {
           
            if (browserVersion == "iphone") {
              window.location.href = "objc://queueHs/?"+"2"+"?"+"videoCallbackHs?"+ _this.userInsert+"?";
              console.log("iphone");

            } else if (browserVersion == "anzuo") {
                jsHandle.queueHs("2","videoCallbackHs",_this.userInsert);
            }
          
          
      } else {
           //调用安卓 ios windows方法
     
        if (browserVersion == "iphone") {
            console.log("iphone");
            var id_type = "2";
            var queue = this.queueid;
            window.location.href = "objc://cancel_queue_5/?"+id_type+"?"+"videoCallback_iphone?"+queue+"?";
        } else if (browserVersion == "anzuo") {
            console.log("anzuo");
            jsHandle.video("2","window.videoCallback",this.queueid)
        } else if (browserVersion == "windows") {
            console.log("windows");
        }

        done()
      }  
    },
    getRejct(){
    var _this = this;
    this.$ajax.post('/video/qryVideoStatus',{'date':new Date().getTime(),"business_type":sessionStorage.getItem('business_type')}).then(function (res) {
      if(res.data.accept_status == '7'){  //驳回
      _this.reject=res.data.video_remark;
      _this.queueUp = false;
      _this.fail = true;
              // _this.redo = "重新开始";
    }else{
      _this.fail = false;
      _this.queueUp = false;
          //    _this.redo = false;
          }
    }).catch(function (error) {
    console.log(error)
    });
      },
    redoMethod(){
    //失败打回的框
    this.fail = false;
    //   this.redo = false;
    if(this.reject == '用户中断'){
     //this.startVideo();
      this.redoData = false;
    }else{
      this.nextMethod('1', '2');
    }
    //done();
    /* 直接跳转页面
    *"business_button_type":'2',//下一步
    **/
  },
    successClose(){
    /*正常下一步*/
    var _this = this;
    //视频正常通过，取消预约
    _this.cancelReservationService();
    //视频通过的框
    _this.$ajax.post('/cert/getSn',{"business_type":sessionStorage.getItem("business_type")}).then(function (res) {
      _this.sn = res.data.ca_sn;
      _this.enable_gm = res.data.enable_gm;
      _this.gm_username = res.data.gm_username;
     
      //调用安卓 ios windows方法
      var browserVersion = Utils.iphOrAnz();
      const Base64 = require('js-base64').Base64;
      // var browserVersion = Utils.iphOrAnz();
      if (_this.enable_gm == '1') {
          let obj = {
            status:"1",
            callback:"gmCallback",
            gmUserName:res.data.gm_username,
            appId:res.data.gm_app_id,
            base64HttpsCert:res.data.gm_mAuth_base64_cert,
            mAuthUrl:res.data.gm_mAuth_url,
            appSecret:res.data.gm_secret_key,
         }
          //调用安卓 ios windows方法
          if (browserVersion == "iphone") {
              window.location.href = "objc://certGM/?"+ Base64.encode(JSON.stringify(obj));
          } else if (browserVersion == "anzuo") {
              console.log("anzuo");
              jsHandle.certGM(JSON.stringify(obj))
          } else if (browserVersion == "windows") {
              console.log("windows");
          }
      } else {
        if(_this.sn == '' || _this.sn == undefined){
            _this.nextMethod('0','2');
            return;
          }
        if (browserVersion == "iphone") {
          console.log("iphone");
          window.location.href = "objc://callIOSCheckLocalSn_5/?"+_this.sn+"?checkCert";
      } else if (browserVersion == "anzuo") {
          console.log("anzuo");
          jsHandle.certCheck("3","checkCert",_this.sn)
      } else if (browserVersion == "windows") {
          console.log("windows");
      }
      }
      
      
    });
  },
  
    redoClose(action,done){
    //刷新中断的框
    // this.redo = false;
    this.redoData = false;
    done();
    // this.startVideo();
  },
    reVedio(action, done){
    //取消排队
    //this.cancelqueue(action, done);
    this.queue = false;
  },
    beforeClose(){

  },
   //国密
      gmCallback(status,result,err) {
          let _this = this;
          
          switch(status) {
            case '1'://检测证书
              // let stepFlag = "2";
              console.log('证书检测结果'+ result)
              if (result == '0') {
                //  stepFlag = "1";
                _this.nextMethod('0','1');
              } else {
                _this.nextMethod('0','2');
              }
              break;
            
          }
      },
    checkCert(status,result){
    var stepFlag = "2";
    //检测证书是否存在
    if(result == '0'){
      stepFlag = "1";
    }
    this.nextMethod('0',stepFlag);
  },
    nextMethod(datatype, stepFlag){
      var business_button_type = "1";
      if(datatype == '1'){  //驳回
          business_button_type = "2";
      }
      var _this= this;
      var data = {
        "accept_no": _this.accept_no,
        "business_type": sessionStorage.getItem('business_type'),
        "business_flow": _this.$router.history.current.name,
        "certificate_sign": stepFlag,
        "business_button_type": business_button_type,//下一步
        "accept_status":""     //"accept_status":_this.accept_status,当需要更改状态时再传值
      };
      _this.$ajax.post('/flow/updateAccept',data).then(function (res) {
        // if(res.data.business_flow == "Video"){
        //   _this.showDiv = true;
        // }else{
        //   _this.showDiv = false;
        // }
        if(res.data.error_no == 0){
           _this.$router.push({ name:res.data.business_flow});
        }else if(res.data.error_no == "-9999"){
          _this.quit = true;
          _this.content = res.data.error_info;
        }
      });
    },
    isNotReservation(){
      if(this.reservationTime.trim() == ""){
        this.show_content = true;
        this.error_content = "请选择您需要预约的日期!";
        return;
      }
      if(this.orderTime.trim() == ""){
        this.show_content = true;
        this.error_content = "请选择您需要预约的时间!(上午/下午)";
        return;
      }
      this.isShow = true;
      this.isReservation = true;
        var order = "";
        if(this.orderTime=="0"){
          order = "上午";
        }else if(this.orderTime=="1"){
          order = "下午";
        }
      this.showContents = "您的预约排队日期为"+this.reservationTime+order+"，确定么？ ";
    },
    ensureReservation(){
      this.isShow = false;
      if(this.isReservation){
        this.reservationService();//预约
        return;
      }
      this.cancelReservationService();//取消预约
    },
    cancelReservation(){
      this.isShow = false;
      this.isReservation =false;
    },
    reservationService(){
      var _this = this;
      var data = {
        "reservation_time":_this.reservationTime,
        "business_type": sessionStorage.getItem('business_type'),
        "accept_no": _this.accept_no,
        "order_time":_this.orderTime
      };
      _this.loadingbox = true;
      _this.$ajax.post('/video/saveOrderQueue',data).then(function (res) {
        _this.loadingbox = false;
        if(res.data.error_no == 0){
          var order = "";
          if(_this.orderTime=="0"){
            order = "上午";
          }else if(_this.orderTime=="1"){
            order = "下午";
          }
          _this.succuseReservation = true;  //预约成功
          _this.error_content = "尊敬的客户，您的视频预约已成功，预约时间为"+_this.reservationTime+"("+order+")"+"，请在此段时间内登录开户云平台进行视频见证。";
          _this.show_content = true;
        }else if (res.data.error_no == '-9999') {
          _this.quit = true;
          _this.content = res.data.error_info;
        } else {
          _this.succuseReservation = false;  //预约失败
          _this.error_content = res.data.error_info;
          _this.show_content = true;
        }
      }).catch(function (error) {
        console.log(error)
      });

    },
    iKnow(){
      this.specification = false;
    },
    isNotCancelReservation(){
      this.isShow = true;
      this.isReservation = false;
      this.showContents = "确定取消预约排队么？ ";
    },
    cancelReservationService(){
      var _this = this;
      _this.axios.post('/video/cancleOrderQueue',{"accpet_no":_this.accpet_no}).then(function (res) {
        if(res.data.error_no == 0){
          _this.showCancelReservation = false;
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
    reservationToast(value){
      var _this = this;
      var data = {
        reservation_time:value,
        check_order_number:"2", //1:获取上午或下午，需传order_time参数    2:获取全部（上午和下午分开返回）
        business_type:sessionStorage.getItem("business_type")
      }
      _this.axios.post('/video/qryOrderQueue',data).then(function (res) {
        if(res.data.error_no == 0){
          _this.appointment = true;
          //当前上午已经预约的人数  配置上午可预约人数
          _this.appointment_SAM = res.data.appointment_SAM;
          var appointment_TAM = res.data.appointment_TAM;
          //当前下午已经预约的人数  配置下午可预约人数
          _this.appointment_SPM = res.data.appointment_SPM;
          var appointment_TPM = res.data.appointment_TPM;

          _this.disabledAM = false;
          if(parseInt(_this.appointment_SAM) >= parseInt(appointment_TAM)){
            _this.disabledAM = true;
          }
          _this.disabledPM = false;
          if(parseInt(_this.appointment_SPM) >= parseInt(appointment_TPM)){
            _this.disabledPM = true;
          }
          _this.appointmentAM= _this.disabledAM?0:(parseInt(appointment_TAM)-parseInt(_this.appointment_SAM));
          _this.appointmentPM= _this.disabledPM?0:(parseInt(appointment_TPM)-parseInt(_this.appointment_SPM));
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
     skipVideo(){
       var data = {
         "accept_no": this.accept_no,
        "business_type": sessionStorage.getItem("business_type"),
         "business_button_type": '1',//上一步
         "business_flow": this.$router.history.current.name,
         "accept_status": ""
       };
       var _this = this;
      this.$ajax.post('/flow/updateAccept', data).then(function (res) {
         if (res.data.error_no == 0) {
           _this.$router.push({name: res.data.business_flow});
         } else if (res.data.error_no == "-9999") {
           _this.quit = true;
          _this.content = res.data.error_info;
         } else {
          _this.show_content = true;
           _this.error_content = res.data.error_info;
         }
       }).catch(function (err) {
         console.log(err)
       });
     }
  }
}
</script>

<style lang="less">
  .queue{
    overflow:auto;
    background: #F6F6F6;
    height: 100%;
    .videoTitle {
        width: 85%;
        font-size: 14px;
        color: #acacb1;
        text-align: left;
        margin: 0 auto;
        padding-top: 17px;
    }
    .video_img{
        overflow: hidden;
        margin-left: 8%;
        margin-top: 2.5rem;

    }
    .queue_block{
        float: left;
        background: white;
        width:30%;
        height:10rem;
        margin-left: 2px;
        padding-top: 1rem;
    }
      .queue_left{
          border-bottom-left-radius: 8px;
          border-top-left-radius: 8px;
      }
      .queue_right{
          border-bottom-right-radius: 8px;
          border-top-right-radius: 8px;
      }
      .queueIcon{
          font-size:36px;
          color: #49a5ff;
      }
      .nextBtn{
          background: #49a5ff;
          margin-top: 3rem;
          width: 85%;
          margin: 0 auto;
          margin-top: 5%;
      }
      .loading{
          margin: 10px auto;
      }
      .van-hairline.van-dialog__content {
          height: 8rem;
      }
      .reason_list{
          font-size: 15px;
          padding-bottom: 20px;
          width: 79%;
          text-align: left;
          margin: 0 auto;
          word-wrap : break-word;
      }
      .success_tips{
          background: #49a5ff;
          height: 40px;
          line-height: 40px;
          color: white;
      }
      .morderCalss{
          color: red;
          font-size: 20px;
          font-weight: 600;
      }
      .button{
          width: 80%;
          height: 40px;
          color: white;
          background: #49a5ff;
          text-align: center;
          line-height: 40px;
          margin: 0 auto;
      }
       .hint{
        text-align: center;
        font-size: 16px;
        color:#333333;
        width:100%;
        background:#49a5ff;
        line-height: 37px;
        color:#ffffff;
    }
     .cancel{
        width: 30%;
        color:#ffffff;
        background: #49a5ff;
        text-align: center;
        line-height: 35px;
        border-radius: 4px;
        margin: 0 auto;
        margin-bottom: 12px;
        border: none;
    }
    .van-dialog .van-button{
        width: 34%;
        height: 40px;
        background: #49a5ff;
        line-height: 33px;
        color: white;
        margin-bottom: 4%;
    }
    [class*=van-hairline]{
        // position: relative;
    }
    .van-radio{
      text-align: left;
      margin-left: 13%;
      font-size: 18px;
      line-height: 45px;
    }
    .van-radio__input{
      font-size: 29px;
    }


    .datetimeLeft{
      padding-top: 11px;
      width:100%;
      .vux-cell-placeholder{
        float: left;
      }
    }
    .formDiv{
      width: 85%;
      height: 40px;
      margin-left: 8%;
    }
    .reservation{
      height: 31px;
      text-align: left;
      padding-top: 5%;
      text-indent: 30px;
      font-size:18px;
    }
    .vux-cell-value{
      font-size: 18px;
      color:black;
      float: left;
    }
    .van-radio-group{
      background-color: white;
      width: 85%;
      margin-left: 8%;
      margin-top: 2%;
    }
    .weui-cell_access .weui-cell__ft:after{
      display: none;
    }
    .vux-no-group-title {
      margin-top: 2%;
    }
    .reservationBtn{
     margin-bottom: 5%;
    }
   .img1{
      height:  20px;
      width: 20px;
      float: left;
      padding-top: 14%;
      padding-left: 30px
    }
    @media only screen and (min-width: 1349px) and (orientation : landscape){
      .reservation{
        text-indent: 8%;
      }
      .van-radio{
        margin-left: 2%;
      }
      .img1{
         padding-top: 2%;
       }
    }
     .IKonw{
      width: 119px;
      height: 40px;
      border: 1px solid #FFFFFF;
      border-radius: 3px;
      // font-family: PingFangSC-Regular;
      font-size: 16px;
      color: #FFFFFF;
      text-align: center;
      line-height: 16px;
      background: none;
      margin-top: 11%;
      position: fixed;
      bottom: 4%;
      left: 33%;
    }
     @media only screen and (min-device-width : 768px) and (max-device-width : 1024px){
       .img1{
         padding-top: 7%;
       }
      .IKonw{
        left: 43%;
      }
      // .nextBtn{
      //   width: 28%;
      // }
     }
    //  @media only screen and (min-device-width : 375px) and (max-device-width : 667px){
    //   .IKonw{
    //     left: 33%;
    //   }
    // }
    .specification{
      // ont-family: PingFangSC-Regular;
      font-size: 18px;
      color: #FFFFFF;
      letter-spacing: 1.82px;
      padding-bottom: 15px;
      padding-top: 30px;
    }
    .li-img{
      width: 82%;
      height: auto;
      margin: 35px auto;
    }
    .img2{
      height:  102px;
      width: 78px;
    }
    .word{
      float: right;
      padding-top: 14px;
      color: white;
      // font-family: PingFangSC-Regular;
      letter-spacing: 1.32px;
      width: 40%;
      text-align: left;
    }
    .vanPopup{
      background: rgba(0,0,0,0.6);
      width: 100%;
      height: 100%;
    }

    .cancelReservation {
      height: 31px;
      text-align: left;
      padding-top: 1%;
      text-indent: 30px;
      font-size: 18px;
      color: #f1be0adb;
    }
    .reservationTime{
      margin-left: 7%;
      font-weight:bold;
    }
    .cancelButton{
      background-color: #f1be0adb;
      color: white;
    }
    .appointmentInfo{
      margin-top: 7%;
      font-size: 18px;
      margin-left: 14%;
      text-align: left;
    }
  }

</style>
