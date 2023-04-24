package com.pinyougou.mapper;

import com.pinyougou.pojo.Specification;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.SpecificationOption;

/**
 * SpecificationOptionMapper 数据访问接口
 * @date 2022-07-31 13:41:47
 * @version 1.0
 */
public interface SpecificationOptionMapper extends Mapper<SpecificationOption>{

    /*添加规格选项*/
    void save(Specification specification);

}