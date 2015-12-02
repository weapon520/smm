package com.weapon.smm3.controller;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by weapon on 2015-11-30.
 */
@RequestMapping("/")
@Controller
public class indexController {



    @RequestMapping(value = "")
    public String index(HttpServletRequest request,HttpServletResponse response){
        return "index";
    }
}
