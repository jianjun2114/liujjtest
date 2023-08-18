<template>
    <div class="question">
        <van-nav-bar
					title="回访问卷"
					right-text="关闭"
					right-arrow
					@click-right="onClickRight"/>
        <!--导航条-->
        <navigation/>
        <div v-for="(question, index) in questionForm.questions" style="width: 94%; margin: 0 auto;">
            <div>
                <van-radio-group
                        :key="question.questionId"
                        :label='question.questionId+". "+question.questionContent'
                        :prop="'questions.'+index+'.answer'"
                        v-model="question.answer"
                        v-if="question.questionType==0 || question.questionType==2">
                    <div class="question_title" >
                        {{index+1+". "+question.questionContent}}
                    </div>
                    <div class="raiod choose">
                        <van-radio :key="'radio'+index"
                                   :name="ans"
                                   v-for="(ans, index) in question.options" >
                            {{ans.optionContent}}
                        </van-radio>
                    </div>
                </van-radio-group>
            </div>
            <div @click = 'checkboxChange($event)'>
                <van-checkbox-group
                        :key="'checkbox_group'+index"
                        v-model="question.answer_list"
                        :label='question.questionId+". "+question.questionContent'
                        :prop="'questions.'+index+'.default_answer'"
                        v-if="question.questionType==1">
                    <div class="question_title"
                         :id="'checkbox'+question.question_no">
                        {{index+1+". "+question.questionContent}}
                    </div>
                    <div class="check choose">
                        <van-checkbox
                                :key="'checkbox'+index"
                                :name="ans"
                                v-for="(ans, index) in question.options" >
                            {{ans.optionContent}}
                        </van-checkbox>
                    </div>
                </van-checkbox-group>
            </div>
        </div>
        <van-button  bottom-action @click="closeIt_onlineBack" class="naxtbutton">下一步</van-button>
        <popUp :tips="error_content" :showBox="show_content" @notarize="notarize"></popUp>
				<van-popup v-model="popUp" :close-on-click-overlay="false">
					<div class="hint">提示</div>
					<div style="width:93%; margin: 0 auto; padding-botom: 3%">
						<p style="text-align: left; font-size: 16px; color:#333333; line-height: 24px; padding-top: 5%;">{{tips}}</p>
					</div>
					<div class="boxes_button">
						<div class="cancel" @click="cancel">取消</div>
						<div class="cancel abolish" @click="ensure">确定</div>
					</div>
				</van-popup>
				<quit :content="content" :quit="quit" @defeated="defeated"></quit>
				<waitPop :loadingbox="loadingbox"></waitPop>
    </div>
</template>
<script>
import WaitPop from "../../components/waitPop.vue";
import Router from 'vue-router'
import { Toast,Dialog,RadioGroup, Radio} from 'vant';
import PopUp from "../../components/popUp.vue";
import Quit from "../../components/quit.vue";
import Navigation from "../../components/navigation.vue";
  export default {
	components: {
		PopUp,
	  Quit,
		Dialog,
		WaitPop,
    Navigation
	},
	data() {
	  return {
			loadingbox: false,
			quit:false,
			content:"",
			popUp: false,
			tips:"尊敬的投资者您好，回访结果可能对您的业务办理造成影响，请您再次确认回访问卷答题是否准确。",
			isOut: "false",
			error_content:"",
			show_content:false,
			list: ['a', 'b', 'c'],
			result: ['a', 'b'],
			radio: [],
			question_no: "",
			saved_answer: "",
		questions: [
		  // {
			// "answer_content": {
			//   "1": "A. 18-30岁",
			//   "2": "B. 31-40岁",
			//   "3": "C. 41-50岁\t",
			//   "4": "D. 51-60岁",
			//   "5": "E. 超过60岁 "
			// },
			// "answer_score": 0,
			// "default_answer": [],
			// "organ_flag": "",
			// "paper_type": "1",
			// "prodta_no": "",
			// "question_content": "您的年龄是？",
			// "question_id": "1",
			// "question_kind": "0",
			// "question_no": 1,
			// "question_type": "1",
			// "saved_answer": []
		  // }
		  // , {
			// "answer_content": {"6": "A. 高中或以下", "7": "B. 大学专科 ", "8": "C. 大学本科 \t", "9": "D. 硕士及以上 "},
			// "answer_score": 0,
			// "default_answer": [],
			// "organ_flag": "",
			// "paper_type": "1",
			// "prodta_no": "",
			// "question_content": "您的最高学历是",
			// "question_id": "2",
			// "question_kind": "0",
			// "question_no": 2,
			// "question_type": "1",
			// "saved_answer": []
		  // },
		  // {
			// "answer_content": {
			//   "10": "A.  债券、货币市场基金、债券基金等固定收益类投资品种",
			//   "11": "B.  股票、混合型基金、偏股型基金、股票型基金等权益类投资品种  ",
			//   "12": "C.期货、融资融券等杠杆性产品"
			// }, "answer_score": 0, "default_answer": [], "organ_flag": "", "paper_type": "1", "prodta_no": "",
			// "question_content": "您打算重点投资于哪一类投资品种？", "question_id": "3", "question_kind": "0", "question_no": 3,
			// "question_type": "1", "saved_answer": []
		  // },
		  // {
			// "answer_content": {
			//   "13": "A.  房地产、建筑业  ",
			//   "14": "B.  教育、卫生、社会保障和社会福利业",
			//   "15": "C.  金融、IT业  ",
			//   "16": "D.  文化、旅游、体育和娱乐业、传媒业",
			//   "17": "E.  公共管理和社会组织  ",
			//   "18": "F.  制造业、交通运输、仓储和邮政业、批发零售业、外贸",
			//   "19": "G.  住宿、租赁、餐饮业、中介业、商务服务业",
			//   "20": "H.  离岸公司",
			//   "21": "I.  废品收购、博彩、艺术品收藏、拍卖、典当、珠宝行业",
			//   "22": "J.  其他"
			// },
			// "answer_score": 0,
			// "default_answer": [],
			// "organ_flag": "",
			// "paper_type": "1",
			// "prodta_no": "",
			// "question_content": "您所从事的行业：",
			// "question_id": "4",
			// "question_kind": "0",
			// "question_no": 4,
			// "question_type": "1",
			// "saved_answer": []
		  // },
		  // {
			// "answer_content": {
			//   "23": "A.  小于3万元  ", "24": "B.  3 万 至 10万元  ", "25": "C.  10万 至 50万元   ", "26": "D.  50万 至 100万元  ",
			//   "27": "E.  大于100万元  "
			// },
			// "answer_score": 0,
			// "default_answer": [],
			// "organ_flag": "",
			// "paper_type": "1",
			// "prodta_no": "",
			// "question_content": "您的家庭年收入情况：",
			// "question_id": "5",
			// "question_kind": "0",
			// "question_no": 5,
			// "question_type": "1",
			// "saved_answer": []
		  // },
		  // {
			// "answer_content": {
			//   "28": "A.  10%以下  ",
			//   "29": "B.  10% - 30%  ",
			//   "30": "C.  30% - 50%   ",
			//   "31": "D.  50% - 70%",
			//   "32": "E.  70%以上  "
			// }, "answer_score": 0, "default_answer": [], "organ_flag": "", "paper_type": "1",
			// "prodta_no": "", "question_content": "您家庭预计进行证券投资的资金占家庭现有总资产（不含自住、自用房产及汽车等）的比例是：",
			// "question_id": "6", "question_kind": "0", "question_no": 6, "question_type": "1", "saved_answer": []
		  // },
		  // {
			// "answer_content": {"33": "A.  5年以上", "34": "B.  1年到5年", "35": "C.  1年以内  "},
			// "answer_score": 0,
			// "default_answer": [],
			// "organ_flag": "",
			// "paper_type": "1",
			// "prodta_no": "",
			// "question_content": "您用于证券投资的大部分资金不会用作其他用途的时间段为：",
			// "question_id": "7",
			// "question_kind": "0",
			// "question_no": 7,
			// "question_type": "1",
			// "saved_answer": []
		  // },
		  // {
			// "answer_content": {"36": "A.  过高  ", "37": "B.  偏高  ", "38": "C.  正常  ", "39": "D.  偏低  "},
			// "answer_score": 0,
			// "default_answer": [],
			// "organ_flag": "",
			// "paper_type": "1",
			// "prodta_no": "",
			// "question_content": "有一位投资者一个月内做了15笔交易（同一个品种买卖计1笔，您认为这样的频率：",
			// "question_id": "8",
			// "question_kind": "0",
			// "question_no": 8,
			// "question_type": "1",
			// "saved_answer": []
		  // },
		  // {
			// "answer_content": {
			//   "40": "A.  10%以下  ",
			//   "41": "B.  10% - 30%",
			//   "42": "C.  30% - 50%  ",
			//   "43": "D.  50%以上  "
			// },
			// "answer_score": 0,
			// "default_answer": [],
			// "organ_flag": "",
			// "paper_type": "1",
			// "prodta_no": "",
			// "question_content": "您认为自己能够承受的最大损失为：",
			// "question_id": "9",
			// "question_kind": "0",
			// "question_no": 9,
			// "question_type": "1",
			// "saved_answer": []
		  // },
		  // {
			// "answer_content": {
			//   "44": "A.  实现资产大幅增长，愿意承担较高风险  ", "45": "B.  产生更多收益，" +
			//   "可以承担一定投资风险  ", "46": "C.  尽可能保证本金安全，不介意较低收益率  ", "47": "D.  资产保值，不愿承担投资风险"
			// },
			// "answer_score": 0,
			// "default_answer": [],
			// "organ_flag": "",
			// "paper_type": "1",
			// "prodta_no": "",
			// "question_content": "  当您进行投资时，您的首要目标是：",
			// "question_id": "10",
			// "question_kind": "1",
			// "question_no": 11,
			// "question_type": "1",
			// "saved_answer": []
		  // },
		  // {
			// "answer_content": {
			//   "48": "A.  非常丰富：我是一位非常有经验的投资者，参与过权证、期货或创业板等高风险产品的交易    ",
			//   "49": "B.  丰富：我是一位有经验的投资者，参与过股票、基金等产品的交易，并倾向自己做出投资决策  ",
			//   "50": "C.  一般：除了银行活期账户和定期存款外，我购买过基金、保险等理财产品，但还需要进一步指导  ",
			//   "51": "D.  有限：除了银行活期账户和定期存款外，我基本没有投资经验  "
			// },
			// "answer_score": 0,
			// "default_answer": [],
			// "organ_flag": "",
			// "paper_type": "1",
			// "prodta_no": "",
			// "question_content": "您的投资经验可以被概括为：",
			// "question_id": "11",
			// "question_kind": "0",
			// "question_no": 10,
			// "question_type": "1",
			// "saved_answer": [],
		  // }
		],
		paper_id:'',
		accept_no:"",
		questionForm: {
		  name: [],
		  questions: [],
      resultSubmit:""
		}
	  }
	},
	// 初始化的
	created(){
	  var _this = this;
  // 开始
	  var data = {
  		"business_type":sessionStorage.getItem('business_type'),
	  };
	  this.axios.post('/flow/qryAcceptInfo',data).then(function (res) {
		if(res.data.error_no == 0){
			_this.accept_no = res.data.accept_no;
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
	  //获取问卷，首次展示
	  var postfirst = {
			'time':new Date().getTime(),
			"systype":sessionStorage.getItem('systype'),
			"business_type": sessionStorage.getItem('business_type')
	  };
	  this.$ajax.post('/callBack/qryRevisitPaper',postfirst).then(function (res) {
		if(res.data.error_no == 0){
		  _this.questionForm.questions=res.data.question_data.paperData;
		  console.log(res.data.question_data.paperData)
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
	  //
	  //调查问卷回访
	  var postData = {
		'time':new Date().getTime(),
		"systype":sessionStorage.getItem('systype'),
	  };
	  this.$ajax.post('/callBack/qryRevisitAnswer',postData).then(function (res) {
		if(res.data.error_no == 0){
		  var arrs = JSON.parse(res.data.question_data).paperData;
		  if(arrs!=undefined){
        for(var i=0;i<arrs.length;i++){
          if(arrs[i].questionType == '1'){
            arrs[i].default_answer = [];
            if(arrs[i].answer != undefined){
              var answerList = arrs[i].answer.split("^");
              for(var m =0;m<answerList.length;m++){
                var options =arrs[i].options
                for(var n=0;n<options.length;n++){
                  if(options[n].optionId == answerList[m]){
                    arrs[i].default_answer.push(options[n])
                  }
                }
              }
            }
          }
          arrs[i].index = i+1;
          var options = arrs[i].options;
          for(var j=0;j<options.length;j++){
            if(options[j].optionId == arrs[i].answer){
              arrs[i].answer = options[j];
            }
          }
        }
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
	methods:{
		ensure(){
		  var _this = this;
      _this.popUp = false;
      var postData = {
      	"paper_answer":_this.resultSubmit,
      	"systype":sessionStorage.getItem('systype'),
      }
      _this.loadingbox = true;
      _this.$ajax.post('/callBack/saveRevisitPaper',postData).then(function (res) {
        _this.loadingbox = false;
      	if(res.data.error_no == 0){
      		var data = {
      			"accept_no":_this.accept_no,
      			"business_type":sessionStorage.getItem('business_type'),
      			"business_button_type":'1',//下一步
      			"business_flow":_this.$router.history.current.name,
      			"accept_status":""     //"accept_status":_this.accept_status,当需要更改状态时再传值
      	};
      	_this.loadingbox = true;
      	_this.$ajax.post('/flow/updateAccept',data).then(function (res) {
          _this.loadingbox = false;
      		if(res.data.error_no == 0){
      				_this.$router.push({name: res.data.business_flow});
      		}else if(res.data.error_no == '-9999' ){
      			_this.quit = true;
        	  _this.content = res.data.error_info;
      		}else{
					_this.error_content = res.data.error_info;
					_this.show_content = true;
      		}
      	}).catch(function (error) {
      			console.log(error)
      	});
      	}else if(res.data.error_no == "-9999"){
      			_this.quit = true;
        		_this.content = res.data.error_info;
      	}else if(res.data.error_no == "-1"){
      		_this.error_content = "未答题"
      		_this.show_content = true;
      	}else{
				_this.error_content = res.data.error_info;
				_this.show_content = true;
      	}
      }).catch(function (error) {
      	console.log(error)
      });
		},
		cancel(){
			this.popUp = false;
		},
			defeated(){
				this.show_quit = false;
				if(this.isOut){
						this.$router.push({name:"Index"});
					}
				},
    notarize(){
      this.show_content = false;
    },
	  checkboxChange(event){
			console.log(event.target);
			var d = event.target.parentNode;
			//console.log('2',d.parentNode);  获取到van_radio_group然后进行变色
			var dd = d.parentNode;
			dd.style.color='#2c3e50';
			var ddd = dd.parentNode;
			ddd.style.color='#2c3e50';
			},
			radioChange(){
				var event = window.event || arguments.callee.caller.arguments[0];
				var d = event.target.parentNode;
				//console.log('2',d.parentNode);  获取到van_radio_group然后进行变色
				var dd = d.parentNode;
				dd.style.color='#2c3e50';
				var ddd = dd.parentNode;
				ddd.style.color='#2c3e50';
				//var Mid = dd.id || ddd.id;  //锚点id
				//window.location.hash='#Mid'
	  	},
	  onClickRight() {
			this.$router.push({name:'MyBussiness'})
	  },
	  closeIt_onlineBack(){  //下一步
			var _this = this;
			var arr = _this.questionForm.questions;
			var result = '';
			// 获取上传参数，格式2000&1^2^3|3000&4
			var questions = _this.questionForm.questions;
			var strRadio = '';
			var strCheckbox = '';
			var result = '';
			for(var i=0; i<questions.length; i++){
				console.log(questions[i]);
				if(questions[i].questionType == "1"){   //题的类型
					if(questions[i].answer_list != '' && questions[i].answer_list != undefined){
						var ansArr = questions[i].answer_list;
						//循环这个答案列表，找到每一个选项
						var arr = [];
						for(var j = 0; j<ansArr.length; j++){
							arr.push(ansArr[j].optionId);
						}
						var ans_new = arr.join('^');
						var temp= questions[i].questionId + '&' +ans_new+'|';
						strCheckbox +=temp;
					}else{
						_this.error_content = "您还有未完成的题目！"
						_this.show_content = true;
						return;
					}
				}else{
					if(questions[i].answer != ''){
						var questionId = questions[i].questionId;
						var questions_no = questions[i].answer;
						var strTemp =questionId+'&'+questions_no.optionId;
						strRadio += strTemp+'|'
					}else{
						_this.error_content = "您还有未完成的题目！"
						_this.show_content = true;
						return;
					}
				}
				result = strRadio +strCheckbox;
			}
			_this.resultSubmit = result;
			_this.popUp = true;
	  },
	},
}
</script>
<style lang="less">
    .question{
        font-size: 1.6rem;
        .question_title{
            padding: 5px;
            font-size: 16px;
            color: black;
        }
        .choose {
            background: white;
            border-radius: 6px;
            padding: 10px;
						width: 87%;
    				margin: 0 auto;
        }
        .check{
            width: 85%;
            margin-left: 5%;
            border-radius: 6px;
        }
        .van-icon-check:before{
            margin-top: 21%;

        }
        .van-button::before{
            background: #49a5ff;
        }
        .van-radio .van-icon-checked{
            color: #49a5ff;
            margin-top: 2%;
        }
		@media only screen and (min-device-width : 768px) and (max-device-width : 1024px){
			.van-radio .van-icon-checked{
				margin-top: 5px;
			}
		}
        .van-checkbox--checked{
            color: #ffffff;
            background-color: #49a5ff;
        }
        .van-icon-success:before{
            margin-left: -9px;
        }
        .van-checkbox__icon, .van-checkbox__label {
            display: inline;
            vertical-align: middle;
            line-height: 20px;
        }
        .van-checkbox__icon--round .van-icon {
            border-radius:0;
        }
        .van-checkbox__icon .van-icon {
            line-height: 15px;
            border: 1px solid #aaa;
            width: 16px;
            height: 16px;
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
			line-height: 34px;
			color: #ffffff;
		}
		.van-popup {
			width: 80%;
			height: auto;
		}
		.abolish{
			float: right;
		}
		.boxes_button{
			width: 53%;
			margin: 0 auto;
		}
		.naxtbutton{
			width: 85%;
			line-height: 36px;
			border: 0;
			border-radius: 0;
			font-size: 16px;
			color: #ffffff;
			background-color: #49a5ff;
			margin-top: 20px;
			margin-bottom: 20px;
			border-radius: 3px;
		}
		.question .van-icon-success:before{
				margin-left: -10px;
		}
		@media only screen and (min-width: 1349px) and (orientation : landscape){
			.choose{
				width: 95%;
			}
			.van-radio .van-icon-checked{
				margin-top: 5px;
			}
		}


    }

</style>
