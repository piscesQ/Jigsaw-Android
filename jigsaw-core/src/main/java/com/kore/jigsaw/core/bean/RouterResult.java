package com.kore.jigsaw.core.bean;

/**
 * @author koreq
 * @date 2021-04-20
 * @description 路由跳转返回的 bean
 */
public class RouterResult {
    private boolean isSuccess;
    private String errorMsg;

    public RouterResult(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public RouterResult(boolean isSuccess, String errorMsg) {
        this.isSuccess = isSuccess;
        this.errorMsg = errorMsg;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return "RouterResult{" +
                "isSuccess=" + isSuccess +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }
}
