package com.hfdlys.harmony.magicoflove.database.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hfdlys.harmony.magicoflove.database.MySQLJDBC;
import com.hfdlys.harmony.magicoflove.database.entity.User;
import com.hfdlys.harmony.magicoflove.database.mapper.UserMapper;
import com.hfdlys.harmony.magicoflove.util.EmailUtil;
import com.hfdlys.harmony.magicoflove.util.SecurityUtil;



import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;
import java.util.List;

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

    public Integer register(String username, String password, String email, byte[] skin) {
        User user = new User();
        user.setUsername(username);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        if (userMapper.selectOne(wrapper) != null) {
            return -1;
        }

        if (!EmailUtil.sendEmail(email, "注册成功", "您已成功注册")) {
            return -2;
        }

        user.setEmail(email);
        
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

    public void deleteUser(int id) {
        userMapper.deleteById(id);
    }

    public void updateUser(User user) {
        userMapper.updateById(user);
    }

    public List<User> getUserList() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        return userMapper.selectList(wrapper);
    }

    public void exportUser(String path) {
        try (Workbook workbook = new HSSFWorkbook()) {
            List<User> userList = getUserList();
            
            Sheet sheet = workbook.createSheet("User");
            sheet.createRow(0).createCell(0).setCellValue("Id");
            sheet.getRow(0).createCell(1).setCellValue("Username");
            sheet.getRow(0).createCell(2).setCellValue("Email");
            for (int i = 0; i < userList.size(); i++) {
                User user = userList.get(i);
                sheet.createRow(i + 1).createCell(0).setCellValue(user.getUserId());
                sheet.getRow(i + 1).createCell(1).setCellValue(user.getUsername());
                sheet.getRow(i + 1).createCell(2).setCellValue(user.getEmail());
            }
            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }
            path = path + "/user.xls";
            FileOutputStream fileOut = new FileOutputStream(path);
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
