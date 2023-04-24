package com.pinyougou.user.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/** 登录控制器 */
@RestController
public class LoginController {
    @GetMapping("/user/showName")
    public Map<String,String> showName(){
        /** 获取登录用户名 */
        String name = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        Map<String,String> map = new HashMap<>();
        map.put("loginName",name);
        return map;
    }
}
