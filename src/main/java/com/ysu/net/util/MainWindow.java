package com.ysu.net.util;

import com.ysu.net.handler.ThreadManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.EventObject;
import java.util.Queue;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MainWindow {

    /**
     * 表格模型
     */
    private DefaultTableModel tableModel;

    /**
     *
     */
    private JTabbedPane jTabbedPane;

    /**
     * 表格数据
     */
    Object[] columnNames = {"接收时间", "楼层", "房间号", "床号", "姓名", "异常信息"};

    /**
     * 监视对象
     */
    private ThreadManager threadManager;

    public MainWindow(ThreadManager threadManager) {
        this.threadManager = threadManager;
        jTabbedPane = new JTabbedPane();
    }

    public void draw() {
        // 运行绘制函数
        SwingUtilities.invokeLater(() -> createAndShowGUI());
        // 创建定时器
        createTimer();
    }



    // 自定义单元格渲染器
    static class DisableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component rendererComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                    row, column);
            // 设置单元格为不可编辑状态
            ((JLabel) rendererComponent).setEnabled(false);
            return rendererComponent;
        }
    }

    // 自定义单元格编辑器
    static class DisableCellEditor extends DefaultCellEditor {
        public DisableCellEditor() {
            super(new JTextField());
        }

        @Override
        public boolean isCellEditable(EventObject e) {
            return false; // 设置单元格为不可编辑状态
        }
    }

    /**
     * 创建主要的界面
     */
    private void createAndShowGUI() {
        // 创建窗体
        JFrame frame = new JFrame("Swing Form Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 设置窗体大小
        frame.setPreferredSize(new Dimension(1200, 900));

        // 禁止改变窗体大小
        frame.setResizable(false);

        // 创建多面板
        JTabbedPane tabbedPane = new JTabbedPane();

        // 创建表格模型
        tableModel = new DefaultTableModel();
        JTable table = new JTable(tableModel);

        table.getTableHeader().setReorderingAllowed(false);

        table.setDefaultRenderer(Object.class, new DisableCellRenderer());
        table.setDefaultEditor(Object.class, new DisableCellEditor());

        // 设置表格字体
        Font tableFont = new Font("Arial", Font.PLAIN, 20);
        table.setFont(tableFont);

        // 设置表格行高
        int rowHeight = 50;
        table.setRowHeight(rowHeight);

        // 设置表格居中对齐
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        // 创建滚动面板，并将表格添加到其中
        JScrollPane scrollPane = new JScrollPane(table);

        // 将滚动面板添加到窗体的内容面板中
        frame.getContentPane().add(scrollPane);

        // 添加滚动面板到主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // 设置表格数据
        tableModel.setColumnIdentifiers(columnNames);

        // 获取列模型
        TableColumnModel columnModel = table.getColumnModel();


        // 设置列的宽度
        columnModel.getColumn(0).setPreferredWidth(300);
        columnModel.getColumn(1).setPreferredWidth(150);
        columnModel.getColumn(2).setPreferredWidth(200);

        // 添加按钮到底部面板
        JPanel buttonPanel = new JPanel();

        // 添加主面板和按钮面板到窗体
        jTabbedPane.addTab("监视主界面", mainPanel);
        frame.add(jTabbedPane);



        // 显示窗体
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * 创建定时监视器，监视病情
     */
    private void createTimer(){
        java.util.Timer timer = new java.util.Timer();
        long delay = 1000; // 延迟1秒
        long period = 2000; // 每隔2秒执行一次

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 在这里编写需要定时执行的函数代码
                // 将对象的值强行转换成两维数组
                System.out.println("查询信息");
                Queue<Object[]> message = new ConcurrentLinkedQueue<>(threadManager.newMessage);
                // 新建一个类
                Object[][] newMessage = new Object[21][];
                // 遍历
                int i = 0;
                for (Object[] item : message) {
                    if (item != null) {
                        newMessage[i] = item;
                        i ++;
                    }
                }
                // 如果没有东西就直接展示一个空行
                while(i < 21) {
                    newMessage[i] = new Object[]{"", "", "", "", "", ""};
                    i++;
                }
                // 更新表格数据
                tableModel.setDataVector(newMessage, columnNames);
            }
        }, delay, period);
    }
}
