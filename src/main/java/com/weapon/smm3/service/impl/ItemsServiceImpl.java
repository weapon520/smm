package com.weapon.smm3.service.impl;

import com.weapon.smm3.common.ExceptionMsg;
import com.weapon.smm3.mapper.ItemsMapper;
import com.weapon.smm3.pojo.Items;
import com.weapon.smm3.service.ItemsService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by weapon on 2015-12-1.
 */
@Service
public class ItemsServiceImpl implements ItemsService {

    @Resource
    private ItemsMapper itemsMapper;

    @Override
    public Items findItemsById(Integer id) throws Exception {
        Items items = itemsMapper.selectByPrimaryKey(id);
        //中间对商品信息进行业务处理
        if(items==null){
            //throw new ExceptionMsg("修改的商品信息不存在!");

        }


        return items;
    }

    @Override
    public void updateItems(Integer id, Items items) throws Exception {
        items.setId(id);
        itemsMapper.updateByPrimaryKeyWithBLOBs(items);
    }
}
