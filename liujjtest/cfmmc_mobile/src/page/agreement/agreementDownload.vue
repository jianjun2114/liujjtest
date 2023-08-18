<template>
  <div class="agreementDownload">
    <van-nav-bar
      title="协议下载"
      left-text=""
      right-text="关闭"
      right-arrow
      @click-right="onClickRight"
    />
    <div v-if="!isIOS">
     <div class="checklist">
       <checklist  :label-position="labelPosition" required :options="agreementList" v-model="checkList" @on-change="pdfSize"></checklist>
     </div>
     <div class="page" v-if="isContext">
       <van-pagination
         v-model="currentPage"
         :show-page-size="showPage"
         force-ellipses
         @change="change"
         :page-count="totalPage"
         :items-per-page="10"
       />
       <van-notice-bar :text="msg" style="margin-left: 8%;width:78%"></van-notice-bar>
     </div>

     <van-button  bottom-action @click="download" :class="{download:true,up:show}" v-if="isContext">下载</van-button>
    <div class="success" v-if="show">
      <span class="text">文件存储路径:{{filePath}}</span>
      <!--<p class="path" v-text="filePath"></p>-->
    </div>
    </div>
   <!--IOS显示   开始-->
    <div class="warpDiv" v-if="isIOS">
      <div class="done" v-html="tips">

      </div>
    </div>
    <!--IOS显示   结束-->
    <popUp :tips="error_content" :showBox="show_content" @notarize="notarize"></popUp>
    <waitPop :loadingbox="loadingbox"></waitPop>
    <quit :content="content" :quit="quit"></quit>
  </div>
</template>
<script type="text/ecmascript-6">
  import {Checklist} from 'vux';
  import PopUp from "../../components/popUp.vue";
  import Utils from '../../util/iphoneOrAndroid';
  import Quit from "../../components/quit.vue";
  import WaitPop from "../../components/waitPop.vue";
  export default {
    components: {
      Checklist,
      PopUp,
      Quit,
      WaitPop
    },
    data(){
      return{
        tips:"",
        error_content:"",
        show_content:false,
        content:"",
        quit:false,
        isIOS:false,
        loadingbox:false,
        checkList:[],
        labelPosition:"",
        currentPage:1,
        pageSize:7,
        showPage:1,
        totalPage:10,
        isContext:true,
        agreementList:[],
        filePath:"",
        show:false,
        msg:"准备下载的文件大小: 0(kb),如下载文件过多时，建议单独进行下载。",
        fileSize:0
      }
    },
    created(){
      window['agreementDownload'] = function(status,filePath){
        _this.agreementDownload(status,filePath);
      };
      var _this = this;
      var browserVersion = Utils.iphOrAnz();
      if (browserVersion == "iphone") {   //ios
        _this.isIOS = true;
        var data = {
          "broker_id":sessionStorage.getItem('broker_id'),
        }
        _this.loadingbox = true;
        _this.axios.post('/index/qryCmpInfoParam',data).then(function (res) {
          _this.loadingbox = false;
          if(res.data.error_no == 0){
            _this.tips = "目前暂不支持IOS移动端下载协议，如需了解详情，请用电脑端或安卓系统下载。</br>电脑下载地址："+ res.data.web_url;
          }else if(res.data.error_no == "-9999"){
            _this.quit = true;
            _this.content = res.data.error_info;
          }else{
            _this.error_content = res.data.error_info;
            _this.show_content = true;
          }
        }).catch(function (err) {
          console.log(err)
        });
      } else if (browserVersion == "anzuo") {  //安卓
        _this.isIOS = false;
        _this.getAgreementList();
      }
    },
    methods:{
      onClickRight(){
        this.$router.push({name:'MyBussiness'});
      },
      change(){
        var _this = this;
        _this.getAgreementList();
      },
      download(){
          var _this = this;
          if(_this.checkList.length == 0){
            _this.error_content = "请选择需要下载的协议";
            _this.show_content = true;
            return;
          }
          var pdfNamess = _this.checkList.join(";");
          var data = {"pdfNames": pdfNamess};
          _this.loadingbox = true;
          _this.axios.post('/agreement/downloadAgreementApp',data).then(function (res) {
            _this.loadingbox = false;
            if(res.data.error_no == 0){
              var currDate = new Date();
              var y= currDate.getFullYear()+'';
              var m= currDate.getMonth()+1;
              var d = currDate.getDate();
              var h = currDate.getHours();
              var mi = currDate.getMinutes();
              var s = currDate.getSeconds();
              m = m < 9? '0'+m:m+'';
              d = d < 9? '0'+d:d+'';
              h = h < 9? '0'+h:h+'';
              mi = mi < 9? '0'+mi:mi+'';
              s = s < 9 ? '0'+s:s+'';
              var df = y+m+d+h+mi+s+''
              var fileName = "期货协议下载-"+df+".zip";
              jsHandle.saveFile(res.data.agreement,fileName,"window.agreementDownload");
              //_this.agreementDownload("0","shsdfhjsd/sdfhjsdhf/skdhkf");
            }else if(res.data.error_no == "-9999"){
              _this.quit = true;
              _this.content = res.data.error_info;
            }else{
              _this.error_content = res.data.error_info;
              _this.show_content = true;
            }
          }).catch(function (err) {
            console.log(err)
          });
        },
      notarize(){
        this.show_content = false;
      },
      getAgreementList() {
        var _this = this;
        _this.axios.post('/agreement/getSignedAgreementList', {
          'currentPage': _this.currentPage,
          'pageSize': _this.pageSize
        }).then(function (res) {
          if (res.data.error_no == 0) {
            var result = JSON.parse(res.data.agreement);
            /*if(result==null || result== undefined){
              _this.isContext = false;
            }*/
            _this.agreementList = result.map((item) => {
              return {
                key:item.businessType+","+item.pdfNames+","+item.pdfSize,
                value: item.pdfNames + "</br>"+item.businessTypeName +" - "+item.pdfSignTime
              }
            });
             if(res.data.total_count%_this.pageSize == 0){
                if(res.data.total_count/_this.pageSize<3){
                  _this.showPage = res.data.total_count/_this.pageSize;
                }else{
                  _this.showPage = 3;
                }
               _this.totalPage = res.data.total_count/_this.pageSize;
             }else{
               if(parseInt(res.data.total_count/_this.pageSize)+1<3){
                 _this.showPage = parseInt(res.data.total_count/_this.pageSize)+1;
               }else{
                 _this.showPage = 3;
               }
               _this.totalPage = parseInt(res.data.total_count/_this.pageSize)+1;
             }
          } else if(res.data.error_no == "-9999") {
            _this.quit = true;
            _this.content = res.data.error_info;
          }else if(res.data.error_no == "-7047"){
            _this.error_content = "您目前没有可下载的协议，如有疑问，请拨打期货公司热线:\n"+sessionStorage.getItem('broker_hotline')+"。";
            _this.show_content = true;
            _this.isContext = false;
          }else{
            _this.error_content = res.data.error_info;
            _this.show_content = true;
            _this.isContext = false;
          }
        }).catch(function (err) {
          console.log(err)
        });
      },
      agreementDownload(status,filePath){
        var _this = this;
        if(status != "0"){
          _this.error_content = "下载协议失败！";
          _this.show_content = true;
          return;
        }
        _this.error_content = "下载协议成功！";
        _this.show_content = true;
       _this.filePath = filePath;
       _this.show = true;
      },
      pdfSize(value, label){
        var size = 0;
        this.checkList.map((item)=>{
          size += parseInt(item.toString().split(",")[2]);
        })
        this.fileSize = size;
        this.msg = "准备下载的文件大小："+this.fileSize +"(kb),如下载文件过多时，建议单独进行下载。"
      }
    }
  }
</script>

<style lang="less">
  .agreementDownload{
    overflow: hidden;
    .checklist{
      padding-top: 12%;
    }
    .weui-cell__bd{
      text-align: left;
    }
    .download {
       background: #00a8ff;
       color: white;
       border-radius: 3px;
       width: 84%;
       height: 40px;
       line-height: 10px;
       margin-bottom: 5%;
       position: fixed;
       left: 9%;
       bottom: 0;
    }
    .page{
      margin: 10px auto;
      position: absolute;
      width: 100%;
      bottom:10%;
    }
    .warpDiv{
      width: 84%;
      margin: 0 auto;
      padding-top: 20%;
      text-align: left;
    }
    .success{
      border-radius: 3px;
      width: 90%;
      height: 40px;
      line-height: 10px;
      position: fixed;
      bottom: 0;
    }
    .text{
      height: 17px;
      text-align: left;
      font-size: 14px;
      color: #8c8c8c;
      line-height: 17px;
    }
    .weui-cells{
      font-size: 16px;
    }
    .up{
      margin-bottom: 16%;
    }
  }
</style>
