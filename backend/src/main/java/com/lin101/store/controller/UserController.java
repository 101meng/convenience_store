package com.lin101.store.controller;

import com.lin101.store.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 更新用户资料接口
     * POST /api/user/update
     */
    @PostMapping("/update")
    public Map<String, Object> updateProfile(@RequestBody Map<String, String> requestData) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 获取前端传参
            String phone = requestData.get("phone");
            String nickname = requestData.get("nickname");
            String address = requestData.get("address");

            // 核心业务全部下沉交给 Service 层处理
            Map<String, Object> result = userService.updateProfile(phone, nickname, address);

            response.put("code", 200);
            response.put("message", "个人资料更新成功");
            response.putAll(result); // 将更新后的 user 信息放入返回体

        } catch (IllegalArgumentException e) {
            // 捕获 Service 层抛出的业务校验异常
            response.put("code", 400);
            response.put("message", e.getMessage());
        } catch (Exception e) {
            // 捕获不可预知的服务器异常
            response.put("code", 500);
            response.put("message", "服务器内部错误");
            e.printStackTrace();
        }
        return response;
    }
}