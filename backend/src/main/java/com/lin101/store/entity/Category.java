package com.lin101.store.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("categories") // 对应数据库表名
public class Category {
    @TableId(type = IdType.AUTO) // 指定主键且为自增
    private Integer categoryId;
    private String categoryName;
    private String iconUrl;
}