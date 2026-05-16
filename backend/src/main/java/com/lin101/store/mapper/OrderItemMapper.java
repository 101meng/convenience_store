package com.lin101.store.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lin101.store.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}