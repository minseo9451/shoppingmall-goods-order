package com.shoppingmall.goods.Service;

import com.shoppingmall.goods.Repository.GoodsRepository;
import com.shoppingmall.goods.Repository.OrderLineRepository;
import com.shoppingmall.goods.Repository.OrdersRepository;
import com.shoppingmall.goods.dto.OrderCreateRequestDto;
import com.shoppingmall.goods.dto.OrderItemDto;
import com.shoppingmall.goods.entity.Goods;
import com.shoppingmall.goods.entity.OrderLine;
import com.shoppingmall.goods.entity.Orders;
import com.shoppingmall.goods.exception.InsufficientStockException;
import com.shoppingmall.goods.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrdersRepository ordersRepository;
    private final OrderLineRepository orderLineRepository;
    private final GoodsRepository goodsRepository;

    public List<Orders> findAll() {
        return ordersRepository.findAll();
    }

    public Orders findById(Integer orderId) {
        return ordersRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("주문을 찾을 수 없습니다: " + orderId));
    }

    public List<Orders> findByUserId(String userId) {
        return ordersRepository.findByUserId(userId);
    }

    @Transactional
    public Orders createOrder(String userId, OrderCreateRequestDto dto) {
        // 1. 재고 확인 및 총 금액 계산
        List<Goods> goodsList = dto.getItems().stream()
                .map(item -> {
                    Goods goods = goodsRepository.findById(item.getGoodsId())
                            .orElseThrow(() -> new NotFoundException("상품을 찾을 수 없습니다: " + item.getGoodsId()));
                    if (goods.getStock() < item.getQty()) {
                        throw new InsufficientStockException("재고가 부족합니다: " + goods.getGoodsName());
                    }
                    return goods;
                })
                .toList();

        int totalAmount = 0;
        for (int i = 0; i < dto.getItems().size(); i++) {
            totalAmount += goodsList.get(i).getGoodsPrice() * dto.getItems().get(i).getQty();
        }

        // 2. 주문 저장
        Orders order = new Orders(null, LocalDateTime.now(), dto.getAddress(), totalAmount, userId);
        ordersRepository.save(order);

        // 3. 주문 상세 저장 + 재고 차감
        for (int i = 0; i < dto.getItems().size(); i++) {
            OrderItemDto item = dto.getItems().get(i);
            Goods goods = goodsList.get(i);

            int amount = goods.getGoodsPrice() * item.getQty();
            orderLineRepository.save(new OrderLine(null, order.getOrderId(), goods.getGoodsId(),
                    goods.getGoodsPrice(), item.getQty(), amount));

            goods.setStock(goods.getStock() - item.getQty());
            goodsRepository.save(goods);
        }

        return order;
    }
}
