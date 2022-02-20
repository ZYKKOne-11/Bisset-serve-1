package com.xjh.common.exception;

public enum CommonErrorCode {
    SUCCESS(200, "执行成功"),
    USER_NOT_LOGIN(400, "用户未登录"),
    VALIDATE_ERROR(401, "校验数据错误"),
    NOT_EXIST(404, "不存在"),
    FORBIDDEN(403, "权限不足"),
    RESOURCE_NOT_FOUND(404, "资源未找到"),
    RESOURCE_CONFLICT(409, "资源冲突"),
    REQUEST_PARAM_ERROR(412, "参数错误"),
    REQUEST_REGEX_ERROR(413, "参数正则表达式错误"),
    PRECONDITION_FAILED(428, "要求先决条件"),
    NOT_SUPPORT(409, "不支持的请求"),
    WRONG_USERNAME_OR_PASSWORD(490, "用户名或密码错误"),
    WRONG_OPEN_API_AUTHORITY(491, "认证错误"),
    UNKNOWN_ERROR(500, "系统未知错误"),
    SERVER_POWER_LESS(501, "服务器无法完成该请求"),
    RETRY_ERROR(502, "服务器重试出错"),
    UPDATE_PASSWORD(599, "密码不安全"),
    OPTIMISTIC_LOCK_VERSION_ERROR(600, "乐观锁版本错误"),
    CS_CONNECT_ERROR(492, "已连接");

    private Integer code;
    private String desc;

    CommonErrorCode(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }
}
