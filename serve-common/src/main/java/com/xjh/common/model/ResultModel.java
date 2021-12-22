package com.xjh.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xjh.common.exception.CommonErrorCode;
import com.xjh.common.exception.CommonException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

public class ResultModel<T> {
    private Integer code;
    private T data;
    private String requestId;
    private String resultMsg;
    private String errorStackTrace;

    private ResultModel() {
        this.code = CommonErrorCode.SUCCESS.getCode();
        this.resultMsg = CommonErrorCode.SUCCESS.getDesc();
    }

    public static ResultModel success() {
        return success((Object) null);
    }

    @JsonIgnore
    public boolean isSuccess() {
        return this.code.equals(CommonErrorCode.SUCCESS.getCode());
    }

    public static <T> ResultModel<T> success(T data) {
        return success(data, CommonErrorCode.SUCCESS.getDesc());
    }

    public static <T> ResultModel<T> success(T data, String msg) {
        return new ResultModel(CommonErrorCode.SUCCESS.getCode(), data, msg, (String) null);
    }

    public static <T> ResultModel<T> fail(CommonException ex) {
        String resultMsg = ex.getError().getDesc();
        if (StringUtils.isNotEmpty(ex.getErrMsg())) {
            resultMsg = ex.getErrMsg();
        }

        return new ResultModel(ex.getError().getCode(), (Object) null, resultMsg, ex.getMessage());
    }

    public static ResultModel<Object> fail(CommonException ex, Object data) {
        String resultMsg = ex.getError().getDesc();
        if (StringUtils.isNotEmpty(ex.getErrMsg())) {
            resultMsg = ex.getErrMsg();
        }

        return new ResultModel(ex.getError().getCode(), data, resultMsg, ex.getMessage());
    }

    public static ResultModel<Object> fail(CommonErrorCode error) {
        return new ResultModel(error.getCode(), (Object) null, error.getDesc(), (String) null);
    }

    public static ResultModel<Object> fail(CommonErrorCode error, String msg) {
        return new ResultModel(error.getCode(), (Object) null, msg, (String) null);
    }

    public static ResultModel<Object> fail(CommonErrorCode error, Object data, String msg) {
        return new ResultModel(error.getCode(), data, msg, (String) null);
    }

    public static ResultModel<Object> fail(CommonErrorCode error, Object data, Throwable ex) {
        return fail(error, data, error.getDesc(), ex);
    }

    public static ResultModel<Object> fail(CommonErrorCode error, Throwable ex) {
        return new ResultModel(error.getCode(), (Object) null, error.getDesc(), ex.getMessage());
    }

    public static ResultModel<Object> fail(CommonErrorCode error, String msg, Throwable ex) {
        return fail(error, (Object) null, msg, ex);
    }

    public static ResultModel<Object> fail(CommonErrorCode error, Object data, String msg, Throwable ex) {
        return new ResultModel(error.getCode(), data, msg, ex.getMessage());
    }

    public ResultModel(int code, T data, String resultMsg, String errorStackTrace) {
        this.code = CommonErrorCode.SUCCESS.getCode();
        this.resultMsg = CommonErrorCode.SUCCESS.getDesc();
        this.code = code;
        this.data = data;
        this.resultMsg = resultMsg;
        this.errorStackTrace = errorStackTrace;
        this.requestId = MDC.get("MDC_LOG_ID");
    }

    public Integer getCode() {
        return this.code;
    }

    public String getErrorStackTrace() {
        return this.errorStackTrace;
    }

    public String getResultMsg() {
        return this.resultMsg;
    }

    public T getData() {
        return this.data;
    }

    public String getRequestId() {
        return this.requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
