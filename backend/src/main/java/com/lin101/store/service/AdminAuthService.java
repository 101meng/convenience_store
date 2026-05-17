package com.lin101.store.service;

import java.util.Map;

public interface AdminAuthService {
    void sendVerificationCode(String phone);

    Map<String, Object> login(String phone, String inputCode);
}
