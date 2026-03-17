package com.lin101.store.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("banners")
public class Banner {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String imageUrl;
    private String linkUrl;
    private Integer sortOrder;
    private Integer isActive; // 1为展示，0为隐藏
    private LocalDateTime createdAt;
}