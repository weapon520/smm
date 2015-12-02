package com.weapon.smm3.controller;

import com.weapon.smm3.pojo.Items;
import com.weapon.smm3.service.ItemsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by weapon on 2015-12-1.
 */
@Controller
@RequestMapping("/items")
public class ItemsController {



    @Resource
    private ItemsService itemsService;

    @RequestMapping(value="/editItems",method={RequestMethod.POST, RequestMethod.GET})
    //@RequestParam里边指定request传入参数名称和形参进行绑定。
    //通过required属性指定参数是否必须要传入
    //通过defaultValue可以设置默认值，如果id参数没有传入，将默认值和形参绑定。
    public String editItems(Model model,@RequestParam(value="id",required=true) Integer items_id)throws Exception {

        //调用service根据商品id查询商品信息
        Items items = itemsService.findItemsById(items_id);

        //通过形参中的model将model数据传到页面
        //相当于modelAndView.addObject方法
        model.addAttribute("itemsCustom", items);

        return "items/editItems";
    }

    //商品信息修改提交
    @RequestMapping("/editItemsSubmit")
    public String editItemsSubmit(HttpServletRequest request,Integer id,Items items)throws Exception{
        //调用service更新商品信息，页面需要将商品信息传到此方法
        itemsService.updateItems(id, items);

        //重定向到商品查询列表
        //		return "redirect:queryItems.action";
        //页面转发
        //return "forward:queryItems.action";
        return "success";
    }
}
