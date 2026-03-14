package com.lin101.store.common;

import lombok.Data;

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

    // --- 成功返回方法 ---

    // 1. 完全使用枚举类（无数据体）
    public static <T> Result<T> success(ResultCode resultCode) {
        return new Result<T>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    // 2. 使用枚举类 + 数据体
    public static <T> Result<T> success(ResultCode resultCode, T data) {
        return new Result<T>(resultCode.getCode(), resultCode.getMessage(), data);
    }

    // --- 失败返回方法 ---

    // 1. 完全使用枚举类（无数据体）
    public static <T> Result<T> failed(ResultCode resultCode) {
        return new Result<T>(resultCode.getCode(), resultCode.getMessage(), null);
    }
}