package com.lin101.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lin101.store.entity.User;
import java.util.Map;

/** 用户持久化扩展：资料更新等（实现见 {@link com.lin101.store.service.impl.UserServiceImpl}）。 */
public interface UserService extends IService<User> {

    Map<String, Object> updateProfile(Integer userId, String nickname, String address);

}
