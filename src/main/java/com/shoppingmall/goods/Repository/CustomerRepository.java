package com.shoppingmall.goods.Repository;

import com.shoppingmall.goods.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,String> {

}
