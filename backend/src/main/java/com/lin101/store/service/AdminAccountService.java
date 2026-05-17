package com.lin101.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lin101.store.entity.AdminAccount;

public interface AdminAccountService extends IService<AdminAccount> {
    AdminAccount getByPhone(String phone);
}
