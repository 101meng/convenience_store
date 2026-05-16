package com.lin101.store.controller;

import com.lin101.store.common.Result;
import com.lin101.store.common.ResultCode;
import com.lin101.store.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户资料更新（需 JWT）。使用手机号定位用户，可更新昵称与收货地址。
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * @param requestData JSON：{@code phone} 必填；{@code nickname}、{@code address} 可选
     */
    @PostMapping("/update")
    public Result<Map<String, Object>> updateProfile(@RequestBody Map<String, String> requestData) {
        try {
            String phone = requestData.get("phone");
            String nickname = requestData.get("nickname");
            String address = requestData.get("address");

            Map<String, Object> result = userService.updateProfile(phone, nickname, address);

            return Result.success(ResultCode.UPDATE_PROFILE_SUCCESS, result);

        } catch (IllegalArgumentException e) {
            return Result.failed(ResultCode.VALIDATE_FAILED);

        } catch (Exception e) {
            return Result.failed(ResultCode.UPDATE_PROFILE_FAILED);
        }
    }
}