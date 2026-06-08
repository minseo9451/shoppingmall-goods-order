package com.shoppingmall.goods.Service;

import com.shoppingmall.goods.Repository.CartRepository;
import com.shoppingmall.goods.entity.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    public List<Cart> findByUserId(String userId) {
        return cartRepository.findByUserId(userId);
    }

    public Cart save(Cart cart) {
        return cartRepository.save(cart);
    }

    public void deleteById(Integer cartId) {
        cartRepository.deleteById(cartId);
    }
}
