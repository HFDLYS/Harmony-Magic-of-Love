package com.hfdlys.harmony.magicoflove.manager;



import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hfdlys.harmony.magicoflove.Client;
import com.hfdlys.harmony.magicoflove.Server;
import com.hfdlys.harmony.magicoflove.constant.MessageCodeConstants;
import com.hfdlys.harmony.magicoflove.game.common.Texture;
import com.hfdlys.harmony.magicoflove.game.entity.EntityManager;
import com.hfdlys.harmony.magicoflove.game.factory.MapFactory;
import com.hfdlys.harmony.magicoflove.network.message.Message;
import com.hfdlys.harmony.magicoflove.view.GameFrame;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>游戏管理器</p>
 * @author Jiasheng
 * @since 2024-07-18
 */
@Data
@Slf4j
public class GameManager {

    /**
     * 用户id
     */
    private int userId;

    /**
     * 游戏时间戳（游戏帧的编号）
     */
    private int timeStamp;

    /**
     * 游戏帧数（存在误差）
     */
    private final int fps = 120;

    /**
     * 玩家皮肤
     */
    private HashMap<Integer, Texture> playerSkin;

    /**
     * 实体管理器
     */
    private EntityManager entityManager;

    /**
     * 是否锁帧数
     */
    private final boolean fixedFps = true;

    /**
     * 游戏帧数校准
     */
    private final int delta_bias = 0;

    /**
     * 运行平台是否是客户端
     */
    private boolean isClient;

    /**
     * 构造方法
     */
    public GameManager() {
        timeStamp = 0;
        playerSkin = new HashMap<>();
        entityManager = new EntityManager(this);
    }

    /**
     * 设置用户id
     * @return 用户id
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * 获取用户id
     * @return 用户id
     */
    public int getUserId() {
        return userId;
    }

    /**
     * 获取时间戳
     * @return 时间戳
     */
    public int getTimeStamp() {
        return timeStamp;
    }

    /**
     * 获取游戏帧数
     * @return 游戏帧数
     */
    public int getFps() {
        return fps;
    }

    /**
     * 平台是否是客户端
     */
    public boolean isClient() {
        return isClient;
    }

    /**
     * 初始化（客户端）
     */
    private void init() {
        // audio
        try {
            AudioSystem.getClip();
        } catch (LineUnavailableException e) {
            e.getStackTrace();
        }
    }

    /**
     * 游戏主循环（客户端）
     */
    public void run() {

        init();
        GameFrame.getInstance().init();
        long lastTime = -1;
        long lastSecondTime = -1;
        int cnt = 0;
        while(true) {
            if(lastTime == -1) {
                lastTime = System.currentTimeMillis();
                lastSecondTime = System.currentTimeMillis();
            }
            // 时间戳+1
            timeStamp++;

            // 调用其他管理器
            // EntityManager.getInstance().run();
            GameFrame.getInstance().renderMenu();
            
            // 帧数限制
            long time = System.currentTimeMillis();
            if(fixedFps) {
                lastTime += 1000 / fps;
                long delta = lastTime - time;
                if(delta <= delta_bias) {
                    if(delta < delta_bias) {

                    }
                } else {
                    try {
                        Thread.sleep(delta - delta_bias);
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }
                }
            }
            cnt++;
            if(System.currentTimeMillis() - lastSecondTime >= 1000) {
                log.info("FPS: " + cnt);
                lastSecondTime = System.currentTimeMillis();
                cnt = 0;
            }
        }
    }

    public void runServer() {
        MapFactory.createMap(this);

        long lastTime = -1;
        long lastSecondTime = -1;
        int cnt = 0;

        while(true) {
            if(lastTime == -1) {
                lastTime = System.currentTimeMillis();
                lastSecondTime = System.currentTimeMillis();
            }
            // 时间戳+1
            timeStamp++;

            // 调用其他管理器
            entityManager.run();
            Server.getInstance().broadcast(MessageCodeConstants.ENTITY_MANAGER_INFO, entityManager.getEntityManagerMessage());

            // 帧数限制
            long time = System.currentTimeMillis();
            if(fixedFps) {
                lastTime += 1000 / fps;
                long delta = lastTime - time;
                if(delta <= delta_bias) {
                    if(delta < delta_bias) {
                        
                    }
                } else {
                    try {
                        Thread.sleep(delta - delta_bias);
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }
                }
            }
            cnt++;
            if(System.currentTimeMillis() - lastSecondTime >= 1000) {
                lastSecondTime = System.currentTimeMillis();
                cnt = 0;
            }
        }
    }

    /**
     * 添加玩家皮肤
     * @param id 玩家id
     * @param data 图片数据
     */
    public void addPlayerSkin(int id, byte[] data) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));
        Texture texture = new Texture(image, 1344, 832, 32, 32);
        playerSkin.put(id, texture);
    }

    public Texture getPlayerSkin(int id) {
        return playerSkin.get(id);
    }

    /**
     * 设置平台属性
     * @param isClient 是否是客户端
     */
    public void setIsClient(boolean isClient) {
        this.isClient = isClient;
    }


    public static void main(String[] args) {
        
    }
}
