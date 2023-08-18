<template>
    <div class="echrts">
        <van-nav-bar
			title="风险测评结果"
			right-text="关闭"
			left-arrow
			@click-right="onClickRight"
			@click-left="onClickLeft"/>
        <!--导航条-->
        <navigation/>
        <div id="main" class="echarts"></div>
		<div class="survey">尊敬的投资者，您所填写的风险承受能力问卷得分为{{paper_score}}分，您属于"<span style="font-weight: bold;">{{result_level}}风险能力投资者</span>"</div>
        <div class="questionResult">
            <div><span class="tips">C1、C2型:</span>投资者仅可购买相关资管产品;</div>
            <div><span class="tips">C3型:</span>投资者仅可开立商品期货账户;</div>
            <div><span class="tips">C4、C5型:</span>投资者可开立商品期货、商品期权、金融期货、原油期货账户。</div>
        </div>
		<div class="parcelBox">
			<van-button class="buttons" size="large" @click="loginNext()">下一步</van-button>
		<van-button class="buttons" size="large" @click="redo()">重新测评</van-button>
		</div>
		<popUp :tips="error_content" :showBox="show_content" @notarize="notarize"></popUp>
			<quit :content="content" :quit="quit"></quit>
		<waitPop :loadingbox="loadingbox"></waitPop>
		<showDiaLog :ShowDisplay="ShowDisplay" :titleName="titleName" :workCenter="workCenter"
			@buttonOneClick="buttonOneClick"  :buttonOne="buttonOneName"
			@buttonTwoClick="buttonTwoClick"  :buttonTwo="buttonTwoName"
			@buttonThreeClick="buttonThreeClick"  :buttonThree="buttonThreeName">
		</showDiaLog>
    </div>
</template>
<script type="text/ecmascript-6">
import ShowDiaLog from "../../components/showDiaLog.vue";
import Quit from "../../components/quit.vue";
import PopUp from "../../components/popUp.vue";
import WaitPop from "../../components/waitPop.vue";
import Navigation from "../../components/navigation.vue";
  var echarts = require('echarts');
  export default {
		components: {
			PopUp,
			WaitPop,
			Quit,
			ShowDiaLog,
      		Navigation
		},
		data() {
			return {
				paper_score:"",
				result_level:"",
				buttonOneName: "接受",
				buttonTwoName: "不接受",
				buttonThreeName: "重新测评",
				workCenter: "本次测评等级低于要求，是否接受，如接受，又需购买高于其等级的产品或服务，需要签署跨级购买特别警示协议，若不接受，则可以重新测评或直接退出。",
				ShowDisplay: false,
				titleName:"提示",
				quit:false,
				content:"",
				loadingbox:false,
				accept_no:"",
				error_content:"",
				show_content:false
			}
		},
	created(){
	  /*this.type = sessionStorage.getItem('business_type');
	  if(sessionStorage.getItem('business_type') == '14'){
		this.bus_name = '适当性测评'
		this.activeIndex = '5'
		this.type = '14'
	  }else if(sessionStorage.getItem('business_type') == '11'){
		this.bus_name = '开立期货账户'
		this.activeIndex = '1'
		this.type = '11'
	  }*/
	// 开始
	var _this= this;
    this.qrCode ='data:image/png;base64,'+sessionStorage.getItem("qrCode");
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
	  this.$ajax.post('/adequacy/qryRiskLevelConfig',{'date':new Date().getTime()}).then(function (res) {
		if(res.data.error_no == 0 ){
		}else{
		}
	  }).catch(function (error) {
			console.log(error)
	  });
	  var qryData = {
		'paper_id':this.paper_id,
		'qry_status':'0'
	  };
	  this.$ajax.post('/adequacy/qryRiskResult',qryData).then(function (res) {
		if(res.data.error_no == 0){
		  _this.paper_score = res.data.paper_score;
		  _this.result_level = res.data.result_level;
			 if (res.data.is_sign_risk_deal == '1') {
			   var businessType = sessionStorage.getItem('business_type');
			   if(businessType == "14" || businessType == "22" || businessType == "24"){
           _this.initignRiskDeal();
         }
        }
		  var type = res.data.result_level;
		  var i = _this.paper_score;   //动态获取分数显示
		  let myChart = echarts.init(document.getElementById('main'));
		  var option = {
			backgroundColor: '#FBFBFB',
			tooltip : {
			  formatter: "{a} <br/>{c} {b}"
			},
			toolbox: {
			  show : false,   //下载还原图标
			  feature : {
				mark : {show: true},
				restore : {show: true},
				saveAsImage : {show: true}
			  }
			},
			series : [
			  {
				name:'cepn',
				type:'gauge',
				center : ['50%', '60%'],    // 默认全局居中
				radius : '100%',
				min:0,
				max:100,
				startAngle:180,
				endAngle:0,
				splitNumber:10,
				axisLine: {            // 坐标轴线
				  lineStyle: { // 属性lineStyle控制线条样式
					color: [
					  [0.2, '#1d479b'],
					  [0.4, '#9d1089'],
					  [0.6, '#d3d647'],
					  [0.8, '#3912c2'],
					  [1, '#e1b710'],
					],
					width:10,
					shadowColor: '#fff', //默认透明
					shadowBlur: 50
				  }
				},
				axisTick: {            // 坐标轴小标记
				  show: true
				},
				axisLabel: {
				  textStyle: {       // 属性lineStyle控制线条样式
					fontWeight: 'bolder',
					color: '#636363',
					shadowColor : '#fff', //默认透明
					shadowBlur: 10
				  },
				  formatter:function(v){
					switch (v + '') {
					  case '0' : return '0';
					  case '20' : return '20';
					  case '40' : return '40';
					  case '60' : return '60';
					  case '80' : return '80';
					  case '100' : return '100';
					}
				  }
				},
				splitLine: {           // 分隔线
				  length :15,         // 属性length控制线长
				  lineStyle: {       // 属性lineStyle（详见lineStyle）控制线条样式
					width:1,
					color: '#fff',
					shadowColor : '#fff', //默认透明
					shadowBlur: 10
				  }
				},
				pointer: {
				  width:2,
				  shadowColor : '#fff', //默认透明
				  shadowBlur: 5
				},
				title : {
				  show: false
				},
				detail:{
				  formatter : i+"分/"+type+'型',
				  offsetCenter: [0, "30%"],
				  fontSize:20,
				  background:"red",
				},
				data:[{value: i, }]
			  }
			]
		  };
		  myChart.setOption(option);
		}else if(res.data.error_no =="-9999"){
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
    methods:{
			buttonOneClick(){
				this.ShowDisplay = false;
			},
			buttonTwoClick(){
				this.$router.push('/Index')
			},
      buttonThreeClick(){
				 var data = {
          "accept_no": this.accept_no,
          "business_type": sessionStorage.getItem('business_type'),
          "business_button_type": '0',//上一步
          "business_flow": this.$router.history.current.name,
          "accept_status": "" //"accept_status":_this.accept_status,当需要更改状态时再传值
        };
        var _this = this;
        this.$ajax.post('/flow/updateAccept', data).then(function (res) {
          if (res.data.error_no == 0) {
            _this.$router.push({
              name: res.data.business_flow,
            });
          } else if (res.data.error_no == "-9999") {
            _this.quit = true;
			  		_this.content = res.data.error_info;
          } else {
            	_this.show_content = true;
							_this.error_content = res.data.error_info;
          }
        });
			},
		 notarize(){
        this.show_content = false;
      },
			 countersign(){
          this.$router.push({name:'Index'})
      },
			ok(){
				this.showBox = false
			},
			loginNext(){
				 var _this = this;
				 var datanext = {
				  "accept_no":_this.accept_no,
				  "business_type":sessionStorage.getItem('business_type'),
				  "business_button_type":'1',//下一步
				  "business_flow":_this.$router.history.current.name,
				  "accept_status":""
				};
        _this.loadingbox = true;
				_this.$ajax.post('/flow/updateAccept',datanext).then(function (res) {
          _this.loadingbox = false;
				  if(res.data.error_no == 0){
						_this.$router.push({name:res.data.business_flow});
				  }else if(res.data.error_no == '-9999' ){
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
	  onClickRight() {
			this.$router.push({name:'MyBussiness'})
	  },
	  onClickLeft() {
		  var _this = this;
				if( _this.accept_no != ""  &&  _this.accept_no != undefined ){
				var datatop = {
					"accept_no": _this.accept_no,
					"business_type":sessionStorage.getItem('business_type'),
					"business_button_type":'0', //上一步 按钮类型
					"business_flow": _this.$router.history.current.name,
					"accept_status":""   //"accept_status":_this.accept_status,当需要更改状态时再传值
				};
				_this.$ajax.post('/flow/updateAccept', datatop).then(function (res) {
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
      redo(){
				// this.$router.push('/question')
				var _this = this;
					var data = {
					"accept_no": _this.accept_no,
					"business_type":sessionStorage.getItem('business_type'),
					"business_button_type":'0', //上一步 按钮类型
					"business_flow": _this.$router.history.current.name,
					"accept_status":""   //"accept_status":_this.accept_status,当需要更改状态时再传值
				};
				_this.axios.post('/flow/updateAccept', data).then(function (res) {
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
      },
			initignRiskDeal(){
				 var _this = this;
         _this.ShowDisplay = true;
			},
    }
  }
</script>

<style lang="less" scoped>
    .echrts{
			overflow: hidden;
        #main{
            height: 300px;
            width: 100%;
			div{
					width: 100%;
					background: yellow;
			}
        }
        canvas{
            width: 100% ;
            height: 300px;
        }
        .questionResult {
            text-align: left;
            font-weight: 500;
            width: 90%;
            margin-left: 5%;
        }
		//ipad竖屏
		@media only screen and (min-device-width : 768px) and (max-device-width : 1024px){
			.questionResult span{
				font-size: 16px;
			}

		}
        .tips{
            text-align: left;
            font-weight: 700;
            font-size: 1.4rem;
            background: #FBFBFB;
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
		.buttons{
			background: #49A5FF;
			width: 82%;
			color:white;
			border-radius: 3px;
		}
		@media only screen and (min-device-width : 1024px) and (max-device-width : 1366px){
			.buttons{
				width: 16%;
				margin-right: 62px;
				margin-left: 67px;
			}
		}
		.survey{
			width: 85%;
			margin: 0 auto;
			text-align: left;
			position: absolute;
			margin-top: -13%;
			left: 8%;
		}
		@media only screen and (min-device-width : 768px) and (max-device-width : 1024px){
			.buttons{
				width: 16%;
				margin-right: 62px;
				margin-left: 67px;
			}
			.survey{
				width: 85%;
				margin: 0 auto;
				text-align: left;
				position: absolute;
				margin-top: -5%;
				left: 8%;
			}
		}
		.parcelBox{
			 width: 100%;
			 bottom: 3%;
			 margin-top: 11%;
		}
		.van-button--large{
			height: 40px;
			line-height: 39px;
			margin-bottom: 4%;
		}
		 //ipad横屏
		@media only screen and (min-width: 1349px) and (orientation : landscape){
			.questionResult{
				width: 70%;
				margin-left: 15%;
				line-height: 27px;
			}
			.parcelBox{
				margin-top: 4%;
			}
			.van-button--large{
				margin-bottom: 2%;
			}
		}
}
</style>
