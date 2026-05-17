package com.lin101.store.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lin101.store.entity.User;
import com.lin101.store.mapper.UserMapper;
import com.lin101.store.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link com.lin101.store.service.UserService} 实现：会话用户由 JWT 解析，昵称非空才覆盖，地址字段允许传空串清空。
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * @param userId   必填，来自 JWT
     * @param nickname 可选，trim 后非空才更新
     * @param address  可选，{@code != null} 即写入（含空字符串表示清空）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateProfile(Integer userId, String nickname, String address) {

        if (userId == null) {
            throw new IllegalArgumentException("缺少登录态，无法定位用户");
        }

        User user = this.getById(userId);

        if (user == null) {
            throw new IllegalArgumentException("该用户不存在");
        }

        boolean isUpdated = false;

        if (nickname != null && !nickname.trim().isEmpty()) {
            user.setNickname(nickname);
            isUpdated = true;
        }
        // address 允许 ""，与「未传 address」区分：此处只要非 null 即视为客户端有意提交
        if (address != null) {
            user.setAddress(address);
            isUpdated = true;
        }

        if (isUpdated) {
            this.updateById(user);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("user", user);

        return result;
    }
}
