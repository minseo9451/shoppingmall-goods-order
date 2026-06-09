package com.shoppingmall.goods.controller;

import com.shoppingmall.goods.Service.OrderService;
import com.shoppingmall.goods.dto.ApiResponse;
import com.shoppingmall.goods.dto.OrderCreateRequestDto;
import com.shoppingmall.goods.entity.Orders;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Orders>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok(orderService.findAll()));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Orders>> findById(@PathVariable Integer orderId) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.findById(orderId)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Orders>>> findByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.findByUserId(userId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Orders>> create(@Valid @RequestBody OrderCreateRequestDto dto) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.status(201).body(ApiResponse.created(orderService.createOrder(userId, dto)));
    }
}
