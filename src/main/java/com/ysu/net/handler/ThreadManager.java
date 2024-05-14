package com.ysu.net.handler;

import com.ysu.net.entity.DoctorInfo;
import com.ysu.net.entity.UserInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.zip.Inflater;

public class ThreadManager implements Runnable{
    private static final int SEND_PORT = 8080;
    private static final int SERVER_PORT_DOCTOR = 8080;

    private static final int SERVER_PORT_USER = 8081;
    private static final int BUFFER_SIZE = 1024;

    private static final int MAX_SIZE = 20;

    // 医生列表
    Map<Integer, DoctorInfo> doctors;

    // 患者列表
    Map<Integer, UserInfo> users;

    // 接收患者的最新数据
    public Queue<Object []> newMessage;

    // 构造函数


    public ThreadManager() {
        newMessage = new ConcurrentLinkedQueue<>();
    }

    // get和set方法

    public Map<Integer, DoctorInfo> getDoctors() {
        return doctors;
    }

    public void setDoctors(Map<Integer, DoctorInfo> doctors) {
        this.doctors = doctors;
    }

    public Map<Integer, UserInfo> getUsers() {
        return users;
    }

    public void setUsers(Map<Integer, UserInfo> users) {
        this.users = users;
    }

    /**
     * 对医生端发送数据
     * @param message 需要发送的信息
     * @param ip 目标ip地址
     */
    public void send2Doctor(String message, String ip) {
        try {
            // 创建Socket对象，并连接到服务器
            Socket socket = new Socket(ip, SEND_PORT);

            // 获取Socket的输出流
            OutputStream outputStream = socket.getOutputStream();

            // 发送数据
            byte[] data = message.getBytes();
            outputStream.write(data);

            // 关闭Socket连接
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen(int port, Map infoList) {
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
                            String[] splitMassage = receivedMessage.split(";");
                            if(port == 8080) { // 如果是医生端
                                // 医生端只会发送注册信息
                                doctorInfo.setFloor(Integer.parseInt(splitMassage[0]));
                                doctorInfo.setIp(String.valueOf(clientSocket.getInetAddress()).substring(1));
                                doctorInfo.setName(splitMassage[1]);
                                if (!infoList.containsKey(doctorInfo.getFloor())) infoList.put(doctorInfo.getFloor(), doctorInfo);
                                // send2Doctor("1;1;70;89;20;38.5;0001;", doctorInfo.getIp()); // 是否异常(0正常 1异常);病人编号;心率;血压;呼吸频率;体温;几号位异常(如果正常0000;如果异常。。。)
                            } else if(port == 8081) { // 如果是患者端
                                // 获取患者的基本信息
                                userInfo.setFloor(Integer.parseInt(splitMassage[0]));
                                userInfo.setRoomId(Integer.parseInt(splitMassage[1]));
                                userInfo.setBedId(Integer.parseInt(splitMassage[2]));
                                userInfo.setName(splitMassage[3]);
                                userInfo.setAge(Integer.parseInt(splitMassage[4]));
                                userInfo.setHeartRate(Double.parseDouble(splitMassage[4]));
                                userInfo.setBreathRate(Double.parseDouble(splitMassage[5]));
                                userInfo.setTemperature(Double.parseDouble(splitMassage[6]));
                                // 获取当前时间
                                LocalDateTime currentTime = LocalDateTime.now();
                                // 定义日期时间格式
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                // 格式化为字符串
                                String formattedTime = currentTime.format(formatter);
                                // 表示在几号位出现异常
                                StringBuilder flag = new StringBuilder("0000");
                                StringBuilder errorMessage = new StringBuilder();
                                // 表示是否存在异常
                                Boolean errorFlag = false; // 开始认为不存在异常
                                //开始判断
                                // 判断体温
                                if(userInfo.getTemperature() < 36.1) {
                                    System.out.println("检测到体温偏低");
                                    errorFlag = true;
                                    errorMessage.append("体温偏低；");
                                } else if(userInfo.getTemperature() > 37.5) {
                                    System.out.println("检测到体温偏高");
                                    errorFlag = true;
                                    errorMessage.append("体温偏高；");
                                }
                                // 判断呼吸频率
                                if(userInfo.getBreathRate() < 11) {
                                    System.out.println("检测到呼吸频率偏低");
                                    errorFlag = true;
                                    errorMessage.append("呼吸缓慢");
                                } else if(userInfo.getBreathRate() > 21) {
                                    System.out.println("检测到呼吸频率偏高");
                                    errorFlag = true;
                                    errorMessage.append("呼吸急促");
                                }
                                // 检测心率
                                if(userInfo.getHeartRate() < 60) {
                                    System.out.println("检测到心率偏低");
                                    errorFlag = true;
                                    errorMessage.append("心率过低");
                                } else if(userInfo.getHeartRate() > 100) {
                                    System.out.println("检测到心率偏高");
                                    errorFlag = true;
                                    errorMessage.append("心率过高");
                                }
                                // 当前时间, 楼层, 房间号, 床位号, 姓名, 是否正常
                                Object[] item = new Object[]{formattedTime, splitMassage[0], splitMassage[1], splitMassage[2], splitMassage[3], errorFlag ? errorMessage : "正常"};
                                while(newMessage.size() > MAX_SIZE - 1) {
                                    newMessage.remove();
                                }
                                newMessage.add(item);
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
