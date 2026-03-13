package com.lin101.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lin101.store.entity.User;
import java.util.Map;

public interface UserService extends IService<User> {

    /**
     * 更新用户资料（业务逻辑层）
     */
    Map<String, Object> updateProfile(String phone, String nickname, String address);

}