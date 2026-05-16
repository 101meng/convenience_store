package com.lin101.store.controller;

import com.lin101.store.common.Result;
import com.lin101.store.common.ResultCode;
import com.lin101.store.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证接口（无需 JWT）。验证码写入 Redis（连接信息见 {@code application.properties}），短信为控制台模拟输出。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 向指定手机号发送 6 位验证码（有效期 5 分钟）。
     */
    @GetMapping("/sendCode")
    public Result<Void> sendCode(@RequestParam String phone) {
        try {
            authService.sendVerificationCode(phone);
            return Result.success(ResultCode.SEND_CODE_SUCCESS);
        } catch (Exception e) {
            return Result.failed(ResultCode.SEND_CODE_FAILED);
        }
    }

    /**
     * 校验验证码；首次手机号自动创建用户（静默注册），返回 JWT 与用户信息。
     *
     * @param requestData JSON：{@code phone}、{@code code}
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> requestData) {
        try {
            String phone = requestData.get("phone");
            String code = requestData.get("code");

            Map<String, Object> authResult = authService.loginAndRegister(phone, code);

            return Result.success(ResultCode.LOGIN_SUCCESS, authResult);
        } catch (Exception e) {
            return Result.failed(ResultCode.LOGIN_FAILED);
        }
    }
}