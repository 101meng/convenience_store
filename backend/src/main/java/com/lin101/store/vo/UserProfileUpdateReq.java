package com.lin101.store.vo;

import lombok.Data;

/** 个人资料更新请求体：身份来自 JWT。 */
@Data
public class UserProfileUpdateReq {
    private String nickname;
    private String address;
}
