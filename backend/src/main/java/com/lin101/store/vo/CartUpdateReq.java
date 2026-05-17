package com.lin101.store.vo;

import lombok.Data;

/** 购物车数量调整请求体。 */
@Data
public class CartUpdateReq {
    private Integer cartId;
    private Integer quantity;
}
