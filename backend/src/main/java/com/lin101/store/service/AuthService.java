package com.lin101.store.service;

import java.util.Map;

/** 登录注册：验证码与 JWT（实现见 {@link com.lin101.store.service.impl.AuthServiceImpl}）。 */
public interface AuthService {

    void sendVerificationCode(String phone);

    Map<String, Object> loginAndRegister(String phone, String code);
}