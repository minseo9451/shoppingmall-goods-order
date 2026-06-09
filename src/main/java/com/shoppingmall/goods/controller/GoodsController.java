package com.shoppingmall.goods.controller;

import com.shoppingmall.goods.Service.GoodsService;
import com.shoppingmall.goods.dto.ApiResponse;
import com.shoppingmall.goods.entity.Goods;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goods")
@RequiredArgsConstructor
public class GoodsController {

    private final GoodsService goodsService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Goods>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok(goodsService.findAll()));
    }

    @GetMapping("/{goodsId}")
    public ResponseEntity<ApiResponse<Goods>> findById(@PathVariable String goodsId) {
        return ResponseEntity.ok(ApiResponse.ok(goodsService.findById(goodsId)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<Goods>> create(@RequestBody Goods goods) {
        return ResponseEntity.status(201).body(ApiResponse.created(goodsService.save(goods)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{goodsId}")
    public ResponseEntity<ApiResponse<Goods>> update(@PathVariable String goodsId, @RequestBody Goods goods) {
        goods.setGoodsId(goodsId);
        return ResponseEntity.ok(ApiResponse.ok(goodsService.save(goods)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{goodsId}")
    public ResponseEntity<Void> delete(@PathVariable String goodsId) {
        goodsService.deleteById(goodsId);
        return ResponseEntity.noContent().build();
    }
}
