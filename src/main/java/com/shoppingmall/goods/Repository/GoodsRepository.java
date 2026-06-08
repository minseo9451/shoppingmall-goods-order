package com.shoppingmall.goods.Repository;

import com.shoppingmall.goods.entity.Goods;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodsRepository extends JpaRepository<Goods,String> {
}
