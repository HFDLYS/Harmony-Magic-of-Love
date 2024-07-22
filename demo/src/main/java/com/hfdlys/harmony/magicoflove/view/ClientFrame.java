package com.hfdlys.harmony.magicoflove.view;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Blob;

import javax.swing.*;
import javax.swing.border.Border;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import com.hfdlys.harmony.magicoflove.Client;
import com.hfdlys.harmony.magicoflove.constant.MessageCodeConstants;
import com.hfdlys.harmony.magicoflove.network.message.Messages.*;
import com.hfdlys.harmony.magicoflove.util.ImageUtil;

public class ClientFrame extends JFrame {

    private JPanel contentPane;
    private JTextField textField;
    private JPasswordField passwordField;
    private ObjectMapper objectMapper;

    File selectedFile = null;

    private static ClientFrame instance = null;

    private LoadingDialog dialog;

    private ClientFrame() {
        repaint();
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        objectMapper = new ObjectMapper();
        dialog = new LoadingDialog(this);
        setSize(800, 600);
    }

    public static ClientFrame getInstance() {
        if (instance == null) {
            instance = new ClientFrame();
        }
        return instance;
    }
    
    public LoadingDialog getDialog() {
        return dialog;
    }

    public void launchFrame() {
        if (Client.getInstance().getUserId() == null || Client.getInstance().getUserId() == -1) {
            launchLoginFrame();
        } else {
            
        }
    }

    void launchLoginFrame() {
        
        setTitle("登录");
        contentPane = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        setSize(400, 300);
        add(contentPane);

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
                    LoginMessage loginMessage = LoginMessage.newBuilder()
                            .setUsername(username)
                            .setPassword(password)
                            .build();
                    Message message = Message.newBuilder()
                            .setCode(MessageCodeConstants.LOGIN)
                            .setLoginMessage(loginMessage)
                            .build();
                    Client.getInstance().sendMessage(message);
                    dialog.initUI();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        registerButton.addActionListener(e -> launchRegisterFrame());
        
        
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
        setLocationRelativeTo(null);
        setVisible(true);
    }

    void launchRegisterFrame() {
        repaint();
        setTitle("注册");
        contentPane = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        setSize(400, 300);
        add(contentPane);

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
                    RegisterMessage registerMessage = RegisterMessage.newBuilder()
                            .setUsername(username)
                            .setPassword(password)
                            .setSkin(ByteString.copyFrom(imageBytes))
                            .build();
                    Message message = Message.newBuilder()
                            .setCode(MessageCodeConstants.REGISTER)
                            .setRegisterMessage(registerMessage)
                            .build();
                    Client.getInstance().sendMessage(message);
                    dialog.initUI();
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
        // 显示框架
        setLocationRelativeTo(null);
        setVisible(true);
    }

    
    
    public static void main(String[] args) {
        ClientFrame.getInstance().launchFrame();
    }
}
