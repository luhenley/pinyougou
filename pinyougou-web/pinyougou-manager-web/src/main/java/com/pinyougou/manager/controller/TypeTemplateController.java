package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.TypeTemplate;
import com.pinyougou.service.TypeTemplateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController//@RestController 是@controller和@ResponseBody 的结合
@RequestMapping("/typeTemplate")
public class TypeTemplateController {
    /**
            * 引用服务层接口
     * timeout: 调用服务方法超时时间 1000毫秒
     * */
    @Reference(timeout = 10000)
    private TypeTemplateService typeTemplateService;
    /*分页查询类型模板*/
    @GetMapping("/findByPage")
    public PageResult findByPage(TypeTemplate typeTemplate,
                                 Integer page,Integer rows){
        /*GET请求中文转码*/
        if (typeTemplate != null && StringUtils.isNoneBlank(typeTemplate.getName())){
            try{
                typeTemplate.setName(new String(typeTemplate.getName()));
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return typeTemplateService.findByPage(typeTemplate,page,rows);
    }

    /** 添加类型模板 */
    @PostMapping("/save")
    public boolean save(@RequestBody TypeTemplate typeTemplate){
        try{
            typeTemplateService.save(typeTemplate);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /** 修改类型模板 */
    @PostMapping("/update")
    public boolean update(@RequestBody TypeTemplate typeTemplate){
        try{
            typeTemplateService.update(typeTemplate);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /** 删除类型模板 */
    @GetMapping("/delete")
    public boolean delete(Long[] ids){
        try{
            typeTemplateService.deleteAll(ids);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /** 查询类型模板 */
    @GetMapping("/findTypeTemplateList")
    public List<Map<String,Object>> findTypeTemplateList(){
        return typeTemplateService.findTypeTemplateList();
    }
}
