<template>
    <div class="question">
      <van-nav-bar
        title="投资者信息采集问卷"
        left-text=""
        right-text="关闭"
        left-arrow
        right-arrow
        @click-left="onClickLeft"
        @click-right="onClickRight"/>
      <!--导航条-->
      <navigation/>
      <div v-for="(question, index) in questionForm.questions" :key="question.questionId">
    <div style="width: 96%;margin: 0 auto;">
    <van-radio-group
        :label='question.questionId+". "+question.questionContent'
        :prop="'questions.'+index+'.answer'"
        v-model="question.answer"
        v-if="question.questionType==0 || question.questionType==2"
    >
    <div class="question_title">{{question.questionId+". "+question.questionContent}}</div>
    <div class="raiod choose">
        <van-radio
            :key="'radio'+index"
            :name="ans"
            v-for="(ans, index) in question.options" >
            {{ans.optionContent}}
        </van-radio>
    </div>
    </van-radio-group>
    </div>
        <div @click = 'checkboxChange($event)' style="width: 96%;margin: 0 auto;">
            <van-checkbox-group
                v-model="question.default_answer"
                :prop="'questions.'+index+'.default_answer'"
                v-if="question.questionType==1"
            >
            <div class="question_title" :id="'checkbox'+question.question_no">{{question.questionId+". "+question.questionContent}}（可复选）</div>
            <div class="check choose">
                <van-checkbox :key="'checkbox'+index" :name="ans" v-for="(ans, index) in question.options">{{ans.optionContent}}
                </van-checkbox>
            </div>
            </van-checkbox-group>
        </div>
    </div>
	<van-button bottom-action @click="submitQuestionForm">下一步</van-button>
    <popUp :tips="error_content" :showBox="show_content" @notarize="notarize"></popUp>
    <quit :content="content" :quit="quit"></quit>
    <waitPop :loadingbox="loadingbox"></waitPop>
	</div>
</template>
<script>
import PopUp from "../../components/popUp.vue";
import Quit from "../../components/quit.vue";
import WaitPop from "../../components/waitPop.vue";
import Navigation from "../../components/navigation.vue";
export default {
      components: {
        PopUp,
        WaitPop,
        Quit,
        Navigation
      },
      data() {
        return {
            loadingbox: false,
            quit:false,
            content:"",
            isOut: "false",
            show_content: false,
            error_content:"",
            acceptNo:"",
            radio: [],
            question_no: "",
            saved_answer: "",
            paper_id:'',
            questionForm: {
                name: [],
                questions: [],
            }
        }
    },
        created(){
            var _this = this;
			// 开始
				_this.qrCode ='data:image/png;base64,'+sessionStorage.getItem("qrCode");
				var datum = {
					"business_type": sessionStorage.getItem('business_type'),
				};
				_this.axios.post('/flow/qryAcceptInfo', datum).then(function (res) {
                    if(res.data.error_no == 0){
                        _this.acceptNo = res.data.accept_no;
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
            var postData = {
               'paper_type':'major',
            };
			this.$ajax.post('/adequacy/getPaperAnswer',postData).then(function (res) {
                if(res.data.error_no == 0){
                    var arr = JSON.parse(res.data.question_data).paperData;
                    _this.paper_id = JSON.parse(res.data.question_data).paperId;
                     console.log(JSON.parse(res.data.question_data).paperId)
                for(var i=0;i<arr.length;i++){
                    arr[i].index = i+1;
                    if(arr[i].questionType == '0'){
                    arr[i].answer=''
                    }else{
                    arr[i].default_answer = []
                    }
                }
            _this.questionForm.questions = arr;
          //获取答案
          var qryData = {
            'paper_id':_this.paper_id+"",
            'qry_status':'0'
          };
          _this.$ajax.post('/adequacy/qryInvestorResult', qryData).then(function (res) {
            if(res.data.error_no == 0){
              var arrs = JSON.parse(res.data.paper_answer).paperData;
              for(var i=0;i<arrs.length;i++){
                if(arrs[i].questionType == '1'){
                  arrs[i].default_answer = [];
                  if(arrs[i].answer != undefined){
                    var answerList = arrs[i].answer.split("^");
                    for(var m =0;m<answerList.length;m++) {
                      var options =arrs[i].options
                      for(var n=0;n<options.length;n++){
                        if(options[n].optionId == answerList[m]){
                          arrs[i].default_answer.push(options[n])
                        }
                      }
                    }
                  }
                }else{
                  arrs[i].index = i+1;
                  var options = arrs[i].options;
                  for(var j=0;j<options.length;j++){
                    if(options[j].optionId == arrs[i].answer){
                      arrs[i].answer = options[j];
                    }
                  }
                }

              }
              _this.questionForm.questions = arrs;
            }else if(res.data.error_no == "-9999"){
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
        }else{
            _this.error_content = res.data.error_info;
            _this.show_content = true;
        }
        }).catch(function (error) {
            console.log(error)
        });
        },
            methods:{
                notarize(){
                    this.show_content = false;
                },
                checkboxChange(event){
                    var d = event.target.parentNode;
                    // 获取到van_radio_group然后进行变色
                    var dd = d.parentNode;
                    dd.style.color='#2c3e50';
                    var ddd = dd.parentNode;
                    ddd.style.color='#2c3e50';
                },
                radioChange(){
                    var event = window.event || arguments.callee.caller.arguments[0];
                    var d = event.target.parentNode;
                    // 获取到van_radio_group然后进行变色
                    var dd = d.parentNode;
                    dd.style.color='#2c3e50';
                    var ddd = dd.parentNode;
                    ddd.style.color='#2c3e50';
                    //var Mid = dd.id || ddd.id;  //锚点id
                    //window.location.hash='#Mid'
                },
                onClickLeft() {
                    var _this = this;
                    if( _this.acceptNo != "" && _this.acceptNo != undefined ){
                    var data = {
                        "accept_no": _this.acceptNo,
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
                        } else if(res.data.error_no == "-9999"){
                            _this.quit = true;
                            _this.content = res.data.error_info;
                            _this.loadingbox = false;
                        }else{
                            _this.loadingbox = false;
                            _this.show_content = true;
                            _this.error_content = res.data.error_info;
                        }
                    }).catch(function (error) {
                        console.log(error)
                    });
                }
            },
            onClickRight() {
                this.$router.push({name:'MyBussiness'})
            },
            submitQuestionForm() {
                var _this = this;
                var arr = _this.questionForm.questions;
                if(arr==false){
                  return;
                }
                var result = '';
                var checkBoxRsult='';
                for(var i=0; i<arr.length; i++){
                    if(arr[i].questionType == '1'){
                        var xuanxiang =arr[i].default_answer;
                        var xuan = [];
                        for(var j=0; j<xuanxiang.length; j++){
                            var A = xuanxiang[j].optionId;
                            xuan.push(A);
                        }
                        if(xuan.length == 0){
                            _this.show_content = true;
                            _this.error_content = "您还有未选择的题！"
                            return
                        }
                        var checkBox = arr[i].question_no +'&'+ xuan.join("^");
                        checkBoxRsult+= checkBox+'|';
                    }else{
                        if(arr[i].answer == undefined){
                            _this.show_content = true;
                            _this.error_content = "您还有未选择的题！";
                            return
                        } else{
                            if(arr[i].answer.optionId == undefined){
                                _this.show_content = true;
                                _this.error_content = "您还有未选择的题！";
                                return
                            }
                        }
                        var radio = arr[i].question_no +'&'+arr[i].answer.optionId+"|";
                        result += radio
                    }
                    var ans = result+checkBoxRsult;
                }
                var ans2 = ans.substring(0, ans.length - 1);
                var savaData = {
                    'paper_id': _this.paper_id + '',
                    'paper_answer':ans2
                };
                _this.loadingbox = true;
                _this.$ajax.post('/adequacy/submitMajorAnswers',savaData).then(function (res) {
                  _this.loadingbox = false;
                  if(res.data.error_no == 0){
                    var data = {
                        "accept_no": _this.acceptNo,
                        "business_type": sessionStorage.getItem('business_type'),
                        "business_button_type":'1',//下一步
                        "business_flow": _this.$router.history.current.name,
                        "accept_status":""     //"accept_status":_this.accept_status,当需要更改状态时再传值
                    };
                    _this.loadingbox = true;
                    _this.$ajax.post('/flow/updateAccept', data).then(function (res) {
                      _this.loadingbox = false;
                      if(res.data.error_no == 0){
                        _this.$router.push({name: res.data.business_flow});
                      }else if(res.data.error_no == '-9999' ){
                        _this.quit = true;
                        _this.content = res.data.error_info;
                        _this.loadingbox = false;
                      }else{
                        _this.show_content = true;
                        _this.error_content = res.data.error_info;
                        _this.loadingbox = false;
                      }
                    }).catch(function(error){
                        console.log(error);
                    });
                }else if(res.data.error_no == '-1801') {
                    _this.show_content = true;
                    _this.error_content = res.data.error_info;
                }else if(res.data.error_no == '-9999'){
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
        padding: 0px;
        width: 94%;
        margin: 0 auto;
    }
    .van-icon-check:before{
      margin-top: 21%;
    }
    .van-button::before{
      background: #49a5ff;
    }
    .van-radio .van-icon-checked{
      color: #49a5ff;
      //margin-top: 2%;
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
        line-height: 13px;
        border: 1px solid #333;
        width: 16px;
        height: 16px;
    }
    .van-checkbox-group{
        text-align: left;
        text-indent: 5px;
        margin-top: 20px;
        width: 97%;
        margin: 0 auto;
        border-radius: 7px;
    }
    .van-checkbox {
        color: #2c3e50;
    }
}
</style>
