package com.shoppingmall.goods.Service;

import com.shoppingmall.goods.Repository.OrdersRepository;
import com.shoppingmall.goods.entity.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrdersRepository ordersRepository;

    public List<Orders> findAll(){
        return ordersRepository.findAll();

    }

    public Orders findById(Integer orderId){
        return ordersRepository.findById(orderId).orElseThrow();
    }

    public List<Orders> findByUserId(String userId){
        return ordersRepository.findByUserId(userId);
    }

    public Orders save(Orders orders){
        return ordersRepository.save(orders);
    }
    
}
