package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.Cart;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.Item;
import com.pinyougou.pojo.OrderItem;
import com.pinyougou.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** 购物车服务接口实现类 */
@Service(interfaceName = "com.pinyougou.service.CartService")
@Transactional
public class CartServiceImpl implements CartService {
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 从Redis中查询购物车
     * @param username 用户名
     * @return 购物车
     */
    public List<Cart> findCartRedis(String username){
        System.out.println("获取Redis中购物车："  + username);
        List<Cart> carts = (List<Cart>) redisTemplate
                .boundValueOps("cart_" + username).get();
        if (carts ==  null){
            carts = new ArrayList<>(0);
        }
        return carts;
    }

    /**
     * 将购物车保存到Redis
     * @param username 用户名
     * @param carts 购物车
     */
    public void saveCartRedis(String username,List<Cart> carts){
        System.out.println("往Redis存入购物车：" + username);
        redisTemplate.boundValueOps("cart_" + username).set(carts);
    }

    /** 合并购物车 */
    public List<Cart> mergeCart(List<Cart> cookieCarts, List<Cart>
            redisCarts) {
        //迭代Cookie 中的购物车数据
        for (Cart cart:cookieCarts) {
            //迭代订单明细
            for (OrderItem orderItem : cart.getOrderItems()){
                redisCarts = addItemToCart(redisCarts,
                        orderItem.getItemId(),orderItem.getNum());
            }
        }
        return redisCarts;
    }

    /**
     * 添加SKU商品到购物车
     * @param carts 购物车(一个Cart对应一个商家)
     * @param itemId SKU商品id
     * @param num 购买数据
     * @return 修改后的购物车
     */
    public List<Cart> addItemToCart(List<Cart> carts,
                                    Long itemId, Integer num){
        try{
            // 根据SKU商品ID查询SKU商品对象
            Item item = itemMapper.selectByPrimaryKey(itemId);
            // 获取商家id
            String sellerId = item.getSellerId();
            // 根据商家id判断购物车集合中是否存在该商家的购物车
            Cart cart = searchCartBySellerId(carts, sellerId);
            if (cart == null){ // 购物车集合中不存在该商家购物车
                // 创建新的购物车对象
                cart = new Cart();
                cart.setSellerId(sellerId);
                cart.setSellerName(item.getSeller());
                // 创建订单明细(购物中一个商品)
                OrderItem orderItem = createOrderItem(item, num);
                List<OrderItem> orderItems = new ArrayList<>(0);
                orderItems.add(orderItem);
                // 为购物车设置订单明细集合
                cart.setOrderItems(orderItems);

                // 将新的购物车对象添加到购物车集合
                carts.add(cart);
            }else{ // 购物车集合中存在该商家购物车
                // 判断购物车订单明细集合中是否存在该商品
                OrderItem orderItem = searchOrderItemByItemId(
                        cart.getOrderItems(), itemId);
                if (orderItem == null){
                    // 如果没有，新增购物车订单明细
                    orderItem = createOrderItem(item, num);
                    cart.getOrderItems().add(orderItem);
                }else{
                    // 如果有，在原购物车订单明细上添加数量，更改金额
                    orderItem.setNum(orderItem.getNum() + num);
                    orderItem.setTotalFee(new BigDecimal(
                            orderItem.getPrice().doubleValue()
                                    * orderItem.getNum()));
                    // 如果订单明细的购买数小于等于0，则删除
                    if (orderItem.getNum() <= 0){
                        // 删除购物车中的订单明细(商品)
                        cart.getOrderItems().remove(orderItem);
                    }
                    // 如果cart的orderItems订单明细为0，则删除cart
                    if (cart.getOrderItems().size() == 0){
                        carts.remove(cart);
                    }
                }
            }
            return carts;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
    /** 从购物车集合中获取该商家的购物车 */
    private Cart searchCartBySellerId(
            List<Cart> carts, String sellerId) {
        for (Cart cart : carts){
            if (cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }
    /** 创建订单明细 */
    private OrderItem createOrderItem(Item item, Integer num) {
        // 创建订单明细
        OrderItem orderItem = new OrderItem();
        orderItem.setSellerId(item.getSellerId());
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setTitle(item.getTitle());
        // 小计
        orderItem.setTotalFee(new BigDecimal(
                item.getPrice().doubleValue() * num));
        return orderItem;
    }
    /** 从订单明细集合中获取指定订单明细 */
    private OrderItem searchOrderItemByItemId(
            List<OrderItem> orderItems, Long itemId) {
        for (OrderItem orderItem : orderItems){
            if (orderItem.getItemId().equals(itemId)){
                return orderItem;
            }
        }
        return null;
    }
}

