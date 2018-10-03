package com.zy.dao;

import com.zy.vo.User;

import java.util.List;

/**
 * @author: Horizon
 * @time: 18:37 2018/10/3
 * Description:
 */
public interface UserDao {
    User getUserByUserName(String userName);

    List<String> getRolesByUserName(String userName);

}

