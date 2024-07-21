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

    public Integer login(String username, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        try {
            User user = userMapper.selectOne(wrapper);
            if (user == null) {
                return -1;
            }
            if (SecurityUtil.hashPassword(password) == user.getPassward()) {
                return user.getUserId();
            }
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public Integer register(String username, String password) {
        User user = new User();
        user.setUsername(username);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        if (userMapper.selectOne(wrapper) != null) {
            return -1;
        }
        try {
            user.setPassward(SecurityUtil.hashPassword(password));
            userMapper.insert(user);
            return user.getUserId();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
