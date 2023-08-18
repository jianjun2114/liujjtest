package com.hundsun.cfmmc.cloud.server.service.impl;

import static com.hundsun.cfmmc.cloud.server.local.common.CaUtil.addImplConfig;

import java.io.*;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.axis.utils.StringUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hundsun.cfmmc.cloud.ca.api.CAErrorCode;
import com.hundsun.cfmmc.cloud.ca.api.CAFields;
import com.hundsun.cfmmc.cloud.ca.api.CAService;
import com.hundsun.cfmmc.cloud.common.biz.constants.*;
import com.hundsun.cfmmc.cloud.common.biz.counter.api.CounterService;
import com.hundsun.cfmmc.cloud.common.biz.util.*;
import com.hundsun.cfmmc.cloud.common.config.util.ConfigUtil;
import com.hundsun.cfmmc.cloud.common.core.context.DataContext;
import com.hundsun.cfmmc.cloud.common.core.event.annotation.Met;
import com.hundsun.cfmmc.cloud.common.core.util.*;
import com.hundsun.cfmmc.cloud.server.dao.atom.*;
import com.hundsun.cfmmc.cloud.server.dao.jce.FmJceUserPresence;
import com.hundsun.cfmmc.cloud.server.dao.jce.FmJceUtil;
import com.hundsun.cfmmc.cloud.server.dao.model.*;
import com.hundsun.cfmmc.cloud.server.local.common.RedisUtil;
import com.hundsun.cfmmc.cloud.server.local.service.UserLocalCommonService;
import com.hundsun.cfmmc.cloud.store.api.StoreService;
import com.hundsun.cfmmc.cloud.store.factory.StoreServiceFactory;
import com.hundsun.cfmmc.cloud.user.api.form.*;
import com.hundsun.cfmmc.cloud.user.api.service.IUserDataCommonService;
import com.hundsun.jrescloud.rpc.annotation.CloudComponent;

import lombok.extern.log4j.Log4j2;

/**
 * 功能说明: 用户微服务接口实现<br>
 * 系统版本: v1.0<br>
 * 开发人员: chenht16492 开发时间: 2018/7/16<br>
 */
@CloudComponent
@Log4j2
public class UserDataCommonService implements IUserDataCommonService {

    @Autowired
    private UserProfileAtom userProfileAtom;

    @Autowired
    private ValidateCodeAtom validateCodeAtom;

    @Autowired
    private SuccessUserLogAtom successUserLogAtom;

    @Autowired
    private UserPresenceAtom userPresenceAtom;

    @Autowired
    private PhoneInfoAtom phoneInfoAtom;

    @Autowired
    private RevisitPaperAtom revisitPaperAtom;

    @Autowired
    private UserIdInfoAtom userIdInfoAtom;

    @Autowired
    private RevisitQuestionAtom revisitQuestionAtom;

    @Autowired
    private BaseDictionaryAtom baseDictionaryAtom;

    @Autowired
    private BaseOrganizationAtom baseOrganizationAtom;

    @Autowired
    private RevisitQuestionOptionsAtom revisitQuestionOptionsAtom;

    @Autowired
    private UserThirdDeposityAtom userThirdDeposityAtom;

    @Autowired
    private BaseBankAtom baseBankAtom;

    @Autowired
    private UserRevisitAtom userRevisitAtom;

    @Autowired
    private CounterService counterService;

    @Autowired
    private UserCaVerifyAtom userCaVerifyAtom;

    @Autowired
    private CAService caService;

    @Autowired
    private UserLocalCommonService userLocalCommonService;

    @Autowired
    private BaseAcceptAtom baseAcceptAtom;

    @Autowired
    private UserIdVerifyAtom userIdVerifyAtom;

    @Autowired
    private UserPresenceAdAtom userPresenceAdAtom;

    @Autowired
    private UserAdequacyAtom userAdequacyAtom;

    @Autowired
    private UserAccountsAtom userAccountsAtom;

    @Autowired
    private ExamTestResultAtom examTestResultAtom;

    @Autowired
    private ExamTestAnswersAtom examTestAnswersAtom;

    @Autowired
    private ObsUserAgreementSignAtom obsUserAgreementSignAtom;

    @Autowired
    private ProfileUserAccountsAtom profileUserAccountsAtom;
    @Autowired
    private ProfileUserAdequacyAtom profileUserAdequacyAtom;
    @Autowired
    private ProfileUserInfoAtom profileUserInfoAtom;
    @Autowired
    private ProfileUserQryAtom profileUserQryAtom;
    @Autowired
    private ProfileUserExtendAtom profileUserExtendAtom;
    @Autowired
    private ProfileUserPicDocAtom profileUserPicDocAtom;

    @Autowired
    private UserIdUploadAtom userIdUploadAtom;

    @Autowired
    private UserVideoVerifyAtom userVideoVerifyAtom;

    @Autowired
    private UserMajorinvestordataAtom userMajorinvestordataAtom;
    @Autowired
    private UserFinancePicAtom userFinancePicAtom;
    @Autowired
    private UserInePicAtom userInePicAtom;
    @Autowired
    private MajorTestResultAtom majorTestResultAtom;
    @Autowired
    private MajorTestAnswersAtom majorTestAnswersAtom;
    @Autowired
    private BaseSysUserAtom baseSysUserAtom;
    @Autowired
    private ObsDocAgreementAtom obsDocAgreementAtom;
    @Autowired
    private TTradedateAtom tTradedateAtom;

    @Autowired
    private BaseAcceptInfoAtom baseAcceptInfoAtom;

    @Autowired
    UserAccountsAtom userAccountAtom;

    @Autowired
    UserProfileBakAtom userProfileBakAtom;

    @Autowired
    private UserAcceptLogAtom userAcceptLogAtom;

    @Autowired
    private UserOTpAtom userOTpAtom;

    @Override
    public Map<String, Object> qryOcrInfo(UserInfoForm form) {
        // 初始化返回值
        Map<String, Object> resultMap = ReturnUtil.setErrorResult(new HashMap<String, Object>(), ErrorCode.SUCCESS);
        // 获取输入参数
        String systype = form.getSystype(); // 业务账户类型
        String userId = form.getUserId(); // 用户编号
        // 验证用户编号
        if (StringUtil.isBlank(userId)) {
            resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.NO_SUCH_CLIENT);
        } else {
            // 验证用户
            UserProfile userProfile = userProfileAtom.getUserProfileByUserId(new BigDecimal(userId));
            if (userProfile == null) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.NO_SUCH_CLIENT);
                return resultMap;
            }
            if (systype.equals(Constant.SYSTYPE_SIGN_ONE) || systype.equals(Constant.SYSTYPE_SIGN_TWO)) {
                UserIdInfo userIdInfo = userIdInfoAtom.getUserIdInfoById(userId);
                if (userIdInfo == null) {
                    resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_USER_ID_INFO_ERROR);
                } else {
                    resultMap.put(Fields.ID_KIND, userIdInfo.getIdentityType());
                    resultMap.put(Fields.ID_NO, userIdInfo.getIdentityCode());
                    resultMap.put(Fields.CLIENT_NAME, userIdInfo.getUserName());
                    resultMap.put(Fields.CLIENT_GENDER, userIdInfo.getGender());
                    resultMap.put(Fields.ID_ADDRESS, userIdInfo.getAddress());
                    if (userIdInfo.getDeliveryDate() != null) {
                        resultMap.put(Fields.ID_BEGINDATE,
                            DateTimeUtil.toString(userIdInfo.getDeliveryDate(), DateTimeUtil.FORMAT_YYYYMMDD_NO_BREAK));
                    } else {
                        resultMap.put(Fields.ID_BEGINDATE, "");
                    }
                    if (userIdInfo.getDueDate() != null) {
                        resultMap.put(Fields.ID_ENDDATE,
                            DateTimeUtil.toString(userIdInfo.getDueDate(), DateTimeUtil.FORMAT_YYYYMMDD_NO_BREAK));
                    } else {
                        resultMap.put(Fields.ID_ENDDATE, "");
                    }
                    resultMap.put(Fields.ISSUED_DEPART, userIdInfo.getDeliveryOrg());
                    resultMap.put(Fields.USER_NATION, userIdInfo.getMinzu());
                }
                // }else if(systype.equals(Constant.SYSTYPE_SIGN_TWO)){
                // resultMap = ReturnUtil.setErrorResult(resultMap,ErrorCode.CC_VALIDATE_SYSTYPE_ERROR);
            } else {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_SYSTYPE_ERROR);
            }
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> quickLogon(LogonForm form) {
        // 初始化返回值
        Map<String, Object> resultMap = ReturnUtil.setErrorResult(new HashMap<String, Object>(), ErrorCode.SUCCESS);
        // 获取输入参数
        String businessType = form.getBusinessType();
        String broker_id = form.getBrokerId();
        String id_no = form.getIdNo();
        String id_kind = form.getIdKind();
        String op_station = form.getOpStation();
        String mobile = form.getMobile();
        String validate_code = form.getValidateCode();
        String systype = form.getSystype();
        String channel = form.getChannel();
        String phoneType = form.getPhoneType();
        String phoneVersion = form.getPhoneVersion();
        String appSdkVersion = form.getAppSdkVersion();
        String clientIp = form.getClientIp();

        String broker_whitelist = ConfigUtil.get("broker.whitelist", "8888,9999");
        boolean brokerWhitelist = Arrays.asList(broker_whitelist.split(",")).contains(form.getBrokerId());
        String registerWay = form.getRegisterWay();
        if (registerWay.equals("16") || registerWay.equals("15") || registerWay.equals("11")) {
            if (!brokerWhitelist) {
                return ReturnUtil.setErrorResult(resultMap, ErrorCode.BROKER_THE_LIMITED);// 未在白名单里面
            }
        }

        // ip不能为空，要根据ip来对应视频的双栈
        if (StringUtil.isBlank(clientIp) || clientIp.startsWith("0-")) {
            return ReturnUtil.setErrorResult(resultMap, ErrorCode.VALIDATECODE_NO_IP);
        }

        String orgId = "";

        // 渠道 验证 登录成功后，没有待办会更新渠道信息
        if (StringUtil.isBlank(channel)) {
            channel = "";
        } else {
            if (channel.indexOf("@") != -1 && channel.indexOf("$") != -1
                && channel.indexOf("@") < channel.indexOf("$")) {
                if ("@$".equals(channel)) {
                    channel = "";
                } else {
                    String branchNo = StringUtil.cutString(channel, "@", "\\$");
                    // 营业部代码，可为空
                    if (!StringUtil.isBlank(branchNo)) {
                        BaseOrganization baseOrganization = baseOrganizationAtom.getByOrgCode(broker_id, branchNo);
                        if (baseOrganization != null) {// 非空时校验营业部是否隐藏，隐藏时给提示
                            if (baseOrganization.getStatus().intValue() != 8) {
                                return ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_ORG_HID_CODE_ERROR);
                            }
                            orgId = baseOrganization.getOrgId().toString();
                        }
                    }
                }
            } else {
                channel = "";
            }
        }
        // 业务类型 验证
        String res = ValidateInfoUtil.validateBusinessType(businessType);
        if (!res.equals(TextConstant.SUCCESS)) {
            return ReturnUtil.setErrorResult(resultMap, ErrorCode.BIZ_TYPE_NOTEXIISTS);
        }
        // 检验是否为黑名单用户
        String blackList = RedisUtil.getCache(Fields.BLACKLISTUSER + broker_id);
        if (blackList != null && !blackList.equals("")) {
            String[] bList = blackList.split(",");
            if (Arrays.asList(bList).contains(id_no)) {
                return ReturnUtil.setErrorResult(resultMap, ErrorCode.LIACK_LIS_USED);
            }
        } else {
            String filePath = ConfigUtil.get("StoreServiceFileImpl$store.file.path", "") + File.separator + "blacklist"
                + File.separator + broker_id + ".txt";
            String txtBlackList = readTxtFile(filePath);
            if (txtBlackList != null && !txtBlackList.equals("")) {
                String[] txtBList = txtBlackList.split(",");
                if (Arrays.asList(txtBList).contains(id_no)) {
                    return ReturnUtil.setErrorResult(resultMap, ErrorCode.LIACK_LIS_USED);
                }
            }

        }

        // 校验是否交易日
        Map<String, Object> result = tTradedateAtom.isTrade();
        if (!"0".equals(result.get(Fields.ERROR_NO))) {
            result.put(Fields.ERROR_NO, -1098);
            return result;
        } else {
            result.put(Fields.ERROR_NO, 0);
        }
        if (businessType.equals(Constant.BUSINESS_TYPE_SIGN_ELEVEN)
            || businessType.equals(Constant.BUSINESS_TYPE_SIGN_TWELVE)) {
            LoginForm loginForm = new LoginForm();
            loginForm.setBrokerId(broker_id);
            loginForm.setIdNo(id_no);
            loginForm.setIdKind(id_kind);
            loginForm.setOpStation(op_station);
            loginForm.setLogonCode(mobile);
            loginForm.setValidateCode(validate_code);
            loginForm.setBusinessType(businessType);
            loginForm.setChannel(channel);
            loginForm.setClientIp(clientIp);
            loginForm.setRegisterWay(registerWay);
            loginForm.setMacAddr(form.getMacAddr());
            loginForm.setPhoneType(phoneType);
            loginForm.setPhoneVersion(phoneVersion);
            loginForm.setAppSdkVersion(appSdkVersion);
            loginForm.setOrgId(orgId);
            return quickLogin(loginForm);
        } else {
            SmsForm smsForm = new SmsForm();
            smsForm.setBrokerId(broker_id);
            smsForm.setIdNo(id_no);
            smsForm.setIdKind(id_kind);
            smsForm.setOpStation(op_station);
            smsForm.setMobile(mobile);
            smsForm.setSmsCode(validate_code);
            smsForm.setSystype(systype);
            smsForm.setBusinessType(businessType);
            smsForm.setFundAccount(form.getFundAccount());
            smsForm.setRegisterWay(registerWay);
            smsForm.setChannel(channel);
            smsForm.setClientIp(clientIp);
            smsForm.setPhoneType(phoneType);
            smsForm.setPhoneVersion(phoneVersion);
            smsForm.setAppSdkVersion(appSdkVersion);
            return validateThdSmsCode(smsForm);
        }
    }

    public String readTxtFile(String filePath) {
        File file = new File(filePath);
        // 判断文件存在并且是文件
        Boolean boo = file.exists() && file.isFile();
        if (boo) {
            BufferedReader bufferedReader = null;
            try {
                // 构造一个BufferedReader类来读取文件
                bufferedReader = new BufferedReader(new FileReader(file));
                String linetxt = null;
                // result用来存储文件内容
                StringBuilder result = new StringBuilder();
                // 按使用readLine方法，一次读一行
                while ((linetxt = bufferedReader.readLine()) != null) {
                    result.append(linetxt);
                }
                return result.toString();
            } catch (Exception e) {
                log.info("读取文件内容出错");
                e.printStackTrace();
            } finally {
                try {
                    bufferedReader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } else {
            log.info("黑名单文件不存在");
        }
        return null;
    }

    @Override
    public Map<String, Object> saveUserInfo(UserInfoForm form) {
        // 初始化返回值
        Map<String, Object> resultMap = ReturnUtil.setErrorResult(new HashMap<String, Object>(), ErrorCode.SUCCESS);
        log.info("执行saveUserInfo方法 输入参数：" + JSON.toJSONString(form));
        // 获取输入参数
        String brokerId = form.getBrokerId(); // 期货公司编号
        String userId = form.getUserId(); // 用户编号
        String systype = form.getSystype(); // 业务账户类型
        // String mobile = form.getMobile(); // 注册手机号
        String idKind = form.getIdKind(); // 证件类型
        idKind = IdKind.NCID_CARD.getVal(); // 默认：1
        String idNo = form.getIdNo(); // 身份证号
        String clientName = form.getClientName(); // 客户姓名
        String contactAddrDetail = form.getContactAddrDetail(); // 联系详细地址
        String postCode = form.getPostCode(); // 邮政编码
        String email = form.getEmail(); // 电子邮箱
        String degreeCode = form.getDegreeCode(); // 学历代码
        String professionCode = form.getProfessionCode(); // 职业代码
        // String industyCode = form.getIndustyCode(); // 行业代码
        String branchNo = form.getBranchNo(); // 营业部代码
        String idBegindate = form.getIdBegindate(); // 证件有效开始日期（日期格式：yyyyMMdd）
        String idEnddate = form.getIdEnddate(); // 证件有效截止日期（日期格式：yyyyMMdd）
        Integer clientGender = form.getClientGender(); // 客户性别
        String contactAddrProvince = form.getContactAddrProvince(); // 联系地址省
        String contactAddrCity = form.getContactAddrCity(); // 联系地址市
        String userNation = form.getUserNation(); // 民族
        String contactNumber = form.getContactNumber(); // 联系电话
        String idAddress = form.getIdAddress(); // 身份证地址
        // String issuedDepart = form.getIssuedDepart(); // 签发机关
        String contactMobile = form.getContactMobile(); // 联系手机
        // String taxType = form.getTaxType(); // 税收类型
        // String platformType = form.getPlatformType(); // 政要关系
        // String badRecord = form.getBadRecord(); // 不良记录
        // String termInvestment = form.getTermInvestment(); // 投资期限
        // String investmentVariety = form.getInvestmentVariety(); // 投资品种
        // String proceedsType = form.getProceedsType(); // 收益类型
        // String conRelation = form.getConRelation(); // 控制关系
        // String beneficiary = form.getBeneficiary(); // 受益人
        String isBroker = form.getIsBroker();// 是否存在居间人
        String fbrokerName = form.getFbrokerName();// 居间人姓名
        String brokerCode = form.getBrokerCode();// 居间人编号
        String val1 = form.getVal1(); // 自定义字段1
        String val2 = form.getVal2(); // 自定义字段2
        String val3 = form.getVal3(); // 自定义字段3
        // 获取开关
        // String industry_flag = ConfigUtil.get("user.industry.input", "0"); // 行业显示开关
        String minzu_visible_flag = ConfigUtil.get("user.minzu.input", "0"); // 民族显示开关
        // String tax_type_flag = ConfigUtil.get("user.taxtype.input", "0"); // 税收类型显示开关
        // String platform_type_flag = ConfigUtil.get("user.platformtype.input", "0"); // 政要关系显示开关
        // String bad_record_flag = ConfigUtil.get("user.badrecord.input", "0"); // 不良记录显示开关
        // String term_investment_flag = ConfigUtil.get("user.terminvestment.input", "0"); // 投资期限显示开关
        // String investment_variety_flag = ConfigUtil.get("user.investmentvariety.input", "0"); // 投资品种显示开关
        // String proceeds_type_flag = ConfigUtil.get("user.proceedstype.input", "0"); // 收益类型显示开关
        // String con_relation_flag = ConfigUtil.get("user.conrelation.input", "0"); // 控制关系显示开关
        // String beneficiary_flag = ConfigUtil.get("user.beneficiary.input", "0"); // 受益人显示开关
        String enable_broker = ConfigUtil.get("enable.broker", "0"); // 是否启用居间人
        // 验证用户编号
        if (StringUtil.isBlank(userId)) {
            resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.NO_SUCH_CLIENT);
        } else {
            // 验证用户
            UserProfile userProfile = userProfileAtom.getUserProfileByUserId(new BigDecimal(userId));
            if (userProfile == null) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.NO_SUCH_CLIENT);
                return resultMap;
            }
            // 1.参数检查
            // 注册手机号校验
            // if(mobile == null){
            // mobile = userProfile.getMobile();
            // }
            // String res1 = ValidateInfoUtil.validateMobilePhone(mobile);
            // if (!res1.equals(TextConstant.SUCCESS)) {
            // resultMap = ReturnUtil.setErrorResult(resultMap,ErrorCode.HK_VALIDATE_MOBILE_PHONE_FORMAT_ERROR);
            // return resultMap;
            // }
            // 身份证号校验
            String res2 = ValidateInfoUtil.validateCID(idNo);
            if (!res2.equals(TextConstant.SUCCESS)) {
                // resultMap = ReturnUtil.setErrorResult(resultMap,ErrorCode.HK_VALIDATE_CID_ERROR);
                // return resultMap;
                resultMap.put(Fields.ERROR_NO, -1);
                resultMap.put(Fields.ERROR_INFO, res2);
                return resultMap;
            }
            // 输入身份证 与 登陆记录的身份证件比对
            String identityCode = userProfile.getIdentityCode();
            if (!idNo.equals(identityCode)) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.SAVE_USER_INFO_ERROR);
                return resultMap;
            }
            // 姓名校验
            String res3 = ValidateInfoUtil.validateName(clientName);
            if (!res3.equals(TextConstant.SUCCESS)) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.HK_VALIDATE_NAME_ERROR);
                return resultMap;
            }
            // 邮箱校验
            String res4 = ValidateInfoUtil.validateEmail(email);
            if (!res4.equals(TextConstant.SUCCESS)) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.HK_VALIDATE_EMAIL_ERROR);
                return resultMap;
            }
            // 邮编校验
            String res5 = ValidateInfoUtil.validatePostCode(postCode);
            if (!res5.equals(TextConstant.SUCCESS)) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.HK_VALIDATE_POSTCODE_ERROR);
                return resultMap;
            }
            // 证件有效开始日期（日期格式：yyyyMMdd）
            String res6 = ValidateInfoUtil.validateDateFormat(idBegindate);
            if (!res6.equals(TextConstant.SUCCESS)) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_BEGIN_DATE_ERROR);
                return resultMap;
            }
            // 证件有效截止日期（日期格式：yyyyMMdd）
            String res7 = ValidateInfoUtil.validateDateFormat(idEnddate);
            if (!res7.equals(TextConstant.SUCCESS)) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_END_DATE_ERROR);
                return resultMap;
            }
            // 证件开始、结束日期验证（日期格式：yyyyMMdd）
            String res111 = ValidateInfoUtil.validateCompareDate(idBegindate, idEnddate);
            if (!res111.equals(TextConstant.SUCCESS)) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_BEGINOREND_DATE_ERROR);
                return resultMap;
            }
            // 联系详细地址
            String res8 = ValidateInfoUtil.validateAddress(contactAddrDetail, 3, 100);
            if (!res8.equals(TextConstant.SUCCESS)) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_CONTACT_ADDR_DETAIL_ERROR);
                return resultMap;
            }
            // 联系手机校验
            if (StringUtil.isBlank(contactMobile)) {
                contactMobile = "";
            } else {
                String res9 = ValidateInfoUtil.validateMobilePhone(contactMobile);
                if (!res9.equals(TextConstant.SUCCESS)) {
                    resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_CONTACT_MOBILE_ERROR);
                    return resultMap;
                }
            }
            // 身份证地址
            String res10 = ValidateInfoUtil.validateAddress(idAddress, 3, 100);
            if (!res10.equals(TextConstant.SUCCESS)) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_ID_ADDRESS_ERROR);
                return resultMap;
            }
            // 签发机关
            // String res11 = ValidateInfoUtil.validateAddress(issuedDepart, 3,100);
            // if (!res11.equals(TextConstant.SUCCESS)) {
            // resultMap = ReturnUtil.setErrorResult(resultMap,ErrorCode.CC_VALIDATE_ISSUED_DEPART_ERROR);
            // return resultMap;
            // }
            // 联系电话
            if (StringUtil.isBlank(contactNumber)) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_CONTACT_NUMBER_ERROR);
                return resultMap;
            } else {
                String res12 = "";
                String res13 = "";
                // if(contactNumber.indexOf("-") == -1){
                res12 = ValidateInfoUtil.validateMobilePhone(contactNumber);
                // }else{
                res13 = ValidateInfoUtil.validatePhone(contactNumber);
                // }
                if (!res12.equals(TextConstant.SUCCESS) && !res13.equals(TextConstant.SUCCESS)) {
                    resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_CONTACT_NUMBER_ERROR);
                    return resultMap;
                }
            }

            if (isBroker.equals("Y")) {// 选择居间人
                // 居间人姓名效验
                String res14 = ValidateInfoUtil.validateName(fbrokerName);
                if (!res14.equals(TextConstant.SUCCESS)) {
                    resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.HK_VALIDATE_NAME_ERROR);
                    return resultMap;
                }
                // 居间人编号效验
                String res15 = ValidateInfoUtil.validateBrokerIDcard(brokerCode);
                if (!res15.equals(TextConstant.SUCCESS)) {
                    resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.VALIDATE_BID_ERROR);
                    return resultMap;
                }
                brokerCode = brokerCode.toUpperCase();
                // String first = brokerCode.substring(0, 1);
                // if (first.equals("j")) {
                // String after = brokerCode.substring(1); //substring(1),获取索引位置1后面所有剩余的字符串
                // first = first.toUpperCase();
                // brokerCode = first + after;//将大写转换为小写
                // }
            }

            // 学历
            if (StringUtil.isBlank(degreeCode)) {
                degreeCode = "";
            }
            BaseDictionary baseDictionary1 =
                baseDictionaryAtom.getByDicAndSubEntry(Constant.DICT_ENTRY_DEGREE_CODE, degreeCode);
            if (baseDictionary1 == null) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_DEGREE_CODE_ERROR);
                return resultMap;
            }
            // 职业
            if (StringUtil.isBlank(professionCode)) {
                professionCode = "";
            }
            BaseDictionary baseDictionary2 =
                baseDictionaryAtom.getByDicAndSubEntry(Constant.DICT_ENTRY_PROFESSION_CODE, professionCode);
            if (baseDictionary2 == null) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_PROFESSION_CODE_ERROR);
                return resultMap;
            }
            // 行业代码
            // if(industry_flag.equals("visible")){
            // if(StringUtil.isBlank(industyCode)){
            // industyCode = "";
            // }
            // BaseDictionary baseDictionary3 =
            // baseDictionaryAtom.getByDicAndSubEntry(Constant.DICT_ENTRY_INDUSTRY_CODE,industyCode);
            // if(baseDictionary3 == null){
            // resultMap = ReturnUtil.setErrorResult(resultMap,ErrorCode.CC_VALIDATE_INDUSTY_CODE_ERROR);
            // return resultMap;
            // }
            // }else{
            // industyCode = "";
            // }
            // 客户性别
            if (clientGender == null) {
                clientGender = 9999;
            }
            BaseDictionary baseDictionary4 =
                baseDictionaryAtom.getByDicAndSubEntry(Constant.DICT_ENTRY_GENDER_CODE, clientGender.toString());
            if (baseDictionary4 == null) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_GENDER_CODE_ERROR);
                return resultMap;
            }
            // 联系地址省
            if (StringUtil.isBlank(contactAddrProvince)) {
                contactAddrProvince = "";
            }
            BaseDictionary baseDictionary5 =
                baseDictionaryAtom.getByDicAndSubEntry(Constant.DICT_ENTRY_CONTACT_ADDR_CODE, contactAddrProvince);
            if (baseDictionary5 == null) {
                resultMap =
                    ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_CONTACT_ADDR_PROVINCE_CODE_ERROR);
                return resultMap;
            }
            // 联系地址市
            if (StringUtil.isBlank(contactAddrCity)) {
                contactAddrCity = "";
            }
            BaseDictionary baseDictionary6 =
                baseDictionaryAtom.getByDicAndSubEntry(Constant.DICT_ENTRY_CONTACT_ADDR_CODE, contactAddrCity);
            if (baseDictionary6 == null) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_CONTACT_ADDR_CITY_CODE_ERROR);
                return resultMap;
            }
            // 民族
            if (minzu_visible_flag.equals("visible")) {
                if (StringUtil.isBlank(userNation)) {
                    userNation = "";
                }
                // BaseDictionary baseDictionary7 =
                // baseDictionaryAtom.getByDicAndSubEntry(Constant.DICT_ENTRY_MINZU_CODE, userNation);
                // if(baseDictionary7 == null){
                // resultMap = ReturnUtil.setErrorResult(resultMap,ErrorCode.CC_VALIDATE_MINZU_CODE_ERROR);
                // return resultMap;
                // }
            } else {
                userNation = "";
            }
            // 营业部代码
            if (StringUtil.isBlank(branchNo)) {
                branchNo = "";
            }
            BaseOrganization baseOrganization = baseOrganizationAtom.getByOrgCode(brokerId, branchNo);
            if (baseOrganization == null) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_ORG_CODE_ERROR);
                return resultMap;
            }
            // // 税收类型
            // if(tax_type_flag.equals("visible")) {
            // if (StringUtil.isBlank(taxType)) {
            // resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_TAX_TYPE_ERROR);
            // return resultMap;
            // }
            // }else{
            // taxType = "";
            // }
            // // 政要关系
            // if(platform_type_flag.equals("visible")) {
            // if(StringUtil.isBlank(platformType)){
            // resultMap = ReturnUtil.setErrorResult(resultMap,ErrorCode.CC_VALIDATE_PLATFORM_TYPE_ERROR);
            // return resultMap;
            // }
            // }else{
            // platformType = "";
            // }
            // // 不良记录
            // if(bad_record_flag.equals("visible")) {
            // if(StringUtil.isBlank(badRecord)){
            // resultMap = ReturnUtil.setErrorResult(resultMap,ErrorCode.CC_VALIDATE_BAD_RECORD_ERROR);
            // return resultMap;
            // }
            // }else{
            // badRecord = "";
            // }
            // // 投资期限
            // if(term_investment_flag.equals("visible")) {
            // if(StringUtil.isBlank(termInvestment)){
            // resultMap = ReturnUtil.setErrorResult(resultMap,ErrorCode.CC_VALIDATE_TERM_INVESTMENT_ERROR);
            // return resultMap;
            // }
            // }else{
            // termInvestment = "";
            // }
            // // 投资品种
            // if(investment_variety_flag.equals("visible")) {
            // if(StringUtil.isBlank(investmentVariety)){
            // resultMap = ReturnUtil.setErrorResult(resultMap,ErrorCode.CC_VALIDATE_INVESTMENT_VARIETY_ERROR);
            // return resultMap;
            // }
            // }else{
            // investmentVariety = "";
            // }
            // // 收益类型
            // if(proceeds_type_flag.equals("visible")) {
            // if(StringUtil.isBlank(proceedsType)){
            // resultMap = ReturnUtil.setErrorResult(resultMap,ErrorCode.CC_VALIDATE_PROCEEDS_TYPE_ERROR);
            // return resultMap;
            // }
            // }else{
            // proceedsType = "";
            // }
            // // 控制关系
            // if(con_relation_flag.equals("visible")) {
            // if(StringUtil.isBlank(conRelation)){
            // resultMap = ReturnUtil.setErrorResult(resultMap,ErrorCode.CC_VALIDATE_CON_RELATION_ERROR);
            // return resultMap;
            // }
            // }else{
            // conRelation = "";
            // }
            // // 受益人
            // if(beneficiary_flag.equals("visible")) {
            // if(StringUtil.isBlank(beneficiary)){
            // resultMap = ReturnUtil.setErrorResult(resultMap,ErrorCode.CC_VALIDATE_BENEFICIARY_ERROR);
            // return resultMap;
            // }
            // }else{
            // beneficiary = "";
            // }
            // 业务逻辑代码
            if (systype.equals(Constant.SYSTYPE_SIGN_ONE) || systype.equals(Constant.SYSTYPE_SIGN_TWO)) {
                // 2.新增 或 修改 user_id_info
                UserIdInfo userIdInfo = userIdInfoAtom.getUserIdInfoById(userId);
                if (userIdInfo == null) {
                    userIdInfo = new UserIdInfo();
                    userIdInfo.setUserId(new BigDecimal(userId));
                }
                userIdInfo.setUserName(clientName);
                userIdInfo.setIdentityType(idKind); // 默认证件类型：身份证
                userIdInfo.setIdentityCode(idNo);
                userIdInfo.setNation(DicConstant.DEFUALT_NATION); // 默认国籍：中国
                userIdInfo.setAddress(idAddress);
                userIdInfo
                    .setDeliveryDate(DateTimeUtil.toDateOrNull(idBegindate, DateTimeUtil.FORMAT_YYYYMMDD_NO_BREAK));
                userIdInfo.setDueDate(DateTimeUtil.toDateOrNull(idEnddate, DateTimeUtil.FORMAT_YYYYMMDD_NO_BREAK));
                // userIdInfo.setDeliveryOrg(issuedDepart);
                userIdInfo.setGender(clientGender.toString());
                String birthday = "";
                if (idNo.length() >= 18) {
                    birthday = idNo.substring(6, 14);
                } else {
                    birthday = "19" + idNo.substring(6, 12);
                }
                userIdInfo.setAge(new BigDecimal(birthday));
                if (idEnddate.equals("20991231")) {
                    userIdInfo.setForeverFlag(new BigDecimal("1"));
                } else {
                    userIdInfo.setForeverFlag(new BigDecimal("0"));
                }
                if (userNation != "") {
                    userIdInfo.setMinzu(userNation);
                }
                // 3.新增 或 修改 user_presence
                UserPresence userPresence = userPresenceAtom.getByUserId(brokerId, userId);
                if (userPresence == null) {
                    userPresence = new UserPresence();
                    userPresence.setUserId(new BigDecimal(userId));
                    userPresence.setCreateTime(new Date());
                    userPresence.setStatus(new BigDecimal(StatusConstant.ACCOUNT_REGISTER));
                    userPresence.setUserStatus(new BigDecimal(StatusConstant.USER_STATUS_REGISTER));
                    userPresence.setOpenType("9");
                    String flowStatus = "000000000010";
                    userPresence.setUserFlowStatus(flowStatus);
                    userPresence.setAdequacyType("0");
                }
                userPresence.setBrokerId(brokerId);
                userPresence.setUserName(clientName);
                userPresence.setIdentityType(idKind); // 默认证件类型：身份证
                userPresence.setIdentityCode(idNo);
                // userPresence.setMobile(mobile);
                userPresence.setOpenOrgId(new BigDecimal(baseOrganization.getOrgId()));
                userPresence.setProvince(contactAddrProvince);
                userPresence.setCity(contactAddrCity);
                userPresence.setFullAddress(
                    baseDictionary5.getDictPrompt() + baseDictionary6.getDictPrompt() + contactAddrDetail);
                userPresence.setAddress(contactAddrDetail);
                userPresence.setTelphone(contactNumber);
                userPresence.setZipCode(postCode);
                userPresence.setDegreeCode(degreeCode);
                userPresence.setProfessionCode(professionCode);
                userPresence.setLastUpdateTime(new Date());
                userPresence.setLastUpdateBy(new BigDecimal(userId));
                // userPresence.setIndustryCode(industyCode);
                userPresence.setMailAddress(email);
                userPresence.setContactMobile(contactMobile);
                // userPresence.setTaxType(taxType);
                // userPresence.setPlatformType(platformType);
                // userPresence.setBadRecord(badRecord);
                // userPresence.setTermInvestment(termInvestment);
                // userPresence.setInvestmentVariety(investmentVariety);
                // userPresence.setProceedsType(proceedsType);
                // userPresence.setConRelation(conRelation);
                // userPresence.setBeneficiary(beneficiary);

                // 不控制居间人修改问题，由前台控制
                /* if (!form.getAcceptStatus().equals("1")) {
                    if (userPresence.getBrokerCode().equals(brokerCode) && userPresence.getBrokerName().equals(fbrokerName)) {
                        //未修改不进行操作
                    } else {
                        return ReturnUtil.setErrorResult(resultMap, ErrorCode.NO_SAVE_UPDATE_FBROKER);
                    }
                }*/

                if (enable_broker.equals("visible")) {
                    if (isBroker.equals("Y")) {
                        userPresence.setIsBroker("1");// 是否存在居间人
                        userPresence.setBrokerName(fbrokerName);// 居间人姓名
                        userPresence.setBrokerCode(brokerCode);// 居间人编号
                    } else {
                        userPresence.setIsBroker("0");// 是否存在居间人
                    }
                } else {// 清空居间人相关字段
                    userPresence.setIsBroker("");// 是否存在居间人
                    userPresence.setBrokerName("");// 居间人姓名
                    userPresence.setBrokerCode("");// 居间人编号
                }
                userPresence.setVal1(val1);
                userPresence.setVal2(val2);
                userPresence.setVal3(val3);
                // 4.更新 user_profile
                // userProfile.setMobile(mobile);
                // userProfile.setIdentityType(idKind);
                // userProfile.setIdentityCode(idNo);
                // 5.存储数据
                try {
                    // user_id_info
                    if (null == userIdInfo.getId()) {
                        userIdInfoAtom.save(userIdInfo);
                    } else {
                        userIdInfoAtom.update(userIdInfo);
                    }
                    // user_presence
                    if (null == userPresence.getId()) {
                        userPresenceAtom.save(userPresence);
                    } else {
                        userPresenceAtom.update(userPresence);
                    }
                    log.info("执行userPresenceAtom方法成功 身份证号：" + userPresence.getIdentityCode());
                    // user_profile
                    // userProfileAtom.update(userProfile);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    return ReturnUtil.setErrorResult(resultMap, ErrorCode.COMMON_ERROR);
                }
                // }else if(systype.equals(Constant.SYSTYPE_SIGN_TWO)){
                // resultMap = ReturnUtil.setErrorResult(resultMap,ErrorCode.CC_VALIDATE_SYSTYPE_ERROR);
            } else {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_SYSTYPE_ERROR);
            }
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> showUserInfo(UserInfoForm form) {
        // 初始化返回值
        Map<String, Object> resultMap = ReturnUtil.setErrorResult(new HashMap<String, Object>(), ErrorCode.SUCCESS);
        // 获取输入参数
        String brokerId = form.getBrokerId(); // 期货公司编号
        String userId = form.getUserId(); // 用户编号
        String systype = form.getSystype(); // 业务账户类型
        String businessType = form.getBusinessType();
        String netAndIp = "";
        try {
            // 验证用户编号
            if (StringUtil.isBlank(userId)) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.NO_SUCH_CLIENT);
            } else {
                // 1.查询 user_profile
                UserProfile userProfile = userProfileAtom.getUserProfileByUserId(new BigDecimal(userId));
                if (userProfile == null) {
                    resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.NO_SUCH_CLIENT);
                    return resultMap;
                }
                netAndIp = userProfile.getLastIp();
                if (systype.equals(Constant.SYSTYPE_SIGN_ONE) || systype.equals(Constant.SYSTYPE_SIGN_TWO)) {
                    resultMap.put(Fields.NCIIS_FLAG, "0");
                    // 查询 user_id_verify 验证 是否做公安认证
                    UserIdVerify userIdVerify = userIdVerifyAtom.getUserIdVerifyByUserId(userId);
                    if (userIdVerify != null) {
                        String uiv = (userIdVerify.getStatus() != null) ? userIdVerify.getStatus().toString() : "";
                        if (uiv.equals("8")) {
                            resultMap.put(Fields.NCIIS_FLAG, "1");
                        }
                    }
                    // 2.查询 user_presence
                    UserPresence userPresence = userPresenceAtom.getByUserId(brokerId, userId);
                    // 3.查询 user_id_info
                    UserIdInfo userIdInfo = userIdInfoAtom.getUserIdInfoById(userId);
                    // 4.返回值处理
                    if (userPresence == null) {
                        resultMap.put(Fields.CLIENT_ID, "");
                        resultMap.put(Fields.MOBILE, "");
                        resultMap.put(Fields.ID_KIND, "");
                        resultMap.put(Fields.ID_NO, "");
                        resultMap.put(Fields.CLIENT_NAME, "");

                        resultMap.put(Fields.CONTACT_ADDR_PROVINCE, "");
                        resultMap.put(Fields.CONTACT_ADDR_CITY, "");
                        resultMap.put(Fields.CONTACT_ADDR_DETAIL, "");
                        resultMap.put(Fields.CONTACT_NUMBER, "");
                        resultMap.put(Fields.POST_CODE, "");
                        resultMap.put(Fields.EMAIL, "");
                        resultMap.put(Fields.DEGREE_CODE, "");
                        resultMap.put(Fields.PROFESSION_CODE, "");
                        resultMap.put(Fields.INDUSTY_CODE, "");
                        resultMap.put(Fields.BRANCH_NO, "");
                        resultMap.put(Fields.ORG_ID, "");
                        resultMap.put(Fields.ORG_NAME, "");

                        resultMap.put(Fields.CLIENT_GENDER, "");
                        resultMap.put(Fields.ID_ADDRESS, "");
                        resultMap.put(Fields.ID_BEGINDATE, "");
                        resultMap.put(Fields.ID_ENDDATE, "");
                        resultMap.put(Fields.ISSUED_DEPART, "");
                        resultMap.put(Fields.USER_NATION, "");

                        resultMap.put(Fields.CONTACT_MOBILE, "");
                        resultMap.put(Fields.TAX_TYPE, "");
                        resultMap.put(Fields.PLATFORM_TYPE, "");
                        resultMap.put(Fields.BAD_RECORD, "");
                        resultMap.put(Fields.TERM_INVESTMENT, "");
                        resultMap.put(Fields.INVESTMENT_VARIETY, "");
                        resultMap.put(Fields.PROCEEDS_TYPE, "");
                        resultMap.put(Fields.CON_RELATION, "");
                        resultMap.put(Fields.BENEFICIARY, "");
                        resultMap.put(Fields.DEAL_TYPE, "");
                        resultMap.put(Fields.VAL1, "");
                        resultMap.put(Fields.VAL2, "");
                        resultMap.put(Fields.VAL3, "");
                        resultMap.put(Fields.IS_BROKER, "");
                        resultMap.put(Fields.FBROKER_NAME, "");// 居间人姓名
                        resultMap.put(Fields.BROKER_CODE, "");// 居间人编码

                    } else {
                        resultMap.put(Fields.CLIENT_ID, (userPresence.getId() != null) ? userPresence.getId() : "");
                        resultMap.put(Fields.MOBILE,
                            (userPresence.getMobile() != null) ? userPresence.getMobile() : "");
                        resultMap.put(Fields.ID_KIND,
                            (userPresence.getIdentityType() != null) ? userPresence.getIdentityType() : "");
                        resultMap.put(Fields.ID_NO,
                            (userPresence.getIdentityCode() != null) ? userPresence.getIdentityCode() : "");
                        resultMap.put(Fields.CLIENT_NAME,
                            (userPresence.getUserName() != null) ? userPresence.getUserName() : "");

                        resultMap.put(Fields.CONTACT_ADDR_PROVINCE,
                            (userPresence.getProvince() != null) ? userPresence.getProvince() : "");
                        resultMap.put(Fields.CONTACT_ADDR_CITY,
                            (userPresence.getCity() != null) ? userPresence.getCity() : "");
                        resultMap.put(Fields.CONTACT_ADDR_DETAIL,
                            (userPresence.getAddress() != null) ? userPresence.getAddress() : "");
                        resultMap.put(Fields.CONTACT_NUMBER,
                            (userPresence.getTelphone() != null) ? userPresence.getTelphone() : "");
                        resultMap.put(Fields.POST_CODE,
                            (userPresence.getZipCode() != null) ? userPresence.getZipCode() : "");
                        resultMap.put(Fields.EMAIL,
                            (userPresence.getMailAddress() != null) ? userPresence.getMailAddress() : "");
                        resultMap.put(Fields.DEGREE_CODE,
                            (userPresence.getDegreeCode() != null) ? userPresence.getDegreeCode() : "");
                        resultMap.put(Fields.PROFESSION_CODE,
                            (userPresence.getProfessionCode() != null) ? userPresence.getProfessionCode() : "");
                        resultMap.put(Fields.INDUSTY_CODE,
                            (userPresence.getIndustryCode() != null) ? userPresence.getIndustryCode() : "");
                        resultMap.put(Fields.BRANCH_NO, "");
                        // 获取 渠道
                        String channel = StringUtil.isEmpty(userPresence.getChannel()) ? "" : userPresence.getChannel();
                        if (!"".equals(channel)) {
                            String branch_no = StringUtil.cutString(channel, "@", "\\$");
                            if (StringUtil.isBlank(branch_no)) {
                                resultMap.put(Fields.CHANNEL, "0");
                            } else {
                                resultMap.put(Fields.BRANCH_NO, branch_no);
                                resultMap.put(Fields.CHANNEL, "1");
                            }
                        } else {
                            resultMap.put(Fields.CHANNEL, "0");
                        }
                        String openOrgId =
                            userPresence.getOpenOrgId() != null ? userPresence.getOpenOrgId().toString() : "";
                        if (!"".equals(openOrgId)) {
                            BaseOrganization bb = baseOrganizationAtom.getByOrgId(brokerId, openOrgId);
                            if (bb != null) {
                                resultMap.put(Fields.BRANCH_NO, bb.getOrgCode());
                                resultMap.put(Fields.ORG_ID, bb.getOrgId());
                                resultMap.put(Fields.ORG_NAME, bb.getName());
                            }
                        }
                        resultMap.put(Fields.CONTACT_MOBILE,
                            (userPresence.getContactMobile() != null) ? userPresence.getContactMobile() : "");
                        resultMap.put(Fields.TAX_TYPE,
                            (userPresence.getTaxType() != null) ? userPresence.getTaxType() : "");
                        resultMap.put(Fields.PLATFORM_TYPE,
                            (userPresence.getPlatformType() != null) ? userPresence.getPlatformType() : "");
                        resultMap.put(Fields.BAD_RECORD,
                            (userPresence.getBadRecord() != null) ? userPresence.getBadRecord() : "");
                        resultMap.put(Fields.TERM_INVESTMENT,
                            (userPresence.getTermInvestment() != null) ? userPresence.getTermInvestment() : "");
                        resultMap.put(Fields.INVESTMENT_VARIETY,
                            (userPresence.getInvestmentVariety() != null) ? userPresence.getInvestmentVariety() : "");
                        resultMap.put(Fields.PROCEEDS_TYPE,
                            (userPresence.getProceedsType() != null) ? userPresence.getProceedsType() : "");
                        resultMap.put(Fields.CON_RELATION,
                            (userPresence.getConRelation() != null) ? userPresence.getConRelation() : "");
                        resultMap.put(Fields.BENEFICIARY,
                            (userPresence.getBeneficiary() != null) ? userPresence.getBeneficiary() : "");
                        resultMap.put(Fields.DEAL_TYPE,
                            (userPresence.getDealType() != null) ? userPresence.getDealType() : "");
                        // 添加居间人信息获取 默认当居间人姓名及编号为空时，为未选择居间人相关信息
                        // 添加居间人开关
                        String enable_broker = ConfigUtil.get("enable.broker", "0"); // 是否启用居间人,只有开立
                        resultMap.put("enable_broker",
                            "visible".equals(enable_broker) && "11".equals(businessType) ? "visible" : "invisible");
                        String isBroker = "";
                        if (userPresence.getIsBroker() != null && "11".equals(businessType)) {
                            isBroker = (userPresence.getIsBroker().equals("1")) ? "Y" : "N";
                        }
                        // Y就是有，N或者null就是没有,数据库是1 =Y，0.
                        resultMap.put(Fields.IS_BROKER, isBroker);
                        if (isBroker.equals("Y")) {
                            resultMap.put(Fields.FBROKER_NAME,
                                (userPresence.getBrokerName() != null) ? userPresence.getBrokerName() : "");
                            resultMap.put(Fields.BROKER_CODE,
                                (userPresence.getBrokerCode() != null) ? userPresence.getBrokerCode() : "");
                        }

                        resultMap.put(Fields.VAL1, (userPresence.getVal1() != null) ? userPresence.getVal1() : "");
                        resultMap.put(Fields.VAL2, (userPresence.getVal2() != null) ? userPresence.getVal2() : "");
                        resultMap.put(Fields.VAL3, (userPresence.getVal3() != null) ? userPresence.getVal3() : "");
                        resultMap.put(Fields.USERPRESENCE_ID, userPresence.getId().toString());
                        resultMap.put(Fields.CLIENT_GENDER, "");
                        resultMap.put(Fields.ID_ADDRESS, "");
                        resultMap.put(Fields.ID_BEGINDATE, "");
                        resultMap.put(Fields.ID_ENDDATE, "");
                        resultMap.put(Fields.ISSUED_DEPART, "");
                        resultMap.put(Fields.USER_NATION, "");

                        if (!StringUtil.isBlank(userPresence.getIdentityCode()) && userIdInfo != null) {
                            resultMap.put(Fields.CLIENT_GENDER, userIdInfo.getGender());
                            resultMap.put(Fields.ID_ADDRESS, userIdInfo.getAddress());
                            if (userIdInfo.getDeliveryDate() != null) {
                                resultMap.put(Fields.ID_BEGINDATE, DateTimeUtil.toString(userIdInfo.getDeliveryDate(),
                                    DateTimeUtil.FORMAT_YYYYMMDD_NO_BREAK));
                            }
                            if (userIdInfo.getDueDate() != null) {
                                resultMap.put(Fields.ID_ENDDATE, DateTimeUtil.toString(userIdInfo.getDueDate(),
                                    DateTimeUtil.FORMAT_YYYYMMDD_NO_BREAK));
                            }
                            resultMap.put(Fields.ISSUED_DEPART, userIdInfo.getDeliveryOrg());
                            resultMap.put(Fields.USER_NATION, userIdInfo.getMinzu());
                        }
                    }
                    // }else if(systype.equals(Constant.SYSTYPE_SIGN_TWO)){
                    // resultMap = ReturnUtil.setErrorResult(resultMap,ErrorCode.CC_VALIDATE_SYSTYPE_ERROR);
                } else {
                    resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_SYSTYPE_ERROR);
                }
            }
        } catch (Exception e) {
            // resultMap.put(Fields.ERROR_NO,-1);
            // resultMap.put(Fields.ERROR_INFO,e.getMessage());
            log.error(e.getMessage(), e);
            resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.COMMON_ERROR);
        }
        String appId = ConfigUtil.get("broker.app.id", "78B6D4F6-F07E-DC4E-BDB0-1F8908CDA57E");
        String AnychatAddr = ConfigUtil.get("anychatQueAddr", "");
        if (!StringUtil.isBlank(netAndIp) && netAndIp.startsWith("6-")) { // V6服务器网络
            AnychatAddr = ConfigUtil.get("anychatQueAddrV6", "");
        }
        String anychatSignAddr = ConfigUtil.get("anychatSignAddr", "");
        // 内网ip泄露加密
        anychatSignAddr = DigestUtil.base64DESEnery(anychatSignAddr, "cfmmc123");
        // AnychatAddr = DigestUtil.base64DESEnery(AnychatAddr,"cfmmc1");

        // 恒生排队开关
        String hundsunVideoFlag = ConfigUtil.get("hundsunVideoFlag", "invisible");
        // 恒生队列服务器地址
        String hundsunVideoUrl = ConfigUtil.get("hundsunVideoUrl", "");

        resultMap.put(Fields.CLIENT_IP, netAndIp);
        resultMap.put(Fields.APP_ID, appId);
        resultMap.put(Fields.ANYCHATQUEADDR, AnychatAddr);
        resultMap.put(Fields.ANYCHATSIGNADDR, anychatSignAddr);

        resultMap.put(Fields.HUNDSUN_VIDEO_FLAG, hundsunVideoFlag);
        resultMap.put(Fields.HUNDSUN_VIDEO_URL, hundsunVideoUrl);
        return resultMap;
    }

    @Override
    public Map<String, Object> saveBankInfo(BankInfoForm form) {
        // 初始化返回值
        Map<String, Object> resultMap = ReturnUtil.setErrorResult(new HashMap<String, Object>(), ErrorCode.SUCCESS);
        // 获取输入参数
        String brokerId = form.getBrokerId(); // 期货公司编号
        String userId = form.getUserId(); // 用户编号
        String systype = form.getSystype(); // 业务账户类型
        String bankAccountData = form.getBankAccountData(); // 银期账户对象
        // 验证用户编号
        if (StringUtil.isBlank(userId)) {
            resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.NO_SUCH_CLIENT);
        } else {
            // 1.查询 user_profile
            UserProfile userProfile = userProfileAtom.getUserProfileByUserId(new BigDecimal(userId));
            if (userProfile == null) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.NO_SUCH_CLIENT);
                return resultMap;
            }
            if (systype.equals(Constant.SYSTYPE_SIGN_ONE)) {
                //
                Map<String, Map<String, Object>> utm = new HashMap();
                // 查询银期账户信息
                List<UserThirdDeposity> userThirdDepositys =
                    userThirdDeposityAtom.getUserThirdDeposityByUserId(Long.parseLong(userId));
                if (userThirdDepositys != null && !userThirdDepositys.isEmpty()) {
                    for (int e = 0; e < userThirdDepositys.size(); e++) {
                        UserThirdDeposity userThirdDeposity = userThirdDepositys.get(e);
                        String bankSign = BizSecurity.desEncrypt(userThirdDeposity.getDeposityId().toString(),
                            BizSecurity.key1, BizSecurity.key2, BizSecurity.key3);
                        Map<String, Object> map = new HashMap();
                        if (userThirdDeposity.getBankId() != null) {
                            BaseBank bank = baseBankAtom.queryBankByKey(userThirdDeposity.getBankId());
                            map.put(Fields.BANK_ID, bank.getBankNo());
                        } else {
                            map.put(Fields.BANK_ID, "");
                        }
                        map.put(Fields.BANK_NO, userThirdDeposity.getAccountNo());
                        map.put(Fields.BANK_BRANCH, userThirdDeposity.getBankBranch());
                        map.put(Fields.BANK_SIGN, bankSign);
                        utm.put(bankSign, map);
                    }
                }
                // 解析 银期账户对象
                if (StringUtil.isBlank(bankAccountData)) {
                    return ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_BANK_ACCOUNT_DATA_NULL_ERROR);
                }
                JSONArray dataArray = null;
                try {
                    dataArray = JSONObject.parseArray(bankAccountData);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    return ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_BANK_ACCOUNT_DATA_ERROR);
                }
                UserPresence userPresence = userPresenceAtom.getByUserId(brokerId, userId);
                if (userPresence == null) {
                    return ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_USER_PRESENCE_ERROR);
                }
                try {
                    if (dataArray != null && !dataArray.isEmpty()) {
                        Map<String, BaseBank> map = new HashMap();
                        List li = new ArrayList<>();
                        for (int j = 0; j < dataArray.size(); j++) {
                            JSONObject o = dataArray.getJSONObject(j);
                            if (StringUtil.isBlank((String)o.get(Fields.BANK_ID))
                                || StringUtil.isBlank((String)o.get(Fields.BANK_NO))
                                || StringUtil.isBlank((String)o.get(Fields.BANK_BRANCH))
                                || StringUtil.isBlank((String)o.get(Fields.BANK_SIGN))) {
                                resultMap =
                                    ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_BANK_ACCOUNT_DATA_ERROR);
                                String bank_id = o.get(Fields.BANK_ID) == null ? "" : o.get(Fields.BANK_ID).toString();
                                String bank_no = o.get(Fields.BANK_NO) == null ? "" : o.get(Fields.BANK_NO).toString();
                                String bank_branch =
                                    o.get(Fields.BANK_BRANCH) == null ? "" : o.get(Fields.BANK_BRANCH).toString();
                                resultMap.put(Fields.ERROR_INFO, resultMap.get(Fields.ERROR_INFO) + "[" + bank_id + "]"
                                    + "[" + bank_no + "]" + "[" + bank_branch + "]");
                                return resultMap;
                            }
                            utm.put((String)o.get(Fields.BANK_SIGN), o);
                            li.add(o.get(Fields.BANK_NO).toString());
                        }
                        // 判断是否有相同银行卡号
                        Integer priCount = li.size(); // 集合的长度
                        Long nextCount = li.stream().distinct().count(); // 经过处理之后的集合长度
                        if (priCount != nextCount.intValue()) {// 606 不允许上传保存同一银行卡号业务修改
                            return ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_BANK_CARD_ERROR);
                        }

                        for (Map.Entry<String, Map<String, Object>> entry : utm.entrySet()) {
                            Map<String, Object> bMap = entry.getValue();
                            String bankId = bMap.get(Fields.BANK_ID).toString();
                            BaseBank bank = baseBankAtom.queryBankByBankNo(bankId);
                            if (bank == null) {
                                return ReturnUtil.setErrorResult(resultMap, ErrorCode.NO_SUCH_BANK_CODE);
                            }
                            if (map.get(bankId) == null) {
                                map.put(bankId, bank);
                            }

                        }
                        for (int i = 0; i < dataArray.size(); i++) {
                            JSONObject j = dataArray.getJSONObject(i);
                            String bankId = j.get(Fields.BANK_ID).toString();
                            String bankNo = j.get(Fields.BANK_NO).toString();
                            String bankBranch = j.get(Fields.BANK_BRANCH).toString();
                            String bankSign = j.get(Fields.BANK_SIGN).toString();
                            // BaseBank bank = baseBankAtom.queryBankByBankNo(bankId);
                            // if (bank == null) {
                            // return ReturnUtil.setErrorResult(resultMap, ErrorCode.NO_SUCH_BANK_CODE);
                            // }
                            BaseBank bank = map.get(bankId);
                            // 解密 银行标记
                            String bank_sign =
                                BizSecurity.desDecrypt(bankSign, BizSecurity.key1, BizSecurity.key2, BizSecurity.key3);
                            UserThirdDeposity deposity =
                                userThirdDeposityAtom.getUserThirdDeposityByUserIdAndDeposityId(Long.parseLong(userId),
                                    Long.parseLong(bank_sign));
                            if (deposity == null) {
                                return ReturnUtil.setErrorResult(resultMap,
                                    ErrorCode.CC_VALIDATE_USER_THIRD_DEPOSITY_NO_UPLOAD_ERROR);
                            } else {
                                String bankPic = deposity.getBankPic();
                                if (StringUtil.isBlank(bankPic)) {
                                    resultMap = ReturnUtil.setErrorResult(resultMap,
                                        ErrorCode.CC_VALIDATE_USER_THIRD_DEPOSITY_NO_UPLOAD_ERROR);
                                    resultMap.put(Fields.ERROR_INFO, "缺少银行卡照片，请点击左侧“无银行卡照”补传对应的银行卡照片");
                                    return resultMap;
                                }
                                deposity.setBankId(bank.getBankId());
                                deposity.setAccountName(userPresence.getUserName());
                                deposity.setAccountNo(bankNo);
                                deposity.setEndTime(new Date());
                                deposity.setValidateFlag(new BigDecimal(StatusConstant.DEPOSITY_REVIEW_APPLY));
                                deposity.setApplySuccessFlag(new BigDecimal(StatusConstant.USER_ACC_SUCCESSFLAG_C)); // 创建银行
                                deposity.setAgreementId(bank.getDepositoryAgreementId());
                                deposity.setBankBranch(bankBranch);
                                userThirdDeposityAtom.updateUserThirdDeposity(deposity);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    return ReturnUtil.setErrorResult(resultMap, ErrorCode.COMMON_ERROR);
                }
            } else if (systype.equals(Constant.SYSTYPE_SIGN_TWO)) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_SYSTYPE_ERROR);
            } else {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_SYSTYPE_ERROR);
            }
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> delBankInfo(BankInfoForm form) {
        // 初始化返回值
        Map<String, Object> resultMap = ReturnUtil.setErrorResult(new HashMap<String, Object>(), ErrorCode.SUCCESS);
        // 获取输入参数
        String brokerId = form.getBrokerId(); // 期货公司编号
        String userId = form.getUserId(); // 用户编号
        String systype = form.getSystype(); // 业务账户类型
        // String bankId = form.getBankId(); // 银行编号
        // String bankNo = form.getBankNo(); // 银行卡卡号
        String bankSign = form.getBankSign(); // 银行标识
        if (StringUtil.isBlank(bankSign)) {
            return ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_BANK_SIGN_ERROR);
        }
        if (systype.equals(Constant.SYSTYPE_SIGN_ONE)) {
            // BaseBank bank = baseBankAtom.queryBankByBankNo(bankId);
            // if (bank == null) {
            // return ReturnUtil.setErrorResult(resultMap, ErrorCode.NO_SUCH_BANK_CODE);
            // }
            // 解密 银行标记
            String bank_sign = BizSecurity.desDecrypt(bankSign, BizSecurity.key1, BizSecurity.key2, BizSecurity.key3);
            UserThirdDeposity deposity = userThirdDeposityAtom
                .getUserThirdDeposityByUserIdAndDeposityId(Long.parseLong(userId), Long.parseLong(bank_sign));
            if (deposity != null) {
                String trxNo = deposity.getTrxNo() == null ? "0" : deposity.getTrxNo();
                if (trxNo.equals("3") || trxNo.equals("4")) {
                    return ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_USER_THIRD_DEPOSITY_THREE_ERROR);
                }
                // 获取图片名称
                String bankPic = deposity.getBankPic();
                // 获取图片路径
                String path = brokerId + File.separator + bankPic;
                // 删除数据
                try {
                    userThirdDeposityAtom.deleteUserThirdDeposity(Long.parseLong(userId), Long.parseLong(bank_sign));
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    return ReturnUtil.setErrorResult(resultMap, ErrorCode.COMMON_ERROR);
                }
                // 删除图片文件
                try {
                    StoreService store = StoreServiceFactory.getInstance().getStoreService();
                    store.deleteObject(path);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    return VOUtil.genErrorResult(ErrorCode.FILE_DELETE_ERROR);

                }
            } else {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_USER_THIRD_DEPOSITY_ERROR);
            }
        } else if (systype.equals(Constant.SYSTYPE_SIGN_TWO)) {
            resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_SYSTYPE_ERROR);
        } else {
            resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_SYSTYPE_ERROR);
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> showBankInfo(BankInfoForm form) {
        // 初始化返回值
        Map<String, Object> resultMap = ReturnUtil.setErrorResult(new HashMap<String, Object>(), ErrorCode.SUCCESS);
        // 获取输入参数
        String userId = form.getUserId(); // 用户编号
        String systype = form.getSystype(); // 业务账户类型
        // 定义返回银行信息列表
        List<Map<String, Object>> bankInfos = new ArrayList<>();
        if (systype.equals(Constant.SYSTYPE_SIGN_ONE)) {
            // 查询银期账户信息
            List<UserThirdDeposity> userThirdDepositys =
                userThirdDeposityAtom.getUserThirdDeposityByUserId(Long.parseLong(userId));
            if (userThirdDepositys != null && !userThirdDepositys.isEmpty()) {
                for (int i = 0; i < userThirdDepositys.size(); i++) {
                    UserThirdDeposity userThirdDeposity = userThirdDepositys.get(i);
                    // String bankPic = userThirdDeposity.getBankPic();
                    // String bp = "";
                    // if(!StringUtil.isBlank(bankPic)){
                    // bp = bankPic.split("_")[2];
                    // }
                    String bankSign = BizSecurity.desEncrypt(userThirdDeposity.getDeposityId().toString(),
                        BizSecurity.key1, BizSecurity.key2, BizSecurity.key3);
                    Map<String, Object> map = new HashMap();
                    if (userThirdDeposity.getBankId() != null) {
                        BaseBank bank = baseBankAtom.queryBankByKey(userThirdDeposity.getBankId());
                        map.put(Fields.BANK_ID, bank.getBankNo());
                        map.put(Fields.BANK_NAME, bank.getBankName());
                    } else {
                        map.put(Fields.BANK_ID, "");
                        map.put(Fields.BANK_NAME, "");
                    }
                    map.put(Fields.BANK_NO,
                        userThirdDeposity.getAccountNo() == null ? "" : userThirdDeposity.getAccountNo());
                    map.put(Fields.BANK_BRANCH,
                        userThirdDeposity.getBankBranch() == null ? "" : userThirdDeposity.getBankBranch());
                    map.put(Fields.BANK_SIGN, bankSign);
                    map.put(Fields.BANK_TRX_NO,
                        userThirdDeposity.getTrxNo() == null ? "0" : userThirdDeposity.getTrxNo());
                    bankInfos.add(map);
                }
            }
            resultMap.put(Fields.BANK_ACCOUNT_DATA, JSON.toJSONString(bankInfos));
        } else if (systype.equals(Constant.SYSTYPE_SIGN_TWO)) {
            resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_SYSTYPE_ERROR);
        } else {
            resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_SYSTYPE_ERROR);
        }
        return resultMap;
    }

    // 证书申请
    @Override
    @Met
    public Map<String, Object> applyDigitalCert(CertForm form) {
        // 初始化返回值
        Map<String, Object> result = ReturnUtil.setErrorResult(new HashMap<String, Object>(), ErrorCode.SUCCESS);
        // 用户编号
        String userId = form.getUserId();
        String brokerId = form.getBrokerId();
        String clientName = form.getClientName();
        String idKind = form.getIdKind();
        String idNo = form.getIdNo();
        String pkcs10 = form.getCsr();
        if (StringUtil.isBlank(userId)) {// 用户不存在
            return ReturnUtil.setErrorResult(result, ErrorCode.NO_SUCH_CLIENT);
        }
        UserPresence userPresence = userPresenceAtom.getByUserId(brokerId, userId);
        if (userPresence == null) {
            return ReturnUtil.setErrorResult(result, ErrorCode.NO_SUCH_CLIENT);
        }
        // 自研sdk申请证书之前，判断是否通过视频
        String registerWay = userPresence.getRegisterWay();
        if ("11".equals(registerWay) || "15".equals(registerWay) || "16".equals(registerWay)) {
            String acceptNo = form.getAcceptNo();
            if (StringUtil.isBlank(acceptNo)) {
                return ReturnUtil.setErrorResult(result, ErrorCode.HK_ACCEPT_N0_ERROR);
            }
            BaseAccept acceptByAcceptNo = baseAcceptAtom.getBaseAcceptByAcceptNo(acceptNo);
            String sdkFlow = acceptByAcceptNo.getSdkFlow();
            if (StringUtil.isBlank(sdkFlow)) {
                return ReturnUtil.setErrorResult(result, ErrorCode.CERT_NO_VIDEO_PASS);
            }
            char[] status = sdkFlow.toCharArray();
            if (status[0] != '1' || status[1] != '0') { // 只有流程是 10 才能申请证书
                return ReturnUtil.setErrorResult(result, ErrorCode.CERT_NO_VIDEO_PASS);
            }
        }

        UserCaVerifyWithBLOBs userCaVerify =
            userCaVerifyAtom.getByUserIdAndType(userId, MiddleConstants.CA_TYPE_ZJ + "");// 自建证书
        if (userCaVerify == null) {
            userCaVerify = new UserCaVerifyWithBLOBs();
            userCaVerify.setUserId(new BigDecimal(userId));
        }
        // 判断是否国密证书
        String enableGM = ConfigUtil.get("enable.gm.sign", "");
        if ("visible".equals(enableGM)) {
            // 验证国密短信
            String validate_code = form.getValidateCode();
            if (StringUtil.isBlank(validate_code)) {
                return ReturnUtil.setErrorResult(result, ErrorCode.HK_MOBILE_CODE_IS_EMPTY);
            }
            ValidateCode validateCode = validateCodeAtom.getByTypeAndMphone(TypeConstant.VALIDATECODE_TYPE_APPLY_CERT,
                userPresence.getMobile(), TypeConstant.VALIDATECODE_STATUS_LOGIN);
            if (validateCode != null) {
                String validatecodeContent = validateCode.getValidatecodeContent();
                if (validate_code.equals(validatecodeContent)
                    || validate_code.equals(ConfigUtil.get("sms.verify.code", "").trim())) {
                    Date createTime = validateCode.getCreateTime();
                    long minutes = DateTimeUtil.getDatePoor(new Date(), createTime);
                    long effectiveTime = ConfigUtil.getSmsCodeEffectiveTime();
                    if (minutes >= effectiveTime) {
                        // 一小时重试次数
                        int dailyThreshold = Integer.valueOf(ConfigUtil.get("verify.code.error.times", "3"));
                        if (userLocalCommonService.codeSendTimesOverLimit(userPresence.getMobile(), dailyThreshold)) {// 每小时错误次数超过限制
                            result.put(Fields.ERROR_INFO, "您已重试超过" + dailyThreshold + "次,请一小时后再重新登录");
                            result.put(Fields.ERROR_NO, -1);
                            return result;
                        }
                        return VOUtil.genErrorResult(ErrorCode.VALIDATECODE_OUT_OF_TIME);
                    }

                    // 如果是国密短信需要验证国密短信系统
                    if ("visible".equals(ConfigUtil.get("gm_sms_validate", ""))
                        && "visible".equals(ConfigUtil.get("enable.gm.sign", ""))) {
                        String s = ConfigUtil.get("sms.verify.code", "");
                        boolean verifyCode = false; // 超级验证码
                        if (!StringUtil.isBlank(s) && validate_code.equals(s)) {
                            verifyCode = true;
                        }
                        // 验证国密短信
                        boolean gmSms = NetPassUtil.getInstance().checkSmsCode(validateCode.getCardSn(), validate_code);
                        if (!gmSms && !verifyCode) {
                            return VOUtil.genErrorResult(ErrorCode.VALIDATECODE_NO_VERIFY);
                        }
                    }
                } else {
                    // 一小时重试次数
                    int dailyThreshold = Integer.valueOf(ConfigUtil.get("verify.code.error.times", "3"));
                    if (userLocalCommonService.codeSendTimesOverLimit(userPresence.getMobile(), dailyThreshold)) {// 每小时错误次数超过限制
                        result.put(Fields.ERROR_INFO, "您已重试超过" + dailyThreshold + "次,请一小时后再重新登录");
                        result.put(Fields.ERROR_NO, -1);
                        return result;
                    }
                    result.put(Fields.ERROR_INFO, "您录入的动态验证码有误,请重试");// 新需求提示:20181030
                    result.put(Fields.ERROR_NO, -1);
                    return result;
                }
                validateCode.setValidatecodeStatus(new BigDecimal(1));
                validateCodeAtom.updateByValidateCode(validateCode);
            } else {
                return VOUtil.genErrorResult(ErrorCode.VALIDATECODE_GET_AGAIN);
            }

            /*
            *   证书申请流程：  新增用户返回注册码和uid，将uid存在证书表，存在即有用户，
            *   将注册码返回手机端进行用户签约（设备与注册码绑定，一台设备一个注册码只能签约一次，重新绑定需重获注册码），
            *   手机端申请证书（申请之前检测手机端是否安装证书和证书状态，证书不存在需要重新申请）
            *
             */

            String gm_mAuth_url = ConfigUtil.get("gm_mAuth_nei_url", "");
            // 获取国密uid
            String gmUid = userCaVerify.getGmUid();
            StringBuffer sb = new StringBuffer(gm_mAuth_url);
            String signCode = "";// 签约码或注册码
            if (StringUtil.isEmpty(gmUid)) {
                // 查询协同签名服务器获取用户
                sb.append("/user/queryUser.do?").append("username=u").append(brokerId)
                    .append(userPresence.getIdentityCode());
                Map<String, Object> userInfos = HttpUtils.getData(sb.toString(), null);
                if (ValueUtil.getInt(userInfos, Fields.ERROR_NO, -1) != 0) {
                    return ReturnUtil.setErrorResult(result, ErrorCode.CERT_GM_FAILD);
                }
                String users = ValueUtil.getString(userInfos, "data", "{}");
                JSONObject usersJson = JSONObject.parseObject(users);
                int gmUsersSize = usersJson.getIntValue("size");
                if (gmUsersSize > 0 && "000000".equals(usersJson.getString("resultcode"))) {
                    // 用户已存在
                    JSONObject jsonObject = usersJson.getJSONArray("data").getJSONObject(0);
                    String gm_uid = jsonObject.getString("uid");
                    userCaVerify.setGmUid(gm_uid);
                    userCaVerify.setCaType(BigDecimal.valueOf(MiddleConstants.CA_TYPE_ZJ));
                    userCaVerify.setVerifyFlag(BigDecimal.valueOf(StatusConstant.CERT_CHECK_PASS));
                    userCaVerifyAtom.saveOrUpdateUserCaVerify(userCaVerify);
                    if ("1".equals(form.getGmCertStatus())) {
                        sb = new StringBuffer(gm_mAuth_url);
                        sb.append("/user/refreshCode.do?uid=").append(gm_uid);
                        Map<String, Object> data = HttpUtils.getData(sb.toString(), null);
                        if (ValueUtil.getInt(data, Fields.ERROR_NO, -1) != 0) {
                            return ReturnUtil.setErrorResult(result, ErrorCode.CERT_GM_FAILD);
                        }
                        String gmResData = ValueUtil.getString(data, "data", "{}");
                        jsonObject = JSONObject.parseObject(gmResData);
                        if (!"000000".equals(jsonObject.getString("resultcode"))) {
                            log.error("重获注册码失败：" + data.toString());
                            result.put(Fields.ERROR_NO, -3);
                            result.put(Fields.ERROR_INFO, jsonObject.getString("resultmsg"));
                            return result;
                        }
                        result.put("gm_sign_code", jsonObject.getString("code"));
                    }
                    result.put("realName", userPresence.getUserName());
                    return result;
                } else {
                    log.info("获取用户失败,新增用户：" + userInfos.toString());
                }
                // username:信安世纪用户唯一值 u+期货公司编号+身份证号 istoken：0-证书用户；1-令牌用户；2-证书/令牌用户； mobile:手机号
                // realname:真实姓名 code:授权码（如不传，系统返回） flag:0返回注册码
                sb = new StringBuffer(gm_mAuth_url);
                sb.append("/user/newUser.do?").append("username=u").append(brokerId)
                    .append(userPresence.getIdentityCode()).append("&istoken=2").append("&realname=")
                    .append(userPresence.getUserName()).append("&flag=0");
                // 新增用户
                Map<String, Object> data = HttpUtils.getData(sb.toString(), null);
                if (ValueUtil.getInt(data, Fields.ERROR_NO, -1) != 0) {
                    return ReturnUtil.setErrorResult(result, ErrorCode.CERT_GM_FAILD);
                }
                String gmResData = ValueUtil.getString(data, "data", "{}");
                JSONObject jsonObject = JSONObject.parseObject(gmResData);
                if (!"000000".equals(jsonObject.getString("resultcode"))) {
                    log.error("新增用户失败：" + data.toString());
                    result.put(Fields.ERROR_NO, -3);
                    result.put(Fields.ERROR_INFO, jsonObject.getString("resultmsg"));
                    return result;
                }
                String gm_uid = jsonObject.getString("uid");
                signCode = jsonObject.getString("code");
                result.put("gm_sign_code", signCode);
                userCaVerify.setGmUid(gm_uid);
                userCaVerify.setCaType(BigDecimal.valueOf(MiddleConstants.CA_TYPE_ZJ));
                userCaVerify.setVerifyFlag(BigDecimal.valueOf(StatusConstant.CERT_CHECK_PASS));
                userCaVerifyAtom.saveOrUpdateUserCaVerify(userCaVerify);
            } else {
                // 获取用户状态
                // sb.append("/user/getUserState.do?uid=").append(gmUid);
                // Map<String, Object> data = HttpUtils.getData(sb.toString(), null);
                // if(ValueUtil.getInt(data, Fields.ERROR_NO, -1) != 0){
                // return ReturnUtil.setErrorResult(result, ErrorCode.CERT_GM_FAILD);
                // }
                // String gmResData = ValueUtil.getString(data, "data", "{}");
                // JSONObject jsonObject = JSONObject.parseObject(gmResData);
                // if (!"000000".equals(jsonObject.getString("resultcode"))){
                // result.put(Fields.ERROR_NO,-3);
                // result.put(Fields.ERROR_INFO,jsonObject.getString("resultmsg"));
                // return result;
                // }
                // if ("3".equals(jsonObject.getString("certstate"))){//证书有效
                //
                // }
                String gmCertStatus = form.getGmCertStatus(); // 0：有证书和注册码 1：没有注册码（需重获注册码） 2：无证书 3:证书失效
                if ("1".equals(gmCertStatus)) {
                    // 重获注册码
                    sb.append("/user/refreshCode.do?uid=").append(gmUid);
                    Map<String, Object> data = HttpUtils.getData(sb.toString(), null);
                    if (ValueUtil.getInt(data, Fields.ERROR_NO, -1) != 0) {
                        return ReturnUtil.setErrorResult(result, ErrorCode.CERT_GM_FAILD);
                    }
                    String gmResData = ValueUtil.getString(data, "data", "{}");
                    JSONObject jsonObject = JSONObject.parseObject(gmResData);
                    if (!"000000".equals(jsonObject.getString("resultcode"))) {
                        log.error("重获注册码失败：" + data.toString());
                        result.put(Fields.ERROR_NO, -3);
                        result.put(Fields.ERROR_INFO, jsonObject.getString("resultmsg"));
                        return result;
                    }
                    result.put("gm_sign_code", jsonObject.getString("code"));
                }
            }
            result.put("realName", userPresence.getUserName());
            return result;
        }

        if (StringUtil.isBlank(pkcs10)) {
            return VOUtil.genErrorResult(ErrorCode.PKCS10_INVALID);
        } else {
            userCaVerify.setCaCsr(pkcs10);
            userCaVerify.setCaType(BigDecimal.valueOf(MiddleConstants.CA_TYPE_ZJ));
        }
        userCaVerify.setVerifyFlag(BigDecimal.valueOf(StatusConstant.CERT_CHECK_PASS));
        if (null == userCaVerify.getVerifyId()) {
            userCaVerifyAtom.saveUserCaVerify(userCaVerify);
        } else {
            userCaVerifyAtom.updateUserCaVerify(userCaVerify);
        }

        Map<String, String> certRequest = new HashMap<String, String>();
        certRequest.put(CAFields.ID_NO, userPresence.getIdentityCode());
        certRequest.put(CAFields.HOLDER_NAME, userPresence.getUserName());
        certRequest.put(CAFields.PKCS10, pkcs10);
        certRequest.put(CAFields.EMAIL, userPresence.getMailAddress());
        certRequest.put(CAFields.MOBILE, userPresence.getMobile());
        certRequest.put(CAFields.COMPANY_ID, brokerId);
        certRequest.put(CAFields.CERT_TYPE, "0");// 证书类型：0-个人 1-企业
        certRequest.put(CAFields.CONPANY_NAME, ConfigUtil.get("security.name"));

        // 证书实现类型 国富安
        String caImplType = ConfigUtil.get("ca.selfca.implType");
        certRequest.put(CAFields.IMPL_TYPE, caImplType);
        addImplConfig(certRequest, caImplType);

        // if("CAServiceMockImpl".equals(caImplType)){//模拟实现
        // result.put(Fields.CSDC_CERT_SN, userCaVerify.getCaSn());
        // result.put(Fields.CSDC_P7CERT, userCaVerify.getCaCert());
        // result.put(Fields.CSDC_CERT_DN, userCaVerify.getCaDn());
        // return result;
        // }
        log.info("证书下载参数：" + certRequest.toString());

        try {
            Map<String, String> certResult = caService.perCertRequestAndDown(certRequest);
            if (CAErrorCode.isSuccess(certResult)) {
                String sn = certResult.get(CAFields.CERT_SN);
                if (sn == null || "".equals(sn)) {
                    return ReturnUtil.setErrorResult(result, ErrorCode.CERT_NO_SN);
                }
                String pkcs7 = certResult.get(CAFields.P7CERT);
                if (pkcs7 == null || "".equals(pkcs7)) {
                    return ReturnUtil.setErrorResult(result, ErrorCode.CERT_NO_P7);
                }
                String cert_start_time = certResult.get(CAFields.CERT_START_DATE);
                String csdc_cert_end_time = certResult.get(CAFields.CERT_FINAL_DATE);
                userCaVerify.setCaSn(sn);
                userCaVerify.setVerifyFlag(BigDecimal.valueOf(StatusConstant.CERT_CHECK_PASS));
                // result.put(Fields.CSDC_CERT_START_TIME, cert_start_time);
                // result.put(Fields.CSDC_CERT_END_TIME, csdc_cert_end_time);
                result.put(Fields.CSDC_CERT_SN, sn);

                result.put(Fields.CSDC_P7CERT, pkcs7);

                userCaVerify.setCaCert(pkcs7);
                userCaVerify.setSetupFlag(BigDecimal.valueOf(StatusConstant.CERT_SETUP_YES));
                userCaVerify.setSetupTime(new Date());

                String dn = certResult.get(CAFields.CERT_DN);
                userCaVerify.setCaDn(dn);
                result.put(Fields.CSDC_CERT_DN, dn);

            } else {
                result.put(Fields.ERROR_NO, -1);
                String errorInfo = certResult.get(CAFields.ERROR_INFO);
                result.put(Fields.ERROR_INFO, errorInfo);
            }
            userCaVerifyAtom.updateUserCaVerify(userCaVerify);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ReturnUtil.setErrorResult(result, ErrorCode.COMMON_ERROR);
        }

        return result;
    }

    // 证书验签
    @Override
    public Map<String, Object> validateCert(CertForm form) {
        Map<String, Object> res_map = new HashMap<>();
        String caImplType = ConfigUtil.get("sign.verify.implType");
        String signedText = form.getSignedText();

        Map<String, String> certRequest = new HashMap<String, String>();
        try {
            // 在验签时对验签对签名数据decode
            certRequest.put(CAFields.SIGNED_TEXT, URLDecoder.decode(signedText, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            res_map.put(Fields.ERROR_NO, 1);
            return res_map;
        }
        certRequest.put(CAFields.PLAIN_TEXT, signedText);
        certRequest.put(CAFields.IMPL_TYPE, caImplType);
        addImplConfig(certRequest, caImplType);
        Map<String, String> map = caService.verifySign(certRequest);
        if (map.get(CAFields.ERROR_NO).equals("0")) {
            res_map.put(Fields.ERROR_NO, 0);
            res_map.put(Fields.ERROR_INFO, "验签成功");
        } else {
            res_map.put(Fields.ERROR_NO, 1);
            res_map.put(Fields.ERROR_INFO, "验签失败");
        }

        return res_map;
    }

    @Override
    public Map<String, Object> qryRevisitPaper(PaperInfoForm form) {
        // 初始化返回值
        Map<String, Object> resMap = ReturnUtil.setErrorResult(new HashMap<String, Object>(), ErrorCode.SUCCESS);
        // 用户编号
        String client_id = form.getUserId();
        String systype = form.getSystype();
        String busType = form.getBusiness_type();

        // 验证用户编号
        if (StringUtil.isBlank(client_id)) {
            resMap = ReturnUtil.setErrorResult(resMap, ErrorCode.NO_SUCH_CLIENT);
            return resMap;
        }
        // 验证业务账户类型
        if (StringUtil.isBlank(systype)) {
            resMap.put(Fields.ERROR_NO, -1);
            resMap.put(Fields.ERROR_INFO, "systype业务账户类型为空");
            return resMap;
        }

        UserProfile userProfile = userProfileAtom.getByUserId(DataContext.getBrokerId(), client_id);
        if (userProfile == null) {// 客户不存在
            VOUtil.setErrorResult(resMap, ErrorCode.NO_SUCH_CLIENT);
            return resMap;
        }
        RevisitPaper paper = null;
        if (busType.equals("14") || busType.equals("22") || busType.equals("24")) {
            // 查询用户回访试卷
            paper = revisitPaperAtom.getBrokerRevisitPaper1(DataContext.getBrokerId());
            if (paper == null) {
                resMap.put(Fields.ERROR_NO, ErrorCode.REVISIT_PAPER_ERROR.getCode());
                resMap.put(Fields.ERROR_INFO, ErrorCode.REVISIT_PAPER_ERROR.getDescription() + "，联系电话"
                    + ConfigUtil.get("security.servicephone") + "。");
                return resMap;
            }
        } else {
            // 查询用户回访试卷
            paper = revisitPaperAtom.getBrokerRevisitPaper0(DataContext.getBrokerId());
            if (paper == null) {
                resMap.put(Fields.ERROR_NO, ErrorCode.REVISIT_PAPER_ERROR.getCode());
                resMap.put(Fields.ERROR_INFO, ErrorCode.REVISIT_PAPER_ERROR.getDescription() + "，联系电话"
                    + ConfigUtil.get("security.servicephone") + "。");
                return resMap;
            }
        }

        try {
            // 查询试题
            List<RevisitQuestion> questionList = revisitQuestionAtom.getRevisitQuestion(DataContext.getBrokerId(),
                String.valueOf(paper.getRevisitPaperId()));
            if (questionList == null || questionList.size() == 0) {
                resMap.put(Fields.ERROR_NO, ErrorCode.REVISIT_PAPER_ERROR.getCode());
                resMap.put(Fields.ERROR_INFO, ErrorCode.REVISIT_PAPER_ERROR.getDescription() + "，联系电话"
                    + ConfigUtil.get("security.servicephone") + "。");
                return resMap;
            }
            List<Map<String, Object>> paperData = new ArrayList<Map<String, Object>>();
            for (Iterator<RevisitQuestion> iterator = questionList.iterator(); iterator.hasNext();) {
                RevisitQuestion question = (RevisitQuestion)iterator.next();
                Map<String, Object> map = new HashMap<String, Object>();
                // 问题编号
                map.put("questionId", question.getQuestionNo());
                // 试题类型（0:单选题 1:多选题 2:可编辑题）
                map.put("questionType", question.getQuestionKind().toString());
                // 试题内容
                map.put("questionContent", question.getQuestionContent());
                // 默认答案(标准答案)
                if (question.getAnswerOptionId() != null) {
                    map.put("default", question.getAnswerOptionId().toString());
                }
                String warn_tip = "";
                // 查询选项列表
                List<RevisitQuestionOptions> optionList = revisitQuestionOptionsAtom
                    .getRevisitQuestionOptions(DataContext.getBrokerId(), "", String.valueOf(question.getQuestionNo()));
                List<Map<String, Object>> options = new ArrayList<Map<String, Object>>();
                for (Iterator<RevisitQuestionOptions> iterator2 = optionList.iterator(); iterator2.hasNext();) {
                    Map<String, Object> optionMap = new HashMap<>();
                    RevisitQuestionOptions revisitQuestionOptions = (RevisitQuestionOptions)iterator2.next();
                    if (question.getAnswerOptionId() != null && !revisitQuestionOptions.getOptionId().toString()
                        .equals(question.getAnswerOptionId().toString())) {
                        warn_tip = revisitQuestionOptions.getSelectedTip();
                    }
                    optionMap.put("optionId", revisitQuestionOptions.getOptionId().toString());
                    optionMap.put("optionContent", revisitQuestionOptions.getOptionContent());
                    // 警告提示
                    map.put("warn_tip", warn_tip);
                    options.add(optionMap);
                }
                // 选项内容(示例:{3:31-40岁,2:23-30岁,1:22岁以下})
                map.put("options", options);
                paperData.add(map);
            }
            // 组装接口返回数据
            Map<String, Object> questionData = new HashMap<String, Object>();
            questionData.put("paperId", paper.getPaperId());
            questionData.put("paperName", paper.getName());
            questionData.put("paperData", paperData);

            resMap.put("question_data", JSON.toJSONString(questionData));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ReturnUtil.setErrorResult(resMap, ErrorCode.COMMON_ERROR);
        }
        return resMap;
    }

    @Override
    public Map<String, Object> saveRevisitPaper(PaperInfoForm form) {
        // 初始化返回值
        Map<String, Object> resultMap = ReturnUtil.setErrorResult(new HashMap<String, Object>(), ErrorCode.SUCCESS);
        // 用户编号
        String user_id = form.getUserId();
        String systype = form.getSystype();
        // 验证用户编号
        if (StringUtil.isBlank(user_id)) {
            resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.NO_SUCH_CLIENT);
            return resultMap;
        }
        // 验证业务账户类型
        if (StringUtil.isBlank(systype)) {
            resultMap.put(Fields.ERROR_NO, -1);
            resultMap.put(Fields.ERROR_INFO, "systype业务账户类型为空");
            return resultMap;
        }
        String paper_answer = form.getPaperAnswer();
        Date now = new Date();
        List<UserRevisit> userRevisitList = new ArrayList<UserRevisit>();
        if (StringUtils.isEmpty(paper_answer)) {
            resultMap.put(Fields.ERROR_NO, -1);
            resultMap.put(Fields.ERROR_INFO, "答题结果为空.");
            return resultMap;
        }
        String[] qAnswer = paper_answer.split("\\|");
        try {
            // 用户和用户的问卷回访是一对一的关系，所以在保存一条数据之前先删除已经存在的数据
            userRevisitAtom.deleteByUserId(Long.valueOf(user_id));
            for (int i = 0; i < qAnswer.length; i++) {
                String[] q_a = qAnswer[i].split("&");
                String questionNo = q_a[0];
                RevisitQuestion question =
                    revisitQuestionAtom.getByQuestionNo(Long.valueOf(questionNo), DataContext.getBrokerId());
                if (question == null) {
                    return VOUtil.genErrorResultWithExtraInfo(ErrorCode.REVISIT_PAPER_ANWER_ERROR,
                        "题号为" + questionNo + "的题目不存在");
                }
                String[] ans = q_a[1].split("\\^");
                for (int j = 0; j < ans.length; j++) {
                    long optionId = Long.valueOf(ans[j]);
                    RevisitQuestionOptions option =
                        revisitQuestionOptionsAtom.getById(optionId, DataContext.getBrokerId());
                    if (option == null) {
                        return VOUtil.genErrorResultWithExtraInfo(ErrorCode.REVISIT_PAPER_ANWER_ERROR,
                            "选项" + optionId + "不存在");
                    }
                    UserRevisit userRevisit = new UserRevisit();
                    userRevisit.setUserId(Long.valueOf(user_id));
                    userRevisit.setRevisitPaperId(question.getRevisitPaperId());
                    userRevisit.setQuestionId(question.getQuestionId());
                    userRevisit.setQuestionContent(question.getQuestionContent());
                    userRevisit.setOptionContent(option.getOptionContent());
                    userRevisit.setOptionId(option.getOptionId());
                    userRevisit.setRevisitTime(now);
                    // 答案和默认答案不匹配
                    if (!StringUtil.isBlank(question.getAnswerOptionId())
                        && !question.getAnswerOptionId().equals(option.getOptionId().toString())) {
                        userRevisit.setAdditionContent1("false");
                    } else {
                        userRevisit.setAdditionContent1("true");
                    }
                    userRevisitAtom.insert(userRevisit);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ReturnUtil.setErrorResult(resultMap, ErrorCode.COMMON_ERROR);
        }
        return VOUtil.genSuccessResult();
    }

    @Override
    public Map<String, Object> qryRevisitAnswer(PaperInfoForm form) {
        // 初始化返回值
        Map<String, Object> resultMap = ReturnUtil.setErrorResult(new HashMap<String, Object>(), ErrorCode.SUCCESS);
        // 用户编号
        String user_id = form.getUserId();
        String systype = form.getSystype();
        // 验证用户编号
        if (StringUtil.isBlank(user_id)) {
            resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.NO_SUCH_CLIENT);
            return resultMap;
        }
        // 验证业务账户类型
        if (StringUtil.isBlank(systype)) {
            resultMap.put(Fields.ERROR_NO, -1);
            resultMap.put(Fields.ERROR_INFO, "systype业务账户类型为空");
            return resultMap;
        }
        UserPresence userPresence = this.userPresenceAtom.getByUserId(DataContext.getBrokerId(), form.getUserId());
        if (userPresence == null) {
            resultMap.put(Fields.ERROR_NO, -1);
            resultMap.put(Fields.ERROR_INFO, "该客户不存在！请重新登录.");
            return resultMap;
        }
        // 获取用户回访问卷信息
        List<UserRevisit> userRevisitList = userRevisitAtom.getByUserId(Long.valueOf(form.getUserId()));
        // if(userRevisitList == null || userRevisitList.size()<=0){
        // resultMap.put(Fields.ERROR_NO,-1);
        // resultMap.put(Fields.ERROR_INFO, "该客户不存在回访问卷记录.");
        // return resultMap;
        // }
        // 把客户的问卷信息放入map
        Map<String, Object> answerMap = new HashMap<>();
        for (UserRevisit userRevisit : userRevisitList) {
            if (answerMap.get(userRevisit.getQuestionId().toString()) != null) {// 多选
                String str =
                    answerMap.get(userRevisit.getQuestionId().toString()) + "^" + userRevisit.getOptionId().toString();
                answerMap.put(userRevisit.getQuestionId().toString(), str);
            } else {
                answerMap.put(userRevisit.getQuestionId().toString(), userRevisit.getOptionId().toString());
            }
        }
        // 查询用户回访试卷
        RevisitPaper paper = revisitPaperAtom.getBrokerRevisitPaper(DataContext.getBrokerId());
        if (paper == null) {
            resultMap.put(Fields.ERROR_NO, ErrorCode.REVISIT_PAPER_ERROR.getCode());
            resultMap.put(Fields.ERROR_INFO, ErrorCode.REVISIT_PAPER_ERROR.getDescription() + "，联系电话"
                + ConfigUtil.get("security.servicephone") + "。");
            return resultMap;
        }

        try {
            // 查询试题
            List<RevisitQuestion> questionList = revisitQuestionAtom.getRevisitQuestion(DataContext.getBrokerId(),
                String.valueOf(paper.getRevisitPaperId()));
            if (questionList == null || questionList.size() == 0) {
                resultMap.put(Fields.ERROR_NO, ErrorCode.REVISIT_PAPER_ERROR.getCode());
                resultMap.put(Fields.ERROR_INFO, ErrorCode.REVISIT_PAPER_ERROR.getDescription() + "，联系电话"
                    + ConfigUtil.get("security.servicephone") + "。");
                return resultMap;
            }
            List<Map<String, Object>> paperData = new ArrayList<Map<String, Object>>();
            for (Iterator<RevisitQuestion> iterator = questionList.iterator(); iterator.hasNext();) {
                RevisitQuestion question = (RevisitQuestion)iterator.next();
                Map<String, Object> map = new HashMap<String, Object>();
                // 问题编号
                map.put("questionId", question.getQuestionNo());
                // 试题类型（0:单选题 1:多选题 2:可编辑题）
                map.put("questionType", question.getQuestionKind().toString());
                // 试题内容
                map.put("questionContent", question.getQuestionContent());
                // 默认答案(标准答案)
                if (question.getAnswerOptionId() != null) {
                    map.put("default", question.getAnswerOptionId().toString());
                }
                String warn_tip = "";
                String answer = "";
                if (answerMap.get(question.getQuestionId().toString()) != null) {// 用户答题结果
                    answer = String.valueOf(answerMap.get(question.getQuestionId().toString()));
                    map.put("answer", answer);
                }
                // 查询选项列表
                List<RevisitQuestionOptions> optionList = revisitQuestionOptionsAtom
                    .getRevisitQuestionOptions(DataContext.getBrokerId(), "", String.valueOf(question.getQuestionNo()));
                List<Map<String, Object>> options = new ArrayList<Map<String, Object>>();
                for (Iterator<RevisitQuestionOptions> iterator2 = optionList.iterator(); iterator2.hasNext();) {
                    Map<String, Object> optionMap = new HashMap<>();
                    RevisitQuestionOptions revisitQuestionOptions = (RevisitQuestionOptions)iterator2.next();
                    if (question.getAnswerOptionId() != null && !revisitQuestionOptions.getOptionId().toString()
                        .equals(question.getAnswerOptionId().toString())) {
                        warn_tip = revisitQuestionOptions.getSelectedTip();
                    }
                    optionMap.put("optionId", revisitQuestionOptions.getOptionId().toString());
                    optionMap.put("optionContent", revisitQuestionOptions.getOptionContent());
                    // 警告提示
                    map.put("warn_tip", warn_tip);
                    options.add(optionMap);
                }
                // 选项内容(示例:{3:31-40岁,2:23-30岁,1:22岁以下})
                map.put("options", options);
                paperData.add(map);
            }
            // 组装接口返回数据
            Map<String, Object> questionData = new HashMap<String, Object>();
            questionData.put("paperId", paper.getPaperId());
            questionData.put("paperName", paper.getName());
            questionData.put("paperData", paperData);
            resultMap.put("question_data", JSON.toJSONString(questionData));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ReturnUtil.setErrorResult(resultMap, ErrorCode.COMMON_ERROR);
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> qryAccountStatus(UserInfoForm form) {
        // 初始化返回值
        Map<String, Object> resultMap = ReturnUtil.setErrorResult(new HashMap<String, Object>(), ErrorCode.SUCCESS);
        // 获取入参
        String idKind = form.getIdKind();
        String idNo = form.getIdNo();
        String systype = form.getSystype();
        String brokerId = form.getBrokerId();
        // 判断入参合法
        if (StringUtil.isBlank(systype)) {
            resultMap.put(Fields.ERROR_NO, -1);
            resultMap.put(Fields.ERROR_INFO, "systype入参为：[" + systype + "]");
            return resultMap;
        }
        if (StringUtil.isBlank(idNo)) {
            resultMap.put(Fields.ERROR_NO, -1);
            resultMap.put(Fields.ERROR_INFO, "idNo入参为：[" + idNo + "]");
            return resultMap;
        }
        if (StringUtil.isBlank(idKind)) {
            resultMap.put(Fields.ERROR_NO, -1);
            resultMap.put(Fields.ERROR_INFO, "idKind入参为：[" + idKind + "]");
            return resultMap;
        }
        // 身份证号校验
        String res = ValidateInfoUtil.validateCID(idNo);
        if (!res.equals(TextConstant.SUCCESS)) {
            // resultMap = ReturnUtil.setErrorResult(resultMap,ErrorCode.HK_VALIDATE_CID_ERROR);
            // return resultMap;
            resultMap.put(Fields.ERROR_NO, -1);
            resultMap.put(Fields.ERROR_INFO, res);
            return resultMap;
        }
        try {
            // 判断客户是否已经存在身份证相同的数据
            if ("1".equals(systype)) {// 期货账户
                List<UserPresence> list = userPresenceAtom.getUserPresencesByIDC(brokerId, idKind, idNo);
                if (list == null || list.size() < 1) {
                    return resultMap;
                }
                // 存在数据就判断对于的数据
                for (UserPresence user : list) {
                    if (!form.getUserId().equals(user.getUserId().toString())) {// 查询出来userPresence表的数据跟当前登陆客户的user_id不同。提示。
                        UserProfile userProfile =
                            userProfileAtom.getByUserId(form.getBrokerId(), user.getUserId().toString());
                        if (userProfile != null) {
                            String mobile = userProfile.getMobile();// 注册手机号
                            resultMap.put(Fields.REMARK, "您此前曾通过其他手机号码办理开户相关业务，目前尚未办结。是否继续使用本次手机号码办理？");
                            resultMap.put(Fields.MOBILE, mobile);
                            resultMap.put(Fields.OPEN_STATUS, user.getStatus());
                            return resultMap;
                        }
                    }
                }
            } else if ("2".equals(systype)) {// 资管账户

            } else {// systype非法
                resultMap.put(Fields.ERROR_NO, -1);
                resultMap.put(Fields.ERROR_INFO, "systype入参为：[" + systype + "]");
                return resultMap;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ReturnUtil.setErrorResult(resultMap, ErrorCode.COMMON_ERROR);
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> qryThdBankAccount(BankInfoForm form) {
        // 初始化返回值
        Map<String, Object> resultMap = ReturnUtil.setErrorResult(new HashMap<String, Object>(), ErrorCode.SUCCESS);
        // 获取输入参数
        String brokerId = form.getBrokerId();
        String systype = form.getSystype();
        String opStation = form.getOpStation();
        String fundAccount = form.getFundAccount();
        String userId = form.getUserId();
        String acceptNo = form.getAcceptNo();
        String businessType = form.getBusinessType();
        // 验证用户
        UserProfile userProfile = userProfileAtom.getUserProfileByUserId(new BigDecimal(userId));
        if (userProfile == null) {
            resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.NO_SUCH_CLIENT);
            return resultMap;
        }
        String idKind = userProfile.getIdentityType();
        String idNo = userProfile.getIdentityCode();
        // 封装调用三方参数
        Map<String, String> params = new HashMap<>();
        params.put(Fields.BROKER_ID, brokerId);
        params.put(Fields.ID_KIND, idKind);
        params.put(Fields.ID_NO, idNo);
        params.put(Fields.OP_STATION, opStation);
        params.put(Fields.SYSTYPE, systype);
        params.put(Fields.FUND_ACCOUNT, fundAccount);
        // 调用三方获取银期账户信息
        Map<String, Object> mapBankAccount = counterService.qryBankAccount(params);
        if (ValueUtil.getInt(mapBankAccount, Fields.ERROR_NO, -1) != 0) {
            return mapBankAccount;
        }
        // 更新本地客户信息
        if (StringUtil.isBlank(acceptNo)) {
            return mapBankAccount;
        } else {
            // 业务类型 验证
            if (!businessType.equals(Constant.BUSINESS_TYPE_SIGN_NINETEEN)) {
                return ReturnUtil.setErrorResult(resultMap, ErrorCode.BIZ_TYPE_NOTEXIISTS);
            }
            // 受理单号验证
            Map<String, Object> baseAcceptMap =
                userLocalCommonService.verificationBaseAccept(brokerId, userId, businessType, acceptNo);
            if (ValueUtil.getInt(baseAcceptMap, Fields.ERROR_NO, -1) != 0) {
                return baseAcceptMap;
            }
            BaseAccept baseAccept = (BaseAccept)baseAcceptMap.get("baseAccept");
            String synchronousFlag = (baseAccept.getSynchronousFlag() != null) ? baseAccept.getSynchronousFlag() : "0";
            if (synchronousFlag.equals("1")) {
                try {
                    // 1.删除 本地 银行信息
                    List<UserThirdDeposity> userThirdDepositys =
                        userThirdDeposityAtom.getUserThirdDeposityByUserId(Long.parseLong(userId));
                    if (userThirdDepositys != null && !userThirdDepositys.isEmpty()) {
                        for (int i = 0; i < userThirdDepositys.size(); i++) {
                            UserThirdDeposity userThirdDeposity = userThirdDepositys.get(i);
                            String bank_sign = userThirdDeposity.getDeposityId().toString();
                            // 删除数据
                            userThirdDeposityAtom.deleteUserThirdDeposity(Long.parseLong(userId),
                                Long.parseLong(bank_sign));
                        }
                    }
                    List<Map<String, Object>> bankInfos = new ArrayList<>();
                    // 2.解析 三方 返回的 银期账户对象 保存 数据到本地 以及图片到 影像服务器
                    if (mapBankAccount.get(Fields.BANK_ACCOUNT_DATA) != null) {
                        List<Map<String, Object>> bankAccountList =
                            JsonUtil.jsonToList(String.valueOf(mapBankAccount.get(Fields.BANK_ACCOUNT_DATA)));
                        if (bankAccountList != null && !bankAccountList.isEmpty()) {
                            for (int i = 0; i < bankAccountList.size(); i++) {
                                Map<String, Object> bankAccount = bankAccountList.get(i);
                                String bankBranch = (bankAccount.get(Fields.BANK_BRANCH) != null)
                                    ? bankAccount.get(Fields.BANK_BRANCH).toString() : "";
                                String bankId = (bankAccount.get(Fields.BANK_ID) != null)
                                    ? bankAccount.get(Fields.BANK_ID).toString() : "";
                                String bankNo = (bankAccount.get(Fields.BANK_NO) != null)
                                    ? bankAccount.get(Fields.BANK_NO).toString() : "";
                                String bankPic = (bankAccount.get(Fields.BANK_PIC) != null)
                                    ? bankAccount.get(Fields.BANK_PIC).toString() : "";
                                String bankRela = (bankAccount.get(Fields.BANK_RELA) != null)
                                    ? bankAccount.get(Fields.BANK_RELA).toString() : "0";
                                Map<String, Object> map = new HashMap();
                                map.put(Fields.BANK_ID, bankId);
                                map.put(Fields.BANK_NO, bankNo);
                                map.put(Fields.BANK_BRANCH, bankBranch);
                                if (!bankId.equals("")) {
                                    BaseBank bank = baseBankAtom.queryBankByKey(Long.valueOf(bankId));
                                    map.put(Fields.BANK_NAME, bank.getBankName());
                                } else {
                                    map.put(Fields.BANK_NAME, "");
                                }
                                // 保存 三方 银行信息 到 本地
                                UserThirdDeposity deposity = new UserThirdDeposity();
                                deposity.setUserId(Long.parseLong(userId));
                                if (!"".equals(bankId)) {
                                    // 获取银行信息
                                    BaseBank bank = baseBankAtom.queryBankByBankNo(bankId);
                                    String bank_id = "";
                                    if (bank != null) {
                                        bank_id = bank.getBankId().toString();
                                        deposity.setBankId(Long.parseLong(bank_id));
                                    }
                                }
                                deposity.setAccountNo(bankNo);
                                // deposity.setTrxNo("1");
                                deposity.setBankBranch(bankBranch);
                                deposity.setEndTime(new Date());
                                deposity.setValidateFlag(new BigDecimal(StatusConstant.DEPOSITY_REVIEW_APPLY));
                                deposity.setApplySuccessFlag(new BigDecimal(StatusConstant.USER_ACC_SUCCESSFLAG_C)); // 创建银行
                                userThirdDeposityAtom.saveUserThirdDeposity(deposity);
                                // if(bankPic.equals("")) {
                                // ClassPathResource classPathResource = new ClassPathResource("static/nobank.jpg");
                                // bankPic = IOUtil.streamToBase64String(classPathResource.getInputStream());
                                // }
                                if (!bankPic.equals("")) {
                                    String bankSign = deposity.getDeposityId().toString();
                                    String picOssKey = IDUtil.genFullID(brokerId, String.valueOf(userId),
                                        bankSign + "Div" + ImageTypeUtil.getImageType(ImageTypeUtil.IDENTITY_BANK));
                                    if (FmJceUtil.isGm()) {
                                        picOssKey = picOssKey + "_GM";
                                    }
                                    deposity.setBankPic(picOssKey);
                                    if (bankRela.equals("0")) {
                                        deposity.setTrxNo("1");
                                    } else {
                                        deposity.setTrxNo("3");
                                    }
                                    userThirdDeposityAtom.updateUserThirdDeposity(deposity);
                                    // 保存 图片 到 影像服务器
                                    // 解析保存图片
                                    Map<String, Object> asiMap =
                                        userLocalCommonService.saveImage(bankPic, brokerId, picOssKey);
                                    if (ValueUtil.getInt(asiMap, Fields.ERROR_NO, -1) != 0) {
                                        return asiMap;
                                    }
                                } else {
                                    if (bankRela.equals("0")) {
                                        deposity.setTrxNo("2");
                                    } else {
                                        deposity.setTrxNo("4");
                                    }
                                    ClassPathResource classPathResource = new ClassPathResource("static/nocard.png");
                                    bankPic = IOUtil.streamToBase64String(classPathResource.getInputStream());
                                    String bankSign = deposity.getDeposityId().toString();
                                    String picOssKey = IDUtil.genFullID(brokerId, String.valueOf(userId),
                                        bankSign + "Div" + ImageTypeUtil.getImageType(ImageTypeUtil.IDENTITY_BANK));
                                    if (FmJceUtil.isGm()) {
                                        picOssKey = picOssKey + "_GM";
                                    }
                                    deposity.setBankPic(picOssKey);
                                    userThirdDeposityAtom.updateUserThirdDeposity(deposity);
                                    // 保存 图片 到 影像服务器
                                    // 解析保存图片
                                    Map<String, Object> asiMap =
                                        userLocalCommonService.saveImage(bankPic, brokerId, picOssKey);
                                    if (ValueUtil.getInt(asiMap, Fields.ERROR_NO, -1) != 0) {
                                        return asiMap;
                                    }
                                }
                                bankInfos.add(map);
                            }
                        }
                    }
                    // 3.3 存储受理单扩展信息数据
                    Map<String, Object> baseAcceptInfoMap = userLocalCommonService.saveBaseAcceptInfo(brokerId, userId,
                        businessType, acceptNo, JSON.toJSONString(bankInfos));
                    if (ValueUtil.getInt(baseAcceptInfoMap, Fields.ERROR_NO, -1) != 0) {
                        return baseAcceptInfoMap;
                    }
                    // 修改 受理单 同步标识
                    baseAccept.setSynchronousFlag("2");
                    baseAcceptAtom.updateBaseAccept(baseAccept);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    return ReturnUtil.setErrorResult(resultMap, ErrorCode.COMMON_ERROR);
                }
            }
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> modifyUserIDcard(UserInfoForm form) {
        // 初始化返回值
        Map<String, Object> resultMap = ReturnUtil.setErrorResult(new HashMap<String, Object>(), ErrorCode.SUCCESS);
        // 获取输入参数
        String brokerId = form.getBrokerId(); // 期货公司编号
        String userId = form.getUserId(); // 用户编号
        String systype = form.getSystype(); // 业务账户类型
        String businessType = form.getBusinessType(); // 业务类型
        String acceptNo = form.getAcceptNo(); // 受理单号
        String idBegindate = form.getIdBegindate(); // 证件有效开始日期（日期格式：yyyyMMdd）
        String idEnddate = form.getIdEnddate(); // 证件有效截止日期（日期格式：yyyyMMdd）
        String opStation = form.getOpStation(); // 操作站点（IP地址:xxx.xxx.xxx.xxx）
        String fundAccount = form.getFundAccount(); // 资金账号
        String idAddress = form.getIdAddress(); // 证件地址
        // 参数验证
        // 业务类型
        if (!businessType.equals(Constant.BUSINESS_TYPE_SIGN_TWENTY_THREE)
            && !businessType.equals(Constant.BUSINESS_TYPE_SIGN_SIXTEEN)) {
            resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.BIZ_TYPE_NOTEXIISTS);
            return resultMap;
        }
        // 受理单号验证
        Map<String, Object> baseAcceptMap =
            userLocalCommonService.verificationBaseAccept(brokerId, userId, businessType, acceptNo);
        if (ValueUtil.getInt(baseAcceptMap, Fields.ERROR_NO, -1) != 0) {
            return baseAcceptMap;
        }
        // 证件有效开始日期（日期格式：yyyyMMdd）
        if (StringUtil.isBlank(idBegindate)) {
            idBegindate = null;
        } else {
            String res6 = ValidateInfoUtil.validateDateFormat(idBegindate);
            if (!res6.equals(TextConstant.SUCCESS)) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_BEGIN_DATE_ERROR);
                return resultMap;
            }
        }
        // 证件有效截止日期（日期格式：yyyyMMdd）
        if (StringUtil.isBlank(idEnddate)) {
            idBegindate = null;
        } else {
            String res7 = ValidateInfoUtil.validateDateFormat(idEnddate);
            if (!res7.equals(TextConstant.SUCCESS)) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_END_DATE_ERROR);
                return resultMap;
            }
        }
        // 证件开始、结束日期验证（日期格式：yyyyMMdd）
        String res111 = ValidateInfoUtil.validateCompareDate(idBegindate, idEnddate);
        if (!res111.equals(TextConstant.SUCCESS)) {
            resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_BEGINOREND_DATE_ERROR);
            return resultMap;
        }
        // 身份证地址
        String res10 = ValidateInfoUtil.validateAddress(idAddress, 3, 100);
        if (!res10.equals(TextConstant.SUCCESS)) {
            resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_ID_ADDRESS_ERROR);
            return resultMap;
        }
        // 验证用户编号
        if (StringUtil.isBlank(userId)) {
            resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.NO_SUCH_CLIENT);
        } else {
            // 1.查询 user_profile
            UserProfile userProfile = userProfileAtom.getUserProfileByUserId(new BigDecimal(userId));
            if (userProfile == null) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.NO_SUCH_CLIENT);
                return resultMap;
            }
            String idKind = userProfile.getIdentityType();
            String idNo = userProfile.getIdentityCode();
            if (systype.equals(Constant.SYSTYPE_SIGN_ONE) || systype.equals(Constant.SYSTYPE_SIGN_TWO)) {
                // 2.查询 user_id_info
                UserIdInfo userIdInfo = userIdInfoAtom.getUserIdInfoById(userId);
                if (userIdInfo == null) {
                    return ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_USER_ID_INFO_ERROR);
                } else {
                    // 3.记录变更数据
                    // 3.1 获取 受理单扩展信息表 数据
                    BaseAcceptInfo baseAcceptInfo =
                        baseAcceptInfoAtom.getBaseAcceptInfo(brokerId, userId, businessType, acceptNo);
                    if (baseAcceptInfo == null) {
                        // 3.2 获取 柜台客户 身份证有效期 开始 结束 日期
                        // 封装调用三方参数
                        Map<String, String> params = new HashMap<>();
                        params.put(Fields.ID_NO, idNo);
                        params.put(Fields.ID_KIND, idKind);
                        params.put(Fields.BROKER_ID, brokerId);
                        params.put(Fields.OP_STATION, opStation);
                        params.put(Fields.SYSTYPE, systype);
                        // 获取本地资金账号
                        UserAccounts userAccount = new UserAccounts();
                        userAccount.setUserId(Long.valueOf(userId));
                        userAccount.setInvsetType("1001");
                        List<UserAccounts> listOne = userAccountAtom.getByUserAccounts(userAccount);
                        if (listOne != null && !listOne.isEmpty() && StringUtil.isBlank(fundAccount)) {
                            UserAccounts ua = listOne.get(0);
                            fundAccount = ua.getAccountNo() == null ? "" : ua.getAccountNo();
                        }
                        params.put(Fields.FUND_ACCOUNT, fundAccount);
                        // 调用三方获取客户信息
                        Map<String, Object> mapClientInfo = counterService.qryClientInfo(params);
                        String futures_account_data =
                            JSON.toJSONString(mapClientInfo.get("futures_account_data") == null ? ""
                                : mapClientInfo.get("futures_account_data"));
                        mapClientInfo.put("futures_account_data", futures_account_data);
                        if (ValueUtil.getInt(mapClientInfo, Fields.ERROR_NO, -1) != 0) {
                            return mapClientInfo;
                        }
                        String idBegindateT = (mapClientInfo.get(Fields.ID_BEGINDATE) != null)
                            ? mapClientInfo.get(Fields.ID_BEGINDATE).toString() : "";
                        String idEnddateT = (mapClientInfo.get(Fields.ID_ENDDATE) != null)
                            ? mapClientInfo.get(Fields.ID_ENDDATE).toString() : "";
                        String idAddressT =
                            (mapClientInfo.get("id_addr") != null) ? mapClientInfo.get("id_addr").toString() : "";
                        if (idEnddateT.equals("-1")) {
                            idEnddateT = "20991231";
                        }
                        //
                        Map<String, Object> map = new HashMap();
                        map.put(Fields.ID_BEGINDATE, idBegindateT);
                        map.put(Fields.ID_ENDDATE, idEnddateT);
                        map.put(Fields.ID_ADDRESS, idAddressT);
                        // 3.3 存储受理单扩展信息数据
                        Map<String, Object> baseAcceptInfoMap = userLocalCommonService.saveBaseAcceptInfo(brokerId,
                            userId, businessType, acceptNo, JSON.toJSONString(map));
                        if (ValueUtil.getInt(baseAcceptInfoMap, Fields.ERROR_NO, -1) != 0) {
                            return baseAcceptInfoMap;
                        }
                    }
                    boolean tz = false;
                    if (idBegindate != null) {
                        userIdInfo.setDeliveryDate(
                            DateTimeUtil.toDateOrNull(idBegindate, DateTimeUtil.FORMAT_YYYYMMDD_NO_BREAK));
                        tz = true;
                    }
                    if (idEnddate != null) {
                        userIdInfo
                            .setDueDate(DateTimeUtil.toDateOrNull(idEnddate, DateTimeUtil.FORMAT_YYYYMMDD_NO_BREAK));
                        if (idEnddate.equals("20991231")) {
                            userIdInfo.setForeverFlag(new BigDecimal(TypeConstant.FOREVER_FLAG_YES));
                        } else {
                            userIdInfo.setForeverFlag(new BigDecimal(TypeConstant.FOREVER_FLAG_NO));
                        }
                        tz = true;
                    }
                    if (idAddress != null) {
                        userIdInfo.setAddress(idAddress);
                        tz = true;
                    }
                    if (!tz) {
                        return ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_USER_INFO_ERROR);
                    }
                    // 4.存储数据
                    try {
                        userIdInfoAtom.update(userIdInfo);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        return ReturnUtil.setErrorResult(resultMap, ErrorCode.COMMON_ERROR);
                    }
                    return resultMap;
                }
            } else {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_SYSTYPE_ERROR);
            }
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> modifyUserTelphone(UserInfoForm form) {
        // 初始化返回值
        Map<String, Object> resultMap = ReturnUtil.setErrorResult(new HashMap<String, Object>(), ErrorCode.SUCCESS);
        // 获取输入参数
        String brokerId = form.getBrokerId(); // 期货公司编号
        String userId = form.getUserId(); // 用户编号
        String systype = form.getSystype(); // 业务账户类型
        String businessType = form.getBusinessType(); // 业务类型
        String acceptNo = form.getAcceptNo(); // 受理单号
        String contactNumber = form.getContactNumber(); // 联系电话
        String validateCode = form.getValidateCode(); // 短信验证码
        // 参数验证
        // 业务类型
        if (!businessType.equals(Constant.BUSINESS_TYPE_SIGN_SEVENTEEN)) {
            resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.BIZ_TYPE_NOTEXIISTS);
            return resultMap;
        }
        // 受理单号验证
        Map<String, Object> baseAcceptMap =
            userLocalCommonService.verificationBaseAccept(brokerId, userId, businessType, acceptNo);
        if (ValueUtil.getInt(baseAcceptMap, Fields.ERROR_NO, -1) != 0) {
            return baseAcceptMap;
        }
        // 联系电话校验
        if (StringUtil.isBlank(contactNumber)) {
            return ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_CONTACT_NUMBER_ERROR);
        } else {
            String res11 = ValidateInfoUtil.validateMobilePhone(contactNumber);
            String res12 = ValidateInfoUtil.validatePhone(contactNumber);
            if (!res11.equals(TextConstant.SUCCESS) && !res12.equals(TextConstant.SUCCESS)) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_CONTACT_NUMBER_ERROR);
                return resultMap;
            }
            if (res11.equals(TextConstant.SUCCESS)) {
                // 短信验证
                ValidateCode vc =
                    validateCodeAtom.getByTypeAndMphone(117, contactNumber, TypeConstant.VALIDATECODE_STATUS_LOGIN);
                if (vc != null) {
                    String validatecodeContent = vc.getValidatecodeContent();
                    if (validateCode.equals(validatecodeContent)) {
                        Date createTime = vc.getCreateTime();
                        long minutes = DateTimeUtil.getDatePoor(new Date(), createTime);
                        long effectiveTime = ConfigUtil.getSmsCodeEffectiveTime();
                        if (minutes >= effectiveTime) {
                            return VOUtil.genErrorResult(ErrorCode.VALIDATECODE_OUT_OF_TIME);
                        }
                    } else {
                        resultMap.put(Fields.ERROR_INFO, "您录入的短信验证码有误,请重试");
                        resultMap.put(Fields.ERROR_NO, -1);
                        return resultMap;
                    }
                    vc.setValidatecodeStatus(new BigDecimal(1));
                    validateCodeAtom.updateByValidateCode(vc);
                } else {
                    return VOUtil.genErrorResult(ErrorCode.VALIDATECODE_GET_AGAIN);
                }
            }
        }
        // 验证用户编号
        if (StringUtil.isBlank(userId)) {
            resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.NO_SUCH_CLIENT);
        } else {
            // 1.查询 user_profile
            UserProfile userProfile = userProfileAtom.getUserProfileByUserId(new BigDecimal(userId));
            if (userProfile == null) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.NO_SUCH_CLIENT);
                return resultMap;
            }
            if (systype.equals(Constant.SYSTYPE_SIGN_ONE) || systype.equals(Constant.SYSTYPE_SIGN_TWO)) {
                // 2.查询 user_presence
                UserPresence userPresence = userPresenceAtom.getByUserId(brokerId, userId);
                if (userPresence == null) {
                    return ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_USER_PRESENCE_ERROR);
                } else {
                    // 3.存储变更数据
                    try {
                        // 3.1 获取 受理单扩展信息表 数据
                        BaseAcceptInfo baseAcceptInfo =
                            baseAcceptInfoAtom.getBaseAcceptInfo(brokerId, userId, businessType, acceptNo);
                        if (baseAcceptInfo == null) {
                            // 记录变更数据
                            Map<String, Object> map = new HashMap();
                            map.put(Fields.CONTACT_NUMBER, userPresence.getTelphone());
                            Map<String, Object> baseAcceptInfoMap = userLocalCommonService.saveBaseAcceptInfo(brokerId,
                                userId, businessType, acceptNo, JSON.toJSONString(map));
                            if (ValueUtil.getInt(baseAcceptInfoMap, Fields.ERROR_NO, -1) != 0) {
                                return baseAcceptInfoMap;
                            }
                        }
                        userPresence.setTelphone(contactNumber);
                        userPresence.setBrokerId(brokerId);
                        userPresence.setLastUpdateTime(new Date());
                        userPresence.setLastUpdateBy(new BigDecimal(userId));
                        userPresenceAtom.update(userPresence);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        return ReturnUtil.setErrorResult(resultMap, ErrorCode.COMMON_ERROR);
                    }
                }
            } else {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_SYSTYPE_ERROR);
            }
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> modifyUserInfo(UserInfoForm form) {
        // 初始化返回值
        Map<String, Object> resultMap = ReturnUtil.setErrorResult(new HashMap<String, Object>(), ErrorCode.SUCCESS);
        // 获取输入参数
        String brokerId = form.getBrokerId(); // 期货公司编号
        String userId = form.getUserId(); // 用户编号
        String systype = form.getSystype(); // 业务账户类型
        String businessType = form.getBusinessType(); // 业务类型
        String acceptNo = form.getAcceptNo(); // 受理单号
        String contactAddrProvince = form.getContactAddrProvince(); // 联系地址省
        String contactAddrCity = form.getContactAddrCity(); // 联系地址市
        String contactAddrDetail = form.getContactAddrDetail(); // 联系详细地址
        String degreeCode = form.getDegreeCode(); // 学历代码
        String professionCode = form.getProfessionCode(); // 职业代码
        String postCode = form.getPostCode(); // 邮政编码
        String email = form.getEmail(); // 电子邮箱
        String taxType = form.getTaxType(); // 税收类型
        String platformType = form.getPlatformType(); // 政要关系
        String badRecord = form.getBadRecord(); // 不良记录
        String termInvestment = form.getTermInvestment(); // 投资期限
        String investmentVariety = form.getInvestmentVariety(); // 投资品种
        String proceedsType = form.getProceedsType(); // 收益类型
        String conRelation = form.getConRelation(); // 控制关系
        String beneficiary = form.getBeneficiary(); // 受益人
        String dealType = form.getDealType(); // 交易类型

        String val1 = form.getVal1(); // 自定义字段1
        String val2 = form.getVal2(); // 自定义字段2
        String val3 = form.getVal3(); // 自定义字段3

        // 获取开关
        String tax_type_flag = ConfigUtil.get("user.taxtype.input", "0"); // 税收类型显示开关
        String platform_type_flag = ConfigUtil.get("user.platformtype.input", "0"); // 政要关系显示开关
        String bad_record_flag = ConfigUtil.get("user.badrecord.input", "0"); // 不良记录显示开关
        String term_investment_flag = ConfigUtil.get("user.terminvestment.input", "0"); // 投资期限显示开关
        String investment_variety_flag = ConfigUtil.get("user.investmentvariety.input", "0"); // 投资品种显示开关
        String proceeds_type_flag = ConfigUtil.get("user.proceedstype.input", "0"); // 收益类型显示开关
        String con_relation_flag = ConfigUtil.get("user.conrelation.input", "0"); // 控制关系显示开关
        String beneficiary_flag = ConfigUtil.get("user.beneficiary.input", "0"); // 受益人显示开关
        String deal_type_flag = ConfigUtil.get("user.dealtype.input", "0"); // 交易类型显示开关
        // 参数验证
        // 业务类型
        if (!businessType.equals(Constant.BUSINESS_TYPE_SIGN_FOURTEEN)
            && !businessType.equals(Constant.BUSINESS_TYPE_SIGN_THIRTEEN)
            && !businessType.equals(Constant.BUSINESS_TYPE_SIGN_EIGHTEEN)
            && !businessType.equals(Constant.BUSINESS_TYPE_SIGN_TWENTY_TWO)
            && !businessType.equals(Constant.BUSINESS_TYPE_SIGN_TWENTY_FOUR)
            && !businessType.equals(Constant.BUSINESS_TYPE_SIGN_EIGHTEEN)) {
            resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.BIZ_TYPE_NOTEXIISTS);
            return resultMap;
        }
        // 受理单号验证
        Map<String, Object> baseAcceptMap =
            userLocalCommonService.verificationBaseAccept(brokerId, userId, businessType, acceptNo);
        if (ValueUtil.getInt(baseAcceptMap, Fields.ERROR_NO, -1) != 0) {
            return baseAcceptMap;
        }
        BaseAccept baseAccept = (BaseAccept)baseAcceptMap.get("baseAccept");
        // 同步标识
        String synchronousFlag = baseAccept.getSynchronousFlag();
        // 联系地址省
        String provinceName = null;
        if (StringUtil.isBlank(contactAddrProvince)) {
            contactAddrProvince = "";
        }
        BaseDictionary baseDictionary5 =
            baseDictionaryAtom.getByDicAndSubEntry(Constant.DICT_ENTRY_CONTACT_ADDR_CODE, contactAddrProvince);
        if (baseDictionary5 == null) {
            resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_CONTACT_ADDR_PROVINCE_CODE_ERROR);
            return resultMap;
        }
        provinceName = baseDictionary5.getDictPrompt();
        // 联系地址市
        String cityName = null;
        if (StringUtil.isBlank(contactAddrCity)) {
            contactAddrCity = "";
        }
        BaseDictionary baseDictionary6 =
            baseDictionaryAtom.getByDicAndSubEntry(Constant.DICT_ENTRY_CONTACT_ADDR_CODE, contactAddrCity);
        if (baseDictionary6 == null) {
            resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_CONTACT_ADDR_CITY_CODE_ERROR);
            return resultMap;
        }
        cityName = baseDictionary6.getDictPrompt();
        // 联系详细地址
        if (StringUtil.isBlank(contactAddrDetail)) {
            contactAddrDetail = "";
        }
        String res8 = ValidateInfoUtil.validateAddress(contactAddrDetail, 3, 100);
        if (!res8.equals(TextConstant.SUCCESS)) {
            resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_CONTACT_ADDR_DETAIL_ERROR);
            return resultMap;
        }
        // 邮编校验
        if (StringUtil.isBlank(postCode)) {
            postCode = "";
        }
        String res5 = ValidateInfoUtil.validatePostCode(postCode);
        if (!res5.equals(TextConstant.SUCCESS)) {
            resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.HK_VALIDATE_POSTCODE_ERROR);
            return resultMap;
        }
        // 学历
        if (StringUtil.isBlank(degreeCode)) {
            degreeCode = "";
        }
        BaseDictionary baseDictionary1 =
            baseDictionaryAtom.getByDicAndSubEntry(Constant.DICT_ENTRY_DEGREE_CODE, degreeCode);
        if (baseDictionary1 == null) {
            resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_DEGREE_CODE_ERROR);
            return resultMap;
        }
        // 职业
        if (StringUtil.isBlank(professionCode)) {
            professionCode = "";
        }
        BaseDictionary baseDictionary2 =
            baseDictionaryAtom.getByDicAndSubEntry(Constant.DICT_ENTRY_PROFESSION_CODE, professionCode);
        if (baseDictionary2 == null) {
            resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_PROFESSION_CODE_ERROR);
            return resultMap;
        }
        // 邮箱校验
        if (StringUtil.isBlank(email)) {
            email = null;
        } else {
            String res4 = ValidateInfoUtil.validateEmail(email);
            if (!res4.equals(TextConstant.SUCCESS)) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.HK_VALIDATE_EMAIL_ERROR);
                return resultMap;
            }
        }
        // 税收类型
        if (tax_type_flag.equals("visible")) {
            if (StringUtil.isBlank(taxType)) {
                taxType = null;
            }
        } else {
            taxType = null;
        }
        // 政要关系
        if (platform_type_flag.equals("visible")) {
            if (StringUtil.isBlank(platformType)) {
                platformType = null;
            }
        } else {
            platformType = null;
        }
        // 投资期限
        if (term_investment_flag.equals("visible")) {
            if (StringUtil.isBlank(termInvestment)) {
                termInvestment = null;
            }
        } else {
            termInvestment = null;
        }
        // 收益类型
        if (proceeds_type_flag.equals("visible")) {
            if (StringUtil.isBlank(proceedsType)) {
                proceedsType = null;
            }
        } else {
            proceedsType = null;
        }
        // 控制关系
        if (con_relation_flag.equals("visible")) {
            if (StringUtil.isBlank(conRelation)) {
                conRelation = null;
            }
        } else {
            conRelation = null;
        }
        // 受益人
        if (beneficiary_flag.equals("visible")) {
            if (StringUtil.isBlank(beneficiary)) {
                beneficiary = null;
            }
        } else {
            beneficiary = null;
        }
        // 投资品种
        if (investment_variety_flag.equals("visible")) {
            if (StringUtil.isBlank(investmentVariety)) {
                investmentVariety = null;
            }
        } else {
            investmentVariety = null;
        }
        // 不良记录
        if (bad_record_flag.equals("visible")) {
            if (StringUtil.isBlank(badRecord)) {
                badRecord = null;
            }
        } else {
            badRecord = null;
        }
        // 交易类型
        if (deal_type_flag.equals("visible")) {
            if (StringUtil.isBlank(dealType)) {
                dealType = null;
            }
        } else {
            dealType = null;
        }
        // 验证用户编号
        if (StringUtil.isBlank(userId)) {
            resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.NO_SUCH_CLIENT);
        } else {
            // 1.查询 user_profile
            UserProfile userProfile = userProfileAtom.getUserProfileByUserId(new BigDecimal(userId));
            if (userProfile == null) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.NO_SUCH_CLIENT);
                return resultMap;
            }
            if (systype.equals(Constant.SYSTYPE_SIGN_ONE) || systype.equals(Constant.SYSTYPE_SIGN_TWO)) {
                // 2.查询 user_presence
                UserPresence userPresence = userPresenceAtom.getByUserId(brokerId, userId);
                if (userPresence == null) {
                    return ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_USER_PRESENCE_ERROR);
                } else {
                    // 记录变更数据
                    Map<String, Object> map = new LinkedHashMap();
                    boolean tz = false;
                    if (contactAddrProvince != null) {
                        map.put(Fields.CONTACT_ADDR_PROVINCE,
                            (userPresence.getProvince() == null ? "" : userPresence.getProvince()));
                        userPresence.setProvince(contactAddrProvince);
                        tz = true;
                    }
                    if (contactAddrCity != null) {
                        map.put(Fields.CONTACT_ADDR_CITY,
                            (userPresence.getCity() == null ? "" : userPresence.getCity()));
                        userPresence.setCity(contactAddrCity);
                        tz = true;
                    }
                    if (contactAddrDetail != null) {
                        map.put(Fields.CONTACT_ADDR_DETAIL,
                            (userPresence.getAddress() == null ? "" : userPresence.getAddress()));
                        userPresence.setAddress(contactAddrDetail);
                        tz = true;
                    }
                    if (degreeCode != null) {
                        map.put(Fields.DEGREE_CODE,
                            (userPresence.getDegreeCode() == null ? "" : userPresence.getDegreeCode()));
                        userPresence.setDegreeCode(degreeCode);
                        tz = true;
                    }
                    if (professionCode != null) {
                        map.put(Fields.PROFESSION_CODE,
                            (userPresence.getProfessionCode() == null ? "" : userPresence.getProfessionCode()));
                        userPresence.setProfessionCode(professionCode);
                        tz = true;
                    }
                    if (postCode != null) {
                        map.put(Fields.POST_CODE, (userPresence.getZipCode() == null ? "" : userPresence.getZipCode()));
                        userPresence.setZipCode(postCode);
                        tz = true;
                    }
                    if (email != null) {
                        map.put(Fields.EMAIL,
                            (userPresence.getMailAddress() == null ? "" : userPresence.getMailAddress()));
                        userPresence.setMailAddress(email);
                        tz = true;
                    } else {
                        map.put(Fields.EMAIL,
                            (userPresence.getMailAddress() == null ? "" : userPresence.getMailAddress()));
                        userPresence.setMailAddress("");
                    }
                    if (taxType != null) {
                        map.put(Fields.TAX_TYPE, (userPresence.getTaxType() == null ? "" : userPresence.getTaxType()));
                        userPresence.setTaxType(taxType);
                        tz = true;
                    }
                    if (platformType != null) {
                        map.put(Fields.PLATFORM_TYPE,
                            (userPresence.getPlatformType() == null ? "" : userPresence.getPlatformType()));
                        userPresence.setPlatformType(platformType);
                        tz = true;
                    }
                    if (termInvestment != null) {
                        map.put(Fields.TERM_INVESTMENT,
                            (userPresence.getTermInvestment() == null ? "" : userPresence.getTermInvestment()));
                        userPresence.setTermInvestment(termInvestment);
                        tz = true;
                    }
                    if (proceedsType != null) {
                        map.put(Fields.PROCEEDS_TYPE,
                            (userPresence.getProceedsType() == null ? "" : userPresence.getProceedsType()));
                        userPresence.setProceedsType(proceedsType);
                        tz = true;
                    }
                    if (conRelation != null) {
                        map.put(Fields.CON_RELATION,
                            (userPresence.getConRelation() == null ? "" : userPresence.getConRelation()));
                        userPresence.setConRelation(conRelation);
                        tz = true;
                    }
                    if (beneficiary != null) {
                        map.put(Fields.BENEFICIARY,
                            (userPresence.getBeneficiary() == null ? "" : userPresence.getBeneficiary()));
                        userPresence.setBeneficiary(beneficiary);
                        tz = true;
                    }
                    if (investmentVariety != null) {
                        map.put(Fields.INVESTMENT_VARIETY,
                            (userPresence.getInvestmentVariety() == null ? "" : userPresence.getInvestmentVariety()));
                        userPresence.setInvestmentVariety(investmentVariety);
                        tz = true;
                    }
                    if (badRecord != null) {
                        map.put(Fields.BAD_RECORD,
                            (userPresence.getBadRecord() == null ? "" : userPresence.getBadRecord()));
                        userPresence.setBadRecord(badRecord);
                        tz = true;
                    }
                    if (dealType != null) {
                        map.put(Fields.DEAL_TYPE,
                            (userPresence.getDealType() == null ? "" : userPresence.getDealType()));
                        userPresence.setDealType(dealType);
                        tz = true;
                    }
                    if (businessType.equals(Constant.BUSINESS_TYPE_SIGN_FOURTEEN)
                        || businessType.equals(Constant.BUSINESS_TYPE_SIGN_TWENTY_TWO)
                        || businessType.equals(Constant.BUSINESS_TYPE_SIGN_TWENTY_FOUR)) {
                        // 1.获取 受理单扩展信息表 数据
                        BaseAcceptInfo baseAcceptInfo =
                            baseAcceptInfoAtom.getBaseAcceptInfo(brokerId, userId, businessType, acceptNo);
                        if (baseAcceptInfo != null) {
                            String formerlyInfo = baseAcceptInfo.getFormerlyInfo();
                            Map<String, Object> fiMap = JsonUtil.jsonToLinkedHashMap(formerlyInfo);
                            if (fiMap.get(Fields.TAX_TYPE) != null) {
                                map.put(Fields.TAX_TYPE, fiMap.get(Fields.TAX_TYPE).toString()); // 税收类型
                            }
                            if (fiMap.get(Fields.PLATFORM_TYPE) != null) {
                                map.put(Fields.PLATFORM_TYPE, fiMap.get(Fields.PLATFORM_TYPE).toString()); // 政要关系
                            }
                            if (fiMap.get(Fields.TERM_INVESTMENT) != null) {
                                map.put(Fields.TERM_INVESTMENT, fiMap.get(Fields.TERM_INVESTMENT).toString()); // 投资期限
                            }
                            if (fiMap.get(Fields.PROCEEDS_TYPE) != null) {
                                map.put(Fields.PROCEEDS_TYPE, fiMap.get(Fields.PROCEEDS_TYPE).toString()); // 收益类型
                            }
                            if (fiMap.get(Fields.CON_RELATION) != null) {
                                map.put(Fields.CON_RELATION, fiMap.get(Fields.CON_RELATION).toString()); // 控制关系
                            }
                            if (fiMap.get(Fields.BENEFICIARY) != null) {
                                map.put(Fields.BENEFICIARY, fiMap.get(Fields.BENEFICIARY).toString()); // 受益人
                            }
                            if (fiMap.get(Fields.INVESTMENT_VARIETY) != null) {
                                map.put(Fields.INVESTMENT_VARIETY, fiMap.get(Fields.INVESTMENT_VARIETY).toString()); // 投资品种
                            }
                            if (fiMap.get(Fields.BAD_RECORD) != null) {
                                map.put(Fields.BAD_RECORD, fiMap.get(Fields.BAD_RECORD).toString()); // 不良记录
                            }
                            if (fiMap.get(Fields.DEAL_TYPE) != null) {
                                map.put(Fields.DEAL_TYPE, fiMap.get(Fields.DEAL_TYPE).toString()); // 交易类型
                            }
                        }
                    }
                    if (val1 != null) {
                        map.put(Fields.VAL1, (userPresence.getVal1() == null ? "" : userPresence.getVal1()));
                        userPresence.setVal1(val1);
                        tz = true;
                    }
                    if (val2 != null) {
                        map.put(Fields.VAL2, (userPresence.getVal2() == null ? "" : userPresence.getVal2()));
                        userPresence.setVal2(val2);
                        tz = true;
                    }
                    if (val3 != null) {
                        map.put(Fields.VAL3, (userPresence.getVal3() == null ? "" : userPresence.getVal3()));
                        userPresence.setVal3(val3);
                        tz = true;
                    }
                    if (!tz) {
                        return ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_USER_INFO_ERROR);
                    }
                    if (provinceName != null || cityName != null || contactAddrDetail != null) {
                        String pName = "";
                        if (provinceName == null) {
                            String p = userPresence.getProvince();
                            BaseDictionary baseDictionaryP =
                                baseDictionaryAtom.getByDicAndSubEntry(Constant.DICT_ENTRY_CONTACT_ADDR_CODE, p);
                            if (baseDictionaryP != null) {
                                pName = baseDictionaryP.getDictPrompt();
                            }
                        } else {
                            pName = provinceName;
                        }
                        String cName = "";
                        if (cityName == null) {
                            String c = userPresence.getCity();
                            BaseDictionary baseDictionaryC =
                                baseDictionaryAtom.getByDicAndSubEntry(Constant.DICT_ENTRY_CONTACT_ADDR_CODE, c);
                            if (baseDictionaryC != null) {
                                cName = baseDictionaryC.getDictPrompt();
                            }
                        } else {
                            cName = cityName;
                        }
                        String aName = "";
                        if (contactAddrDetail == null) {
                            String a = userPresence.getAddress();
                            if (a != null) {
                                aName = a;
                            }
                        } else {
                            aName = contactAddrDetail;
                        }
                        userPresence.setFullAddress(pName + cName + aName);
                    }
                    userPresence.setBrokerId(brokerId);
                    userPresence.setLastUpdateTime(new Date());
                    userPresence.setLastUpdateBy(new BigDecimal(userId));
                    // 3.存储数据
                    try {
                        userPresenceAtom.update(userPresence);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        return ReturnUtil.setErrorResult(resultMap, ErrorCode.COMMON_ERROR);
                    }
                    if (businessType.equals(Constant.BUSINESS_TYPE_SIGN_FOURTEEN)
                        || businessType.equals(Constant.BUSINESS_TYPE_SIGN_TWENTY_TWO)
                        || businessType.equals(Constant.BUSINESS_TYPE_SIGN_TWENTY_FOUR)) {
                        if (synchronousFlag.equals("1") || synchronousFlag.equals("2")) {
                            // 4.存储受理单扩展信息数据
                            userLocalCommonService.saveBaseAcceptInfo(brokerId, userId, businessType, acceptNo,
                                JSON.toJSONString(map));
                            baseAccept.setSynchronousFlag("3");
                            baseAcceptAtom.updateBaseAccept(baseAccept);
                        }
                    } else {
                        if (synchronousFlag.equals("1")) {
                            // 4.存储受理单扩展信息数据
                            userLocalCommonService.saveBaseAcceptInfo(brokerId, userId, businessType, acceptNo,
                                JSON.toJSONString(map));
                            baseAccept.setSynchronousFlag("2");
                            baseAcceptAtom.updateBaseAccept(baseAccept);
                        }
                    }
                }
                // }else if(systype.equals(Constant.SYSTYPE_SIGN_TWO)){
                // resultMap = ReturnUtil.setErrorResult(resultMap,ErrorCode.CC_VALIDATE_SYSTYPE_ERROR);
            } else {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_SYSTYPE_ERROR);
            }
        }
        return resultMap;
    }

    /**
     * 三方用户信息查询
     * 
     * @param form
     * @return
     */
    @Override
    public Map<String, Object> qryThdClientInfo(UserInfoForm form) {
        // 初始化返回值
        Map<String, Object> resultMap = ReturnUtil.setErrorResult(new HashMap<String, Object>(), ErrorCode.SUCCESS);
        // 获取输入参数
        String brokerId = form.getBrokerId();
        String userId = form.getUserId();
        String systype = form.getSystype();
        String opStation = form.getOpStation();
        String fundAccount = form.getFundAccount();
        String businessType = form.getBusinessType();
        String acceptNo = form.getAcceptNo();
        // 验证用户
        UserProfile userProfile = userProfileAtom.getUserProfileByUserId(new BigDecimal(userId));
        if (userProfile == null) {
            resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.NO_SUCH_CLIENT);
            return resultMap;
        }
        String idKind = userProfile.getIdentityType();
        String idNo = userProfile.getIdentityCode();
        // 封装调用三方参数
        Map<String, String> params = new HashMap<>();
        params.put(Fields.ID_NO, idNo);
        params.put(Fields.ID_KIND, idKind);
        params.put(Fields.BROKER_ID, brokerId);
        params.put(Fields.OP_STATION, opStation);
        params.put(Fields.FUND_ACCOUNT, fundAccount);
        params.put(Fields.SYSTYPE, systype);
        try {
            // 更新本地客户信息
            if (StringUtil.isBlank(acceptNo)) {
                // 调用三方获取客户信息
                Map<String, Object> mapClientInfo = counterService.qryClientInfo(params);
                String futures_account_data = JSON.toJSONString(
                    mapClientInfo.get("futures_account_data") == null ? "" : mapClientInfo.get("futures_account_data"));
                mapClientInfo.put("futures_account_data", futures_account_data);
                if (ValueUtil.getInt(mapClientInfo, Fields.ERROR_NO, -1) != 0) {
                    return mapClientInfo;
                }
                mapClientInfo.put(Fields.ID_NO, idNo);
                String idEnddate =
                    (mapClientInfo.get("id_enddate") != null) ? mapClientInfo.get("id_enddate").toString() : "";
                if (idEnddate.equals("-1")) {
                    mapClientInfo.put("id_enddate", "20991231");
                }
                return mapClientInfo;
            } else {
                // 业务类型 验证
                String res = ValidateInfoUtil.validateBusinessType(businessType);
                if (!res.equals(TextConstant.SUCCESS)) {
                    return ReturnUtil.setErrorResult(resultMap, ErrorCode.BIZ_TYPE_NOTEXIISTS);
                }
                // 受理单号验证
                Map<String, Object> baseAcceptMap =
                    userLocalCommonService.verificationBaseAccept(brokerId, userId, businessType, acceptNo);
                if (ValueUtil.getInt(baseAcceptMap, Fields.ERROR_NO, -1) != 0) {
                    return baseAcceptMap;
                }
                BaseAccept baseAccept = (BaseAccept)baseAcceptMap.get("baseAccept");
                String synchronousFlag =
                    (baseAccept.getSynchronousFlag() != null) ? baseAccept.getSynchronousFlag() : "0";
                if (!(synchronousFlag.equals("1") || synchronousFlag.equals("2") || synchronousFlag.equals("3"))) {
                    // 调用三方获取客户信息
                    Map<String, Object> mapClientInfo = counterService.qryClientInfo(params);
                    String futures_account_data = JSON.toJSONString(mapClientInfo.get("futures_account_data") == null
                        ? "" : mapClientInfo.get("futures_account_data"));
                    mapClientInfo.put("futures_account_data", futures_account_data);
                    if (ValueUtil.getInt(mapClientInfo, Fields.ERROR_NO, -1) != 0) {
                        return mapClientInfo;
                    }
                    mapClientInfo.put(Fields.ID_NO, idNo);
                    String clientName =
                        (mapClientInfo.get("client_name") != null) ? mapClientInfo.get("client_name").toString() : "";
                    String clientGender =
                        (mapClientInfo.get("gender") != null) ? mapClientInfo.get("gender").toString() : "";
                    String branchNo =
                        (mapClientInfo.get("branch_code") != null) ? mapClientInfo.get("branch_code").toString() : "";
                    String idAddress =
                        (mapClientInfo.get("id_addr") != null) ? mapClientInfo.get("id_addr").toString() : "";
                    String idBegindate =
                        (mapClientInfo.get("id_begindate") != null) ? mapClientInfo.get("id_begindate").toString() : "";
                    String idEnddate =
                        (mapClientInfo.get("id_enddate") != null) ? mapClientInfo.get("id_enddate").toString() : "";
                    String contactMobile = (mapClientInfo.get("contact_moblie") != null)
                        ? mapClientInfo.get("contact_moblie").toString() : "";// 联系手机
                    String contactNumber =
                        (mapClientInfo.get("telphone") != null) ? mapClientInfo.get("telphone").toString() : "";// 联系电话
                    String industyCode =
                        (mapClientInfo.get("industy_code") != null) ? mapClientInfo.get("industy_code").toString() : "";
                    String contactAddrProvince = (mapClientInfo.get("addr_province") != null)
                        ? mapClientInfo.get("addr_province").toString() : "";
                    String contactAddrCity =
                        (mapClientInfo.get("addr_city") != null) ? mapClientInfo.get("addr_city").toString() : "";
                    String contactAddrDetail =
                        (mapClientInfo.get("addr_address") != null) ? mapClientInfo.get("addr_address").toString() : "";
                    String degreeCode =
                        (mapClientInfo.get("education") != null) ? mapClientInfo.get("education").toString() : "";
                    String professionCode =
                        (mapClientInfo.get("occupation") != null) ? mapClientInfo.get("occupation").toString() : "";
                    String postCode =
                        (mapClientInfo.get("addr_zipcode") != null) ? mapClientInfo.get("addr_zipcode").toString() : "";
                    String email =
                        (mapClientInfo.get(Fields.EMAIL) != null) ? mapClientInfo.get(Fields.EMAIL).toString() : "";
                    String taxType = (mapClientInfo.get(Fields.TAX_TYPE) != null)
                        ? mapClientInfo.get(Fields.TAX_TYPE).toString() : "";
                    String platformType = (mapClientInfo.get(Fields.PLATFORM_TYPE) != null)
                        ? mapClientInfo.get(Fields.PLATFORM_TYPE).toString() : "";
                    String badRecord = (mapClientInfo.get(Fields.BAD_RECORD) != null)
                        ? mapClientInfo.get(Fields.BAD_RECORD).toString() : "";
                    String termInvestment = (mapClientInfo.get(Fields.TERM_INVESTMENT) != null)
                        ? mapClientInfo.get(Fields.TERM_INVESTMENT).toString() : "";
                    String investmentVariety = (mapClientInfo.get(Fields.INVESTMENT_VARIETY) != null)
                        ? mapClientInfo.get(Fields.INVESTMENT_VARIETY).toString() : "";
                    String proceedsType = (mapClientInfo.get(Fields.PROCEEDS_TYPE) != null)
                        ? mapClientInfo.get(Fields.PROCEEDS_TYPE).toString() : "";
                    String conRelation = (mapClientInfo.get(Fields.CON_RELATION) != null)
                        ? mapClientInfo.get(Fields.CON_RELATION).toString() : "";
                    String beneficiary = (mapClientInfo.get(Fields.BENEFICIARY) != null)
                        ? mapClientInfo.get(Fields.BENEFICIARY).toString() : "";
                    String dealType = (mapClientInfo.get(Fields.DEAL_TYPE) != null)
                        ? mapClientInfo.get(Fields.DEAL_TYPE).toString() : "";
                    String fund_account = (mapClientInfo.get(Fields.FUND_ACCOUNT) != null)
                        ? mapClientInfo.get(Fields.FUND_ACCOUNT).toString() : "";
                    String val1 =
                        (mapClientInfo.get(Fields.VAL1) != null) ? mapClientInfo.get(Fields.VAL1).toString() : "";
                    String val2 =
                        (mapClientInfo.get(Fields.VAL2) != null) ? mapClientInfo.get(Fields.VAL2).toString() : "";
                    String val3 =
                        (mapClientInfo.get(Fields.VAL3) != null) ? mapClientInfo.get(Fields.VAL3).toString() : "";
                    String thdReamrkInfo = (mapClientInfo.get(Fields.THD_REAMRK_INFO) != null)
                        ? mapClientInfo.get(Fields.THD_REAMRK_INFO).toString() : "";

                    // 416 488 增加扩展字段格式有误限制 先校验再保存
                    if (!"".equals(val1) && val1.split("\\$\\$", 3).length != 3) {
                        log.error("柜台扩展字段传输格式有误：val1=" + val1 + "  idNo=" + idNo);
                        return ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_EX_ERROR);
                    }
                    if (!"".equals(val2) && val2.split("\\$\\$", 3).length != 3) {
                        log.error("柜台扩展字段传输格式有误：val2=" + val2 + "  idNo=" + idNo);
                        return ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_EX_ERROR);
                    }
                    if (!"".equals(val3) && val3.split("\\$\\$", 3).length != 3) {
                        log.error("柜台扩展字段传输格式有误：val3=" + val3 + "  idNo=" + idNo);
                        return ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_EX_ERROR);
                    }

                    // 适当性信息 ======开始======
                    String adequacyType = (mapClientInfo.get(Fields.ADEQUACY_TYPE) != null)
                        ? mapClientInfo.get(Fields.ADEQUACY_TYPE).toString() : "";
                    String adeScore = (mapClientInfo.get(Fields.ADE_SCORE) != null)
                        ? mapClientInfo.get(Fields.ADE_SCORE).toString() : "";
                    String adeDate = (mapClientInfo.get(Fields.ADE_DATE) != null)
                        ? mapClientInfo.get(Fields.ADE_DATE).toString() : "";
                    String riskLevel = (mapClientInfo.get(Fields.RISK_LEVEL) != null)
                        ? mapClientInfo.get(Fields.RISK_LEVEL).toString() : "";
                    // 与柜台适当性类型相反
                    if ("1".equals(adequacyType)) {
                        adequacyType = "2";
                    } else if ("2".equals(adequacyType)) {
                        adequacyType = "1";
                    }
                    Map<String, Object> csaiMap =
                        userLocalCommonService.clientSynchroAdeInfo(userId, adequacyType, adeDate, riskLevel, adeScore);
                    if (ValueUtil.getInt(csaiMap, Fields.ERROR_NO, -1) != 0) {
                        return csaiMap;
                    }
                    // 适当性信息 ======结束======

                    if (StringUtil.isBlank(fund_account)) {
                        resultMap.put(Fields.ERROR_NO, -1);
                        // resultMap.put(Fields.ERROR_INFO,"柜台：客户信息查询获取资金账号或客户号失败。");
                        resultMap.put(Fields.ERROR_INFO, "柜台错误，请及时联系期货公司或柜台厂商处理:获取资金账号或客户号失败");
                        log.error("柜台错误：客户信息查询获取资金账号或客户号失败");
                    }
                    // 验证三方传递过来的营业部信息
                    BaseOrganization baseOrganization = baseOrganizationAtom.getByOrgCode(brokerId, branchNo);
                    if (baseOrganization == null) {
                        resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_ORG_CODE_ERROR);
                        String errorInfo = resultMap.get(Fields.ERROR_INFO).toString();
                        // resultMap.put(Fields.ERROR_INFO,"柜台："+errorInfo);
                        log.error("柜台错误：" + errorInfo);
                        resultMap.put(Fields.ERROR_INFO, "柜台错误，请及时联系期货公司或柜台厂商处理：" + errorInfo);
                        return resultMap;
                    }
                    // 验证三方传递过来的联系地址省
                    if (StringUtil.isBlank(contactAddrProvince)) {
                        contactAddrProvince = "";
                    }
                    BaseDictionary baseDictionary5 = baseDictionaryAtom
                        .getByDicAndSubEntry(Constant.DICT_ENTRY_CONTACT_ADDR_CODE, contactAddrProvince);
                    String contactAddrProvince_str = "";
                    if (baseDictionary5 == null) {
                        // resultMap = ReturnUtil.setErrorResult(resultMap,
                        // ErrorCode.CC_VALIDATE_CONTACT_ADDR_PROVINCE_CODE_ERROR);
                        // String errorInfo = resultMap.get(Fields.ERROR_INFO).toString();
                        // resultMap.put(Fields.ERROR_INFO,"柜台："+errorInfo);
                        // return resultMap;
                    } else {
                        contactAddrProvince_str = baseDictionary5.getDictPrompt();
                    }
                    // 验证三方传递过来的联系地址市
                    if (StringUtil.isBlank(contactAddrCity)) {
                        contactAddrCity = "";
                    }
                    BaseDictionary baseDictionary6 =
                        baseDictionaryAtom.getByDicAndSubEntry(Constant.DICT_ENTRY_CONTACT_ADDR_CODE, contactAddrCity);
                    String contactAddrCity_str = "";
                    if (baseDictionary6 == null) {
                        // resultMap = ReturnUtil.setErrorResult(resultMap,
                        // ErrorCode.CC_VALIDATE_CONTACT_ADDR_CITY_CODE_ERROR);
                        // String errorInfo = resultMap.get(Fields.ERROR_INFO).toString();
                        // resultMap.put(Fields.ERROR_INFO,"柜台："+errorInfo);
                        // return resultMap;
                    } else {
                        contactAddrCity_str = baseDictionary6.getDictPrompt();
                    }
                    // 业务逻辑代码
                    if (systype.equals(Constant.SYSTYPE_SIGN_ONE) || systype.equals(Constant.SYSTYPE_SIGN_TWO)) {
                        // 2.修改 user_id_info
                        UserIdInfo userIdInfo = userIdInfoAtom.getUserIdInfoById(userId);
                        if (userIdInfo == null) {
                            userIdInfo = new UserIdInfo();
                            userIdInfo.setUserId(new BigDecimal(userId));
                            userIdInfoAtom.save(userIdInfo);
                        }
                        userIdInfo.setUserName(clientName);
                        userIdInfo.setIdentityType(idKind); // 默认证件类型：身份证
                        userIdInfo.setIdentityCode(idNo);
                        userIdInfo.setNation(DicConstant.DEFUALT_NATION); // 默认国籍：中国
                        if (!businessType.equals(Constant.BUSINESS_TYPE_SIGN_SIXTEEN)
                            && !businessType.equals(Constant.BUSINESS_TYPE_SIGN_TWENTY_THREE)) {
                            userIdInfo.setAddress(idAddress);
                            userIdInfo.setDeliveryDate(
                                DateTimeUtil.toDateOrNull(idBegindate, DateTimeUtil.FORMAT_YYYYMMDD_NO_BREAK));
                            if (idEnddate.equals("-1")) {
                                userIdInfo.setDueDate(
                                    DateTimeUtil.toDateOrNull("20991231", DateTimeUtil.FORMAT_YYYYMMDD_NO_BREAK));
                                userIdInfo.setForeverFlag(new BigDecimal(TypeConstant.FOREVER_FLAG_YES));
                            } else {
                                userIdInfo.setDueDate(
                                    DateTimeUtil.toDateOrNull(idEnddate, DateTimeUtil.FORMAT_YYYYMMDD_NO_BREAK));
                                userIdInfo.setForeverFlag(new BigDecimal(TypeConstant.FOREVER_FLAG_NO));
                            }
                        }
                        userIdInfo.setGender(clientGender.toString());
                        String age = idNo.substring(idNo.length() - 12, idNo.length() - 4);
                        userIdInfo.setAge(new BigDecimal(age));
                        // 3.修改 user_presence
                        UserPresence userPresence = userPresenceAtom.getByUserId(brokerId, userId);
                        if (userPresence == null) {
                            return ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_USER_PRESENCE_ERROR);
                        }
                        userPresence.setBrokerId(brokerId);
                        userPresence.setUserName(clientName);
                        userPresence.setIdentityType(idKind); // 默认证件类型：身份证
                        userPresence.setIdentityCode(idNo);
                        userPresence.setOpenOrgId(new BigDecimal(baseOrganization.getOrgId()));
                        userPresence.setProvince(contactAddrProvince);
                        userPresence.setCity(contactAddrCity);
                        userPresence.setFullAddress(contactAddrProvince_str + contactAddrCity_str + contactAddrDetail);
                        userPresence.setAddress(contactAddrDetail);
                        if (!contactNumber.equals("")) {
                            userPresence.setTelphone(contactNumber); // 联系电话
                        }
                        userPresence.setZipCode(postCode);
                        userPresence.setDegreeCode(degreeCode);
                        userPresence.setProfessionCode(professionCode);
                        userPresence.setLastUpdateTime(new Date());
                        userPresence.setLastUpdateBy(new BigDecimal(userId));
                        userPresence.setIndustryCode(industyCode);
                        userPresence.setMailAddress(email);
                        userPresence.setContactMobile(contactMobile); // 联系手机
                        userPresence.setTaxType(taxType);
                        userPresence.setPlatformType(platformType);
                        userPresence.setBadRecord(badRecord);
                        userPresence.setTermInvestment(termInvestment);
                        userPresence.setInvestmentVariety(investmentVariety);
                        userPresence.setProceedsType(proceedsType);
                        userPresence.setConRelation(conRelation);
                        userPresence.setBeneficiary(beneficiary);
                        userPresence.setDealType(dealType);
                        userPresence.setChannel("");
                        if (val1.equals("")) {
                            userPresence.setVal1("$$$$");
                        } else {
                            userPresence.setVal1(val1);
                        }
                        if (val2.equals("")) {
                            userPresence.setVal2("$$$$");
                        } else {
                            userPresence.setVal2(val2);
                        }
                        if (val3.equals("")) {
                            userPresence.setVal3("$$$$");
                        } else {
                            userPresence.setVal3(val3);
                        }
                        userPresence.setCounterRemark(thdReamrkInfo);
                        // 5.存储数据

                        // user_id_info
                        userIdInfoAtom.update(userIdInfo);
                        // user_presence
                        userPresenceAtom.update(userPresence);
                        //
                        baseAccept.setSynchronousFlag("1");
                        baseAcceptAtom.updateBaseAccept(baseAccept);
                        //
                        // if (systype.equals(Constant.SYSTYPE_SIGN_ONE)) {
                        // 更新资金账号
                        UserAccounts userAccounts = userAccountsAtom.getCapitalAccounts(Long.valueOf(userId));
                        if (userAccounts == null) {
                            userAccounts = new UserAccounts();
                            userAccounts.setUserId(Long.valueOf(userId));
                            userAccounts.setInvsetType(String.valueOf(DicConstant.ACCOUNT_CAPITAL));
                            userAccounts.setAccountType(String.valueOf(DicConstant.ACCOUNT_CAPITAL));
                            userAccounts.setAccountNo(fund_account);
                            userAccounts.setAccountName("资金账号");
                            userAccounts.setBranchId(String.valueOf(userPresence.getOpenOrgId()));
                            userAccounts.setValidateFlag(new BigDecimal(2));
                            userAccounts.setErrorInfo("客户信息查询同步更新资金账号");
                            userAccounts.setApplySuccessFlag(new BigDecimal(0));
                            userAccounts.setStartTime(new Date());
                            userAccountsAtom.save(userAccounts);
                        } else {
                            userAccounts.setAccountNo(fund_account);
                            userAccounts.setApplySuccessFlag(new BigDecimal(0));
                            userAccounts.setErrorInfo("客户信息查询同步更新资金账号");
                            userAccounts.setEndTime(new Date());
                            userAccountsAtom.updateByUserAccounts(userAccounts);
                        }
                        // }
                        if (idEnddate.equals("-1")) {
                            mapClientInfo.put("id_enddate", "20991231");
                        }
                        resultMap.putAll(mapClientInfo);
                        // } else if (systype.equals(Constant.SYSTYPE_SIGN_TWO)) {
                        // resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_SYSTYPE_ERROR);
                    } else {
                        resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_SYSTYPE_ERROR);
                    }
                }
            }
        } catch (Exception e) {
            log.error("qryThdClientInfo异常" + e.getMessage(), e);
            return ReturnUtil.setErrorResult(resultMap, ErrorCode.COMMON_ERROR);
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> qryCertInfo(CertForm form) {
        Map<String, Object> result = ReturnUtil.setErrorResult(new HashMap<String, Object>(), ErrorCode.SUCCESS);
        String brokerId = form.getBrokerId();
        String userId = form.getUserId();
        String device = form.getDevice();// 设备来源
        if (StringUtil.isBlank(userId)) {// 用户不存在
            return ReturnUtil.setErrorResult(result, ErrorCode.NO_SUCH_CLIENT);
        }
        UserPresence userPresence = userPresenceAtom.getByUserId(brokerId, userId);
        if (userPresence == null) {
            return ReturnUtil.setErrorResult(result, ErrorCode.NO_SUCH_CLIENT);
        }

        // 判断是否国密证书
        String enableGM = ConfigUtil.get("enable.gm.sign", "invisible");
        if ("visible".equals(enableGM)) {
            result.put("enable_gm", "1");
            String gm_mAuth_url = ConfigUtil.get("gm_mAuth_url", "");
            String gm_mAuth_cert_path = ConfigUtil.get("gm_mAuth_https_cert", "");
            String gm_mAuth_https_base64Cert = "";
            try {
                if (!StringUtil.isEmpty(gm_mAuth_cert_path)) {
                    File file = new File(gm_mAuth_cert_path);
                    if (file.exists()) {
                        gm_mAuth_https_base64Cert =
                            Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(file));
                    } else {
                        log.error("mAuth证书不存在：" + gm_mAuth_cert_path);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            result.put("gm_mAuth_url", gm_mAuth_url);
            result.put("gm_mAuth_base64_cert", gm_mAuth_https_base64Cert);
            String gm_app_id = "";
            String gm_secretKey_key = "";
            if ("1".equals(device)) {// Android
                gm_app_id = ConfigUtil.get("gm_android_appId", "");
                gm_secretKey_key = ConfigUtil.get("gm_android_secretKey", "");
            } else if ("2".equals(device)) {// ios
                gm_app_id = ConfigUtil.get("gm_ios_appId", "");
                gm_secretKey_key = ConfigUtil.get("gm_ios_secretKey", "");
            } else {// pc

            }
            result.put("gm_app_id", gm_app_id);
            result.put("gm_secret_key", gm_secretKey_key);
            result.put("gm_username", "u" + brokerId + userPresence.getIdentityCode());
        } else {
            result.put("enable_gm", "0");
        }
        UserCaVerifyWithBLOBs userCa = userCaVerifyAtom.getByUserIdAndType(userId, MiddleConstants.CA_TYPE_ZJ + "");
        if (userCa == null) {
            return ReturnUtil.setErrorResult(result, ErrorCode.CERT_NOT_APPLY);
        }
        result.put(Fields.CSDC_CERT_SN, userCa.getCaSn());
        result.put(Fields.CERT_SETUP_FLAG, userCa.getSetupFlag());
        result.put(Fields.CERT_SETUP_TIME, userCa.getSetupTime());

        return result;
    }

    @Override
    public Map<String, Object> setCertInfo(CertForm form) {
        Map<String, Object> result = ReturnUtil.setErrorResult(new HashMap<String, Object>(), ErrorCode.SUCCESS);
        String brokerId = form.getBrokerId();
        String userId = form.getUserId();
        if (StringUtil.isBlank(userId)) {// 用户不存在
            return ReturnUtil.setErrorResult(result, ErrorCode.NO_SUCH_CLIENT);
        }
        UserPresence userPresence = userPresenceAtom.getByUserId(brokerId, userId);
        if (userPresence == null) {
            return ReturnUtil.setErrorResult(result, ErrorCode.NO_SUCH_CLIENT);
        }
        // 自研sdk重置证书，需修改到视频
        String registerWay = userPresence.getRegisterWay();
        if ("11".equals(registerWay) || "15".equals(registerWay) || "16".equals(registerWay)) {
            String acceptNo = form.getAcceptNo();
            if (StringUtil.isBlank(acceptNo)) {
                return ReturnUtil.setErrorResult(result, ErrorCode.HK_ACCEPT_N0_ERROR);
            }
            BaseAccept acceptByAcceptNo = baseAcceptAtom.getBaseAcceptByAcceptNo(acceptNo);
            String sdkFlow = acceptByAcceptNo.getSdkFlow();
            if (!StringUtil.isBlank(sdkFlow)) {
                char[] status = sdkFlow.toCharArray();
                status[0] = '0';
                status[1] = '0';
                String ss = Arrays.toString(status).replaceAll("[\\[\\]\\s,]", "");
                acceptByAcceptNo.setSdkFlow(ss);
                baseAcceptAtom.updateBaseAccept(acceptByAcceptNo);
            }
        }
        UserCaVerifyWithBLOBs userCa = userCaVerifyAtom.getByUserIdAndType(userId, MiddleConstants.CA_TYPE_ZJ + "");
        if (userCa == null) {
            return ReturnUtil.setErrorResult(result, ErrorCode.CERT_NOT_APPLY);
        }
        String certDn = form.getCertDn();
        String certBase64 = form.getCertBase64();
        String certSn = form.getCertSn();
        String certEndTime = form.getCertEndTime();
        if (!StringUtil.isEmpty(certDn)) {
            userCa.setCaDn(certDn);
        }
        if (!StringUtil.isEmpty(certBase64)) {
            // 设置国密证书
            userCa.setCaCert(certBase64);
            userCa.setSetupFlag(new BigDecimal(1));
            userCa.setSetupTime(new Date());
        }
        if (!StringUtil.isEmpty(certSn)) {
            userCa.setCaSn(certSn);
        } else {
            userCa.setCaSn(userCa.getCaSn() + "_bak");
        }
        if (!StringUtil.isEmpty(certEndTime)) {
            Date parse = DateUtil.parse(certEndTime, "yyyy年MM月dd日 HH:mm:ss");
            userCa.setCertEndTime(parse);
        }
        userCaVerifyAtom.updateUserCaVerify(userCa);
        return result;
    }

    @Override
    public Map<String, Object> quickLogin(LoginForm form) {
        // 初始化返回值
        Map<String, Object> resultMap = ReturnUtil.setErrorResult(new HashMap<String, Object>(), ErrorCode.SUCCESS);

        // 获取数据
        String brokerId = form.getBrokerId();
        String mobile = form.getLogonCode();
        String validate_code = form.getValidateCode();
        String business_type = form.getBusinessType();
        String channel = form.getChannel();
        String register_way = form.getRegisterWay();
        String ip = form.getOpStation();
        String mac_addr = form.getMacAddr();
        String phone_type = form.getPhoneType();
        String phone_version = form.getPhoneVersion();
        String appSdkVersion = form.getAppSdkVersion();
        String idNo = form.getIdNo() == null ? "" : form.getIdNo();
        Date loginTime = new Date();
        String orgId = form.getOrgId();
        String clientIp = form.getClientIp() == null ? "" : form.getClientIp();

        String enableGM = ConfigUtil.get("enable.gm.sign", "invisible");
        String gmOTP = ConfigUtil.get("gm_otp", "invisible");

        if (StringUtil.isBlank(validate_code)) {
            resultMap.put(Fields.ERROR_INFO, "您录入的动态验证码不能为空");
            resultMap.put(Fields.ERROR_NO, -1);
            return resultMap;
        }
        if (StringUtil.isBlank(clientIp)) {
            return VOUtil.genErrorResult(ErrorCode.VALIDATECODE_NO_IP);
        }

        try {
            // 增加用户注册来源区分
            if (StringUtil.isBlank(register_way)) {
                log.error("注册方式为空,手机号是：" + mobile);
                resultMap.put(Fields.ERROR_NO, -1001);
                resultMap.put(Fields.ERROR_INFO, "接入方式为空，请重新操作!");
                return resultMap;
            }

            // 判断是否为已注册用户
            UserProfile userProfile = userProfileAtom.getByMobileBrokerid(mobile, DataContext.getBrokerId());
            int validateCodeType = TypeConstant.VALIDATECODE_TYPE_KH_REGISTER;
            if (null != userProfile) { // 登录
                validateCodeType = TypeConstant.VALIDATECODE_TYPE_KH_LOGIN;
                String identityCode = userProfile.getIdentityCode() == null ? "" : userProfile.getIdentityCode();
                if (!idNo.equals("") && !identityCode.equals("") && !identityCode.equals(idNo)) {
                    return VOUtil.genErrorResult(ErrorCode.LOGIN_ERROR);
                }
            }

            // 暂不使用前台传入的otpSN 使用数据库绑定的sn
            UserOtp userOtp = userOTpAtom.getUserOtp(brokerId, idNo);
            // otp打开时，对于不在OTP名单内的开立用户，走的是短信认证
            boolean isBusSms = false;
            if (("11".equals(business_type) || "12".equals(business_type)) && userOtp == null) {
                isBusSms = true;
            }
            if ("visible".equals(enableGM) && "visible".equals(gmOTP) && !isBusSms) {
                // 启用国密和otp
                if (userOtp == null || StringUtil.isEmpty(userOtp.getOtpSn())) {
                    return VOUtil.genErrorResult(ErrorCode.VALIDATECODE_OTP_NO_USER);
                }
                boolean otp = NetPassUtil.getInstance().checkOtpCode(userOtp.getOtpSn(), validate_code);
                if (!otp) {
                    return VOUtil.genErrorResult(ErrorCode.VALIDATECODE_OTP_NO_VERIFY);
                }
            } else {
                // 校验短信验证码
                // 登入后短信验证码需要改
                ValidateCode validateCode = validateCodeAtom.getByTypeAndMphone(validateCodeType, mobile,
                    TypeConstant.VALIDATECODE_STATUS_LOGIN);
                if (validateCode != null) {
                    String validatecodeContent = validateCode.getValidatecodeContent();
                    if (validate_code.equals(validatecodeContent)
                        || validate_code.equals(ConfigUtil.get("sms.verify.code", "").trim())) {
                        Date createTime = validateCode.getCreateTime();
                        long minutes = DateTimeUtil.getDatePoor(new Date(), createTime);
                        long effectiveTime = ConfigUtil.getSmsCodeEffectiveTime();
                        if (minutes >= effectiveTime) {
                            // 一小时重试次数
                            int dailyThreshold = Integer.valueOf(ConfigUtil.get("verify.code.error.times", "3"));
                            if (userLocalCommonService.codeSendTimesOverLimit(mobile, dailyThreshold)) {// 每小时错误次数超过限制
                                resultMap.put(Fields.ERROR_INFO, "您已重试超过" + dailyThreshold + "次,请一小时后再重新登录");
                                resultMap.put(Fields.ERROR_NO, -1);
                                return resultMap;
                            }
                            return VOUtil.genErrorResult(ErrorCode.VALIDATECODE_OUT_OF_TIME);
                        }
                        // 如果是国密短信需要验证国密短信系统
                        if ("visible".equals(ConfigUtil.get("gm_sms_validate", ""))
                            && "visible".equals(ConfigUtil.get("enable.gm.sign", ""))
                            && !(validate_code.equals(ConfigUtil.get("sms.verify.code", "").trim()))) {
                            // 验证国密短信
                            boolean gmSms =
                                NetPassUtil.getInstance().checkSmsCode(validateCode.getCardSn(), validate_code);
                            if (!gmSms) {
                                return VOUtil.genErrorResult(ErrorCode.VALIDATECODE_NO_VERIFY);
                            }
                        }
                    } else {
                        // 一小时重试次数
                        int dailyThreshold = Integer.valueOf(ConfigUtil.get("verify.code.error.times", "3"));
                        if (userLocalCommonService.codeSendTimesOverLimit(mobile, dailyThreshold)) {// 每小时错误次数超过限制
                            resultMap.put(Fields.ERROR_INFO, "您已重试超过" + dailyThreshold + "次,请一小时后再重新登录");
                            resultMap.put(Fields.ERROR_NO, -1);
                            return resultMap;
                        }
                        resultMap.put(Fields.ERROR_INFO, "您录入的动态验证码有误,请重试");// 新需求提示:20181030
                        resultMap.put(Fields.ERROR_NO, -1);
                        return resultMap;
                    }
                    validateCode.setValidatecodeStatus(new BigDecimal(1));
                    validateCodeAtom.updateByValidateCode(validateCode);
                } else {
                    return VOUtil.genErrorResult(ErrorCode.VALIDATECODE_GET_AGAIN);
                }
            }

            // 保存用户信息
            Map<String, Object> resMap = new HashMap<String, Object>();
            Boolean xzFlag = true;
            if (null != userProfile) {
                userProfile.setLastLoginTime(new Date());
                String identityCode = userProfile.getIdentityCode() == null ? "" : userProfile.getIdentityCode();
                if (identityCode.equals("") && !idNo.equals("")) {
                    userProfile.setIdentityType("1");
                    userProfile.setIdentityCode(idNo);
                }
                userProfile.setLastIp(clientIp);
                userProfile.setBusinessType(business_type);// 用户最后一次登录所做的业务
                userProfileAtom.updateByPrimaryKey(userProfile);
            } else { // 注册
                // ===================开始=================== 获取账户状态
                String systype = "";
                if (business_type.equals(Constant.BUSINESS_TYPE_SIGN_ELEVEN)) {
                    systype = "1";
                } else {
                    systype = "2";
                }
                Map<String, Object> rqtfaMap =
                    userLocalCommonService.qryThdFundAccount(brokerId, systype, "1", idNo, "", "", false, "");
                if (ValueUtil.getInt(rqtfaMap, Fields.ERROR_NO, -1) != 0) {
                    if (ValueUtil.getInt(rqtfaMap, Fields.ERROR_NO, -1) == -703) {
                        // 同一个身份证 有多个用户 不同手机号 处理方案
                        userProfile = userLocalCommonService.getUserProfile(form.getBrokerId(), "1", idNo, mobile);
                        if (userProfile != null) {
                            xzFlag = false;
                            userProfile.setMobile(mobile);
                            userProfile.setLastIp(clientIp);
                            userProfile.setBusinessType(business_type);// 用户最后一次登录所做的业务
                            userProfileAtom.update(userProfile);
                        }
                    } else {
                        return rqtfaMap;
                    }
                } else { // 校验是否是非开户或销户
                    List<Map<String, Object>> account_info =
                        JsonUtil.jsonToList(String.valueOf(rqtfaMap.get("account_info")));
                    Map<String, Object> accountMap = new HashMap();
                    if (account_info.size() > 0) {
                        accountMap = account_info.get(0);
                    }
                    if (!"N".equals(accountMap.get("account_status").toString())
                        && !"C".equals(accountMap.get("account_status").toString())) {
                        resultMap.put(Fields.ERROR_NO, -1001);
                        if (systype.equals("2")) {
                            resultMap.put(Fields.ERROR_INFO, "您已注册过资管账户，不能重复开户，可直接选择办理其它业务。如有疑问请联系期货公司");
                        } else {
                            resultMap.put(Fields.ERROR_INFO, "您已注册过期货账户，不能重复开户，可直接选择办理其它业务。如有疑问请联系期货公司");
                        }
                        return resultMap;
                    }
                }
                // // 校验身份证号是否存在用户
                // List<UserProfile> profiles = userProfileAtom.getUserProfilesByIDC(DataContext.getBrokerId(), "1",
                // idNo);
                // if ((profiles != null && profiles.size()>0) && userProfile==null){
                // //校验手机号不一致，否则userProfile不为空
                // userProfile = profiles.get(0);
                // xzFlag = false;
                // userProfile.setMobile(mobile);
                // userProfile.setBusinessType(business_type);//用户最后一次登录所做的业务
                // userProfileAtom.update(userProfile);
                // }

                if (xzFlag) {
                    userProfile = new UserProfile();
                    userProfile.setCreateTime(new Date());
                    userProfile.setStatus(new BigDecimal(StatusConstant.USER_STATUS_NORMAL));
                    userProfile.setLastLoginTime(new Date());
                    userProfile.setMobile(mobile);
                    userProfile.setBusinessType(business_type);
                    userProfile.setIsacceptno("0");
                    userProfile.setLastIp(clientIp);
                    if (!idNo.equals("")) {
                        userProfile.setIdentityType("1");
                        userProfile.setIdentityCode(idNo);
                    }
                    userProfile.setBrokerId(DataContext.getBrokerId());
                    userProfileAtom.save(userProfile);
                }
                // ===================结束===================
            }
            String userId = userProfile.getUserId().toString();
            UserPresence userPresence = userPresenceAtom.getByUserId(brokerId, userId);
            if (null == userPresence) {
                userPresence = new UserPresence();
                userPresence.setUserId(userProfile.getUserId());
                userPresence.setStatus(new BigDecimal(StatusConstant.ACCOUNT_REGISTER));
                userPresence.setUserStatus(new BigDecimal(StatusConstant.USER_STATUS_REGISTER));
                String flowStatus = "000000000010";
                userPresence.setUserFlowStatus(flowStatus);
                userPresence.setCreateTime(new Date());
                userPresence.setOpenType("9");
                userPresence.setBrokerId(form.getBrokerId());
                userPresence.setChannel(channel);
                userPresence.setAdequacyType("0");
                userPresence.setAccessWay(register_way);
            }
            userPresence.setMobile(mobile);
            userPresence.setLastUpdateTime(new Date());
            userPresence.setLastUpdateBy(userProfile.getUserId());
            if (!xzFlag) {
                userPresence.setTelphone(mobile);
                userPresence.setContactMobile(mobile);
            }
            // 接入方式传过来
            userPresence.setRegisterWay(register_way);
            // Integer opentimes =(userPresence.getOpenTimes()==null?0:userPresence.getOpenTimes()).intValue();
            // 二次开户修改用户流水控制位
            // if ("102001110110".equals(userPresence.getUserFlowStatus())||2 == opentimes) {
            // userPresence.setOpenTimes(new BigDecimal(2));
            // resMap.put("times", "second");
            // }

            if (null == userPresence.getId()) {
                userPresenceAtom.save(userPresence);
            } else {
                userPresenceAtom.update(userPresence);
            }
            FmJceUserPresence.desUserPresence(userPresence);
            // 保存手机信息
            if (!StringUtil.isBlank(phone_type) || !StringUtil.isBlank(phone_version)) {
                PhoneInfo phoneInfo = new PhoneInfo();
                phoneInfo.setBrokerId(DataContext.getBrokerId());
                phoneInfo.setMobile(mobile);
                phoneInfo.setPhoneType(phone_type);
                phoneInfo.setPhoneVersion(phone_version);
                phoneInfo.setVersion(appSdkVersion);
                phoneInfo.setRegisterWay(register_way);
                phoneInfo.setCreateTime(DateUtil.format(loginTime, DateUtil.DATE_TIME_FORMAT));
                phoneInfoAtom.save(phoneInfo);
            }

            // 保存用户登入信息日志
            successUserLogAtom.SaveSuccessUserinformation(register_way, loginTime, mobile, phone_type, ip, mac_addr, "",
                "", "");

            // log日志
            StringBuffer logInfo = new StringBuffer();
            logInfo.append("【登录成功手机号mphone：" + mobile + "】");
            logInfo.append("【登录时间loginTime：" + DateUtil.format(loginTime, DateUtil.DATE_TIME_FORMAT) + "】");
            logInfo.append("【业务类型business_type：" + business_type + "】");
            log.info(logInfo);
            resultMap.put(Fields.USER_ID, userId);
            resultMap.put(Fields.STATUS, userPresence.getStatus().toString());
            if (StringUtil.isBlank(userPresence.getUserName())) {
                resultMap.put(Fields.CLIENT_NAME, mobile);
            } else {
                resultMap.put(Fields.CLIENT_NAME, userPresence.getUserName());
            }
            // 处理渠道问题
            userLocalCommonService.setChannel(brokerId, userId, channel, userPresence, orgId);
            resultMap.put(Fields.CHANNEL, channel);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ReturnUtil.setErrorResult(resultMap, ErrorCode.COMMON_ERROR);
        }
        return resultMap;
    }

    @Override
    @Met
    public Map<String, Object> qryThdSmsCode(SmsForm form) {
        // 初始化返回值
        Map<String, Object> resultMap = ReturnUtil.setErrorResult(new HashMap<String, Object>(), ErrorCode.SUCCESS);
        try {
            // 校验是否交易日
            Map<String, Object> result = tTradedateAtom.isTrade();
            if (!"0".equals(result.get(Fields.ERROR_NO))) {
                result.put(Fields.ERROR_NO, -1098);
                return result;
            } else {
                result.put(Fields.ERROR_NO, 0);
            }
            // 获取输入参数
            String id_no = form.getIdNo();
            String id_kind = form.getIdKind();
            String broker_id = form.getBrokerId();
            String systype = form.getSystype();
            String businessType = form.getBusinessType();
            String pageFlow = form.getPageFlow();
            if (StringUtil.isBlank(id_no)) {
                resultMap.put(Fields.ERROR_NO, -1);
                resultMap.put(Fields.ERROR_INFO, "身份证号码不能为空");
                log.error("id_no入参错误!id_no=[" + id_no + "]");
                return resultMap;
            }
            if (StringUtil.isBlank(id_kind)) {
                resultMap.put(Fields.ERROR_NO, -1);
                resultMap.put(Fields.ERROR_INFO, "证件类型获取失败");
                log.error("id_kind入参错误!id_kind=[" + id_kind + "]");
                return resultMap;
            }
            // 身份证号校验
            String res = ValidateInfoUtil.validateCID(id_no);
            if (!res.equals(TextConstant.SUCCESS)) {
                // resultMap = ReturnUtil.setErrorResult(resultMap,ErrorCode.HK_VALIDATE_CID_ERROR);
                // return resultMap;
                resultMap.put(Fields.ERROR_NO, -1);
                resultMap.put(Fields.ERROR_INFO, res);
                return resultMap;
            }
            if (StringUtil.isBlank(broker_id)) {
                resultMap.put(Fields.ERROR_NO, -1);
                resultMap.put(Fields.ERROR_INFO, "期货公司编号未获取,请刷新浏览器再次登录");
                log.error("broker_id入参错误!broker_id=[" + broker_id + "]");
                return resultMap;
            }
            Map<String, String> qryThdSmsCodeParams = new HashMap<>();
            qryThdSmsCodeParams.put(Fields.BROKER_ID, broker_id);
            qryThdSmsCodeParams.put(Fields.ID_KIND, id_kind);
            qryThdSmsCodeParams.put(Fields.ID_NO, id_no);
            qryThdSmsCodeParams.put(Fields.OP_STATION, form.getOpStation());
            qryThdSmsCodeParams.put(Fields.SYSTYPE, systype);
            resultMap = counterService.qryThdSmsCode(qryThdSmsCodeParams);
            int errorNo = (int)resultMap.get(Fields.ERROR_NO);
            if (errorNo == 0) {
                String mobile = "";
                String sms_code = "";
                String account_status = "";
                boolean is_send = true;
                String error_info = "";
                if (resultMap.get("account_info") != null) {
                    List<Map<String, Object>> account_info =
                        JsonUtil.jsonToList(String.valueOf(resultMap.get("account_info")));
                    if (account_info != null && account_info.size() > 0
                        && account_info.get(0).get("telPhone") != null) {// 必须返回手机号
                        mobile = account_info.get(0).get("telPhone") == null ? ""
                            : account_info.get(0).get("telPhone").toString();
                        sms_code = account_info.get(0).get("smsCode") == null ? ""
                            : account_info.get(0).get("smsCode").toString();
                        account_status = account_info.get(0).get("account_status") == null ? ""
                            : account_info.get(0).get("account_status").toString();
                    }
                }
                if (account_status.equals("N")) {
                    resultMap = VOUtil.setErrorResult(resultMap, ErrorCode.WEB_ACCOUNT_STATUS_N_ID_ERROR);
                    if (systype.equals("2")) {
                        resultMap.put(Fields.ERROR_INFO, "您未开通资管账户，请先开通账户业务，如有疑问请联系期货公司咨询");
                    }
                } else if (account_status.equals("C")) {
                    resultMap = VOUtil.setErrorResult(resultMap, ErrorCode.WEB_ACCOUNT_STATUS_C_ID_ERROR);
                } else if (account_status.equals("D")) {
                    resultMap = VOUtil.setErrorResult(resultMap, ErrorCode.WEB_ACCOUNT_STATUS_D_ID_ERROR);
                    if (systype.equals("2")) {
                        resultMap.put(Fields.ERROR_INFO, "您已开通资管账户，账户已冻结，不允许使用系统，如有疑问请联系期货公司咨询");
                    }
                } else {
                    if (account_status.equals("S") && businessType.equals(Constant.BUSINESS_TYPE_SIGN_THIRTEEN)) {
                        resultMap = VOUtil.setErrorResult(resultMap, ErrorCode.WEB_ACCOUNT_STATUS_S_ID_ERROR);
                    }
                }
                if ((int)resultMap.get(Fields.ERROR_NO) != 0) {
                    return resultMap;
                }
                if (StringUtil.isBlank(mobile)) {// 未获取手机号
                    resultMap.put(Fields.ERROR_NO, ErrorCode.THD_VALIDATE_MOBILE.getCode());
                    resultMap.put(Fields.ERROR_INFO,
                        "您预留的手机号错误，如有疑问请联系期货公司咨询，联系电话" + ConfigUtil.get("security.servicephone"));
                    return resultMap;
                }
                if (StringUtil.isBlank(sms_code)) {// 判断柜台内部发送短信,不调用我们开户云
                    return resultMap;
                } else if (!TextConstant.SUCCESS.equals(ValidateInfoUtil.validateMobilePhone(mobile))) {// 手机号校验错误,取消发送短信
                    resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.HK_VALIDATE_MOBILE_PHONE_FORMAT_ERROR);
                    return resultMap;
                }
                String sms_switch = ConfigUtil.get("sms.switch", "1");// 默认获取不到发送.
                if (sms_switch.equals("invisible")) {
                    sms_switch = "0";
                } else {
                    sms_switch = "1";
                }
                if (sms_switch.equals("0")) {// 提示短信发送开关关闭
                    resultMap.put(Fields.ERROR_NO, -1);
                    resultMap.put(Fields.ERROR_INFO, "期货公司配置:短信发送开关状态为关闭");
                    return resultMap;
                }
                // 调用内部发送短信
                Map<String, Object> smsMap =
                    userLocalCommonService.sendValidateCode(mobile, sms_code, "3", businessType, pageFlow);
                if ("0".equals(smsMap.get(Fields.ERROR_NO).toString())) {
                    return resultMap;
                } else {
                    return smsMap;
                }
            } else {
                String errorInfo = "";
                // 姓名、身份证号码、账户类型有误
                if (ErrorCode.THD_VALIDATE_NAME.getCode() == errorNo || ErrorCode.THD_VALIDATE_IDNO.getCode() == errorNo
                    || ErrorCode.THD_VALIDATE_FUNDACCOUNT.getCode() == errorNo) {
                    errorInfo = "您的登录信息有误或账户不存在，如有疑问请联系期货公司咨询，联系电话" + ConfigUtil.get("security.servicephone");// 期货公司服务热线
                    resultMap.put(Fields.ERROR_INFO, errorInfo);
                } else if (ErrorCode.THD_VALIDATE_MOBILE.getCode() == errorNo) {
                    errorInfo = "您预留的手机号错误，如有疑问请联系期货公司咨询，联系电话" + ConfigUtil.get("security.servicephone");// 期货公司服务热线
                    resultMap.put(Fields.ERROR_INFO, errorInfo);
                }
            }
        } catch (Exception e) {
            log.error("获取短信验证码接口异常：" + e.getMessage(), e);
            return ReturnUtil.setErrorResult(resultMap, ErrorCode.COMMON_ERROR);
        }
        // if(resultMap.get("account_info")!= null){
        // resultMap.put("account_info",String.valueOf(resultMap.get("account_info")));
        // }
        return resultMap;
    }

    @Override
    public Map<String, Object> validateThdSmsCode(SmsForm form) {
        // 初始化返回值
        Map<String, Object> resultMap = ReturnUtil.setErrorResult(new HashMap<String, Object>(), ErrorCode.SUCCESS);
        // 获取输入参数
        String id_no = form.getIdNo();
        String id_kind = form.getIdKind();
        String broker_id = form.getBrokerId();
        String sms_code = form.getSmsCode();
        String mobile = form.getMobile();
        String op_station = form.getOpStation();
        String systype = form.getSystype();
        String business_type = form.getBusinessType();
        String register_way = form.getRegisterWay();
        String channel = form.getChannel();
        String phone_type = form.getPhoneType();
        String phone_version = form.getPhoneVersion();
        String appSdkVersion = form.getAppSdkVersion();
        String mac_addr = form.getMacAddr();
        String clientIp = form.getClientIp();

        if (StringUtil.isBlank(clientIp)) {
            return ReturnUtil.setErrorResult(resultMap, ErrorCode.VALIDATECODE_NO_IP);
        }

        if (StringUtil.isBlank(register_way)) {
            log.error("接入方式为空,身份证号是：" + id_no);
            resultMap.put(Fields.ERROR_NO, -1001);
            resultMap.put(Fields.ERROR_INFO, "接入方式为空，请重新操作!");
            return resultMap;
        }

        if (StringUtil.isBlank(id_no)) {
            resultMap.put(Fields.ERROR_NO, -1);
            resultMap.put(Fields.ERROR_INFO, "身份证号码不能为空");
            log.error("id_no入参错误!id_no=[" + id_no + "]");
            return resultMap;
        }
        if (StringUtil.isBlank(id_kind)) {
            resultMap.put(Fields.ERROR_NO, -1);
            resultMap.put(Fields.ERROR_INFO, "证件类型错误");
            log.error("id_kind入参错误!id_kind=[" + id_kind + "]");
            return resultMap;
        }
        // 身份证号校验
        String res = ValidateInfoUtil.validateCID(id_no);
        if (!res.equals(TextConstant.SUCCESS)) {
            // resultMap = ReturnUtil.setErrorResult(resultMap,ErrorCode.HK_VALIDATE_CID_ERROR);
            // return resultMap;
            resultMap.put(Fields.ERROR_NO, -1);
            resultMap.put(Fields.ERROR_INFO, res);
            return resultMap;
        }
        if (StringUtil.isBlank(broker_id)) {
            resultMap.put(Fields.ERROR_NO, -1);
            resultMap.put(Fields.ERROR_INFO, "期货公司编号未获取,请刷新浏览器再次登录");
            log.error("broker_id入参错误!broker_id=[" + broker_id + "]");
            return resultMap;
        }
        if (StringUtil.isBlank(business_type)) {
            resultMap.put(Fields.ERROR_NO, -1);
            resultMap.put(Fields.ERROR_INFO, "业务类型获取失败");
            log.error("business_type入参错误!business_type=[" + business_type + "]");
            return resultMap;
        }
        try {
            boolean validate = false;
            String enableGM = ConfigUtil.get("enable.gm.sign", "invisible");
            String gmOTP = ConfigUtil.get("gm_otp", "invisible");
            if ("visible".equals(enableGM) && "visible".equals(gmOTP)) {

                if (StringUtil.isBlank(sms_code)) {
                    resultMap.put(Fields.ERROR_NO, -1);
                    resultMap.put(Fields.ERROR_INFO, "OTP令牌不能为空");
                    log.error("sms_code入参错误!sms_code=[" + sms_code + "]");
                    return resultMap;
                }

                UserOtp userOtp = userOTpAtom.getUserOtp(broker_id, id_no);
                // 启用国密和otp
                if (userOtp == null || StringUtil.isEmpty(userOtp.getOtpSn())) {
                    return VOUtil.genErrorResult(ErrorCode.VALIDATECODE_OTP_NO_USER);
                }
                boolean otp = NetPassUtil.getInstance().checkOtpCode(userOtp.getOtpSn(), sms_code);
                if (!otp) {
                    return VOUtil.genErrorResult(ErrorCode.VALIDATECODE_OTP_NO_VERIFY);
                }
                Map<String, String> params = new HashMap<>();
                params.put(Fields.ID_NO, id_no);
                params.put(Fields.ID_KIND, id_kind);
                params.put(Fields.BROKER_ID, broker_id);
                params.put(Fields.OP_STATION, op_station);
                params.put(Fields.SYSTYPE, systype);
                params.put(Fields.BUSINESS_TYPE, business_type);
                params.put(Fields.FUND_ACCOUNT, form.getFundAccount());
                // resultMap = counterService.validateThdSmsCode(params);
                // OTP登录验证账户
                resultMap = counterService.qryThdFundAccount(params);
                int errorNo = (int)resultMap.get(Fields.ERROR_NO);
                if (errorNo == 0) {
                    validate = true;
                } else {
                    Map<String, Object> result = new HashMap<>();
                    result.put(Fields.ERROR_NO, resultMap.get(Fields.ERROR_NO));
                    result.put(Fields.ERROR_INFO, resultMap.get(Fields.ERROR_INFO));
                    return result;
                }
            } else {
                if (StringUtil.isBlank(sms_code)) {
                    resultMap.put(Fields.ERROR_NO, -1);
                    resultMap.put(Fields.ERROR_INFO, "短信验证码不能为空");
                    log.error("sms_code入参错误!sms_code=[" + sms_code + "]");
                    return resultMap;
                }
                if (StringUtil.isBlank(mobile)) {
                    resultMap.put(Fields.ERROR_NO, -1);
                    resultMap.put(Fields.ERROR_INFO, "手机号获取失败");
                    log.error("mobile入参错误!mobile=[" + mobile + "]");
                    return resultMap;
                }

                Map<String, String> params = new HashMap<>();
                params.put(Fields.ID_NO, id_no);
                params.put(Fields.ID_KIND, id_kind);
                params.put(Fields.BROKER_ID, broker_id);
                params.put(Fields.SMS_CODE, sms_code);
                params.put(Fields.MOBILE, mobile);
                params.put(Fields.OP_STATION, op_station);
                params.put(Fields.SYSTYPE, systype);
                params.put(Fields.BUSINESS_TYPE, business_type);
                params.put(Fields.FUND_ACCOUNT, form.getFundAccount());
                resultMap = counterService.validateThdSmsCode(params);
                // if(!sms_code.equals("333333")){
                // resultMap.put(Fields.ERROR_NO,-1);
                // }
                int errorNo = (int)resultMap.get(Fields.ERROR_NO);
                if (errorNo == 0) {
                    validate = true;
                } else {
                    String errorInfo = "";
                    // 姓名、身份证号码、账户类型有误
                    if (ErrorCode.THD_VALIDATE_NAME.getCode() == errorNo
                        || ErrorCode.THD_VALIDATE_IDNO.getCode() == errorNo
                        || ErrorCode.THD_VALIDATE_FUNDACCOUNT.getCode() == errorNo) {
                        errorInfo = "您的登录信息有误或账户不存在，如有疑问请联系期货公司咨询，联系电话" + ConfigUtil.get("security.servicephone");// 期货公司服务热线
                        resultMap.put(Fields.ERROR_INFO, errorInfo);
                    } else if (ErrorCode.THD_VALIDATE_MOBILE.getCode() == errorNo) {
                        errorInfo = "您预留的手机号错误，如有疑问请联系期货公司咨询，联系电话" + ConfigUtil.get("security.servicephone");// 期货公司服务热线
                        resultMap.put(Fields.ERROR_INFO, errorInfo);
                    }
                    Map<String, Object> result = new HashMap<>();
                    result.put(Fields.ERROR_NO, resultMap.get(Fields.ERROR_NO));
                    result.put(Fields.ERROR_INFO, resultMap.get(Fields.ERROR_INFO));
                    return result;
                }
            }
            log.info("身份证号三方登录校验结果：" + validate);
            if (validate) {// 校验成功
                // List<Map<String,Object>> account_info =
                // JsonUtil.jsonToList(String.valueOf(resultMap.get("account_info")));
                // String fund_account = "";
                // // 资金账号状态
                // String account_status = "";
                // if(account_info!= null && account_info.size()>0){
                // fund_account = account_info.get(0).get(Fields.FUND_ACCOUNT) == null ? "" :
                // account_info.get(0).get(Fields.FUND_ACCOUNT).toString(); // 资金账号
                // account_status = account_info.get(0).get(Fields.ACCOUNT_STATUS) == null ? "" :
                // account_info.get(0).get(Fields.ACCOUNT_STATUS).toString(); // 资金账号状态
                // }else{
                // resultMap = VOUtil.setErrorResult(resultMap, ErrorCode.WEB_ACCOUNT_INFO_ERROR);
                // }
                // if(StringUtil.isBlank(fund_account)){
                // return VOUtil.setErrorResult(resultMap, ErrorCode.WEB_FUND_ACCOUNT_ERROR);
                // }
                // if(StringUtil.isBlank(account_status)){
                // return VOUtil.setErrorResult(resultMap, ErrorCode.WEB_ACCOUNT_STATUS_ERROR);
                // }
                // 获取数据
                // String register_way = "1";//网上开户-期货公司自研
                String account_status = "";
                if (resultMap.get("account_info") != null) {
                    List<Map<String, Object>> account_info =
                        JsonUtil.jsonToList(String.valueOf(resultMap.get("account_info")));
                    if (account_info != null && account_info.size() > 0
                        && account_info.get(0).get("telPhone") != null) {// 必须返回手机号
                        account_status = account_info.get(0).get("account_status") == null ? ""
                            : account_info.get(0).get("account_status").toString();
                    }
                }
                if (account_status.equals("N")) {
                    resultMap = VOUtil.setErrorResult(resultMap, ErrorCode.WEB_ACCOUNT_STATUS_N_ID_ERROR);
                } else if (account_status.equals("C")) {
                    resultMap = VOUtil.setErrorResult(resultMap, ErrorCode.WEB_ACCOUNT_STATUS_C_ID_ERROR);
                } else if (account_status.equals("D")) {
                    resultMap = VOUtil.setErrorResult(resultMap, ErrorCode.WEB_ACCOUNT_STATUS_D_ID_ERROR);
                } else {
                    if (account_status.equals("S") && business_type.equals(Constant.BUSINESS_TYPE_SIGN_THIRTEEN)) {
                        resultMap = VOUtil.setErrorResult(resultMap, ErrorCode.WEB_ACCOUNT_STATUS_S_ID_ERROR);
                    }
                }
                if ((int)resultMap.get(Fields.ERROR_NO) != 0) {
                    return resultMap;
                }
                String ip = form.getOpStation();
                Date loginTime = new Date();
                // 判断是否为已注册用户
                // UserProfile userProfile = userProfileAtom.getUserProfileByIDC(form.getBrokerId(),id_kind,id_no);
                // 同一个身份证 有多个用户 不同手机号 处理方案
                UserProfile userProfile =
                    userLocalCommonService.getUserProfile(form.getBrokerId(), id_kind, id_no, mobile);
                // UserProfile userProfile = null;
                // List<UserProfile> userProfileList = userProfileAtom.getUserProfilesByIDC(form.getBrokerId(), id_kind,
                // id_no);
                // if(userProfileList != null && !userProfileList.isEmpty()){
                // if(userProfileList.size() == 1){
                // userProfile = userProfileList.get(0);
                // }else{
                // List<UserProfile> userProfileBakList = new ArrayList<>();
                // for(int g=0; g<userProfileList.size(); g++){
                // UserProfile up = userProfileList.get(g);
                // String dh = up.getMobile() == null ? "" : up.getMobile();
                // if(mobile.equals(dh)){
                // userProfile = up;
                // }else{
                // userProfileBakList.add(up);
                // }
                // }
                // if(userProfile == null){
                // userProfile = userProfileList.get(0);
                // }
                // for(int q=0; q<userProfileBakList.size(); q++){
                // UserProfile upq = userProfileBakList.get(q);
                // String upqId = upq.getUserId().toString();
                // String upId = userProfile.getUserId().toString();
                // if(!upId.equals(upqId)){
                // UserProfileBak upb = userProfileBakAtom.getByUserId(broker_id, upqId);
                // if(upb == null){
                // UserProfileBak upbb = new UserProfileBak();
                // upbb.setUserId(upq.getUserId());
                // upbb.setIdentityType(upq.getIdentityType());
                // upbb.setIdentityCode(upq.getIdentityCode());
                // upbb.setMobile(upq.getMobile());
                // upbb.setCreateTime(upq.getCreateTime());
                // upbb.setLastLoginTime(upq.getLastLoginTime());
                // upbb.setLastIp(upq.getLastIp());
                // upbb.setStatus(upq.getStatus());
                // upbb.setAccessDomain(upq.getAccessDomain());
                // upbb.setBrokerId(upq.getBrokerId());
                // userProfileBakAtom.save(upbb);
                // }else{
                // upb.setIdentityType(upq.getIdentityType());
                // upb.setIdentityCode(upq.getIdentityCode());
                // upb.setMobile(upq.getMobile());
                // upb.setCreateTime(upq.getCreateTime());
                // upb.setLastLoginTime(upq.getLastLoginTime());
                // upb.setLastIp(upq.getLastIp());
                // upb.setStatus(upq.getStatus());
                // upb.setAccessDomain(upq.getAccessDomain());
                // upb.setBrokerId(upq.getBrokerId());
                // userProfileBakAtom.update(upb);
                // }
                // userProfileAtom.deleteByPrimaryKey(Long.parseLong(upqId));
                // }
                // }
                // }
                // }
                if (null != userProfile) {
                    userProfile.setLastLoginTime(new Date());
                    userProfile.setLastIp(clientIp);
                    // userProfile.setMobile(mobile);
                    userProfile.setBusinessType(business_type);// 用户最后一次是做什么业务进来的
                    userProfileAtom.updateByPrimaryKey(userProfile);
                } else { // 注册
                    // 判断是否为已注册用户
                    userProfile = userProfileAtom.getUserProfileByMobile(form.getBrokerId(), mobile);
                    if (userProfile != null) {
                        String idNo = (userProfile.getIdentityCode() != null) ? userProfile.getIdentityCode() : "";
                        if (!idNo.trim().equals("")) {
                            resultMap.put(Fields.ERROR_NO, ErrorCode.THD_VALIDATE_MOBILE.getCode());
                            resultMap.put(Fields.ERROR_INFO,
                                "您预留的手机号已被他人开户使用，如有疑问请联系期货公司咨询，联系电话" + ConfigUtil.get("security.servicephone"));// 期货公司服务热线
                            return resultMap;
                        } else {
                            userProfile.setIdentityCode(id_no);
                            userProfile.setIdentityType(id_kind);
                            userProfile.setBusinessType(business_type);
                            userProfile.setIsacceptno("0");
                            userProfile.setLastIp(clientIp);
                            userProfileAtom.update(userProfile);
                        }
                    } else {
                        userProfile = new UserProfile();
                        userProfile.setCreateTime(new Date());
                        userProfile.setStatus(new BigDecimal(StatusConstant.USER_STATUS_NORMAL));
                        userProfile.setLastLoginTime(new Date());
                        userProfile.setMobile(mobile);
                        userProfile.setBrokerId(DataContext.getBrokerId());
                        userProfile.setIdentityCode(id_no);
                        userProfile.setIdentityType(id_kind);
                        userProfile.setBusinessType(business_type);
                        userProfile.setIsacceptno("0");
                        userProfile.setLastIp(clientIp);
                        userProfileAtom.save(userProfile);
                    }
                }
                String userId = userProfile.getUserId().toString();
                if (systype.equals(Constant.SYSTYPE_SIGN_ONE) || systype.equals(Constant.SYSTYPE_SIGN_THREE)
                    || systype.equals(Constant.SYSTYPE_SIGN_TWO)) {
                    UserPresence userPresence = userPresenceAtom.getByUserId(form.getBrokerId(), userId);
                    UserPresence user = userPresenceAtom.getUserPresenceByMobile(form.getBrokerId(), mobile);// user_presence表broker_id关联mobile唯一索引
                    if ((user != null && userPresence == null)
                        || (user != null && !userPresence.getUserId().toString().equals(user.getUserId().toString()))) {
                        resultMap.put(Fields.ERROR_NO, ErrorCode.THD_VALIDATE_MOBILE.getCode());
                        resultMap.put(Fields.ERROR_INFO,
                            "您预留的手机号已被他人开户使用，如有疑问请联系期货公司咨询，联系电话" + ConfigUtil.get("security.servicephone"));// 期货公司服务热线
                        return resultMap;
                    }
                    if (null == userPresence) {
                        userPresence = new UserPresence();
                        userPresence.setUserId(userProfile.getUserId());
                        userPresence.setStatus(new BigDecimal(StatusConstant.ACCOUNT_REGISTER));
                        userPresence.setUserStatus(new BigDecimal(StatusConstant.USER_STATUS_REGISTER));
                        String flowStatus = "000000000010";
                        userPresence.setUserFlowStatus(flowStatus);
                        userPresence.setCreateTime(new Date());
                        userPresence.setOpenType("9");
                        userPresence.setBrokerId(form.getBrokerId());
                        userPresence.setAdequacyType("0");
                        userPresence.setIdentityCode(id_no);
                        userPresence.setIdentityType(id_kind);
                        userPresence.setAccessWay(register_way);
                        userPresence.setChannel(channel);
                        userPresence.setMobile(mobile);
                        // userPresence.setTelphone(mobile);//联系电话改成柜台返回的手机号。
                    } else {
                        // userPresence.setTelphone(mobile);//联系电话改成柜台返回的手机号。
                        userPresence.setIdentityCode(id_no);
                        userPresence.setIdentityType(id_kind);
                    }
                    userPresence.setLastUpdateTime(new Date());
                    userPresence.setLastUpdateBy(userProfile.getUserId());
                    // 接入方式传过来
                    userPresence.setRegisterWay(register_way);

                    if (null == userPresence.getId()) {
                        userPresenceAtom.save(userPresence);
                    } else {
                        userPresenceAtom.update(userPresence);
                    }
                    // }else if(systype.equals(Constant.SYSTYPE_SIGN_TWO)){
                    // resultMap = ReturnUtil.setErrorResult(resultMap,ErrorCode.CC_VALIDATE_SYSTYPE_ERROR);
                    // }else if(systype.equals(Constant.SYSTYPE_SIGN_THREE)){
                    // UserPresenceAd userPresenceAd = userPresenceAdAtom.getByUserId(form.getBrokerId(), userId);
                    // if (null == userPresenceAd) {
                    // userPresenceAd = new UserPresenceAd();
                    // userPresenceAd.setUserId(userProfile.getUserId());
                    // userPresenceAd.setStatus(new BigDecimal(StatusConstant.ACCOUNT_REGISTER));
                    // userPresenceAd.setUserStatus(new BigDecimal(StatusConstant.USER_STATUS_REGISTER));
                    // String flowStatus = "000000000010";
                    // userPresenceAd.setUserFlowStatus(flowStatus);
                    // userPresenceAd.setCreateTime(new Date());
                    // userPresenceAd.setOpenType("9");
                    // userPresenceAd.setBrokerId(form.getBrokerId());
                    // userPresenceAd.setAdequacyType("0");
                    // userPresenceAd.setIdentityCode(id_no);
                    // userPresenceAd.setIdentityType(id_kind);
                    // }
                    // userPresenceAd.setMobile(mobile);
                    // userPresenceAd.setLastUpdateTime(new Date());
                    // userPresenceAd.setLastUpdateBy(userProfile.getUserId());
                    // // 接入方式传过来
                    // userPresenceAd.setRegisterWay(register_way);
                    //
                    // if (null == userPresenceAd.getId()) {
                    // userPresenceAdAtom.save(userPresenceAd);
                    // } else {
                    // userPresenceAdAtom.update(userPresenceAd);
                    // }
                } else {
                    resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_SYSTYPE_ERROR);
                }
                // 保存手机信息
                if (!StringUtil.isBlank(phone_type) || !StringUtil.isBlank(phone_version)) {
                    PhoneInfo phoneInfo = new PhoneInfo();
                    phoneInfo.setBrokerId(DataContext.getBrokerId());
                    phoneInfo.setMobile(mobile);
                    phoneInfo.setPhoneType(phone_type);
                    phoneInfo.setPhoneVersion(phone_version);
                    phoneInfo.setVersion(appSdkVersion);
                    phoneInfo.setRegisterWay(register_way);
                    phoneInfo.setCreateTime(DateUtil.format(loginTime, DateUtil.DATE_TIME_FORMAT));
                    phoneInfoAtom.save(phoneInfo);
                }

                // 保存用户登入信息日志
                successUserLogAtom.SaveSuccessUserinformation(register_way, loginTime, mobile, "", ip, mac_addr, "", "",
                    "");

                // log日志
                StringBuffer logInfo = new StringBuffer();
                logInfo.append("【登录成功手机号mphone：" + mobile + "】");
                logInfo.append("【登录时间loginTime：" + DateUtil.format(loginTime, DateUtil.DATE_TIME_FORMAT) + "】");
                resultMap.put(Fields.USER_ID, userId);
                log.info(logInfo);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ReturnUtil.setErrorResult(resultMap, ErrorCode.COMMON_ERROR);
        }
        // resultMap = new HashMap<>();
        // try{
        // Thread.sleep(1000);
        // }catch (Exception e){
        //
        // }
        return resultMap;
    }

    /**
     * 同步用户信息数据
     * 
     * @param form
     * @return
     */
    @Override
    public Map<String, Object> syncUserInfoWhole(UserInfoForm form) {
        // 初始化返回值
        Map<String, Object> result = ReturnUtil.setErrorResult(new HashMap<String, Object>(), ErrorCode.SUCCESS);
        // 获取输入参数
        String brokerId = form.getBrokerId();
        String userId = form.getUserId();
        String businessType = form.getBusinessType();
        Long reviewedUserId = Long.parseLong(userId);
        String acceptNo = form.getAcceptNo();
        String funcStatus = Constant.ACCEPT_STATUS_SIGN_EIGHT;// 受理单状态 放弃
        if (StringUtil.isBlank(userId)) {// 用户不存在
            return ReturnUtil.setErrorResult(result, ErrorCode.NO_SUCH_CLIENT);
        }
        try {
            // 用户
            UserPresence presence = userPresenceAtom.getByUserId(brokerId, userId);
            if (presence == null) {
                return ReturnUtil.setErrorResult(result, ErrorCode.NO_SUCH_CLIENT);
            }
            UserIdInfo userIdInfo = userIdInfoAtom.getUserIdInfoById(userId);
            UserIdUpload userIdUpload = userIdUploadAtom.getUserIdUploadByUserId(userId);
            // 受理单
            if (StringUtil.isBlank(acceptNo)) {
                acceptNo = "";
            }
            BaseAccept baseAccept = baseAcceptAtom.getBaseAcceptByAcceptNo(brokerId, userId, businessType, acceptNo);
            if (baseAccept == null) {
                return ReturnUtil.setErrorResult(result, ErrorCode.HK_ACCEPT_N0_FORM_ERROR);
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 适当性
            UserAdequacy userAdequacy = new UserAdequacy();
            userAdequacy.setUserId(BigDecimal.valueOf(reviewedUserId));
            List<UserAdequacy> userAdeList = userAdequacyAtom.getByUserAdequacy(userAdequacy);

            // 账户
            UserAccounts userAccounts = new UserAccounts();
            userAccounts.setUserId(reviewedUserId);
            List<UserAccounts> accountsList = userAccountsAtom.getByUserAccounts(userAccounts);
            String accoutNo = "";
            Date accoutTime = null;
            String accountsInfo = "";
            String accoutsFail = "";
            if (accountsList != null && accountsList.size() > 0) {
                for (UserAccounts accout : accountsList) {
                    if ("1001".equals(accout.getInvsetType())) {
                        accoutNo = accout.getAccountNo();
                        accoutTime = accout.getStartTime();
                    } else {
                        accountsInfo = accountsInfo + "|-|" + accout.getAccountType() + "|+|" + accout.getAccountName();
                    }
                }
                if (accountsInfo.length() > 3) {
                    accountsInfo = accountsInfo.substring(3);
                }
                if (accoutsFail.length() > 3) {
                    accoutsFail = accoutsFail.substring(3);
                }
            }
            // 营业部
            String orgName = "";
            Long openOrgId = null;
            if (presence.getOpenOrgId() != null) {
                openOrgId = presence.getOpenOrgId().longValue();
                BaseOrganization byOrgId =
                    baseOrganizationAtom.getByOrgId(brokerId, presence.getOpenOrgId().longValue() + "");
                if (byOrgId != null) {
                    orgName = byOrgId.getName();
                }
            }

            // 三方存管银行
            List<UserThirdDeposity> userBankList = userThirdDeposityAtom.getUserThirdDeposityByUserId(reviewedUserId);

            // 获取公安认证信息
            UserIdVerify verify = userIdVerifyAtom.getUserIdVerifyByUserId(userId);

            // 视频
            UserVideoVerify userVideoVerify = userVideoVerifyAtom.getUserVideoVerifyByUserId(userId);

            // 证书信息
            UserCaVerifyWithBLOBs userCaVerify =
                userCaVerifyAtom.getByUserIdAndType(userId, MiddleConstants.CA_TYPE_ZJ + "");

            // 协议签署
            ObsUserAgreementSign obsUserAgreementSign = new ObsUserAgreementSign();
            obsUserAgreementSign.setUserId(reviewedUserId);
            int signFlag = 1;// 签署完成 （复核通过）
            if (Constant.ACCEPT_STATUS_SIGN_EIGHT.equals(funcStatus)) {// 如果是放弃，查询预签署完成的协议
                signFlag = 3;
            }
            obsUserAgreementSign.setSignFlag(BigDecimal.valueOf(signFlag));
            obsUserAgreementSign.setBusinessType(businessType);
            List<ObsUserAgreementSignWithBLOBs> signAgreementList =
                obsUserAgreementSignAtom.selectListByObsSign(obsUserAgreementSign);
            List<ObsUserAgreementSignWithBLOBs> signBrokerAgreementList = null;
            if (null != presence.getIsBroker()) {
                if (presence.getIsBroker().equals("1")) {
                    obsUserAgreementSign.setBusinessType(businessType + "J");
                    signBrokerAgreementList = obsUserAgreementSignAtom.selectListByObsSign(obsUserAgreementSign);// 居间人协议
                }

            }

            // 回访问卷
            List<UserRevisit> userRevisitsList = userRevisitAtom.getByUserId(reviewedUserId);

            // 金融资料
            List<UserFinancePic> financePicsList = userFinancePicAtom.getUserFinancePics(brokerId, userId);

            // 原油资料
            List<UserInePic> inePicsList = userInePicAtom.getUserInePics(brokerId, userId);

            // String acceptNo = baseAccept.getAcceptNo();

            // 插入数据，新增不更新
            // 用户信息表
            ProfileUserInfo profileUserInfo = profileUserInfoAtom.getByAcceptNo(acceptNo);
            // 用户信息查询表
            ProfileUserQry profileUserQry = profileUserQryAtom.getByAcceptNo(acceptNo);
            // 用户适当性关系表
            ProfileUserAdequacy profileUserAdequacy = profileUserAdequacyAtom.getByAcceptNo(acceptNo);
            // 客户协议图片表
            ProfileUserPicDocWithBLOBs profileUserPicDoc = profileUserPicDocAtom.getByAcceptNo(acceptNo);
            // 客户其他信息表
            ProfileUserExtend profileUserExtend = profileUserExtendAtom.getByAcceptNo(acceptNo);
            // 客户账户关系表
            ProfileUserAccounts profileUserAccounts = profileUserAccountsAtom.getByAcceptNo(acceptNo);

            if (profileUserInfo == null) {
                profileUserInfo = new ProfileUserInfo();
                profileUserInfo.setAcceptNo(acceptNo);
            }
            if (profileUserQry == null) {
                profileUserQry = new ProfileUserQry();
            }
            if (profileUserAdequacy == null) {
                profileUserAdequacy = new ProfileUserAdequacy();
            }
            if (profileUserPicDoc == null) {
                profileUserPicDoc = new ProfileUserPicDocWithBLOBs();
            }
            if (profileUserExtend == null) {
                profileUserExtend = new ProfileUserExtend();
            }
            if (profileUserAccounts == null) {
                profileUserAccounts = new ProfileUserAccounts();
            }
            profileUserInfo.setAcceptStatus(funcStatus); // 受理单状态固定 8 放弃
            profileUserInfo.setBusinessType(businessType);
            profileUserInfo.setAcceptCreateTime(baseAccept.getCreateTime());
            profileUserInfo.setLastUpdateTime(baseAccept.getLastUpdateTime());
            profileUserInfo.setBrokerId(brokerId);
            profileUserInfo.setUserId(BigDecimal.valueOf(reviewedUserId));
            profileUserInfo.setUserName(presence.getUserName());
            profileUserInfo.setMobile(presence.getMobile());
            profileUserInfo.setIdentityType(presence.getIdentityType());
            profileUserInfo.setIdentityCode(presence.getIdentityCode());
            if (userIdInfo != null) {
                profileUserInfo.setGender(userIdInfo.getGender());
                profileUserInfo.setMinzu(userIdInfo.getMinzu());
                profileUserInfo.setBirthday(userIdInfo.getAge());
                profileUserInfo.setIdentityAddress(userIdInfo.getAddress());
                profileUserInfo.setDeliveryDate(userIdInfo.getDeliveryDate());
                profileUserInfo.setDueDate(userIdInfo.getDueDate());
                profileUserInfo.setNation(userIdInfo.getNation());
                profileUserQry.setBirthday(userIdInfo.getAge());
                profileUserQry.setDueDate(userIdInfo.getDueDate());
            }
            profileUserInfo.setTelPhone(presence.getTelphone());
            profileUserInfo.setContactMobile(presence.getContactMobile());
            profileUserInfo.setZipCode(presence.getZipCode());
            profileUserInfo.setMailAddress(presence.getMailAddress());
            profileUserInfo.setDegreeCode(presence.getDegreeCode());
            profileUserInfo.setProfessionCode(presence.getProfessionCode());// 职业 参考数据字典1015
            profileUserInfo.setVocationCode(presence.getIndustryCode());// 行业 参考数据字典1019
            profileUserInfo.setFullAddress(presence.getFullAddress());
            profileUserInfo.setProductName(presence.getProdName());// 产品名称
            Long antiEmpId = baseAccept.getAntiEmpId();
            if (antiEmpId != null) {
                profileUserInfo.setAntiEmpId(new BigDecimal(antiEmpId));
                profileUserQry.setAntiEmpId(new BigDecimal(antiEmpId));
                BaseSysUser antiEmp = baseSysUserAtom.getByEmpIdAndBrokerId(brokerId, antiEmpId);
                if (antiEmp != null) {
                    profileUserInfo.setAntiEmpName(antiEmp.getEmpName());
                    profileUserQry.setAntiEmpName(antiEmp.getEmpName());
                }
            }
            profileUserInfo.setVideoTime(baseAccept.getVideoTime());
            profileUserInfo.setReviewTime(baseAccept.getReviewTime());
            Long empId = baseAccept.getEmpId();
            if (empId != null) {
                profileUserInfo.setEmpId(new BigDecimal(empId));
                profileUserQry.setEmpId(new BigDecimal(empId));
                BaseSysUser emp = baseSysUserAtom.getByEmpIdAndBrokerId(brokerId, empId);
                if (emp != null) {
                    profileUserInfo.setEmpName(emp.getEmpName());
                    profileUserQry.setEmpName(emp.getEmpName());
                }
            }
            profileUserInfo.setCreateTime(presence.getCreateTime());
            profileUserInfo.setRegisterWay(presence.getRegisterWay());
            String extendField = "";
            String val1 = presence.getVal1();
            String val2 = presence.getVal2();
            String val3 = presence.getVal3();
            if (!StringUtil.isBlank(val1)) {
                extendField += "," + val1;
            }
            if (!StringUtil.isBlank(val2)) {
                extendField += "," + val2;
            }
            if (!StringUtil.isBlank(val3)) {
                extendField += "," + val3;
            }
            if (!StringUtil.isBlank(extendField)) {
                extendField = extendField.substring(1);
            }
            profileUserInfo.setExtendField(extendField);
            profileUserInfo.setOpenOrgId(openOrgId + "");
            profileUserInfo.setOpenOrgName(orgName);

            BigDecimal id;
            try {
                // 保存成功返回主键，在ProfileUserInfoMapper.xml中insert标签添加属性 useGeneratedKeys="true" keyProperty="id"
                // keyColumn="ID"
                id = profileUserInfoAtom.saveUpdateProfileUserInfo(profileUserInfo);
            } catch (Exception e) {
                log.error("用户信息表保存失败：", e);
                return ReturnUtil.setErrorResult(result, ErrorCode.COMMON_ERROR);
            }
            if (id == null) {
                return ReturnUtil.setErrorResult(result, ErrorCode.COMMON_ERROR);
            }

            profileUserQry.setId(id);
            profileUserQry.setAcceptNo(acceptNo);
            profileUserQry.setAccountNo(accoutNo);
            profileUserQry.setUserName(presence.getUserName());
            profileUserQry.setMobile(presence.getMobile());
            profileUserQry.setChannel(presence.getChannel());
            profileUserQry.setRegisterWay(presence.getRegisterWay());
            profileUserQry.setBusinessType(businessType);
            profileUserQry.setAcceptStatus(funcStatus);
            profileUserQry.setCreateTime(presence.getCreateTime());
            profileUserQry.setReviewTime(baseAccept.getReviewTime() == null ? new Date() : baseAccept.getReviewTime());
            profileUserQry.setIdentityCode(presence.getIdentityCode());
            profileUserQry.setBrokerId(brokerId);
            profileUserQry.setOpenOrgName(orgName);
            if (openOrgId != null) {// 修改开立业务在未保存身份信息前放弃失败问题
                profileUserQry.setOpenOrgId(new BigDecimal(openOrgId));
            }
            profileUserQry.setExtendField(extendField);

            // 居间人同步
            String isBroker = presence.getIsBroker() == null ? "" : presence.getIsBroker();
            profileUserQry.setIsBroker(isBroker);
            if (isBroker.equals("1")) {
                profileUserQry.setBrokerName(presence.getBrokerName());
                profileUserQry.setBrokerCode(presence.getBrokerCode());
            }

            profileUserAdequacy.setId(id);
            profileUserAdequacy.setAcceptNo(acceptNo);

            if (userAdeList != null && userAdeList.size() > 0) {
                UserAdequacy userAde = userAdeList.get(0);
                String adequacyType = userAde.getAdequacyType();
                profileUserQry.setAdequacyType(adequacyType);
                profileUserAdequacy.setAdequacyType(adequacyType);
                profileUserAdequacy.setThirdFinish(userAde.getThirdFinish());
                profileUserAdequacy.setIsSignRiskDeal(userAde.getIsSignRiskDeal());

                if ("1".equals(userAde.getAdequacyType())) {// 专业
                    // 专业测评
                    MajorTestResult majorTestResult = majorTestResultAtom.getTestResult(reviewedUserId);
                    if (majorTestResult != null) {
                        profileUserAdequacy.setAdequacyPaperId(new BigDecimal(majorTestResult.getMajorPaperId()));
                        profileUserAdequacy.setAdequacyTestResult(majorTestResult.getResult());
                        profileUserAdequacy.setAdequacyTestTime(majorTestResult.getTestEnd());

                        List<MajorTestAnswers> majorAnswers =
                            majorTestAnswersAtom.getTestAnswers(majorTestResult.getResultId());
                        String answers = "";
                        if (majorAnswers != null && majorAnswers.size() > 0) {
                            Map<String, String> map = new HashMap<>();
                            for (MajorTestAnswers answer : majorAnswers) {
                                String qid = answer.getQuestionId();
                                String option = map.get(qid);
                                if (!StringUtil.isBlank(option)) {
                                    map.put(qid, option + "," + answer.getOptionId());
                                } else {
                                    map.put(qid, answer.getOptionId());
                                }
                            }
                            Set<String> keys = map.keySet();
                            for (String key : keys) {
                                answers = answers + "|-|" + key + "|+|" + map.get(key);
                            }
                            if (answers.length() > 3) {
                                answers = answers.substring(3);
                            }
                        }
                        profileUserAdequacy.setAdequacyTestAnswer(answers);
                    }
                    // 专业投资者资料
                    List<UserMajorinvestordata> usermajors =
                        userMajorinvestordataAtom.getUserMajorinvestordatas(brokerId, userId);
                    if (usermajors != null && usermajors.size() > 0) {
                        String majorPics = "";
                        for (UserMajorinvestordata userMajor : usermajors) {
                            majorPics = majorPics + "," + userMajor.getMajorinvestordataPic();
                        }
                        if (majorPics.length() > 1) {
                            majorPics = majorPics.substring(1);
                        }
                        profileUserAdequacy.setMajorPics(majorPics);
                    }
                } else {
                    // 风险测评
                    ExamTestResult testResult = examTestResultAtom.getTestResult(reviewedUserId);
                    if (testResult != null) {
                        if (testResult.getLocalPaperId() != null) { // 临时判空，柜台返回无此值
                            profileUserAdequacy.setAdequacyPaperId(new BigDecimal(testResult.getLocalPaperId()));
                        }
                        profileUserAdequacy.setAdequacyTestResult(testResult.getResult());
                        profileUserAdequacy.setAdequacyResultComment(testResult.getResultComment());
                        profileUserAdequacy.setAdequacyTestTime(testResult.getTestEnd());
                        profileUserAdequacy.setRiskLevel(testResult.getResultComment());// 风险等级

                        List<ExamTestAnswers> testAnswers =
                            examTestAnswersAtom.getTestAnswers(testResult.getResultId());
                        String answers = "";
                        if (testAnswers != null && testAnswers.size() > 0) {
                            Map<String, String> map = new HashMap<>();
                            for (ExamTestAnswers answer : testAnswers) {
                                String qid = answer.getQuestionId();
                                String option = map.get(qid);
                                if (!StringUtil.isBlank(option)) {
                                    map.put(qid, option + "," + answer.getOptionId());
                                } else {
                                    map.put(qid, answer.getOptionId());
                                }
                            }
                            Set<String> keys = map.keySet();
                            for (String key : keys) {
                                answers = answers + "|-|" + key + "|+|" + map.get(key);
                            }
                            if (answers.length() > 3) {
                                answers = answers.substring(3);
                            }
                        }
                        profileUserAdequacy.setAdequacyTestAnswer(answers);
                    }
                }

            }
            profileUserPicDoc.setId(id);
            profileUserPicDoc.setAcceptNo(acceptNo);

            profileUserExtend.setId(id);
            profileUserExtend.setAcceptNo(acceptNo);
            profileUserExtend.setTaxType(presence.getTaxType());
            profileUserExtend.setPlatformType(presence.getPlatformType());
            profileUserExtend.setBadRecord(presence.getBadRecord());
            profileUserExtend.setTermInvestment(presence.getTermInvestment());
            profileUserExtend.setInvestmentVariety(presence.getInvestmentVariety());
            profileUserExtend.setProceedsType(presence.getProceedsType());
            profileUserExtend.setConRelation(presence.getConRelation());
            profileUserExtend.setBeneficiary(presence.getBeneficiary());
            profileUserExtend.setDealType(presence.getDealType());

            profileUserAccounts.setId(id);
            profileUserAccounts.setAcceptNo(acceptNo);
            profileUserAccounts.setAccountNo(accoutNo);
            profileUserAccounts.setAccountsInfo(accountsInfo);
            profileUserAccounts.setAccountsFail(accoutsFail);
            profileUserAccounts.setAccountsTime(accoutTime);

            String bankInfoStr = "";
            if (null != userBankList && (businessType.equals(Constant.BUSINESS_TYPE_SIGN_ELEVEN)
                || businessType.equals(Constant.BUSINESS_TYPE_SIGN_NINETEEN))) {
                for (UserThirdDeposity bankList : userBankList) {
                    Long bankId = bankList.getBankId();
                    String bankName = "";
                    if (bankId != null) {
                        BaseBank baseBank = baseBankAtom.queryBankByKey(bankId);
                        if (baseBank != null) {
                            bankName = baseBank.getBankName();
                        }
                    }
                    bankInfoStr = bankInfoStr + "|-|" + bankList.getAccountNo() + "|+|" + bankName + "|+|"
                        + bankList.getBankBranch() + "|+|" + bankList.getBankPic();
                }
                if (bankInfoStr.length() > 3) {
                    bankInfoStr = bankInfoStr.substring(3);
                }
            }
            profileUserAccounts.setBankInfo(bankInfoStr);

            if (null != verify) {
                profileUserPicDoc.setVerifyStatus(verify.getStatus());
                profileUserPicDoc.setVerifyPic(verify.getUserPic());
                profileUserPicDoc.setVerifyTime(verify.getValidateTime());
                profileUserPicDoc.setScore(verify.getScore());
                profileUserPicDoc.setScoreMessage(verify.getScoreMessage());
            }

            if (userCaVerify != null) {

                profileUserPicDoc.setVerifyCaId(userCaVerify.getVerifyId().longValue());
                profileUserPicDoc.setCaSn(userCaVerify.getCaSn());
                profileUserPicDoc.setSetupTime(userCaVerify.getSetupTime());
            }

            if (signAgreementList != null && signAgreementList.size() > 0) {
                String signPdfName = "";
                String pdfDigestInfo = "";
                String signValue = "";
                String signNo = "";
                String signAgreementIds = "";
                String signTime = "";
                long pdfSize = 0l;
                String pdfPath = "";
                String signAgreement = "";

                ObsUserAgreementSignWithBLOBs obs = signAgreementList.get(0);
                signPdfName += "," + obs.getPdfName();
                signNo += "," + obs.getSignNo();
                pdfDigestInfo += "|+|" + obs.getPdfDigestInfo();
                signTime = format.format(obs.getSignTime());
                signValue += "|+|" + obs.getSignValue();
                pdfSize = pdfSize + Long.parseLong(obs.getPdfSize() == null ? "0" : obs.getPdfSize());
                pdfPath = obs.getPdfSize() == null ? pdfPath : obs.getPdfPath();
                String agreementIds = obs.getAgreementIds();
                if (!"".equals(agreementIds)) {
                    String[] ids = agreementIds.split(",");
                    for (String agreementId : ids) {
                        ObsDocAgreement oda = obsDocAgreementAtom.getByObsId(Long.valueOf(agreementId));
                        String agreementName = oda.getAgreementName();
                        String agreementContent = oda.getAgreementContent();
                        signAgreement = signAgreement + "|-|" + agreementName + "|+|" + agreementContent;
                    }
                }
                signAgreementIds += "," + agreementIds;
                try {
                    // 如果放弃 将已签署的协议状态改为 2 放弃
                    obs.setSignFlag(BigDecimal.valueOf(2));
                    obsUserAgreementSignAtom.updateByPrimaryKey(obs);
                } catch (Exception e) {
                    log.error("协议放弃异常：", e);
                }
                if (signBrokerAgreementList != null && signBrokerAgreementList.size() > 0) {
                    ObsUserAgreementSignWithBLOBs obsBroker = signBrokerAgreementList.get(0);
                    signPdfName += "," + obsBroker.getPdfName();
                    signNo += "," + obsBroker.getSignNo();
                    pdfDigestInfo += "|+|" + obsBroker.getPdfDigestInfo();
                    signTime = format.format(obsBroker.getSignTime());
                    signValue += "|+|" + obsBroker.getSignValue();
                    pdfSize = pdfSize + Long.parseLong(obsBroker.getPdfSize() == null ? "0" : obsBroker.getPdfSize());
                    pdfPath = obsBroker.getPdfSize() == null ? pdfPath : obsBroker.getPdfPath();
                    String agreementBrokerIds = obsBroker.getAgreementIds();
                    if (!"".equals(agreementBrokerIds)) {
                        String[] ids = agreementBrokerIds.split(",");
                        for (String agreementId : ids) {
                            ObsDocAgreement oda = obsDocAgreementAtom.getByObsId(Long.valueOf(agreementId));
                            String agreementName = oda.getAgreementName();
                            String agreementContent = oda.getAgreementContent();
                            signAgreement = signAgreement + "|-|" + agreementName + "|+|" + agreementContent;
                        }
                    }
                    signAgreementIds += "," + agreementBrokerIds;

                    try {
                        // 如果放弃 将已签署的协议状态改为 2 放弃
                        obsBroker.setSignFlag(BigDecimal.valueOf(2));
                        obsUserAgreementSignAtom.updateByPrimaryKey(obsBroker);
                    } catch (Exception e) {
                        log.error("居间人协议放弃异常：", e);
                    }
                }

                profileUserPicDoc.setSignPdf(signPdfName.length() > 0 ? signPdfName.substring(1) : "");
                profileUserPicDoc.setSignNo(signNo.length() > 0 ? signNo.substring(1) : "");
                profileUserPicDoc.setSignTime(signTime);
                profileUserPicDoc
                    .setSignAgreementIds(signAgreementIds.length() > 0 ? signAgreementIds.substring(1) : "");
                profileUserPicDoc.setPdfDigestInfo(pdfDigestInfo.length() > 3 ? pdfDigestInfo.substring(3) : "");
                profileUserPicDoc.setSignValue(signValue.length() > 3 ? signValue.substring(3) : "");
                profileUserPicDoc.setPdfSize(pdfSize + "");

                profileUserPicDoc.setSignAgreement(signAgreement.length() > 3 ? signAgreement.substring(3) : "");

            }

            if (userRevisitsList != null && userRevisitsList.size() > 0) {// 期货开户 增开 适当性 有回访 其他没有
                if (businessType.equals(Constant.BUSINESS_TYPE_SIGN_ELEVEN)
                    || businessType.equals(Constant.BUSINESS_TYPE_SIGN_THIRTEEN)
                    || businessType.equals(Constant.ACCEPT_STATUS_SIGN_FOURTEEN)
                    || businessType.equals(Constant.BUSINESS_TYPE_SIGN_TWENTY_TWO)
                    || businessType.equals(Constant.BUSINESS_TYPE_SIGN_TWENTY_FOUR)) {

                    Long revisitPaperId = userRevisitsList.get(0).getRevisitPaperId();
                    profileUserAdequacy.setRevisitPaperId(new BigDecimal(revisitPaperId));
                    String revisitAnswer = "";
                    for (UserRevisit userRevisit : userRevisitsList) {
                        revisitAnswer =
                            revisitAnswer + "|-|" + userRevisit.getQuestionId() + "|+|" + userRevisit.getOptionId();
                    }
                    if (revisitAnswer.length() > 3) {
                        revisitAnswer = revisitAnswer.substring(3);
                    }
                    profileUserAdequacy.setRevisitTestAnswer(revisitAnswer);
                }
            }
            if (userIdUpload != null) {
                profileUserPicDoc.setFrontPic(userIdUpload.getFrontPic());
                profileUserPicDoc.setBackPic(userIdUpload.getBackPic());
                profileUserPicDoc.setSignPic(userIdUpload.getAttr2());
            }
            if (userVideoVerify != null) {
                profileUserPicDoc.setFacePic(userVideoVerify.getSnapImage3());
            }

            String evaltePics = "";
            if (financePicsList != null && financePicsList.size() > 0) {
                String finacePics = "";
                for (UserFinancePic financePic : financePicsList) {
                    if ("1".equals(financePic.getPicType())) {
                        finacePics = finacePics + "," + financePic.getFinancePic();
                    } else {
                        evaltePics = evaltePics + "," + financePic.getFinancePic();
                    }
                }
                if (finacePics.length() > 1) {
                    finacePics = finacePics.substring(1);
                }
                profileUserAdequacy.setFinancePics(finacePics);
            }

            if (inePicsList != null && inePicsList.size() > 0) {
                String inePics = "";
                for (UserInePic inePic : inePicsList) {
                    if ("1".equals(inePic.getPicType())) {
                        inePics = inePics + "," + inePic.getInePic();
                    } else {
                        evaltePics = evaltePics + "," + inePic.getInePic();
                    }
                }
                if (inePics.length() > 1) {
                    inePics = inePics.substring(1);
                }
                profileUserAdequacy.setInePics(inePics);
            }
            if (evaltePics.length() > 1) {
                evaltePics = evaltePics.substring(1);
            }
            profileUserAdequacy.setEvaluatePics(evaltePics);

            boolean suc = true;
            try {
                profileUserQryAtom.saveUpdateProfileUserQry(profileUserQry);
            } catch (Exception e) {
                log.error("用户查询信息表保存失败：", e);
                suc = false;
            }
            try {
                profileUserExtendAtom.saveUpdateProfileUserExtend(profileUserExtend);
            } catch (Exception e) {
                log.error("用户其他信息表保存失败：", e);
                suc = false;
            }
            try {
                profileUserAccountsAtom.saveUpdateProfileUserAccounts(profileUserAccounts);
            } catch (Exception e) {
                log.error("用户账户关系表保存失败：", e);
                suc = false;
            }
            try {
                profileUserAdequacyAtom.saveUpdateProfileUserAdequacy(profileUserAdequacy);
            } catch (Exception e) {
                log.error("用户适当性关系表保存失败：", e);
                suc = false;
            }
            try {
                profileUserPicDocAtom.saveUpdateProfileUserPicDoc(profileUserPicDoc);
            } catch (Exception e) {
                log.error("用户协议图片表保存失败：", e);
                suc = false;
            }
            // 开始 -- 针对期货开户，同步数据大表后，调整 CHANNEL 渠道 @营业部code$渠道信息 字段为"" 调整val1 val2 val3 为空
            // 将居间人信息清空。
            if (businessType.equals(Constant.BUSINESS_TYPE_SIGN_ELEVEN)) {
                presence.setIsBroker("");
                presence.setBrokerCode("");
                presence.setBrokerName("");
                presence.setChannel("");
                presence.setVal1("");
                presence.setVal2("");
                presence.setVal3("");
                userPresenceAtom.update(presence);
            }
            // 结束 -- 针对期货开户，同步数据大表后，调整 CHANNEL 渠道 @营业部code$渠道信息 字段为""
            // if (suc && "11".equals(businessType)){
            // //删除业务类型是开户的 user_presence user_profile user_id_info
            // Long uid = Long.parseLong(userId);
            // userProfileAtom.deleteByPrimaryKey(uid);
            // userIdInfoAtom.deleteByExample(uid);
            // userPresenceAtom.deleteByUserId(uid);
            // }else {
            // result.put(Fields.ERROR_NO,-1);
            // result.put(Fields.ERROR_INFO,"同步数据保存失败");
            // }

        } catch (Exception e) {
            log.error("同步用户信息失败:", e);
            return ReturnUtil.setErrorResult(result, ErrorCode.COMMON_ERROR);
        }

        return result;
    }

    @Override
    public Map<String, Object> getLaunderingBack(UserInfoForm form) {
        // 初始化返回值
        Map<String, Object> resultMap = ReturnUtil.setErrorResult(new HashMap<String, Object>(), ErrorCode.SUCCESS);
        // 获取输入参数
        String brokerId = form.getBrokerId(); // 期货公司编号
        String userId = form.getUserId(); // 用户编号
        String systype = form.getSystype(); // 业务账户类型
        // 验证用户编号
        if (StringUtil.isBlank(userId)) {
            resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.NO_SUCH_CLIENT);
        } else {
            // 1.查询 user_profile
            UserProfile userProfile = userProfileAtom.getUserProfileByUserId(new BigDecimal(userId));
            if (userProfile == null) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.NO_SUCH_CLIENT);
                return resultMap;
            }
            if (systype.equals(Constant.SYSTYPE_SIGN_ONE) || systype.equals(Constant.SYSTYPE_SIGN_TWO)) {
                // 2.查询 user_presence
                UserPresence userPresence = userPresenceAtom.getByUserId(brokerId, userId);
                // 3.返回值处理
                if (userPresence == null) {
                    resultMap.put(Fields.TAX_TYPE, "");
                    resultMap.put(Fields.PLATFORM_TYPE, "");
                    resultMap.put(Fields.BAD_RECORD, "");
                    resultMap.put(Fields.TERM_INVESTMENT, "");
                    resultMap.put(Fields.INVESTMENT_VARIETY, "");
                    resultMap.put(Fields.PROCEEDS_TYPE, "");
                    resultMap.put(Fields.CON_RELATION, "");
                    resultMap.put(Fields.BENEFICIARY, "");
                    resultMap.put(Fields.DEAL_TYPE, "");
                } else {
                    resultMap.put(Fields.TAX_TYPE,
                        (userPresence.getTaxType() != null) ? userPresence.getTaxType() : "");
                    resultMap.put(Fields.PLATFORM_TYPE,
                        (userPresence.getPlatformType() != null) ? userPresence.getPlatformType() : "");
                    resultMap.put(Fields.BAD_RECORD,
                        (userPresence.getBadRecord() != null) ? userPresence.getBadRecord() : "");
                    resultMap.put(Fields.TERM_INVESTMENT,
                        (userPresence.getTermInvestment() != null) ? userPresence.getTermInvestment() : "");
                    resultMap.put(Fields.INVESTMENT_VARIETY,
                        (userPresence.getInvestmentVariety() != null) ? userPresence.getInvestmentVariety() : "");
                    resultMap.put(Fields.PROCEEDS_TYPE,
                        (userPresence.getProceedsType() != null) ? userPresence.getProceedsType() : "");
                    resultMap.put(Fields.CON_RELATION,
                        (userPresence.getConRelation() != null) ? userPresence.getConRelation() : "");
                    resultMap.put(Fields.BENEFICIARY,
                        (userPresence.getBeneficiary() != null) ? userPresence.getBeneficiary() : "");
                    resultMap.put(Fields.DEAL_TYPE,
                        (userPresence.getDealType() != null) ? userPresence.getDealType() : "");
                }
            } else {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_SYSTYPE_ERROR);
            }
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> saveLaunderingBack(UserInfoForm form) {
        // 初始化返回值
        Map<String, Object> resultMap = ReturnUtil.setErrorResult(new HashMap<String, Object>(), ErrorCode.SUCCESS);
        // 获取输入参数
        String brokerId = form.getBrokerId(); // 期货公司编号
        String userId = form.getUserId(); // 用户编号
        String systype = form.getSystype(); // 业务账户类型
        String businessType = form.getBusinessType(); // 业务类型
        String acceptNo = form.getAcceptNo(); // 受理单号
        String taxType = form.getTaxType(); // 税收类型
        String platformType = form.getPlatformType(); // 政要关系
        String badRecord = form.getBadRecord(); // 不良记录
        String termInvestment = form.getTermInvestment(); // 投资期限
        String investmentVariety = form.getInvestmentVariety(); // 投资品种
        String proceedsType = form.getProceedsType(); // 收益类型
        String conRelation = form.getConRelation(); // 控制关系
        String beneficiary = form.getBeneficiary(); // 受益人
        String dealType = form.getDealType(); // 交易类型
        // 业务类型 验证
        String res = ValidateInfoUtil.validateBusinessType(businessType);
        if (!res.equals(TextConstant.SUCCESS)) {
            return ReturnUtil.setErrorResult(resultMap, ErrorCode.BIZ_TYPE_NOTEXIISTS);
        }
        // 受理单号验证
        Map<String, Object> baseAcceptMap =
            userLocalCommonService.verificationBaseAccept(brokerId, userId, businessType, acceptNo);
        if (ValueUtil.getInt(baseAcceptMap, Fields.ERROR_NO, -1) != 0) {
            return baseAcceptMap;
        }
        BaseAccept baseAccept = (BaseAccept)baseAcceptMap.get("baseAccept");
        // 同步标识
        String synchronousFlag = baseAccept.getSynchronousFlag();
        // 获取开关
        String tax_type_flag = ConfigUtil.get("user.taxtype.input", "0"); // 税收类型显示开关
        String platform_type_flag = ConfigUtil.get("user.platformtype.input", "0"); // 政要关系显示开关
        String bad_record_flag = ConfigUtil.get("user.badrecord.input", "0"); // 不良记录显示开关
        String term_investment_flag = ConfigUtil.get("user.terminvestment.input", "0"); // 投资期限显示开关
        String investment_variety_flag = ConfigUtil.get("user.investmentvariety.input", "0"); // 投资品种显示开关
        String proceeds_type_flag = ConfigUtil.get("user.proceedstype.input", "0"); // 收益类型显示开关
        String con_relation_flag = ConfigUtil.get("user.conrelation.input", "0"); // 控制关系显示开关
        String beneficiary_flag = ConfigUtil.get("user.beneficiary.input", "0"); // 受益人显示开关
        String deal_type_flag = ConfigUtil.get("user.dealtype.input", "0"); // 交易类型显示开关
        // 验证用户编号
        if (StringUtil.isBlank(userId)) {
            resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.NO_SUCH_CLIENT);
        } else {
            // 验证用户
            UserProfile userProfile = userProfileAtom.getUserProfileByUserId(new BigDecimal(userId));
            if (userProfile == null) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.NO_SUCH_CLIENT);
                return resultMap;
            }
            // 1.参数检查
            // 税收类型
            if (tax_type_flag.equals("visible")) {
                if (!businessType.equals(Constant.BUSINESS_TYPE_SIGN_THIRTEEN)) {
                    if (StringUtil.isBlank(taxType)) {
                        resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_TAX_TYPE_ERROR);
                        return resultMap;
                    }
                }
            } else {
                taxType = "";
            }
            // 政要关系
            if (platform_type_flag.equals("visible")) {
                if (!businessType.equals(Constant.BUSINESS_TYPE_SIGN_THIRTEEN)) {
                    if (StringUtil.isBlank(platformType)) {
                        resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_PLATFORM_TYPE_ERROR);
                        return resultMap;
                    }
                }
            } else {
                platformType = "";
            }
            // 不良记录
            if (bad_record_flag.equals("visible")) {
                if (!businessType.equals(Constant.BUSINESS_TYPE_SIGN_THIRTEEN)) {
                    if (StringUtil.isBlank(badRecord)) {
                        resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_BAD_RECORD_ERROR);
                        return resultMap;
                    }
                }
            } else {
                badRecord = "";
            }
            // 投资期限
            if (term_investment_flag.equals("visible")) {
                if (!businessType.equals(Constant.BUSINESS_TYPE_SIGN_THIRTEEN)) {
                    if (StringUtil.isBlank(termInvestment)) {
                        resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_TERM_INVESTMENT_ERROR);
                        return resultMap;
                    }
                }
            } else {
                termInvestment = "";
            }
            // 投资品种
            if (investment_variety_flag.equals("visible")) {
                if (!businessType.equals(Constant.BUSINESS_TYPE_SIGN_THIRTEEN)) {
                    if (StringUtil.isBlank(investmentVariety)) {
                        resultMap =
                            ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_INVESTMENT_VARIETY_ERROR);
                        return resultMap;
                    }
                }
            } else {
                investmentVariety = "";
            }
            // 收益类型
            if (proceeds_type_flag.equals("visible")) {
                if (!businessType.equals(Constant.BUSINESS_TYPE_SIGN_THIRTEEN)) {
                    if (StringUtil.isBlank(proceedsType)) {
                        resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_PROCEEDS_TYPE_ERROR);
                        return resultMap;
                    }
                }
            } else {
                proceedsType = "";
            }
            // 控制关系
            if (con_relation_flag.equals("visible")) {
                if (!businessType.equals(Constant.BUSINESS_TYPE_SIGN_THIRTEEN)) {
                    if (StringUtil.isBlank(conRelation)) {
                        resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_CON_RELATION_ERROR);
                        return resultMap;
                    }
                }
            } else {
                conRelation = "";
            }
            // 受益人
            if (beneficiary_flag.equals("visible")) {
                if (!businessType.equals(Constant.BUSINESS_TYPE_SIGN_THIRTEEN)) {
                    if (StringUtil.isBlank(beneficiary)) {
                        resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_BENEFICIARY_ERROR);
                        return resultMap;
                    }
                }
            } else {
                beneficiary = "";
            }
            // 交易类型
            if (deal_type_flag.equals("visible")) {
                if (!businessType.equals(Constant.BUSINESS_TYPE_SIGN_THIRTEEN)) {
                    if (StringUtil.isBlank(dealType)) {
                        resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_DEAL_TYPE_ERROR);
                        return resultMap;
                    }
                }
            } else {
                dealType = "";
            }
            // 业务逻辑代码
            if (systype.equals(Constant.SYSTYPE_SIGN_ONE) || systype.equals(Constant.SYSTYPE_SIGN_TWO)) {
                // 3.新增 或 修改 user_presence
                UserPresence userPresence = userPresenceAtom.getByUserId(brokerId, userId);
                if (userPresence == null) {
                    userPresence = new UserPresence();
                    userPresence.setUserId(new BigDecimal(userId));
                    userPresence.setCreateTime(new Date());
                    userPresence.setStatus(new BigDecimal(StatusConstant.ACCOUNT_REGISTER));
                    userPresence.setUserStatus(new BigDecimal(StatusConstant.USER_STATUS_REGISTER));
                    userPresence.setOpenType("9");
                    String flowStatus = "000000000010";
                    userPresence.setUserFlowStatus(flowStatus);
                    userPresence.setAdequacyType("0");
                }
                userPresence.setBrokerId(brokerId);
                userPresence.setLastUpdateTime(new Date());
                userPresence.setLastUpdateBy(new BigDecimal(userId));

                // 记录变更数据
                Map<String, Object> map = new LinkedHashMap();
                if (businessType.equals(Constant.BUSINESS_TYPE_SIGN_FOURTEEN)
                    || businessType.equals(Constant.BUSINESS_TYPE_SIGN_TWENTY_TWO)
                    || businessType.equals(Constant.BUSINESS_TYPE_SIGN_TWENTY_FOUR)) {
                    map.put(Fields.TAX_TYPE, (userPresence.getTaxType() == null ? "" : userPresence.getTaxType()));
                    map.put(Fields.PLATFORM_TYPE,
                        (userPresence.getPlatformType() == null ? "" : userPresence.getPlatformType()));
                    map.put(Fields.TERM_INVESTMENT,
                        (userPresence.getTermInvestment() == null ? "" : userPresence.getTermInvestment()));
                    map.put(Fields.PROCEEDS_TYPE,
                        (userPresence.getProceedsType() == null ? "" : userPresence.getProceedsType()));
                    map.put(Fields.CON_RELATION,
                        (userPresence.getConRelation() == null ? "" : userPresence.getConRelation()));
                    map.put(Fields.BENEFICIARY,
                        (userPresence.getBeneficiary() == null ? "" : userPresence.getBeneficiary()));
                    map.put(Fields.INVESTMENT_VARIETY,
                        (userPresence.getInvestmentVariety() == null ? "" : userPresence.getInvestmentVariety()));
                    map.put(Fields.BAD_RECORD,
                        (userPresence.getBadRecord() == null ? "" : userPresence.getBadRecord()));
                    map.put(Fields.DEAL_TYPE, (userPresence.getDealType() == null ? "" : userPresence.getDealType()));
                }

                userPresence.setTaxType(taxType);
                userPresence.setPlatformType(platformType);
                userPresence.setBadRecord(badRecord);
                userPresence.setTermInvestment(termInvestment);
                userPresence.setInvestmentVariety(investmentVariety);
                userPresence.setProceedsType(proceedsType);
                userPresence.setConRelation(conRelation);
                userPresence.setBeneficiary(beneficiary);
                userPresence.setDealType(dealType);
                // 5.存储数据
                try {
                    // user_presence
                    if (null == userPresence.getId()) {
                        userPresenceAtom.save(userPresence);
                    } else {
                        userPresenceAtom.update(userPresence);
                    }
                    // user_profile
                    userProfileAtom.update(userProfile);
                    if (synchronousFlag.equals("1") && (businessType.equals(Constant.BUSINESS_TYPE_SIGN_FOURTEEN)
                        || businessType.equals(Constant.BUSINESS_TYPE_SIGN_TWENTY_TWO)
                        || businessType.equals(Constant.BUSINESS_TYPE_SIGN_TWENTY_FOUR))) {
                        // 4.存储受理单扩展信息数据
                        userLocalCommonService.saveBaseAcceptInfo(brokerId, userId, businessType, acceptNo,
                            JSON.toJSONString(map));
                        baseAccept.setSynchronousFlag("2");
                        baseAcceptAtom.updateBaseAccept(baseAccept);
                    }
                } catch (Exception e) {
                    return ReturnUtil.setErrorResult(resultMap, ErrorCode.COMMON_ERROR);
                }
            } else {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.CC_VALIDATE_SYSTYPE_ERROR);
            }
        }
        return resultMap;
    }

    @Override
    @Met
    public Map<String, Object> validateThdCust(UserInfoForm form) {
        // 初始化返回值
        Map<String, Object> resultMap = ReturnUtil.setErrorResult(new HashMap<String, Object>(), ErrorCode.SUCCESS);
        // 获取输入参数
        String broker_id = form.getBrokerId();
        String id_kind = form.getIdKind();
        String id_no = form.getIdNo();
        String op_station = form.getOpStation();
        String business_type = form.getBusinessType();
        String systype = form.getSystype() == null ? "" : form.getSystype();
        // 身份证号校验
        String res2 = ValidateInfoUtil.validateCID(id_no);
        if (!res2.equals(TextConstant.SUCCESS)) {
            resultMap.put(Fields.ERROR_NO, -1);
            resultMap.put(Fields.ERROR_INFO, res2);
            return resultMap;
        }
        String res = ValidateInfoUtil.validateBusinessType(business_type);
        if (!res.equals(TextConstant.SUCCESS)) {
            return ReturnUtil.setErrorResult(resultMap, ErrorCode.BIZ_TYPE_NOTEXIISTS);
        }
        if (systype.equals("")) {
            if (business_type.equals(Constant.BUSINESS_TYPE_SIGN_ELEVEN)) {
                systype = "1";
            } else {
                systype = "2";
            }
        }

        String enable_gm_sign = "visible".equals(ConfigUtil.get("enable.gm.sign", "invisible")) ? "1" : "0";
        String gm_otp = ConfigUtil.get("gm_otp", "visible");
        UserOtp userOtp = userOTpAtom.getUserOtp(broker_id, id_no);
        if (userOtp != null && "1".equals(enable_gm_sign) && "visible".equals(gm_otp)) {
            gm_otp = "1";
        } else {
            gm_otp = "0";
        }
        resultMap.put(Fields.GM_OTP, gm_otp);

        Boolean cmt = true;
        if (Constant.COUNTER_TIME_BUSINESS_TYPE.indexOf(business_type) != -1) {
            // 获取是否柜台维护时间
            cmt = userLocalCommonService.getCounterMaintenanceTime(broker_id);
        }
        if (cmt) {
            // 调用三方接口--验证客户身份
            Map<String, String> params = new HashMap<>();
            params.put(Fields.BROKER_ID, broker_id);
            params.put(Fields.ID_KIND, id_kind);
            params.put(Fields.ID_NO, id_no);
            params.put(Fields.OP_STATION, op_station);
            params.put(Fields.SYSTYPE, systype);
            Map<String, Object> vtcMap = counterService.validateThdCust(params);
            resultMap.put(Fields.MOBILE, "");
            resultMap.put(Fields.ERROR_NO, vtcMap.get(Fields.ERROR_NO));
            resultMap.put(Fields.ERROR_INFO, vtcMap.get(Fields.ERROR_INFO));
            Boolean as = false;
            String accountStatus = "";
            if (vtcMap.get(Fields.ACCOUNT_STATUS) != null) {
                as = true;
                accountStatus = vtcMap.get(Fields.ACCOUNT_STATUS).toString();
            }
            if (as) {
                if ("C".equals(accountStatus)) {
                    resultMap.put(Fields.ERROR_NO, "-703");
                    resultMap.put(Fields.ERROR_INFO, "柜台校验客户不存在(期货\\资管等业务都无客户信息)");
                    return resultMap;
                }
            }
            if (ValueUtil.getInt(vtcMap, Fields.ERROR_NO, -1) == 0) {
                // 获取 对象组 validate_info
                List<Map<String, Object>> validate_info =
                    JsonUtil.jsonToList(String.valueOf(vtcMap.get(Fields.VALIDATE_INFO)));
                if (validate_info != null && validate_info.size() > 0) {
                    Map<String, Object> validateInfoMap = validate_info.get(0);
                    String ifFlag =
                        validateInfoMap.get(Fields.IFFLAG) == null ? "" : validateInfoMap.get(Fields.IFFLAG).toString();
                    String telphone = validateInfoMap.get(Fields.TEL_PHONE) == null ? ""
                        : validateInfoMap.get(Fields.TEL_PHONE).toString();
                    if (ifFlag.equals("1")) {
                        // ===================开始=================== 获取账户状态
                        Map<String, Object> rqtfaMap = userLocalCommonService.qryThdFundAccount(broker_id, systype,
                            id_kind, id_no, "", op_station, as, accountStatus);
                        if (ValueUtil.getInt(rqtfaMap, Fields.ERROR_NO, -1) != 0) {
                            rqtfaMap.put(Fields.GM_OTP, gm_otp);
                            return rqtfaMap;
                        }
                        // ===================结束===================
                        Map<String, Object> map =
                            userLocalCommonService.qryClientMobileByIdNo(broker_id, id_kind, id_no);
                        if (ValueUtil.getInt(map, Fields.ERROR_NO, -1) != 0) {
                            map.put(Fields.GM_OTP, gm_otp);
                            return map;
                        } else {
                            resultMap.put(Fields.MOBILE, map.get(Fields.MOBILE));
                        }
                    } else {
                        if (telphone.equals("")) {
                            resultMap.put(Fields.ERROR_NO, "-702");
                            resultMap.put(Fields.ERROR_INFO, "柜台预留手机号有误(座机号或空)");
                            return resultMap;
                        } else {
                            String res12 = ValidateInfoUtil.validateMobilePhone(telphone);
                            if (!res12.equals(TextConstant.SUCCESS)) {
                                resultMap.put(Fields.ERROR_NO, "-702");
                                resultMap.put(Fields.ERROR_INFO, "柜台预留手机号有误(座机号或空)");
                                return resultMap;
                            } else {
                                resultMap.put(Fields.MOBILE, telphone);
                            }
                        }
                    }
                } else {
                    resultMap.put(Fields.ERROR_NO, "-1");
                    resultMap.put(Fields.ERROR_INFO, "接口返回 validate_info 错误");
                    return resultMap;
                }
            } else {
                if (ValueUtil.getInt(vtcMap, Fields.ERROR_NO, -1) == -703) {
                    // ===================开始=================== 获取账户状态
                    Map<String, Object> rqtfaMap = userLocalCommonService.qryThdFundAccount(broker_id, systype, id_kind,
                        id_no, "", op_station, as, accountStatus);
                    if (ValueUtil.getInt(rqtfaMap, Fields.ERROR_NO, -1) != 0) {
                        rqtfaMap.put(Fields.GM_OTP, gm_otp);
                        return rqtfaMap;
                    }
                    // ===================结束===================
                    Map<String, Object> map = userLocalCommonService.qryClientMobileByIdNo(broker_id, id_kind, id_no);
                    if (ValueUtil.getInt(map, Fields.ERROR_NO, -1) != 0) {
                        map.put(Fields.GM_OTP, gm_otp);
                        return map;
                    } else {
                        resultMap.put(Fields.MOBILE, map.get(Fields.MOBILE));
                        resultMap.put(Fields.ERROR_NO, map.get(Fields.ERROR_NO));
                        resultMap.put(Fields.ERROR_INFO, map.get(Fields.ERROR_INFO));
                    }
                }
            }
        } else {
            Map<String, Object> map = userLocalCommonService.qryClientMobileByIdNo(broker_id, id_kind, id_no);
            if (ValueUtil.getInt(map, Fields.ERROR_NO, -1) != 0) {
                map.put(Fields.GM_OTP, gm_otp);
                return map;
            } else {
                resultMap.put(Fields.ERROR_NO, map.get(Fields.ERROR_NO));
                resultMap.put(Fields.ERROR_INFO, map.get(Fields.ERROR_INFO));
                resultMap.put(Fields.MOBILE, map.get(Fields.MOBILE));
            }
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> qryProduct(UserInfoForm form) {
        // 初始化返回值
        Map<String, Object> resultMap = ReturnUtil.setErrorResult(new HashMap<String, Object>(), ErrorCode.SUCCESS);
        // 获取输入参数
        String brokerId = form.getBrokerId();
        String userId = form.getUserId();
        // 验证用户编号
        if (StringUtil.isBlank(userId)) {
            resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.NO_SUCH_CLIENT);
        } else {
            // 验证用户
            UserProfile userProfile = userProfileAtom.getUserProfileByUserId(new BigDecimal(userId));
            if (userProfile == null) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.NO_SUCH_CLIENT);
                return resultMap;
            }
            UserPresence userPresence = userPresenceAtom.getByUserId(brokerId, userId);
            if (userPresence == null) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.USER_INFO_ERROR);
            } else {
                String prodName = userPresence.getProdName() == null ? "" : userPresence.getProdName();
                resultMap.put(Fields.PROD_NAME, prodName);
            }
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> updateProduct(UserInfoForm form) {
        // 初始化返回值
        Map<String, Object> resultMap = ReturnUtil.setErrorResult(new HashMap<String, Object>(), ErrorCode.SUCCESS);
        // 获取输入参数
        String brokerId = form.getBrokerId();
        String userId = form.getUserId();
        String prodName = form.getProdName() == null ? "" : form.getProdName();
        String res8 = ValidateInfoUtil.validateAddress(prodName, 0, 100);
        if (!res8.equals(TextConstant.SUCCESS)) {
            resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.PRODUCT_NAME_ERROR);
            return resultMap;
        }
        // 验证用户编号
        if (StringUtil.isBlank(userId)) {
            resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.NO_SUCH_CLIENT);
        } else {
            // 验证用户
            UserProfile userProfile = userProfileAtom.getUserProfileByUserId(new BigDecimal(userId));
            if (userProfile == null) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.NO_SUCH_CLIENT);
                return resultMap;
            }
            UserPresence userPresence = userPresenceAtom.getByUserId(brokerId, userId);
            if (userPresence == null) {
                resultMap = ReturnUtil.setErrorResult(resultMap, ErrorCode.USER_INFO_ERROR);
            } else {
                userPresence.setProdName(prodName);
                try {
                    userPresenceAtom.update(userPresence);
                } catch (Exception e) {
                    return ReturnUtil.setErrorResult(resultMap, ErrorCode.COMMON_ERROR);
                }
            }
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> getCounterMaintenanceTime(UserInfoForm form) {
        // 初始化返回值
        Map<String, Object> resultMap = ReturnUtil.setErrorResult(new HashMap<String, Object>(), ErrorCode.SUCCESS);
        String brokerId = form.getBrokerId();
        try {
            Boolean cmt = userLocalCommonService.getCounterMaintenanceTime(brokerId);
            if (cmt) {
                resultMap.put(Fields.CMT_FLAG, "0");
            } else {
                resultMap.put(Fields.CMT_FLAG, "1");
            }
        } catch (Exception e) {
            return ReturnUtil.setErrorResult(resultMap, ErrorCode.COMMON_ERROR);
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> saveUserBaseLog(LogForm form) {
        // 初始化返回值
        Map<String, Object> resultMap = ReturnUtil.setErrorResult(new HashMap<String, Object>(), ErrorCode.SUCCESS);
        try {
            String brokerId = form.getBrokerId();
            String businessType = form.getBusinessType();
            String acceptNo = form.getAcceptNo();
            String logInfo = form.getLogInfo();
            String userId = form.getUserId();
            if (StringUtil.isEmpty(acceptNo) || StringUtil.isEmpty(logInfo) || StringUtil.isEmpty(businessType)) {
                return ReturnUtil.setErrorResult(resultMap, ErrorCode.PARAM_ERROR);
            }
            Map<String, Object> logMap = JsonUtil.jsonToMap(logInfo);
            Object isCall1 = logMap.remove("isCall");
            Object isConnect1 = logMap.remove("isConnect");
            int isCall = isCall1 == null ? 0 : Integer.parseInt(isCall1.toString());
            int isConnect = isConnect1 == null ? 0 : Integer.parseInt(isConnect1.toString());

            List<UserAcceptLog> userAcceptList = userAcceptLogAtom.getUserAccept(brokerId, acceptNo);
            if (userAcceptList != null && userAcceptList.size() > 0) {
                UserAcceptLog userAcceptLog = userAcceptList.get(0);
                String userLogInfo = userAcceptLog.getLogInfo();
                Map<String, Object> map = JsonUtil.jsonToMap(userLogInfo);
                // 发起视频的次数
                int callCount = map.get("callCount") == null ? 0 : Integer.parseInt(map.get("callCount").toString());
                // 接通的次数
                int connectCount =
                    map.get("connectCount") == null ? 0 : Integer.parseInt(map.get("connectCount").toString());
                if (isConnect > 0) {// 接通时 有empNo empName 本次接通相当于下一次的查看队列时的上次操作人员
                    map.put("empNoLast", logMap.get("empNo") == null ? "" : logMap.get("empNo"));// 上一次视频人员
                    map.put("empNameLast", logMap.get("empName") == null ? "" : logMap.get("empName"));// 上一次视频人员
                    // map.put("empNo",logMap.get("empNo"));
                    // map.put("empName",logMap.get("empName"));
                }
                map.put("callCount", callCount + isCall);
                map.put("connectCount", connectCount + isConnect);
                JSONObject jsonObject = JsonUtil.objToJson(map);
                userAcceptLog.setLogInfo(jsonObject.toString());
                userAcceptLogAtom.updateUserAcceptLog(userAcceptLog);
            } else {
                UserAcceptLog userAcceptLog = new UserAcceptLog();
                userAcceptLog.setBrokerId(brokerId);
                userAcceptLog.setAcceptNo(acceptNo);
                userAcceptLog.setUserId(BigDecimal.valueOf(Long.valueOf(userId)));
                userAcceptLog.setBusinessType(businessType);
                userAcceptLog.setCreateTime(new Date());
                userAcceptLog.setStatus("8");
                logMap.put("callCount", isCall);
                logMap.put("connectCount", isConnect);
                userAcceptLog.setLogInfo(JsonUtil.objToJson(logMap).toString());
                userAcceptLogAtom.saveUserAcceptLog(userAcceptLog);
            }
        } catch (Exception e) {
            log.error("视频日志记录异常：", e);
            return ReturnUtil.setErrorResult(resultMap, ErrorCode.COMMON_ERROR);
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> updateConfigZk(LogForm form) {
        // 初始化返回值
        Map<String, Object> resultMap = ReturnUtil.setErrorResult(new HashMap<String, Object>(), ErrorCode.SUCCESS);
        String category = form.getCategoryType();
        if (category == null || "".equals(category)) {
            return ReturnUtil.setErrorResult(resultMap, ErrorCode.PARAM_ERROR);
        }
        try {
            ConfigUtil.updateBrokerConfig(category);
        } catch (Exception e) {
            log.error("ZK更新异常，category=" + category, e);
            return ReturnUtil.setErrorResult(resultMap, ErrorCode.COMMON_ERROR);
        }
        if ("gm".equals(category)) {
            // 国密修改后清除初始化的操作
            NetPassUtil.clearConfig();
        }

        log.info("同步ZK配置成功：category=" + category);
        return resultMap;
    }

}
