package com.shoppingmall.goods.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {
    private String userId;
    private String userPwd;
    private String userName;
}
