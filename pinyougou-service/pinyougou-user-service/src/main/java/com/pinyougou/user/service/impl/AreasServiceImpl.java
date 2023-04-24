package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.AreasMapper;
import com.pinyougou.pojo.Areas;
import com.pinyougou.service.AreasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Lu.Henley
 * @Date File Created at 2023-02-10
 * @Version 1.0
 */
@Service(interfaceName = "com.pinyougou.service.AreasService")
@Transactional
public class AreasServiceImpl implements AreasService{
    /** 注入数据访问接口代理对象 */
    @Autowired
    private AreasMapper areasMapper;

    @Override
    public void save(Areas areas) {

    }

    @Override
    public void update(Areas areas) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Areas findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Areas> findAll() {
        return null;
    }

    @Override
    public List<Areas> findByPage(Areas areas, int page, int rows) {
        return null;
    }

    @Override
    public List<Areas> findByCityId(Integer cityId) {
        /** 创建地址对象封装查询条件 */
        try{
            //select * from tb_cities where provinceId = '110000';
            //创建示范对象
            Example example = new Example(Areas.class);
            //创建条件对象
            Example.Criteria criteria = example.createCriteria();
            // user_id = ?
            criteria.andEqualTo("cityId",cityId);
            //条件查询
            return areasMapper.selectByExample(example);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
