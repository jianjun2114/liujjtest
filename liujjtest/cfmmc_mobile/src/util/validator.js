exports.install = function (Vue) {
  Vue.prototype.v_custname = function (name) {
    if (name != undefined && name.trim() != "") {
      var reg = /^([\u4e00-\u9fa5+\·?\u4e00-\u9fa5+]{2,20}$)/;
      if (reg.test(name) == false) {
        return "姓名仅允许汉字、中间点，长度2-20";
      }
      return "truetrue";
    }
    return "姓名不能为空！";
  }
  Vue.prototype.v_idno = function (name) {
    if (name != undefined && name.trim() != "") {
      var reg = /^[1-9]\d{5}(19|20)?\d{2}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}(\d|X)$/;
      if (reg.test(name) == false) {
        return "身份证号输入错误";
      }
      return "truetrue";
    }
    return "身份证号不能为空！";
  }
  Vue.prototype.v_idAddress = function (name) {
    if (name != undefined && name.trim() != "") {
      /* var reg = /^([A-Za-z0-9-#()（）\u4e00-\u9fa5]{3,100}$)/;
       var regs = /^[\d\+\-\/\(\)]/;
       var rega = /^[0-9,]/;*/
      var reg = /^[A-Za-z0-9-－#＃()（）、、\u4e00-\u9fa5]{3,100}$/
      if (reg.test(name.trim()) == false) {
        return "身份证地址特殊字符支持 - # ()（）、且长度3-100";
      }
      return "truetrue";
    }
    return "身份证地址不能为空！";
  }
  Vue.prototype.v_idBeginDate = function (name) {
    if (name != undefined && name.trim() != "") {
      return "truetrue";
    }
    return "请选择身份证开始日期！";
  }
  Vue.prototype.v_idEndDate = function (name) {
    if (name != undefined && name.trim() != "") {
      return "truetrue";
    }
    return "请选择身份证结束日期！";
  }
  Vue.prototype.v_CompDate = function (bDate, eDate) {
    if (bDate == undefined || bDate.trim() == "") {
      return "请选择身份证开始日期！";
    }
    if (eDate == undefined || eDate.trim() == "") {
      return "请选择身份证结束日期！";
    }
    if (bDate > eDate) {
      return "开始日期不能大于结束日期！";
    }
    if (Date.parse(new Date(bDate)) > new Date().getTime()) {
      return "开始日期不能大于今天！";
    }
    if (Date.parse(new Date(eDate)) - new Date().getTime() <= -100000000) {
      return "身份证已过期！";
    }
    return "truetrue";
  }
  Vue.prototype.v_custAddress = function (province, city, detail) {
    if (province == false) {
      return "请选择联系地址省份！";
    }
    if (city == false) {
      return "请选择联系地址城市！";
    }
    if (detail == undefined || detail.trim() == "") {
      return "请填写详细联系地址！";
    }
    var reg = /^([A-Za-z0-9-#()（）、\u4e00-\u9fa5]{3,50}$)/;
    if (reg.test(detail.trim()) == false) {
      return "详细联系地址特殊字符支持 - # ()（）、且长度3-50";
    }
    return "truetrue";
  }
  Vue.prototype.v_zipCode = function (name) {
    if (name != undefined && name.trim() != "") {
      if (/^[0-9]{6}$/.test(name) == false) {
        return "请输入正确的邮政编码！";
      }
      return "truetrue";
    }
    return "邮政编码不能为空！";
  }
  Vue.prototype.v_telPhone = function (name, preg, param) {
    var errorinfo = "请输入正确格式的联系手机！";
    if (preg == undefined || preg.trim() == "") {
      preg = /^1[345789]\d{9}$/;
    } else {
      preg = eval('/^(' + this.reg_phone + ')+\\d{8}$/');
    }
    if (name != undefined && name.trim() != "") {
      if (param == 'telPhone') {
        errorinfo = "请输入正确格式的联系电话！";
        if (/^0\d{2,3}-?\d{7,8}$/.test(name) == false && preg.test(name) == false) {
          return errorinfo;
        }
      } else {
        if (preg.test(name) == false) {
          return errorinfo;
        }
      }
      return "truetrue";
    }
    if (param == 'telPhone')
      return "联系电话不能为空！";
    else
      return "truetrue";
  }
  Vue.prototype.v_email = function (name) {
    if (name != undefined && name.trim() != "") {
      // if (/^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/.test(name) == false) {
      //if (/^[\w.-]+@[\w]+[\.\w]+$/.test(name) == false) {//20200401 电子邮箱名支持点
      if (/^[\w-.]+@[\w-.]+.[\w]+$/.test(name)) {
        return "truetrue";
      }else{
        return "请输入正确的电子邮箱！";
      }
    }
    return "truetrue";
  }
  Vue.prototype.v_profession = function (name) {
    if (name == false) {
      return "职业不能为空！";
    }
    return "truetrue";
  }
  Vue.prototype.v_educational = function (name) {
    if (name == false) {
      return "学历不能为空！";
    }
    return "truetrue";
  }
  Vue.prototype.isBlank = function (value) {
    if (value != undefined && value.trim() != "") {
      return true;
    }
    return false;
  }
  Vue.prototype.isObjBlank = function (value) {
    if (value != undefined && value.trim() != "") {
      return true;
    }
    return false;
  }
  Vue.prototype.v_buss3 = function (name) {
    if (name == undefined || name.length == '0') {
      return "营业部类别不能为空！";
    }
    return "truetrue";
  }
  Vue.prototype.v_buss4 = function (name) {
    if (name == undefined || name.length == '0') {
      return "开户营业部不能为空！";
    }
    return "truetrue";
  }
  Vue.prototype.v_custAddressIsBlank = function (province, city, detail, flag) {
    if (province == false) {
      return "联系地址省份不能为空！";
    }
    if (city == false) {
      return "联系地址城市不能为空！";
    }
    if (detail == undefined || detail.trim() == "") {
      return "详细联系地址不能为空！";
    }
    /*var reg = /^([A-Za-z0-9-#()（）\u4e00-\u9fa5]{3,50}$)/;
    var regs = /^[\d\+\-\/\(\)]/;
    var rega = /^[0-9,]/;*/
    if (!flag) {
      var reg = /^([A-Za-z0-9-#()（）、\u4e00-\u9fa5]{3,50}$)/;
      if (reg.test(detail.trim()) == false) {
        return "详细联系地址特殊字符支持 - # ()（）、且长度3-50";
      }
    }
    return "truetrue";
  }
  Vue.prototype.v_val = function (name, value) {
    if (value.trim() != "") {
      if (value.indexOf("$") > -1) {
        return name + "不允许输入$!"
      }
    }
    return "truetrue";
  }
  Vue.prototype.v_mobile = function (name, preg, param) {
    var errorinfo = "请输入正确格式的联系手机！";
    if (preg == undefined || preg.trim() == "") {
      preg = /^1[345789]\d{9}$/;
    } else {
      preg = eval('/^(' + this.reg_phone + ')+\\d{8}$/');
    }
    if (name != undefined && name.trim() != "") {
      if (param == 'telPhone') {
        errorinfo = "请输入正确格式的联系电话！";
        if (/^0\d{2,3}-?\d{7,8}$/.test(name) == false) {
          return errorinfo;
        }
      } else {
        if (preg.test(name) == false) {
          return errorinfo;
        }
      }

      return "truetrue";
    }
  }
  Vue.prototype.idAddressFormat = function (idAddress) {
    idAddress = idAddress.replace(/\(/g, '（');
    idAddress = idAddress.replace(/\)/g, '）');
    idAddress = idAddress.replace(/#/g, '＃');
    idAddress = idAddress.replace(/-/g, '－');
    idAddress = idAddress.replace(/、/g, '、');
    return idAddress
  }
}
