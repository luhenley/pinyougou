package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Order;
import com.pinyougou.pojo.OrderItem;
import com.pinyougou.pojo.PayLog;
import com.pinyougou.service.OrderService;
import com.pinyougou.service.PayLogService;
import com.pinyougou.service.WeixinPayService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Lu.Henley
 * @Date File Created at 2023-02-14
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

    /*  支付订单接口*/
    @Reference(timeout = 10000)
    private PayLogService payLogService;



    // 完善显示所有订单
    @PostMapping("/showOrders")
    public List<Order> showOrders(){
        try{
            /** 获取登录用户名 */
            String loginName = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            return orderService.findByUserId(loginName);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    // 显示商品
    @PostMapping("/showOrdersItems")
    public List<OrderItem> showOrdersItems(String orderId){
        try{
            return orderService.findByOrderId(orderId);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /* 分页查询品牌 */
    @GetMapping("/findByPage")
    public PageResult findByPage(Order order, Integer page, Integer rows){
        /** 获取登录用户名 */
        String loginName = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        try{
            order.setUserId(loginName);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        /** GET请求中文转码 *//*
        if (order != null && StringUtils.isNoneBlank(order.getName())){
            try{
                order.setName(new String(order.getName()
                        .getBytes("ISO8859-1"),"UTF-8"));
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }*/
        return orderService.findByPage(order,page,rows);
    }


    /** 根据订单号查询支付订单，并保存在Redis */
    @GetMapping("/findPayLogFromOrderId")
    public boolean findPayLogFromOrderId(String orderId){
        //根据订单号查询支付订单,并存入Redis
        return orderService.findPayLogFromOrderId(orderId);
    }

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
