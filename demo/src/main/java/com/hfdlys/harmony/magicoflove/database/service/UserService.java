package com.hfdlys.harmony.magicoflove.database.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hfdlys.harmony.magicoflove.database.MySQLJDBC;
import com.hfdlys.harmony.magicoflove.database.entity.User;
import com.hfdlys.harmony.magicoflove.database.mapper.UserMapper;
import com.hfdlys.harmony.magicoflove.util.SecurityUtil;



/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Jiasheng Wang
 * @since 2024-07-21
 */
public class UserService {
    /*
     * 单例模式
     */
    private static UserService instance;

    private UserMapper userMapper;

    private UserService() {
        userMapper = MySQLJDBC.getInstance().getSqlSession().getMapper(UserMapper.class);
    }

    /**
     * 获取服务器实例
     * @return 服务器实例
     */
    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public User login(String username, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        try {
            User user = userMapper.selectOne(wrapper);
            if (user == null) {
                return null;
            }
            if (SecurityUtil.hashPassword(password).equals(user.getPassword())) {
                return user;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Integer register(String username, String password, byte[] skin) {
        User user = new User();
        user.setUsername(username);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        if (userMapper.selectOne(wrapper) != null) {
            return -1;
        }
        
        try {
            user.setSkin(skin);
            user.setPassword(SecurityUtil.hashPassword(password));
            userMapper.insert(user);
            return user.getUserId();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
