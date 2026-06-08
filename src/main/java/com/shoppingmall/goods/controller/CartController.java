package com.shoppingmall.goods.controller;


import com.shoppingmall.goods.Service.CartService;
import com.shoppingmall.goods.entity.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    public List<Cart> findByUserID(@PathVariable String userId){
        return cartService.findByUserId(userId);
    }

    @PostMapping
    public Cart create(@RequestBody Cart cart){
        return cartService.save(cart);
    }

    @DeleteMapping("/{cartId}")
    public void deleteById(@PathVariable Integer cartId){
        cartService.deleteById(cartId);
    }
}
