package com.lin101.store.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("admin_accounts")
public class AdminAccount {

    @TableId(value = "admin_id", type = IdType.AUTO)
    private Integer adminId;

    private String phone;

    private String name;

    private String role;

    private Integer storeId;

    private String avatarUrl;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
