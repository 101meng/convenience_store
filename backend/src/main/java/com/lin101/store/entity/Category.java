package com.lin101.store.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/** 表 categories：商品分类。 */
@Data
@TableName("categories")
public class Category {
    @TableId(type = IdType.AUTO)
    private Integer categoryId;
    private String categoryName;
    private String iconUrl;
}