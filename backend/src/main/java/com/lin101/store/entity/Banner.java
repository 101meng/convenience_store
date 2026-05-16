package com.lin101.store.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** 表 banners：首页轮播。 */
@Data
@TableName("banners")
public class Banner {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String imageUrl;
    private String linkUrl;
    private Integer sortOrder;
    /** 1 展示，0 下线 */
    private Integer isActive;
    private LocalDateTime createdAt;
}