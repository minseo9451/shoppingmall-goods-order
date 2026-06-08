package com.shoppingmall.goods.Repository;

import com.shoppingmall.goods.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders,Integer> {
    List<Orders> findByUserId(String userId);

}
