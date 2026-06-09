package com.shoppingmall.goods.controller;

import com.shoppingmall.goods.Service.CustomerService;
import com.shoppingmall.goods.dto.ApiResponse;
import com.shoppingmall.goods.dto.CustomerResponseDto;
import com.shoppingmall.goods.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerResponseDto>>> findAll() {
        List<CustomerResponseDto> result = customerService.findAll().stream()
                .map(c -> new CustomerResponseDto(c.getUserId(), c.getUserName(), c.getRegDate(), c.getRole()))
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<CustomerResponseDto>> findById(@PathVariable String userId) {
        Customer c = customerService.findById(userId);
        return ResponseEntity.ok(ApiResponse.ok(
                new CustomerResponseDto(c.getUserId(), c.getUserName(), c.getRegDate(), c.getRole())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponseDto>> create(@RequestBody Customer customer) {
        customer.setUserPwd(passwordEncoder.encode(customer.getUserPwd()));
        customer.setRegDate(LocalDate.now());
        Customer saved = customerService.save(customer);
        return ResponseEntity.status(201).body(ApiResponse.created(
                new CustomerResponseDto(saved.getUserId(), saved.getUserName(), saved.getRegDate(), saved.getRole())));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable String userId) {
        customerService.deleteById(userId);
        return ResponseEntity.noContent().build();
    }
}
