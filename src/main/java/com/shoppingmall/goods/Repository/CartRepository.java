package com.shoppingmall.goods.Repository;

import com.shoppingmall.goods.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart,Integer> {
    List<Cart> findByUserId(String userId);
}
