package com.xjh.common.exception;

import org.apache.commons.lang3.StringUtils;

public class CommonException extends RuntimeException {
    protected String errMsg;
    protected String detailMsg;
    protected CommonErrorCode error;
    protected Object data;

    public CommonException(CommonErrorCode error) {
        super(error.getDesc());
        this.error = error;
        this.errMsg = error.getDesc();
        this.detailMsg = error.getDesc();
    }

    public CommonException(CommonErrorCode error, String errMsg) {
        super(errMsg);
        this.error = error;
        this.errMsg = errMsg;
        this.detailMsg = errMsg;
    }

    public CommonException(CommonErrorCode error, String errMsg, String detailMsg) {
        super(StringUtils.isEmpty(detailMsg) ? errMsg : detailMsg);
        this.error = error;
        this.errMsg = errMsg;
        this.detailMsg = detailMsg;
    }

    public CommonException(CommonErrorCode error, String errMsg, Throwable cause) {
        super(errMsg, cause);
        this.error = error;
        this.errMsg = errMsg;
        this.detailMsg = errMsg;
    }

    public CommonException(CommonErrorCode error, String errMsg, String detailMsg, Throwable cause) {
        super(StringUtils.isEmpty(detailMsg) ? errMsg : detailMsg, cause);
        this.error = error;
        this.errMsg = errMsg;
        this.detailMsg = detailMsg;
    }

    public CommonException(CommonErrorCode error, Throwable cause) {
        super(error.getDesc(), cause);
        this.error = error;
        this.errMsg = error.getDesc();
        this.detailMsg = error.getDesc();
    }

    public String getErrMsg() {
        return this.errMsg;
    }

    public CommonErrorCode getError() {
        return this.error;
    }

    public String getDetailMsg() {
        return this.detailMsg;
    }

    public void setDetailMsg(String detailMsg) {
        this.detailMsg = detailMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public void setError(CommonErrorCode error) {
        this.error = error;
    }

    public Object getData() {
        return this.data;
    }

    public CommonException setData(Object data) {
        this.data = data;
        return this;
    }
}
