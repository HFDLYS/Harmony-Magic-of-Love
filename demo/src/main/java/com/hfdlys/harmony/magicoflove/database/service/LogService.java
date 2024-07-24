package com.hfdlys.harmony.magicoflove.database.service;

import com.hfdlys.harmony.magicoflove.database.MySQLJDBC;
import com.hfdlys.harmony.magicoflove.database.entity.Log;
import com.hfdlys.harmony.magicoflove.database.mapper.LogMapper;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

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
        try (Workbook workbook = new HSSFWorkbook()) {
            List<Log> logList = getLogList();
            

            Sheet sheet = workbook.createSheet("Log");

            sheet.createRow(0).createCell(0).setCellValue("Id");
            sheet.getRow(0).createCell(1).setCellValue("Content");
            sheet.getRow(0).createCell(2).setCellValue("CreateTime");

            for (int i = 0; i < logList.size(); i++) {
                Log log = logList.get(i);
                sheet.createRow(i + 1).createCell(0).setCellValue(log.getId());
                sheet.getRow(i + 1).createCell(1).setCellValue(log.getContent());
                sheet.getRow(i + 1).createCell(2).setCellValue(log.getCreateTime().toString());
            }

            for (int i = 0; i < 2; i++) {
                sheet.autoSizeColumn(i);
            }
            path = path + "/log.xls";

            FileOutputStream fileOut = new FileOutputStream(path);

            workbook.write(fileOut);

        } catch (IOException e) {
            log.error("Export log failed", e);
        }
    }
}
