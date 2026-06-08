package com.shoppingmall.goods.controller;

import com.shoppingmall.goods.Service.CustomerService;
import com.shoppingmall.goods.dto.CustomerResponseDto;
import com.shoppingmall.goods.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public List<CustomerResponseDto> findAll(){
        return customerService.findAll().stream()
                .map(c -> new CustomerResponseDto(c.getUserId(), c.getUserName(), c.getRegDate(), c.getRole()))
                .toList();
    }

    @GetMapping("/{userId}")
    public CustomerResponseDto findById(@PathVariable String userId){
        Customer c = customerService.findById(userId);
        return new CustomerResponseDto(c.getUserId(), c.getUserName(), c.getRegDate(), c.getRole());
    }

    @PostMapping
    public CustomerResponseDto create(@RequestBody Customer customer){
        customer.setUserPwd(passwordEncoder.encode(customer.getUserPwd()));
        customer.setRegDate(LocalDate.now());
        Customer saved = customerService.save(customer);
        return new CustomerResponseDto(saved.getUserId(), saved.getUserName(), saved.getRegDate(), saved.getRole());
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable String userId){
        customerService.deleteById(userId);
    }

}
