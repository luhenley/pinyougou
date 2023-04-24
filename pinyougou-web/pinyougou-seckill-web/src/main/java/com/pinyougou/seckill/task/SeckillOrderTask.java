package com.pinyougou.seckill.task;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.SeckillOrder;
import com.pinyougou.service.SeckillOrderService;
import com.pinyougou.service.WeixinPayService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 秒杀订单任务
 * @Author Lu.Henley
 * @Date File Created at 2022-12-26
 * @Version 1.0
 */
@Component
public class SeckillOrderTask {
    @Reference(timeout = 10000)
    private SeckillOrderService seckillOrderService;
    @Reference(timeout = 10000)
    private WeixinPayService weixinPayService;

    /** 定时关闭超时未支付定单(每隔3秒调度一次) */
    @Scheduled(cron = "0/3 * * * * ?")
    public void closeOrderTask(){
        System.out.println("==毫秒==" + System.currentTimeMillis());
        // 查询所有超时未支付的订单
        List<SeckillOrder> seckillOrderList = seckillOrderService.findOrderByTimeout();
        // 迭代未支付的订单
        for (SeckillOrder seckillOrder : seckillOrderList) {
            //关闭微信未支付订单
            Map<String,String> map = weixinPayService
                    .closePayTimeout(seckillOrder.getId().toString());
            //如果正常关闭
            if(map != null && "SUCCESS".equals(map.get("result_code"))){
                System.out.println("===超时，删除订单===");
                // 删除超时未支付的订单
                seckillOrderService.deleteOrderFromRedis(seckillOrder);
            }
        }
    }
}
