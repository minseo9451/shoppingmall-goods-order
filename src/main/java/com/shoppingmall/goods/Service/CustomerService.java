package com.shoppingmall.goods.Service;

import com.shoppingmall.goods.Repository.CustomerRepository;
import com.shoppingmall.goods.entity.Customer;
import com.shoppingmall.goods.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public Customer findById(String userId) {
        return customerRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다: " + userId));
    }

    public boolean existsById(String userId) {
        return customerRepository.existsById(userId);
    }

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    public void deleteById(String userId) {
        customerRepository.deleteById(userId);
    }
}
