package com.hfdlys.harmony.magicoflove.view;


import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Comparator;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.hfdlys.harmony.magicoflove.game.common.Animation;
import com.hfdlys.harmony.magicoflove.game.common.Hitbox;
import com.hfdlys.harmony.magicoflove.game.common.Texture;
import com.hfdlys.harmony.magicoflove.game.controller.ClientController;
import com.hfdlys.harmony.magicoflove.game.controller.Controller;
import com.hfdlys.harmony.magicoflove.game.entity.Character;
import com.hfdlys.harmony.magicoflove.game.entity.Entity;
import com.hfdlys.harmony.magicoflove.game.entity.EntityManager;
import com.hfdlys.harmony.magicoflove.game.factory.CharacterFactory;
import com.hfdlys.harmony.magicoflove.game.factory.WeaponFactory;
import com.hfdlys.harmony.magicoflove.manager.GameManager;

import lombok.extern.slf4j.Slf4j;


/**
 * <p>游戏窗口</p>
 * @author Jiasheng Wang
 * @since 2024-07-18
 */
@Slf4j
public class GameFrame extends JFrame {
    /**
     * 初始窗口大小
     * 高度
     */
    private final int WINDOW_X = 1200;

    /**
     * 初始窗口大小
     * 宽度
     */
    private final int WINDOW_Y = 900;

    /**
     * 缩放
     */
    private float scale = 1.0f;
    
    private static GameFrame instance = null;

    Character character;
    /**
     * <p>获取实例</p>
     * @return GameFrame
     */
    public static GameFrame getInstance() {
        if (instance == null) {
            instance = new GameFrame();
        }
        return instance;
    }

    /**
     * <p>构造函数</p>
     */
    private GameFrame() {
        super("和弦：爱的魔法");
        this.setSize(WINDOW_X, WINDOW_Y);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        
        EntityManager entityManager = EntityManager.getInstance();
        entityManager.restart();

        Character c = CharacterFactory.getCharacter(1, -200, -500, 100, WeaponFactory.CHAOS_STAVES);
        entityManager.addWithoutMessage(c);
        entityManager.addWithoutMessage(CharacterFactory.getCharacter(2, +200, 50, 100, 0));
    }

    /**
     * 初始化渲染管理器
     */
    public void init() {
        setLayout(null);
        setBounds(300, 0, WINDOW_Y, WINDOW_X);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);


        setVisible(true);
    }
    
    /**
     * 渲染游戏画面
     */
    public void renderGame() {
        Graphics graphics = this.getGraphics();

        // 画布
        Image offScreenImage = this.createImage(getWidth(), getHeight());
        Graphics g = offScreenImage.getGraphics();

        
        EntityManager.getInstance().sort(new Comparator<Entity>() {
            @Override
            public int compare(Entity o1, Entity o2) {
                return o1.getHitbox().getX() - o2.getHitbox().getX();
            }
        });
        

        for(int i = 0; i < EntityManager.getInstance().getEntitySize(); i++) {
            Entity entity = EntityManager.getInstance().getEntity(i);
            Texture texture;
            if(entity instanceof Character) {
                texture = ((Character)entity).getCurrentTexture();
            } else {
                texture = entity.getTexture();
            }
            if(texture == null) continue; // 空气墙等实体没有贴图，直接跳过渲染
            texture.setScale(1);
            g.drawImage(texture.getImage(), (int)(entity.getHitbox().getY()*scale) - texture.getDy() + getWidth() / 2, (int)(entity.getHitbox().getX()*scale) - texture.getDx() + getHeight() / 2, null);
        }

        setBackground(new Color(38, 40, 74));
        graphics.drawImage(offScreenImage, 0, 0, null);
    }

    /**
     * 获取缩放比例
     */
    public float getScale() {
        return scale;
    }
}
