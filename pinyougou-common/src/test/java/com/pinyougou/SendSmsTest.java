package com.pinyougou;

import com.pinyougou.common.util.HttpClientUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 短信发送测试(调用接口)
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-12-23<p>
 */
public class SendSmsTest {

    public static void main(String[] args){
        HttpClientUtils httpClientUtils = new HttpClientUtils(false);
        Map<String, String> params = new HashMap<>();
        params.put("phone", "17818965367");
        params.put("signName", "五子连珠");
        params.put("templateCode","SMS_11480310");
        params.put("templateParam","{'number':'666666'}");

        String content = httpClientUtils.sendPost("http://sms.pinyougou.com/sms/sendSms", params);
        System.out.println(content);

    }
}
