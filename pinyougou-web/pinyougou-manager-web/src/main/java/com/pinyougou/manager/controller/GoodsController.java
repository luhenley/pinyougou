package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Goods;
import com.pinyougou.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference(timeout = 10000)
    private GoodsService goodsService;

    /** 搜索商品 */
    @GetMapping("/findByPage")
    public PageResult findByPage(Goods goods, Integer page, Integer rows){
        try{
            /** 添加查询条件 */
            goods.setAuditStatus("0");
            /** GET请求中文转码 */
            if (StringUtils.isNoneBlank(goods.getGoodsName())){
                goods.setGoodsName(new String(
                        goods.getGoodsName().getBytes("ISO8859-1"),"UTF-8"));
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        /** 调用服务层分页查询 */
        return goodsService.findByPage(goods, page, rows);
    }

    /** 商品审批，修改商品状态 */
    @GetMapping("/updateStatus")
    public boolean updateStatus(Long[] ids,String status){
        try{
            goodsService.updateStatus(ids,status);
            return true;
        }catch (Exception ex){
            return false;
        }
    }

    /** 商品删除，修改商品删除状态 */
    @GetMapping("/delete")
    public boolean delete(Long[] ids){
        try{
            goodsService.deleteAll(ids);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
}
