package com.shoppingmall.goods.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class CustomerResponseDto {
    private String userId;
    private String userName;
    private LocalDate regDate;
    private String role;
}
