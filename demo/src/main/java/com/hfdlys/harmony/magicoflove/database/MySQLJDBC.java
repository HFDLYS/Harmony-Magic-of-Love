package com.hfdlys.harmony.magicoflove.database;

import java.io.InputStream;
import java.sql.Blob;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.BlobTypeHandler;

import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.hfdlys.harmony.magicoflove.database.mapper.LogMapper;
import com.hfdlys.harmony.magicoflove.database.mapper.UserMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jiasheng Wang
 * @date 2024/7/17
 */
@Slf4j
public class MySQLJDBC {

    /*
     * 单例模式
     */
    private static MySQLJDBC instance = null;

    /**
     * 获取MySQLJDBC实例
     * @return MySQLJDBC实例
     */
    public static MySQLJDBC getInstance() {
        if (instance == null) {
            instance = new MySQLJDBC();
        }
        return instance;
    }

    private SqlSession session = null;

    private MySQLJDBC() {
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream("mysql-config.xml");
        } catch (Exception e) {
            log.error("Failed to load mysql-config.xml", e);
        }

        SqlSessionFactory sessionFactory = new MybatisSqlSessionFactoryBuilder().build(inputStream);
        sessionFactory.getConfiguration().addMapper(UserMapper.class);
        sessionFactory.getConfiguration().addMapper(LogMapper.class);
        sessionFactory.getConfiguration().getTypeHandlerRegistry().register(byte[].class, BlobTypeHandler.class);
        session = sessionFactory.openSession(true);
        log.info("MyBatis session opened.");
    }

    public void reload() {
        session.clearCache();
        log.info("MyBatis session reloaded.");
    }

    public void close() {
        session.close();
        log.info("MyBatis session closed.");
    }

    public SqlSession getSqlSession() {
        if (session.getConnection() == null) {
            log.error("MyBatis session is closed.");
            return null;
        }
        return session;
    }
}
