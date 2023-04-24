package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Seller;
import com.pinyougou.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/seller")
public class SellerController {
    /** 注入商家服务接口代理对象 */
    @Reference(timeout = 10000)
    private SellerService sellerService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /* 添加商家 */
    @PostMapping("/save")
    public boolean save(@RequestBody Seller seller){
        try{
            /** 密码加密 */
            String password = passwordEncoder.encode(seller.getPassword());
            seller.setPassword(password);
            sellerService.save(seller);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /* 显示商家信息 */
    @PostMapping("/show")
    public Seller show(){
            /** 获取登录用户名 */
            String loginName = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            return sellerService.findOne(loginName);
    }

    /* 修改商家信息 */
    @PostMapping("/update")
    public boolean update(@RequestBody Seller seller){
        try{
            sellerService.update(seller);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    // 修改商家密码
    @PostMapping("/updatePass")
    public boolean updatePass(@RequestBody Map<String,String> map){
        try{
            //商家id
            String sellerId = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            //商家数据库原密码
            String password = sellerService.findOne(sellerId).getPassword();
            //页面原密码
            String oldPass = map.get("oldPass");
            //页面新密码
            String NewPass = map.get("newPass");
            System.out.println(passwordEncoder.matches(oldPass,password));
            //将页面的未加密的密码和已经加密的密码进行验证
            if (passwordEncoder.matches(oldPass,password)){
                String newPass = passwordEncoder.encode(NewPass);
                sellerService.updatePass(sellerId,newPass);
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

}
