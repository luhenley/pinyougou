package com.pinyougou.service;

import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Item;
import com.pinyougou.pojo.Order;
import com.pinyougou.pojo.OrderItem;
import com.pinyougou.pojo.PayLog;

import java.util.List;
import java.io.Serializable;
/**
 * OrderService 服务接口
 * @date 2022-07-31 13:45:01
 * @version 1.0
 */
public interface OrderService {

	/** 添加方法 */
	void save(Order order);

	/** 修改方法 */
	void update(Order order);

	/** 根据主键id删除 */
	void delete(Serializable id);

	/** 批量删除 */
	void deleteAll(Serializable[] ids);

	/** 根据主键id查询 */
	Order findOne(Serializable id);

	/** 查询全部 */
	List<Order> findAll();

	/** 多条件分页查询 */
	//List<Order> findByPage(Order order, int page, int rows);
	PageResult findByPage(Order order, int page, int rows);

	/** 根据用户查询支付日志 */
    PayLog findPayLogFromRedis(String userId);

    /**
	 * 修改订单状态
	 * @param outTradeNo
	 * @param transaction_id
	 * @return void
	 **/
	void updatePayStatus(String outTradeNo, String transaction_id);

	/*
	 *  根据用户id查询订单
	 * @param userId 用户id
	 * @return 订单列表
	 **/
	List<Order> findByUserId(String userId);

	/*
	 *  根据订单id查询订单商品
	 * @param orderId 订单id
	 * @return 商品列表
	 **/
	List<OrderItem> findByOrderId(String orderId);

	/** 根据订单查询支付订单 */
    boolean findPayLogFromOrderId(String orderId);
}