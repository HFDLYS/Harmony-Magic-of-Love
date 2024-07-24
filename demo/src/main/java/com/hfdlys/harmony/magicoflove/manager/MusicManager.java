package com.hfdlys.harmony.magicoflove.manager;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.*;

/**
 * 音乐管理器
 * @author Jiasheng Wang
 * @since 2024-07-23
 */
public class MusicManager {
    
    /**
     * 单例模式
     */
    private static MusicManager musicManager = null;

    /**
     * 当前播放的音乐线程
     */
    private MusicThread currentMusicThread;

    private MusicManager() {
    }

    public static MusicManager getInstance() {
        if (musicManager == null) {
            musicManager = new MusicManager();
        }
        return musicManager;
    }

    /**
     * 播放音乐
     * @param inputStream 音乐输入流
     */
    public void playMusic(InputStream inputStream) {
        stopMusic();
        currentMusicThread = new MusicThread(inputStream);
        currentMusicThread.start();
    }

    /**
     * 停止音乐
     */
    public void stopMusic() {
        if (currentMusicThread != null) {
            currentMusicThread.interrupt();
            currentMusicThread = null;
        }
    }

    /**
     * 音乐线程
     */
    private class MusicThread extends Thread {
        private InputStream inputStream;
        private AdvancedPlayer player;

        public MusicThread(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            try {
                player = new AdvancedPlayer(inputStream);
                player.play();
            } catch (JavaLayerException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void interrupt() {
            super.interrupt();
            if (player != null) {
                player.close();
            }
        }
    }
}