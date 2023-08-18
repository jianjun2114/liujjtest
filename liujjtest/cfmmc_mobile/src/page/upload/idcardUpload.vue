<template>
  <div class="upload">
    <van-nav-bar
      title="上传身份证照片"
      right-text="关闭"
      right-arrow
      @click-right="onClickRight"
    />
   <navigation></navigation>
    <div class="imgDiv">
      <div class="zm uploadImg">
        <div>
          <img id="zm" :src='zmImage.img' alt="" class="upload_img" @click="disabled && getPicZm()">
        </div>
      </div>
      <div class="fm uploadImg">
        <div>
          <img id="fm" :src='fmImage.img' alt="" class="upload_img" @click="disabled1 && getPicFm()">
        </div>
      </div>
      <div class="qm uploadImg">
        <div>
          <img id="qm" :src='qmImage.img' alt="" class="upload_img" @click="disabled2 && getPicQm()">
        </div>
      </div>
    </div>
    <div class="checkCertificate">
      <van-checkbox v-model="checked" class="read">我已阅读并同意</van-checkbox>
      <span style=" color: dodgerblue;font-size: 14px; float: left; margin-top: 0%" @click="certificate">《个人数字证书申请责任书》</span>
    </div>
    <br>
    <div class="qmz" style="height:50px;">
      <p style="color:#ccc">请按照签名照样例使用白纸及黑色签字笔规范签名。</p>
    </div>
    <van-button bottom-action class="nextBtn" @click="goNext()">下一步</van-button>
    <van-popup v-model="popUp" style="height:79%;" :close-on-click-overlay="false">
      <div class="hint">个人数字证书申请责任书</div>
      <div style="width: 86%; height: 76%; overflow: auto;  margin: 7% auto;">
        <p style="text-align: left; font-size: 16px; color:#333333; line-height: 29px;" v-html="html">{{tips}}</p>
      </div>
      <div class="ok" @click="ok">确定</div>
    </van-popup>
    <van-popup v-model="show" class="vanPopup">
      <div>
        <div class="specification">—&nbsp;拍摄规范&nbsp;—</div>
        <ul v-for="(idCardImg,index) in idCard_imgs" :key="index">
          <li class="li-img">
            <img class="img1" :src="'static/shootSpecification/'+idCardImg.status+'.svg'" :name='idCardImg.status'/>
            <img class="img2" :src="'static/shootSpecification/'+idCardImg.name+'.png'" :name='idCardImg.name'/>
            <span v-html="idCardImg.description" class="word"></span>
          </li>
        </ul>
        <div class="hr"></div>
        <ul v-for="(signImg,index) in sign_imgs" :key="'signImg'+index">
          <li class="li-img">
            <img class="img1" :src="'static/shootSpecification/'+signImg.status+'.svg'" :name='signImg.status'/>
            <img class="img2" :src="'static/shootSpecification/'+signImg.name+'.png'" :name='signImg.name'/>
            <span v-html="signImg.description" class="word"></span>
          </li>
        </ul>
        <button class="IKonw" @click="iKnow">我知道了</button>
      </div>
    </van-popup>
     <waitPop :loadingbox="loadingbox"></waitPop>
     <quit :content="content" :quit="quit"></quit>
     <popUp :tips="tips" :showBox="popup" @notarize="notarize"></popUp>
  </div>
</template>
<script>
  import WaitPop from "../../components/waitPop.vue";
  import {Toast, Popup, Loading} from "vant";
  import Header from "../../components/header.vue";
  import PopUp from "../../components/popUp.vue";
  import Utils from '../../util/iphoneOrAndroid';
  import Quit from "../../components/quit.vue";
  import Navigation from "../../components/navigation.vue";
  export default {
    components: {
      Header,
      PopUp,
      WaitPop,
      Loading,
      Quit,
      Navigation
    },
    data() {
      return {
        disabled: true,
        disabled1: true,
        disabled2: true,
        accept_status:"",
        remark:"",
        quit:false,
        content:"",
        html: "",
        popUp: false,
        tips: "",
        acceptNo: "",
        size: 204800,
        zmImage: { img: require("../../assets/front_img.png") },
        imageTemp: { img: require("../../assets/card-front.png") },
        fmImage: { img: require("../../assets/revse_img.png") },
        qmImage: { img: require("../../assets/personal_name.png") },
        title: "上传身份证照片",
        checked: "",
        temp: "",
        zm: false,
        fm: false,
        qm: false,
        loadingbox: false,
        is_upload: true,
        business_type: '',
        signature: "",
        back: "",  //正面
        front: "",
        tips: "",
        popup: false,
        la: true,
        show:false,
        idCard_imgs:[
          {name:"idCard1",description:"边角完整</br>字迹清晰",status:"done"},
          {name:"idCard2",description:"缺边角",status:"failed"},
          {name:"idCard3",description:"模糊或闪光灯",status:"failed"}
        ],
        sign_imgs:[
          {name:"sign1",description:"签字完整</br>字迹清晰",status:"done"},
          {name:"sign2",description:"缺笔画或模糊",status:"failed"}
        ]
      };
    },
    created() {
      var _this = this;
      _this.alter = false;
      _this.business_type = sessionStorage.getItem('business_type');
      if (_this.business_type == '11' || _this.business_type == '13'|| _this.business_type == '16'||_this.business_type == '17'||_this.business_type == '18' ||_this.business_type == '21') {
        _this.la = false;
      }
      window["takePic"] = function (picNo, picStr) {
        _this.takePic(picNo, picStr);
      };
      // 开始  控制页面流程不能相互跳页面
      _this.qrCode = 'data:image/png;base64,' + sessionStorage.getItem("qrCode");
      var data = {
        "business_type": sessionStorage.getItem('business_type'),
      };
      _this.axios.post('/flow/qryAcceptInfo', data).then(function (res) { //复核打回身份证修改或不能修改
        if(res.data.error_no == 0){
           _this.acceptNo = res.data.accept_no;
           _this.business_flow = res.data.business_flow;
           _this.accept_status = res.data.accept_status;
           _this.remark = res.data.remark;
            if(_this.accept_status == '6'){
              if(_this.remark.indexOf("证件照不清晰") >=0 && _this.remark.indexOf("签名照不清晰") >=0){
                  _this.disabled = true;
                  _this.disabled1 = true;
                  _this.disabled2 = true;
              }else if(_this.remark.indexOf("证件照不清晰") >= 0){
                  _this.disabled = true;
                  _this.disabled1 = true;
                  _this.disabled2 = false;
              }else if(_this.remark.indexOf("签名照不清晰") >= 0){
                  _this.disabled = false;
                  _this.disabled1 = false;
                  _this.disabled2 = true;
              }else{

              }
            }
        if (_this.$router.history.current.name != res.data.business_flow) {
          _this.$router.push(res.data.business_flow)
        }
        }else if(res.data.error_no == "-9999"){
          _this.quit = true;
          _this.content = res.data.error_info;
        }else{
          _this.popup = true;
          _this.tips = res.data.error_info;
        }
      }).catch(function (err) {
        console.log(err)
      });

      // 结束

      const getBankData = {
        "systype": sessionStorage.getItem('systype'),
        "is_upload": _this.is_upload,
      };
      _this.$ajax.post('/userInfo/downloadOcrImageIdCard', getBankData).then(function (res) {
        if (res.data.error_no == 0) {
          var base64Type = "data:image/jpeg;base64,";
          var zm_image = "";
          var fm_image = "";
          if(!(_this.acceptNo == "" && (_this.business_type == "16"|| _this.business_type == "23"))){
            if (res.data.front != '') {
              _this.zmImage.img = base64Type + res.data.front;
              _this.front = base64Type + res.data.front;
              zm_image = res.data.front;
            }
            if (res.data.back != '') {
              _this.fmImage.img = base64Type + res.data.back;
              _this.back = base64Type + res.data.back;
              fm_image = res.data.back;
            }
          }
          if (res.data.signature != '') {
            _this.qmImage.img = base64Type + res.data.signature;
            _this.signature = base64Type + res.data.signature;
          }
          //只要身份证正反面、签名照有图片没有就会提示拍摄规范
          if(zm_image.trim() == ''|| fm_image.trim() == ''|| res.data.signature.trim() == ''){
            _this.show = true;
          }
        }else if(res.data.error_no == "-9999"){
          _this.quit = true;
          _this.content = res.data.error_info;
        }else{
          _this.popup = true;
          _this.tips = res.data.error_info;
        }
      }).catch(function (error) {
        console.log(error);
      });
    },
    methods: {
      ok() {
        this.popUp = false;
      },
      certificate() {
        var _this = this;
        _this.axios.post('/agreement/getCertAgreement', {}).then(function (res) {
          if (res.data.errorNo == 0) {
            _this.popUp = true;
            _this.html = res.data.content;
          } else if (res.data.error_no == '-9999') {
            _this.quit = true;
            _this.content = res.data.error_info;
          } else {
            _this.tips = res.data.errorInfo;
            _this.popup = true;
          }
        }).catch(function (err) {
          console.log(err);
        });
      },
      notarize() {
        this.popup = false;
      },
      goNext() {
        this.qrCode = 'data:image/png;base64,' + sessionStorage.getItem("qrCode");
        var _this = this;
        if (_this.front == "") {
          _this.popup = true;
          _this.tips = "请上传身份证正面照！";
          return
        }
        if (_this.back == "") {
          _this.popup = true;
          _this.tips = "请上传身份证反面照！";
          return
        }
        if (_this.signature == "") {
          _this.popup = true;
          _this.tips = "请上传签名照！";
          return
        }
        if (_this.checked == "") {
          _this.popup = true;
          _this.tips = "请阅读并同意个人数字证书申请责任书！";
          return
        }
        // 下一步判断受理单号是否为空,没有受理单的情况
        var data = {   //每个步骤的第一步(生成受理单)
          "business_type": sessionStorage.getItem('business_type'),
          "business_button_type": '1', //下一步
          "systype":sessionStorage.getItem('systype'),
          "supplement_sign_type":sessionStorage.getItem('_supplementStatus')
        };
        if (_this.acceptNo == "" || _this.acceptNo == undefined) {
          _this.$ajax.post('/flow/getAcceptNo', data).then(function (res) {
            if (res.data.error_no == 0) {
              _this.acceptNo = res.data.accept_no;
              _this.$router.push({
                name: res.data.business_flow,
              });
            } else if (res.data.error_no == '-9999') {
              _this.quit = true;
              _this.content = res.data.error_info;
            } else {
              _this.popup = true;
              _this.tips = res.data.error_info;
            }
          });
        } else if (_this.acceptNo != "" || _this.acceptNo != undefined) {
          var data = {
            "accept_no": _this.acceptNo,
            "business_type": sessionStorage.getItem('business_type'),
            "business_button_type": '1',
            "business_flow": _this.$router.history.current.name,
          };
          _this.$ajax.post('/flow/updateAccept', data).then(function (res) {
            if (res.data.error_no == 0) {
              _this.$router.push({
                name: res.data.business_flow,
              });
            } else if (res.data.error_no == '-9999') {
             _this.quit = true;
             _this.content = res.data.error_info;
            } else {
              _this.popup = true;
              _this.tips = res.data.error_info;
            }
          }).catch(function (err) {
            console.log(err)
          });
        }
      },
      getPicZm() {
          var width_div = document.getElementById("zm").getBoundingClientRect().width * 3 + "";
          var height_div = document.getElementById("zm").getBoundingClientRect().height * 3 + "";
          //调用安卓 ios windows方法
            var browserVersion = Utils.iphOrAnz();
            if (browserVersion == "iphone") {
              var id_type = "6A";
              var aaa = "";
              window.location.href = "objc://takePicture_5/?"+id_type+"?"+aaa+"?takePic";
            } else if (browserVersion == "anzuo") {
                jsHandle.takePic("6A", width_div, height_div,"takePic");
            } else if (browserVersion == "windows") {
                console.log("windows");
          }

      },
      getPicFm() {
        var width_div = document.getElementById("fm").getBoundingClientRect().width * 3+ "";
        var height_div = document.getElementById("fm").getBoundingClientRect().height * 3 + "";
         //调用安卓 ios windows方法
          var browserVersion = Utils.iphOrAnz();
            if (browserVersion == "iphone") {
              var idtype = "6B";
              var bbb = "";
              window.location.href = "objc://takePicture_5/?"+idtype+"?"+bbb+"?takePic";
            } else if (browserVersion == "anzuo") {
              jsHandle.takePic("6B", width_div, height_div,"takePic");
            } else if (browserVersion == "windows") {
                console.log("windows");
            }
      },
      getPicQm() {
        var width_div = document.getElementById("qm").getBoundingClientRect().width * 3 + "";
        var height_div = document.getElementById("qm").getBoundingClientRect().height * 3 + "";
         //调用安卓 ios windows方法
          var qmtype = "99";
         if (sessionStorage.getItem('enable_write_sign') === '1') {
                  qmtype = "199"
            } 
          var browserVersion = Utils.iphOrAnz();
            if (browserVersion == "iphone") {              
               var mmm = "";
               window.location.href = "objc://takePicture_5/?"+qmtype+"?"+mmm+"?takePic";
            } else if (browserVersion == "anzuo") {
              
              jsHandle.takePic(qmtype, width_div, height_div,"takePic");
            } else if (browserVersion == "windows") {
                console.log("windows");
            }
      },
      takePic(picNo, picStr) {
        if (picStr == "") {
          return;
        }
        if(picNo == '199') {
           picNo = '99';
        }
        this.upload(picNo, picStr);
      },
      upload(flag, url) {
        //身份证图片上传
        if (url == "") {
          this.popup = true;
          this.tips = "图片为空!";
          return;
        }
        var self = this;
        var idcardDate = {
          "image_no": flag,
          "image_data": url,
          "systype": sessionStorage.getItem('systype'),
          "business_type": sessionStorage.getItem('business_type'),
          "accept_status":self.accept_status
        };
        var interfaceName = "uploadArchives";
        if (flag == '6A' || flag == '6B') {
          interfaceName = "uploadRecognition";
        }
        self.loadingbox = true;
        self.$ajax.post("/record/" + interfaceName, idcardDate).then(function (res) {
            self.loadingbox = false;
            if (res.data.error_no == 0) {
              var base64Type = "data:image/jpeg;base64,";
              if (flag == "6A") {
                self.zmImage.img = base64Type + res.data.cropped_image;
                self.front = base64Type + res.data.cropped_image;
                if(res.data.user_validate != "0" && res.data.user_validate != ""){
                  self.popup = true;
                  self.tips = "证件照姓名或号码识别有误，请重新上传";
                }
              } else if (flag == "6B") {
                self.fmImage.img = base64Type + res.data.cropped_image;
                self.back = base64Type + res.data.cropped_image;
              } else if (flag == '99') {
                self.qmImage.img = base64Type + url;
                self.signature = base64Type + res.data.url;
              }
            } else if (res.data.error_no == "-9999") {
              self.quit = true;
              self.content = res.data.error_info;
            } else if(res.data.error_no =="-2007"){
              self.popup = true;
              self.tips = res.data.error_info;
            }else {
             /* self.imgQM = false;
              self.loading3 = false;
              self.imageUrlQm = self.imageUrlQm1;*/
              self.popup = true;
              self.tips = res.data.error_info;
            }
          }).catch(function (error) {
          console.log(error);
        });
      },
      getAndroid() {
        this.idcardUpload();
      },
      //关闭回到我也业务
      onClickRight() {
        this.$router.push({name: 'MyBussiness'})
      },
      onRead(file) {
        this.zmImage.img = file.content;
      },
      onReadFm(file) {
        //alert("fm");
        this.fmImage.img = file.content;
      },
      onReadQm(file) {
        //alert("qm");
        this.qmImage.img = file.content;
      },
      oversize(file) {
        this.popup = true;
        this.tips = "文件大小超过限制,请重新上传！"
      },
      iKnow(){
        this.show = false;
      }
    },
  };
</script>

<style lang="less">
  .qmz{
    text-align: left;
    position: relative;
    height: 20px;
    p{
      position: absolute;
      left: 20px;
      top: 5px;
    }
  }
  .upload {
    background:white;
    td {
      text-align: center;
    }
    .van-uploader {
      width: 100%;
    }
    img.upload_img {
      width: 82%;
      margin-top: 1%;
      // height: 184px;
    }
    .fm {
      margin-top: 1rem;
      margin-bottom: 1rem;
    }
    .uploadImg {
      border: 1px solid lavender;
      width: 90%;
      margin-left: 5%;
      position: relative;
      //height: 184px;
    }
    .checkCertificate {
      font-size: 1.65rem;
      margin-top: 1rem;
      margin-bottom: 1rem;
      margin-left: 4%;
    }
    .certificate {
      color: dodgerblue;
      float: left;
      line-height: 27px;
      font-size: 14px;
    }
    .lost {
      float: left;
      margin-left: 3.3rem;
      margin-top: 0.5rem;
      color: dodgerblue;
    }
    /*//ipad横屏*/
    @media only screen and (min-device-width: 768px) and (max-device-width: 1024px) and (orientation: landscape) {
      .uploadImg {
        display: inline-block;
        width: 32%;
        margin-left: 0%;
        height: auto;
      }
      .imgDiv {
        width: 90%;
        margin: 0 auto;
        position: relative;
      }
    }
    /*   //ipad竖屏*/
    @media only screen and (min-device-width: 768px) and (max-device-width: 1024px) and (orientation: portrait) {
      .uploadImg {
        display: block;
        width: 82%;
        margin: 0 auto;
        margin-top: 18px;
      }

      .imgDiv {
        width: 90%;
        margin: 0 auto;
      }

      img.upload_img {
        width: 80%;
        height: 260px;
        margin-top: 0.8rem;
        padding-bottom: 8px;
      }
    }
    /*//-------------------------ipad pro*/
    /*   //ipad pro横屏*/
    @media only screen and (min-device-width: 1024px) and (max-device-width: 1366px) and (orientation: landscape) {
      .uploadImg {
        display: inline-block;
        width: 32%;
        margin-left: 0%;
        height: auto;
      }
      .imgDiv {
        width: 90%;
        margin: 0 auto;
      }
      .upload_img{
        width: 82%;
        height: 192px;
      }
      .checkCertificate{
        margin-top: 8rem;
      }
    }
    /* //ipad pro竖屏*/
    @media only screen and (min-device-width: 1024px) and (max-device-width: 1366px) and (orientation: portrait) {
      .uploadImg {
        display: block;
        width: 82%;
        margin: 0 auto;
        height: auto;
      }
      .imgDiv {
        width: 90%;
        margin: 0 auto;
      }

      img.upload_img {
        width: 80%;
        height: auto;
        margin-top: 0.8rem;
      }
    }
    .van-checkbox--checked {
      border-color: #49a5ff;
      background-color: #49a5ff;
    }
    .van-button--bottom-action {
      width: 86%;
    }
    .ok {
      width: 22%;
      color: #ffffff;
      background: #49a5ff;
      text-align: center;
      line-height: 35px;
      border-radius: 4px;
      margin: -1% auto;
      margin-bottom: 12px;
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

    .van-checkbox {
      overflow: hidden;
      // width: 98%;
      float: left;
      margin-left: 2%;
    }
    .read {
      font-size: 14px;
      // padding-top: 2px;
    }
    .ertificate {
      font-size: 15px;
    }
    .van-checkbox__icon .van-icon {
      font-size: 14px;
      color: transparent;
      text-align: center;
      line-height: inherit;
      border: 1px solid #e5e5e5;
      width: 17px;
      height: 17px;
      box-sizing: border-box;
      -webkit-transition: .2s;
      transition: .2s;
      line-height: 12px;
    }
    .van-icon-success::before {
      content: inherit;
      color: #fff;
      font-size: 12px;
    }
    .boxes_div{
      width:100%;
      position: absolute;
      top:24%;
      left:0;
       .photo{
          width:20%;
       }
       p{
         line-height: 36px;
       }
    }
    //没有导航条时   留点间隙
    .imgDiv{
      margin-top: 1%;
    }
    .IKnow{
      border: 1px solid #FFFFFF;
      border-radius: 3px;
    }
    .img1{
       height:  20px;
      width: 20px;
      float: left;
      padding-top: 8%;
      padding-left: 26px
    }
    .img2{
      height:  64px;
      width: 100px;
    }
    .li-img{
      width: 82%;
      height: auto;
      margin: 6px auto;
    }
    .word{
      float: right;
      padding-top: 14px;
      color: white;
      font-family: PingFangSC-Regular;
      letter-spacing: 1.32px;
      width: 34%;
      text-align: left;
    }
    .vanPopup{
      background: rgba(0,0,0,0.6);
      width: 100%;
      height: 100%;
    }
    .specification{
      ont-family: PingFangSC-Regular;
      font-size: 18px;
      color: #FFFFFF;
      letter-spacing: 1.82px;
      padding-bottom: 15px;
      padding-top: 30px;
    }
    .IKonw{
      width: 119px;
      height: 40px;
      border: 1px solid #FFFFFF;
      border-radius: 3px;
      font-family: PingFangSC-Regular;
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
    .hr{
      opacity: 0.16;
      background: #FFFFFF;
      border-radius: 2px;
      width: 80%;
      border: 1px solid #FFFFFF;
      margin: 15px auto; margin-bottom:19px;
    }
  }
</style>
