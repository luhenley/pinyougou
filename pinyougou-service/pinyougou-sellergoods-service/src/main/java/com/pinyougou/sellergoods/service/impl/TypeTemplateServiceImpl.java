package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.mapper.TypeTemplateMapper;
import com.pinyougou.pojo.SpecificationOption;
import com.pinyougou.pojo.TypeTemplate;
import com.pinyougou.service.TypeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service(interfaceName = "com.pinyougou.service.TypeTemplateService")
/*上面指定接口名，产生服务名，不然会用代理名*/
@Transactional//声明式事务管理
public class TypeTemplateServiceImpl implements TypeTemplateService {
    /*注入数据访问接口代理对象*/
    @Autowired
    private TypeTemplateMapper typeTemplateMapper;
    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;


    /** 添加类型模板 */
    @Override
    public void save(TypeTemplate typeTemplate) {
        typeTemplateMapper.insertSelective(typeTemplate);
    }

    /** 修改类型模板 */
    @Override
    public void update(TypeTemplate typeTemplate) {
        typeTemplateMapper.updateByPrimaryKeySelective(typeTemplate);
    }

    @Override
    public void delete(Serializable id) {

    }

    /** 删除类型模板 */
    @Override
    public void deleteAll(Serializable[] ids) {
        //创建示范对象
        Example example = new Example(TypeTemplate.class);
        //创建条件对象
        Example.Criteria criteria = example.createCriteria();
        //添加in条件
        criteria.andIn("id", Arrays.asList(ids));
        //条件删除
        typeTemplateMapper.deleteByExample(example);
    }

    /** 根据主键id查询类型模板 */
    @Override
    public TypeTemplate findOne(Serializable id) {
        return typeTemplateMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<TypeTemplate> findAll() {
        return null;
    }

    /* 分页查询类型模板*/
    @Override
    public PageResult findByPage(TypeTemplate typeTemplate, int page, int rows) {
        try{
            /* 开始分页*/
            PageInfo<TypeTemplate> pageInfo = PageHelper.startPage(page,rows)
                    .doSelectPageInfo(new ISelect() {
                        @Override
                        public void doSelect() {
                            typeTemplateMapper.findAll(typeTemplate);
                        }
                    });
            return new PageResult(pageInfo.getTotal(),pageInfo.getList());
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /* 查询类型模板 */
    @Override
    public List<Map<String, Object>> findTypeTemplateList() {
        try{
            return typeTemplateMapper.findTypeTemplateList();
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 根据模版id查询所有的规格与规格选项 */
    @Override
    public List<Map> findSpecByTemplateId(Long id) {
        try{
            TypeTemplate typeTemplate = findOne(id);
            /**
             * [{"id":33,"text":"电视屏幕尺寸"}]
             * 获取模版中所有的规格，转化成  List<Map>
             */
            List<Map> specLists =
                    JSON.parseArray(typeTemplate.getSpecIds(),Map.class);
            /** 迭代模版中所有的规格 */
            for (Map map:specLists) {
                /** 创建查询条件 */
                SpecificationOption so = new SpecificationOption();
                so.setSpecId(Long.valueOf(map.get("id").toString()));
                /** 通过规格id，查询规格选项数据 */
                List<SpecificationOption> specOptions =
                        specificationOptionMapper.select(so);
                /**
                 * [{"id":33,"text":"电视屏幕尺寸","options":specOptions}]
                 */
                map.put("options",specOptions);
            }
            return specLists;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
