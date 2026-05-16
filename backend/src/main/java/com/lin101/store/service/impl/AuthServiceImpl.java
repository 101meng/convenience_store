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

/**
 * {@link com.lin101.store.service.AuthService} 实现：依赖 {@link org.springframework.data.redis.core.StringRedisTemplate} 存验证码，
 * {@link com.lin101.store.utils.JwtUtils} 签发令牌；用户持久化走 {@link UserService}。
 * <p>Redis 连接参数见 {@code application.properties}（{@code spring.data.redis.*}）。</p>
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 生成 6 位数字写入 Redis，TTL 5 分钟；生产环境应替换 {@code System.out} 为真实短信通道。
     */
    @Override
    public void sendVerificationCode(String phone) {
        // 0～999999 格式化为 6 位，前导零由 String.format 补齐
        String code = String.format("%06d", new Random().nextInt(999999));

        String redisKey = "VERIFY_CODE:" + phone;
        redisTemplate.opsForValue().set(redisKey, code, 5, TimeUnit.MINUTES);

        System.out.println("【模拟短信平台】发送给手机号 " + phone + " 的验证码为：[" + code + "]");
    }

    /**
     * 校验验证码并登录；手机号不存在时插入默认昵称与头像。返回 {@code token}、{@code user}。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> loginAndRegister(String phone, String inputCode) {
        String redisKey = "VERIFY_CODE:" + phone;
        String realCode = redisTemplate.opsForValue().get(redisKey);

        if (realCode == null || !realCode.equals(inputCode)) {
            throw new RuntimeException("验证码错误或已过期");
        }

        // 验证成功即删键，防止同一验证码被多次使用
        redisTemplate.delete(redisKey);

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        User user = userService.getOne(queryWrapper);

        if (user == null) {
            user = new User();
            user.setPhone(phone);
            // 假定大陆 11 位手机号：取后四位避免昵称过长；位数不足会抛 StringIndexOutOfBounds
            user.setNickname("User_" + phone.substring(7));
            user.setAvatarUrl("https://ui-avatars.com/api/?name=U&background=random");
            user.setBalance(BigDecimal.ZERO);
            userService.save(user);
        }

        String token = jwtUtils.generateToken(user.getUserId(), user.getPhone());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);

        return result;
    }
}