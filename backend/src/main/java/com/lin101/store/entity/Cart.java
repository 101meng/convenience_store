package com.lin101.store.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("cart")
public class Cart {

    @TableId(type = IdType.AUTO)
    private Integer cartId;

    private Integer userId;

    private Integer storeId;

    private Integer productId;

    private Integer quantity;
}
