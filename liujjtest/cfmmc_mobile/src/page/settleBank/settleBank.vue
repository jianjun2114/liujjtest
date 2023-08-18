<template>
  <div class="settleBank">
    <van-nav-bar
      title="银行卡信息"
      right-text="关闭"
      right-arrow
      left-arrow
      @click-left="onClickLeft"
      @click-right="onClickRight"/>
    <!--导航条-->
    <navigation/>
    <div class="bankImg" v-if="isShowUpload">
      <!--点击上传-->
      <div @click="bankUpload" class="bank_img" style="margin: 0 auto; width: 100%;">
        <img class="photo" src="../../assets/bankbj.png" alt=""/>
      </div>
    </div>
    <!-- div循环 -->
    <div class="warpCirculation" :v-model="warpdiv" v-for="(item,index) in imgs" :key="item.index"
         @click="goChoose(item,index)">
      <div @click="deleteItem(item,index)" v-if="item.bank_trx_no == '1' || item.bank_trx_no == '0' || item.bank_trx_no == '2'">
        <x-icon type="ios-close-outline" size="30" class='deleteIcon'></x-icon>
      </div>
      <div v-if="item.bank_trx_no == '2' || item.bank_trx_no == '4'" @click="noBankImgUpload(item,index)" class="bank_img"
           style="margin: 15px auto;  width:73%;">
        <img :src='item.imgUrl' class="bank_img" alt="" style="box-shadow: 0 0 15px #00a4e3">
      </div>
      <div v-else class="bank_img" style="margin: 0 auto; width: 73%;">
        <img :src='item.imgUrl' class="bank_img" alt="">
      </div>
      <div style="padding-top:5%">
        <p class="settlementBank">结算银行</p>
      </div>
      <div @click="jump(item,index)" style="margin-top:-2%">
        <group v-model="bank">
          <!--  <cell title="请选择银行" link="/bankList" @click="jump" :value="item.bankName">{{bankIt}}</cell>-->
          <cell title="请选择银行" :value="item.bank_id">{{item.bank_name}}</cell>
        </group>
      </div>
      <div class="bankcardMessage-box"><p class="bankcardMessage">银行卡信息</p></div>
      <van-cell-group>
        <van-field :id="forId(item.bank_sign)" maxlength="25" @blur="field(item.bank_no)" v-model='item.bank_no'
                   label="卡号" type="textarea" placeholder="请输入卡号" rows="1" autosize style="text-align: left;"
                   :disabled="item.bank_trx_no != 0"/>
      </van-cell-group>
      <van-cell-group>
        <van-field :id="forBankBranchId(item.bank_sign)" maxlength="50" v-model='item.bank_branch' label="银行网点"
                   type="textarea" placeholder="请输入银行网点名称" rows="1" autosize style="text-align: left;"
                   :disabled="item.bank_trx_no != 0"/>
      </van-cell-group>
    </div>
    <div style="display: block;color: #F6F6F6;">{{test}}</div>
    <van-popup
      v-model="show2"
      position="bottom"
    >
      <van-nav-bar
        title="选择银行"
        left-text=""
        left-arrow
        @click-left="show2 = false;" style="position: relative;">
      </van-nav-bar>
      <div class="bankListDiv" id="bankList">
        <ul v-for="(bank,index) in bankLists" :key="index">
          <li class="bankList_li" :id='bank.bankNo' @click="getBank($event, bank)">
            <img :src="'static/bankimg/'+bank.bankLogo+'.png'" :name='bank.bankName'/>
            <van-icon v-show=true name="success" class="bankSuccess" v-if="bank.bankNo == bankId"/>
          </li>
        </ul>
      </div>
    </van-popup>
    <van-popup v-model="show" class="vanPopup">
      <div>
        <div class="specification">—&nbsp;拍摄规范&nbsp;—</div>
        <ul v-for="(img,index) in bank_imgs" :key="index">
          <li class="li-img">
            <img class="img1" :src="'static/shootSpecification/'+img.status+'.svg'" :name='img.status'/>
            <img class="img2" :src="'static/shootSpecification/'+img.name+'.png'" :name='img.name'/>
            <span v-html="img.description" class="word"></span>
          </li>
        </ul>
        <button class="IKonw" @click="iKnow">我知道了</button>
      </div>
    </van-popup>
    <van-button size="large" class="nextBtn" @click="Confirm()">下一步</van-button>
    <popUp :tips="error_content" :showBox="show_content" @notarize="notarize"></popUp>
    <waitPop :loadingbox="loadingbox"></waitPop>
    <quit :content="content" :quit="quit"></quit>
    <confirm :showContents="showContents" :isShow="isShow" :yesButton="yesButton" @ensure="ensure"></confirm>
  </div>
</template>

<script>
    import {PopupPicker, Group, Cell} from 'vux';
    import {Vant, Loading, Popup} from 'vant';
    import PopUp from '../../components/popUp.vue';
    import Utils from '../../util/iphoneOrAndroid';
    import WaitPop from "../../components/waitPop.vue";
    import Quit from "../../components/quit.vue";
    import Navigation from "../../components/navigation.vue";
    import Confirm from "../../components/confirm.vue";

    export default {
        components: {
            PopupPicker,
            Group,
            Cell,
            PopUp,
            Popup,
            WaitPop,
            Loading,
            Quit,
            Navigation,
            Confirm
        },
        data() {
            return {
                disabled1: true,
                business_type: "",
                type: "",
                bankItem: {},
                bankId: "",
                show2: false,
                quit: false,
                content: "",
                error_content: "",
                show_content: false,
                acceptNo: "",
                bankMap: new Map(),
                imgs: [],
                id: "",
                bank: "",
                name: '1',
                test: 'test1',
                bankImage: {img: require("../../assets/card-front.png")},
                noCard: {img: require("../../assets/nocard.png")},
                list1: [[{name: '请选择', value: ''}, {name: '1邮政', value: '111111'}]],
                value: [''],
                message: "",
                Img: "",
                src: "",
                warpdiv: "",  //循环
                bankListSupport: [],
                showUpload: true,
                bankLists: [],
                loadingbox: false,
                onlySign: "",
                branchSign: "",
                isShow: false,
                showContents: "",
                yesButton: "确定",
                show: false,
                bank_imgs: [
                    {name: "bank1", description: "边角完整</br>字迹清晰", status: "done"},
                    {name: "bank2", description: "缺边角", status: "failed"},
                    {name: "bank3", description: "模糊或闪光灯", status: "failed"}
                ],
                testIndex: '9988',
                textBank: ""
            }
        },
        created() {
            // 定义 this
            var _this = this;

            window["settleBankImage"] = function (picNo, picStr) {
                _this.settleBankImage(picNo, picStr);
            };
            _this.flowInit();
            //_this.getInitPic();
        },
        mounted() {
            this.bankName = this.$route.query.bankName;
        },
        activated() {
            //this.getData()
        },
        computed: {
            isShowUpload() {
                if (this.imgs.length >= 10) {
                    return this.showUpload = false;
                } else {
                    return this.showUpload = true;
                }
            },
        },
        /*	beforeRouteLeave(to, from, next) {
        //	  if (to.path == "/settleBank") {
        //		to.meta.keepAlive = true;
        //	  } else {
        //		to.meta.keepAlive = false;
        //	  }
        to.meta.keepAlive = true;
        next();
        },*/
        beforeRouteEnter(to, from, next) {
            if (from.path == "/bankList") {
                next(vm => {
                    localStorage.setItem('bankIndex', vm.$route.query.bankNo);
                    vm.getData();
                })
            } else {
                next()
            }
        },
        methods: {
            getBank(event, bankObj) {
                var _this = this;
                var bankNo = bankObj.bankNo;
                var bankName = bankObj.bankName;
                var bankItemNew = {};
                bankItemNew = _this.bankItem;
                bankItemNew.bank_id = bankNo;
                bankItemNew.bank_name = bankName;
                _this.imgs.splice(_this.id, 1, bankItemNew);
                _this.show2 = false;
            },
            getBankLists() {
                var _this = this;
                let initData = {
                    "time": new Date().getTime(),
                    "systype": sessionStorage.getItem('systype')
                };
                _this.$ajax.post('/thirdDeposity/showBankInfo', initData).then(function (res) {
                    if (res.data.error_no == 0) {
                        var dataBanks = res.data.bank_list;
                        _this.bankLists = dataBanks;
                    } else if (res.data.error_no == '-9999') {
                        _this.$router.push({
                            name: "Index"
                        });
                    } else {
                        _this.error_content = res.data.error_info;
                        _this.show_content = true;
                    }
                }).catch(function (err) {
                    console.log(err)
                });
            },
            field(eee) {
                var ref = /^\d{16,25}$/;
                if (!ref.test(eee)) {
                    this.show_content = true;
                    this.error_content = "请输入正确格式的卡号";
                }
            },
            forId(index) {
                return "card_number_" + index;
            },
            forBankBranchId(index) {
                return "banking_outlets_" + index;
            },
            flowInit() {
                var _this = this;
                _this.type = sessionStorage.getItem('business_type');
                // 方法：开始 流程 控制页面不能相互跳页面
                var data = {
                    "business_type": sessionStorage.getItem('business_type')
                };
                _this.axios.post('/flow/qryAcceptInfo', data).then(function (res) {
                    if (res.data.error_no == 0) {
                        _this.acceptNo = res.data.accept_no;
                        if (_this.$router.history.current.name != res.data.business_flow) {
                            _this.$router.push({name: res.data.business_flow});
                        }
                        // //同步数据  修改结算账户
                        if (_this.type == '19') {
                            var parameter = {
                                "systype": sessionStorage.getItem('systype'),
                                "accept_no": _this.acceptNo,
                                "business_type": sessionStorage.getItem('business_type')
                            }
                            _this.$ajax.post('/thirdDeposity/synchroClientAndBankInfo', parameter).then(function (res) {
                                if (res.data.error_no == 0) {
                                    let initData = {
                                        "time": new Date().getTime(),
                                        "systype": sessionStorage.getItem('systype')
                                    };
                                    _this.$ajax.post('/thirdDeposity/showBankInfo', initData).then(function (res) {
                                        if (res.data.error_no == 0) {
                                            var datas = res.data.bank_account_data;
                                            // for(var i=0;i<datas.length;i++) {
                                            if (_this.imgs.length == 0) {
                                                for (var i = (datas.length - 1); i >= 0; i--) {
                                                    var newImage = {};
                                                    newImage.imgUrl = "data:image/jpeg;base64," + datas[i].bank_pic;
                                                    if (datas[i].bank_pic.trim() == "nocard") {
                                                        newImage.imgUrl = _this.noCard.img;
                                                    }
                                                    newImage.bank_no = datas[i].bank_no;
                                                    newImage.bank_branch = datas[i].bank_branch;
                                                    newImage.bank_sign = datas[i].bank_sign;
                                                    newImage.bank_id = datas[i].bank_id;
                                                    newImage.bank_name = datas[i].bank_name;
                                                    newImage.bank_trx_no = datas[i].bank_trx_no;
                                                    _this.imgs.push(newImage);
                                                }
                                                if (_this.imgs.length == 0) {
                                                    _this.show = true;
                                                }
                                                // if(res.data.bank_trx_no == 1){
                                                //   //_this.bankUpload();
                                                //   _this.number = true;
                                                // }
                                                // if(res.data.bank_trx_no == 2){
                                                //   _this.bankUpload1();
                                                //    _this.number = true;
                                                // }

                                                var dataBanks = res.data.bank_list;
                                                for (var j = 0; j < dataBanks.length; j++) {
                                                    _this.bankMap.set(dataBanks[j].bankNo, dataBanks[j].bankName);
                                                }
                                                _this.bankLists = dataBanks;
                                            }

                                        } else if (res.data.error_no == '-9999') {
                                            _this.quit = true;
                                            _this.content = res.data.error_info;
                                        } else {
                                            _this.error_content = res.data.error_info;
                                            _this.show_content = true;
                                        }
                                    }).catch(function (err) {
                                        console.log(err)
                                    });
                                } else if (res.data.error_no == '-9999') {
                                    _this.quit = true;
                                    _this.content = res.data.error_info;
                                } else {
                                    _this.error_content = res.data.error_info;
                                    _this.show_content = true;
                                }
                            }).catch(function (err) {
                                console.log(err)
                            });
                        } else {
                            _this.getInitPic();
                        }
                        //结束
                    } else if (res.data.error_no == '-9999') {
                        _this.quit = true;
                        _this.content = res.data.error_info;
                    } else {
                        _this.error_content = res.data.error_info;
                        _this.show_content = true;
                    }
                }).catch(function (err) {
                    console.log(err);
                });


                // 方法：结束 流程 控制页面不能相互跳页面
            },
            onClickLeft() {
                var _this = this;
                if (_this.acceptNo != "" && _this.acceptNo != undefined) {
                    // 方法：开始 流程 跳转页面
                    var data = {
                        "accept_no": _this.acceptNo,
                        "business_type": sessionStorage.getItem('business_type'),
                        "business_button_type": "0", // 上一步 按钮类型
                        "business_flow": _this.$router.history.current.name
                    };
                    _this.loadingbox = true;
                    _this.$ajax.post('/flow/updateAccept', data).then(function (res) {
                        _this.loadingbox = false;
                        if (res.data.error_no == 0) {
                            _this.$router.push({
                                name: res.data.business_flow
                            });
                        } else if (res.data.error_no == '-9999') {
                            _this.quit = true;
                            _this.content = res.data.error_info;
                        } else {
                            _this.error_content = res.data.error_info;
                            _this.show_content = true;
                        }
                    }).catch(function (err) {
                        console.log(err);
                    });
                    // 方法：结束 流程 跳转页面
                }
            },
            onClickRight() {
                this.$router.push({name: 'MyBussiness'});
            },
            notarize() {
                this.show_content = false;
                if (this.onlySign != "") {
                    document.getElementById("card_number_" + this.onlySign).focus();
                    this.onlySign = "";
                }
                if (this.branchSign != "") {
                    document.getElementById("banking_outlets_" + this.branchSign).focus();
                    this.branchSign = "";
                }
            },
            jump(item, index) {
                if (item.bank_trx_no != 0) {
                    return;
                }
                var _this = this;
                _this.id = index;
                _this.bankId = item.bank_id;
                _this.bankItem = item
                _this.show2 = true;

            },
            noBankImgUpload(item, index) {
                var width_div = document.getElementsByClassName("bank_img")[0].getBoundingClientRect().width + "";
                var height_div = document.getElementsByClassName("bank_img")[0].getBoundingClientRect().width + "";
                //调用安卓 ios windows方法
                var browserVersion = Utils.iphOrAnz();
                this.testIndex = index;
                this.textBank = item.bank_sign;
                if (browserVersion == "iphone") {
                    var id_type = "81";
                    var aaa = "";
                    window.location.href = "objc://takePicture_5/?" + id_type + "?" + aaa + "?settleBankImage";
                } else if (browserVersion == "anzuo") {
                    jsHandle.takePic("81", width_div, height_div, "settleBankImage");
                } else if (browserVersion == "windows") {
                    console.log("windows");
                }
            },
            //点击上传
            bankUpload() {
                var width_div = document.getElementsByClassName("bank_img")[0].getBoundingClientRect().width + "";
                var height_div = document.getElementsByClassName("bank_img")[0].getBoundingClientRect().width + "";

                this.testIndex = '9988';
                this.textBank = '';
                //调用安卓 ios windows方法
                var browserVersion = Utils.iphOrAnz();
                if (browserVersion == "iphone") {
                    var id_type = "81";
                    var aaa = "";
                    window.location.href = "objc://takePicture_5/?" + id_type + "?" + aaa + "?settleBankImage";
                } else if (browserVersion == "anzuo") {
                    jsHandle.takePic("81", width_div, height_div, "settleBankImage");
                } else if (browserVersion == "windows") {
                    console.log("windows");
                }
            },
            settleBankImage(picNo, picStr) {
                if (picStr == '') {
                    return
                }
                this.myUploadBank(picNo, picStr)
            },
            myUploadBank(picNo, picStr) {   //upload自定义上传
                const self = this;  //这个很重要！读取文件base64
                //alert(self.testIndex);
                const bankDate = {
                    "image_no": picNo,
                    "image_data": picStr,
                    "systype": sessionStorage.getItem('systype'),
                    "business_type": sessionStorage.getItem('business_type'),
                    "bank_sign": self.textBank
                };
                self.loadingbox = true;
                self.$ajax.post('/record/uploadRecognition', bankDate).then(function (res) {
                    self.loadingbox = false;
                    if (res.data.error_no == 0) {
                        if (self.testIndex != '9988') {
                            self.imgs[self.testIndex].imgUrl = "data:image/jpeg;base64," + res.data.cropped_image;
                            return;
                        }
                        var newImages = {};
                        newImages.imgUrl = "data:image/jpeg;base64," + res.data.cropped_image;
                        newImages.bank_sign = res.data.bank_sign;
                        newImages.bank_no = res.data.bank_no;
                        newImages.bank_id = res.data.bank_id;
                        newImages.bank_branch = res.data.bank_branch;
                        newImages.bank_trx_no = 0;
                        var bank_id = res.data.bank_id;
                        if (bank_id == "") {
                            newImages.bank_name = "";
                        } else {
                            newImages.bank_name = self.bankMap.get(bank_id);
                        }
                        self.imgs.unshift(newImages);
                    } else if (res.data.error_no == -1 || res.data.error_no == -6059) {
                        if (self.testIndex != '9988') {
                            self.imgs[self.testIndex].imgUrl = "data:image/jpeg;base64," + res.data.cropped_image;
                            return;
                        }
                        self.error_content = res.data.error_info;
                        self.show_content = true;
                        var newImages = {};
                        newImages.imgUrl = "data:image/jpeg;base64," + res.data.cropped_image;
                        newImages.bank_sign = res.data.bank_sign;
                        newImages.bank_no = res.data.bank_no;
                        newImages.bank_id = res.data.bank_id;
                        newImages.bank_branch = res.data.bank_branch;
                        newImages.bank_trx_no = 0;
                        var bank_id = res.data.bank_id;
                        if (bank_id == "") {
                            newImages.bank_name = "";
                        } else {
                            newImages.bank_name = self.bankMap.get(bank_id);
                        }
                        self.imgs.unshift(newImages);
                    } else if (res.data.error_no == "-9999") {
                        self.quit = true;
                        self.content = res.data.error_info;
                    } else {
                        self.error_content = res.data.error_info;
                        self.show_content = true;
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
            getData() {
                var bank_index = this.$route.query.bankIndex;
                var bank_item = {};
                bank_item = this.$route.query.bankItem;
                bank_item.bank_id = this.$route.query.bankNo;
                bank_item.bank_name = this.$route.query.bankName;
                var bank_imgs = [];
                bank_imgs = this.$route.query.bankImgs;
                var bank_imgs_new = [];
                for (var i = 0; i < bank_imgs.length; i++) {
                    var obj = bank_imgs[i];
                    if (i == bank_index) {
                        bank_imgs_new.push(bank_item);
                    } else {
                        bank_imgs_new.push(obj);
                    }
                }
                this.imgs = bank_imgs_new;
            },
            goChoose(item, index) {
                localStorage.setItem("bankIndex", index);
            },
            deleteItem(item, index) {
                var _this = this;
                var deleteData = {
                  "systype": sessionStorage.getItem('systype'),
                  "bank_id": item.bank_id,
                  "bank_no": item.bank_no,
                  "bank_sign": item.bank_sign,
                  "bank_name":item.bank_name,
                  "accept_no":_this.acceptNo,
                };
                _this.loadingbox = true;
                _this.$ajax.post('/thirdDeposity/delBankInfo', deleteData).then(function (res) {
                    _this.loadingbox = false;
                    if (res.data.error_no == 0) {
                        _this.imgs.splice(index, 1);
                        if (_this.imgs.length == 0) {
                            _this.show = true;
                        }
                    } else if (res.data.error_no == '-9999') {
                        _this.quit = true;
                        _this.content = res.data.error_info;
                    } else {
                        _this.error_content = res.data.error_info;
                        _this.show_content = true;
                    }
                }).catch(function (err) {
                    console.log(err);
                });
            },
            onShow() {
                console.log('on show')
            },
            onHide(type) {
                console.log('on hide', type)
            },
            onRead(file) {
                var base64 = file.content.split(',')[1];
                const bankDate = {
                    "image_no": '81',
                    "image_data": base64,
                    "systype": sessionStorage.getItem('systype'),
                };
                var _this = this;
                this.$ajax.post('/record/uploadRecognition', bankDate).then(function (res) {
                    var newImages = {};
                    newImages.imgUrl = "data:image/jpeg;base64," + res.data.cropped_image;
                    newImages.bank_sign = res.data.bank_sign;
                    if (res.data.bank_no) {
                        newImages.bank_no = res.data.bank_no;
                    } else {
                        newImages.bank_no = '';
                    }
                    var bankName = res.data.bank_id;
                    _this.imgs.push(newImages);
                }).catch(function (error) {
                    console.log(error)
                })
            },
            Confirm() {
                var imgs = this.imgs;
                if (imgs.length == 0) {
                    this.show_content = true;
                    this.error_content = "请上传银行卡！";
                    return false
                }
                for (var j = 0; j < imgs.length; j++) {
                    var item = imgs[j];
                    if (item.bank_id == "") {
                        this.show_content = true;
                        this.error_content = "请选择银行！";
                        return false
                    }
                    var bankNo = item.bank_no + "";
                    if (bankNo == "") {
                        this.show_content = true;
                        this.error_content = "请填写银行卡号！";
                        this.onlySign = item.bank_sign;
                        return false
                    }
                    var ref = /^\d{16,25}$/;
                    if (!ref.test(bankNo)) {
                        this.show_content = true;
                        this.error_content = "请输入正确格式的银行卡号！";
                        return false
                    }
                    if (bankNo.length < 16 || bankNo.length > 26) {
                        this.show_content = true;
                        this.error_content = "请输入16-25位银行卡号！";
                        return false
                    }
                    if (item.bank_branch == "") {
                        this.show_content = true;
                        this.error_content = "请填写银行网点！";
                        this.branchSign = item.bank_sign;
                        return false
                    } else {
                        var reg = /^([0-9\u4e00-\u9fa5]{2,50}$)/;
                        if (reg.test(item.bank_branch) == false) {
                            this.show_content = true;
                            this.error_content = "您输入的银行网点信息有误！";
                            return false;
                        }
                    }
                }
                this.isShow = true;
                this.showContents = "您办理完毕期货账户开户后，还需联系对应银行通过手机银行或电脑端网银系统或银行柜台等方式办理银期签约后才能使用银期转账功能，各银行银期签约具体办理指引详询热线电话：" + sessionStorage.getItem('broker_hotline');
            },
            ensure() {
                var _this = this;
                _this.isShow = false;
                _this.saveData();
            },
            saveData() {
                //保存数据
                var _this = this;
                var saveData = {
                    "systype": sessionStorage.getItem('systype'),
                    "bank_account_data": _this.imgs,
                };
                _this.loadingbox = true;
                _this.$ajax.post('/thirdDeposity/saveBankInfo', saveData).then(function (res) {
                    _this.loadingbox = false;
                    if (res.data.error_no == 0) {
                        // 下一步判断受理单号是否为空,有受理单的情况
                        if (_this.acceptNo != "" || _this.acceptNo != undefined) {
                            var data = {
                                "accept_no": _this.acceptNo,
                                "business_type": sessionStorage.getItem('business_type'),
                                "business_button_type": '1', // 下一步
                                "business_flow": _this.$router.history.current.name,
                                "certificate_sign": "1"
                            };
                            _this.loadingbox = true;
                            _this.$ajax.post('/flow/updateAccept', data).then(function (res) {
                                _this.loadingbox = false;
                                if (res.data.error_no == 0) {
                                    _this.$router.push({
                                        name: res.data.business_flow
                                    });
                                } else if (res.data.error_no == '-9999') {
                                    _this.quit = true;
                                    _this.content = res.data.error_info;
                                } else {
                                    _this.error_content = res.data.error_info;
                                    _this.show_content = true;
                                }
                            })
                        }
                    } else if (res.data.error_no == '-9999') {
                        _this.quit = true;
                        _this.content = res.data.error_info;
                    } else {
                        _this.error_content = res.data.error_info;
                        _this.show_content = true;
                    }
                }).catch(function (error) {
                    console.log(error)
                });
            },
            getInitPic() {
                var _this = this;
                let initData = {
                    "time": new Date().getTime(),
                    "systype": sessionStorage.getItem('systype')
                };
                _this.$ajax.post('/thirdDeposity/showBankInfo', initData).then(function (res) {
                    if (res.data.error_no == 0) {
                        var datas = res.data.bank_account_data;
                        // for(var i=0;i<datas.length;i++) {
                        if (_this.imgs.length == 0) {
                            for (var i = (datas.length - 1); i >= 0; i--) {
                                var newImage = {};
                                newImage.imgUrl = "data:image/jpeg;base64," + datas[i].bank_pic;
                                newImage.bank_no = datas[i].bank_no;
                                newImage.bank_branch = datas[i].bank_branch;
                                newImage.bank_sign = datas[i].bank_sign;
                                newImage.bank_id = datas[i].bank_id;
                                newImage.bank_name = datas[i].bank_name;
                                newImage.bank_trx_no = datas[i].bank_trx_no;
                                _this.imgs.push(newImage);
                            }
                            if (_this.imgs.length == 0) {
                                _this.show = true;
                            }
                            var dataBanks = res.data.bank_list;
                            for (var j = 0; j < dataBanks.length; j++) {
                                _this.bankMap.set(dataBanks[j].bankNo, dataBanks[j].bankName);
                            }
                            _this.bankLists = dataBanks;
                        }
                    } else if (res.data.error_no == '-9999') {
                        _this.quit = true;
                        _this.content = res.data.error_info;
                    } else {
                        _this.error_content = res.data.error_info;
                        _this.show_content = true;
                    }
                }).catch(function (err) {
                    console.log(err)
                });
            },
            iKnow() {
                this.show = false;
            }
        },
    }
</script>

<style lang="less">
  .settleBank {
    overflow: hidden;
    background: #F6F6F6;

    .upload_div.van-uploader {
      width: 100%;
      height: 100%;
    }

    svg#Layer_1 {
      position: absolute;
      left: 82%;
      width: 25px;
    }

    .bankCard_contain {
      border: 1px solid lavender;
      width: 94%;
      height: 184px;
      margin: 4% auto;
      border-radius: 10px;
      background: #ffffff;

      img {
        border-radius: 10px;
      }
    }

    p {
      text-align: left;
    }

    .van-field__control {
      text-align: right;
      color: #323233;
    }
    .van-field--disabled .van-field__control{
      color: #323233;
    }
    .van-button--large {
      width: 92%;
      height: 50px;
      line-height: 38px;
      border: none;
      margin-top: 12%;
      color: #ffffff;
      background: #49a5ff;
    }

    .bankcardMessage {
      margin-left: 4%;
      line-height: 38px;
      color: #666666;
      font-size: 14px;
    }

    .settlementBank {
      margin-left: 4%;
      color: #666666;
      font-size: 14px;
      line-height: 13px;
    }

    .bankImg {
      width: 94%;
      margin: 0 auto;
      background: white;
      display: inline-block;
      border-radius: 10px;
      height: 169px;
      margin-top: 3%;
    }

    .bank_img {
      width: 100%;
      //height: 93%;
      padding-top: 1.5%;
    }

    .van-button {
      width: 84%;
      height: 45px;
      background-color: #49A5FF;
      margin: 23px auto;
      font-size: 15px;
      color: #ffffff;
      text-align: center;
      line-height: 44px;
      margin-bottom: 6%;
    }

    .photo {
      width: 257px;
      height: 155px;
      margin-top: -0.5%;
    }

    .img1 {
      height: 20px;
      width: 20px;
      float: left;
      padding-top: 8%;
      padding-left: 26px
    }

    .IKonw {
      width: 119px;
      height: 40px;
      border: 1px solid #FFFFFF;
      border-radius: 3px;
      font-family: PingFangSC-Regular;
      font-size: 16px;
      color: #FFFFFF;
      text-align: center;
      line-height: 16px;
      background: none;
      margin-top: 11%;
      position: fixed;
      bottom: 4%;
      left: 33%;
    }

    //ipad竖屏
    @media only screen and (min-device-width: 768px) and (max-device-width: 1024px) {
      .photo {
        width: 90%;
        height: auto;
      }

      .bankcardMessage {
        margin-left: 2%;
      }

      .settlementBank {
        margin-left: 2%;
      }

      .bankImg {
        height: auto;
        width: 56%;
      }

      .bank_img {
        padding-top: 2%;
        padding-bottom: 12px;
        width: 71%;
      }

      svg#Layer_1 {
        left: 76%;
        margin-top: 2%;
      }

      .img1 {
        padding-top: 4%;
      }

      .IKonw {
        left: 43%;
      }
    }

    //ipad横屏
    @media only screen and (min-width: 1349px) and (orientation: landscape) {
      .bankImg {
        width: 500px;
        height: 250px;
      }

      .photo {
        width: 286px;
        height: 174px;
        margin-top: 2%;
      }

      .bank_img {
        width: 309px;
        height: 191px;
        padding-top: 6%;
      }

      svg#Layer_1 {
        left: 68%;
        margin-top: 28px;
      }

      .settlementBank {
        margin-left: 1%;
        line-height: 37px;
      }

      .bankcardMessage {
        margin-left: 1%;
      }

      .img1 {
        padding-top: 2%;
      }

    }

    .weui-cell__ft {
      text-align: right;
      color: #999999;
      font-size: 14px;
    }

    .vux-label {
      display: inline-block;
      word-wrap: break-word;
      word-break: break-all;
      font-size: 14px;
    }

    .bankcardMessage-box {
      padding-top: 2%;
    }

    .bankListDiv {
      li {
        position: relative;
        float: left;
        width: 100%;
        border-bottom: 1px solid lavender;
        text-align: left;
        text-indent: 20px;

        .bankSuccess {
          color: green;
          position: absolute;
          right: 17px;
          top: 12px;
        }
      }
    }

    .specification {
      ont-family: PingFangSC-Regular;
      font-size: 18px;
      color: #FFFFFF;
      letter-spacing: 1.82px;
      padding-bottom: 15px;
      padding-top: 30px;
    }

    .li-img {
      width: 82%;
      height: auto;
      margin: 35px auto;
    }

    .img2 {
      height: 64px;
      width: 100px;
    }

    .word {
      float: right;
      padding-top: 14px;
      color: white;
      font-family: PingFangSC-Regular;
      letter-spacing: 1.32px;
      width: 34%;
      text-align: left;
    }

    .vanPopup {
      background: rgba(0, 0, 0, 0.6);
      width: 100%;
      height: 100%;
    }
  }
</style>
