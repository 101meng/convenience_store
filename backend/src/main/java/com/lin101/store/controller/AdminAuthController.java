package com.lin101.store.controller;

import com.lin101.store.common.Result;
import com.lin101.store.common.ResultCode;
import com.lin101.store.service.AdminAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin-auth")
public class AdminAuthController {

    @Autowired
    private AdminAuthService adminAuthService;

    @GetMapping("/sendCode")
    public Result<Void> sendCode(@RequestParam String phone) {
        try {
            adminAuthService.sendVerificationCode(phone);
            return Result.success(ResultCode.ADMIN_SEND_CODE_SUCCESS);
        } catch (Exception e) {
            log.error("Failed to send admin verification code for phone {}", phone, e);
            return Result.failed(ResultCode.ADMIN_SEND_CODE_FAILED);
        }
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> requestData) {
        try {
            return Result.success(
                    ResultCode.ADMIN_LOGIN_SUCCESS,
                    adminAuthService.login(requestData.get("phone"), requestData.get("code"))
            );
        } catch (Exception e) {
            log.error("Failed to login admin for phone {}", requestData.get("phone"), e);
            return Result.failed(ResultCode.ADMIN_LOGIN_FAILED);
        }
    }
}
