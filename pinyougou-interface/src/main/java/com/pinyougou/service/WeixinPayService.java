package com.pinyougou.service;

/**
 * @Author Lu.Henley
 * @Date File Created at 2022-12-23
 * @Version 1.0
 */

import java.util.Map;

/** 微信支付服务接口 */
public interface WeixinPayService {
    /*
     * 生成微信支付二维码
     * @param outTradeNo 订单交易号
     * @param totalFee 金额（分）
     * @return Map<String ,String>集合
     **/
    Map<String,String> genPayCode(String outTradeNo,
                                  String totalFee);

    /**
     * 查询支付状态
     * @param outTradeNo 订单交易号
     * @return java.util.Map<java.lang.String , java.lang.String>
     **/
    Map<String,String> queryPayStatus(String outTradeNo);

    /** 关闭超时未支付订单 */
    Map<String,String> closePayTimeout(String outTradeNo);
}
