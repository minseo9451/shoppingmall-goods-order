package com.shoppingmall.goods.controller;

import com.shoppingmall.goods.Service.GoodsService;
import com.shoppingmall.goods.entity.Goods;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goods")
@RequiredArgsConstructor
public class GoodsController {

    private final GoodsService goodsService;

    @GetMapping
    public List<Goods> findAll(){
        return goodsService.findAll();
    }

    @GetMapping("/{goodsId}")
    public Goods findById(@PathVariable String goodsId){
        return goodsService.findById(goodsId);
    }

    @PostMapping
    public void create(@RequestBody Goods goods){
         goodsService.save(goods);
    }

    @PutMapping("/{goodsId}")
    public void update(@PathVariable String goodsId, @RequestBody Goods goods){
        goods.setGoodsId(goodsId);
        goodsService.save(goods);
    }

    @DeleteMapping("/{goodsId}")
    public void delete(@PathVariable String goodsId){
        goodsService.deleteById(goodsId);
    }
}
