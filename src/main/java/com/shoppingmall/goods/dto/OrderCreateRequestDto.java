package com.shoppingmall.goods.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequestDto {
    @NotBlank(message = "배송지를 입력해주세요.")
    private String address;

    @NotEmpty(message = "주문 상품이 없습니다.")
    @Valid
    private List<OrderItemDto> items;
}
