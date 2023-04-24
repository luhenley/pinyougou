package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.pojo.Order;
import com.pinyougou.pojo.PayLog;
import com.pinyougou.service.OrderService;
import com.pinyougou.service.WeixinPayService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Lu.Henley
 * @Date File Created at 2022-12-21
 * @Version 1.0
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference(timeout = 10000)
    private OrderService orderService;
    /**  微信支付服务接口 */
    @Reference(timeout = 10000)
    private WeixinPayService weixinPayService;

    /** 生成微信支付二维码 */
    @GetMapping("/genPayCode")
    public Map<String,String> genPayCode(HttpServletRequest request){
        // 获取登录用户名
        String userId = request.getRemoteUser();
        // 根据登录用户名到Redis中查询支付的订单
        PayLog payLog = orderService.findPayLogFromRedis(userId);

        /** 调用生成微信支付二维码服务方法 */
        return weixinPayService.genPayCode(payLog.getOutTradeNo(),
                String.valueOf(payLog.getTotalFee()));
    }

    /** 保存订单 */
    @PostMapping("/save")
    public boolean save(@RequestBody Order order,
                        HttpServletRequest request){
        try{
            // 获取登录用户名
            String userId = request.getRemoteUser();
            order.setUserId(userId);
            //设置订单来源PC端
            order.setSourceType("2");
            orderService.save(order);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /** 查询支付状态 */
    @GetMapping("/queryPayStatus")
    public Map<String,Integer> queryPayStatus(String outTradeNo){
        Map<String,Integer> data = new HashMap<>();
        data.put("status",3);
        try{
            // 调用查询订单接口
            Map<String,String> resMap = weixinPayService.queryPayStatus(outTradeNo);
            if (resMap != null && resMap.size() > 0){
                //判断是否支付成功
                if ("SUCCESS".equals(resMap.get("trade_state"))){
                    /** 修改订单状态 */
                    orderService.updatePayStatus(outTradeNo,
                            resMap.get("transaction_id"));

                    data.put("status",1);
                }
                if ("NOTPAY".equals(resMap.get("trade_state"))){
                    data.put("status",2);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return data;
    }
}
