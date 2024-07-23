package com.hfdlys.harmony.magicoflove.database.service;

import com.hfdlys.harmony.magicoflove.database.MySQLJDBC;
import com.hfdlys.harmony.magicoflove.database.entity.Log;
import com.hfdlys.harmony.magicoflove.database.mapper.LogMapper;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.io.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Jiasheng Wang
 * @since 2024-07-21
 */
@Slf4j
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

    public List<Log> getLogList() {
        LambdaQueryWrapper<Log> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Log::getCreateTime);
        return logMapper.selectList(wrapper);
    }

    public Log insertLog(String content) {
        Log log = new Log();
        log.setContent(content);
        logMapper.insert(log);
        return log;
    }

    public void deleteLog(int id) {
        logMapper.deleteById(id);
    }

    public void exportLog(String path) {
        try {
            List<Log> logList = getLogList();
            File file = new File(path, "log.txt");
            FileWriter writer = new FileWriter(file);
            for (Log log : logList) {
                writer.write(log.getContent() + "\n");
            }
            writer.close();
            log.info("Export log to " + file.getAbsolutePath());
        } catch (IOException e) {
            log.error("Export log failed", e);
        }
    }
}
