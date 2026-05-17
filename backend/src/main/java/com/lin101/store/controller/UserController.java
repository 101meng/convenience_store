package com.lin101.store.controller;

import com.lin101.store.common.Result;
import com.lin101.store.common.ResultCode;
import com.lin101.store.interceptor.JwtInterceptor;
import com.lin101.store.service.UserService;
import com.lin101.store.vo.UserProfileUpdateReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户资料更新（需 JWT）。用户身份从拦截器注入，可更新昵称与收货地址。
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * @param requestData JSON：{@code nickname}、{@code address} 可选
     */
    @PostMapping("/update")
    public Result<Map<String, Object>> updateProfile(
            @RequestAttribute(JwtInterceptor.ATTR_USER_ID) Integer userId,
            @RequestBody UserProfileUpdateReq requestData) {
        try {
            Map<String, Object> result = userService.updateProfile(
                    userId,
                    requestData.getNickname(),
                    requestData.getAddress()
            );

            return Result.success(ResultCode.UPDATE_PROFILE_SUCCESS, result);

        } catch (IllegalArgumentException e) {
            return Result.failed(ResultCode.VALIDATE_FAILED);

        } catch (Exception e) {
            return Result.failed(ResultCode.UPDATE_PROFILE_FAILED);
        }
    }
}
