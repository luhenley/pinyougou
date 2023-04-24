package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.TypeTemplate;

import java.util.List;
import java.util.Map;

/**
 * TypeTemplateMapper 数据访问接口
 * @date 2022-07-31 13:41:47
 * @version 1.0
 */
public interface TypeTemplateMapper extends Mapper<TypeTemplate>{

    /*多条件查询类型模板*/
    List<TypeTemplate> findAll(TypeTemplate typeTemplate);

    /* 查询类型模板 */
    @Select("select id,name from tb_type_template order by id asc")
    List<Map<String,Object>> findTypeTemplateList();
}