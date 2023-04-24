package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Order;
import com.pinyougou.pojo.User;
import com.pinyougou.service.OrderService;
import com.pinyougou.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/user")
public class UserController {
    @Reference(timeout = 10000)
    private UserService userService;


    /** 注册用户 */
    @PostMapping("/save")
    public boolean save(@RequestBody User user,String smsCode){
        try{
            boolean ok = userService.checkSmsCode(user.getPhone(), smsCode);
            if (ok){
                userService.save(user);
                return true;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /** 发送短信验证码 */
    @GetMapping("/sendCode")
    public boolean sendCode(String phone){
        try{
            if (StringUtils.isNoneBlank(phone)){
                /** 发送验证码 */
                userService.sendCode(phone);
                return true;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }


    //显示个人信息
    @PostMapping("/showInfo")
    public User showInfo(){
        try {
            /** 获取登录用户名 */
            String loginName = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            return userService.findByName(loginName);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    // 完善用户个人信息
    @PostMapping("/saveInfo")
    public Boolean saveInfo(@RequestBody User user){
        try{
            userService.update(user);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    // 修改用户密码
    @PostMapping("/updatePass")
    public boolean updatePass(@RequestBody Map<String,String> map){
        try{
            //商家id
            String username = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            //商家数据库原密码
            String password = userService.findByName(username).getPassword();
            //页面原密码
            String oldPass = map.get("oldPass");
            //页面新密码
            String newPass = map.get("newPass");
            //将页面原密码加密后和数据库做验证，正确则更新密码
            if(DigestUtils.md5Hex(oldPass).equals(password)){
                userService.updatePass(username,newPass);
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    /** 用户绑定手机验证第一步 */
    @PostMapping("/nextStep")
    public boolean nextStep(@RequestBody User user,String smsCode){
        try{
            return userService.checkSmsCode(user.getPhone(), smsCode);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /* 用户手机改绑新的手机号*/
    /** 注册用户 */
    @PostMapping("/updatePhone")
    public boolean updatePhone(@RequestBody User user,String smsCode){
        try{
            boolean ok = userService.checkSmsCode(user.getPhone(), smsCode);
            if (ok){
                userService.update(user);
                return true;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }





}
