package com.weapon.smm3.service;

import com.weapon.smm3.pojo.User;

import java.util.List;

/**
 * Created by weapon on 2015-11-29.
 */
public interface UserService {
    List<User> findUser()throws Exception;
}
