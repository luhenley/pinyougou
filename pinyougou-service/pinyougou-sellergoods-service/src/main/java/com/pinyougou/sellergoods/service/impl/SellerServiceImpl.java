package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.mapper.SellerMapper;
import com.pinyougou.pojo.Seller;
import com.pinyougou.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Service(interfaceName = "com.pinyougou.service.SellerService")
/*上面指定接口名，产生服务名，不然会用代理名*/
@Transactional
public class SellerServiceImpl implements SellerService {
    @Autowired
    private SellerMapper sellerMapper;

    /* 添加商品 */
    @Override
    public void save(Seller seller) {
        try{
            seller.setStatus("0");
            seller.setCreateTime(new Date());
            sellerMapper.insertSelective(seller);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void update(Seller seller) {
        try{
            sellerMapper.updateByPrimaryKeySelective(seller);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    /** 根据主键id查询商家 */
    @Override
    public Seller findOne(Serializable id) {
        return sellerMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Seller> findAll() {
        return null;
    }

    /* 多条件分页查询 */
    @Override
    public PageResult findByPage(Seller seller, int page, int rows) {
        try{
            /* 开始分页 */
            PageInfo<Seller> pageInfo = PageHelper.startPage(page,rows)
                    .doSelectPageInfo(new ISelect() {
                        @Override
                        public void doSelect() {
                            sellerMapper.findAll(seller);
                        }
                    });
            return new PageResult(pageInfo.getTotal(),pageInfo.getList());
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 修改商家状态 */
    @Override
    public void updateStatus(String sellerId, String status) {
        try{
            Seller seller = new Seller();
            seller.setSellerId(sellerId);
            seller.setStatus(status);
            sellerMapper.updateByPrimaryKeySelective(seller);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    /** 修改商家密码 */
    @Override
    public void updatePass(String sellerId, String newPass) {
        try{
            Seller seller = new Seller();
            seller.setSellerId(sellerId);
            seller.setPassword(newPass);
            sellerMapper.updateByPrimaryKeySelective(seller);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
