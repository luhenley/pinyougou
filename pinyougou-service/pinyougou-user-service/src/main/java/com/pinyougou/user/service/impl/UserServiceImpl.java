package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.common.util.HttpClientUtils;
import com.pinyougou.mapper.UserMapper;
import com.pinyougou.pojo.User;
import com.pinyougou.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service(interfaceName = "com.pinyougou.service.UserService")//
@Transactional//开启事务
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /** 短信服务需要用到的对象 */
    //将短信验证码存储到Redis中设置过期时间,需要引用
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Value("${sms.url}")
    private String smsUrl;
    @Value("${sms.signName}")
    private String signName;
    @Value("${sms.templateCode}")
    private String templateCode;

    @Override
    public void save(User user) {
        try{
            //创建日期
            user.setCreated(new Date());
            //修改日期
            user.setUpdated(user.getCreated());
            //密码加密
            user.setPassword(DigestUtils.md5Hex(user.getPassword()));
            userMapper.insertSelective(user);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void update(User user) {
        try{
            userMapper.updateByPrimaryKeySelective(user);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public User findOne(Serializable id) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public List<User> findByPage(User user, int page, int rows) {
        return null;
    }

    /** 发送短信验证码 */
    @Override
    public boolean sendCode(String phone){
        try{
            /** 生成6位随机数 */
            String code = UUID.randomUUID().toString()
                    .replaceAll("-", "")
                    .replaceAll("[a-z|A-Z]","")
                    .substring(0, 6);
            // 测试用固定6位
            //String code = "5201314";
            System.out.println("验证码：" + code);
            /** 调用短信发送接口 */
            HttpClientUtils httpClientUtils = new HttpClientUtils(false);
            // 创建Map集合封装请求参数
            Map<String, String> param = new HashMap<>();
            param.put("phone", phone);
            param.put("signName", signName);
            param.put("templateCode", templateCode);
            //param.put("templateParam", "{\"number\":\"" + code + "\"}");
            //测试用
            param.put("templateParam", "{\"code\":\"" + code + "\"}");
            // 发送Post请求
            String content = httpClientUtils.sendPost(smsUrl, param);
            // 把json字符串转化成Map
            Map<String, Object> resMap = JSON.parseObject(content, Map.class);
            /** 存入Redis中(90秒) */
            redisTemplate.boundValueOps(phone).set(code, 90, TimeUnit.SECONDS);
            return (boolean)resMap.get("success");
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 检查短信验证码是否正确 */
    @Override
    public boolean checkSmsCode(String phone, String code) {
        /** 获取Redis中存储的验证码 */
        String sysCode = redisTemplate.boundValueOps(phone).get();
        return StringUtils.isNoneBlank(sysCode) && sysCode.equals(code);
    }


    @Override
    public User findByName(String loginName) {
        /** 创建地址对象封装查询条件 */
        try{
            User user = new User();
            user.setUsername(loginName);
            return userMapper.selectOne(user);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void updatePass(String username, String newPass) {
        try{
            //update tb_user set password = '0' where username = 'lhn';
            //创建示范对象
            Example example = new Example(User.class);
            //创建条件对象
            Example.Criteria criteria = example.createCriteria();
            // user = ?
            criteria.andEqualTo("username",username);
            //set password = '0'
            User user = new User();
            user.setPassword(DigestUtils.md5Hex(newPass));
            userMapper.updateByExampleSelective(user,example);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
