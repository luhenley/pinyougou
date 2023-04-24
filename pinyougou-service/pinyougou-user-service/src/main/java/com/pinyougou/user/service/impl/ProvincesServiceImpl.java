package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.AddressMapper;
import com.pinyougou.mapper.ProvincesMapper;
import com.pinyougou.pojo.Provinces;
import com.pinyougou.service.ProvincesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Lu.Henley
 * @Date File Created at 2023-02-10
 * @Version 1.0
 */
@Service(interfaceName = "com.pinyougou.service.ProvincesService")
@Transactional
public class ProvincesServiceImpl implements ProvincesService {
    /** 注入数据访问接口代理对象 */
    @Autowired
    private ProvincesMapper provincesMapper;

    @Override
    public void save(Provinces provinces) {

    }

    @Override
    public void update(Provinces provinces) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Provinces findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Provinces> findAll() {
        try{
            //查询所有省份
            return provincesMapper.selectAll();
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }

    }

    @Override
    public List<Provinces> findByPage(Provinces provinces, int page, int rows) {
        return null;
    }
}
