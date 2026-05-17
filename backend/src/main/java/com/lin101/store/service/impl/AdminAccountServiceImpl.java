package com.lin101.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lin101.store.entity.AdminAccount;
import com.lin101.store.mapper.AdminAccountMapper;
import com.lin101.store.service.AdminAccountService;
import org.springframework.stereotype.Service;

@Service
public class AdminAccountServiceImpl extends ServiceImpl<AdminAccountMapper, AdminAccount> implements AdminAccountService {

    @Override
    public AdminAccount getByPhone(String phone) {
        return getOne(new QueryWrapper<AdminAccount>().eq("phone", phone));
    }
}
