package com.hfdlys.harmony.magicoflove.view;

import javax.swing.*;
import java.awt.*;

public class LoadingDialog extends JDialog {
    public LoadingDialog(Frame owner) {
        super(owner, "Loading", true); // 创建模态对话框
        setAutoRequestFocus(false);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    }

    public void initUI() {
        getContentPane().removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel label = new JLabel("等待中");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.NORTH);

        // 添加进度条
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        panel.add(progressBar, BorderLayout.CENTER);

 
        getContentPane().add(panel);
        pack(); // 调整对话框大小
        setVisible(true);
        setLocationRelativeTo(getOwner());
    }

    public void finish(String message) {
        getContentPane().removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel label = new JLabel(message);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.NORTH);
        setFocusable(false);
        JButton button = new JButton("确定");

        getContentPane().add(panel);
        getContentPane().add(button, BorderLayout.SOUTH);
        button.addActionListener(e -> close());
        pack(); // 调整对话框大小
        setLocationRelativeTo(getOwner());
    }

    public void close() {
        setVisible(false);
        ClientFrame.getInstance().launchFrame();
        
        dispose();
    }
}