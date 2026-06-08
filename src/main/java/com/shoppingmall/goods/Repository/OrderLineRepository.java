package com.shoppingmall.goods.Repository;

import com.shoppingmall.goods.entity.OrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderLineRepository extends JpaRepository<OrderLine,Integer> {
}
