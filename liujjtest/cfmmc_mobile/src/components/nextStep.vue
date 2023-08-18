<template >
    <div class="next_step">
        <router-link :to = "{name:''}">
            <div size="large" class="van-button" @nextstep="nextstep()">下一步</div>
        </router-link>
    </div>
</template>
<script>

export default {
    data() {
        return{

        }
    },
    methods: {

    },
    components:{
        nextstep(){
             //           var username = this.username;
  //          var idNumber = this.idNumber;
  //          if(this.username==""){  //为空
  //              Toast('请输入姓名');
  //         }
  //         else{  //不为空
  //            if (!/^([\u4e00-\u9fa5+\·?\u4e00-\u9fa5+]{2,20}$)/.test(username)==false) {
  //              Toast('身份证姓名不符合规范');
  //            }else{
  //           if(this.idNumber==""){
  //              Toast('请填写身份证号码');
  //           }else{
  //               idNumber = idNumber.replace(/x/g, "X");
  //               if (!/^\d{17}(\d|X)$/i.test(idNumber)){
  //                 console.log('cidCity',/^\d{17}(\d|X)$/i.test(idNumber));
  //                 Toast('身份证号码填写错误');
  //               }else if(cidCity[parseInt(idNumber.substr(0,2))] == null){
  //                 console.log('cidCity',cidCity[parseInt(idNumber.substr(0,2))]);
  //                 Toast('身份证号码填写错误');
  //               }
  //           var today = new Date();
  //           var delt = parseInt(today.getFullYear()) - parseInt(idNumber.substr(6,4));
  //           if (delt > 150){
  //             Toast('身份证号码格式不正确');
  //           }
  //           if (delt <= 0){
  //             Toast('身份证号码格式不正确');
  //           }
  //           var sBirthday = idNumber.substr(6,4) + "-" + Number(idNumber.substr(10,2)) + "-" + Number(idNumber.substr(12,2));
  //           var d = new Date(sBirthday.replace(/-/g, "/"));
  //           if (sBirthday != (d.getFullYear() + "-" + (d.getMonth()+1) + "-" + d.getDate()))
  //             Toast( "身份证号码填写错误");  //生日有误
  //           if (this.devCalcAge(idNumber.substring(6, 14)) < 18)
  //             Toast("未满18周岁不能开户");
  //           var ai = idNumber.substr(0, 17);
  //           var totalMulAiWi = 0;
  //           for (var i = 0; i < 17; i++) {
  //             totalMulAiWi = totalMulAiWi + parseInt(ai.charAt(i)) * parseInt(wi[i]);
  //           }
  //         var modValue = totalMulAiWi % 11;
  //         var strVerifyCode = valCodeArr[modValue];
  //         ai = ai + strVerifyCode;
  //         if (ai != idNumber) {
  //           Toast("身份证号码填写错误"); //校验位有误
  //         }
  //       //身份证地址
  //         if (this.idAddress =='') {
  //             Toast('请输入身份证地址');
  //             return false
  //         }
  //       //身份证开始日期
  //         if (this.startTime =='') {
  //             Toast('请选择身份证开始日期');
  //             return false
  //         }
  //       //身份证结束日期
  //          if (this.endingTime =='') {
  //             Toast('请选择身份证结束日期');
  //             return false
  //         }
  //         //联系地址省市
  //             if(this.addressValue == '') {
  //               Toast("请填写联系地址");// 联系地址
  //             } else if(this.detailedAddress =='') {  //详细地址
  //                  Toast('请填写详细地址地址');
  //             if (this.phone ==''){
  //               Toast('请输入手机号码');
  //             } else{
  //             var regMobile = /^(13|14|15|18|17|19)\d{9}$/;
  //             var phone = this.phone;
  //             if (!regMobile.test(phone)) {
  //               Toast('手机号码格式不正确');
  //             } else{
  //               var email = this.email;   //校验邮箱email
  //              var regEmail = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
  //             if(this.email == ""){
  //                  Toast('请填写电子邮箱');
  //             }else if(!(null == email || email == '' || regEmail.test(email))){
  //                  Toast('电子邮箱填写错误');
  //             }else if(email.length>64){
  //                  Toast('邮箱填写超出最大长度');
        //           }else{
  //             if(this.zipCode ==""){   //校验学历
        //              Toast('请填写邮编号');
  //             }else{
  //               // this.$router.push('/futuresBank');
  //               if (this.option1 =='') {
  //                 Toast('请选择职业');
  //             }else{
  //                 if (this.option2 =='') {
  //                   Toast('请选择学历');
  //              }
  //             }
  //             }
  //           }
  //         }
  //       }
  //     }
  //   }
  //  }
  // }

        },
    },
}
</script>
<style lang="less" scoped>
.next_step {
   .van-button{
        width: 84%;
        height: 45px;
        background-color: #49A5FF;
        margin: 0 auto;
        font-size: 15px;
        color: #ffffff;
        text-align: center;
        line-height: 40px;
        margin-bottom: 6%;
   }
}
</style>
