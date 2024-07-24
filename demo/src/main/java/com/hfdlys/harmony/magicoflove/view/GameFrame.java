package com.hfdlys.harmony.magicoflove.view;


import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Comparator;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.apache.ibatis.io.Resources;

import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default;
import com.hfdlys.harmony.magicoflove.Client;
import com.hfdlys.harmony.magicoflove.constant.GameViewConstants;
import com.hfdlys.harmony.magicoflove.constant.MessageCodeConstants;
import com.hfdlys.harmony.magicoflove.database.entity.User;
import com.hfdlys.harmony.magicoflove.game.common.Animation;
import com.hfdlys.harmony.magicoflove.game.common.Hitbox;
import com.hfdlys.harmony.magicoflove.game.common.Texture;
import com.hfdlys.harmony.magicoflove.game.controller.ClientController;
import com.hfdlys.harmony.magicoflove.game.controller.Controller;
import com.hfdlys.harmony.magicoflove.game.entity.Character;
import com.hfdlys.harmony.magicoflove.game.entity.Entity;
import com.hfdlys.harmony.magicoflove.game.factory.CharacterFactory;
import com.hfdlys.harmony.magicoflove.game.factory.ObstacleFactory;
import com.hfdlys.harmony.magicoflove.game.factory.WeaponFactory;
import com.hfdlys.harmony.magicoflove.manager.EntityManager;
import com.hfdlys.harmony.magicoflove.manager.GameManager;
import com.hfdlys.harmony.magicoflove.manager.MusicManager;
import com.hfdlys.harmony.magicoflove.network.message.LoginMessage;
import com.hfdlys.harmony.magicoflove.network.message.RegisterMessage;
import com.hfdlys.harmony.magicoflove.network.message.RoomInfoMessage;
import com.hfdlys.harmony.magicoflove.network.message.UserMessgae;
import com.hfdlys.harmony.magicoflove.network.message.EntityRegister.CharacterRegisterMessage;
import com.hfdlys.harmony.magicoflove.network.message.EntityRegister.ObstacleRegisterMessage;
import com.hfdlys.harmony.magicoflove.util.ColorUtil;
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
    private final int WINDOW_Y = 800;

    /**
     * 初始窗口大小
     * 宽度
     */
    private final int WINDOW_X = 1200;

    /**
     * 缩放
     */
    private float scale = 1.0f;


    private Image backgroundImage0;

    private Image backgroundImage1;

    private ImageIcon logoIcon;
    
    private static GameFrame instance = null;

    private EntityManager entityManager;

    private File selectedFile = null;

    /**
     * 房间列表
     */
    private DefaultListModel<RoomInfoMessage> roomListModel;

    /**
     * 房间玩家列表
     */
    private DefaultListModel<UserMessgae> roomPlayerModel;

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
        roomListModel = new DefaultListModel<>();
        roomPlayerModel = new DefaultListModel<>();
        /*
        EntityManager entityManager = entityManager;
        entityManager.restart();

        Character c = CharacterFactory.getTestCharacter(1, 0, 0, 100, WeaponFactory.CHAOS_STAVES);
        entityManager.addWithoutMessage(c);
        entityManager.addWithoutMessage(CharacterFactory.getTestCharacter(2, +200, 50, 100, 0));
        entityManager.add(ObstacleFactory.getObstacle(ObstacleFactory.AIR_WALL, -450, 0, 25, 600), new ObstacleRegisterMessage(ObstacleFactory.AIR_WALL));
        entityManager.add(ObstacleFactory.getObstacle(ObstacleFactory.AIR_WALL, 450, 0, 25, 600), new ObstacleRegisterMessage(ObstacleFactory.AIR_WALL));
        entityManager.add(ObstacleFactory.getObstacle(ObstacleFactory.AIR_WALL, 0, -600, 450, 25), new ObstacleRegisterMessage(ObstacleFactory.AIR_WALL));
        entityManager.add(ObstacleFactory.getObstacle(ObstacleFactory.AIR_WALL, 0, 600, 450, 25), new ObstacleRegisterMessage(ObstacleFactory.AIR_WALL));
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
        try {
            backgroundImage0 = ImageIO.read(Resources.getResourceAsStream("ui/background.png"));
            backgroundImage1 = ImageIO.read(Resources.getResourceAsStream("ui/background1.png"));
            logoIcon = new ImageIcon(ImageIO.read(Resources.getResourceAsStream("ui/logo.png")));
        } catch (IOException e) {
            log.error("Failed to load background image", e);
        }
        setGameState(GameViewConstants.LOGIN_VIEW);
        entityManager = Client.getInstance().getGameManager().getEntityManager();
        Client.getInstance().setController(new ClientController(this));
    }

    private boolean isRendered;

    private void reset() {
        isRendered = false;
        getContentPane().removeAll();
        revalidate();
        repaint();
    }

    public void setGameState(int gameState) {
        this.gameState = gameState;
        reset();
    }

    private int gameState;

    /**
     * 
     */
    public void renderMenu() {
        // 切换界面
        if (gameState == GameViewConstants.LOGIN_VIEW) {
            launchLoginFrame();
        } else if (gameState == GameViewConstants.REGISTER_VIEW) {
            launchRegisterFrame();
        } else if (gameState == GameViewConstants.GAME_VIEW) {
            Client.getInstance().sendMessage(MessageCodeConstants.CONTROL, Client.getInstance().getControlMessage());
            renderGame();
        } else if (gameState == GameViewConstants.LOADING_VIEW) {
            launchLoadingFrame();
        } else if (gameState == GameViewConstants.LOBBY_VIEW) {
            launchLobbyFrame();
        } else if (gameState == GameViewConstants.ROOM_VIEW) {
            launchRoomFrame();
        }
    }

    /**
     * 渲染游戏画面
     */
    public void renderGame() {
        
        if (Client.getInstance().getUserId() == null) {
            return;
        }
        
        if (!isRendered) {
            isRendered = true;
            setTitle("和弦：❤的魔法");
            try {
                MusicManager.getInstance().playMusic(Resources.getResourceAsStream("music/gameStart.mp3"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        requestFocus();
        Graphics graphics = this.getGraphics();
        
        // 画布
        Image offScreenImage = this.createImage(getWidth(), getHeight());
        Graphics g = offScreenImage.getGraphics();
        int x = (getWidth() - backgroundImage1.getWidth(this)) / 2;
        int y = (getHeight() - backgroundImage1.getHeight(this)) / 2;
        g.drawImage(backgroundImage1, x, y, this);
        Client.getInstance().getGameManager().getEntityManager().sort(new Comparator<Entity>() {
            @Override
            public int compare(Entity o1, Entity o2) {
                return o1.getHitbox().getX() - o2.getHitbox().getX();
            }
        });
        

        for(int i = 0; i < entityManager.getEntitySize(); i++) {
            Entity entity = entityManager.getEntity(i);
            Texture texture;
            if(entity instanceof Character) {
                texture = ((Character)entity).getCurrentTexture(Client.getInstance().getGameManager());
            } else {
                texture = entity.getTexture();
            }
            if(texture == null) continue;
            texture.setScale(1);
            
            // g.setColor(Color.BLACK);
            // g.drawLine((int)(entity.getHitbox().getY()*scale) - entity.getHitbox().getLy() + getWidth() / 2, (int)(entity.getHitbox().getX()*scale) - entity.getHitbox().getLx() + getHeight() / 2, (int)(entity.getHitbox().getY()*scale) + entity.getHitbox().getLy() + getWidth() / 2, (int)(entity.getHitbox().getX()*scale) + entity.getHitbox().getLx() + getHeight() / 2);
            // g.drawLine((int)(entity.getHitbox().getY()*scale) + entity.getHitbox().getLy() + getWidth() / 2, (int)(entity.getHitbox().getX()*scale) - entity.getHitbox().getLx() + getHeight() / 2, (int)(entity.getHitbox().getY()*scale) - entity.getHitbox().getLy() + getWidth() / 2, (int)(entity.getHitbox().getX()*scale) + entity.getHitbox().getLx() + getHeight() / 2);
            if(entity instanceof Character) {
                g.drawImage(texture.getImage(), (int)(entity.getHitbox().getY()*scale) - texture.getDy() + getWidth() / 2, (int)(entity.getHitbox().getX()*scale) - texture.getDx() + getHeight() / 2 - 16, null);
                if (((Character)entity).getUsername() == null) {
                    continue;
                }
                int comp = Client.getInstance().getGameManager().getEntityManager().getCamp(entity.getId());
                String compName = (Client.getInstance().getGameManager().getEntityManager().getCampName(comp));
                g.setColor(ColorUtil.convertToColor(compName));
                g.setFont(new Font("宋体", Font.BOLD, 20));
                String name = ((Character)entity).getUsername();
                int hp = ((Character)entity).getHp();
                int maxhp = ((Character)entity).getMaxHp();
                if (maxhp == 0) {
                    maxhp = 400;
                }
                if (hp < 0) {
                    hp = 0;
                }
                
                int dy = name.length() / 2 * 10;
                g.drawString(name, (int)(entity.getHitbox().getY()*scale) - dy + getWidth() / 2, (int)(entity.getHitbox().getX()*scale) - texture.getDx() + getHeight() / 2 - 16);
                g.setColor(Color.GRAY);
                g.fillRect((int)(entity.getHitbox().getY()*scale) - 32 + getWidth() / 2 - 1, (int)(entity.getHitbox().getX()*scale) + texture.getDx() + getHeight() / 2 + 20 - 16, 66, 10);
                g.setColor(Color.RED);
                g.fillRect((int)(entity.getHitbox().getY()*scale) - 32 + getWidth() / 2, (int)(entity.getHitbox().getX()*scale) + texture.getDx() + getHeight() / 2 + 20 + 1 - 16, (int)(64*(maxhp-hp)/maxhp), 8);
            } else {
                g.drawImage(texture.getImage(), (int)(entity.getHitbox().getY()*scale) - texture.getDy() + getWidth() / 2, (int)(entity.getHitbox().getX()*scale) - texture.getDx() + getHeight() / 2, null);
            }
        }
        setBackground(new Color(38, 40, 74));
        graphics.drawImage(offScreenImage, 0, 0, null);
    }

    void launchLoginFrame() {
        if (isRendered) {
            return;
        }

        isRendered = true;

        if (Client.getInstance().getUserId() != null) {
            setGameState(GameViewConstants.LOADING_VIEW);
            Client.getInstance().sendMessage(MessageCodeConstants.ASK_LOBBY_INFO, null);
            return;
        }
        setTitle("登录");
        JPanel contentPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage0 != null) {
                    // Calculate the x and y coordinates to center the image
                    int x = (getWidth() - backgroundImage0.getWidth(this)) / 2;
                    int y = (getHeight() - backgroundImage0.getHeight(this)) / 2;
                    // Draw the image
                    g.drawImage(backgroundImage0, x, y, this);
                }
            }
        };
        JPanel mainPane = new JPanel(new GridBagLayout());
        mainPane.setBackground(new Color(0, 0, 0, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        JLabel logoLabel = new JLabel(logoIcon);
        contentPanel.add(logoLabel, BorderLayout.NORTH);
        

        // 创建用户名标签和文本框
        JLabel usernameLabel = new JLabel("用户名：");
        JTextField usernameField = new JTextField(30);
        
        // 设置用户名标签的布局
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.LINE_END;
        mainPane.add(usernameLabel, gbc);
        
        // 设置用户名文本框的布局
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        mainPane.add(usernameField, gbc);

        // 创建密码标签和密码框
        JLabel passwordLabel = new JLabel("密码：");
        JPasswordField passwordField = new JPasswordField(30);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        mainPane.add(passwordLabel, gbc);
        
        // 设置密码框的布局
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        mainPane.add(passwordField, gbc);

        

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
                    setGameState(GameViewConstants.LOADING_VIEW);;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        registerButton.addActionListener(e -> setGameState(GameViewConstants.REGISTER_VIEW));
        
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPane.add(loginButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPane.add(registerButton, gbc);

        contentPanel.add(mainPane, BorderLayout.CENTER);
        setContentPane(contentPanel);
        revalidate();
        repaint();
    }

    void launchRegisterFrame() {
        if (isRendered) {
            return;
        }

        isRendered = true;
        setTitle("注册");
        JPanel contentPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage0 != null) {
                    // Calculate the x and y coordinates to center the image
                    int x = (getWidth() - backgroundImage0.getWidth(this)) / 2;
                    int y = (getHeight() - backgroundImage0.getHeight(this)) / 2;
                    // Draw the image
                    g.drawImage(backgroundImage0, x, y, this);
                }
            }
        };
        JPanel mainPane = new JPanel(new GridBagLayout());
        mainPane.setBackground(new Color(0, 0, 0, 0));
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel logoLabel = new JLabel(logoIcon);
        contentPanel.add(logoLabel, BorderLayout.NORTH);

        // 创建用户名标签和文本框
        JLabel usernameLabel = new JLabel("用户名：");
        JTextField usernameField = new JTextField(20);
        
        // 设置用户名标签的布局
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.LINE_END;
        mainPane.add(usernameLabel, gbc);
        
        // 设置用户名文本框的布局
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        mainPane.add(usernameField, gbc);

        // 邮箱

        JLabel emailLabel = new JLabel("邮箱：");
        JTextField emailField = new JTextField(20);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        mainPane.add(emailLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        mainPane.add(emailField, gbc);


        // 创建密码标签和密码框
        JLabel passwordLabel = new JLabel("密码：");
        JPasswordField passwordField = new JPasswordField(20);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        mainPane.add(passwordLabel, gbc);
        
        // 设置密码框的布局
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        mainPane.add(passwordField, gbc);

        JLabel chooseFileLabel = new JLabel("皮肤文件:");

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        mainPane.add(chooseFileLabel, gbc);

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
        JButton cancelButton = new JButton("取消");

        cancelButton.addActionListener(e -> setGameState(GameViewConstants.LOGIN_VIEW));
    
        JButton registerButton = new JButton("注册");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String email = emailField.getText();
                if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "用户名和密码不能为空");
                    return;
                }
                if (selectedFile == null) {
                    JOptionPane.showMessageDialog(null, "请选择一个皮肤文件");
                    return;
                }
                try (InputStream in = new FileInputStream(selectedFile)) {
                    byte[] imageBytes = in.readAllBytes();
                    RegisterMessage registerMessage = new RegisterMessage(username, password, email,imageBytes);
                    Client.getInstance().sendMessage(MessageCodeConstants.REGISTER, registerMessage);
                    setGameState(GameViewConstants.LOADING_VIEW);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        mainPane.add(chooseFileButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        mainPane.add(cancelButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.LAST_LINE_END;
        mainPane.add(registerButton, gbc);

        contentPanel.add(mainPane, BorderLayout.CENTER);
        setContentPane(contentPanel);
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

        JPanel contentPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage0 != null) {
                    // Calculate the x and y coordinates to center the image
                    int x = (getWidth() - backgroundImage0.getWidth(this)) / 2;
                    int y = (getHeight() - backgroundImage0.getHeight(this)) / 2;
                    // Draw the image
                    g.drawImage(backgroundImage0, x, y, this);
                }
            }
        };


        JPanel mainPane = new JPanel(new GridBagLayout());
        mainPane.setBackground(new Color(0, 0, 0, 0));

        JLabel logoLabel = new JLabel(logoIcon);
        contentPanel.add(logoLabel, BorderLayout.NORTH);
        GridBagConstraints gbc = new GridBagConstraints();
        setContentPane(mainPane);

        JLabel loadingLabel = new JLabel("加载中...");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPane.add(loadingLabel, gbc);

        contentPanel.add(mainPane, BorderLayout.CENTER);
        setContentPane(contentPanel);
        revalidate();
        repaint();
    }

    void launchLobbyFrame() {
        if (isRendered) {
            return;
        }

        isRendered = true;
        remove(getContentPane());
        repaint();
        JPanel contentPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage0 != null) {
                    // Calculate the x and y coordinates to center the image
                    int x = (getWidth() - backgroundImage0.getWidth(this)) / 2;
                    int y = (getHeight() - backgroundImage0.getHeight(this)) / 2;
                    // Draw the image
                    g.drawImage(backgroundImage0, x, y, this);
                }
            }
        };
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(0, 0, 0, 0));
        setTitle("和弦：❤的魔法 游戏大厅");
        JList<RoomInfoMessage> roomList = new JList<>(roomListModel);
        roomList.setPreferredSize(new Dimension(200, 800));
        JScrollPane roomListScrollPane = new JScrollPane(roomList);
        GridBagConstraints gbc0 = new GridBagConstraints();
        gbc0.gridx = 0;
        gbc0.gridy = 1;
        gbc0.weightx = 1;
        gbc0.weighty = 1;
        gbc0.fill = GridBagConstraints.VERTICAL;

        contentPanel.add(roomListScrollPane, gbc0);
        gbc0.weighty = 0;
        gbc0.gridy = 0;
        gbc0.fill = 0;
        JLabel roomListLabel = new JLabel("房间列表");
        contentPanel.add(roomListLabel, gbc0);
        JButton refreshButton = new JButton("刷新");
        JButton createRoomButton = new JButton("创建房间");
        JButton joinRoomButton = new JButton("加入房间");

        createRoomButton.addActionListener(e -> {
            String roomName = JOptionPane.showInputDialog("请输入房间名");
            if (roomName != null) {
                Client.getInstance().sendMessage(MessageCodeConstants.CREATE_ROOM, roomName);
                setGameState(GameViewConstants.LOADING_VIEW);
            }
        });

        joinRoomButton.addActionListener(e -> {
            RoomInfoMessage roomInfoMessage = roomList.getSelectedValue();
            if (roomInfoMessage != null) {
                Client.getInstance().sendMessage(MessageCodeConstants.JOIN_ROOM, roomInfoMessage.getRoomId());
                setGameState(GameViewConstants.LOADING_VIEW);
            }
        });

        refreshButton.addActionListener(e -> {
            Client.getInstance().sendMessage(MessageCodeConstants.ASK_LOBBY_INFO, null);
            setGameState(GameViewConstants.LOADING_VIEW);
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LAST_LINE_START;
        mainPanel.add(joinRoomButton, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(refreshButton, gbc);
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LAST_LINE_END;
        mainPanel.add(createRoomButton, gbc);

        gbc0.gridy = 2;

        contentPanel.add(mainPanel, gbc0);
        setContentPane(contentPanel);
        revalidate();
        repaint();
    }

    void launchRoomFrame() {
        if (isRendered) {
            return;
        }

        isRendered = true;
        remove(getContentPane());
        JPanel contentPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage0 != null) {
                    // Calculate the x and y coordinates to center the image
                    int x = (getWidth() - backgroundImage0.getWidth(this)) / 2;
                    int y = (getHeight() - backgroundImage0.getHeight(this)) / 2;
                    // Draw the image
                    g.drawImage(backgroundImage0, x, y, this);
                }
            }
        };
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(0, 0, 0, 0));

        setTitle("和弦：❤的魔法 房间");
        JList<UserMessgae> roomPlayerList = new JList<>(roomPlayerModel);
        JScrollPane roomPlayerListScrollPane = new JScrollPane(roomPlayerList);
        GridBagConstraints gbc0 = new GridBagConstraints();
        gbc0.gridx = 0;
        gbc0.gridy = 1;

        contentPanel.add(roomPlayerListScrollPane, gbc0);

        JLabel roomPlayerListLabel = new JLabel("玩家列表");

        gbc0.gridy = 0;

        contentPanel.add(roomPlayerListLabel, gbc0);
        JButton startGameButton = new JButton("开始游戏");
        JButton leaveRoomButton = new JButton("离开房间");

        if (Client.getInstance().getUserId() != null) {
            if (Client.getInstance().getUserId().equals(roomPlayerModel.get(0).getUserId())) {
                startGameButton.setEnabled(true);
            } else {
                startGameButton.setEnabled(false);
            }
        }

        startGameButton.addActionListener(e -> {
            Client.getInstance().sendMessage(MessageCodeConstants.START_GAME, null);
            setGameState(GameViewConstants.LOADING_VIEW);
        });

        leaveRoomButton.addActionListener(e -> {
            Client.getInstance().sendMessage(MessageCodeConstants.EXIT_ROOM, null);
            setGameState(GameViewConstants.LOADING_VIEW);
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LAST_LINE_START;
        mainPanel.add(leaveRoomButton, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(startGameButton, gbc);

        gbc0.gridy = 2;

        contentPanel.add(mainPanel, gbc0);
        setContentPane(contentPanel);
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
