package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.ItemCatMapper;
import com.pinyougou.pojo.ItemCat;
import com.pinyougou.service.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Service(interfaceName = "com.pinyougou.service.ItemCatService")
@Transactional
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private ItemCatMapper itemCatMapper;

    @Override
    public void save(ItemCat itemCat) {

    }

    @Override
    public void update(ItemCat itemCat) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public ItemCat findOne(Serializable id) {
        return null;
    }

    @Override
    public List<ItemCat> findAll() {
        return null;
    }

    @Override
    public List<ItemCat> findByPage(ItemCat itemCat, int page, int rows) {
        return null;
    }

    /** 根据父级id查询商品分类 */
    @Override
    public List<ItemCat> findItemCatByParentId(Long parentId) {
        try{
            /** 创建ItemCat封装查询条件 */
            ItemCat itemCat = new ItemCat();
            itemCat.setParentId(parentId);
            return itemCatMapper.select(itemCat);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /* 添加商品类目 */
    @Override
    public void saveItemCat(ItemCat itemCat) {
        try{
            itemCatMapper.insertSelective(itemCat);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 修改商品类目 */
    @Override
    public void updateItemCat(ItemCat itemCat) {
        try{
            itemCatMapper.updateByPrimaryKeySelective(itemCat);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /**
     * 删除商品类目
     * @param ids 多个类目id
     */
    @Override
    public void deleteItemCat(Long[] ids) {
        try{
            /** 定义List集合 */
            List<Long> idLists = new ArrayList<>();
            for (Long id:ids) {
                /* 添加id */
                idLists.add(id);
                /** 递归查询 */
                findLeafNode(id, idLists);
            }
            /* 批量删除商品类目 */
            itemCatMapper.deleteById(idLists);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 递归查询所有的子节点 */
    private void findLeafNode(Long id,List<Long> idLists){
        /** 根据父节点查询所有的子节点 */
        List<ItemCat> itemCatLists = findItemCatByParentId(id);
        /** 判断集合是否为空，也是递归退出条件 */
        if (itemCatLists != null && itemCatLists.size() > 0){
            for (ItemCat itemCat:itemCatLists) {
                idLists.add(itemCat.getId());
                findLeafNode(itemCat.getId(),idLists);
            }
        }
    }
}
