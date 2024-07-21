package com.hfdlys.harmony.magicoflove.database.service;

import com.hfdlys.harmony.magicoflove.database.MySQLJDBC;
import com.hfdlys.harmony.magicoflove.database.entity.Log;
import com.hfdlys.harmony.magicoflove.database.mapper.LogMapper;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Jiasheng Wang
 * @since 2024-07-21
 */
public class LogService {
    /*
     * 单例模式
     */
    private static LogService instance;

    private LogMapper logMapper;

    private LogService() {
        logMapper = MySQLJDBC.getInstance().getSqlSession().getMapper(LogMapper.class);
    }

    /**
     * 获取服务器实例
     * @return 服务器实例
     */
    public static LogService getInstance() {
        if (instance == null) {
            instance = new LogService();
        }
        return instance;
    }

    

}
