package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.ItemCat;
import com.pinyougou.service.ItemCatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/itemCat")
public class ItemCatController {
    @Reference(timeout = 10000)
    private ItemCatService itemCatService;

    /** 根据父级id查询商品分类 */
    @GetMapping("/findItemCatByParentId")
    public List<ItemCat> findItemCatByParentId(Long parentId){
        return itemCatService.findItemCatByParentId(parentId);
    }

    /* 添加商品类目 */
    @PostMapping("/save")
    public boolean save(@RequestBody ItemCat itemCat){
        try{
            itemCatService.saveItemCat(itemCat);
            return true;
        }catch (Exception ex){
           ex.printStackTrace();
        }
        return false;
    }

    /* 修改商品类目 */
    @PostMapping("/update")
    public boolean update(@RequestBody ItemCat itemCat){
        try{
            itemCatService.updateItemCat(itemCat);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /* 删除商品类目 */
    @GetMapping("/delete")
    public boolean delete(Long[] ids){
        try{
            itemCatService.deleteItemCat(ids);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
}
