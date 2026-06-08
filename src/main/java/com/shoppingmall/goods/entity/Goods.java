package com.shoppingmall.goods.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Goods {

    @Id
    private String goodsId;
    private String goodsName;
    private int goodsPrice;
    private int stock;
    private LocalDateTime regdate;

}
