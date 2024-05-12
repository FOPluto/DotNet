package com.ysu.net.handler;

import com.ysu.net.entity.DoctorInfo;
import com.ysu.net.entity.UserInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class ThreadManager implements Runnable{
    private static final int SERVER_PORT_DOCTOR = 8080;

    private static final int SERVER_PORT_USER = 8081;
    private static final int BUFFER_SIZE = 1024;

    // 医生列表
    Map<Integer, DoctorInfo> doctors;

    // 患者列表
    Map<Integer, UserInfo> users;

    public static void listen(int port, Map infoList) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("服务器已启动，监听端口：" + port);

            while (true) {
                // 监听并接受客户端连接
                Socket clientSocket = serverSocket.accept();
                System.out.println("接收到客户端连接：" + clientSocket.getInetAddress());

                // 创建线程处理客户端连接，对
                Thread clientThread = new Thread(() -> {
                    try {
                        InputStream inputStream = clientSocket.getInputStream();
                        byte[] buffer = new byte[BUFFER_SIZE];
                        int bytesRead;
                        UserInfo userInfo = new UserInfo();
                        DoctorInfo doctorInfo = new DoctorInfo();

                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            String receivedMessage = new String(buffer, 0, bytesRead);
                            System.out.println("接收到数据：" + receivedMessage);
                            // 在这里可以根据需要对接收到的数据进行处理
                            // 数据解析
                            if(port == 8080) { // 如果是医生端
                                // 医生端只会发送注册信息
                                String[] splitMassage = receivedMessage.split(";");
                                doctorInfo.setFloor(Integer.parseInt(splitMassage[0]));
                                doctorInfo.setIp(String.valueOf(clientSocket.getInetAddress()).substring(1));
                                doctorInfo.setName(splitMassage[1]);
                                infoList.put(doctorInfo.getFloor(), doctorInfo);
                            } else if(port == 8081) { // 如果是患者端
                                if(receivedMessage.charAt(0) == '1') { // 如果类型编号为1，就是注册信息
                                } else { // 如果类型编号为2，就是其他信息

                                }
                            }

                            // 清空缓冲区
                            buffer = new byte[BUFFER_SIZE];
                        }

                        inputStream.close();
                        clientSocket.close();
                        System.out.println("客户端连接已关闭：" + clientSocket.getInetAddress());
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                        System.out.println("客户端连接已关闭：" + clientSocket.getInetAddress());
                    }
                });

                // 启动处理客户端连接的线程
                clientThread.start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        doctors = new HashMap<>();
        users = new HashMap<>();
        Thread thread_doctor = new Thread(() -> {
            listen(8080, doctors);
        });
        Thread thread_user = new Thread(() -> {
            listen(8081, users);
        });
        thread_doctor.start();
        thread_user.start();
    }
}
