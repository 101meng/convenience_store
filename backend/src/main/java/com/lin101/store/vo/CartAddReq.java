package com.lin101.store.vo;

import lombok.Data;

/** 加购请求体：用户身份从 JWT 提取，客户端只需提交商品与数量。 */
@Data
public class CartAddReq {
    private Integer productId;
    private Integer quantity;
}
