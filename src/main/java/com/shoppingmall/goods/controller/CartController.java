package com.shoppingmall.goods.controller;

import com.shoppingmall.goods.Service.CartService;
import com.shoppingmall.goods.dto.ApiResponse;
import com.shoppingmall.goods.entity.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<Cart>>> findByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.ok(cartService.findByUserId(userId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Cart>> create(@RequestBody Cart cart) {
        return ResponseEntity.status(201).body(ApiResponse.created(cartService.save(cart)));
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer cartId) {
        cartService.deleteById(cartId);
        return ResponseEntity.noContent().build();
    }
}
