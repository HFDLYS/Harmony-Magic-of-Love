package com.hfdlys.harmony.magicoflove.game.controller;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import com.hfdlys.harmony.magicoflove.view.GameFrame;
import com.hfdlys.harmony.magicoflove.game.entity.Character;

/**
 * <p>客户端控制器</p>
 * <p>用于处理客户端的控制逻辑</p>
 * @author Jiasheng Wang
 * @since 2024-07-18
 */
public class ClientController extends Controller {
    private boolean firstPlay = true;
    private boolean[] keyboard = new boolean[26];
    public final int[][] directTable = new int[][] {
        {4, 5, 6,},
        {3, 0, 7,},
        {2, 1, 8,},
    };
    @Override
    public void control(Character character) {
        if(firstPlay) {
            init();
            firstPlay = false;
        }
        //System.out.println("W:" + pressed['W'] + ", S:" + pressed['S'] + ", A:" + pressed['A'] + ", D:" + pressed['D']);
        int x, y;
        if(keyboard[0] && !keyboard['D'-'A']) y = 0;
        else if(!keyboard[0] && keyboard['D'-'A']) y = 2;
        else y = 1;
        if(keyboard['W'-'A'] && !keyboard['S'-'A']) x = 0;
        else if(!keyboard['W'-'A'] && keyboard['S'-'A']) x = 2;
        else x = 1;

        // getControl().setAttack(keyboard['Z'-'A']);

        getControl().setMoveDirect(directTable[x][y]);

        Point aimPoint = new Point(MouseInfo.getPointerInfo().getLocation());
        SwingUtilities.convertPointFromScreen(aimPoint, GameFrame.getInstance());
        // BE CAREFUL!! 这里进行了坐标系变换 + swap(x, y)
        getControl().setAimX((int) ((aimPoint.y - GameFrame.getInstance().getHeight() / 2) / GameFrame.getInstance().getScale()));
        getControl().setAimY((int) ((aimPoint.x - GameFrame.getInstance().getWidth() / 2) / GameFrame.getInstance().getScale()));
        
        // play
        if(character == null) return;
        character.move(getControl().getMoveDirect());
        character.aim(getControl().getAimX(), getControl().getAimY());
        
        if(getControl().isAttack()) {
            character.attack();
        }
    }
    private void init() {
        //System.out.println("init controller.");
        GameFrame.getInstance().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(KeyEvent.VK_A <= e.getKeyCode() && e.getKeyCode() <= KeyEvent.VK_Z) {
                    keyboard[e.getKeyCode() - 'A'] = true;
                }
                //System.out.println("Pressed: " + (char)(e.getKeyCode()));
            }
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if(KeyEvent.VK_A <= e.getKeyCode() && e.getKeyCode() <= KeyEvent.VK_Z) {
                    keyboard[e.getKeyCode() - 'A'] = false;
                }
            }
        });
        GameFrame.getInstance().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                if(e.getButton() == MouseEvent.BUTTON1) {
                    getControl().setAttack(true);
                } else if(e.getButton() == MouseEvent.BUTTON3) {
                    getControl().setRush(true);
                }
            }
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if(e.getButton() == MouseEvent.BUTTON1) {
                    getControl().setAttack(false);
                } else if(e.getButton() == MouseEvent.BUTTON3) {
                    getControl().setRush(false);
                }
            }
        });
    }

}
