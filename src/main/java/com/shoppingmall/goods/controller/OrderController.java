package com.shoppingmall.goods.controller;

import com.shoppingmall.goods.Service.OrderService;
import com.shoppingmall.goods.entity.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<Orders> findAll(){
        return orderService.findAll();
    }

    @GetMapping("/{orderId}")
    public Orders findById(@PathVariable Integer orderId){
        return orderService.findById(orderId);
    }

    @GetMapping("/user/{userId}")
    public List<Orders> findByUserId(String userId){
        return orderService.findByUserId(userId);
    }

    @PostMapping
    public Orders create (@RequestBody Orders orders){
        return orderService.save(orders);
    }
}
