package com.ysu.net;

import com.ysu.net.handler.ThreadManager;
import com.ysu.net.util.FrontDrawer;

public class Main {

    public static final int user_port = 8080;   // 患者端的接收端口

    public static final int doctor_port = 8081;   // 医生端的接收端口

    public static void main(String[] args) throws InterruptedException {
        // 创建两个对象
        ThreadManager threadManager = new ThreadManager();
        FrontDrawer frontDrawer = new FrontDrawer(threadManager);
        // 创建线程对象
        Thread manager_thread = new Thread(threadManager);
        Thread drawer_thread = new Thread(frontDrawer);
        // 开始运行
        manager_thread.start();
        drawer_thread.start();
    }
}
