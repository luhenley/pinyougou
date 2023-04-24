package com.pinyougou.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.GoodsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class ItemController {
    @Reference(timeout=10000)
    private GoodsService goodsService;

    /** 根据SPU的id查询商品信息
     *  http://item.pinyougou.com/1658006432.html
     *  {}取请求URL路径上的参数
     * */
    @GetMapping("/{goodsId}")
    public String getGoods(@PathVariable("goodsId")Long goodsId,
                           Model model){
        // 根据SPU的id查询商品数据
        Map<String,Object> data = goodsService.getGoods(goodsId);
        // 把商品数据添加到数据模型
        model.addAllAttributes(data);
        //返回视图字符串
        return "item";
    }
}
