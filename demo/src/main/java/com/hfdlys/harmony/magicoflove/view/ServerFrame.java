package com.hfdlys.harmony.magicoflove.view;

import java.net.*;
import javax.swing.*;

import com.hfdlys.harmony.magicoflove.database.service.LogService;

import java.awt.*;
import java.awt.event.*;

public class ServerFrame extends JFrame {
    /**
     * 初始窗口大小
     * 高度
     */
    private final int WINDOW_X = 2000;

    /**
     * 初始窗口大小
     * 宽度
     */
    private final int WINDOW_Y = 1000;

    private JList<String> logList;
    
    private static ServerFrame instance = null;

    /**
     * <p>获取实例</p>
     * @return GameFrame
     */
    public static ServerFrame getInstance() {
        if (instance == null) {
            instance = new ServerFrame();
        }
        return instance;
    }
    
    /**
     * <p>构造函数</p>
     */
    private ServerFrame() {
        setTitle("Server");
        setSize(WINDOW_X, WINDOW_Y);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        addWindowListener(new WindowCloseListener());

        logList = new JList<>();
        logList.setListData(LogService.getInstance().getLogList().stream().map(log -> log.getContent()).toArray(String[]::new));
    }

    public void launchFrame() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());        

        mainPanel.add(new JScrollPane(logList), BorderLayout.CENTER);
        setContentPane(mainPanel);

        JPanel operatePanel = new JPanel();
        operatePanel.setLayout(new FlowLayout());
        JButton clearButton = new JButton("导出");
        clearButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                String path = fileChooser.getSelectedFile().getAbsolutePath();
                LogService.getInstance().exportLog(path);
            }
        });

        operatePanel.add(clearButton);

        mainPanel.add(operatePanel, BorderLayout.WEST);

        pack();
        setVisible(true);
        
    }

    public void appendText(String text) {
        if (text.endsWith("\n")) {
            text = text.substring(0, text.length() - 1);
        }
        LogService.getInstance().insertLog(text);
        logList.setListData(LogService.getInstance().getLogList().stream().map(log -> log.getContent()).toArray(String[]::new));
    }

    private class WindowCloseListener implements WindowListener {

        @Override
        public void windowOpened(WindowEvent e) {
            
        }

        @Override
        public void windowClosing(WindowEvent e) {

            System.exit(0);
        }

        @Override
        public void windowClosed(WindowEvent e) {
            
        }

        @Override
        public void windowIconified(WindowEvent e) {
            
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
            
        }

        @Override
        public void windowActivated(WindowEvent e) {
            
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
            
        }
        
    }
}
