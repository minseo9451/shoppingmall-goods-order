package com.shoppingmall.goods.Service;

import com.shoppingmall.goods.Repository.CartRepository;
import com.shoppingmall.goods.entity.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;

    public List<Cart> findByUserId(String userId) {
        return cartRepository.findByUserId(userId);
    }

    @Transactional
    public Cart save(Cart cart) {
        Optional<Cart> existing = cartRepository.findByUserIdAndGoodsId(cart.getUserId(), cart.getGoodsId());
        if (existing.isPresent()) {
            Cart found = existing.get();
            found.setQty(found.getQty() + cart.getQty());
            return cartRepository.save(found);
        }
        return cartRepository.save(cart);
    }

    @Transactional
    public void deleteById(Integer cartId) {
        cartRepository.deleteById(cartId);
    }
}
