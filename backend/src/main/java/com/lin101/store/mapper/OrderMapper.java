package com.lin101.store.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lin101.store.entity.Order;
import com.lin101.store.vo.OrderItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/** 订单主表 Mapper；附订单明细与商品信息的联表查询。 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /** 某订单下各行的展示名、图、数量、下单时单价。 */
    @Select("SELECT oi.product_id, oi.quantity, oi.price_at_time, p.name, p.image_url " +
            "FROM order_items oi LEFT JOIN products p ON oi.product_id = p.product_id " +
            "WHERE oi.order_id = #{orderId}")
    List<OrderItemVO> getOrderItemsWithProductInfo(Integer orderId);
}