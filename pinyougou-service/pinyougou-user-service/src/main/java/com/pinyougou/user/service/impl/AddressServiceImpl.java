package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.AddressMapper;
import com.pinyougou.pojo.Address;
import com.pinyougou.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Lu.Henley
 * @Date File Created at 2022-12-20
 * @Version 1.0
 */
@Service(interfaceName = "com.pinyougou.service.AddressService")
@Transactional
public class AddressServiceImpl implements AddressService {

    /** 注入数据访问接口代理对象 */
    @Autowired
    private AddressMapper addressMapper;

    @Override
    public void save(Address address) {
            addressMapper.insertSelective(address);
    }

    @Override
    public void update(Address address) {
        addressMapper.updateByPrimaryKey(address);
    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Address findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Address> findAll() {
        return null;
    }

    @Override
    public List<Address> findByPage(Address address, int page, int rows) {
        return null;
    }

    /**
     * 根据用户编号查询地址
     * @param userId 用户编号
     * @return 地址集合
     */
    @Override
    public List<Address> findAddressByUser(String userId) {
        /** 创建地址对象封装查询条件 */
        try{
            //select * from tb_address where user_id = 'itcast' order by is_default desc
            //创建示范对象
            Example example = new Example(Address.class);
            //创建条件对象
            Example.Criteria criteria = example.createCriteria();
            // user_id = ?
            criteria.andEqualTo("userId",userId);
            // order by is_default desc
            example.orderBy("isDefault").desc();
            //条件查询
             return addressMapper.selectByExample(example);
        }catch (Exception ex){
           throw new RuntimeException(ex);
        }
    }

    // 根据id删除收货地址信息
    @Override
    public void deleteById(Long id) {
        try{
            addressMapper.deleteByPrimaryKey(id);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void updateByUserId(Address address) {
        /** 创建地址对象封装查询条件 */
        try{
            //update tb_address set is_default = '0' where user_id = 'lhn';
            //创建示范对象
            Example example = new Example(Address.class);
            //创建条件对象
            Example.Criteria criteria = example.createCriteria();
            // user_id = ?
            criteria.andEqualTo("userId",address.getUserId());
            //set is_default = '0'
            Address newAddress = new Address();
            newAddress.setIsDefault("0");
            //先根据用户id将所有的地址默认改为is_default = 0
            addressMapper.updateByExampleSelective(newAddress,example);
            //然后再将当前要设为默认的地址的值修改为1
            address.setIsDefault("1");
            addressMapper.updateByPrimaryKeySelective(address);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }


}
