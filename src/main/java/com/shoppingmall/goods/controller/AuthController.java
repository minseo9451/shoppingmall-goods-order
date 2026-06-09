package com.shoppingmall.goods.controller;

import com.shoppingmall.goods.Service.CustomerService;
import com.shoppingmall.goods.config.JwtUtil;
import com.shoppingmall.goods.dto.ApiResponse;
import com.shoppingmall.goods.dto.LoginRequestDto;
import com.shoppingmall.goods.dto.RegisterRequestDto;
import com.shoppingmall.goods.entity.Customer;
import com.shoppingmall.goods.exception.AuthException;
import com.shoppingmall.goods.exception.DuplicateException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final CustomerService customerService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequestDto dto) {
        if (customerService.existsById(dto.getUserId())) {
            throw new DuplicateException("이미 사용 중인 아이디입니다: " + dto.getUserId());
        }
        Customer customer = new Customer(
                dto.getUserId(),
                passwordEncoder.encode(dto.getUserPwd()),
                dto.getUserName(),
                LocalDate.now(),
                "USER"
        );
        customerService.save(customer);
        return ResponseEntity.status(201).body(ApiResponse.created(null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginRequestDto dto) {
        Customer customer = customerService.findById(dto.getUserId());
        if (!passwordEncoder.matches(dto.getUserPwd(), customer.getUserPwd())) {
            throw new AuthException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        String token = jwtUtil.generateToken(customer.getUserId(), customer.getRole());
        return ResponseEntity.ok(ApiResponse.ok(token));
    }
}
