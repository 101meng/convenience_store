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

    /**
     * Redis 存储管理员验证码的 key 前缀
     * 格式：ADMIN_VERIFY_CODE:手机号
     */
    private static final String CODE_KEY_PREFIX = "ADMIN_VERIFY_CODE:";
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private AdminAccountService adminAccountService;
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void sendVerificationCode(String phone) {
        // 校验管理员账号状态：必须存在且为启用状态
        AdminAccount adminAccount = validateActiveAdmin(phone);
        // 生成6位随机数字验证码（不足6位前面补0）
        String code = String.format("%06d", new Random().nextInt(999999));
        // 将验证码存入Redis，设置5分钟过期时间
        redisTemplate.opsForValue().set(CODE_KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);
        // 模拟短信发送，控制台打印验证码（生产环境替换为真实短信接口）
        System.out.println("【管理端模拟短信】发送给手机号 " + adminAccount.getPhone() + " 的验证码为：[" + code + "]");
    }

    @Override
    public Map<String, Object> login(String phone, String inputCode) {
        // 校验管理员账号状态
        AdminAccount adminAccount = validateActiveAdmin(phone);
        // 从Redis中获取真实的验证码
        String realCode = redisTemplate.opsForValue().get(CODE_KEY_PREFIX + phone);

        // 校验验证码：为空/不匹配则抛出异常
        if (realCode == null || !realCode.equals(inputCode)) {
            throw new RuntimeException("验证码错误或已过期");
        }

        // 登录成功，删除Redis中的验证码（防止重复使用）
        redisTemplate.delete(CODE_KEY_PREFIX + phone);

        // 封装返回结果：JWT令牌 + 管理员信息
        Map<String, Object> result = new HashMap<>();
        result.put("token", jwtUtils.generateAdminToken(adminAccount));
        result.put("admin", adminAccount);
        return result;
    }

    private AdminAccount validateActiveAdmin(String phone) {
        // 根据手机号查询管理员账号
        AdminAccount adminAccount = adminAccountService.getByPhone(phone);
        // 校验账号状态：不存在/状态为空/状态不等于1（禁用）均抛出异常
        if (adminAccount == null || adminAccount.getStatus() == null || adminAccount.getStatus() != 1) {
            throw new RuntimeException("管理端账号不存在或已禁用");
        }
        return adminAccount;
    }
}