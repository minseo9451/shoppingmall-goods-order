package com.shoppingmall.goods.controller;

import com.shoppingmall.goods.Service.GoodsService;
import com.shoppingmall.goods.entity.Goods;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public void create(@RequestBody Goods goods){
         goodsService.save(goods);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{goodsId}")
    public void update(@PathVariable String goodsId, @RequestBody Goods goods){
        goods.setGoodsId(goodsId);
        goodsService.save(goods);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{goodsId}")
    public void delete(@PathVariable String goodsId){
        goodsService.deleteById(goodsId);
    }
}
