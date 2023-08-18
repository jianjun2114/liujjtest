<template>
  <div class="Product">
    <van-nav-bar
        style="border-bottom: 1px solid #eeeeee"
        title="产品名称" 
        right-text="关闭" 
        right-arrow 
        left-arrow 
        @click-left="onClickLeft"
        @click-right="onClickRight"
    />
    <div style="padding-top:12%"></div>
    <van-cell-group>
        <van-field v-model="prod_name" placeholder="请输入产品名称" maxlength="50" class="prod_name" onkeyup="this.value=this.value.replace(/^(\-)*(\d+)\.(\d\d).*$/,'$1$2.$3')"/>
    </van-cell-group>
    <van-button size="large" class="van-button" @click="ProductnameNext()">下一步</van-button>
    <popUp :tips="error_content" :showBox="show_content" @notarize="notarize"></popUp>
    <waitPop :loadingbox="loadingbox"></waitPop>
    <quit :content="content" :quit="quit"></quit>
  </div>
</template>
<script>
import { } from 'vant';
import Utils from '../../util/iphoneOrAndroid';
import Quit from "../../components/quit.vue";
import PopUp from "../../components/popUp.vue";
import WaitPop from "../../components/waitPop.vue";
  export default {
    components: {Quit, PopUp, WaitPop},
    data(){
        return{
            show_content: false,
            error_content: "",
            loadingbox: false,
            content: "",
            quit: false,
            prod_name: "",
            acceptNo: ""
        }
    },
    created(){
        var _this = this;
        // 开始
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
        //产品名称 查询
        _this.$ajax.post('/userInfo/qryProduct',{}).then(function (res) {
            if(res.data.error_no == 0){
                _this.prod_name = res.data.prod_name;
            }else if(res.data.error_no == '-9999'){
                _this.quit = true;
                _this.content = res.data.error_info;
            }else {
                _this.show_content = true;
                _this.error_content = res.data.error_info;
            }
        }).catch(function (err) {
            console.log(err)
        });
    },
    methods:{
        onClickLeft(){
            var _this = this;
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
                    _this.$router.push({name: res.data.business_flow,});
                }else if(res.data.error_no == "-9999"){
                    _this.quit = true;
                    _this.content = res.data.error_info;
                    _this.loadingbox = false;
                }else{
                    _this.loadingbox = false;
                    _this.show_content = true;
                    _this.error_content = res.data.error_info;
                }
            }).catch(function (err) {
                console.log(err)
            });
        },
        onClickRight() {
            this.$router.push({name:'MyBussiness',})
        },
      notarize(){
        this.show_content = false;
      },
      ProductnameNext(){  
        var _this = this;
         if(!_this.prod_name){
            _this.show_content = true;
            _this.error_content = "请输入产品名称！";
            return false;
        }
        var reg = /^([A-Za-z0-9-\u4e00-\u9fa5]{0,50}$)/;
        if(reg.test(_this.prod_name.trim()) == false){
            _this.show_content = true;
            _this.error_content = "请输入正确格式的产品名称！";
            return false;
        }
        //产品名称下一步保存
        _this.$ajax.post('/userInfo/updateProduct',{'prod_name': _this.prod_name}).then(function (res) {
            if(res.data.error_no == 0){
                var data = {
                    "accept_no": _this.acceptNo,
                    "business_type": sessionStorage.getItem('business_type'),
                    "business_button_type":'1',  //下一步
                    "business_flow":_this.$router.history.current.name,
                    "accept_status":""     //"accept_status":_this.accept_status,当需要更改状态时再传值
                };
                _this.loadingbox = true;
                _this.$ajax.post('/flow/updateAccept',data).then(function (res) { //下一步
                    _this.loadingbox = false;
                    if(res.data.error_no == 0){
                        _this.$router.push({name: res.data.business_flow});
                    }else if(res.data.error_no == "-9999"){
                        _this.loadingbox = false;
                        _this.quit = true;
                        _this.content = res.data.error_info;
                    }else{
                        _this.loadingbox = false;
                        _this.show_content = true;
                        _this.error_content = res.data.error_info;
                    }
                }).catch(function (err) {
                    console.log(err)
                });
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
      },
    }
  }
</script>
<style lang="less">
 .Product {
   .van-button {
      width: 84%;
      margin: 0 auto;
      background: #49a5ff;
      text-align: center;
      line-height: 20px;
      color: white;
      position: absolute;
      bottom: 5%;
      left: 8%;
    }
    [class*=van-hairline]::after{
        border: none;
    }
    .van-cell-group{
        background-color: #fff;
        width: 86%;
        margin: 5% auto;
    }
}
</style>
