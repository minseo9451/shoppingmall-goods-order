package com.shoppingmall.goods.Service;

import com.shoppingmall.goods.Repository.GoodsRepository;
import com.shoppingmall.goods.Repository.OrderLineRepository;
import com.shoppingmall.goods.Repository.OrdersRepository;
import com.shoppingmall.goods.dto.OrderCreateRequestDto;
import com.shoppingmall.goods.dto.OrderItemDto;
import com.shoppingmall.goods.entity.Goods;
import com.shoppingmall.goods.entity.Orders;
import com.shoppingmall.goods.exception.InsufficientStockException;
import com.shoppingmall.goods.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock OrdersRepository ordersRepository;
    @Mock OrderLineRepository orderLineRepository;
    @Mock GoodsRepository goodsRepository;

    @InjectMocks OrderService orderService;

    @Test
    @DisplayName("주문 생성 성공 - 재고 차감 및 totalAmount 계산 검증")
    void createOrder_success() {
        Goods goods = new Goods("G001", "노트북", 1_000_000, 10, LocalDateTime.now());
        Orders savedOrder = new Orders(1, LocalDateTime.now(), "서울시 강남구", 2_000_000, "user1");

        given(goodsRepository.findByIdForUpdate("G001")).willReturn(Optional.of(goods));
        given(ordersRepository.save(any())).willReturn(savedOrder);
        given(orderLineRepository.save(any())).willReturn(null);
        given(goodsRepository.save(any())).willReturn(goods);

        OrderCreateRequestDto dto = new OrderCreateRequestDto("서울시 강남구",
                List.of(new OrderItemDto("G001", 2)));

        Orders result = orderService.createOrder("user1", dto);

        assertThat(result.getTotalAmount()).isEqualTo(2_000_000);
        assertThat(goods.getStock()).isEqualTo(8);
        verify(orderLineRepository).save(any());
    }

    @Test
    @DisplayName("재고 부족 시 InsufficientStockException 발생")
    void createOrder_insufficientStock() {
        Goods goods = new Goods("G001", "노트북", 1_000_000, 1, LocalDateTime.now());

        given(goodsRepository.findByIdForUpdate("G001")).willReturn(Optional.of(goods));

        OrderCreateRequestDto dto = new OrderCreateRequestDto("서울시 강남구",
                List.of(new OrderItemDto("G001", 5)));

        assertThatThrownBy(() -> orderService.createOrder("user1", dto))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("재고가 부족합니다");
    }

    @Test
    @DisplayName("존재하지 않는 상품 주문 시 NotFoundException 발생")
    void createOrder_goodsNotFound() {
        given(goodsRepository.findByIdForUpdate("INVALID")).willReturn(Optional.empty());

        OrderCreateRequestDto dto = new OrderCreateRequestDto("서울시 강남구",
                List.of(new OrderItemDto("INVALID", 1)));

        assertThatThrownBy(() -> orderService.createOrder("user1", dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("상품을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("존재하지 않는 주문 조회 시 NotFoundException 발생")
    void findById_notFound() {
        given(ordersRepository.findById(999)).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.findById(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("주문을 찾을 수 없습니다");
    }
}
