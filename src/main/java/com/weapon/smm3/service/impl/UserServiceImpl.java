package com.weapon.smm3.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.weapon.smm3.mapper.RedisDao;
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
    @Resource
    private RedisDao redisDao;


    public List<User> findUser() throws Exception {
        List<User> users = userMapper.selectByExample(null);

        return users;
    }

    @Override
    public List<User> findUserByRedis(String redisKey) throws Exception {
        List<User> result=null;

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        if(redisDao.isKeyExist(redisKey)) {
            result = gson.fromJson(redisDao.readFromString(redisKey),List.class);
        }
        if(result==null){
            result = userMapper.selectByExample(null);
            if(result!=null)
                redisDao.writeToString(redisKey,gson.toJson(result,List.class));
        }
        return result;
    }
}
