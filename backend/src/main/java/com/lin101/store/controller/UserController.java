package com.lin101.store.controller;

import com.lin101.store.common.Result;
import com.lin101.store.common.ResultCode;
import com.lin101.store.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/update")
    public Result<Map<String, Object>> updateProfile(@RequestBody Map<String, String> requestData) {
        try {
            String phone = requestData.get("phone");
            String nickname = requestData.get("nickname");
            String address = requestData.get("address");

            Map<String, Object> result = userService.updateProfile(phone, nickname, address);

            // 【重构极致版】：完全消除硬编码，传入枚举类和数据即可
            return Result.success(ResultCode.UPDATE_PROFILE_SUCCESS, result);

        } catch (IllegalArgumentException e) {
            // 参数错误，直接抛出统一定义的枚举
            return Result.failed(ResultCode.VALIDATE_FAILED);

        } catch (Exception e) {
            // 业务失败，抛出统一定义的枚举
            return Result.failed(ResultCode.UPDATE_PROFILE_FAILED);
        }
    }
}