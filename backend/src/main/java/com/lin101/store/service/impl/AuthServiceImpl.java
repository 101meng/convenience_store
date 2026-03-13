package com.lin101.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lin101.store.entity.User;
import com.lin101.store.service.AuthService;
import com.lin101.store.service.UserService;
import com.lin101.store.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void sendVerificationCode(String phone) {
        // 1. 生成 6 位随机数字
        String code = String.format("%06d", new Random().nextInt(999999));

        // 2. 存入 Redis，有效期 5 分钟
        String redisKey = "VERIFY_CODE:" + phone;
        redisTemplate.opsForValue().set(redisKey, code, 5, TimeUnit.MINUTES);

        // 3. 【模拟发送短信】
        System.out.println("【模拟短信平台】发送给手机号 " + phone + " 的验证码为：[" + code + "]");
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 【核心】保证静默注册的数据库操作具有原子性
    public Map<String, Object> loginAndRegister(String phone, String inputCode) {
        // 1. 从 Redis 取出真正的验证码
        String redisKey = "VERIFY_CODE:" + phone;
        String realCode = redisTemplate.opsForValue().get(redisKey);

        // 2. 校验验证码
        if (realCode == null || !realCode.equals(inputCode)) {
            throw new RuntimeException("验证码错误或已过期"); // 业务异常抛出交由全局处理，或者按需返回特定状态
        }

        // 3. 验证码正确，删除 Redis 缓存
        redisTemplate.delete(redisKey);

        // 4. 查询数据库是否存在该用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        User user = userService.getOne(queryWrapper);

        // 5. 【静默注册】
        if (user == null) {
            user = new User();
            user.setPhone(phone);
            user.setNickname("User_" + phone.substring(7));
            user.setAvatarUrl("https://ui-avatars.com/api/?name=U&background=random");
            user.setBalance(BigDecimal.ZERO);
            // 这里执行 INSERT 操作，被 @Transactional 保护
            userService.save(user);
        }

        // 6. 签发 JWT Token
        String token = jwtUtils.generateToken(user.getUserId(), user.getPhone());

        // 7. 组装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);

        return result;
    }
}