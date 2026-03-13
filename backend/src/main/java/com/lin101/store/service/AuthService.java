package com.lin101.store.service;

import java.util.Map;

public interface AuthService {

    /**
     * 发送验证码
     */
    void sendVerificationCode(String phone);

    /**
     * 校验验证码并执行登录/静默注册
     * 返回 Token 和 用户信息的 Map
     */
    Map<String, Object> loginAndRegister(String phone, String code);
}