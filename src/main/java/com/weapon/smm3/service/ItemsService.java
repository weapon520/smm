package com.weapon.smm3.service;

import com.weapon.smm3.mapper.ItemsMapper;
import com.weapon.smm3.pojo.Items;

import javax.annotation.Resource;

/**
 * Created by weapon on 2015-12-1.
 */
public interface ItemsService {


    //根据id查询商品信息
    /**
     *
     * <p>Title: findItemsById</p>
     * <p>Description: </p>
     * @param id 查询商品的id
     * @return
     * @throws Exception
     */
    public Items findItemsById(Integer id) throws Exception;

    //修改商品信息
    /**
     *
     * <p>Title: updateItems</p>
     * <p>Description: </p>
     * @param id 修改商品的id
     * @param
     * @throws Exception
     */
    public void updateItems(Integer id,Items items) throws Exception;


}
