
<template>
  <div class="canahartalogin">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1 user-scalable=no">
    <!--手机号登录-->
    <div class="register_tille">
      <van-nav-bar v-if="mobilenav" title="开立期货账户" right-text="关闭" left-arrow @click-left="onClickLeft" @click-right="onClickRight"/>
      <div class="highly"></div>
      <div class="warp_box" v-if="showPhoneDiv">
        <van-cell-group>
          <van-field maxlength="4" placeholder="请输入图形验证码" class="group-x-input" v-model="identifyingCode" style="line-height: 40px;">
          </van-field>
          <img :src="src" class="vilidate_code_img" @click="VerifyCode()">
        </van-cell-group>
        <div class="input-group" >
          <button v-bind:class="{ register_msg_btn: true, registerbag:isLoad }" v-show="show" v-on:click="getCode" v-if="gm_otp === '0'">{{mesg}}</button>
          <button v-bind:class="{ register_msg_btn: true, registerbag:isLoad , datetcolor: true}" v-show="!show" v-if="gm_otp === '0'">{{count}}秒后重新获取</button>
          <van-field v-model="messageCode" maxlength="6" class="authCode" placeholder="请输入验证码" v-if="gm_otp === '0'"/>
          <van-field v-model="otpcode" maxlength="6" class="authCode" placeholder="请输入OTP动态令牌" v-else/>
          <span class="glyphicon glyphicon-font form-control-feedback"/>
        </div>
         <van-notice-bar  v-show="isSmsSuccuseTips" :text="smsSuccuseTip"></van-notice-bar>
         <p class="msmTitle" v-show="isCloudSms">{{smsText}}<span v-bind:class="{smsClass:true,notClick:!show}" @click="getCloudSms" v-show="displayContent">{{smsContent}}</span></p>
        <van-button size="large" class="van-button" @click="OpenAccount()">下一步</van-button>
      </div>
    </div>

    <!--身份证登录-->
    <div class="register_tille2">
      <van-nav-bar v-if="idNumberNav" title="登录" right-text="关闭" left-arrow @click-left="onClickLeft" @click-right="onClickRight"/>
      <div class="warp_box" v-if="showIdDiv">
        <van-cell-group>
          <van-field maxlength="18" v-model="idNumber" placeholder="请输入身份证" @blur="idNumberFiled()"/>
        </van-cell-group>
        <van-cell-group>
          <input maxlength="4" placeholder="请输入图形验证码" class="group-x-input" v-model="identifyingCode_login"></input>
          <img :src="src" class="vilidate_code_img" @click="VerifyCode()">
        </van-cell-group>
        
        <van-button size="large" class="van-button" @click="idCardLogin()">下一步</van-button>
      </div>
    </div>
    <div class="warmPrompt">
      <h3 style="margin-left: 6%; text-align: left;">温馨提示</h3>
      <div v-if="showIdDiv" class="warpbox">
        <p>1、网上开户服务时间:{{serviceTime}}。</p>
        <p>2、网上开户暂仅限年满18周岁、具有有效期内的中华人民共和国居民二代身份证的中国大陆个人客户申请。</p>
        <p>3、请输入本人身份证号后点击下一步。</p>
      </div>
     </div>
       <div class="null">
          <button>请准备好</button>
        </div>
        <div class="van-row" style="padding-top: 0%;">
          <van-row>
            <van-col span="8">
              <img src="../../assets/IDcard.svg">
              <p>二代身份证</p>
            </van-col>
            <van-col span="8">
              <img src="../../assets/Bank.svg">
              <p>银行卡</p>
            </van-col>
            <van-col span="8">
              <img src="../../assets/Wi-Fi.svg">
              <p>网络环境</p>
            </van-col>
          </van-row>
          <van-row>
            <van-col>
              <div>版本信息:{{app_sdk_version}}</div>
            </van-col>
          </van-row>
        </div>

        <div v-if="showPhoneDiv">
        <p style="width: 80%;margin: 0 auto;text-align: left;line-height: 26px;">1、您在我司办理业务时已预留了手机号，请确保您的预留手机号可正常接收短信后点击获取短信验证码进行登录。</p>
      </div>
        <div>
          <p style="text-align: center;width:100%;margin-top: 10%">服务热线:{{broker_hotline}}</p>
        </div>

    <!-- 隐私协议 -->
    <van-popup v-model="popUp" style="width: 80%; height: 385px;" :close-on-click-overlay="false">
      <div class="hint">开户云隐私协议</div>
      <div class="ones" v-if="ones">
        <p>感谢您下载期货开户云！</p>
        <p>开户云尊重并保护用户个人隐私权。当您开始使用开户云时，我们可能会采集、使用和共享您的部分个人信息。希望您仔细阅读<span @click="privacyPolicy">《开户云隐私协议》</span>，详细了解我们对信息的收集、使用方式，以便您更好地了解我们的服务并做出适当的选择。</p>
        <p>《开户云隐私协议》将帮助您了解以下内容</p>
        <p>一、 我们如何收集和使用您的个人信息</p>
        <p>二、 我们如何存储和保护您的个人信息</p>
        <p>三、 我们如何共享和公开披露您的个人信息</p>
        <p>四、 您如何管理个人信息</p>
        <p>五、 我们如何使用Cookie和同类技术</p>
        <p>六、《开户云隐私协议》的更新</p>
        <p>七、 如何联系我们</p>
        <p>若您同意本政策，请点击“同意”开始使用开户云，我们将会尽力保护您的隐私权。</p>
      </div>

      <div class="twoPopUpDiv" v-if="twos">
        <h2>引言</h2>
        <p>开户云尊重并保护用户个人隐私权。当您开始使用开户云时，我们可能会采集、使用和共享您的部分个人信息。希望您仔细阅读<span>《开户云隐私协议》</span>，详细了解我们对信息的收集、使用方式，以便您更好地了解我们的服务并做出适当的选择。
        <p>本《开户云隐私协议》将帮助您了解以下内容 </p>
        <h2>一、我们如何收集和使用您的个人信息</h2>
        <p>二、我们如何存储和保护您的个人信息</p>
        <p>三、我们如何共享和公开披露您的个人信息</p>
        <p>四、您如何管理个人信息</p>
        <p>五、我们如何使用Cookie和同类技术</p>
        <p>六、《开户云隐私协议》的更新</p>
        <p>七、如何联系我们</p>
        <div>您应当仔细阅读、充分理解本隐私协议后使用我们的产品或服务，若您使用开户云服务，即表示您认同本隐私协议（包括更新版本）的全部内容，并且同意我们按照本隐私协议收集、使用、保存和共享您的相关信息。</div>
        <div>如对本隐私协议或相关事宜有任何问题，请联系我们。</div>
        <div>一、我们如何收集和使用您的个人信息
          <p>（一）我们如何收集您的信息</p>
          <p>我们会通过如下两种渠道获取您的个人信息：</p>
        </div>
        <div><h2>1、您直接提供的信息：</h2>
           <p>我们可能收集并存储您在使用开户云服务时向我们提供的任何信息，包括您在我们页面中在线填写信息时、上传材料时、解决争议以及与我们联系时所提供的信息。</p>
        </div>
        <div><h2>2、我们主动采集的信息：</h2>
           <p>在您使用开户云网站、APP视频认证过程中，我们可能主动采集您的部分信息。</p>
        </div>
        <div>（二）我们如何使用您的信息
           <p>我们收集您信息的目的主要是为了向您提供安全、高效以及个性化的服务体验。我们会出于以下目的使用您的个人信息：</p>
        </div>
         <div><h2>1、进行身份验证：</h2>
           <p>您在使用开户云服务时我们需要首先认证您身份的真实性，为此您需要提供您的相关身份信息、银行卡信息、照片及手机号。我们会将相关信息提交给合法持有您上述信息的第三方身份验证机构，以便对您所提供信息的准确性进行核对。如果您不提供上述信息则无法使用根据期货开户业务相关法律法规必须进行实名制管理的相关服务。</p>
        </div>
         <div><h2>2、进行资格、能力审核：</h2>
           <p> 您在使用开户云服务时，我们会使用您的个人基本信息、身份信息、财产信息及其他在具体业务开展过程中基于您的同意而采集的信息进行相关资格审核以及风险管理，防止潜在的非法活动。同时，在您享受开户云服务的过程中，我们会持续性的利用上述信息检测、防止或纠正违反相关法律法规或适用的用户协议的行为。</p>
        </div>
         <div><h2>3、提供客户服务及投诉处理：</h2>
           <p> 当您与我们联系沟通，进行查询、咨询或需要我们提供相关服务时，为保证您的账号安全，我们的客服人员会使用您的账号信息与您核验身份，并在法律和业务规则允许的情况下，提供您所需要的相关信息。</p>
           <p>为了保证您及他人的合法权益，如您被他人投诉或投诉他人，我们会将您的姓名及身份证号码、投诉相关内容提供给消费者权益保护部门及监管机关，以便及时解决投诉纠纷，但法律法规明确禁止提供的除外。</p>
        </div>
        <div><h2>4、用户安全保障功能：</h2>
           <p>我们可能会了解您的部分信息用于安全防范、反诈骗监测、存档备份、客户的安全服务等用途。</p>
        </div>
        <div><h2>5、管理软件：</h2>
           <p>例如，进行软件认证、软件升级等。</p>
        </div>
        <div>（三）我们保证不在未经您授权的条件下收集、使用个人信息，但下列情况除外：
          <p>1、根据有关法律法规的要求；</p>
          <p>2、按照相关政府主管部门的要求；</p>
          <p>3、维护社会个体和公众的安全；</p>
          <p>4、为维护社会公共利益的需要；</p>
          <p>5、维护开户云运营机构的合法权益；</p>
          <p>6、所收集的个人信息是个人信息主体自行向社会公众公开的；</p>
          <p>7、符合其他相关的要求。</p>
        </div>
         <div><h2>二、我们如何存储和保护您的个人信息</h2>
           <p>（一）您个人信息的存储</p>
           <p>1、存储地点：我们依照法律法规的规定，将您的个人信息存储于中华人民共和国境内。目前我们不存在跨境存储您的个人信息或向境外提供个人信息的场景。</p>
           <p>2、存储期限：根据《期货公司管理办法》等相关规定，有关您开户、变更、销户以及进行其他操作所涉及的资料应当按照规定的期限进行保存。</p>
           <p>（二）您个人信息的保护</p>
           <p>1、我们严格遵守法律法规保护用户的个人信息。</p>
           <p>2、 我们将在合理的安全水平内使用符合行业标准的安全技术措施及配套的组织架构和管理体系等多层面保护措施以最大程度降低您的信息被泄露、毁损、误用、非授权访问、非授权披露和更改的风险。</p>
           <p>3、我们建立专门的管理制度、流程和组织确保信息安全。例如，我们严格限制访问信息的人员范围，要求他们遵守保密义务，并定期审查。</p>
           <p>4、 若发生个人信息泄露等安全事件，我们会启动应急预案，阻止安全事件扩大，并以推送通知、公告等形式告知您。</p>
        </div>
         <div><h2>三、我们如何共享和公开披露您的个人信息</h2>
           <p>（一）我们如何共享您的个人信息：</p>
           <p>1、我们不会与开户云以外的任何公司、组织和个人共享您的个人信息，但以下情况除外：</p>
           <p>（1）基于政府监管的需要而使用；</p>
           <p>（2）根据适用的法律法规、法律程序的要求、强制性的行政或司法要求所必须的情况下进行提供；</p>
           <p>（3）在法律法规允许的范围内，为维护开户云、开户云的关联方、您或其他开户云用户或社会公众利益、财产或安全免遭损害而有必要提供；</p>
           <p>（4）只有共享您的信息，才能实现我们服务的核心功能或提供您需要的服务；</p>
           <p>（5）应您需求为您处理您与他人的纠纷或争议；</p>
           <p>（6） 符合与您签署的相关协议（包括在线签署的电子协议以及相应的平台规则）或其他的法律文件约定所提供；</p>
           <p>（7）事先获得您明确的同意或授权；</p>
           <p>（8）基于符合法律法规的社会公共利益而使用。</p>
           <p>2、您理解并知悉，为了必要/合理的业务的顺利开展、履行我们在相关用户协议或本隐私协议中的义务和行使我们的权利，我们可能需要向部分业务关联方共享您的部分信息。但我们承诺我们只会共享您的必要个人信息，如果我们共享您的个人敏感信息或改变个人信息的使用及处理目的，将再次征求您的授权同意。</p>
           <p>3、对我们与之共享个人信息的公司、组织和个人，我们会与其签署严格的保密协定，要求他们按照我们的说明、本隐私协议以及其他任何相关的保密和安全措施来处理个人信息。</p>
           <p>（二）公开披露</p>
           <p>我们仅会在以下情况下，且采取符合业界标准的安全防护措施的前提下，才会公开披露您的个人信息：</p>
           <p>1、根据有关法律法规的要求；</p>
           <p>2、按照相关政府主管部门的要求；</p>
           <p>3、维护社会个体和公众的安全；</p>
           <p>4、为维护社会公共利益的需要；</p>
           <p>5、维护开户云运营机构的合法权益；</p>
           <p>6、事先获得用户的明确授权；</p>
           <p>7、符合其他相关的要求；</p>
        </div>
        <div><h2>四、您如何管理您的个人信息</h2>
          <p>（一）您可以在使用我们服务的过程中，访问、修改和注销您提供的身份信息和其他一般信息。您访问、修改和删除个人信息的范围和方式将取决于您使用的具体服务，建议您仔细阅读相关指引。</p>
          <p>（二）我们将按照本政策所述，仅为实现我们业务的需要，收集、使用您的信息。如您发现我们违反法律、行政法规的规定或者双方的约定收集、使用您的个人信息，您可以要求我们删除。如您发现我们收集、存储的您的个人信息有错误的，您也可以要求我们更正。请通过本政策列明的联系方式与我们联系。</p>
          <p>（三）在您访问、修改和注销相关信息时，我们可能会要求您进行身份验证，以保障帐号的安全。</p>
          <p>（四）请您理解，由于技术所限、法律或监管要求，我们可能无法满足您的所有要求，我们会在合理的期限内答复您的请求。</p>
        </div>
        <div><h2>五、我们如何使用 Cookie 和同类技术</h2>
          <p>（一）关于Cookie和同类技术</p>
          <p>1、Cookie是包含字符串的小文件，在您登入和使用网站或其他网络内容时发送、存放在您的计算机、移动设备或其他装置内（通常经过加密）。Cookie同类技术是可用于与Cookie类似用途的其他技术，例如：Web Beacon、Proxy、嵌入式脚本等。</p>
          <p>2、目前，我们主要使用Cookie收集您的个人信息。您知悉并同意，随着技术的发展和我们产品和服务的进一步完善，我们也可能会使用Cookie同类技术。</p>
          <p>（二）我们如何使用Cookie和同类技术</p>
          <p> 1、在您使用我们的产品与/或服务时，我们可能会使用Cookie和同类技术收集您的一些个人信息，例如：您访问网站的习惯、您的登录信息等，Cookie和同类技术收集该类信息是为了您使用我们的产品与/或服务的必要、简化您重复操作的步骤（如注册、登录）、保护您的信息和账号安全性、改善我们的产品和服务等。我们承诺仅在本《开户云隐私协议》所述目的范围内使用Cookie和同类技术。</p>
          <p> 2、如果您的浏览器允许，您可以通过您的浏览器的设置以管理、（部分/全部）拒绝Cookie与/或同类技术；或删除已经储存在您的计算机、移动设备或其他装置内的Cookie与/或同类技术，从而实现我们无法全部或部分追踪您的个人信息。您如需详细了解如何更改浏览器设置，请具体查看您使用的浏览器的相关设置页面。您理解并知悉：我们的部分产品/服务只能通过使用Cookie或同类技术才可得到实现，如您拒绝使用或删除的，您可能将无法正常使用我们的相关产品与/或服务或无法通过我们的产品与/或服务获得最佳的服务体验，同时也可能会对您的信息保护和账号安全性造成一定的影响。</p>
        </div>
        <div><h2>六、《开户云隐私协议》的更新</h2>
          <p>我们可能适时修订本政策内容。如该等更新造成您在本政策下权利的实质减少或重大变更，我们将在变更生效前，通过在页面显著位置提示。在该种情况下，若您继续使用我们的服务，即表示同意受经修订的政策约束。</p>
          <p>上述的“重大变更”包括但不限于：</p>
          <p>1、我们的服务模式发生重大变化。如处理个人信息的目的、处理的个人信息的类型、个人信息的使用方式等；</p>
          <p>2、我们在所有权结构、组织架构等方面发生重大变化。如业务调整、破产并购等引起的所有变更等；</p>
          <p>3、个人信息共享、转让或公开披露的主要对象发生变化；</p>
          <p>4、您参与个人信息处理方面的权利及其行使方式发生重大变化；</p>
          <p>5、我们负责处理个人信息安全的责任部门、联络方式及投诉渠道发生变化时；</p>
          <p>6、个人信息安全影响评估报告表明存在高风险时；</p>
          <p>7、其他重要的或可能严重影响您的个人权益的情况发生时。</p>
          <p>更新日期: 2019年4月1日 </p>
        </div>
        <div><h2>七、如何联系我们</h2>
          <p>如您对本隐私协议或您个人信息的相关事宜有任何问题、意见或建议，请通过拨打客服电话等方式与我们联系。您也可根据我们提供的指引，填写相关资料，将您的问题发送至cloud@cfmmc.com或寄到如下地址：</p>
          <p>地址：北京市西城区金融大街5号新盛大厦B座（收）</p>
          <p>邮编：100032 </p>
        </div>
      </div>


      <!--弹窗提示-->
      <div class="checkCertificate" style="margin-left: 5%">
        <div v-model="checked" class="deal">
          <span class="read">点击"同意"，即表示您已阅读并同意 </span>
          <span class="certificate">《隐私协议》</span></div>
      </div>
      <div class="ok" @click="agree">同意</div>
    </van-popup>
    <!--结束-->
    <popUp :tips="error_content" :showBox="show_content" @notarize="notarize"></popUp>
    <waitPop :loadingbox="loadingbox"></waitPop>
    <quit :content="content" :quit="quit"></quit>
    <confirm :showContents="showContents" :isShow="isShow" :yesButton="yesButton" :noButton="noButton" @ensure="ensure" @cancel="cancel"></confirm>
  </div>
</template>
<script>
  import {Selector, XInput, Group, Actionsheet, PopupRadio} from 'vux';
  import {Toast} from 'vant';
  import Utils from '../../util/iphoneOrAndroid';
  import PopUp from "../../components/popUp.vue";
  import WaitPop from "../../components/waitPop.vue";
  import Quit from "../../components/quit.vue";
  import Confirm from "../../components/confirm.vue";
  export default {
    inject: ['reload'],
    components: {
      Selector,
      XInput,
      Group,
      Actionsheet,
      Toast,
      PopupRadio,
      PopUp,
      WaitPop,
      Quit,
      Confirm
    },
    data() {
      return {
        // Service_hotline:"",
        ones: true,
        twos: false,
        isLoad: true,
        showContents:"",
        isShow:false,
        yesButton:"确定",
        noButton:"取消",
        html:"",
        mesg:"短信验证码",
        // mesgage:"OTP动态令牌",
        quit:false,
        content:"",
        loadingbox:false,
        serviceTime: "",
        error_content: "",
        show_content: false,
        popUp: false, //弹窗
        acceptNo: "",
        tips: "",
        idNumberNav: true,
        mobilenav: true,
        checked: false,
        idNumber: '',
        src: "",
        show: true,
        timer: null,
        count: "",
        identifyingCode: "",  //图形验证码_开户
        identifyingCode_login: '',//图形验证码_登录
        messageCode: "",  //验证码,
        otpcode:"",//otp
        //mobile: "", //手机号
        verify_code: "",
        loginMobile: "",
        bus_type: '',
        type: '',
        showPhoneDiv: false,
        showIdDiv: true,
        showAction: false,
        reg_phone: '',
        isDisabled: false,
        privacy_version:"",
        broker_hotline:"",
        reloadFlag: false,
        network:"0",
        // reloadFlag: false,
        isSmsSuccuseTips:false,
        smsSuccuseTip:"",
        model:"",
        phoneVersion:"",
        resgisterWay:"2",
        app_sdk_version:"",
        close:true,
        gotoPage:"",
        serviceTime:"",
        app_sdk_version:"",
        thdMobile:"",
        isCloudSms: false,
        smsText:"若无法收到短信验证码，您可使用",
        smsContent:"语音验证码",
        displayContent: true,
        voice_flag:"0",
        gm_otp:"",

      }
    },
    created() {
      this.broker_hotline = sessionStorage.getItem('broker_hotline');
      this.type = sessionStorage.getItem('business_type');
      if (this.type == '11' || this.type == '12') {
        this.showPhoneDiv = false;
        this.showIdDiv = true;
      }
      var _this = this;
      window['getPrivatePolicyCallback'] = function(gainVersions,sdfFlag){
		      _this.getPrivatePolicyCallback(gainVersions,sdfFlag);
		  };
      window['appSystemInfo'] = function(model,phoneVesion){
      _this.appSystemInfo(model,phoneVesion);
		};
      window['IOSGetSysInfo'] = function(phoneVesion,phoneModel){
      _this.IOSGetSysInfo(phoneVesion,phoneModel);
		};
      var list = [];

      var _clientIP = sessionStorage.getItem('_clientIP');
      if(_clientIP == undefined || _clientIP == null){
        _clientIP = ''
      }
      //判断期货公司不存在
      var data = {
        "broker_id":sessionStorage.getItem('broker_id'),
        "clientIP":_clientIP
      }
      _this.$ajax.post('/initAppPage', data, {}).then(function (res) {
        if (res.data.error_no == 0) {
          _this.html = res.data.privacy_policy_content;
          _this.privacy_version= res.data.privacy_version;
          _this.serviceTime = res.data.sys_time;
          _this.reg_phone = res.data.phone_limit_Num;
          _this.network = res.data.network_test;
          
          if(_this.network == "1"){
            _this.show_content = true;
            _this.error_content = "尊敬的投资者：目前为非交易时段，无法正常受理您的开户申请，如需办理业务请于下一交易日8:40以后再次登录。为更好的向投资者提供服务，本系统今日正在按照既定安排开展联合测试，今日登录后您所进行的全部操作均视为测试目的，非受邀测试用户请于下一交易日8:40以后登录办理正常业务，特请注意。";
          }
          //调用安卓 ios windows方法
          var browserVersion = Utils.iphOrAnz();
          if(browserVersion == "iphone"){
            window.location.href = "objc://getIphoneInfo";
          }else if(browserVersion == "anzuo"){
            jsHandle.getAppSystemInfo("appSystemInfo");
          }else{
            console.log("windows");
          }
          var arr = res.data.systype_data;
          for (var i = 0; i < arr.length; i++) {
            list.push({
              key: arr[i].systype,
              value: arr[i].systype_name
            });
          }
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
      var way = sessionStorage.getItem('resgisterWay');
      if(way != null){
        _this.resgisterWay = way;
      }
        _this.app_sdk_version = sessionStorage.getItem('versionCode');
    },
    mounted() {
      this.VerifyCode();
    },
    methods: {
      appSystemInfo(model,phoneVesion){  //安卓手机型号
        this.model = model;
        this.phoneVersion = phoneVesion;
        //安卓获取隐私协议
        jsHandle.getPrivatePolicy(this.privacy_version, "window.getPrivatePolicyCallback");
      },

      IOSGetSysInfo(phoneVesion,phoneModel){  // ios手机型号
        this.model = phoneModel;
        this.phoneVersion = phoneVesion;
        //IOS获取隐私协议
        window.location.href = "objc://privacyPolicy_5/?"+this.privacy_version+"?"+"getPrivatePolicyCallback";
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
      idNumberFiled(){
		    if (this.idNumber == '') {
          this.show_content = true;
          this.error_content = "请先输入身份证号！"
          return;
        }
        var v_info = this.v_idno(this.idNumber);
        this.vvalidate(v_info);
      },
      privacyPolicy(){
        this.ones = false;
        this.twos = true;
      },
    //弹窗取消按钮
      cancel(){
          this.isShow = false;
          if(!this.close){
            var browserVersion = Utils.iphOrAnz();
            if (browserVersion == "iphone") {
              window.location.href = "objc://callIOSOut_5";
            } else if (browserVersion == "anzuo") {
              jsHandle.logoutRelease();
            } else if (browserVersion == "windows") {
              console.log("windows");
            }
          }
      },
    //弹窗确定按钮
      ensure(){
        this.isShow = false;
        if(this.close){
          window.location.href = "objc://callIOSOut_5";
          return;
        }
        this.$router.push({name: this.gotoPage});
      },
      getPrivatePolicyCallback(gainVersions,sdkFlag){
        if(sdkFlag == '1'){   //标识是app 0 还是sdk 1
          this.popUp = false;
          return;
        }
         if(gainVersions == '1'){
            this.popUp = false;
         }else{
           // 20210525 隐私协议显示位置修改 修改为false
            this.popUp = false;
         }
      },
      notarize() {
        this.show_content = false;
        if(this.reloadFlag){
          this.reloadFlag = false;
          this.$router.push('/indexselection');
        }
      },
      agree() {
        var _this = this;
        _this.popUp = false;
          //调用安卓 ios windows方法
          var browserVersion = Utils.iphOrAnz();
          if (browserVersion == "iphone") {
              window.location.href = "objc://privacyPolicyAgree_5/?"+_this.privacy_version+"?"+"getPrivatePolicyCallback";
          } else if (browserVersion == "anzuo") {
              jsHandle.savePrivatePolicy(_this.privacy_version, "");
          } else if (browserVersion == "windows") {
              console.log("windows");
          }
      },
    //身份证下一步
      idCardLogin() {
        
    //验证身份证格式
        var v_info = this.v_idno(this.idNumber);
        if(!this.vvalidate(v_info)){
          return;
        };
        if (this.identifyingCode_login == '') {
          this.show_content = true;
          this.error_content = "请先输入图形验证码！"
          return
        }
        var postData = {
          "id_no": this.idNumber.toUpperCase(),
          "id_kind": '1',
          "verify_code": this.identifyingCode_login,
          "business_type": this.type,
          
        }
        //支持销户重开
        if(this.type === '11'){
          postData['systype'] = '1'
        }else if(this.type === '12'){
          postData['systype'] = '2'
        }
        var _this = this;
        _this.loadingbox = true;
        _this.$ajax.post('/index/initLogin', postData).then(function (res) { //身份证登录的下一步
            _this.loadingbox = false;
            console.log(res.data.gm_otp)
            _this.gm_otp = res.data.gm_otp;
            sessionStorage.setItem('gm_otp',res.data.gm_otp)
           //_this.reloadFlag = true;
            if(res.data.error_no == 0){
              if(res.data.business_flow!=undefined && res.data.business_flow=="canahartalogin"){
                _this.showIdDiv = false;
                _this.showPhoneDiv = true;
                _this.thdMobile = res.data.thdMobile;
                _this.VerifyCode();
              }else{
                _this.show_content = true;
                _this.error_content = res.data.error_info;
                return false
              }
            }else if(res.data.error_no == '-703'){ //手机号不存在或者为空
              if(res.data.business_flow!=undefined && res.data.business_flow=="register"){
                _this.$router.push(res.data.business_flow);
                _this.loadingbox = false;
                _this.VerifyCode();
              }else{
                _this.show_content = true;
                _this.error_content = res.data.error_info;
                return false
              }
            }else if (res.data.error_no == "-9999") {
              _this.quit = true;
              _this.content = res.data.error_info;
            }else{
              _this.loadingbox = false;
              _this.show_content = true;
              _this.error_content = res.data.error_info;
              return false
            }
        }).catch(function (error) {
            console.log(error)
          });
      },
      getBusType() {
        this.showAction = true
      },
    //点击选项时默认不会关闭菜单，可以手动关闭
      onSelect(item) {
        sessionStorage.setItem('systype', item.value)
        this.showAction = false;
        this.bus_type = item.name;
      },
    //上一步返回按钮
      onClickLeft() {
        this.$router.push('/indexselection');
      },
    //关闭退出
      onClickRight() {
        //调用安卓 ios windows方法
        var browserVersion = Utils.iphOrAnz();
        if (browserVersion == "iphone") {
          this.isShow = true;
          this.yesButton = "确定";
          this.noButton = "取消";
          this.showContents = "是否要退出开户流程？"
        } else if (browserVersion == "anzuo") {
          jsHandle.logout();
        } else if (browserVersion == "windows") {
          console.log("windows");
        }
      },
    //点击下一步判断
      OpenAccount() {
        if (!this.identifyingCode) {
          this.show_content = true;
          this.error_content = "请输入图形验证码！"
          return false
        }
        if (this.idNumber == '') {
            this.show_content = true;
            this.error_content = "请先输入身份证号！"
            return;
          }
        if (this.gm_otp === '1') {
          var reg = /^\d+$|^\d+[.]?\d+$/;
          if (this.otpcode == '') {
              this.show_content = true;
              this.error_content = "请输入动态令牌"
              return;
          }
          if (reg.test(this.otpcode) == false) {
              this.show_content = true;
              this.error_content = "动态令牌只支持6位数字"
              return;
          }
          
        } else {
            if (!(this.messageCode)) {
            this.show_content = true;
            this.error_content = "请输入验证码！"
            return false
          }
        }
       
        var _this = this;
        let phoneData = {
          "accept_no": _this.acceptNo,
          "verify_code": _this.identifyingCode.toUpperCase(),
          "mobile": _this.mobile,
          "validate_code":_this.gm_otp === '1' ? _this.otpcode : _this.messageCode,
          "business_type": sessionStorage.getItem('business_type'),
          //"mac_addr": '',
          "register_way": _this.resgisterWay,   //判断注册来源 手机还是电脑
          "time": new Date().getTime(),
          "channel":sessionStorage.getItem('channel'),
          "phone_type": _this.model,
          "phone_version": _this.phoneVersion,
          "app_sdk_version": _this.app_sdk_version,
        };
        _this.$ajax.post('/login', phoneData).then(function (res) {
          //  _this.reloadFlag = true;
           _this.loadingbox = false;
          if(res.data.error_no == 0){
            if(res.data.review_check_info != "1"){
              _this.noButton = "否";
              _this.yesButton = "是";
              //_this.close = false;
              _this.showContents = res.data.review_check_info;
              _this.isShow = true;
              _this.gotoPage = res.data.business_flow;
            }else{
              _this.$router.push({name: res.data.business_flow});
            }
            if(sessionStorage.getItem('business_type') == "11"){
              sessionStorage.setItem("systype","1");
            }
            if(sessionStorage.getItem('business_type') == "12"){
              sessionStorage.setItem("systype","2");
            }
          }else if (res.data.error_no == "-9999") {
            _this.quit = true;
            _this.content = res.data.error_info;
          } else if (res.data.error_no == "-8888") {
            _this.quit = true;
            _this.content = res.data.error_info;
          }else {
            _this.show_content = true;
            _this.error_content = res.data.error_info;
            if(res.data.error_no == "-80000"){
              _this.reloadFlag = true;
            }
            return false;
          }
        }).catch(function (err) {
           console.log(err)
        });
      },
    //图形验证码验证
      VerifyCode() {
        var _this = this;
        this.$ajax.post('/index/verifyCode', {}).then(function (res) {
            _this.verify_code = res.data.verify_code;
            _this.src = "data:image/jpeg;base64," + res.data.verify_code_img;
          })
      },
    //短信验证码
      getCode() {
        if (!this.identifyingCode) {
          this.show_content = true;
          this.error_content = "请输入图形验证码！"
          return false
        }
        var _this = this;
        var postData = {
          //"op_station": "",
          "accept_no": _this.acceptNo,
          "verify_code": _this.identifyingCode,
          //"mobile": _this.mobile,
          "messageCode": _this.messageCode,
          "business_type": sessionStorage.getItem('business_type'),
          //"page_flow":"2"
          "voice_flag":_this.voice_flag
        }

        _this.loadingbox = true;
        _this.$ajax.post('/index/validateCode', postData).then(function (res) {
          _this.loadingbox = false;
          if (res.data.error_no == 0) {
            _this.showTimer();
            _this.isSmsSuccuseTips = true;
            _this.smsSuccuseTip = "您好，动态验证码（切勿告知他人）已发送至您的"+_this.thdMobile+"手机，请注意查收。如未能收到验证码，请使用语音验证码或拨打期货公司电话："+ _this.broker_hotline;
          }else if(res.data.error_no == "-9999"){
            _this.quit = true;
            _this.content = res.data.error_info;
          }else if(res.data.error_no == "-8888"){
            _this.quit = true;
            _this.content = res.data.error_info;
          } else {
            _this.isDisabled = false;
            _this.show_content = true;
            _this.error_content = res.data.error_info;
            return false
          }
        }).catch(function (err) {
          _this.isDisabled = false;
          console.log(err)
        });
      },
    //60秒计时(短信验证码)
      showTimer() {
         var _this = this;
         _this.isDisabled = false;
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
              _this.isDisabled = false;
              _this.isCloudSms = true;  // 开启语音验证码
              _this.isLoad=true;
              clearInterval(this.timer);
              _this.timer = null;
            }
          }, 1000)
        }
      },
    //身份证登录短信验证码
      getCodeThr() {
        var _this = this;
        if (_this.idNumber == '') {
          _this.show_content = true;
          _this.error_content = "请先输入身份证号！"
          return
        }
    //验证身份证格式
        var v_info = _this.v_idno(this.idNumber);
        if(!_this.vvalidate(v_info)){
          return;
        };
        if (_this.identifyingCode_login == '') {
          _this.show_content = true;
          _this.error_content = "请先输入图形验证码！"
          return
        }
        let postData = {
          "id_kind": "1",
          "id_no": _this.idNumber,
          "opStation": '',
          "business_type": sessionStorage.getItem('business_type'),
          //"systype": _this.option1,   //账户类型
          "verify_code": _this.identifyingCode_login
        };
        _this.loadingbox = true;
        _this.$ajax.post('/index/qryThdSmsCode', postData).then(function (res) { //短信验证码
          if (res.data.error_no == 0) {
            _this.loadingbox = false;
            //_this.isSmsSuccuseTips = true;
            //_this.smsSuccuseTip = res.data.error_info+"。" + "如未能收到验证码，请拨打期货公司电话："+ _this.broker_hotline;
            _this.showTimer();
          }else if(res.data.error_no == "-9999"){
             _this.quit = true;
             _this.content = res.data.error_info;
             _this.loadingbox = false;
          }else if(res.data.error_no == "-8888"){
            _this.quit = true;
            _this.content = res.data.error_info;
          }else {
            _this.isDisabled = false;
            _this.show_content = true;
            _this.error_content = res.data.error_info;
            _this.loadingbox = false;
            return false
          }
        }).catch(function (err) {
          _this.isDisabled = false;
          console.log(err)
        });
      },
      getCloudSms(){
        if(!this.show){
          return;
        }
        this.mesg = "语音验证码";
        this.smsText = "您正在使用语音验证码";
        this.displayContent = false;
        this.voice_flag = "1";
      }
    }
  }
</script>
<style lang="less">
  .canahartalogin {
    background: white;
    height: 100%;
    .line {
      display: inline-block;
      width: 20%;
      border-bottom: 1px solid lavender;
    }
    .warp_box {
      width: 90%;
      margin: 0 auto;
    }
    .van-cell {
      display: -webkit-box;
      display: -ms-flexbox;
      display: flex;
      padding: 8px 15px;
      -webkit-box-sizing: border-box;
      box-sizing: border-box;
      line-height: 36px;
      position: relative;
      background-color: #fff;
      color: #333;
      font-size: 14px;
      overflow: hidden;
    }
    .tips {
      float: left;
      width: 44%;
    }
    .time {
      margin-top: 30px;
    }
    .vilidate_code_img {
      // width: 33%;
      // height: 42px;
      //margin-left: 9%;
      margin-top: 2.5%;
      float: right;
    }
    .van-button-code {
      width: 28%;
      height: 40px;
      border: 1px solid #eeeeee;
      position: absolute;
      right: 0;
      top: 29%;
      color: #cccccc;
      background: none;
      border-radius: 5px;
    }
    .group-x-input {
      color: #333333;
      line-height: 49px;
      float: left;
      border: none;
      text-indent: 9%;
      width: 53%;
    }
    .van-button {
      width: 100%;
      margin: 0 auto;
      background: #49a5ff;
      text-align: center;
      line-height: 20px;
      color: white;
      margin-top: 7%;
    }
    .van-row {
      width: 94%;
      margin: 5px 3%;
      padding-top: 4%;
    }
    input::-webkit-input-placeholder {
      color: #cccccc;
    }
    .register_msg_btn {
      width: 32%;
      height: 69%;
      line-height: 32px;
      right: 0;
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
    .input-group {
      width: 98%;
      border-bottom: 1px solid #eeeeee;
      position: relative;
    }
    .checkCertificate {
      text-align: left;
      text-indent: 0.3rem;
      font-size: 1.65rem;
      margin-top: 1.5rem;
    }
    .certificate {
      color: #ff8908;
    }
    .van-checkbox--checked {
      color: #fff;
      border-color: #49a5ff;
      background-color: #49a5ff;
    }
    .vux-label {
      display: inline-block;
      word-wrap: break-word;
      word-break: break-all;
      color: #cccccc;
      float: left;
    }
    .weui-cell_access .weui-cell__ft:after {
      content: " ";
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
      display: none;
    }
    .weui-cells {
      margin-top: 1.17647059em;
      background-color: #FFFFFF;
      line-height: 1.41176471;
      font-size: 17px;
      overflow: hidden;
      position: relative;
      margin-top: -3px;
    }
    .van-checkbox {
      margin-left: 9%;
    }
    .van-checkbox__label {
      margin-left: 10px;
      font-size: 15px;
    }
    .checkCertificate div span {
      font-size: 14px;
    }
    .vux-cell-primary{
      font-size: 14px;
    }
    .vux-cell-value{
      color: #333333;
    }
    [class*=van-hairline]::after{
      content: ' ';
      position: absolute;
      pointer-events: none;
      -webkit-box-sizing: border-box;
      box-sizing: border-box;
      top: -50%;
      left: -50%;
      right: -50%;
      bottom: -50%;
      -webkit-transform: scale(.5);
      transform: scale(.5);
      border: none;
    }
    .van-cell-group{
      background-color: #fff;
      border-bottom: 1px solid #eeeeee;
      line-height: 48px;
      width: 98%;
      height: 60px;
      margin: 0 auto;
    }
    .van-cell:not(:last-child)::after{
      content: ' ';
      position: absolute;
      pointer-events: none;
      -webkit-box-sizing: border-box;
      box-sizing: border-box;
      left: 15px;
      right: 0;
      bottom: 0;
      -webkit-transform: scaleY(.5);
      transform: scaleY(.5);
      border-bottom: none;
    }
    .smsSuccuseTips {
      color: blue;
      font-size: 10pt;
      overflow: hidden;
      white-space: nowrap;
      width: 90%;
      margin-left: 5%;
      margin-bottom: 10px;
    }
    .datetcolor{
      background: #cccccc;
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
    .ones{
      width: 86%;
      height: 200px;
      overflow: auto;
      margin: 7% auto;
        span{
          font-weight: bold;
          text-decoration:underline;
          color: #ff8908;
        }
        p{
          text-align: left;
        }
    }
    .twoPopUpDiv{
      width: 86%;
      height: 200px;
      overflow: auto;
      margin: 7% auto;
        span{
          font-weight: bold;
          text-decoration:underline;
          color: #ff8908;
        }
        p{
          text-align: left;
        }
        h2{
          font-weight: bold;
          text-align: left;
          font-size: 14px;
        }
    }
    .deal {
      font-size: 14px;
      width: 93%;
    }
    .highly{
      padding-top: 12%;
    }
    .ok {
      width: 22%;
      color: #ffffff;
      background: #49a5ff;
      text-align: center;
      line-height: 35px;
      border-radius: 4px;
      margin: 4% auto;
      margin-bottom: 12px;
  }
   .null {
      width: 65%;
      margin: 0 auto;
      height: 30px;
      border-bottom: 1px solid #e5e5e5;
      button {
        width: 48%;
        font-size: 14px;
        color: #999999;
        letter-spacing: 0;
        text-align: center;
        line-height: 20px;
        border: none;
        padding-top: 9%;
        background: #ffffff;
      }
    }
  //ipad适配
  @media only screen and (min-device-width : 768px) and (max-device-width : 1024px){
    .vilidate_code_img{
      margin-top: 1.5%;
    }
    .register_msg_btn{
      width: 19%;
      margin-right: -1%;
    }
    .group-x-input{
      width: 83%;
      text-indent: 2%;
    }
    .highly{
      padding-top: 6%;
    }
    .null button{
      padding-top: 4%;
    }
     .van-cell-group{
       height: 62px;
     }
  }
  .warmPrompt {
      width: 97%;
      margin: 0 auto;
      padding-top: 6%;
    }
  //ipad横屏
  @media only screen and (min-width: 1349px) and (orientation : landscape){
     .vilidate_code_img{
       margin-top: 0.8%;
     }
     .group-x-input{
        text-indent: 3%;
     }
     .highly{
       padding-top: 7%;
     }
     .input-group{
       width: 100%;
     }
     .van-cell-group{
       width: 100%;
     }
     .register_msg_btn{
       width: 12%;
     }
   }
    .warpbox {
      width: 81%;
      margin: 0 auto;
        p{
          text-align: left;
          line-height: 22px;
        }
    }
    .msmTitle{
       text-align: left;
       margin-top: 10px;
      .smsClass{
        color: #00a8ff;
        margin-left: 10px;
      }
      .notClick{
        opacity: 0.2;
      }
    }

}
</style>
