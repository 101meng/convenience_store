package com.lin101.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lin101.store.entity.User;
import com.lin101.store.mapper.UserMapper;
import com.lin101.store.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    @Transactional(rollbackFor = Exception.class) // 加上事务保护
    public Map<String, Object> updateProfile(String phone, String nickname, String address) {

        // 1. 基础参数校验
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("手机号不能为空，无法定位用户");
        }

        // 2. 根据手机号去数据库查找该用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        User user = this.getOne(queryWrapper);

        if (user == null) {
            throw new IllegalArgumentException("该用户不存在");
        }

        // 3. 字段更新标记
        boolean isUpdated = false;

        if (nickname != null && !nickname.trim().isEmpty()) {
            user.setNickname(nickname);
            isUpdated = true;
        }
        if (address != null) { // 地址可以允许为空白或清空
            user.setAddress(address);
            isUpdated = true;
        }

        // 4. 执行数据库更新操作
        if (isUpdated) {
            this.updateById(user);
        }

        // 5. 组装并返回最新的用户信息
        Map<String, Object> result = new HashMap<>();
        result.put("user", user);

        return result;
    }
}