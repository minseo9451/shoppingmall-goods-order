package com.shoppingmall.goods.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    @NotBlank(message = "상품 ID를 입력해주세요.")
    private String goodsId;

    @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
    private int qty;
}
