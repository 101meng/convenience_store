package com.lin101.store.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

/** 表 stores：自提门店及坐标。 */
@Data
@TableName("stores")
public class Store {

    @TableId(value = "store_id", type = IdType.AUTO)
    private Integer storeId;

    private String storeName;

    private String address;

    private String phone;

    private String hours;

    private BigDecimal latitude;

    private BigDecimal longitude;
}
