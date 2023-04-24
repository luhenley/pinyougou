package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.mapper.SpecificationMapper;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.pojo.Brand;
import com.pinyougou.pojo.Specification;
import com.pinyougou.pojo.SpecificationOption;
import com.pinyougou.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service(interfaceName = "com.pinyougou.service.SpecificationService")
/*上面指定接口名，产生服务名，不然会用代理名*/
@Transactional
public class SpecificationServiceImpl implements SpecificationService{
    /** 注入数据访问接口代理对象 */
    @Autowired
    private SpecificationMapper specificationMapper;
    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;


    /*添加规格*/
    @Override
    public void save(Specification specification) {
        try{
            specificationMapper.insertSelective(specification);
            specificationOptionMapper.save(specification);
        }catch (Exception ex){

        }
    }

    /* 修改规格与规格选项 */
    @Override
    public void update(Specification specification) {
        try{
            //修改往规格表数据
            specificationMapper.updateByPrimaryKeySelective(specification);
            /**########### 修改规格选项表数据 ###########*/
            // 第一步：删除规格选项表中的数据 spec_id
            // delete from tb_specification_option where spec_id = ?
            // 创建规格选项对象，封装删除条件 通用Mapper
            SpecificationOption so = new SpecificationOption();
            so.setSpecId(specification.getId());
            specificationOptionMapper.delete(so);
            // 第二步：往规格选项表插入数据
            specificationOptionMapper.save(specification);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void delete(Serializable id) {

    }

    /** 批量删除规格与规格选项 */
    @Override
    public void deleteAll(Serializable[] ids) {
        try{
            //开始===有追求，这里只是做个对比，代码还是冗余，
                //优化：可以设置MySQL级联删除，在mybatis配置文件设置
                    // 突然间忘了，有时间在回来看看2022.8.6
            //创建示范对象
            Example example = new Example(Specification.class);
            Example example2 = new Example(SpecificationOption.class);
            //创建条件对象
            Example.Criteria criteria = example.createCriteria();
            Example.Criteria criteria2 = example2.createCriteria();
            //添加in条件
            criteria.andIn("id", Arrays.asList(ids));
            criteria2.andIn("specId", Arrays.asList(ids));
            //根据条件删除
            specificationMapper.deleteByExample(example);
            specificationOptionMapper.deleteByExample(example2);
            //结束===有追求，这里只是做个对比，代码还是冗余，可优化，使用MySQL级联删除

            /*//开始===简单开发，轻松自在，但是性能不怎么好，没有什么追求
            for (Serializable id : ids){
                SpecificationOption so = new SpecificationOption();
                so.setSpecId((Long)id);
                specificationOptionMapper.delete(so);
                specificationMapper.deleteByPrimaryKey(id);
            }
            //结束===简单开发，轻松自在，但是性能不怎么好，没有什么追求*/
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Specification findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Specification> findAll() {
        return null;
    }

    /** 多条件分页查询规格 */
    @Override
    public PageResult findByPage(Specification specification, int page, int rows) {
        try{
            /** 开始分页 */
            PageInfo<Specification> pageInfo = PageHelper.startPage(page,rows)
                    .doSelectPageInfo(new ISelect() {
                        @Override
                        public void doSelect() {
                            specificationMapper.findAll(specification);
                        }
                    });
            return new PageResult(pageInfo.getTotal(),pageInfo.getList());
        }catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /** 根据规格主键查询规格选项 */
    @Override
    public List<SpecificationOption> findSpecOption(Long id) {
        try{
            //创建规格选项对象封装查询条件
            SpecificationOption so = new SpecificationOption();
            so.setSpecId(id);
            return specificationOptionMapper.select(so);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 查询所有的规格(id与specName) */
    @Override
    public List<Map<String, Object>> findAllByIdAndName() {
        try {
            return specificationMapper.findAllByIdAndName();
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
