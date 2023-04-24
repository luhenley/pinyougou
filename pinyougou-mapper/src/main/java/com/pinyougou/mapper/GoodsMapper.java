package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Goods;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * GoodsMapper 数据访问接口
 * @date 2022-07-31 13:41:47
 * @version 1.0
 */
public interface GoodsMapper extends Mapper<Goods>{

    /** 多条件查询商品 */
    List<Map<String,Object>> findAll(Goods goods);

    /** 批量修改状态
     * @Param("ids") Long[] ids,
     * @Param("status") String status
     * 两个参数mybatis不知道怎么获取，所以要指定名称
     * */
    void updateStatus(@Param("ids") Long[] ids,
                      @Param("status") String status);

    /** 修改删除状态 */
    void updateDeleteStatus(@Param("ids")Serializable[] ids,
                            @Param("isDelete")String isDelete);

    /** 修改可销售状态 */
    void updateMarketable(@Param("ids")Long[] ids,
                          @Param("status") String status);
}