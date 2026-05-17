package com.lin101.store.service.impl;

import com.lin101.store.entity.AdminAccount;
import com.lin101.store.service.AdminAccountService;
import com.lin101.store.service.AdminAuthService;
import com.lin101.store.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AdminAuthServiceImpl implements AdminAuthService {

    private static final String CODE_KEY_PREFIX = "ADMIN_VERIFY_CODE:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AdminAccountService adminAccountService;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void sendVerificationCode(String phone) {
        AdminAccount adminAccount = validateActiveAdmin(phone);
        String code = String.format("%06d", new Random().nextInt(999999));
        redisTemplate.opsForValue().set(CODE_KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);
        System.out.println("【管理端模拟短信】发送给手机号 " + adminAccount.getPhone() + " 的验证码为：[" + code + "]");
    }

    @Override
    public Map<String, Object> login(String phone, String inputCode) {
        AdminAccount adminAccount = validateActiveAdmin(phone);
        String realCode = redisTemplate.opsForValue().get(CODE_KEY_PREFIX + phone);
        if (realCode == null || !realCode.equals(inputCode)) {
            throw new RuntimeException("验证码错误或已过期");
        }

        redisTemplate.delete(CODE_KEY_PREFIX + phone);

        Map<String, Object> result = new HashMap<>();
        result.put("token", jwtUtils.generateAdminToken(adminAccount));
        result.put("admin", adminAccount);
        return result;
    }

    private AdminAccount validateActiveAdmin(String phone) {
        AdminAccount adminAccount = adminAccountService.getByPhone(phone);
        if (adminAccount == null || adminAccount.getStatus() == null || adminAccount.getStatus() != 1) {
            throw new RuntimeException("管理端账号不存在或已禁用");
        }
        return adminAccount;
    }
}
