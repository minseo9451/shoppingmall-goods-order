package com.shoppingmall.goods.Repository;

import com.shoppingmall.goods.entity.Goods;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GoodsRepository extends JpaRepository<Goods, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT g FROM Goods g WHERE g.goodsId = :goodsId")
    Optional<Goods> findByIdForUpdate(@Param("goodsId") String goodsId);
}
