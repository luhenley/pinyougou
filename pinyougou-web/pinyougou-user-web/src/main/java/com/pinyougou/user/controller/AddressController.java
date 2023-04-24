package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Address;
import com.pinyougou.pojo.User;
import com.pinyougou.service.AddressService;
import com.pinyougou.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author Lu.Henley
 * @Date File Created at 2023-02-10
 * @Version 1.0
 */
@RestController
@RequestMapping("/address")
public class AddressController {
    @Reference(timeout = 10000)
    private AddressService addressService;

    //显示收货地址
    @PostMapping("/show")
    public List<Address> showAddress(){
        try {
            /** 获取登录用户名 */
            String loginName = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            return addressService.findAddressByUser(loginName);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    // 保存收货地址信息
    @PostMapping("/saveAddress")
    public Boolean saveAddress(@RequestBody Address address){
        try{
            /** 获取登录用户名 */
            String loginName = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            address.setUserId(loginName);
            addressService.save(address);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
    // 删除收货地址信息
    @GetMapping("/deleteAddressById")
    public Boolean deleteAddress(
            @RequestParam(value = "id")Long id){
        try{
            addressService.deleteById(id);
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

    @PostMapping("/updateDefault")
    public Boolean updateDefault(@RequestBody Address address){
        try{
            addressService.updateByUserId(address);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
}
