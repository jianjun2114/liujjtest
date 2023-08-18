<template>
    <div class="uploadReport">
        <van-nav-bar
            title="专业投资者资料上传"
            right-text="关闭"
            left-arrow
            right-arrow
            @click-right="onClickRight"
            @click-left="onClickLeft"/>
      <!--导航条-->
      <navigation/>
        <div>
            <div class="reportKind" @click="materIals">
              <p>*&nbsp;证明材料上传说明</p>
               <img src="../../assets/question.svg"/>
            </div>
            <div class="uploadbox">
                 <img v-if="isShow" @click="getImage" class="upload_a" id="upload_a" src="../../assets/photo.png"/>
                <p v-for="(item,index) in imgs" style="position: relative" :key="index" id="img_a" class="contain_a">
                  <img :src='item.url' alt="" id="upload_img" class="inner_img">
                  <van-icon name="close" class="del" @click="delItem(index)"/>
                </p>
            </div>
             <div style="height:250px; float: left;"><span>&nbsp;</span></div>
        </div>
      <div class="divcase">
        <van-button size="large" class="nextmajorBtn" @click="majorBtn">下一步</van-button>
      </div>
      <popUp :tips="error_content" :showBox="show_content" @notarize="notarize"></popUp>
      <waitPop :loadingbox="loadingbox"></waitPop>
      <quit :content="content" :quit="quit"></quit>
    <div class="materIalsboxWarp" v-if="gaineBox">
      <div class="materIalsbox">
        <div>证明材料上传说明</div>
        <p style="margin-top: 7%">1、请上传证明材料照片（证明材料可以是1、金融资产证明：如银行存款证明、证券、基金、期货权益证明等；2、投资经历证明：材料为加盖证券营业部专用章的对账单、加盖基金公司专用章的基金份额证明、加盖期货公司专用章的交易结算单等；3、年收入证明：税务机关出具的收入纳税证明、银行出具的工资流水单或者其他收入证明。4、工作经历证明） </p>
        <p>2、图片要求清晰可见，每张大小建议不要超过10M，仅支持JPG格式 </p>
        <p>3、根据开户要求，至少上传2张符合条件的资料照片，最多上传10张资料照片。</p>
        <img @click="photoImg" class="closeTwo" src="../../assets/close2.svg"/>
      </div>
    </div>
    </div>
</template>
<script type="text/ecmascript-6">
import Quit from "../../components/quit.vue";
import WaitPop from "../../components/waitPop.vue";
import { Toast,Dialog,Icon} from 'vant';
import PopUp from "../../components/popUp.vue";
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
	  return{
      gaineBox: false,
      quit:false,
      content:"",
      loadingbox: false,
      error_content:"",
      show_content:false,
      imgs:[],
      imgs_J:[],
      size:60000,
      uploadHide:true,
      uploadHide_J:true,
      accept_no:""
	  }
	},
	computed:{
	  isShow(){
		if(this.imgs.length>9){
		  return this.uploadHide = false;
		}else{
		  return this.uploadHide =true
		}
	  },
	  isShow_J(){
		if(this.imgs_J.length>9){
		  return this.uploadHide_J = false;
		}else{
		  return this.uploadHide_J =true
		}
	  },
	},
  created(){
    this.getPic();
    var _this = this;
    window['MajorImage'] = function(picNo,img){
      _this.MajorImage(picNo,img)
  }
  // 开始
	  var data = {
  		"business_type":sessionStorage.getItem('business_type'),
	  };
	  this.axios.post('/flow/qryAcceptInfo',data).then(function (res) {
      if(res.data.error_no == 0){
        _this.accept_no =res.data.accept_no;
		    _this.business_flow=res.data.business_flow;
        if(_this.$router.history.current.name != res.data.business_flow){
           _this.$router.push(res.data.business_flow)
        }
      }else if(res.data.error_no == '-9999'){
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
    materIals(){
      this.gaineBox = true;
    },
    photoImg(){
      this.gaineBox = false;
    },
    	notarize(){
				this.show_content = false;
			},
    getImage(){
        var width = document.getElementById("upload_a").getBoundingClientRect().width + "";
        var height = document.getElementById("upload_a").getBoundingClientRect().height + "";
         //调用安卓 ios windows方法
        var browserVersion = Utils.iphOrAnz();
        if (browserVersion == "iphone") {
          console.log("iphone");
          var aaa = "";
          window.location.href = "objc://takePicture_5/?adequacy?"+aaa+"?MajorImage";
        } else if (browserVersion == "anzuo") {
          console.log("anzuo");
           jsHandle.takePic("adequacy", "600", "600","MajorImage");
        } else if (browserVersion == "windows") {
          console.log("windows");
        }
    },
    MajorImage(picNo,img){
        this.onRead(img)
    },
	  onRead(img){
		Toast.loading({
		  mask: true,
		  duration:'500',
		  message: '正在上传和识别,请稍等...'
		});
		var self = this;
		// var base64_J = file.content.split(',');
		var J_Date = {
		  "image_no":'adequacy',
		  "image_data":img,
		  "systype": sessionStorage.getItem('systype')
		};
		self.$ajax.post('/record/uploadArchives', J_Date).then(function (res) {
		  if(res.data.error_no == 0){
			self.imgs.unshift({"url":"data:image/jpeg;base64,"+img,"id":res.data.record_id});
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
	  oversize(){
		  // Toast.fail('文件过大,请重新上传!');
      _this.show_content = true;
      _this.error_content = "文件过大,请重新上传!"
	  },
	  delItem(index){
      var _this = this;
      var delData_N = {
        "image_no":'adequacy',
        "record_id": _this.imgs[index].id,
        "systype":sessionStorage.getItem('systype')
      };
       _this.loadingbox = true;
      _this.$ajax.post('/record/delArchives',delData_N).then(function (res) {
        if(res.data.error_no == 0){
          _this.loadingbox = false;
        _this.imgs.splice(index,1)
        }else if(res.data.error_no == "-9999"){
          _this.quit = true;
          _this.content = res.data.error_info;
          _this.loadingbox = false;
        }else{
          _this.show_content = true;
          _this.error_content = res.data.error_info;
          _this.loadingbox = false;
        }
      }).catch(function (error) {
        console.log(error)
      });
    },
	  getPic(){
		var majorData={
		  "image_no":'adequacy',
		  "systype":sessionStorage.getItem('systype')
		};
		var _this = this;
		this.$ajax.post('/record/getArchivesInfo', majorData).then(function (res) {
      if(res.data.error_no == 0){
         var arr = res.data.record_data;
        for(var i=0;i<arr.length;i++){
        _this.imgs.push({
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

    majorBtn(){
       var _this = this;
       if(_this.imgs.length<2){
          _this.show_content = true;
          _this.error_content = "请至少上传2张影像资料！";
          }else{
          var datapicture = {
            "accept_no": _this.accept_no,
            "business_type": sessionStorage.getItem('business_type'),
            "business_button_type":'1',//下一步
            "business_flow": _this.$router.history.current.name,
            "accept_status":""   //"accept_status":_this.accept_status,当需要更改状态时再传值
          };
          _this.loadingbox = true;
          _this.$ajax.post('/flow/updateAccept', datapicture).then(function (res) {
            _this.loadingbox = false;
            if(res.data.error_no == 0){
              _this.$router.push({name: res.data.business_flow});
            } else if(res.data.error_no == '-9999' ){
              _this.quit = true;
              _this.content = res.data.error_info;
            } else{
               _this.show_content = true;
               _this.error_content = res.data.error_info;
            }
        })
      }
    },
	  onClickRight(){
      this.$router.push({name:'MyBussiness'});
    },
	  onClickLeft(){
        var _this = this;
				if( _this.accept_no != ""  &&  _this.accept_no != undefined ){
          var datatop = {
            "accept_no": _this.accept_no,
            "business_type":sessionStorage.getItem('business_type'),
            "business_button_type":'0', //上一步 按钮类型
            "business_flow": _this.$router.history.current.name,
            "accept_status":""   //"accept_status":_this.accept_status,当需要更改状态时再传值
          };
          _this.loadingbox = true;
          _this.$ajax.post('/flow/updateAccept', datatop).then(function (res) {
             _this.loadingbox = false;
             if(res.data.error_no == 0){
                _this.$router.push({name: res.data.business_flow});
             } else if(res.data.error_no == '-9999' ){
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
}
</script>

<style lang="less" scoped>
    .uploadReport{
        background: white;
        height: 100%;
        .upload{
            border:1px solid lightgrey;
            float: left;
        }
        .upload_img{
            width: 100%;
            height: 100%;
        }
        .inner_img{
            width: 100%;
            margin-left: 3%;
        }
        .contain_a{
            width: 139px;
            height: 130px;
            margin: 3% 2%;
            float: left;
            img{
                height: 100%;
            }
        }
        .upload_a{
            width: 135px;
            height:135px;
            margin-left: 5%;
            margin-top: 2%;
            float: left;
            font-size: 13px;
            color: rgb(152, 158, 164);
        }
        .del{
          position: absolute;
          top: -9%;
          right: -5%;
          color: gray;
          font-size: 2rem;
        }
        .reportKind{
            font-size: 16px;
            text-indent: 20px;
            width: 100%;
            height: 43px;
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
        .hint{
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
    .idcardUploadTips{
      width: 88%;
      margin: 0 auto;
      height: 112px;
        div{
          float: left;
          text-align: left;
        }
    }
    .nextmajorBtn{
       width: 84%;
       line-height: 25px;
       height: 44px;
       color: white;
       text-align: center;
       background: #49a5ff;
       margin-top: 2%;
    }
    .divcase{
      width: 100%;
      height: 60px;
      background: white;
      position: fixed;
      left: 0;
      bottom: 0;
    }
    .reportKind{
      p{
         float: left;
        color:#999999;
      }
      img{
        float: left;
        width: 5%;
        padding: 3px 5px;
      }
    }
      //ipad竖屏
    @media only screen and (min-device-width : 768px) and (max-device-width : 1024px){
      .divcase{
        bottom: 3%;
      }
       .reportKind img{
         width: 20px;
         height: 20px;
       }
       .uploadbox img{
         
       }
       .upload_a{
         margin-left: 3%;
       }
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
    .materIalsboxWarp{
      width: 100%;
      height: 100%;
      position: fixed;
      top: 0;
      background: white;
      z-index: 99;
    }
    .closeTwo{
      position: absolute;
      bottom: 0;
      left: 46%;
    }
}

</style>
