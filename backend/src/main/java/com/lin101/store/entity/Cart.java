package com.lin101.store.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 购物车实体类
 * 专门用于和数据库中的 `cart` 表进行一一映射
 */
@Data // Lombok 注解，自动生成 Getter/Setter、toString 等方法，保持代码干净
@TableName("cart") // 告诉 MyBatisPlus 这个类对应数据库的哪张表
public class Cart {

    // 标记这是一个自增主键
    @TableId(type = IdType.AUTO)
    private Integer cartId;

    // 关联的用户 ID，代表这条购物车记录属于谁
    private Integer userId;

    // 关联的商品 ID，代表加了什么商品
    private Integer productId;

    // 添加的商品数量
    private Integer quantity;
}