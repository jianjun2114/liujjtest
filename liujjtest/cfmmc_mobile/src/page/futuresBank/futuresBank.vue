<template>
    <div class="futuresBank">
        <van-nav-bar
          title="银行卡信息确认"
          right-text="关闭"
          right-arrow
          left-arrow
          @click-left="onClickLeft"
          @click-right="onClickRight"
        />
        <div  class="bankCommon">结算银行</div>
        <div class="chooseBank" @click="chooseBank">
            <van-field
              v-model="bankName"
              label="请选择银行"
            />
            <van-icon name="arrow"  class="more"/>
        </div>
        <div class="bankInfo">
            <van-field
              v-model="bankNumber"
              label="卡号"
            />
            <van-field
              v-model="bankAddress"
              label="银行网点"
            />
        </div>
        <div class="zm uploadImg">
          <van-uploader :after-read="onRead" accept="image/gif, image/jpeg" >
            <img :src='bankImage.img' alt="" class="upload_img">
          </van-uploader>
        </div>
      <van-button  bottom-action @click="addBank">添加银行卡</van-button>
      <div  class="bankSpec">已经添加的银行</div>
      <div class="addedBank" v-show="showBankDetail">
        <span class="bankName">{{bankName}}</span>
        <span class="bankNumber" style="color: blue;">{{bankNumber}}</span>
        <span class="del" @click="del">删除</span>
      </div>
      <van-button  bottom-action @click="bankSubmit">下一步</van-button>
      <van-dialog
        v-model="showDialog"
        show-cancel-button
        :before-close="beforeClose"
        title="温馨提示"
        message="您已经添加过该行银行卡,再次添加将覆盖以前信息"
      >
      </van-dialog>
    </div>
</template>
<script type="text/ecmascript-6">
    import { Toast,Dialog } from 'vant';
    export default {
      components: {},
      data(){
        return{
          bankNumber:'123456789012',
          bankAddress:'北京',
          bankName:'',
          showDialog:false,
          showBankDetail:false,
          idNumber:'',
          index:1,
          all:false,
          bankImage:{img:require("../../assets/card-front.png")},
        }
      },
      created() {
      //   // 开始
      //   this.qrCode ='data:image/png;base64,'+sessionStorage.getItem("qrCode");
      //   var data = {
      //     "business_type":sessionStorage.getItem('business_type'),
      //   };
      //   this.axios.post('/flow/qryAcceptInfo',data).then(function (res) {
      //       _this.accept_no =res.data.accept_no;
      //       _this.business_flow=res.data.business_flow;
      //       if(_this.$router.history.current.name != res.data.business_flow){
      //         _this.$router.push(res.data.business_flow)
      //       }
      //   }).catch(function (err) {
      //       console.log(err)
      //   });
      // // 结束
      },
      mounted(){
        this.bankName = this.$route.query.bankName;
      },
      methods:{
        onClickLeft() {
          Toast('返回');
        },
        onClickRight() {
          Toast('按钮');
        },
        onRead(file) {
          Toast.loading({
            mask: true,
            message: '正在上传和识别,请稍等...'
          });
          var _this=this;
          setTimeout(function () {
            _this.bankImage.img=file.content;
          },3000)
        },
        del(){
            this.bankName='';
            this.bankNumber='';
            this.bankAddress='';
            this.showBankDetail=false;

        },
        addBank(){
            if(this.bankName == ''){
              Toast('请选择银行');
            }else if(this.bankNumber == ''){
              Toast('请输入银行卡号');
            }else if(this.bankAddress == ''){
              Toast('请输入银行网点');
            }else if(this.bankImage.img == require("../../assets/card-front.png")){
              Toast('请上传照片');
            }else{
              this.all=true; //ji==记录资料是否完整
              this.index +=1;
              this.showBankDetail=true;
            }

            if(this.index >2){  //计数
                this.showDialog=true;
          }
          console.log(this.index);
        },
        beforeClose(action, done) {
          if (action === 'confirm') {
            done();
          } else {
            done();
          }
        },
        chooseBank(){
          this.$router.push({path:'/bankList' });
        },
        bankSubmit(){
            if(this.all == true){
              Dialog.confirm({
                message: '确定提交-----------吗？'
              }).then(() => {
//                console.log('确定');
                Toast.loading({
                  mask: true,
                  message: '提交中...',
				          duration:1000
                });
                //跳转下一页
				var _this = this;
				setTimeout(function () {
				  _this.$router.push({path:'/question'});
				},1000)
              }).catch(() => {
                console.log('取消')
              });
            }else{
              Toast('请完善资料');
            }
          }
       },
     }
</script>

<style lang="less" scoped>
.futuresBank{
  background: #fbfbfb;
  .van-nav-bar {
    height: 46px;
    position: relative;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
    text-align: center;
    line-height: 46px;
    background-color: #fff;
  }
  .chooseBank{
    position: relative;
    margin-top: 26px;
      .more{
        position: absolute;
        right: 20px;
        top: 16px;
      }
  }
  .bankCommon {
    height: 50px;
    line-height: 50px;
    margin-top: -56px;
    text-align: left;
    text-indent: 18px;
    font-size: 17px;
    background: #e8e8e8;
    border-bottom: 1px solid lavender;
    border-top: 1px solid lavender;
  }
  .bankSpec{
    height: 50px;
    line-height: 50px;
    text-align: left;
    text-indent: 18px;
    font-size: 17px;
    background: #F5F5F5;
    border-bottom: 1px solid lavender;
    border-top: 1px solid lavender;
    margin: 10px auto;
  }
  .bankInfo {
     margin-top: 20px;
     border-bottom: 1px solid #FBFBFB;
   }
  .zm.uploadImg {
    height: 130px;
    width: 90%;
    margin: 30px auto;
    background: white;
    border-radius: 8px;
  }
  img.upload_img {
    width:140px;
    height: 102px;
  }
  .addedBank{
    background: white;
    height: 50px;
    line-height: 50px;
    font-size: 15px;
    margin: 10px auto;
    width: 90%;
        .del{
          color: blue;
          display: inline-block;
          width: 17%;
        }
        span.bankNumber {
          width: 51%;
          display: inline-block;
        }
        span.bankName {
          display: inline-block;
          width: 28%;
          text-align: left;
        }
  }



}

</style>
