var utils = {
  iphOrAnz:function(){
    //判断浏览器
    var browser = {
      versions: function() {
        var u = window.navigator.userAgent,
          app = window.navigator.appVersion;
        return { //移动终端浏览器版本信息
          trident: u.indexOf('Trident') > -1, //IE内核
          presto: u.indexOf('Presto') > -1, //opera内核
          webKit: u.indexOf('AppleWebKit') > -1, //苹果、谷歌内核
          gecko: u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1, //火狐内核
          mobile: !!u.match(/AppleWebKit.*Mobile.*/), //是否为移动终端
          ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), //ios终端
          newios: !!u.match(/\(M[^;]+;( U;)? Intel.+Mac OS X/), //ios终端
          android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, //android终端或uc浏览器
          iPhone: u.indexOf('iPhone') > -1, //是否为iPhone或者QQHD浏览器
          iPad: u.indexOf('iPad') > -1, //是否iPad
          webApp: u.indexOf('Safari') == -1, //是否web应该程序，没有头部与底部
          YiXin: u.indexOf('YiXin') > -1, //是否YiXin手机开户
          WeiXin: u.indexOf('MicroMessenger') > -1, //是否微信手机开户
          Chrome: u.indexOf('Chrome') > -1, //Chrome浏览器
          Windows: u.indexOf('Windows') > -1, //Chrome浏览器
        };
      }(),
      language: (navigator.browserLanguage || navigator.language).toLowerCase()
    };
    if (browser.versions.ios || browser.versions.newios) { //iphone
      return "iphone"
    } else if (browser.versions.android) {
      return "anzuo"
    } else if (browser.versions.Windows) {
      return "windows"
    }else{
      alert("识别失败");
    }
  }
}
export default utils


