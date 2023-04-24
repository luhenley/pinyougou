package com.pinyougou.service;

import com.pinyougou.pojo.Address;
import com.pinyougou.pojo.User;

import java.util.List;
import java.io.Serializable;
/**
 * AddressService 服务接口
 * @date 2022-07-31 13:45:01
 * @version 1.0
 */
public interface AddressService {

	/** 添加方法 */
	void save(Address address);

	/** 修改方法 */
	void update(Address address);

	/** 根据主键id删除 */
	void delete(Serializable id);

	/** 批量删除 */
	void deleteAll(Serializable[] ids);

	/** 根据主键id查询 */
	Address findOne(Serializable id);

	/** 查询全部 */
	List<Address> findAll();

	/** 多条件分页查询 */
	List<Address> findByPage(Address address, int page, int rows);

	/**
	 * 根据用户编号查询地址
	 * @param userId 用户编号
	 * @return 地址集合
	 */
	List<Address> findAddressByUser(String userId);

	// 根据id删除收货地址信息
	void deleteById(Long id);

	// 根据用户名批量更新
	void updateByUserId(Address address);
}