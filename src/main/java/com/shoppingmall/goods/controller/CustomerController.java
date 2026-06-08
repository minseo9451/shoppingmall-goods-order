package com.shoppingmall.goods.controller;

import com.shoppingmall.goods.Service.CustomerService;
import com.shoppingmall.goods.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public List<Customer> findAll(){
        return customerService.findAll();
    }

    @GetMapping("/{userId}")
    public Customer findById(@PathVariable String userId){
        return customerService.findById(userId);
    }

    @PostMapping
    public Customer create(@RequestBody Customer customer){
        return customerService.save(customer);
    }

    @DeleteMapping("/{userId}")
    public void delete (@PathVariable String userId){
        customerService.deleteById(userId);
    }

}
