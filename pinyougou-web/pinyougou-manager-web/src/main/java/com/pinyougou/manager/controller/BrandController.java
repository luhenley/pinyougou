package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Brand;
import com.pinyougou.service.BrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 品牌控制器
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2018-12-02<p>
 */
@RestController
@RequestMapping("/brand")
public class BrandController {

    /**
     * 引用服务层接口
     * timeout: 调用服务方法超时时间 1000毫秒
     * */
    @Reference(timeout = 10000)
    private BrandService brandService;

    /** 查询全部品牌 get请求 */
    @GetMapping("/findAll")
    public List<Brand> findAll(){
        System.out.println("brandService: " + brandService);
        return brandService.findAll();
    }

    /* 分页查询品牌 */
    @GetMapping("/findByPage")
    public PageResult findByPage(Brand brand,Integer page, Integer rows){
        /** GET请求中文转码 */
        if (brand != null && StringUtils.isNoneBlank(brand.getName())){
            try{
                brand.setName(new String(brand.getName()
                .getBytes("ISO8859-1"),"UTF-8"));
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        return brandService.findByPage(brand,page,rows);
    }

    /** 添加品牌 post请求 */
    @PostMapping("/save")
    public boolean save(@RequestBody Brand brand){
        try {
            brandService.save(brand);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /** 修改品牌 post请求 */
    @PostMapping("/update")
    public boolean update(@RequestBody Brand brand){
        try {
            brandService.update(brand);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /** 删除品牌 */
    @GetMapping("/delete")
    public boolean delete(Long[] ids){
        try{
            brandService.deleteAll(ids);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /** 查询所有的品牌 */
    @GetMapping("/findBrandList")
    public List<Map<String,Object>> findBrandList(){
        return brandService.findAllByIdAndName();
    }
}
