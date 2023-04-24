package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.CitiesMapper;
import com.pinyougou.pojo.Cities;
import com.pinyougou.service.CitiesService;
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
@Service(interfaceName = "com.pinyougou.service.CitiesService")
@Transactional
public class CitiesServiceImpl implements CitiesService{
    /** 注入数据访问接口代理对象 */
    @Autowired
    private CitiesMapper citiesMapper;

    @Override
    public void save(Cities cities) {

    }

    @Override
    public void update(Cities cities) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Cities findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Cities> findAll() {
        return null;
    }

    @Override
    public List<Cities> findByPage(Cities cities, int page, int rows) {
        return null;
    }

    @Override
    public List<Cities> findByProvinceId(Integer provinceId) {
        /** 创建地址对象封装查询条件 */
        try{
            //select * from tb_cities where provinceId = '110000';
            //创建示范对象
            Example example = new Example(Cities.class);
            //创建条件对象
            Example.Criteria criteria = example.createCriteria();
            // user_id = ?
            criteria.andEqualTo("provinceId",provinceId);
            //条件查询
            return citiesMapper.selectByExample(example);
    }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

}
