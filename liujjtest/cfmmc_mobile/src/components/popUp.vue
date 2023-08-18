<template>
   <div class="pop_up">
        <van-popup v-model="showBox" :close-on-click-overlay="false">
            <div class="hint">提示</div>
            <p style="text-align: left; font-size: 16px; color:#333333; padding-top:5%; line-height: 26px; width:86%; margin: 0 auto;" v-if="tips">{{tips}}</p>
            <p style="text-align: left; font-size: 16px; color:#333333; padding-top:5%; line-height: 26px; width:86%; margin: 0 auto;" v-else>{{text}}</p>

            <div class="ok" @click="notarize">确认</div>
        </van-popup>
    </div>
</template>
<script>
export default {
    components:{
        
    },
    data() {
        return{
           text:"网络错误，请重新尝试"
        }
    },
    props: {
        // 定义 调用组件输入参数
        tips: String,
        showBox: Boolean
    },
    methods: {
         notarize(){
            this.$emit('notarize')
        },
    },
}
</script>
<style lang="less" scoped>
.pop_up{
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
        margin: 0px auto;
        margin-bottom: 5%;
    }
    .hint{
        text-align: center; 
        font-size: 16px; 
        width:100%; 
        background:#49a5ff; 
        line-height: 37px; 
        color:#ffffff;
    }
     //ipad适配
    @media only screen and (min-device-width : 768px) and (max-device-width : 1024px){
        .van-popup{
            width: 55%;
        }
    }
}
</style>