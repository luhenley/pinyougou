package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.cart.Cart;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.OrderItemMapper;
import com.pinyougou.mapper.OrderMapper;
import com.pinyougou.mapper.PayLogMapper;
import com.pinyougou.pojo.Item;
import com.pinyougou.pojo.Order;
import com.pinyougou.pojo.OrderItem;
import com.pinyougou.pojo.PayLog;
import com.pinyougou.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author Lu.Henley
 * @Date File Created at 2022-12-21
 * @Version 1.0
 */
@Service(interfaceName = "com.pinyougou.service.OrderService")
@Transactional
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private PayLogMapper payLogMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /** 保存订单 */
    @Override
    public void save(Order order) {
        try{
            // 根据用户名获取Redis中购物车数据
            List<Cart> carts = (List<Cart>) redisTemplate
                    .boundValueOps("cart_" + order.getUserId()).get();

            /** 定义订单ID集合(一次支付对应多个订单) */
            List<String> orderIdList = new ArrayList<>();
            /** 定义多个订单支付的总金额（元） */
            double totalMoney = 0;

            // 迭代购物车数据
            for (Cart cart : carts) {
                /** ####### 往订单表插入数据 ######### */
                //生成订单主键id
                long orderId = idWorker.nextId();
                //创建新的订单
                Order order1 = new Order();
                //设置订单id
                order1.setOrderId(orderId);
                // 设置支付类型
                order1.setPaymentType(order.getPaymentType());
                // 设置支付状态码为“未支付”
                order1.setStatus("1");
                // 设置订单创建时间
                order1.setCreateTime(new Date());
                // 设置订单修改时间
                order1.setUpdateTime(order1.getCreateTime());
                // 设置用户名
                order1.setUserId(order.getUserId());
                // 设置收件人地址
                order1.setReceiverAreaName(order.getReceiverAreaName());
                // 设置收件人手机号码
                order1.setReceiverMobile(order.getReceiverMobile());
                // 设置收件人
                order1.setReceiver(order.getReceiver());
                // 设置订单来源
                order1.setSourceType(order.getSourceType());
                // 设置商家id
                order1.setSellerId(cart.getSellerId());

                //定义订单总金额
                double money = 0;
                /** ####### 往订单明细表插入数据 ######### */
                for (OrderItem orderItem : cart.getOrderItems()) {
                    //设置主键id
                    orderItem.setId(idWorker.nextId());
                    // 设置关联的订单id
                    orderItem.setOrderId(orderId);
                    // 累计总金额
                    money += orderItem.getTotalFee().doubleValue();
                    // 保存数据到订单明细表
                    orderItemMapper.insertSelective(orderItem);
                }
                // 设置支付总金额
                order1.setPayment(new BigDecimal(money));
                // 保存数据到订单表
                orderMapper.insertSelective(order1);

                /** 记录订单id*/
                orderIdList.add(String.valueOf(orderId));
                /** 记录总金额 */
                totalMoney += money;
            }
            /** 判断是否为微信支付 */
            if("1".equals(order.getPaymentType())){
                /** 创建支付日志对象 */
                PayLog payLog = new PayLog();
                /** 生成订单交易号 */
                String outTradeNo = String.valueOf(idWorker.nextId());
                /** 设置订单交易号 */
                payLog.setOutTradeNo(outTradeNo);
                /** 创建时间 */
                payLog.setCreateTime(new Date());
                /** 支付总金额(分) */
                payLog.setTotalFee((long)(totalMoney * 100));
                /** 用户ID */
                payLog.setUserId(order.getUserId());
                /** 支付状态 */
                payLog.setTradeState("0");
                /** 订单号集合，逗号分隔 */
                String ids = orderIdList.toString().replace("[", "")
                        .replace("]", "").replace(" ","");
                /** 设置订单号 */
                payLog.setOrderList(ids);
                /** 支付类型 */
                payLog.setPayType("1");
                /** 往支付日志表插入数据 */
                payLogMapper.insertSelective(payLog);
                /** 存入 Redis 缓存 */
                redisTemplate.boundValueOps("payLog_" + order.getUserId()).set(payLog);

            }
            // 删除该用户购物车数据
            redisTemplate.delete("cart_" + order.getUserId());
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void update(Order order) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Order findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Order> findAll() {
        return null;
    }

    @Override
    public PageResult findByPage(Order order, int page, int rows) {
        try{
            //开始分页
            PageInfo<Order> pageInfo = PageHelper.startPage(page,rows)
                    .doSelectPageInfo(new ISelect() {
                @Override
                public void doSelect() {
                    // 这个方法以后再写，主要是根据交易状态去进行条件查询
                    //orderMapper.findAll();
                    // select * from tb_order where user_id = lhn order by
                    //创建示范对象
                    Example example = new Example(Order.class);
                    //创建条件对象
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("userId",order.getUserId());
                    criteria.andEqualTo("status",order.getStatus());
                    // order by is_default desc
                    example.orderBy("updateTime").desc();
                    System.out.println(order.getUserId());
                    //添加条件
                    //orderMapper.select(order);
                    orderMapper.selectByExample(example);
                }
            });
            return new PageResult(pageInfo.getTotal(),pageInfo.getList());
        }catch (Exception ex){
            throw  new RuntimeException(ex);
        }

    }

    /** 根据用户查询支付日志 */
    @Override
    public PayLog findPayLogFromRedis(String userId) {
        try{
            return (PayLog) redisTemplate.boundValueOps("payLog_" + userId).get();
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * 修改订单状态
     * @param outTradeNo 订单交易号
     * @param transactionId 微信交易流水号
     */
    @Override
    public void updatePayStatus(String outTradeNo, String transactionId) {
        try{
            // 查询支付日志对象
            PayLog payLog = payLogMapper.selectByPrimaryKey(outTradeNo);

            // 1. 修改支付状态
            // 微信支付订单号
            payLog.setTransactionId(transactionId);
            // 支付状态 : 已支付
            payLog.setTradeState("1");
            // 支付时间
            payLog.setPayTime(new Date());
            // 修改
            payLogMapper.updateByPrimaryKeySelective(payLog);

            // 2. 修改订单状态
            // 获取多个订单号
            String[] orderIds = payLog.getOrderList().split(",");
            for (String orderId : orderIds){
                Order order = new Order();
                // 设置订单id
                order.setOrderId(Long.valueOf(orderId));
                // 设置订单付款状态 2、已付款
                order.setStatus("2");
                // 设置付款时间
                order.setPaymentTime(new Date());
                orderMapper.updateByPrimaryKeySelective(order);
            }

            // 3. 删除Redis中支付日志
            redisTemplate.delete("payLog_" + payLog.getUserId());

        }catch (Exception ex){
            throw new RuntimeException(ex);
        }

    }


    /*
     *  根据用户id查询订单
     * @param userId 用户id
     * @return 订单列表
     **/
    @Override
    public List<Order> findByUserId(String userId) {
        try{
            Order order = new Order();
            order.setUserId(userId);
            return orderMapper.select(order);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }



    /*
     *  根据订单id查询订单商品
     * @param orderId 订单id
     * @return 商品列表
     **/
    @Override
    public List<OrderItem> findByOrderId(String orderId) {
        /** 创建地址对象封装查询条件 */
        try{
            //select * from tb_order_item where order_id = 1607111917808124000 order by is_default asc
            //创建示范对象
            Example example = new Example(OrderItem.class);
            //创建条件对象
            Example.Criteria criteria = example.createCriteria();
            // user_id = ?
            criteria.andEqualTo("orderId",orderId);
            // order by is_default desc
            example.orderBy("id").desc();
            //条件查询
            return orderItemMapper.selectByExample(example);
            /*OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(Long.parseLong(orderId));
            return orderItemMapper.select(orderItem);*/
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 根据订单查询支付订单,并存入Redis */
    @Override
    public boolean findPayLogFromOrderId(String orderId) {
        try{
            PayLog payLog = new PayLog();
            payLog.setOrderList(orderId);
            List<PayLog> payLogList =  payLogMapper.select(payLog);
            //迭代支付订单列表
            for (PayLog paylog: payLogList) {
                //将查询到的支付订单保存到Redis
                /** 存入 Redis 缓存 */
                redisTemplate.boundValueOps("payLog_" + paylog.getUserId()).set(paylog);
            }
            return true;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
