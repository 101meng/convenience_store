package com.lin101.store.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lin101.store.entity.Order;
import com.lin101.store.vo.OrderItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    // 【新增】根据订单ID，联表查出商品的名字和图片
    @Select("SELECT oi.product_id, oi.quantity, oi.price_at_time, p.name, p.image_url " +
            "FROM order_items oi LEFT JOIN products p ON oi.product_id = p.product_id " +
            "WHERE oi.order_id = #{orderId}")
    List<OrderItemVO> getOrderItemsWithProductInfo(Integer orderId);
}