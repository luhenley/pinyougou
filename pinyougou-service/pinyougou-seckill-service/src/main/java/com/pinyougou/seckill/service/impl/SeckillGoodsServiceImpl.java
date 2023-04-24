package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.pojo.SeckillGoods;
import com.pinyougou.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author Lu.Henley
 * @Date File Created at 2022-12-25
 * @Version 1.0
 */
@Service(interfaceName = "com.pinyougou.service.SeckillGoodsService")
@Transactional
public class SeckillGoodsServiceImpl implements SeckillGoodsService{

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void save(SeckillGoods seckillGoods) {

    }

    @Override
    public void update(SeckillGoods seckillGoods) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public SeckillGoods findOne(Serializable id) {
        return null;
    }

    @Override
    public List<SeckillGoods> findAll() {
        return null;
    }

    @Override
    public List<SeckillGoods> findByPage(SeckillGoods seckillGoods,
                                         int page, int rows) {
        return null;
    }

    /** 查询秒杀的商品集合 */
    @Override
    public List<SeckillGoods> findSeckillGoods() {
        // 定义秒杀商品数据
        List<SeckillGoods> seckillGoodsList = null;
        try{
            // 从Redis中获取秒杀商品数据
            seckillGoodsList = redisTemplate.boundHashOps("seckillGoodsList").values();
            if (seckillGoodsList != null && seckillGoodsList.size() > 0){
                System.out.println("========从Redis数据库或获取秒杀商品========");
                return seckillGoodsList;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        try{
            // select * from tb_seckill goods where status = 1 and stock_count >0
            // and start_time <= now() and end_time >= now()
            /** 创建示范对象 */
            Example example = new Example(SeckillGoods.class);
            /** 创建条件查询对象 */
            Example.Criteria criteria = example.createCriteria();
            /** 审核状态码：审核通过 */
            criteria.andEqualTo("status","1");
            /** 剩余库存大于0 */
            criteria.andGreaterThan("stockCount", 0);
            /** 开始时间小于等于当前时间 */
            criteria.andLessThanOrEqualTo("startTime", new Date());
            /** 结束时间大于等于当前时间 */
            criteria.andGreaterThanOrEqualTo("endTime", new Date());

            seckillGoodsList = seckillGoodsMapper.selectByExample(example);
            System.out.println("=========把秒杀商品存入缓存=========");
            try{
                for (SeckillGoods seckillGoods : seckillGoodsList) {
                    redisTemplate.boundHashOps("seckillGoodsList")
                            .put(seckillGoods.getId(),seckillGoods);
                }
            }catch (Exception ex){
                throw new RuntimeException(ex);
            }
            //条件查询
            return seckillGoodsMapper.selectByExample(example);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 根据秒杀商品id查询商品 */
    @Override
    public SeckillGoods findOneFromRedis(Long id) {
        try{
            /** 获取秒杀商品 */
            return (SeckillGoods) redisTemplate
                    .boundHashOps("seckillGoodsList").get(id);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
