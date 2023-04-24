package com.pinyougou.service;

import com.pinyougou.cart.Cart;

import java.util.List;

/** 购物车服务接口 */
public interface CartService {

    /**
     * 添加SKU商品到购物车
     * @param carts 购物车(一个Cart对应一个商家)
     * @param itemId SKU商品id
     * @param num 购买数据
     * @return 修改后的购物车
     */
    List<Cart> addItemToCart(List<Cart> carts,
                             Long itemId, Integer num);

    /**
     * 将购物车保存到Redis
     * @param username 用户名
     * @param carts 购物车
     */
    void saveCartRedis(String username,List<Cart> carts);

    /**
     * 从Redis中查询购物车
     * @param username 用户名
     * @return 购物车
     */
    List<Cart> findCartRedis(String username);

    /**
     * 合并购物车
     * @param cookieCarts Cookie购物车
     * @param redisCarts Redis购物车
     * @return 合并后的购物车
     */
    List<Cart> mergeCart(List<Cart> cookieCarts,List<Cart> redisCarts);
}

