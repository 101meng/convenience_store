package com.lin101.store.common;

/**
 * API 业务状态码与中文提示：覆盖认证、用户资料、购物车、订单等模块，避免控制器硬编码文案。
 */
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAILED(500, "服务器开小差了，请稍后再试"),
    VALIDATE_FAILED(400, "参数检验失败"),

    SEND_CODE_SUCCESS(200, "验证码发送成功"),
    SEND_CODE_FAILED(500, "验证码发送失败，请稍后再试"),
    LOGIN_SUCCESS(200, "登录成功"),
    LOGIN_FAILED(400, "验证码错误或已过期，登录失败"),
    ADMIN_SEND_CODE_SUCCESS(200, "管理端验证码发送成功"),
    ADMIN_SEND_CODE_FAILED(500, "管理端验证码发送失败，请稍后再试"),
    ADMIN_LOGIN_SUCCESS(200, "管理端登录成功"),
    ADMIN_LOGIN_FAILED(400, "管理端账号不存在、已禁用或验证码错误"),
    FORBIDDEN(403, "您没有权限执行此操作"),

    UPDATE_PROFILE_SUCCESS(200, "个人资料更新成功"),
    UPDATE_PROFILE_FAILED(500, "个人资料更新失败，请重试"),

    CART_ADD_SUCCESS(200, "已成功加入购物车"),
    CART_ADD_FAILED(500, "加入购物车失败，请重试"),
    CART_LIST_FAILED(500, "获取购物车列表失败"),
    CART_UPDATE_SUCCESS(200, "商品数量已更新"),
    CART_UPDATE_FAILED(500, "商品数量更新失败"),
    CART_REMOVE_SUCCESS(200, "商品已移出购物车"),
    CART_REMOVE_FAILED(500, "移出购物车失败"),
    CART_DELETE_FAILED(500, "删除购物车失败"),

    ORDER_SUBMIT_SUCCESS(200, "订单提交成功，即将跳转"),
    ORDER_SUBMIT_FAILED(500, "订单提交失败，请重试"),
    ORDER_CART_EMPTY(400, "购物车为空，无法下单"),


    PRODUCT_NOT_FOUND(500, "找不到商品");
    private final long code;
    private final String message;

    ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
