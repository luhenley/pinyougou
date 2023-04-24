package com.pinyougou.mapper;

import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.ItemCat;

import java.util.List;

/**
 * ItemCatMapper 数据访问接口
 * @date 2022-07-31 13:41:47
 * @version 1.0
 */
public interface ItemCatMapper extends Mapper<ItemCat>{

    /** 批量删除商品类目  */
    void deleteById(List<Long> idLists);
}