<template>
   <div class="confirm">
       <van-popup v-model="present_isShow" :close-on-click-overlay="false">
					<div class="hint">提示</div>
					<div class="confirm-div">
						<p>{{present_showContents}}</p>
					</div>
					<div class="boxes_button">
						<div v-bind:class="{cancel:true,onlyOne:yesButton == undefined}" v-if="noButton!= undefined" @click="cancel">{{noButton}}</div>
						<div v-bind:class="{cancel:true, abolish:true,onlyOne:noButton == undefined}"  v-if="yesButton != undefined" @click="ensure">{{yesButton}}</div>
					</div>
				</van-popup>
    </div>
</template>
<script>
export default {
    components:{

    },
    data() {
        return{
            present_isShow:false,
            present_showContents:""
        }
    },
    props: {
      // 定义 调用组件输入参数
      showContents: String,
      isShow: Boolean,
      yesButton: String,
      noButton: String
    },
    watch:{
      showContents(val){
         this.present_showContents = val;
        },
      isShow(val){
         this.present_isShow = val;
        }
    },
    methods: {
        cancel(){
          this.$emit('cancel')
        },
         ensure(){
            this.$emit('ensure')
        }
    }
}
</script>
<style lang="less" scoped>
.confirm{
    .van-popup{
        width: 80%;
    }
     .cancel {
        width: 78px;
        color: #ffffff;
        background: #49a5ff;
        text-align: center;
        line-height: 35px;
        border-radius: 4px;
        margin: 4% 0;
        margin-bottom: 12px;
        float: left;
    }
    .hint {
        text-align: center;
        font-size: 16px;
        color: #333333;
        width: 100%;
        background: #49a5ff;
        line-height: 37px;
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
        width: 64%;
        margin: 0 auto;
    }
    .confirm-div{
        width:93%;
        margin: 0 auto;
        padding-bottom: 3%;
            p{
                text-align: left;
                font-size: 16px;
                color:#333333;
                line-height: 24px;
                padding-top: 5%;
            }
    }
  .onlyOne{
    float: inherit;
    margin-left: 24%;
  }
  @media only screen and (min-device-width : 768px) and (max-device-width : 1024px){
      .van-popup{
          width: 45%;
      }
  }
}
</style>
