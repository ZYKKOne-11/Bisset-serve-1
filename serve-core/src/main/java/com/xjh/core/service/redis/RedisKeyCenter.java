package com.xjh.core.service.redis;


import com.xjh.common.utils.MyDateUtils;

public class RedisKeyCenter {
    public RedisKeyCenter() {
    }

    public static String getDayPasswordErrorCount(Long userId) {
        return String.format("user:password:error:count:%s", userId);
    }

    public static String getNeedUpdatePassword(Long userId) {
        return String.format("user:password:update:%s", userId);
    }

    public static String fujianPoliceHangupKey(String caseNo) {
        return String.format("fujianpolice:hangup:data:%s", caseNo);
    }

    public static String isQuestionDataRedisKey(Long dialogFlowId) {
        return String.format("dialogManager:faqQuestion:dialogFlowId:%s", dialogFlowId);
    }

    public static String getDialogFinishStepRedisKey(Long robotCallTaskId) {
        return String.format("dialogManager:finish_step:record_id:%s", robotCallTaskId);
    }

    public static String getRandomPasswordRedisKey(String phoneNumber) {
        return String.format("user_token:random_password:%s", phoneNumber);
    }

    public static String getTenantOpenApiTokenRedisKey(String appKey, String appSecret) {
        return String.format("open_api:token:appKey%s:appSecret%s", appKey, appSecret);
    }

    public static String getTenantOpenApiTokenRedisKeyPattern() {
        return "open_api:token:appKey*";
    }

    public static String getUserLoginInfoRedisKey(String token) {
        return String.format("user_token:user_login_info:%s", token);
    }

    public static String getUserWsLoginInfo(String name) {
        return String.format("user_ws_info:%s", name);
    }

    public static String getUserLoginTokenRedisKey(String userId) {
        return String.format("user_token:user_login_token:%s", userId);
    }

    public static String getTextConnectionListRedisKey(Long tenantId) {
        return String.format("text_service:visit_connection_list:%s", tenantId);
    }

    public static String getVisitSessionRedisKey(String visitorInfoNum) {
        return String.format("text_service:visit_session:%s", visitorInfoNum);
    }

    public static String getTenantTodayRobotCount(Long tenantId) {
        String today = MyDateUtils.formatLocalDateYYYYMMDD();
        return String.format("TenantRobotCount:TenantId%s:%s", tenantId, today);
    }

    public static String getPhoneNumberRedisKey(Long tenantId, String phoneNumber) {
        return String.format("WhiteListPhoneNumber:TenantId%s:%s", tenantId, phoneNumber);
    }

    public static String getRobotCallJobTransferIndex(Long tenantId, Long robotCallJobId) {
        return String.format("RobotCallJobTransferIndex:TenantId%s:%s", tenantId, robotCallJobId);
    }

    public static String getUserPrimaryRedisKey(Number userId) {
        return String.format("User:userId:%s", userId);
    }

    public static String getUserPrimaryRedisKeyPattern() {
        return "User:userId:*";
    }

    public static String getTenantPrimaryRedisKey(Number tenantId) {
        return String.format("Tenant:tenantId:%s", tenantId);
    }

    public static String getTenantPrimaryRedisKeyPattern() {
        return "Tenant:tenantId:*";
    }

    public static String getDistributorPrimaryRedisKey(Number distributorId) {
        return String.format("Distributor:distributorId:%s", distributorId);
    }

    public static String getDistributorPrimaryRedisKeyPattern() {
        return "Distributor:distributorId:*";
    }

    public static String getPhoneNumberPrimaryRedisKey(Number phoneNumberId) {
        return String.format("PhoneNumber:phoneNumberId:%s", phoneNumberId);
    }

    public static String getPhoneNumberPrimaryRedisKeyPattern() {
        return "PhoneNumber:phoneNumberId:*";
    }

    public static String getTryFreeUserCall(String openId) {
        String today = MyDateUtils.formatLocalDateYYYYMMDD();
        return String.format("TryFreeUserCall:openId%s:%s", openId, today);
    }

    public static String getCountAsrConcurrencyKey() {
        return "AsrConcurrency:";
    }

    public static String getCountYiwiseAsrConcurrencyKey() {
        return "AsrYiwiseConcurrency";
    }

    public static String getYiwiseAsrServerConcurrencyCountKey() {
        return "AsrYiwiseServerConcurrencyCount";
    }

    public static String getVerificationCode(String phoneNumber) {
        return String.format("VerificationCode:phoneNumber%s", phoneNumber);
    }

    public static String getWhiteListRedisKey(Long tenantId, Long customerPersonId) {
        return String.format("WhiteListPhoneNumber:TenantId%s:%s", tenantId, customerPersonId);
    }

    public static String getWhiteListRedisKeyPatten(Long tenantId) {
        return String.format("WhiteListPhoneNumber:TenantId%s:*", tenantId);
    }

    public static String getWechatLoginStatusKey(String callBackId) {
        return String.format("userLogin_%s", callBackId);
    }

    public static String getDialogFlowLastRecordTimeRedisKey(Long dialogFlowId) {
        return String.format("DialogFlow:LastRecordTime:DialogFlowId%s", dialogFlowId);
    }

    public static String getBossShowCensorRedisKey(Long dialogFlowId) {
        return String.format("BossShowCensor:DialogFlowId%s", dialogFlowId);
    }

    public static String getIpWhiteListRedisKey() {
        return "WhiteList:IPWhiteList:Content";
    }

    public static String getFollowStatusCountRedisKey(Long tenantId, Long followUserId) {
        return String.format("FollowStatusCount:TenantId%s:%s", tenantId, followUserId);
    }

    public static String getUserApiResourceListRedisKey(Long userId) {
        return String.format("User:UserApiResourceMapping:%s", userId);
    }

    public static String getUserIsBuiltInSuperAdmin(Long userId) {
        return String.format("User:IsBuiltInSuperAdmin:%s", userId);
    }

    public static String getIntentLevelTagDetailListRedisKey(Long intentLevelTagId) {
        return String.format("IntentLevelTagDetailList:IntentLevelTagId:%s", intentLevelTagId);
    }

    public static String getIntentLevelTagRedisKey(Long intentLevelTagId) {
        return String.format("IntentLevelTag:IntentLevelTagId:%s", intentLevelTagId);
    }

    public static String getIntentLevelTagDetailRedisKey(Long intentLevelTagId, Integer code) {
        return String.format("IntentLevelTagDetail:IntentLevelTagId:%s:%s", intentLevelTagId, code);
    }

    public static String getIntentLevelTagDetailRedisKeyPatten() {
        return String.format("IntentLevelTagDetail:IntentLevelTagId:*:*");
    }

    public static String getIntentLevelTagDetailListRedisKeyPatten() {
        return String.format("IntentLevelTagDetailList:IntentLevelTagId:*");
    }

    public static String getIntentLevelTagRedisKeyPatten() {
        return String.format("IntentLevelTag:IntentLevelTagId:*");
    }

    public static String getIntentLevelTagDetailRedisKeyPatten(Long intentLevelTagId) {
        return String.format("IntentLevelTagDetail:IntentLevelTagId:%s:*", intentLevelTagId);
    }

    public static String getTenantDefaultIntentLevelTagRedisKey(Long tenantId) {
        return String.format("TenantDefaultIntentLevelTag:TenantId:%s", tenantId);
    }

    public static String getDistributorDefaultIntentLevelTagRedisKey(Long distributorId) {
        return String.format("DistributorDefaultIntentLevelTag:DistributorId:%s", distributorId);
    }

    public static String getTenantDefaultIntentLevelTagRedisKeyPatten() {
        return String.format("TenantDefaultIntentLevelTag:TenantId:*");
    }

    public static String getDistributorDefaultIntentLevelTagRedisKeyPatten() {
        return String.format("DistributorDefaultIntentLevelTag:DistributorId:*");
    }

    public static String getSpringBatchJobIdleCountRedisKey() {
        return "SpringBatchJob:IdleCount";
    }

    public static String getSpringBatchStatusRedisKey() {
        return "SpringBatchJob:EnabledStatus";
    }

    public static String getSpringBatchStopIdSetRedisKey() {
        return "SpringBatchJob:StoppedJobInstanceIdSet";
    }

    public static String getDoorOpeUserIdRedisKey(String opeToken) {
        return String.format("Door:opeUserId:%s", opeToken);
    }

    public static String getOPEDefaultIntentLevelTagRedisKey() {
        return "DistributorDefaultIntentLevelTag:OPE";
    }

    public static String getUserUsedThisVersionRedisKey(Long userId) {
        return String.format("TenantUsedThisVersion:UserId:%s", userId);
    }

    public static String getUserUsedThisVersionRedisKeyPatten() {
        return "TenantUsedThisVersion:TenantId:*";
    }

    public static String getIntentCustomerCountRedisKey(Long tenantId, Long robotCallJobId) {
        return String.format("IntentCustomerCount:TenantId:%s:%s", tenantId, robotCallJobId);
    }

    public static String getRandomStringSeedRedisKey() {
        return "RandomStringSeed";
    }

    public static String getRandomStringSecretKeyRedisKey() {
        return "RandomStringSecretKey";
    }

    public static String getTempIpListRediskey() {
        return "TempIpListKey";
    }

    public static String getAuthIpListRedisKey() {
        return "AuthIpListKey";
    }

    public static String getAuthSecretKeyFile() {
        return "AuthKeyFile";
    }

    public static String getCallInStaffInfoListKeyByStaffGroupId(Long staffGroupId) {
        return String.format("CallIn:StaffInfoList:CsStaffGroupId:%s", staffGroupId);
    }

    public static String getCallInAiReceptionConcurrencyCount(Long csStaffGroupId) {
        return String.format("CallIn:AiReceptionConcurrencyCount:CsStaffGroupId:%s", csStaffGroupId);
    }

    public static String getCallInStaffLastHangUpTime(Long staffId) {
        return String.format("CallIn:StaffLastHangUpTime:CsStaffInfoId:%s", staffId);
    }

    public static String getCallInStaffIsOnline(Long staffId) {
        return String.format("CallIn:StaffIsOnLine:CsStaffInfoId:%s", staffId);
    }

    public static String getCallInStaffIsCalling(Long staffId) {
        return String.format("CallIn:StaffIsCalling:CsStaffInfoId:%s", staffId);
    }

    public static String getIntentMessageStatusCount(Long tenantId, Long robotCallJobId) {
        return String.format("IntentMessageStatusCount:TenantId:%s:%s", tenantId, robotCallJobId);
    }

    public static String getCsSeatGroupListRedisKey(Long tenantId, Long staffGroupId) {
        return String.format("CsSeatGroup:TenantId:%s:%s", tenantId, staffGroupId);
    }

    public static String getCallJobCallDetailRedisKey(String identifyId) {
        return String.format("CallDetail:JobId:%s", identifyId);
    }

    public static String getCallJobCsCallTransferMonitorFlagRedisKey(Long robotCallJobId, String identifyId) {
        return String.format("CsSeatMonitor:JobId:%s:%s", robotCallJobId, identifyId);
    }

    public static String getCallJobCsCallTransferHangUpRedisKey(Long robotCallJobId, String identifyId) {
        return String.format("CsSeatHangUp:JobId:%s:%s", robotCallJobId, identifyId);
    }

    public static String getCsSeatListRedisKey(Long tenantId) {
        return String.format("CsSeatList:TenantId:%s", tenantId);
    }

    public static String getCallPolicyGroupLastModifyTime(Long policyGroupId) {
        return String.format("CallPolicyGroupLastModify:PolicyGroupId:%s", policyGroupId);
    }

    public static String getCustomerPersonExtraFieldList(Long tenantId) {
        return String.format("CustomerPersonExtraFieldList:TenantId:%s", tenantId);
    }

    public static String getCustomerPersonExtraFieldListPatten() {
        return String.format("CustomerPersonExtraFieldList:TenantId:*");
    }

    public static String getSystemMaintain() {
        return "SystemMaintain";
    }

    public static String getGatewayStatusKey(String deviceId) {
        return String.format("GatewayStatus:DID:%s", deviceId);
    }

    public static String getPhoneNumberStatusKey(Long phoneNumberId) {
        return String.format("PhoneNumberStatus:PID:%s", phoneNumberId);
    }

    public static String getCallBaiDuTTSKey(String time) {
        return String.format("BaiDuTTSCount:%s:", time);
    }

    public static String getCallBiaoBeiTTSKey(String time) {
        return String.format("BiaoBeiTTSCount:%s", time);
    }

    public static String getCallBiaoBeiTTSConcurrencyKey() {
        return String.format("BiaoBeiTTSConcurrencyKey");
    }

    public static String getCallBiaoBeiTTSQpsKey(String time) {
        return String.format("BiaoBeiTTSQpsKey:%s", time);
    }

    public static String getCallYiwiseTTSKey(String time) {
        return String.format("YiwiseTTSCount:%s", time);
    }

    public static String getCallYiwiseTTS2Key(String time) {
        return String.format("YiwiseTTS2Count:%s", time);
    }

    public static String getCallYiwiseTTS2Key() {
        return "YiwiseTTS2Count";
    }

    public static String getCallHuaweiTTSKey(String time) {
        return String.format("HuaweiTTSCount:%s", time);
    }

    public static String getCallAliyunTTSKey(String time) {
        return String.format("AliyunTTSCount:%s", time);
    }

    public static String getCallTtsHitKey(String time) {
        return String.format("TtsCallHit:%s", time);
    }

    public static String getCallTtsCountKey(String time) {
        return String.format("TtsCallTotal:%s", time);
    }

    public static String getConcurrencyCalledCountByGatewayKey(String prefix, String time) {
        return String.format("concurrencyCalledCount:%s:%s", prefix, time);
    }

    public static String getGatewayClearedKey(String deviceId) {
        return String.format("gatewayCleared:DID:%s", deviceId);
    }

    public static String getAudioMinAppToken() {
        return "AudioMinAppTokenKey";
    }

    public static String getLineStatsViewKey(Long tenantId) {
        return String.format("LineStats:ViewEnabled:TID:%s", tenantId);
    }

    public static String getLineStatsKey(Long phoneNumberId, int twentyFourHour) {
        return String.format("LineStats:PID:%s:%s", phoneNumberId, twentyFourHour);
    }

    public static String getDataBakerAccessToken() {
        return "DataBakerAccessToken";
    }

    public static String getDataBakerBackupAccessToken() {
        return "DataBakerBackupAccessToken";
    }

    public static String getYiwiseTtsToken() {
        return "YiwiseTtsToken";
    }

    public static String customerDayCallCount(String day, String phoneNumber) {
        return String.format("CusDayCall:%s:%s", day, phoneNumber);
    }

    public static String customerNotExistCallCount(String day, String phoneNumber) {
        return String.format("CusNotExist:%s:%s", day, phoneNumber);
    }

    public static String customerNotServiceCallCount(String day, String phoneNumber) {
        return String.format("CusNotService:%s:%s", day, phoneNumber);
    }

    public static String tenantCustomerDayCallCount(Long tenantId, String day, String phoneNumber) {
        return String.format("TenantCusDayCall:%s:%s:%s", tenantId, day, phoneNumber);
    }

    public static String tenantCustomerNotExistCallCount(Long tenantId, String day, String phoneNumber) {
        return String.format("TenantCusNotExist:%s:%s:%s", tenantId, day, phoneNumber);
    }

    public static String tenantCustomerNotServiceCallCount(Long tenantId, String day, String phoneNumber) {
        return String.format("TenantCusNotService:%s:%s:%s", tenantId, day, phoneNumber);
    }

    public static String CsEslRecordMap(Long tenantId) {
        return String.format("CsEslRecordMap:%s", tenantId);
    }

    public static String getCsSeatListRedisKeyPattern() {
        return "CsSeatList:TenantId:*";
    }

    public static String csNotify(Long tenantId, Long csJobRecordId) {
        return String.format("csNotify:%s:%s", tenantId, csJobRecordId);
    }

    public static String getPolicyGroupCyclicCount(Long tenantId, Long robotCallJobId) {
        return String.format("policyGroupCyclicCount:%s:%s", tenantId, robotCallJobId);
    }

    public static String huaweiToken() {
        return "huawei:token";
    }

    public static String getAsrModelRecord(String recordId) {
        return String.format("AsrModelRecord:%s", recordId);
    }

    public static String getAudioMinAppTokenNew(Integer currEnv, String system) {
        return system + "-AudioMinAppTokenKey-" + currEnv;
    }

    public static String currentCustomerId() {
        return "currentOpeCustomerId";
    }

    public static String getWeiBaoCallStatus(String callSid) {
        return String.format("WeiBaoCallStatus:%s", callSid);
    }

    public static String getWeiBaoCompanyCode(Long robotCallJobId) {
        return String.format("WeiBaoCompanyCode:%s", robotCallJobId);
    }

    public static String getWeiBaoCampaignCode(Long robotCallJobId) {
        return String.format("WeiBaoCampaignCode:%s", robotCallJobId);
    }

    public static String getVerificationCodeCount(String phoneNumber) {
        return String.format("VerificationCodeCount:phoneNumber%s", phoneNumber);
    }

    public static String getCaptchaCode(String key) {
        return String.format("CaptchaCode:PH%s", key);
    }

    public static String getCsStaffAutoAnswer(Long userId) {
        return String.format("CsStaffAutoAnswer:%s", userId);
    }

    public static String getCsStatus(Long csStaffId) {
        return String.format("CsStatus:%s", csStaffId);
    }

    public static String getCsWorkStatus(Long csStaffId) {
        return String.format("CsWorkStatus:%s", csStaffId);
    }

    public static String getCsOperationStatus(Long csStaffId) {
        return String.format("CsOperationStatus:%s", csStaffId);
    }

    public static String lastStaffStatus(Long csStaffId) {
        return String.format("lastStaffStatus:staffId:%s", csStaffId);
    }

    public static String groupQueueCount(Long csStaffGroupId) {
        return String.format("StaffGroupQueueCount:groupId:%s", csStaffGroupId);
    }

    public static String getCsStatusRedisKeyPattern() {
        return "CsStatus:*";
    }

    public static String getCsConnectKey(Long csStaffId) {
        return String.format("CsConnect:%s", csStaffId);
    }

    public static String getCsConnectKeyRedisKeyPattern() {
        return "CsConnect:*";
    }

    public static String getCsSessionKey(Long userId) {
        return String.format("CsSession:%s", userId);
    }

    public static String getCsPublicSessionKey(String visitorInfoNum) {
        return String.format("CsPublicSession:%s", visitorInfoNum);
    }

    public static String HumanToCsCallInfo(String callId) {
        return String.format("HumanToCsCallInfo:%s", callId);
    }

    public static String ReceptionQueueCount(Long callInReceptionId) {
        return String.format("ReceptionQueueCount:%s", callInReceptionId);
    }

    public static String ReceptionCallCount(Long callInReceptionId) {
        return String.format("ReceptionCallCount:%s", callInReceptionId);
    }

    public static String getWorkOrderSeqKey() {
        return String.format("WorkOrderSeq:%s", MyDateUtils.formatLocalDateYYYYMMDD());
    }

    public static String getTransferCallKey(String identifyId) {
        return String.format("TransferCall:%s", identifyId);
    }

    public static String getTransferCallInfoKey(Long tenantId, Long csStaffId) {
        return String.format("TransferCallInfo:%s:%s", tenantId, csStaffId);
    }

    public static String getCsHangUpKey(String identifyId) {
        return String.format("CsHangUp:%s", identifyId);
    }

    public static String getCsAnswerKey(String identifyId) {
        return String.format("CsAnswer:%s", identifyId);
    }

    public static String getCallInHeaderEslKey(String identifyId) {
        return String.format("CallInHeaderEsl:%s", identifyId);
    }

    public static String getTextStaffQueueKey(Long csStaffId) {
        return String.format("TextStaffQueue:%s", csStaffId);
    }

    public static String getTextStaffGroupQueueKey(Long csStaffGroupId) {
        return String.format("TextStaffGroupQueue:%s", csStaffGroupId);
    }

    public static String getTextVisitorQueueMap(Long tenantId) {
        return String.format("TextVisitorQueueMap:%s", tenantId);
    }

    public static String getIvrTransferNum(String eid) {
        return String.format("IvrTransferNum:%s", eid);
    }
}

