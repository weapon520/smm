package com.weapon.smm3.service.impl;

import com.weapon.smm3.mapper.UserMapper;
import com.weapon.smm3.pojo.User;
import com.weapon.smm3.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by weapon on 2015-11-29.
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;


    public List<User> findUser() throws Exception {
        List<User> users = userMapper.selectByExample(null);

        return users;
    }
}
