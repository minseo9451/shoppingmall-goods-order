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
public class Orders {

    @Id
    private Integer orderId;
    private LocalDateTime orderDate;
    private String address;
    private int totalAmount;
    private String userId;
}
