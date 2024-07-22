package com.hfdlys.harmony.magicoflove.game.controller;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import com.hfdlys.harmony.magicoflove.view.GameFrame;
import com.hfdlys.harmony.magicoflove.game.entity.Character;

/**
 * <p>客户端控制器</p>
 * <p>用于处理客户端的控制逻辑</p>
 * @author Jiasheng Wang
 * @since 2024-07-18
 */
public class ClientController extends Controller {
    public ClientController(JFrame frame) {
        init(frame);
    }

    private boolean[] keyboard = new boolean[26];
    public final int[][] directTable = new int[][] {
        {4, 5, 6,},
        {3, 0, 7,},
        {2, 1, 8,},
    };
    @Override
    public void control(Character character) {
        //System.out.println("W:" + pressed['W'] + ", S:" + pressed['S'] + ", A:" + pressed['A'] + ", D:" + pressed['D']);
        int x, y;
        if(keyboard[0] && !keyboard['D'-'A']) y = 0;
        else if(!keyboard[0] && keyboard['D'-'A']) y = 2;
        else y = 1;
        if(keyboard['W'-'A'] && !keyboard['S'-'A']) x = 0;
        else if(!keyboard['W'-'A'] && keyboard['S'-'A']) x = 2;
        else x = 1;

        getControlMessage().setMoveDirect(directTable[x][y]);

        Point aimPoint = new Point(MouseInfo.getPointerInfo().getLocation());
        SwingUtilities.convertPointFromScreen(aimPoint, GameFrame.getInstance());
        getControlMessage().setAimX((int) ((aimPoint.y - GameFrame.getInstance().getHeight() / 2) / GameFrame.getInstance().getScale()));
        getControlMessage().setAimY((int) ((aimPoint.x - GameFrame.getInstance().getWidth() / 2) / GameFrame.getInstance().getScale()));
        
        if(character == null) return;
        character.move(getControlMessage().getMoveDirect());
        character.aim(getControlMessage().getAimX(), getControlMessage().getAimY());
        
        if(getControlMessage().isAttack()) {
            character.attack();
        }
    }
    private void init(JFrame frame) {
        /*
        GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent e) {
                if(NativeKeyEvent.VC_A <= e.getKeyCode() && e.getKeyCode() <= NativeKeyEvent.VC_Z) {
                    keyboard[e.getKeyCode() - NativeKeyEvent.VC_A] = true;
                    System.out.println("Key " + (char)e.getKeyCode() + " pressed");
                }
            }
            @Override
            public void nativeKeyReleased(NativeKeyEvent e) {
                if(NativeKeyEvent.VC_A <= e.getKeyCode() && e.getKeyCode() <= NativeKeyEvent.VC_Z) {
                    keyboard[e.getKeyCode() - NativeKeyEvent.VC_A] = false;
                }
            }
            @Override
            public void nativeKeyTyped(NativeKeyEvent e) {
            }
        });
        */
        
        frame.setVisible(true);
        frame.setFocusable(true);
        frame.requestFocusInWindow();
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(KeyEvent.VK_A <= e.getKeyCode() && e.getKeyCode() <= KeyEvent.VK_Z) {
                    keyboard[e.getKeyCode() - 'A'] = true;
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if(KeyEvent.VK_A <= e.getKeyCode() && e.getKeyCode() <= KeyEvent.VK_Z) {
                    keyboard[e.getKeyCode() - 'A'] = false;
                }
            }
        });
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                if(e.getButton() == MouseEvent.BUTTON1) {
                    getControlMessage().setAttack(true);
                } else if(e.getButton() == MouseEvent.BUTTON3) {
                    getControlMessage().setRush(true);
                }
            }
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if(e.getButton() == MouseEvent.BUTTON1) {
                    getControlMessage().setAttack(false);
                } else if(e.getButton() == MouseEvent.BUTTON3) {
                    getControlMessage().setRush(false);
                }
            }
        });
        
        
    }

}
