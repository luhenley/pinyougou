package com.pinyougou.service;

import com.pinyougou.pojo.User;
import java.util.List;
import java.io.Serializable;
/**
 * UserService 服务接口
 * @date 2022-07-31 13:45:01
 * @version 1.0
 */
public interface UserService {

	/** 添加方法 */
	void save(User user);

	/** 修改方法 */
	void update(User user);

	/** 根据主键id删除 */
	void delete(Serializable id);

	/** 批量删除 */
	void deleteAll(Serializable[] ids);

	/** 根据主键id查询 */
	User findOne(Serializable id);

	/** 查询全部 */
	List<User> findAll();

	/** 多条件分页查询 */
	List<User> findByPage(User user, int page, int rows);

    boolean checkSmsCode(String phone, String smsCode);

	boolean sendCode(String phone);

	/** 根据用户id查询 */
	User findByName(String loginName);

    void updatePass(String username, String newPass);
}