package com.lin101.store.controller;

import com.lin101.store.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/sendCode")
    public Map<String, Object> sendCode(@RequestParam String phone) {
        Map<String, Object> response = new HashMap<>();
        try {
            authService.sendVerificationCode(phone);
            response.put("code", 200);
            response.put("message", "验证码发送成功");
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "发送失败: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> requestData) {
        Map<String, Object> response = new HashMap<>();
        try {
            String phone = requestData.get("phone");
            String code = requestData.get("code");

            // 复杂的业务全部交给 Service 处理
            Map<String, Object> authResult = authService.loginAndRegister(phone, code);

            response.put("code", 200);
            response.put("message", "登录成功");
            response.putAll(authResult); // 将 token 和 user 信息合并到返回结果中
        } catch (Exception e) {
            response.put("code", 400);
            response.put("message", e.getMessage());
        }
        return response;
    }
}