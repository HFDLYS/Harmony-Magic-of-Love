package com.hfdlys.harmony.magicoflove.view;

import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ServerFrame extends JFrame {
    /**
     * 初始窗口大小
     * 高度
     */
    private final int WINDOW_X = 800;

    /**
     * 初始窗口大小
     * 宽度
     */
    private final int WINDOW_Y = 1000;

    private JTextArea textArea;
    
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
        textArea = new JTextArea(10, 50);
    }

    public void launchFrame() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        

        mainPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        add(mainPanel);

        pack();
        setVisible(true);
        
    }

    public void appendText(String text) {
        textArea.append(text);
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
