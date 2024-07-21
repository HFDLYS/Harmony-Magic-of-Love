package com.hfdlys.harmony.magicoflove.view;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;

public class ClientFrame extends JFrame {

    private JPanel contentPane;
    private JTextField textField;
    private JPasswordField passwordField;

    public ClientFrame() {
        repaint();
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        
    }
    

    void launchFrame() {
        setVisible(true);
    }

    void launchLoginFrame() {
        repaint();
        setTitle("登录");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel NameLabel = new JLabel("用户名：");
        NameLabel.setBounds(100, 50, 54, 15);
        contentPane.add(NameLabel);

        JLabel PasswordLabel = new JLabel("密码：");
        PasswordLabel.setBounds(100, 100, 54, 15);
        contentPane.add(PasswordLabel);

        textField = new JTextField();
        textField.setBounds(200, 50, 100, 21);
        contentPane.add(textField);
        textField.setColumns(10);

        passwordField = new JPasswordField();
        passwordField.setBounds(200, 100, 100, 21);
        contentPane.add(passwordField);

        JButton LoginButton = new JButton("登录");
        LoginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = textField.getText();
                String password = new String(passwordField.getPassword());
                if (username.equals("admin") && password.equals("admin")) {
                    JOptionPane.showMessageDialog(null, "登录成功！");
                } else {
                    JOptionPane.showMessageDialog(null, "登录失败！");
                }
            }
        });

        JButton JRegisterButton = new JButton("注册");
        JRegisterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
            }
        });
        LoginButton.setBounds(150, 150, 93, 23);
        contentPane.add(LoginButton);
    }
}
