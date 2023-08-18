<template>   <!--适当性分类-->
    <div class="appropriate">
      <van-nav-bar
        title="投资者适当性分类"
        right-text="关闭"
        right-arrow
        left-arrow
        @click-right="onClickRight"
		    @click-left="onClickLeft"/>
      <!--导航条-->
      <navigation/>
      <div class="chooseKind">投资者类型选择</div>
      <div class="twoKind">
        <div class="putong tz" @click="chooseKind"  :class="{'putong':isPutong}">普通投资者</div>
        <div class="zhuanye tz" @click="chooseKind" :class="{'zhuanye':iszhuanYe}">专业投资者</div>
      </div>
      <van-button  bottom-action @click="chooseOver" class="chooseBtn">下一步</van-button>
      <div class="tips">
				<div>温馨提示：</div>
        <div>当您选择专业投资者，需要提前准备如下材料(盖章)：</div>
        <div>1、金融资产大于500万，或最近3年个人年均收入大于50万。</div>
        <div>2、金融投资经历超过2年；或金融产品设计、投资、风险管理经历超过2年；或金融高管；或金融相关注册会计师；或律师资格。</div>
      </div>
			 <popUp :tips="error_content" :showBox="show_content" @notarize="notarize"></popUp>
			 <waitPop :loadingbox="loadingbox"></waitPop>
			 <quit :content="content" :quit="quit"></quit>
    </div>
</template>
<script>
import Quit from "../../components/quit.vue";
import PopUp from "../../components/popUp.vue";
import WaitPop from "../../components/waitPop.vue";
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
						quit:false,
						content:"",
						loadingbox: false,
						error_content:"",
						show_content:false,
            classify:'',
            isPutong:false,
            iszhuanYe:false,
						accept_no:'',
						choose:"",
      			PopUp,
          }
       },
       created(){
		   // 开始控制流程
			 	  var _this = this;
					var data = {
						"business_type":sessionStorage.getItem('business_type'),
					};
					_this.axios.post('/flow/qryAcceptInfo',data).then(function (res) {
						if(res.data.error_no == 0){
							_this.accept_no = res.data.accept_no;
							_this.business_flow=res.data.business_flow;
							if(_this.$router.history.current.name != res.data.business_flow){
								_this.$router.push({name: res.data.business_flow});
							}
						}else if(res.data.error_no == '-9999'){
							_this.quit = true;
							_this.content = res.data.error_info;
						}else{
							_this.error_content = res.data.error_info;
							_this.show_content = true;
					  }
					}).catch(function (err) {
						console.log(err)
					});
		  // 结束
			var _this = this;
			var queryData = {};
			var divs = document.getElementsByClassName('tz');
			_this.$ajax.post('/adequacy/queryAdeType',queryData).then(function (res) {   //查询适当性专业或者普通
				if(res.data.adequacy_type == '1'){  //专业投资者
					_this.choose ="专业投资者";
					_this.isZhuanye = true;
				for(var i=0; i<divs.length; i++){
					if(divs[i].innerHTML == '专业投资者'){
							divs[i].style.background='#49A5FF'
							divs[i].style.color='white'
    			}
	 			}
		   } else if (res.data.adequacy_type == '2') {
					_this.isPutong = true;
					_this.choose ="普通投资者";
			 	for(var i=0;i<divs.length;i++){
			   	if(divs[i].innerHTML == '普通投资者'){
						divs[i].style.background='#49A5FF'
						divs[i].style.color='white'
			    }
			  }
		   }else{
				 	_this.choose ="未分类";
			 }
		 }).catch(function (error) {
		   		console.log(error);
		 });
  	},
      methods:{
				 notarize(){
        	 this.show_content = false;
      	 },
	        onClickRight() {
	           this.$router.push({name:'MyBussiness'});
	        },
					onClickLeft(){
						var _this = this;
						if(_this.accept_no != "" && _this.accept_no != undefined){
							var data = {
								"accept_no":_this.accept_no,
								"business_type":sessionStorage.getItem('business_type'),
								"business_button_type":'0', //上一步 按钮类型
								"business_flow": _this.$router.history.current.name
							};
              _this.loadingbox = true;
							_this.$ajax.post('/flow/updateAccept',data).then(function (res) {
								_this.loadingbox = false;
								if(res.data.error_no == 0){
									 _this.$router.push({name: res.data.business_flow});
								}else if(res.data.error_no == "-9999"){
									 _this.quit = true;
									 _this.content = res.data.error_info;
									 _this.loadingbox = false;
								}else{
									 _this.show_content = true;
									 _this.error_content = res.data.error_info;
									 _this.loadingbox = false;
								}
							});
						}
					},
	        chooseKind(){
	           var tzs = document.getElementsByClassName('tz');
							for(var i=0; i<tzs.length; i++){
								tzs[i].style.border='2px solid #49A5FF'
								tzs[i].style.background='#F6F6F6'
								tzs[i].style.color='gray'
							}
							event.target.style.border ='2px solid #49A5FF';
							event.target.style.background ='#49A5FF';
							event.target.style.color ='white';
							this.classify = event.target.innerHTML;
							this.choose = event.target.innerHTML;
	        	},
	    	chooseOver(){
					if(this.choose == ""){
						this.show_content = true;
					  this.error_content = "请选择适当性！"
					} else {
							if(this.choose == '普通投资者'){
								 var adequacy_type = '2'
							}else if(this.choose == '专业投资者'){
								var adequacy_type = '1'
							}else{
								this.show_content = true;
					  		this.error_content = "请选择适当性！"
								return
							}
						var saveData = {
							"adequacy_type": adequacy_type,
						};
						var _this = this;
						_this.loadingbox = true;
						_this.$ajax.post('/adequacy/saveAdeType',saveData).then(function (res) {
							_this.loadingbox = false;
							if(res.data.error_no == 0){
									var data = {
										"accept_no": _this.accept_no,
										"business_type":sessionStorage.getItem('business_type'),
										"business_button_type":'1',//下一步
										"business_flow": _this.$router.history.current.name,
										"accept_status":""  //"accept_status":_this.accept_status,当需要更改状态时再传值
									};
								_this.loadingbox = true;
								_this.$ajax.post('/flow/updateAccept',data).then(function (res) {
									_this.loadingbox = false;
									if(res.data.error_no == 0){
											_this.$router.push({
											name: res.data.business_flow,
									});
									} else if (res.data.error_no == '-9999'){
											_this.quit = true;
											_this.content = res.data.error_info;
											_this.loadingbox = false;
									} else {
										_this.show_content = true;
					  				_this.error_content = res.data.error_info;
										_this.loadingbox = false;
									}
								}).catch(function (err) {
										console.log(err)
								});
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
			   }
			 },
		 }
  	}
</script>
<style lang="less">
.appropriate{
  height: 100%;
    .chooseKind{
      font-size: 16px;
      text-align: left;
      margin-left: 20px;
      letter-spacing: 2px;
      margin-top: 10px;
    }
    .twoKind{
      margin: 0 auto;
      display: flex;
      flex-direction: row;
      justify-content: space-around;
      padding: 45px 15px;
		.tz{
			height: 60px;
			line-height: 60px;
			width: 100px;
			border-radius: 10px;
		}
    }
    .chooseBtn{
      width: 88%;
      border-radius: 5px;
      background: dodgerblue;
    }
    .tips{
      font-size: 14px;
      color: gray;
      text-align: left;
			width: 87%;
			margin: 0 auto;
			padding-top: 5%;
			line-height: 23px;
    }
   .putong{
      border: 2px solid #49a5ff;
    }
    .zhuanye{
      border: 2px solid #49a5ff;
    }
	 @media only screen and (min-width: 1349px) and (orientation : landscape){
		.twoKind{
			width: 65%;
		}
		.chooseKind{
			margin-left: 6%;
		}
	 }
}

</style>
