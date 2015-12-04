package com.weapon.smm3.controller;

import com.google.common.collect.Lists;
import com.weapon.smm3.common.RedisCons;
import com.weapon.smm3.pojo.User;
import com.weapon.smm3.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by weapon on 2015-11-29.
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @RequestMapping("/findUser")
    public ModelAndView findUser()throws Exception{
        ModelAndView modelAndView = new ModelAndView();

        //调用service方法得到用户列表
        List<User> users = userService.findUser();

        //将得到的用户列表内容添加到ModelAndView中
        modelAndView.addObject("users",users);
        //设置响应的jsp视图
        modelAndView.setViewName("findUser");

        return modelAndView;
    }

    @RequestMapping("/findUserRedis")

    public ModelAndView findUserRedis()throws  Exception{
        ModelAndView modelAndView = new ModelAndView();
        List<User> users=userService.findUserByRedis(RedisCons.REDIS_KEY_USER);
        modelAndView.addObject("users",users);
        modelAndView.setViewName("findUser");
        return modelAndView;
    }
}
