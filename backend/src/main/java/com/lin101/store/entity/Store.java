package com.lin101.store.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 自提门店实体类
 * 对应数据库中的 stores 表
 */
@Data
@TableName("stores")
public class Store {

    @TableId(value = "store_id", type = IdType.AUTO)
    private Integer storeId;

    private String storeName;

    private String address;

    // 坐标建议使用 BigDecimal 以保证经纬度精度
    private BigDecimal latitude;

    private BigDecimal longitude;
}