package com.pinyougou.user.controller;

import com.pinyougou.common.util.RandomValidateCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @Author Lu.Henley
 * @Date File Created at 2023-02-13
 * @Version 1.0
 */

@RestController
@RequestMapping("/verify")
public class PicVerifyController {
    private final static Logger logger = LoggerFactory.getLogger(PicVerifyController.class);

    /**
     * 生成验证码
     */
    @RequestMapping(value = "/getVerify")
    public void getVerify(HttpServletRequest request, HttpServletResponse response,HttpSession session) {

        try {

            //设置相应类型,告诉浏览器输出的内容为图片
            response.setContentType("image/jpeg");

            //设置响应头信息，告诉浏览器不要缓存此内容
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expire", 0);

            RandomValidateCodeUtil randomValidateCode = new RandomValidateCodeUtil();

            randomValidateCode.getRandomCode(request, response);//输出验证码图片方法
            System.out.println("图片验证码：" + session.getAttribute("RANDOMVALIDATECODEKEY"));

        } catch (Exception e) {

            logger.error("获取验证码失败>>>>   ", e);

        }

    }

    /**
     * 校验验证码
     */
    @RequestMapping(value = "/checkVerify", method = RequestMethod.POST, headers = "Accept=application/json")
    public boolean checkVerify(@RequestParam(value = "picCode")  String verifyInput,
                               HttpSession session) {
        try {

            //从session中获取随机数
            String inputStr = verifyInput;

            String random = (String) session.getAttribute("RANDOMVALIDATECODEKEY");
            System.out.println(random);
            System.out.println(verifyInput);

            if (random == null || "".equals(random) || !random.equalsIgnoreCase(inputStr)) {
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            logger.error("验证码校验失败", e);
            return false;
        }
    }

}
