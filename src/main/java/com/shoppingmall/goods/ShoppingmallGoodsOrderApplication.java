package com.shoppingmall.goods;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class ShoppingmallGoodsOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingmallGoodsOrderApplication.class, args);
    }

}
