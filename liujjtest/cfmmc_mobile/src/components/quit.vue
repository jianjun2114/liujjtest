<template>
   <div class="quit">
        <van-popup v-model="present_quit" :close-on-click-overlay="false">
            <div class="hint">提示</div>
            <p style="text-align: left; font-size: 16px; color:#333333; padding-top:5%; width: 82%; margin: 0 auto;">{{present_content}}</p>
            <div class="ok" @click="defeated">确认</div>
        </van-popup>
    </div>
</template>
<script>
import Utils from '../util/iphoneOrAndroid';
export default {
    components:{
        
    },
    data() {
        return{
            present_quit:false,
            present_content:"网络错误，请重新尝试",
            resgisterWay:""
        }
    },
    props: {
        // 定义 调用组件输入参数
          content: String,
          quit: Boolean
    },
    mounted(){
        
    },
    watch:{
        content(val){
         this.present_content = val;
        },
        quit(val){
         this.present_quit = val;
        }
    },
    methods: {
         defeated(){
            var resgisterWay = sessionStorage.getItem('resgisterWay');
            if(resgisterWay == "5" || resgisterWay == "6"){
                var browserVersion = Utils.iphOrAnz();
                if (browserVersion == "iphone") {
                    window.location.href = "objc://callIOSOut_5";
                } else if (browserVersion == "anzuo") {
                    jsHandle.logoutRelease();
                } else if (browserVersion == "windows") {
                    console.log("windows");
                }
            }else{
                this.present_quit = false;
                this.$router.push({name: "Indexselection"});
            }
            
        },
    },
}
</script>
<style lang="less" scoped>
.quit{
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
    .hint{
        text-align: center; 
        font-size: 16px; 
        color:#333333; 
        width:100%; 
        background:#49a5ff; 
        line-height: 37px; 
        color:#ffffff;
    }
}
</style>