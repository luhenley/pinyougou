package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.SeckillGoods;
import com.pinyougou.service.SeckillGoodsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author Lu.Henley
 * @Date File Created at 2022-12-25
 * @Version 1.0
 */
@RestController
@RequestMapping("/seckill")
public class SeckillGoodsController {
    @Reference(timeout = 10000)
    private SeckillGoodsService seckillGoodsService;

    /** 查询秒杀商品列表 */
    @GetMapping("/findSeckillGoodsList")
    public List<SeckillGoods> findSeckillGoods(){
        return seckillGoodsService.findSeckillGoods();
    }

    /** 根据秒杀商品id查询商品 */
    @GetMapping("/findOne")
    public SeckillGoods findOne(Long id){
        return seckillGoodsService.findOneFromRedis(id);
    }
}
