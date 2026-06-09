package com.shoppingmall.goods.Service;

import com.shoppingmall.goods.Repository.CartRepository;
import com.shoppingmall.goods.entity.Cart;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock CartRepository cartRepository;

    @InjectMocks CartService cartService;

    @Test
    @DisplayName("장바구니에 새 상품 담기 - 신규 row 생성")
    void save_newItem() {
        Cart cart = new Cart(null, "user1", "G001", 2);
        given(cartRepository.findByUserIdAndGoodsId("user1", "G001")).willReturn(Optional.empty());
        given(cartRepository.save(cart)).willReturn(new Cart(1, "user1", "G001", 2));

        Cart result = cartService.save(cart);

        assertThat(result.getQty()).isEqualTo(2);
        verify(cartRepository).save(cart);
    }

    @Test
    @DisplayName("이미 담긴 상품 재담기 - 수량 합산")
    void save_existingItem_mergesQty() {
        Cart existing = new Cart(1, "user1", "G001", 3);
        Cart incoming = new Cart(null, "user1", "G001", 2);

        given(cartRepository.findByUserIdAndGoodsId("user1", "G001")).willReturn(Optional.of(existing));
        given(cartRepository.save(existing)).willReturn(existing);

        Cart result = cartService.save(incoming);

        assertThat(result.getQty()).isEqualTo(5);
        verify(cartRepository).save(existing);
    }
}
