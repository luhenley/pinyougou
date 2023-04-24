package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Address;
import com.pinyougou.service.AddressService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author Lu.Henley
 * @Date File Created at 2022-12-20
 * @Version 1.0
 */
@RestController
@RequestMapping("/order")
public class AddressController {

    @Reference(timeout = 10000)
    private AddressService addressService;
    /** 获取登录用户地址列表 */
    @GetMapping("/findAddressByUser")
    public List<Address> findAddressByUser(HttpServletRequest request){
        //获取登录用户名
        String userId = request.getRemoteUser();
        return addressService.findAddressByUser(userId);
    }


    // 保存收货地址信息
    @PostMapping("/saveAddress")
    public Boolean saveAddress(@RequestBody Address address,HttpServletRequest request){
        try{
            //获取登录用户名
            String loginName = request.getRemoteUser();
            address.setUserId(loginName);
            addressService.save(address);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }


    // 保存更新收货地址信息
    @PostMapping("/updateAddress")
    public Boolean updateAddress(@RequestBody Address address){
        try{
            addressService.update(address);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
}
