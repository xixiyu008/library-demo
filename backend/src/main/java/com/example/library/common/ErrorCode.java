package com.example.library.common;

public enum ErrorCode {
    SUCCESS(0, "success"),
    PARAM_ERROR(40000, "请求参数错误"),
    AUTH_FAILED(40001, "认证失败"),
    FORBIDDEN(40300, "无权限"),
    NOT_FOUND(40400, "资源不存在"),
    CONFLICT(40900, "业务冲突"),
    SYSTEM_ERROR(50000, "系统内部错误");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
