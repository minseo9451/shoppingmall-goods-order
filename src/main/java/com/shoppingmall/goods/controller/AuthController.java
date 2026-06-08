package com.shoppingmall.goods.controller;

import com.shoppingmall.goods.Service.CustomerService;
import com.shoppingmall.goods.config.JwtUtil;
import com.shoppingmall.goods.dto.LoginRequestDto;
import com.shoppingmall.goods.dto.RegisterRequestDto;
import com.shoppingmall.goods.entity.Customer;
import lombok.RequiredArgsConstructor;
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
    public String register(@RequestBody RegisterRequestDto dto) {
        Customer customer = new Customer(
                dto.getUserId(),
                passwordEncoder.encode(dto.getUserPwd()),
                dto.getUserName(),
                LocalDate.now(),
                "USER"
        );
        customerService.save(customer);
        return "회원가입 완료";
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequestDto dto) {
        Customer customer = customerService.findById(dto.getUserId());
        if (!passwordEncoder.matches(dto.getUserPwd(), customer.getUserPwd())) {
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }
        return jwtUtil.generateToken(customer.getUserId());
    }
}