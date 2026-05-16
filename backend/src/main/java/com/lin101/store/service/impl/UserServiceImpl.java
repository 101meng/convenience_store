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

/**
 * {@link com.lin101.store.service.UserService} 实现：除 BaseMapper CRUD 外，提供按手机号的资料更新。
 * <p>客户端需传手机号以关联会话用户；昵称非空才覆盖，地址字段允许传空串清空。</p>
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * @param phone    必填，与 {@code users.phone} 唯一匹配
     * @param nickname 可选，trim 后非空才更新
     * @param address  可选，{@code != null} 即写入（含空字符串表示清空）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateProfile(String phone, String nickname, String address) {

        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("手机号不能为空，无法定位用户");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        User user = this.getOne(queryWrapper);

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
        // isUpdated==false：例如只传了空昵称且 address 为 null，不落库但仍返回内存中的 user

        Map<String, Object> result = new HashMap<>();
        result.put("user", user);

        return result;
    }
}