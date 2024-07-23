package com.hfdlys.harmony.magicoflove.manager;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.*;

public class MusicManager {
    private static MusicManager musicManager = null;
    private MusicThread currentMusicThread;

    private MusicManager() {
    }

    public static MusicManager getInstance() {
        if (musicManager == null) {
            musicManager = new MusicManager();
        }
        return musicManager;
    }

    public void playMusic(InputStream inputStream) {
        stopMusic();
        currentMusicThread = new MusicThread(inputStream);
        currentMusicThread.start();
    }

    public void stopMusic() {
        if (currentMusicThread != null) {
            currentMusicThread.interrupt();
            currentMusicThread = null;
        }
    }

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