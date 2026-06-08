package com.shoppingmall.goods.Service;

import com.shoppingmall.goods.Repository.GoodsRepository;
import com.shoppingmall.goods.entity.Goods;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoodsService {

    private final GoodsRepository goodsRepository;

    public List<Goods> findAll(){
        return goodsRepository.findAll();
    }

    public Goods findById(String goodsId){
        return goodsRepository.findById(goodsId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다: " + goodsId));
    }

    public Goods save(Goods goods){
        return goodsRepository.save(goods);
    }

    public void deleteById(String goodsId){
        goodsRepository.deleteById(goodsId);
    }
    
}
