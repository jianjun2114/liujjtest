<template>
   <div class="showDiaLog">
        <van-popup v-model="ShowDisplay" :close-on-click-overlay="false">
            <div class="hint">{{titleName}}</div>
            <div class="workPBox">
                <p class="workP">{{workCenter}}</p>
            </div>
        <div class="buttonDiv">
            <div class="ok" v-model="buttonOnehide" @click="buttonOneClick">{{buttonOne}}</div>
            <div class="ok" v-model="buttonTwohide" @click="buttonTwoClick">{{buttonTwo}}</div>
            <div class="ok" v-model="buttonThreehide" @click="buttonThreeClick">{{buttonThree}}</div>
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
        }
    },
    props: {
        // 定义 调用组件输入参数
        workCenter: String,
        titleName: String,
        buttonOne: String,
        buttonTwo: String,
        buttonThree: String,
        ShowDisplay: Boolean,
        buttonOnehide: Boolean,
        buttonTwohide: Boolean,
        buttonThreehide: Boolean
    },
    methods: {
         buttonOneClick(){
            this.$emit('buttonOneClick')
        },
        buttonTwoClick(){
            this.$emit('buttonTwoClick')
        },
        buttonThreeClick(){
            this.$emit('buttonThreeClick')
        },
    },
}
</script>
<style lang="less" scoped>
.showDiaLog{
    .van-popup{
        width: 80%;
        height: auto;
    }
    .ok{
        width: 28%;
        color:#ffffff;
        background: #49a5ff;
        text-align: center;
        line-height: 32px;
        border-radius: 4px;
        margin: 0 auto;
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
    .workPBox{
        width: 86%;
        height: auto;
        margin: 5% auto;
        overflow: auto;
            p{
                text-align: left;
                font-size: 16px;
            }
    }
    .buttonDiv{
        width: 83%;
        margin: 0px 9%;
            div{
                // float: left;
                // margin-left: 5%;
                margin-bottom: 8%;
            }
    }
}
</style>