package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.common.util.HttpClientUtils;
import com.pinyougou.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付服务接口实现类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-12-28<p>
 */
@Service(interfaceName = "com.pinyougou.service.WeixinPayService")
public class WeixinPayServiceImpl implements WeixinPayService {


    /* 微信公众账号或开放平台APP的唯一标识 */
    @Value("${appid}")
    private String appid;
    /** 商户账号 */
    @Value("${partner}")
    private String partner;
    /** 商户密钥 */
    @Value("${partnerkey}")
    private String partnerkey;

    /* 统一下单接口URL*/
    @Value("${unifiedorder}")
    private String unifiedorder;
    /* 查询订单接口URL */
    @Value("${orderquery}")
    private String orderquery;

    /** 关闭订单请求地址 */
    @Value("${closeorder}")
    private String closeorder;


    /**
     * 调用微信支付系统的“统一下单接口” 生成支付二维码
     * 获取"code_url" 支付URL
     * */
    public Map<String,String> genPayCode(String outTradeNo, String totalFee){
        try{

            // 1. 封装请求参数
            Map<String,String> params = new HashMap<>();
            // 公众账号ID	appid	是
            params.put("appid", appid);
            // 商户号	mch_id	是
            params.put("mch_id", partner);
            // 随机字符串	nonce_str	是
            params.put("nonce_str", WXPayUtil.generateNonceStr());
            // 商品描述	body	是
            params.put("body", "品优购");
            // 商户订单号	out_trade_no	是
            params.put("out_trade_no", outTradeNo);
            // 标价金额	total_fee (单位为分)	是
            params.put("total_fee", totalFee);
            // 终端IP	spbill_create_ip	是
            params.put("spbill_create_ip", "127.0.0.1");
            // 通知地址	notify_url 是
            params.put("notify_url", "http://www.pinyougou.com");
            // 交易类型	trade_type	是
            params.put("trade_type", "NATIVE");

            // 生成带签名的xml请求参数
            String xmlParam = WXPayUtil.generateSignedXml(params, partnerkey);
            System.out.println("xmlParam: " + xmlParam);

            // 2. 调用统一下单接口，得到响应数据
            HttpClientUtils httpClientUtils = new HttpClientUtils(true);
            String xmlData = httpClientUtils.sendPost(unifiedorder, xmlParam);
            System.out.println("xmlData: " + xmlData);


            // 3. 返回数据
            Map<String,String> data = new HashMap<>();
            // 订单号
            data.put("outTradeNo", outTradeNo);
            // 金额
            data.put("totalFee", totalFee);

            // 把xmlData转化成Map集合
            Map<String, String> resMap = WXPayUtil.xmlToMap(xmlData);
            // 支付URL 二维码链接	code_url
            data.put("codeUrl", resMap.get("code_url"));
            return data;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }


    /**
     * 调用微信支付系统的“查询订单接口” 获取支付状态
     * 获取"trade_code" 交易状态
     * */
    public Map<String,String> queryPayStatus(String outTradeNo){
        try{

            // 1. 封装请求参数
            Map<String,String> params = new HashMap<>();
            // 公众账号ID	appid	是
            params.put("appid", appid);
            // 商户号	mch_id	是
            params.put("mch_id", partner);
            // 商户订单号	out_trade_no	是
            params.put("out_trade_no", outTradeNo);
            // 随机字符串	nonce_str	是
            params.put("nonce_str", WXPayUtil.generateNonceStr());

            // 生成带签名的xml请求参数
            /** 根据商户密钥签名生成XML格式请求参数 */
            String xmlParam = WXPayUtil.generateSignedXml(params, partnerkey);
            System.out.println("xmlParam: " + xmlParam);

            // 2. 调用查询订单接口，得到响应数据
            HttpClientUtils httpClientUtils = new HttpClientUtils(true);
            /** 发送请求，得到响应数据 */
            String xmlData = httpClientUtils.sendPost(orderquery, xmlParam);
            System.out.println("xmlData: " + xmlData);

            // 3. 返回数据
            /** 将响应数据XML格式转化成Map集合 */
            return WXPayUtil.xmlToMap(xmlData);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 关闭超时未支付订单 */
    @Override
    public Map<String, String> closePayTimeout(String outTradeNo) {
        /** 创建Map集合封装请求参数 */
        Map<String, String> params = new HashMap<>();
        /** 公众账号 */
        params.put("appid", appid);
        /** 商户账号 */
        params.put("mch_id", partner);
        /** 订单交易号 */
        params.put("out_trade_no", outTradeNo);
        /** 随机字符串 */
        params.put("nonce_str", WXPayUtil.generateNonceStr());
        try{
            /** 生成签名的xml参数 */
            String xmlParam = WXPayUtil.generateSignedXml(params, partnerkey);
            System.out.println("请求参数：" + xmlParam);
            /** 创建HttpClientUtils对象 */
            HttpClientUtils client = new HttpClientUtils(true);
            /** 发送post请求，得到响应数据 */
            String result = client.sendPost(closeorder, xmlParam);
            System.out.println("响应数据：" + result);

            /** 将xml响应数据转化成Map */
            return WXPayUtil.xmlToMap(result);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
