package com.hfdlys.harmony.magicoflove.view;


import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Comparator;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.hfdlys.harmony.magicoflove.Client;
import com.hfdlys.harmony.magicoflove.constant.MessageCodeConstants;
import com.hfdlys.harmony.magicoflove.game.common.Animation;
import com.hfdlys.harmony.magicoflove.game.common.Hitbox;
import com.hfdlys.harmony.magicoflove.game.common.Texture;
import com.hfdlys.harmony.magicoflove.game.controller.ClientController;
import com.hfdlys.harmony.magicoflove.game.controller.Controller;
import com.hfdlys.harmony.magicoflove.game.entity.Character;
import com.hfdlys.harmony.magicoflove.game.entity.Entity;
import com.hfdlys.harmony.magicoflove.game.entity.EntityManager;
import com.hfdlys.harmony.magicoflove.game.factory.CharacterFactory;
import com.hfdlys.harmony.magicoflove.game.factory.ObstacleFactory;
import com.hfdlys.harmony.magicoflove.game.factory.WeaponFactory;
import com.hfdlys.harmony.magicoflove.manager.GameManager;
import com.hfdlys.harmony.magicoflove.network.message.LoginMessage;
import com.hfdlys.harmony.magicoflove.network.message.RegisterMessage;
import com.hfdlys.harmony.magicoflove.network.message.EntityRegister.ObstacleRegisterMessage;
import com.hfdlys.harmony.magicoflove.util.ImageUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;


/**
 * <p>游戏窗口</p>
 * @author Jiasheng Wang
 * @since 2024-07-18
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class GameFrame extends JFrame {
    /**
     * 初始窗口大小
     * 高度
     */
    private final int WINDOW_Y = 900;

    /**
     * 初始窗口大小
     * 宽度
     */
    private final int WINDOW_X = 1200;

    /**
     * 缩放
     */
    private float scale = 1.0f;
    
    private static GameFrame instance = null;

    File selectedFile = null;

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
        
        /*
        EntityManager entityManager = EntityManager.getInstance();
        entityManager.restart();

        Character c = CharacterFactory.getTestCharacter(1, 0, 0, 100, WeaponFactory.CHAOS_STAVES);
        entityManager.addWithoutMessage(c);
        entityManager.addWithoutMessage(CharacterFactory.getTestCharacter(2, +200, 50, 100, 0));
        EntityManager.getInstance().add(ObstacleFactory.getObstacle(ObstacleFactory.AIR_WALL, -450, 0, 25, 600), new ObstacleRegisterMessage(ObstacleFactory.AIR_WALL));
        EntityManager.getInstance().add(ObstacleFactory.getObstacle(ObstacleFactory.AIR_WALL, 450, 0, 25, 600), new ObstacleRegisterMessage(ObstacleFactory.AIR_WALL));
        EntityManager.getInstance().add(ObstacleFactory.getObstacle(ObstacleFactory.AIR_WALL, 0, -600, 450, 25), new ObstacleRegisterMessage(ObstacleFactory.AIR_WALL));
        EntityManager.getInstance().add(ObstacleFactory.getObstacle(ObstacleFactory.AIR_WALL, 0, 600, 450, 25), new ObstacleRegisterMessage(ObstacleFactory.AIR_WALL));
        */
    }

    /**
     * 初始化渲染管理器
     */
    public void init() {
        setLayout(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setFocusable(true);
        setResizable(false);
        setVisible(true);
        requestFocusInWindow();
        requestFocus();
        setMenuState(1);
        Client.getInstance().setController(new ClientController(this));
    }

    private boolean isRendered;

    private void reset() {
        isRendered = false;
        getContentPane().removeAll();
        revalidate();
        repaint();
    }

    public void setMenuState(int menuState) {
        this.menuState = menuState;
        reset();
    }

    /**
     * 菜单状态
     * 1 登录
     * 2 注册
     * 3 加载中
     * 5 大厅(创建、加入房间)
     */
    private int menuState;

    /**
     * 
     */
    public void renderMenu() {
        if (menuState == 1) {
            launchLoginFrame();
        } else if (menuState == 2) {
            launchRegisterFrame();
        } else if (menuState == 3) {
            launchLoadingFrame();
        } else if (menuState == 5) {
            Client.getInstance().sendMessage(MessageCodeConstants.CONTROL, Client.getInstance().getControlMessage());
            renderGame();
        }
    }

    /**
     * 渲染游戏画面
     */
    public void renderGame() {
        if (Client.getInstance().getUserId() == null) {
            return;
        }

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

    void launchLoginFrame() {
        if (isRendered) {
            return;
        }

        isRendered = true;
        setTitle("登录");
        JPanel contentPane = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // 创建用户名标签和文本框
        JLabel usernameLabel = new JLabel("用户名：");
        JTextField usernameField = new JTextField(20);
        
        // 设置用户名标签的布局
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.LINE_END;
        contentPane.add(usernameLabel, gbc);
        
        // 设置用户名文本框的布局
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        contentPane.add(usernameField, gbc);

        // 创建密码标签和密码框
        JLabel passwordLabel = new JLabel("密码：");
        JPasswordField passwordField = new JPasswordField(20);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        contentPane.add(passwordLabel, gbc);
        
        // 设置密码框的布局
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        contentPane.add(passwordField, gbc);

        JButton loginButton = new JButton("登录");
        JButton registerButton = new JButton("注册");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "用户名和密码不能为空");
                    return;
                }
                try {
                    LoginMessage loginMessage = new LoginMessage();
                    loginMessage.setUsername(username);
                    loginMessage.setPassword(password);
                    Client.getInstance().sendMessage(MessageCodeConstants.LOGIN, loginMessage);
                    setMenuState(3);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        registerButton.addActionListener(e -> setMenuState(2));
        
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPane.add(loginButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPane.add(registerButton, gbc);

        setContentPane(contentPane);
        revalidate();
        repaint();
    }

    void launchRegisterFrame() {
        if (isRendered) {
            return;
        }

        isRendered = true;
        setTitle("注册");
        JPanel contentPane = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        setContentPane(contentPane);

        // 创建用户名标签和文本框
        JLabel usernameLabel = new JLabel("用户名：");
        JTextField usernameField = new JTextField(20);
        
        // 设置用户名标签的布局
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.LINE_END;
        contentPane.add(usernameLabel, gbc);
        
        // 设置用户名文本框的布局
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        contentPane.add(usernameField, gbc);

        // 创建密码标签和密码框
        JLabel passwordLabel = new JLabel("密码：");
        JPasswordField passwordField = new JPasswordField(20);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        contentPane.add(passwordLabel, gbc);
        
        // 设置密码框的布局
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        contentPane.add(passwordField, gbc);

        JLabel chooseFileLabel = new JLabel("皮肤文件:");

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        contentPane.add(chooseFileLabel, gbc);

        JButton chooseFileButton = new JButton("选择皮肤文件");
        
        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    selectedFile = fileChooser.getSelectedFile();
                    if (ImageUtil.isValidImageFile(selectedFile)) {
                        chooseFileButton.setText(selectedFile.getName());
                    } else {
                        JOptionPane.showMessageDialog(null, "请选择一个832 x 1344的PNG文件");
                    }
                }
            }
        });

    
        JButton registerButton = new JButton("注册");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "用户名和密码不能为空");
                    return;
                }
                if (selectedFile == null) {
                    JOptionPane.showMessageDialog(null, "请选择一个皮肤文件");
                    return;
                }
                try (InputStream in = new FileInputStream(selectedFile)) {
                    byte[] imageBytes = in.readAllBytes();
                    RegisterMessage registerMessage = new RegisterMessage(username, password, imageBytes);
                    Client.getInstance().sendMessage(MessageCodeConstants.REGISTER, registerMessage);
                    setMenuState(3);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        contentPane.add(chooseFileButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPane.add(registerButton, gbc);

        setContentPane(contentPane);
        revalidate();
        repaint();
    }

    void launchLoadingFrame() {
        if (isRendered) {
            return;
        }

        isRendered = true;
        remove(getContentPane());
        repaint();
        setTitle("加载中");
        JPanel contentPane = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        setContentPane(contentPane);

        JLabel loadingLabel = new JLabel("加载中...");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPane.add(loadingLabel, gbc);

        setContentPane(contentPane);
        revalidate();
        repaint();
    }

    /**
     * 获取缩放比例
     */
    public float getScale() {
        return scale;
    }
}
