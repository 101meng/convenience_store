package com.lin101.store.common;

import lombok.Data;

/**
 * 统一 HTTP JSON 结构（code / message / data），与各业务 {@link ResultCode} 枚举配合使用。
 */
@Data
public class Result<T> {

    private long code;
    private String message;
    private T data;

    protected Result() {}

    protected Result(long code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success(ResultCode resultCode) {
        return new Result<T>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    public static <T> Result<T> success(ResultCode resultCode, T data) {
        return new Result<T>(resultCode.getCode(), resultCode.getMessage(), data);
    }

    public static <T> Result<T> failed(ResultCode resultCode) {
        return new Result<T>(resultCode.getCode(), resultCode.getMessage(), null);
    }
}