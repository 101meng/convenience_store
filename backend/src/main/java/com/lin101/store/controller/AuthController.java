package com.lin101.store.controller;

import com.lin101.store.common.Result;
import com.lin101.store.common.ResultCode;
import com.lin101.store.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 * 处理用户登录、验证码发送等认证相关的接口请求
 * 接口基础路径：/api/auth
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/sendCode")
    public Result<Void> sendCode(@RequestParam String phone) {
        try {
            authService.sendVerificationCode(phone);
            // 纯枚举驱动：发送成功
            return Result.success(ResultCode.SEND_CODE_SUCCESS);
        } catch (Exception e) {
            // 纯枚举驱动：发送失败
            return Result.failed(ResultCode.SEND_CODE_FAILED);
        }
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> requestData) {
        try {
            String phone = requestData.get("phone");
            String code = requestData.get("code");

            Map<String, Object> authResult = authService.loginAndRegister(phone, code);

            // 纯枚举驱动：登录成功，并携带 token 和 user 数据
            return Result.success(ResultCode.LOGIN_SUCCESS, authResult);
        } catch (Exception e) {
            // 纯枚举驱动：登录失败
            return Result.failed(ResultCode.LOGIN_FAILED);
        }
    }
}