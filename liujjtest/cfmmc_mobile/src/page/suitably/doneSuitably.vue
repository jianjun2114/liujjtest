<template>
    <div class="doneSuitably">
        <van-nav-bar title="适当性测评"
          right-text="关闭"
          :left-arrow="isShow"
          @click-left="onClickLeft"
          @click-right="onClickRight"/>
        <!--导航条-->
        <navigation/>
        <div class="warpDiv">
            <div class="done" v-if="this.adequacy_type =='2'"> 尊敬的投资者您好，您最近一次测评的分数是 <span style="color: orangered;font-size: 20px">{{ade_score}}</span> 分，风险等级为<span style="color: orangered;font-size: 20px"> {{risk_level}}</span>级，测评时间是{{time}}。<span v-if="this.if_flag == '1'">您的风险测评已过期，请重新测评。</span></div>
            <div class="done" v-if="this.adequacy_type =='1'">您是 <span style="color: dodgerblue;">{{adequacyType}}</span>，已于{{time}}完成适当性测评。但可能存在信息不完整情况，如需完善部分信息，请联系期货公司临柜办理，联系电话:{{broker_hotline}}</div>
            <div class="done" v-if="this.adequacy_type =='0'">根据监管部门要求，为有效落实适当性管理要求，维护投资者合法权益，向投资者销售适当的产品或者提供适当的服务，请根据您的实际情况完成投资者风险承受能力评估问卷</div>
        </div>
        <div  class="suitBtn" v-if="this.adequacy_type =='1' && (this.type == '11' || this.type == '12')"><!--专业-->
            <van-button class="buttons" @click="goChooseFutures('1')">继续开户</van-button>
        </div>
        <div class="suitBtn" v-if="this.adequacy_type =='2' && (this.type == '11' || this.type == '12')"><!--普通-->
            <van-button class="buttons" @click="goChooseFutures('2')">重新测评</van-button>
            <van-button class="buttons" @click="goChooseFutures('1')" v-if="if_flag=='0'">继续开户</van-button>
        </div>
        <div v-show="(this.type == '14' || this.type == '24' || this.type == '22')" class="suitBtn">
          <van-button class="buttons" @click="goChooseFutures('1')" v-if="this.adequacy_type =='2'">重新测评</van-button><!--普通-->
          <van-button class="buttons" @click="goChooseFutures('1')" v-if="this.adequacy_type =='0'">开始测评</van-button><!--未分类-->
        </div>
        <popUp :tips="error_content" :showBox="show_content" @notarize="notarize"></popUp>
        <quit :content="content" :quit="quit"></quit>
       <!-- <div v-show="this.adequacy_type !=2 && this.business == '14'" class="suitBtn"> &lt;!&ndash;专业&ndash;&gt;
        </div>-->
    </div>
</template>

<script type="text/ecmascript-6">
  import Quit from "../../components/quit.vue";
  import PopUp from '../../components/popUp.vue';
  import Navigation from "../../components/navigation.vue";
  export default {
	  components: {
      Quit,
      PopUp,
      Navigation
    },
    data(){
      return{
        error_content:"",
        show_content:false,
        quit:false,
        content:"",
        adequacyType:'',
        ade_score:'',
        time:'',
        type:'',
        accept_no:'',
        adequacy_type:'',
        business:'',
        broker_hotline:'',
        if_flag:''
      }
	  },
    created(){
			var _this = this;
      _this.type = sessionStorage.getItem('business_type');
      _this.broker_hotline = sessionStorage.getItem('broker_hotline');
			var data = {
			  "business_type":sessionStorage.getItem('business_type'),
			  "register_way":'2'
			};
      _this.$ajax.post('/flow/qryAcceptInfo',data).then(function (res) {
        if(res.data.error_no == 0){
          _this.accept_no =res.data.accept_no;
          _this.business_flow=res.data.business_flow;
          if(_this.$router.history.current.name != res.data.business_flow){
            _this.$router.push({
              name: res.data.business_flow
            })
          }
          if(_this.type == "11" || _this.type == "12"){  //同步三方适当性
            _this.$ajax.post('/adequacy/synchroThdAdequacy',{"accept_no": _this.accept_no, "business_type": sessionStorage.getItem('business_type')}).then(function (res) {
              if(res.data.error_no == 0){
                _this.ade_score = res.data.risk_score;
                _this.risk_level = res.data.risk_level;
                _this.adequacy_type = res.data.adequacy_type;
                var time = res.data.risk_date;
                _this.if_flag=res.data.if_flag ;
                _this.time = time.substring(0, 4) + '-' + time.substring(4, 6) + '-' + time.substring(6, 8) + ' ' + time.substring(8, 10) + ':' + time.substring(10, 12) + ':' + time.substring(12, 14);

                if (res.data.adequacy_type == '1') {
                  _this.adequacyType = '专业投资者'
                } else if (res.data.adequacy_type == '2') {
                  _this.adequacyType = '普通投资者'
                } else {
                  _this.adequacyType = '未分类'
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
          }
          if(_this.type == "14" || _this.type == "22"){  //查询三方最新适当性信息
            _this.$ajax.post('/adequacy/qryThdNewAdequacy',{}).then(function (res) {
              if(res.data.error_no == 0){
                _this.ade_date =res.data.ade_date ;
                _this.ade_score=res.data.ade_score ;
                _this.adequacy_type=res.data.adequacy_type ;
                if(res.data.adequacy_type == '1'){
                  _this.adequacyType = '专业投资者'
                }else if(res.data.adequacy_type == '2'){
                  _this.adequacyType = '普通投资者'
                }else if(res.data.adequacy_type == '0'){
                  _this.adequacyType = '未分类'
                }
                _this.if_flag=res.data.if_flag ;
                _this.risk_level=res.data.risk_level ;
                _this.special_flag=res.data.special_flag ;
                var time = res.data.ade_date;
                _this.time = time.substring(0,4)+'-'+time.substring(4,6)+'-'+time.substring(6,8)+' '+time.substring(8,10)+':'+time.substring(10,12)+':'+time.substring(12,14);
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
          }
          if (_this.type == '24') {
            _this.adequacy_type = '0';
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

	  },
    computed:{
      isShow(){
        if(this.type == '11' || this.type == '12'){
            return true
        }
      }
    },
    methods:{
      notarize(){
        this.show_content = false;
      },
      onClickLeft(){
        var _this = this;
        if( _this.accept_no != "" && _this.accept_no != undefined ){
          var data = {
            "accept_no": _this.accept_no,
            "business_type":sessionStorage.getItem('business_type'),
            "business_button_type":'0', //上一步 按钮类型
            "business_flow": _this.$router.history.current.name,
            "accept_status":""   //"accept_status":_this.accept_status,当需要更改状态时再传值
          };
          _this.$ajax.post('/flow/updateAccept',data).then(function (res) {
            _this.$router.push({
              name: res.data.business_flow,
            });
          });
        }
        },
      onClickRight(){
        this.$router.push({name:'MyBussiness'})
        },
      changeSuitable(){
      console.log('上一步')
        },
      goChooseFutures(type){
        var _this = this;
        var data = {
          "accept_no":_this.accept_no,
          "business_type":sessionStorage.getItem('business_type'),
          "business_button_type":type,
          "business_flow":_this.$router.history.current.name,
          "accept_status":""     //"accept_status":_this.accept_status,当需要更改状态时再传值
        };
        if(_this.accept_no=='' || _this.accept_no == undefined) {
          this.$ajax.post('/flow/getAcceptNo', {   //每个步骤的第一步(生成受理单)
            "business_type": sessionStorage.getItem('business_type'),
            "business_button_type": '1',//下一步
            "systype":sessionStorage.getItem('systype')
          }).then(function (res) {
            if (res.data.error_no == 0) {
              _this.accept_no = res.data.accept_no;
              _this.$router.push({
                name: res.data.business_flow,
              });
            } else if (res.data.error_no == '-9999') {
                _this.quit = true;
                _this.content = res.data.error_info;
            } else {
              _this.error_content = res.data.error_info;
              _this.show_content = true;
            }
          });
        }else{
          var _this = this;
          this.$ajax.post('/flow/updateAccept',data).then(function (res) {
            _this.$router.push({
              name: res.data.business_flow,
            });
          });
        }
        },
      /*changeSuitable(){
      var data = {
        "accept_no":this.accept_no,
        "business_type":sessionStorage.getItem('business_type'),
        "business_button_type":'0',//上一步
        "business_flow":this.$router.history.current.name,
        "accept_status":""     //"accept_status":_this.accept_status,当需要更改状态时再传值
      };
      var _this = this;
      this.$ajax.post('/flow/updateAccept',data).then(function (res) {
        if(res.data.error_no == 0){
        _this.$router.push({
          name: res.data.business_flow,
        });
        }else if(res.data.error_no == '-9999' ){
        _this.$msgbox({
          message:'登录超时请重新登录!',
          showCancelButton: false,
          showConfirmButton: true,
          cancelButtonText: '确定',
          beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            done();
            setTimeout(function () {
            _this.$router.push('/');
            },1000)
          }
          }
        });
        }else{
        _this.$message.error(res.data.error_info)
        }
      });
      },*/
      showModalMethod(){
      this.showModal = true;
      },
      /*goChooseFutures(){
      var _this = this;
      var data = {
        "accept_no":_this.accept_no,
        "business_type":sessionStorage.getItem('business_type'),
        "business_button_type":'1',//下一步
        "business_flow":this.$router.history.current.name,
        "accept_status":""     //"accept_status":_this.accept_status,当需要更改状态时再传值
      };
      _this.$ajax.post('/flow/updateAccept',data).then(function (res) {
        if(res.data.error_no == 0){
        _this.$router.push({
          name: res.data.business_flow,
        });
        }else if(res.data.error_no == '-9999' ){
        _this.$msgbox({
          message:'登录超时请重新登录!',
          showCancelButton: false,
          showConfirmButton: true,
          cancelButtonText: '确定',
          beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            done();
            setTimeout(function () {
            _this.$router.push('/');
            },1000)
          }
          }
        });
        }else{
        _this.$message.error(res.data.error_info)
        }
      })
      var nextData = {
        "adequacy_type":this.adequacy_type,//1或者2
        "ade_date":this.ade_date,
        "ade_score":this.ade_score,
        "risk_level":this.risk_level,
        "business_flow":this.$router.history.current.name,

      }
      _this.$ajax.post('/flow/updateAccept',nextData).then(function (res) {
        if(res.data.error_no == 0){
        _this.$router.push({
          name: res.data.business_flow,
        });
        }else if(res.data.error_no == '-9999' ){
        _this.$msgbox({
          message:'登录超时请重新登录!',
          showCancelButton: false,
          showConfirmButton: true,
          cancelButtonText: '确定',
          beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            done();
            setTimeout(function () {
            _this.$router.push('/');
            },1000)
          }
          }
        });
        }else{
        _this.$message.error(res.data.error_info)
        }
      })

      },*/
      /*	  goTest(){
      var data = {
        "accept_no":this.accept_no,
        "business_type":sessionStorage.getItem('business_type'),
        "business_button_type":'2',//其他
        "business_flow":this.$router.history.current.name,
        "accept_status":""     //"accept_status":_this.accept_status,当需要更改状态时再传值
      };
      var _this = this;
        this.$ajax.post('/flow/updateAccept',data).then(function (res) {
        _this.$router.push({
          name: res.data.business_flow,
        });
        });
      },*/
    }
  }
</script>

<style lang="less" scoped>
.doneSuitably{
	overflow: hidden;
    .done{
        font-size: 14px;
        margin-top: 1rem;
        width: 98%;
        margin-left: 2%;
        text-align: left;
        color: #666;
        margin-bottom: 3rem;
    }
    .suitBtn{
        position: fixed;
        bottom: 3%;
        left:0;
        width: 100%;
    }
		.warpDiv{
			width: 84%;
			margin: 0 auto;
			padding-top: 18%;
		}
		.buttons{
			background:#49a5ff;
			color: white;
			width:80%;
			margin-top: 15px;
		}
}


</style>
