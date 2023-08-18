<template>
  <div class="result">
      <van-nav-bar
          title="申请提交"
          right-text="关闭"
          @click-right="onClickRight"
      />
      <!--<div class="title" style="background: white;position: relative" >
          <span class="step1">1.资料报审</span><van-icon name="arrow" />
          <span class="step2">2.签署协议</span><van-icon name="arrow" />
          <span class="step3">3.在线回访</span><van-icon name="arrow" />
      </div>-->
      <div class="result_contain">
        <van-icon name="passed" class="passed"/>
        <div class="result_tips1">提交成功,审核中...</div>
        <div class="result_tips2"></div>
        <p v-if="this.type == '14' || this.type == '22' || this.type == '24'" class="result_tips3">您的适当性已提交，正在等待复核；您可在系统的适当性功能中查看结果。</p>
        <p v-if="this.type == '13'" class="result_tips3">您的增开交易编码申请已成功提交！请在下一交易时间或收到通知短信后登录网上开户系统查看。如有疑问请拨打客服热线：<span>{{broker_hotline}}</span>。</p>
        <p v-if="this.type == '11'" class="result_tips3">尊敬的{{client_name}}{{client_sex}}，您的期货开户申请已成功提交！请在下一交易时间或收到通知短信后登录网上开户系统查看。如有疑问请拨打客服热线：<span>{{broker_hotline}}</span>。</p>
        <p v-if="this.type == '12'" class="result_tips3">
          尊敬的{{client_name}}{{client_sex}}，开立资管线上申请提交，复核完成后请线下办理剩余手续。如有疑问请拨打客服热线：<span>{{broker_hotline}}</span>。
        </p>
        <p v-if="this.type == '16' || this.type == '23'" class="result_tips3">尊敬的{{client_name}}{{client_sex}}，您的身份证有效期修改已成功提交，正在等待复核；您可在我的基本资料中查看结果。</p>
        <p v-if="this.type == '17'" class="result_tips3">尊敬的{{client_name}}{{client_sex}}，您的联系电话修改已成功提交，正在等待复核；您可在我的基本资料中查看结果。</p>
        <p v-if="this.type == '18'" class="result_tips3">尊敬的{{client_name}}{{client_sex}}，您的其他信息修改已成功提交，正在等待复核；您可在我的基本资料中查看结果。</p>
        <p v-if="this.type == '19'" class="result_tips3">尊敬的{{client_name}}{{client_sex}}，您办理完毕银行卡变更后，还需联系对应银行通过手机银行或电脑端网银系统或银行柜台等方式办理银期签约后才能使用银期转账功能，各银行银期签约具体办理指引详询热线电话：<span>{{broker_hotline}}</span>。</p>
        <p v-if="this.type == '21'" class="result_tips3">尊敬的{{client_name}}{{client_sex}}，您的补签协议已成功提交，正在等待复核。</p>
        <van-cell-group v-for="(item,index) in resultList" :key="index" v-if="type == '11'" >
          <van-field v-model="item.value"  :label="item.name" :readonly="true"/>
        </van-cell-group>
        <van-button  bottom-action  @click="goIndex" class="result_btn">{{message}}秒后 返回我的业务</van-button>
      </div>
      <popUp :tips="error_content" :showBox="show_content" @notarize="notarize"></popUp>
      <quit :content="content" :quit="quit"></quit>
  </div>
</template>
<script type="text/ecmascript-6">
  import PopUp from "../../components/popUp.vue";
  import Quit from "../../components/quit.vue";
  export default {
    components: {
      PopUp,
      Quit
    },
    data(){
      return{
        numbers:"",
        quit:false,
        content:"",
        error_content:"",
        show_content:false,
        hot_line:'',
        client_name:'',
        accetp_no:'',
        bussinessHall:'受理中',
        settleBank:'工商银行  受理中',
        resultList:[],
        type:'',
        client_sex:'',
        business_flow:"",
        timer:0,
        message:5
      }
    },
    created(){
      // 开始
      this.broker_hotline = sessionStorage.getItem('broker_hotline');
       var _this = this;
      _this.qrCode ='data:image/png;base64,'+sessionStorage.getItem("qrCode");
      var data = {
        "business_type":sessionStorage.getItem('business_type'),
      };
      _this.axios.post('/flow/qryAcceptInfo',data).then(function (res) {
        if(res.data.error_no == 0){
          _this.accetp_no =res.data.accept_no;
          _this.business_flow=res.data.business_flow;
        if(_this.$router.history.current.name != res.data.business_flow){
          _this.$router.push(res.data.business_flow)
        }
        _this.type = sessionStorage.getItem('business_type');
        var resultData = {
            "systype": sessionStorage.getItem('systype'),
            "business_type": sessionStorage.getItem('business_type')
        }
        _this.$ajax.post('/result/resultPage',resultData).then(function (res) {
            if(res.data.error_no == 0){
              _this.client_sex = res.data.client_gender+'';
              if(_this.client_sex == '1'){
                _this.client_sex = '先生';
              }else if(_this.client_sex == '2'){
                _this.client_sex = '女士';
              }else{
                _this.client_sex = '先生/女士';
              }
              _this.client_name = res.data.client_name;
              _this.resultList = res.data.resultList;
              // 数据回显完成  5秒之后回到我的业务
              _this.timer =  setInterval(function () {
                _this.message--;
                if(_this.message == 0){
                  _this.goIndex();
                }
              },1000);
            }else if(res.data.error_no =="-9999"){
              _this.quit = true;
              _this.content = res.data.error_info;
            }else{
              _this.show_content = true;
              _this.error_content = res.data.error_info;
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
      // 结束
    },
    methods:{
      notarize(){
        this.show_content = false;
      },
      onClickRight(){
        this.$router.push({name:'MyBussiness'});
      },
      resultNext(){},
      goIndex(){
        if(this.timer!=0){
          clearInterval(this.timer);
        }
        this.$router.push({name:'MyBussiness'});
      },
    }
  }
</script>
<style lang="less" scoped>
  .result{
        height: 100%;
        .result_contain{
            width: 94%;
            margin-left: 3%;
            border-radius: 1rem;
            padding-top:12%;
            background: white;
        }
        .passed{
            color: green;
            font-size: 5rem;
            margin-top: 2rem;
        }
        .result_tips1{
            font-size: 1.6rem;
            margin-top: 1rem;
        }
        .result_tips2{
            border-bottom: 1px solid #e5e5e5;
            width: 90%;
            margin-left: 5%;
            margin-top:1.5rem;
        }
        .result_tips3{
            font-size: 1.2rem;
            margin-top: 1rem;
            width:90%;
            margin-left: 5%;
            text-align: left;
            color: #666;
            margin-bottom: 3rem;
        }
        .result_btn{
            margin-top: 2.5rem;
            width: 90%;
            position: fixed;
            bottom: 4%;
            background: #49A5FF;
            color: white;
            left:5%;
            height: 47px;
        }
    }
</style>
