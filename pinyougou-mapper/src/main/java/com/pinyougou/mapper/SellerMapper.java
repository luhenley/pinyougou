package com.pinyougou.mapper;

import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Seller;

import java.util.List;

/**
 * SellerMapper 数据访问接口
 * @date 2022-07-31 13:41:47
 * @version 1.0
 */
public interface SellerMapper extends Mapper<Seller>{

    /* 多条件查询 */
    List<Seller> findAll(Seller seller);
}