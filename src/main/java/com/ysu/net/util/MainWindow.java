package com.ysu.net.util;

import javax.swing.*;
import java.awt.*;

public class MainWindow {
    public void draw() {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        // 创建窗体
        JFrame frame = new JFrame("Swing Form Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(400, 300));

        // 创建文本框
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);

        // 创建按钮
        JButton button1 = new JButton("Button 1");
        JButton button2 = new JButton("Button 2");
        JButton button3 = new JButton("Button 3");

        // 创建右侧面板，使用 BoxLayout 进行垂直排列
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(button1);
        buttonPanel.add(button2);
        buttonPanel.add(button3);

        // 创建主面板，使用 BorderLayout 进行布局
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.EAST);

        // 将主面板添加到窗体中
        frame.getContentPane().add(mainPanel);

        // 显示窗体
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
