<template>
  <div class="video">
    <Header :showOut="showOut"></Header>
    <Menu :activeIndex="activeIndex"></Menu>
    <div id="video_div1" class="titleCommon" style="padding-top: 75px;margin-left: 190px;">业务办理/{{bus_name}}<span
      style="font-size: 12px;color: #6f6f6f;">「受理单号:{{accept_no}}」</span>
    </div>
    <div id="video_div2" class="uploadContain" :style="{width:screenWidth, height:screenHeight}"
         style="margin-left:200px;">
      <div class="step uploadStep" v-if="this.type == this.navbar.openFuturesAccount">
        <el-steps :active="active" align-center process-status="finish" finish-status="success">
          <el-step :title="ntile.title" :key="index" class="stepTitle" v-for="(ntile,index) in navtitles"></el-step>
        </el-steps>
      </div>
      <div class="circleContain">
        <span class="circle"></span>
        <span class="Report">视频见证</span>
      </div>
      <div id="video_div" style="margin-top:1%; width:755px; height:auto;">
        <div id="" style="background-color:#ECF5FF;width:730px;height: 50%;border:solid 4px #DBDBDB;">
          <div style="height:100%">
            <div style="height:20%"></div>
            <div style="width:730px;line-height: 60px;text-align: center;">
              <span style="display: inline-block;width: 80px; border-top: 1px solid #ccc;"></span>
              <span style="font-size:18px;color: #686868;vertical-align: -4px;">请确保以下设备正常</span>
              <span style="display: inline-block;width: 80px; border-top: 1px solid #ccc;"></span>
            </div>
            <div style="width:730px;height:40%;">
              <table style="width: 90%;" align="center">
                <tr>
                  <td style="text-align: center;"><i id="hasMicrophone" ref="hasMicrophone" style="font-size:60px;"
                                                     class="iconfont icon-Microphone"></i>
                  </td>
                  <td style="text-align: center;"><i id="hasCamera" ref="hasCamera" style="font-size: 60px;"
                                                     class="iconfont icon-Camera"></i></td>
                  <td style="text-align: center;"><i id="hasSpeaker" ref="hasSpeaker" style="font-size: 60px;"
                                                     class="iconfont icon-Headphone"></i>
                  </td>
                </tr>
                <tr>
                  <td style="text-align: center;">麦克风</td>
                  <td style="text-align: center;">摄像头</td>
                  <td style="text-align: center;">耳机</td>
                </tr>
              </table>
            </div>
            <div style="height:30%"></div>
          </div>
        </div>
        <div class="btnGroup" style="width:443px; margin:4% auto; margin-bottom: 2%;">
          <el-button @click="videoGoHead" class="lastStep">上一步</el-button>
          <el-button @click="toVideo" class="nextStep">开始视频通话</el-button>
          <!-- <el-button @click="gogo" class="nextStep">测试跳过</el-button> -->
<!--          <span v-if="queueStatusFlag" style="margin-left:30px;cursor: pointer;font-size:12pt"
                @click="clkNextDate"><u>预约视频</u></span>-->
        </div>
        <div v-if="queueStatusFlag">
          <div style="font-size: 11pt;text-align: left;width:712px;">&ensp;&ensp;&ensp;&ensp;若当前排队人数过多，您也可点击“<span style="color:blue">预约视频</span>”进行预约；
            预约视频可预约<span style="color:blue">明日起近30日内</span>的指定时间段，您在预约时间段内登录开户云，可优先为您接通视频。
            <span v-if="queueStatusFlag" style="cursor: pointer;font-size:12pt;color:blue" @click="clkNextDate"><u>预约视频</u></span>
          </div>

          <div id="nextTimeID" ref="nextTimeRef" style="display: none;width:730px;text-align:center;margin-top:30px">
            <div style="font-weight: bold;margin-bottom: 10px;">预约视频</div>
            <div>预约日期：
              <el-date-picker
                type="date"
                v-model="nextDate"
                style="width:168px"
                :editable="false"
                :disabled="false"
                :clearable="true"
                value-format="yyyy-MM-dd"
                :picker-options="pickerOptions"
                @change="qryQueueNum"
              >
              </el-date-picker>
            </div>
            <div style="margin-top:10px;display: none" id="queueTip">
            <span>上午：
              已约<span style="font-weight:bold;color: red;margin-left: 5px">{{am_s}}</span>人
              可约<span style="font-weight:bold;color: darkgreen;margin-left: 5px">{{am_n}}</span>人
            </span>
              <br/>
              <span>下午：
              已约<span style="font-weight:bold;color: red;margin-left: 5px">{{pm_s}}</span>人
              可约<span style="font-weight:bold;color: darkgreen;margin-left: 5px">{{pm_n}}</span>人
            </span>
            </div>
            <div style="margin-top:10px;font-size: 11pt;
          color: orangered;text-align: center;display: none" id="queueTipErr"></div>
            <div style="margin-top:10px">预约时段：
              <el-radio-group v-model="nextTime" style="width:168px">
                <el-radio label="0" border size="mini">上午</el-radio>
                <el-radio label="1" border size="mini">下午</el-radio>
              </el-radio-group>
            </div>
          </div>
          <div class="btnGroup" id="nextTime_btn_id" ref="nextTimeBtnRef"
               style="display: none;margin-top:10px;margin-left:30%">
            <el-button @click="msgNextDate" class="lastStep">预约视频</el-button>
            <div style="font-size: 12pt;margin-top:10px;color: orange;margin-left:-15%;" v-if="orderVideoFlag">
              预约视频时间： <span style="font-weight: bold">{{orderVideoInfo}}</span>
              <el-button type="warning" style="height:28px;width:70px;" @click="orderVideoShow = true">取消预约</el-button>
            </div>
          </div>
        </div>
      </div>
      <!------------------------------------------------------anychat------------------------------------------------------>
      <div id="anychat" style="padding-top:50px;">
        <!-- 登录层 -->
        <!--<div id="anychatloginDiv" style="display: none"></div>-->
        <!-- 安装插件提示层 -->
        <!-- <div id="prompt_div" style="display: none">
          <div class="close_div">
            <div id="prompt_div_headline1">插件安装提示</div>
            <div id="prompt_div_headline2">刷新</div>
          </div>
          <div>
            <div id="prompt_div_line1"></div>
            <div id="prompt_div_line2">控件安装完成后，请重启浏览器</div>
            <div id="prompt_div_btn_load" onclick="pluginDownload">下载安装</div>
          </div>
        </div> -->

        <!-- 营业厅/队列选择层 -->
        <div class="enterRoom" id="enterRoom">
          <div class="head">
            <h2></h2>
            <a class="btn" id="roomOut"></a>
          </div>
          <div class="content">
            <div class="contentArea">
              <ul class="list" id="poptip">

              </ul>
            </div>
            <!-- 等待呼叫层 -->
            <center>
              <div class="callBox" style="margin-top: 10%">
                <div id="callLayer">
                  <h4>服务等待中</h4>
                  <br/>
                  <div id="queueMsg1">
                    <div class="queueBox">
                      当前排队人数共：<strong>{{totle}}</strong> 人，您现在排在第 <strong>{{self}}</strong> 位 <br/>
                      已等待时长：<strong>{{minutes}}分:{{seconds}}秒</strong>
                      <br/>
                      <br/>
                      <a class="confirmMsg btn btn-info" id="queueup" @click="deViedoQueue()">取消排队</a>
                    </div>
                  </div>
                  <div id="queueMsg2">
                    <p>坐席请求与您视频通话，是否接受？</p>
                    <a class="confirmMsg  btn btn-info" id="takeUp">接受</a>
                    <a class="confirmMsg  btn btn-info" id="reject">拒绝</a>
                  </div>
                </div>
              </div>

              <div id="videoCall" style="margin-top: 10%;height:400px;">
                <!--<table style="background: red;"><tr><td>-->
                <!-- 视频层 -->
                <div id="videoCallContent" style="margin-left:50px;height:400px;">
                  <div id="localVideoDiv">
                    <div id="localVideoPos">
                    </div>
                  </div>
                  <div id="remoteVideoDiv">
                    <div id="remoteVideoPos">
                    </div>
                  </div>
                </div>
                <!--</td></tr></table>-->


              </div>

            </center>
          </div>
        </div>

        <!-- 呼叫会话信息提示层 -->
        <div id="SessionPrompt_Div"></div>
        <!--参数设置界面 -->
        <div id="advanceset_div" title="参数设置">
          <!-- <div id="advanceset_div_close">×关闭</div> -->
          <div id="paramSetting_Tabs" class="advanceset_box">
            <ul id="advanceset_div_Div_Btn">
              <li><a id="Btn_Div_Video" class="Btn_sel" clickstate="true"
                     onclick="selectSettingMenu('videoSettingTab','Btn_Div_Video')">视频参数</a></li>
              <li><a id="Btn_Div_Audio" class="Btn_Div" clickstate="false"
                     onclick="selectSettingMenu('audioSettingTab','Btn_Div_Audio')">音频参数</a></li>
              <li><a id="Btn_Div_Record" class="Btn_Div" clickstate="false"
                     onclick="selectSettingMenu('recordSettingTab','Btn_Div_Record')">录像及拍照</a></li>
              <li><a id="Btn_Div_Other" class="Btn_Div" clickstate="false"
                     onclick="selectSettingMenu('otherSettingTab','Btn_Div_Other')">其他设置</a></li>
            </ul>
            <!--
              <div id="advanceset_div_Div_Btn">
                  <div id="Btn_Div_Device" clickstate="true" onclick="selectSettingMenu('Device_Interface','Btn_Div_Device')" >设备选择</div>
                  <div id="Btn_Div_Video" class="Btn_Div" clickstate="false" onclick="selectSettingMenu('Video_Parameter_Interface','Btn_Div_Video')" >视频参数</div>
                  <div id="Btn_Div_Sound" class="Btn_Div" clickstate="false" onclick="selectSettingMenu('Sound_Parameter_Interface','Btn_Div_Sound')"  >音频参数</div>
                  <div id="Btn_Div_Other" class="Btn_Div" clickstate="false" onclick="selectSettingMenu('Other_Parameter_Interface','Btn_Div_Other')"  >其他设置</div>
              </div>
            -->
            <div id="videoSettingTab">
              <p><label for="DeviceType_VideoCapture">视频设备：</label><select id="DeviceType_VideoCapture"
                                                                           class="form-control"
                                                                           onchange="GetTheValue('DeviceType_VideoCapture')"
                                                                           style=" width:280px;"></select></p>
              <fieldset id="videoParamsField">
                <legend>视频参数</legend>
                <p>
                  <input type="checkbox" id="ServerSetting" onclick="ChangeTheResult('ServerSetting')"
                         checked="checked"/>服务器配置参数优先
                </p>
                <table>
                  <tr>
                    <td><label for="quality">质量：</label><select id="quality" class="form-control"
                                                                onchange="GetTheValue('quality')" disabled="disabled"
                                                                style=" width:120px;"></select></td>
                    <td><label for="code_rate" class="tabRight">码率：</label><select id="code_rate" class="form-control"
                                                                                   onchange="GetTheValue('code_rate')"
                                                                                   disabled="disabled"
                                                                                   style=" width:120px;"></select></td>
                  </tr>
                  <tr>
                    <td><label for="distinguishability">分辨率：</label><select id="distinguishability" class="form-control"
                                                                            onchange="GetTheValue('distinguishability')"
                                                                            disabled="disabled"
                                                                            style=" width:120px;"></select></td>
                    <td><label for="frame_rate" class="tabRight">帧率：</label><select id="frame_rate" class="form-control"
                                                                                    onchange="GetTheValue('frame_rate')"
                                                                                    disabled="disabled"
                                                                                    style=" width:120px;"></select></td>
                  </tr>
                  <tr>
                    <td><label for="current_resolution">当前分辨率：</label><span id="current_resolution"></span></td>
                    <td><label for="preinstall" class="tabRight">预设：</label><select id="preinstall" class="form-control"
                                                                                    onchange="GetTheValue('preinstall')"
                                                                                    disabled="disabled"
                                                                                    style=" width:120px;"></select></td>
                  </tr>
                  <tr>
                    <td colspan="2" style="text-align:center;"><input id="apply_changes" class="btn btn-info"
                                                                      type="button" onclick="BtnApply()" value="参数生效"
                                                                      disabled="disabled"></input></td>
                  </tr>
                </table>
              </fieldset>


            </div>
            <div id="audioSettingTab">
              <p><label for="DeviceType_AudioCapture">录音设备：</label><select id="DeviceType_AudioCapture"
                                                                           class="form-control"
                                                                           onchange="GetTheValue('DeviceType_AudioCapture')"
                                                                           style=" width:280px;"></select></p>
              <p><label for="DeviceType_AudioPlayBack">放音设备：</label><select id="DeviceType_AudioPlayBack"
                                                                            class="form-control"
                                                                            onchange="GetTheValue('DeviceType_AudioPlayBack')"
                                                                            style=" width:280px;"></select></p>
              <!--
                <div style=" height:30px; margin:10px 0px 0px 5px;">录音设备：<select id="DeviceType_AudioCapture" onchange="GetTheValue('DeviceType_AudioCapture')" style=" width:250px;"></select><br />
                          放音设备：<select id="DeviceType_AudioPlayBack" onchange="GetTheValue('DeviceType_AudioPlayBack')" style=" width:250px;"></select></div>
                -->
              <fieldset>
                <legend>音频参数</legend>
                <p><label for="Speak_Mode">选择声音模式：</label><select id="Speak_Mode" class="form-control"
                                                                  style=" width:140px;"
                                                                  onChange="GetTheValue('Speak_Mode')"></select></p>
                <p>
                  <input id="audio_vadctrl" onchange="ChangeTheResult('audio_vadctrl')" type="checkbox"/>静音检测
                  <input id="audio_echoctrl" onchange="ChangeTheResult('audio_echoctrl')" type="checkbox"/>回音消除
                  <input id="audio_nsctrl" onchange="ChangeTheResult('audio_nsctrl')" type="checkbox"/>噪音抑制
                  <input id="audio_agcctrl" onchange="ChangeTheResult('audio_agcctrl')" type="checkbox"/>自动增益
                </p>
              </fieldset>

            </div>
            <div id="recordSettingTab">
              <div class="widget">
                <fieldset>
                  <legend>拍照设置</legend>
                  <p>
                    <label>拍照对象选择：</label>
                    <input type="radio" name="snapshotObjectSelect" id="snapshotObjectSelect_owner" value=0>
                    <label for="snapshotObjectSelect_owner">拍自己</label>

                    <input type="radio" name="snapshotObjectSelect" id="snapshotObjectSelect_other" value=1
                           checked="checked">
                    <label for="snapshotObjectSelect_other">拍对方</label>
                  </p>
                  <p>
                    <label>拍照存储选择：</label>
                    <input type="radio" name="snapshotObjectSave" id="snapshotObjectSave_local" value=0
                           checked="checked">
                    <label for="snapshotObjectSave_local">存本地</label>

                    <input type="radio" name="snapshotObjectSave" id="snapshotObjectSave_server" value=1>
                    <label for="snapshotObjectSave_server">存服务器</label>

                  </p>
                  <p>
                    <label>拍照本地存储路径：</label>
                    <input type="text" id="snapshotSavePath" class="form-control" value="d:\anychat\snapshot">
                  </p>
                </fieldset>
                <fieldset>
                  <legend>录像设置</legend>
                  <p>
                    <label>选择录像方式</label>
                    <select id="recordTypeSelect" class="form-control" style="width:150px;"></select>

                    <span id="recordObject">
                  <input type="radio" name="recordObjectSelect" id="recordObjectSelect_owner" value=0>
                        <label for="recordObjectSelect_owner">录自己</label>

                        <input type="radio" name="recordObjectSelect" id="recordObjectSelect_other" value=1
                               checked="true">
                        <label for="recordObjectSelect_other">录对方</label>
                      </span>
                  </p>
                  <p>
                    <label>选择录制画面模式：</label>
                    <select id="videoAlignModeSelect" class="form-control" style="width:150px;"></select>
                  </p>
                  <p>
                    <label>选择录像保存文件：</label>
                    <select id="videoFileSelect" class="form-control" style="width:150px;"></select>
                  </p>
                  <p>
                    <label>录像本地存储路径：</label>
                    <input type="text" class="form-control" id="recordSavePath" value="d:\anychat\record">
                  </p>
                </fieldset>

              </div>
            </div>

            <div id="otherSettingTab">
              <fieldset>
                <p>
                  <input id="Checkbox_P2P" onchange="ChangeTheResult('Checkbox_P2P')" type="checkbox"/>P2P优先
                  <label for="videoshow_clipmode" class="tabRight" style="width:180px;">视频显示裁剪模式：</label><select
                  id="videoshow_clipmode" class="form-control" onchange="GetTheValue('videoshow_clipmode')"
                  style=" width:120px;"></select>
                </p>
              </fieldset>
            </div>
          </div>
        </div>
        <!-- 传输任务文件选择对话框 -->
        <div id="fileTransDiv" title="文件传输">
          <p style="font-size: 20px;">已选择的文件: <span id="filename" style="color: red;"></span></p>
          <input type="file" id="choosefile" style="display: none;">
          <!-- <div id="fileBtnDiv"><button id="filebtn">选择文件</button><button id="transbtn">发送文件</button></div> -->
        </div>
        <!-- 确认操作对话框 -->
        <div id="dialog-confirm" class="dialog-confirm" title="提示"></div>
      </div>
      <!------------------------------------------------------anychat------------------------------------------------------>
      <div id="nextStep" style="margin-left: 25%;display: none;">
        <div>
          <div class="img_div">
            <img src="~@a/other/videoP.jpg" alt="">
          </div>
          <div class="videoTxt">
            视频验证完成,请点击"下一步",继续完成开户操作
            <!--<div style="float:right;">-->
            <el-button @click="videoNext"
                       style="background:#169BD5;height:35px;color: white;width: 80px;margin-right: 5px;">下一步
            </el-button>
            <!--</div>-->
          </div>
        </div>
      </div>
      <!--  <div id="reStart">-->
      <!-- <el-dialog
         title=""
         :visible.sync="reStart"
         width="50%"
         custom-class="customClass"
         :show-close="false"
         :close-on-click-modal="false"
         :modal="false"
         center>
         <div>视频验证未通过11</div>
         <div>{{reject}}</div>
         <div slot="footer" class="dialog-footer">
           <el-button type="primary" class="nextStep" style="width: 150px;" @click="reSatrt">重新开始1</el-button>
         </div>
       </el-dialog>-->
      <el-dialog
        :close-on-click-modal="false"
        :close-on-press-escape="false"
        title="提示"
        :lock-scroll="false"
        :visible.sync="reStart"
        :show-close="false"
        width="40%"
        center>
        <span style="font-size:16px">视频验证未通过</span>
        <span> </span><br/>
        <span class="lineFeed">{{reject}}</span>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" class="nextStep" style="width: 150px;" @click="reSatrt">重新开始</el-button>
        </div>
      </el-dialog>
      <el-dialog
        :close-on-click-modal="false"
        :close-on-press-escape="false"
        title="提示"
        :lock-scroll="false"
        :visible.sync="reVideo"
        :show-close="false"
        width="40%"
        center>
        <span style="font-size:16px">视频异常中断,请重新发起视频</span>
        <div slot="footer" class="dialog-footer">
          <!--          <el-button type="primary" class="lastStep" style="width: 100px;" @click="deViedoQueue()">取消</el-button>
                    <el-button type="primary" class="nextStep" style="width: 100px;" @click="redoVideo">确定</el-button>  -->
          <el-button type="primary" class="nextStep" style="width: 150px;" @click="redoVideo">重新发起视频</el-button>
        </div>
      </el-dialog>
      <el-dialog
        :close-on-click-modal="false"
        :close-on-press-escape="false"
        :modal-append-to-body="false"
        :lock-scroll="false"
        :show-close="false"
        title="提示"
        :visible.sync="orderVideoShow"
        width="30%"
        center>
        <span>确定取消预约排队么？</span>
        <span slot="footer" class="dialog-footer">
        <el-button type="primary" style="height:40px;width:80px;" @click="orderVideoShow = false">取消</el-button>
        <el-button type="primary" style="height:40px;width:80px;" @click="cancleOrderQueue('y')">确定</el-button>
      </span>
      </el-dialog>

      <!-- </div>-->
      <!--      <div id="reVideo">-->
      <!--        <el-dialog
                title=""
                :show-close="false"
                :visible.sync="reVideo"
                width="50%"
                :close-on-click-modal="false"
                :modal="false"
                center>
                <div>视频异常中断,重新发起视频</div>
                <div slot="footer" class="dialog-footer">
                  <el-button type="primary" class="videoClass" @click="redoVideo">重新发起视频</el-button>
                </div>
              </el-dialog>-->
      <!--      </div>-->
      <!--      <div
              type="primary"
              v-loading.fullscreen.lock="fullscreenLoading"
              element-loading-spinner="el-icon-loading"
              element-loading-background="rgba(0, 0, 0, 0.8)"
              :element-loading-text=this.loadingText
              style="width: 100%"
            >
            </div>-->
      <ScreenLoading :fullscreenLoading="fullscreenLoading" :loadingText="loadingText"></ScreenLoading>
    </div>
    <!-- <el-dialog
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :lock-scroll="false"
      title="插件安装提示"
      :visible.sync="safePlugin"
      :show-close="false"
      width="30%"
      center>
      <span class="promptTips2">{{pluginTips}}</span>
      <span> </span><br/>
      <span style="font-size: 20px;">安装完毕后需要刷新浏览器。</span>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="pluginDownload" class="nextStep">下载安装</el-button>
      </span>
    </el-dialog> -->

    <el-dialog
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      title="提示"
      :lock-scroll="false"
      :visible.sync="linkVideoFail"
      :show-close="false"
      width="40%"
      center>
      <div class="lineFeed" style="font-size:16px">{{linkVideoFailText}}</div>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" class="nextStep" style="width: 80px;height:40px;" @click="linkVideoFailLoad">确 定
        </el-button>
      </div>
    </el-dialog>
    <el-dialog
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :lock-scroll="false"
      title="提示"
      :visible.sync="ndDlgFlag"
      :show-close="false"
      width="30%"
      center>
      <span v-html="ndMsg"></span>
      <span slot="footer" class="dialog-footer" v-if="ndFlag">
        <el-button type="primary" @click="ndDlgFlag=false,ndFlag=false" style="width: 80px;height:40px;">取 消</el-button>
        <el-button type="primary" @click="submitNextDate" style="width: 80px;height:40px;">确 定</el-button>
      </span>
      <span slot="footer" class="dialog-footer" v-if="ndOFlag">
        <el-button type="primary" @click="goOut" style="width: 80px;height:40px;">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog
      title="拍照规范"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :visible.sync="tipsDialog"
      :show-close="false"
      :lock-scroll="false">
      <div style="width:100%;">
        <span class="tipsFont" style="margin-left:7%;">请确保正对镜头<br/>
      &nbsp;&nbsp;&nbsp;&nbsp;使脸部完整露出</span>
        <span class="tipsFont">请保持镜头平稳<br/>
      &nbsp;&nbsp;&nbsp;&nbsp;避免镜头的晃动</span>
        <span class="tipsFont">请确保光线充足<br/>
      &nbsp;&nbsp;&nbsp;&nbsp;避免太强或太弱</span>
      </div>
      <div style="width:100%;text-align:center;padding-top: 10px;">
        <span class="tipsImg" style="margin-left:7%;"><img :src="splz01"/></span>
        <span class="tipsImg"><img :src="splz02"/></span>
        <span class="tipsImg"><img :src="splz03"/></span>
      </div>
      <div style="clear:both;padding-top:10px;">
        <span class="iconfont icon-Sucess tipsFont" style="color:seagreen;font-size:16pt;margin-left:7%;"></span>
        <span class="iconfont icon-Failed tipsFont" style="color:#FF4E50;font-size:16pt;"></span>
        <span class="iconfont icon-Failed tipsFont" style="color:#FF4E50;font-size:16pt;"></span>
      </div>
      <div class="btnGroup" style="margin-left:0;width:100%;text-align:center;padding-top: 10px;">
        <el-button type="primary" @click="tipsDialog = false" class="nextStep">我知道了</el-button>
      </div>
    </el-dialog>
    <PopDialog :popShowDialog="popPShow" :popTitle="popPTitle" :popContext="popPText"
               :outSystem="outSystem" :outSysName="outSysName"></PopDialog>
  </div>
</template>

<script type="text/ecmascript-6">
    import Header from '../header.vue';
    import Menu from '@c/menu.vue';
    import ScreenLoading from "@c/screenLoading.vue";
    import PopDialog from '@c/popupDialog.vue';

    export default {
        components: {
            PopDialog,
            ScreenLoading,
            Header, Menu
        },
        inject: ['reload'],
        data() {
            return {
                popPShow: false,
                outSystem: false,
                popPText: '',
                outSysName: '',
                popPTitle: '提示',
                btnConfirm: '确 定',
                timeOutTips: '登录超时，请重新登录！',
                fullscreenLoading: false,
                loadingText: '',

                active: 5,
                bus_name: '',
                type: '',
                showDiv: false,
                reject: '',
                btn_value: '重新开始',
                reVideo: false,
                activeIndex: '11',
                showOut: true,
                sn: '',
                accept_no: '',
                queueNo: '',
                reStart: false,
                closeImg: require('../../style/img/others/close.png'),
                timeOut: false,
                navtitles: this.navbar.navtitles,
                safePlugin: false,
                pluginTips: '',
                hasMicrophone: true,
                hasCamera: true,
                hasSpeaker: true,
                linkVideoFail: false,
                linkVideoFailText: '视频服务连接失败。',
                screenWidth: '1000px',
                screenHeight: '530px',
                nextDate: '',
                nextTime: '',
                ndDlgFlag: false,
                ndFlag: false,
                ndMsg: '',
                ndOFlag: false,

                pickerOptions: {
                    disabledDate(time) {// /(1000*60*60*24)
                        var nowTime = Date.now();
                        var selTime = time.getTime();
                        if (selTime < nowTime) {
                            return true;
                        }
                        if ((selTime - nowTime) / (1000 * 60 * 60 * 24) > 30) {
                            return true;
                        }
                        var day = new Date(time).getDay();
                        if (day == 0 || day == 6) {
                            return true;
                        }
                        return false;
                    }
                },
                tipsDialog: false,
                splz01: require('@a/tips/splz01.png'),
                splz02: require('@a/tips/splz02.png'),
                splz03: require('@a/tips/splz03.png'),
                orderVideoShow: false,
                orderVideoFlag: false,
                orderVideoInfo: '',
                orderVideoBsname: '',
                orderVideoAccpetNo: '',
                am_s: 0,
                am_n: 0,
                pm_s: 0,
                pm_n: 0,
                queueStatusFlag: false,
                seconds: 0,
                minutes: 0,
                timer: '',
                self: '',
                totle: '',
                ws:'',
                orgCode:''
            }
        },
        created() {
            this.navbar.inVideoQueue = 'no';
            sessionStorage.setItem('khyQueueStatus', 'out');
            /*      document.onkeydown = function (e) {//按键
                    let key = window.event.keyCode;
                    if (key == 116) {

                    }
                  };
                  window.onbeforeunload = function(e){//关闭浏览器
                    alert(472);
                  };*/
            //监控键盘
            /*      addEventListener("keydown", function (e) {
                    if (e.keyCode == 116) {
                      alert('F5');
                      var errorCode = BRAC_Logout();
                      console.log("视频注销系统errorcode" + errorCode);
                      errorCode = BRAC_Release();
                      console.log("视频退出插件errorcode" + errorCode);
                    }
                  }, false);*/
            //监控浏览器
            window.addEventListener('beforeunload', e => {
                var errorcode = BRAC_Logout();
                console.log("视频注销系统errorcode" + errorcode);
                errorcode = BRAC_Release();
                console.log("视频退出插件errorcode" + errorcode);
            });

            this.type = sessionStorage.getItem('business_type');

            this.bus_name = this.navbar.menu_map.get('menu' + this.type);
            this.activeIndex = this.type;

            var _this = this;
            var data = {
                "business_type": sessionStorage.getItem('business_type'),
                "register_way": '1',
            };
            this.axios.post('/flow/qryAcceptInfo', data).then(function (res) {
                var data = {
                    "accept_no": res.data.accept_no,
                    "business_flow": _this.$router.history.current.name,
                    "business_type": sessionStorage.getItem('business_type'),
                    "business_button_type": '1',//下一步
                    "accept_status": ""     //"accept_status":_this.accept_status,当需要更改状态时再传值
                };
                _this.accept_no = res.data.accept_no;

                _this.business_flow = res.data.business_flow;
                if (res.data.accept_status == '7') {
                    _this.reStart = true;
                } else {
                    //_this.showDiv=true;
                    GetID("video_div").style.display = "";
                }
                if (_this.$router.history.current.name != res.data.business_flow) {
                    _this.$router.push('/error')
                }
            }).catch(function (err) {
                console.log(err)
            });

            var data2 = {'date': new Date().getTime(), "business_type": sessionStorage.getItem('business_type')}
            this.axios.post('/video/qryVideoStatus', data2).then(function (res) {
                if (res.data.accept_status == '7') {  //驳回
                    _this.reject = res.data.video_remark;
                }
            });

            this.qryOrderQueue();
        },
        mounted() {
            sessionStorage.setItem("vBaskURI", this.axios.defaults.baseURL);

            if (this.navbar.screenWidth > 1000) {
                this.screenWidth = this.navbar.screenWidth
            }
            if (this.navbar.screenHeight > 530) {
                this.screenHeight = this.navbar.screenHeight
            }
            // this.screenWidth = sessionStorage.getItem('screenWidth');
            // this.screenHeight = sessionStorage.getItem('screenHeight');
            var a = this;
            getData(a);
            console.log("anychatIsInit=" + anychatIsInit);
            //if (anychatIsInit == false) {
            this.LogicInit2();
            // }

            var _this = this;
            var _tipsDialog = sessionStorage.getItem('tipsDialog');
            if (_tipsDialog == '' || _tipsDialog == null || _tipsDialog == undefined) {
                _this.tipsDialog = true;
            }
            ;
            var data = {
                "business_type": sessionStorage.getItem('business_type'),
                "register_way": '1',
            };
            this.axios.post('/flow/qryAcceptInfo', data).then(function (res) {
                window.updateFlow = function () {
                    var data = {
                        "accept_no": res.data.accept_no,
                        "business_type": sessionStorage.getItem('business_type'),
                        "business_button_type": '1',//下一步
                        "accept_status": "",
                        "business_flow": _this.$router.history.current.name,
                    };
                    _this.axios.post('/flow/updateAccept', data).then(function (res) {
                        _this.$router.push({
                            name: res.data.business_flow,
                        });
                    })
                }
                _this.accept_no = res.data.accept_no;
                _this.business_flow = res.data.business_flow;
                if (_this.$router.history.current.name != res.data.business_flow) {
                    _this.$router.push('/error')
                }
            }).catch(function (err) {
                console.log(err)
            });

            _this.loadDevice();

            if (sessionStorage.getItem('linkVideoFail') == 'linkSuccess') {
                sessionStorage.setItem('linkVideoFail', '');
                if (Number(sessionStorage.getItem('failNum')) > 2) {
                    sessionStorage.setItem('failNum', '0');
                    _this.linkVideoFail = true;
                    _this.linkVideoFailText = "视频服务器连接失败，请退出系统重新登录或联系期货公司咨询，联系电话：" + sessionStorage.getItem('broker_hotline');
                } else {
                    _this.toVideo();
                }
            }

            if (sessionStorage.getItem('redoVideo') == 'redoVideo') {
                setTimeout(() => {
                    sessionStorage.setItem('redoVideo', '');
                    _this.toVideo();
                }, 300);
            }
        },
        methods: {
          videoBeforeStart(dwUserId){
            console.log(dwUserId,'dwUserId')
              let option = {
                "sendFlag": '5',
                "businessType":sessionStorage.getItem('business_type'),
                "orgCode":this.orgCode,
                "userVideo":dwUserId
              }
              this.ws.send(JSON.stringify(option))
              
           },
            messageMethod(num,text) {
              if (num == 0) {
                  this.$message.success(text)
              } else {
                this.$message.error(text)
              }
               
            },
            anychatVersion(param){//anychat版本号
              this.axios.post('/video/anychatVersion',{"log_info":param,"accept_no":this.accept_no,"business_type":sessionStorage.getItem('business_type')}).then(function (res) {
              })
            },
            qryQueueNum() {
                if (this.nextDate == '' || this.nextDate == null) {
                    document.getElementById('queueTip').style.display = 'none';
                    document.getElementById('queueTipErr').style.display = 'none';
                    return;
                }
                var _this = this;
                var pQueue = {
                    reservation_time: _this.nextDate,
                    order_time: '',//时间段
                    check_order_number: '2',//1:时间段必传，2：时间段无效
                    business_type: sessionStorage.getItem('business_type'),
                }
                this.axios.post('video/qryOrderQueue', pQueue).then(function (res) {
                    if (res.data.error_no == 0) {
                        _this.am_s = res.data.appointment_SAM;
                        _this.am_n = res.data.appointment_TAM - res.data.appointment_SAM;
                        _this.pm_s = res.data.appointment_SPM;
                        _this.pm_n = res.data.appointment_TPM - res.data.appointment_SPM;
                        document.getElementById('queueTip').style.display = '';
                        //_this.$refs.queueTip.style.display = '';
                    } else if (res.data.error_no == -1) {
                        document.getElementById('queueTipErr').innerHTML = res.data.error_info;
                        document.getElementById('queueTipErr').style.display = '';
                    }
                }).catch(function (err) {
                    console.log('查询预约人数失败');
                    _this.fosParamScreenLoading();
                })
            },
            redoVideo() {
                this.reVideo = false;
                // this.LogicInit3();
                cancelEvent();
                this.toVideo();
                // AnychatscoreAll.cancelEvent();
            },
            reSatrt() {
                this.reStart = true;
                this.navbar.inVideoQueue = 'no';
//	    this.nextMethod();
                cancelEvent();
                this.nextMethod2();
                // AnychatscoreAll.cancelEvent();
            },
            deViedoQueue() {
                var _this = this;
                _this.navbar.inVideoQueue = 'no';
                if(queFlag == 1) {
                  console.log(queFlag,'_this.queFlag')
                  let option = {
                      "sendFlag": '4',
                      "businessType":sessionStorage.getItem('business_type'),
                      "orgCode":_this.orgCode,
                    }
                    
                    _this.ws.send(JSON.stringify(option))
                    clearInterval(_this.timer);
                    _this.minutes = 0;
                    _this.seconds = 0;
                    $('.callBox').hide();
                    sessionStorage.setItem('tipsDialog', 'deViedoQueue');
                    sessionStorage.setItem('khyQueueStatus', 'out');
                    setTimeout(() => {
                    
                      _this.reload();
                    }, 500);
                    // _this.$message.error('排队取消关闭,即将刷新页面请耐心等待');
                    // setTimeout(()=>_this.$router.go(0),300);
                    return
                }
                sessionStorage.setItem('tipsDialog', 'deViedoQueue');
                sessionStorage.setItem('khyQueueStatus', 'out');
                setTimeout(() => {
                    //   cancelEvent();
                    _this.reload();
                }, 500);
            },
            refreshHref() {
                this.reload();
            },
            gogo() {
                var _this = this;
                var data = {
                    "accept_no": _this.accept_no,
                    "business_type": sessionStorage.getItem('business_type'),
                    "business_flow": _this.$router.history.current.name,
                    "certificate_sign": '1',
                    "business_button_type": '1',//下一步
                    "accept_status": ""     //"accept_status":_this.accept_status,当需要更改状态时再传值
                };
                this.axios.post('/flow/updateAccept', data).then(function (res) {
                    _this.$router.push({
                        name: res.data.business_flow,
                    });
                });
            },
            toVideo() {
              var _this = this;
              _this.toParamScreenLoading("排队准备中，请稍后...  当长时间未排队成功，请刷新重试");
              clearInterval(_this.timer);
              if (queFlag == 1) {
                  console.log(test,'sssss')
                  const ws = new WebSocket(wsUrl);
                  test.sendFlag = "1";
                  _this.LogicInit3();
                  console.log(ws,'ssss')
                  _this.anychatVersion(" {\"isCall\":\"1\"} ");
                  _this.orgCode = test.orgCode
                  let message = JSON.stringify(test)
                  ws.onopen = function (event) {
                        if (ws.readyState === 1) {
                          // 连接成功，可以通信了
                          console.log('连接成功，可以通信了')
                          ws.send(message)
                          _this.ws = ws
                          _this.formatDate();
                          
                        }
                      }
                      ws.onmessage =  function (event) {
                        let {data} = event;
                        console.log(data,'data')
                        let dataList = JSON.parse(data)
                        
                        
                        if (dataList.status == 1) {
                            _this.self = dataList.self;
                            _this.totle = dataList.total;
                           
                        }
                        if (dataList.status == 6) {
                          sessionStorage.setItem("roomNo",dataList.roomNo)
                        }
                        
                      }
                      ws.onerror = function (event) {
                        console.error('WS异常事件：', event)
                      }
                      ws.onclose = function (event) {
                        console.error('WS关闭事件：', event.code)
                        if (event.code == 4001) {
                            _this.$message.error('Wesoket队列关闭,即将刷新页面请耐心等待');
                            setTimeout(()=>_this.$router.go(0),300);
                            return
                            
                        }
                        if (event.code === 4005 ) {
                            clearInterval(_this.timer);
                            _this.minutes = 0;
                            _this.seconds = 0;
                            $('.callBox').hide();
                            $("#video_div").hide();
                        }
                          if (event.code == 4002 || event.code === 4006) {
                            $('.callBox').hide();
                            _this.$message.error('Wesoket队列关闭,即将刷新页面请耐心等待');
                            setTimeout(()=>_this.$router.go(0),300);
                          }
                          _this.ws = ''
                      }
                  return
              }
             
                this.navbar.inVideoQueue = 'no';
                this.axios.post('/video/isTrade', {"business_type": sessionStorage.getItem('business_type')}).then(function (res) {
                    if (res.data.error_no == 0) {
                        _this.timeOut = false;
                        // _this.fullscreenLoading=false;
                        cancelEvent();
                        // _this.videoQueueRecord();
                        //发起视频时增加 20201228
                        _this.anychatVersion(" {\"isCall\":\"1\"} ");
                        setTimeout(() => {
                            _this.LogicInit3();
                        }, 300);
                    } else if (res.data.error_no == -9999) {
                        _this.initDialogParam(_this.timeOutTips);
                        _this.fosParamScreenLoading();
                    } else if (res.data.error_no == -1098) {
                        _this.initDialogParam('非视频时间，如有疑问请联系期货公司，联系电话：' + sessionStorage.getItem('broker_hotline'));
                        _this.fosParamScreenLoading();
                    } else {
                        _this.$message.error(res.data.error_info);
                        _this.fosParamScreenLoading();
                    }
                }).catch(function (error) {
                    // _this.$message.error("调用接口错误！");
                    _this.reload();
                    return;
                });
                // AnychatscoreAll.cancelEvent();
            },
            videoQueueRecord() {
                var _this = this;
                this.axios.post('/video/saveUserVideoNotVerified', {
                    'accept_no': _this.accept_no,
                    'business_type': sessionStorage.getItem('business_type')
                }).then(function (res) {
                    if (res.data.error_no == 0) {
                        console.log('video.vue insert videoQueueRecord sucess!!' + new Date());
                    } else if (res.data.error_no == '-9999') {
                        // _this.timeOut = true;
                        // _this.fullscreenLoading = false;
                    } else {
                        // _this.$message.error(res.data.error_info);
                        // _this.fullscreenLoading = false;
                    }
                }).catch(function (error) {
                });
            },
             // 格式化时分秒
             formatDate () {
              var _this = this;
              _this.timer = setInterval(function(){       //开启定时器
                    _this.seconds++;                              //seconds每10毫秒加1
                    if(_this.seconds == 60){                    //当seconds一直增加到等于60的时候
                        _this.seconds = 0;                      //seconds清空等于0
                        _this.minutes++;                        //然后miutes加上1
                  }                                    //minutes == minutesNode里面的value
                  if(_this.minutes == 60){                    //当minutes一直增加到等于5的时候
                      clearInterval(_this.timer);            //清理定时器，停止运行！！！
                  }
              },1000);
            },

            videoNext() {
                this.navbar.inVideoQueue = 'no';
                var _this = this;
                this.axios.post('/cert/getSn', {}).then(function (res) {
                    if (res.data.error_no == 0) {
                        _this.sn = res.data.ca_sn;
                        if (!_this.sn || !window.devDetectCert(_this.sn)) {
                            _this.nextMethod('5');
                            /*没有证书随意‘’*/
                        } else {
                            _this.nextMethod('1');
                            /*有证书1，*/
                        }
                    } else if (res.data.error_no == '-9999') {
                        _this.initDialogParam(_this.timeOutTips);
                    } else if (res.data.error_no = '-1605') {
                        _this.nextMethod('5');
                    }

                }).catch(function (error) {

                });
            },
            nextMethod(stepFlag) {
                var _this = this;
                var data = {
                    "accept_no": _this.accept_no,
                    "business_type": sessionStorage.getItem('business_type'),
                    "business_flow": _this.$router.history.current.name,
                    "certificate_sign": stepFlag,
                    "business_button_type": '1',//下一步
                    "accept_status": ""     //"accept_status":_this.accept_status,当需要更改状态时再传值
                };
                _this.axios.post('/flow/updateAccept', data).then(function (res) {
                    if (res.data.business_flow == "Video") {
                        // _this.showDiv = true;
                        GetID("video_div").style.display = "";
                    } else {
                        // _this.showDiv = false;
                        GetID("video_div").style.display = "none";
                    }
                    _this.cancleOrderQueue('n');
                    _this.$router.push({
                        name: res.data.business_flow,
                    });
                })
            },
            nextMethod2(stepFlag) {
                var _this = this;
                var data = {
                    "accept_no": _this.accept_no,
                    "business_type": sessionStorage.getItem('business_type'),
                    "business_flow": _this.$router.history.current.name,
                    "certificate_sign": stepFlag,
                    "business_button_type": '2',//下一步
                    "accept_status": ""     //"accept_status":_this.accept_status,当需要更改状态时再传值
                };
                _this.axios.post('/flow/updateAccept', data).then(function (res) {
                    if (res.data.business_flow == "Video") {
                        // _this.showDiv = true;
                        GetID("video_div").style.display = "";
                        sessionStorage.setItem('tipsDialog', 'reStart');
                        _this.reload();
                    } else {
                        // _this.showDiv = false;
                        GetID("video_div").style.display = "none";
                    }
                    _this.$router.push({
                        name: res.data.business_flow,
                    });
                })
            },
            videoGoHead() {
                var data = {
                    "accept_no": this.accept_no,
                    "business_type": sessionStorage.getItem('business_type'),
                    "business_button_type": '0',//上一步
                    "business_flow": this.$router.history.current.name,
                    "accept_status": ""     //"accept_status":_this.accept_status,当需要更改状态时再传值
                };
                var _this = this;
                this.axios.post('/flow/updateAccept', data).then(function (res) {
                    if (res.data.error_no == 0) {
                        _this.$router.push({
                            name: res.data.business_flow,
                        });
                    } else if (res.data.error_no == -9999 || res.data.error_no == -8888) {
                        _this.initDialogParam(_this.timeOutTips);
                    } else {
                        _this.$message.error(res.data.error_info)
                    }
                });

            },
            updateFlow(a) {
                var _this = this;
                this.accept_no = a;
                var data = {
                    "accept_no": a,
                    "business_type": sessionStorage.getItem('business_type'),
                    "business_flow": this.$router.history.current.name,
                    "business_button_type": '1',//下一步
                    "accept_status": ""     //"accept_status":_this.accept_status,当需要更改状态时再传值
                };
                _this.axios.post('/flow/updateAccept', data).then(function (res) {
                    _this.$router.push({
                        name: res.data.business_flow,
                    });
                })
            },
            goNext() {
                var data = {
                    "accept_no": this.accept_no,
                    "business_type": sessionStorage.getItem('business_type'),
                    "business_flow": this.$router.history.current.name,
                    "business_button_type": '1',//下一步
                    "accept_status": ""     //"accept_status":_this.accept_status,当需要更改状态时再传值
                };
                var _this = this;
                _this.axios.post('/flow/updateAccept', data).then(function (res) {
                    _this.$router.push({
                        name: res.data.business_flow,
                    });
                })
            },
            LogicInit3() {
                this.navbar.inVideoQueue = 'no';
                BRAC_SetSDKOption(BRAC_SO_CLOUD_APPGUID, sessionStorage.getItem("anychatAppId"));
                BRAC_SetSDKOption(BRAC_SO_NETWORK_IPV6DNS,1);
                var errorcode = BRAC_Connect(sessionStorage.getItem("anychatServerAddr"), sessionStorage.getItem("anychatServerPort"));//连接服务器
                var _this = this;
                //增加对errorcode错误码值判断
                if (errorcode == 2) {
                    // _this.fullscreenLoading = true;
                    _this.loadingText = "视频服务器连接失败，3秒后重新连接，请稍后...";
                    cancelEvent();
                    // _this.nextMethod2();
                    setTimeout(() => {
                        var failNumber = sessionStorage.getItem('failNum');
                        if (!failNumber) {
                            sessionStorage.setItem('failNum', '0');
                        } else {
                            sessionStorage.setItem('failNum', Number(failNumber) + 1 + '');
                        }
                        //重新连接
                        _this.videoLoginReload();
                    }, 3000);
                } else if (errorcode == 0) {
                    console.log('连接服务器视频errorcode=' + errorcode + "anychatServerAddr=" + sessionStorage.getItem("anychatServerAddr"));
                    // GetID("nextStep").style.display = "none";
                    // GetID("video_div").style.display = "none";
                    // //GetID("video_div1").style.display = "none";
                    // //GetID("video_div2").style.display = "none";
                    // GetID("anychat").style.display = "block";
                    // // var _this = this;
                    // this.safePlugin = false;
                    // this.navbar.inVideoQueue = 'yes';
                    // //GetID("prompt_div").style.display = "none"; // 隐藏插件安装提示界面
                    // // GetID("anychatloginDiv").style.display = "block";//anychat登录
                    // sessionStorage.setItem('linkVideoFail', '');
                    // sessionStorage.setItem('failNum', '0');
                    // console.log('连接服务器视频errorcode=' + errorcode + "anychatServerAddr=" + sessionStorage.getItem("anychatServerAddr"));
                    //
                    // _this.fosParamScreenLoading();
                } else {
                    _this.linkVideoFail = true;
                    _this.linkVideoFailText = "视频服务器连接失败，请退出系统重新登录或联系期货公司咨询，联系电话：" + sessionStorage.getItem('broker_hotline') + "。【" + errorcode + "】";
                    sessionStorage.setItem('linkVideoFail', '');
                    sessionStorage.setItem('failNum', '0');
                }
            },
            LogicInit2() {
                setTimeout(() => {
                    // 判断是否支持插件和插件长度（插件检测）
                    if (navigator.plugins && navigator.plugins.length) {
                        window.navigator.plugins.refresh(false);
                    }
                    var _this = this;

                    // 检查是否安装了插件
                    var NEED_ANYCHAT_APILEVEL = "0"; // 定义业务层需要的AnyChat
                    // API Level
                    var errorcode = BRAC_InitSDK(NEED_ANYCHAT_APILEVEL); // 初始化插件（返回成功(0)或插件版本号太低的编号）
                    // AddLog("BRAC_InitSDK(" + NEED_ANYCHAT_APILEVEL + ")=" + errorcode, LOG_TYPE_API);
                    if (errorcode == GV_ERR_SUCCESS) {// 安装成功的情况下
                        anychatIsInit = true;
                        _this.safePlugin = false;
                        // BRAC_SetSDKOption(BRAC_SO_CORESDK_SCREENCAMERACTRL,1);//显示桌面
                        if (mRefreshPluginTimer != -1)// 检查插件是否安装完成定时器
                            clearInterval(mRefreshPluginTimer); // 清除插件安装检测定时器(下面else有定义)
                        var anycahtVersion = BRAC_GetVersion(0);
                        // AnychatscoreAll.BRAC_Connect(AnychatscoreAll.mDefaultServerAddr,AnychatscoreAll.mDefaultServerPort);//连接服务器
                        // console.log('连接服务器视频');
                        // AnychatscoreAll.GetID("nextStep").style.display = "none";
                        // AnychatscoreAll.GetID("video_div").style.display = "none";
                        // AnychatscoreAll.GetID("anychat").style.display = "block";
                        // AnychatscoreAll.GetID("prompt_div").style.display = "none"; // 隐藏插件安装提示界面
                        // // AnychatscoreAll.GetID("LOG_DIV_BODY").style.display = "block";
                        // AnychatscoreAll.GetID("anychatloginDiv").style.display = "block";//anychat登录
                        console.log("AnyChat Plugin Version:" + BRAC_GetVersion(0));
                    } else { // 没有安装插件，或是插件版本太旧，显示插件下载界面
                        // Anyinit.GetID("loginDiv_vue").style.display = "none";
                        // Anyinit.GetID("loginDiv_vue").style.width=0;
                        _this.safePlugin = true;

                        if (errorcode == GV_ERR_PLUGINNOINSTALL) {
                            _this.pluginTips = "为保障您在网上业务办理过程中的信息安全和操作顺畅，请先下载和安装安全控件！";
                        } else if (errorcode == GV_ERR_PLUGINOLDVERSION) {
                            _this.pluginTips = "您的安全插件版本过低，为保障您在网上业务办理过程中的信息安全和操作顺畅，请下载和安装最新版本！";
                        }
                        GetID("anychat").style.display = "block";
                        // GetID("prompt_div").style.display = "block";// 显示插件安装提示界面
                        // if (errorcode == GV_ERR_PLUGINNOINSTALL)// 第2个参数指 插件没有安装(编码)
                        //   GetID("prompt_div_line1").innerHTML = "首次进入需要安装插件，请点击下载按钮进行安装！";
                        // else if (errorcode == GV_ERR_PLUGINOLDVERSION)// 第2个参数指
                        // // 插件版本太低(编码)
                        //   GetID("prompt_div_line1").innerHTML = "检测到当前插件的版本过低，请下载安装最新版本！";

                        if (mRefreshPluginTimer == -1) {// 检查插件是否安装完成定时器
                            mRefreshPluginTimer = setInterval(function () {
                                _this.LogicInit2();
                            }, 500);
                        }
                    }
                }, 500);
            },
            loadDevice() {
                // return;//不检测硬件，直接返回
                setTimeout(() => {
                    var _this = this;
                    //视频
                    if (BRAC_EnumDevices(1).length > 0) {
                        // _this.$refs.hasCamera.className = "iconfont icon-Camera-disable";
                        document.getElementById("hasCamera").className = "iconfont icon-Camera";
                    } else {
                        // _this.$ref.hasCamera.className = "iconfont icon-Camera";
                        document.getElementById("hasCamera").className = "iconfont icon-Camera-disable";
                    }
                    //麦克风
                    if (BRAC_EnumDevices(2).length > 0) {
                        // _this.$ref.hasMicrophone.className = "iconfont icon-Microphone-disable";
                        document.getElementById("hasMicrophone").className = "iconfont icon-Microphone";
                    } else {
                        // _this.$ref.hasMicrophone.className = "iconfont icon-Microphone";
                        document.getElementById("hasMicrophone").className = "iconfont icon-Microphone-disable";
                    }
                    //耳机
                    if (BRAC_EnumDevices(3).length > 0) {
                        // _this.$ref.hasSpeaker.className = "iconfont icon-Headphone-disable";
                        document.getElementById("hasSpeaker").className = "iconfont icon-Headphone";
                    } else {
                        // _this.$ref.hasSpeaker.className = "iconfont icon-Headphone";
                        document.getElementById("hasSpeaker").className = "iconfont icon-Headphone-disable";
                    }
                }, 700);
            },
            videoLoginReload() {
                sessionStorage.setItem('linkVideoFail', 'linkSuccess');
                sessionStorage.setItem('tipsDialog', 'linkSuccess');
                this.reload();
            },
            linkVideoFailLoad() {
                this.linkVideoFail = false;
                sessionStorage.setItem('tipsDialog', 'linkVideoFailLoad');
                this.reload;
            },
            clkNextDate() {
                this.$refs.nextTimeRef.style.display = '';
                this.$refs.nextTimeBtnRef.style.display = '';
                this.$refs.nextTimeRef.scrollIntoView();
                // document.documentElement.scrollTop=380;//不需要加单位
            },
            msgNextDate() {
                if (this.nextDate == '') {
                    this.$message.error('预约日期不能为空！');
                    return;
                }
                if (this.nextTime == '') {
                    this.$message.error('预约日期时段不能为空！');
                    return;
                }
                var ntName = '下午';
                if (this.nextTime == 0) {
                    ntName = '上午';
                }

                this.ndDlgFlag = true;
                this.ndFlag = true;
                this.ndOFlag = false;
                this.ndMsg = '您的预约排队日期为<span style=\"font-weight: bold\">' + this.nextDate + ntName + "</span>，确定么？";
            },
            submitNextDate() {
                var _this = this;
                var ndData = {
                    reservation_time: _this.nextDate,
                    order_time: _this.nextTime,
                    accept_no: _this.accept_no,
                    business_type: _this.type,
                }
                this.axios.post('/video/saveOrderQueue', ndData).then(function (res) {
                    if (res.data.error_no == 0) {
                        _this.ndDlgFlag = true;
                        _this.ndFlag = false;
                        _this.ndOFlag = true;
                        var ntName = '下午';
                        if (_this.nextTime == 0) {
                            ntName = '上午';
                        }
                        _this.ndMsg = '尊敬的客户，您的视频预约已成功，预约时间为<span style=\"font-weight: bold\">' + _this.nextDate + ntName + "</span>，请在此段时间内登录开户云平台进行视频见证。";
                    } else if (res.data.error_no == -9999 || res.data.error_no == -8888) {
                        _this.initDialogParam(_this.timeOutTips);
                    } else {
                        _this.ndDlgFlag = false;
                        _this.ndFlag = false;
                        _this.ndOFlag = false;
                        _this.$message.error(res.data.error_info)
                    }
                }).catch(function (err) {
                    console.log('预约排队出错：' + err);
                    alert('预约排队出错：' + err);
                })
            },
            qryOrderQueue() {
                var _this = this;
                this.axios.post('/video/qryOrderQueue', {business_type: ''}).then(function (res) {
                    if (res.data.error_no == 0) {
                        if (res.data.queue_status == 1) {
                            _this.queueStatusFlag = true;
                        }
                        if (res.data.queue_status == 0) {//有点重复
                            _this.queueStatusFlag = false;
                        } else if (res.data.queueMap != null) {
                            _this.orderVideoFlag = true;
                            var _rtime = res.data.queueMap.reservation_time;
                            var _otime = res.data.queueMap.order_time;
                            _otime = _otime == 0 ? '上午' : '下午'
                            var _btype = res.data.queueMap.business_type;
                            if (_btype != '') {
                                _this.orderVideoBsname = _this.navbar.m_busname.get('bustype' + _btype);
                            }

                            _this.orderVideoInfo = _rtime + _otime;
                            _this.orderVideoAccpetNo = res.data.queueMap.accept_no;
                        }
                    }
                }).catch(function (err) {
                    console.log('查询预约排队失败：' + err);
                });
            },
            cancleOrderQueue(_p) {
                var _this = this;//_this.orderVideoAccpetNo
                this.axios.post('/video/cancleOrderQueue', {accept_no: _this.accept_no}).then(function (res) {
                    _this.orderVideoShow = false;
                    if (_p == 'y') {
                        if (res.data.error_no == 0) {
                            _this.$message.success('取消预约排队成功！');
                            _this.orderVideoFlag = false;
                            _this.orderVideoInfo = '';
                            _this.orderVideoAccpetNo = '';
                            _this.orderVideoBsname = '';
                        } else {
                            _this.$message.error(res.data.error_info);
                        }
                    }
                }).catch(function (err) {
                    console.log('取消预约排队失败：' + err);
                });
            },
            goOut() {
                var _this = this;
                _this.loginOut();
            },
            initDialogParam(ptips) {
                var _this = this;
                _this.popPShow = true;
                _this.outSystem = true;
                _this.outSysName = _this.btnConfirm;
                _this.popPText = ptips;
            },
            pluginDownload() {
                window.open('static/plugin/CFMMCSetup.exe');
            },
            toParamScreenLoading(lText) {
                this.fullscreenLoading = true;
                this.loadingText = lText;
            },
            fosParamScreenLoading() {
                this.fullscreenLoading = false;
            }
        }
    }
</script>

<style lang="less" scoped>

  @import "../../style/css/advanceset.css";
  @import "../../style/css/bootstrap.min.css";
  @import "../../style/css/ErrorPageStyles.css";
  @import "../../style/css/fileTransProgress.css";
  @import "../../style/css/ie8Style.css";
  @import "../../style/css/jquery-ui.css";
  @import "../../style/css/login.css";
  @import "../../style/css/main.css";
  @import "../../style/css/prompt.css";
  @import "../../style/css/VideoCall.css";
  @import "../../style/icon/iconfont.css";
  @import "../../style/icon/iconfont.css";

  .video {
    background: #f6f6f6;
    /*    #anychat{
          background: lightgrey;
          margin-left:200px;
        }*/

    .img_div {
      /*background: #F0F0F0;*/

      img {
        width: 70%;
        margin-top: 20px;
      }
    }

    .videoTxt {
      background: #808080;
      width: 70%;
      height: 50px;
      color: white;
      padding-top: 20px;
    }

    i.el-icon-circle-check.videoNextStep {
      color: orange;
      font-size: 49px;
      float: right;
      margin-top: -10px;
      margin-right: 20px;
      cursor: pointer;
    }

    .videoClass {
      width: 120px;
      height: 40px;
      background: #FF9122;
      color: white;
      border: none;
    }

    .nextBtn {
      display: none;
    }

    .prepareVideo {
      height: 326px;
      width: 70%;
    }

    #video_div {
      margin-left: 18%;
    }

    .button {
      display: block;
      height: 40px;
    }

    .btnGroup {
      margin: 3% 18%;
    }

    .lastStep {
      height: 40px;
    }

    .nextStep {
      height: 40px;
    }

    .el-button:active {
      /* color: #3a8ee6;*/
      border-color: #DCDFE6;
      outline: 0;
    }

    .el-button:focus, .el-button:hover {
      border-color: #DCDFE6;
    }

    .lastStep {
      margin-left: 3%;
    }
  }

  .tipsDialog {
    width: 710px;
    height: 442px;
  }

  .tipsImg {
    margin-left: 80px;
    width: 21%;
    height: 170px;
    //border: 8px solid #EDEDED;
    float: left;
  }

  .tipsFont {
    margin-left: 75px;
    color: #333333;
    font-size: 14px;
    width: 21%;
    display: inline-block;
    text-align: center;
    border: 0px solid red;
    font-weight: bold;
  }

</style>
