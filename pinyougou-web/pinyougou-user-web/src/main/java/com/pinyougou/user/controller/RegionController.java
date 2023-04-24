package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.*;
import com.pinyougou.service.AddressService;
import com.pinyougou.service.AreasService;
import com.pinyougou.service.CitiesService;
import com.pinyougou.service.ProvincesService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author Lu.Henley
 * @Date File Created at 2023-02-10
 * @Version 1.0
 */
@RestController
@RequestMapping("/region")
public class RegionController {
    @Reference(timeout = 10000)
    private ProvincesService provincesService;
    @Reference(timeout = 10000)
    private CitiesService citiesService;
    @Reference(timeout = 10000)
    private AreasService areasService;

    /** 查询所有省份 */
    @GetMapping("/findAllProvince")
    public List<Provinces> findAllProvince(){
        try{
            return provincesService.findAll();
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 根据省份id查询城市 */
    @GetMapping("/findRegionByProvinceId")
    public List<Cities> findRegionByProvinceId(
            @RequestParam(value = "provinceId")Integer provinceId){
        try{
            return citiesService.findByProvinceId(provinceId);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 根据城市id查询地区 */
    @GetMapping("/findRegionByCityId")
    public List<Areas> findRegionByCityId(
            @RequestParam(value = "cityId")Integer cityId){
        try{
            return areasService.findByCityId(cityId);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
