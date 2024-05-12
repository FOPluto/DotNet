package com.ysu.net;

import com.ysu.net.handler.ThreadManager;

public class Main {

    public static final int user_port = 8080;   // 患者端的接收端口

    public static final int doctor_port = 8081;   // 医生端的接收端口

    public static void main(String[] args) throws InterruptedException {
        // 创建两个对象
        ThreadManager threadManager = new ThreadManager();
        Thread _thread = new Thread(threadManager);

        _thread.start();
    }
}
