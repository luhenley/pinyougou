package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.Cart;
import com.pinyougou.common.util.CookieUtils;
import com.pinyougou.service.CartService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/** 购物车控制器 */
@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference(timeout = 30000)
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;


    /** 添加SKU商品到购物车 */
    @GetMapping("/addCart")
    /** 跨域请求注解 */
    @CrossOrigin(origins = "http://item.pinyougou.com",
        allowCredentials = "true")
    public boolean addCart(Long itemId, Integer num){
       /** 设置允许访问的域名 (99%的跨域请求只需设置 Access-Control-Allow-Origin)*/
       //response.setHeader("Access-Control-Allow-Origin", "http://item.pinyougou.com");
       // 设置允许访问Cookie（1%）
       //response.setHeader("Access-Control-Allow-Credentials","true");

        try{
            // 获取登录用户名
            String username = request.getRemoteUser();
            // 获取购物车集合
            List<Cart> carts = findCart();
            // 调用服务层添加SKU商品到购物车
            carts = cartService.addItemToCart(carts, itemId, num);
            if (StringUtils.isNoneBlank(username)){// 已登录
                /**######## 往Redis存储购物车 #######*/
                cartService.saveCartRedis(username,carts);
            }else {// 未登录
                /**######## 往Cookie存储购物车 #######*/
                // 将购物车重新存入Cookie中
                CookieUtils.setCookie(request, response,
                        CookieUtils.CookieName.PINYOUGOU_CART,
                        JSON.toJSONString(carts),
                        3600 * 24, true);
            }
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /** 获取购物车集合 */
    @GetMapping("/findCart")
    public List<Cart> findCart() {
        /** 获取登录用户名 */
        String username = request.getRemoteUser();
        //定义购物车集合
        List<Cart> carts = null;
        //判断用户是否登录
        if (StringUtils.isNoneBlank(username)) {//已登录
            /**######## 从Redis获取购物车 #######*/
            carts = cartService.findCartRedis(username);
            // 从Cookie 中获取购物车集合json 字符串
            String cartStr = CookieUtils.getCookieValue(request,
                    CookieUtils.CookieName.PINYOUGOU_CART,true);
            //判断是否为空
            if (StringUtils.isNoneBlank(cartStr)){
                //转化List集合
                List<Cart> cookieCarts = JSON.parseArray(cartStr,Cart.class);
                if (cookieCarts != null && cookieCarts.size() > 0){
                    //合并购物车
                    carts = cartService.mergeCart(cookieCarts,carts);
                    // 将合并后的购物车存入Redis
                    cartService.saveCartRedis(username,carts);
                    //删除Cookie购物车
                    CookieUtils.deleteCookie(request,response,
                            CookieUtils.CookieName.PINYOUGOU_CART);
                }
            }
        } else {
            /**######## 从Cookie获取购物车 #######*/
            // 从Cookie中获取购物车集合json字符串
            String cartStr = CookieUtils.getCookieValue(request,
                    CookieUtils.CookieName.PINYOUGOU_CART, true);
            // 判断是否为空
            if (StringUtils.isBlank(cartStr)) {
                cartStr = "[]";
            }
            carts = JSON.parseArray(cartStr, Cart.class);
        }
        return carts;
    }



}

