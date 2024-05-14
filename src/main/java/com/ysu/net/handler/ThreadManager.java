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
                /**
                 * 监听逻辑
                 */
                // 监听并接受客户端连接
                Socket clientSocket = serverSocket.accept();
                System.out.println("接收到客户端连接：" + clientSocket.getInetAddress());
                // 创建线程处理客户端连接
                Thread clientThread = new Thread(() -> {
                    try {
                        InputStream inputStream = clientSocket.getInputStream();
                        byte[] buffer = new byte[BUFFER_SIZE];
                        int bytesRead;
                        UserInfo userInfo = new UserInfo();
                        DoctorInfo doctorInfo = new DoctorInfo();
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            String receivedMessage = new String(buffer, 0, bytesRead);
                            // 在这里可以根据需要对接收到的数据进行处理
                            // 数据解析
                            String[] splitMassage = receivedMessage.split(";");
                            if(port == 8080) { // 如果是医生端
                                // 医生端只会发送注册信息
                                doctorInfo.setFloor(Integer.parseInt(splitMassage[0]));
                                doctorInfo.setIp(String.valueOf(clientSocket.getInetAddress()).substring(1));
                                doctorInfo.setName(splitMassage[1]);
                                if (!infoList.containsKey(doctorInfo.getFloor())) infoList.put(doctorInfo.getFloor(), doctorInfo);
                                // send2Doctor("1;1;70;89;20;38.5;0001;", doctorInfo.getIp()); // 是否异常(0正常 1异常);病人编号;心率;血压;呼吸频率;体温;几号位异常(如果正常0000;如果异常。。。);
                            } else if(port == 8081) { // 如果是患者端
                                // 获取患者的基本信息
                                System.out.println("数据位：" + splitMassage[0]);
                                if(splitMassage[0].charAt(0) == '.') {
                                    continue;
                                }
                                userInfo.setFloor(Integer.parseInt(splitMassage[0]));
                                userInfo.setRoomId(Integer.parseInt(splitMassage[1]));
                                userInfo.setBedId(Integer.parseInt(splitMassage[2]));
                                userInfo.setName(splitMassage[3]);
                                userInfo.setAge(Integer.parseInt(splitMassage[4]));
                                userInfo.setBaiXiBaoNumber(Double.parseDouble(splitMassage[5]));
                                userInfo.setLinBaNumber(Double.parseDouble(splitMassage[6]));
                                userInfo.setXueXiaoBan(Double.parseDouble(splitMassage[7]));
                                userInfo.setRedNumber(Double.parseDouble(splitMassage[8]));
                                userInfo.setAverage(Double.parseDouble(splitMassage[9]));
                                userInfo.setSuanjianDu(Double.parseDouble(splitMassage[10]));
                                userInfo.setNiaoBiZhong(Double.parseDouble(splitMassage[11]));
                                userInfo.setNiaoDanYuan(Double.parseDouble(splitMassage[12]));
                                userInfo.setYinYue(splitMassage[13]);
                                userInfo.setNiaoTang(splitMassage[14]);
                                userInfo.setXueya(Double.parseDouble(splitMassage[15]));
                                userInfo.setXuetang(Double.parseDouble(splitMassage[16]));
                                userInfo.setHeartRate(Double.parseDouble(splitMassage[17]));
                                userInfo.setBreathRate(Double.parseDouble(splitMassage[18]));
                                userInfo.setTemperature(Double.parseDouble(splitMassage[19]));
                                // 获取当前时间
                                LocalDateTime currentTime = LocalDateTime.now();
                                // 定义日期时间格式
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                // 格式化为字符串
                                String formattedTime = currentTime.format(formatter);
                                // 表示在几号位出现异常
                                StringBuilder flag = new StringBuilder("000000000000000");
                                StringBuilder flagNew = new StringBuilder("0000");
                                StringBuilder errorMessage = new StringBuilder();
                                // 表示是否存在异常
                                Boolean errorFlag = false; // 开始认为不存在异常
                                //开始判断
                                // 判断白细胞数量
                                if(!(userInfo.getBaiXiBaoNumber() >= 4 && userInfo.getBaiXiBaoNumber() <= 10)) {
                                    flag.setCharAt(0, '1');
                                    errorMessage.append("白细胞数量异常；");
                                    errorFlag = true;
                                }
                                // 判断淋巴细胞的数量
                                if(!(userInfo.getLinBaNumber() >= 0.8 && userInfo.getLinBaNumber() <= 4.0)) {
                                    flag.setCharAt(1, '1');
                                    errorMessage.append("淋巴细胞数量异常；");
                                    errorFlag = true;
                                }
                                // 判断血小板的数量
                                if(!(userInfo.getXueXiaoBan() >= 100 && userInfo.getXueXiaoBan() <= 300)) {
                                    flag.setCharAt(2, '1');
                                    errorMessage.append("血小板数量异常；");
                                    errorFlag = true;
                                }
                                // 判断红细胞的数量
                                if(!(userInfo.getRedNumber() >= 3.5 && userInfo.getRedNumber() <= 5.5)) {
                                    flag.setCharAt(3, '1');
                                    errorMessage.append("红细胞数量异常；");
                                    errorFlag = true;
                                }
                                // 判断红细胞体积
                                if(!(userInfo.getAverage() >= 80 && userInfo.getAverage() <= 100)) {
                                    flag.setCharAt(4, '1');
                                    errorMessage.append("红细胞平均体积异常；");
                                    errorFlag = true;
                                }
                                // 判断酸碱度
                                if((userInfo.getSuanjianDu() > 8.0 || userInfo.getSuanjianDu() < 4.6)) {
                                    flag.setCharAt(5, '1');
                                    errorMessage.append("尿检pH值异常；");
                                    errorFlag = true;
                                }
                                // 尿比重
                                if(!(userInfo.getNiaoBiZhong() >= 1.015 && userInfo.getNiaoBiZhong() <= 1.025)) {
                                    flag.setCharAt(6, '1');
                                    errorMessage.append("尿重比异常；");
                                    errorFlag = true;
                                }
                                // 尿胆原
                                if(userInfo.getNiaoDanYuan() >= 16) {
                                    flag.setCharAt(7, '1');
                                    errorMessage.append("尿胆原异常；");
                                    errorFlag = true;
                                }
                                // 隐血
                                if(!Objects.equals(userInfo.getYinYue(), "阴性-")) {
                                    flag.setCharAt(8, '1');
                                    errorMessage.append("隐血异常；");
                                    errorFlag = true;
                                }
                                // 尿糖
                                if(!Objects.equals(userInfo.getNiaoTang(), "阴性-")) {
                                    flag.setCharAt(9, '1');
                                    errorMessage.append("尿糖阳性；");
                                    errorFlag = true;
                                }
                                // 血压
                                if(userInfo.getXueya() < 80 || userInfo.getXuetang() > 120) {
                                    flag.setCharAt(10, '1');
                                    flagNew.setCharAt(1, '1');
                                    errorFlag = true;
                                }
                                // 血糖
                                if(userInfo.getXuetang() < 80 || userInfo.getXuetang() > 120) {
                                    flag.setCharAt(11, '1');
                                    errorMessage.append("血糖数值异常");
                                    errorFlag = true;
                                }
                                // 检测心率
                                if(userInfo.getHeartRate() < 60) {
                                    flag.setCharAt(12, '1');
                                    flagNew.setCharAt(0, '1');
                                    System.out.println("检测到心率偏低");
                                    errorFlag = true;
                                } else if(userInfo.getHeartRate() > 100) {
                                    flag.setCharAt(12, '1');
                                    flagNew.setCharAt(0, '1');
                                    System.out.println("检测到心率偏高");
                                    errorFlag = true;
                                }
                                // 判断呼吸频率
                                if(userInfo.getBreathRate() < 11) {
                                    flag.setCharAt(13, '1');
                                    flagNew.setCharAt(2, '1');
                                    System.out.println("检测到呼吸频率偏低");
                                    errorFlag = true;
                                } else if(userInfo.getBreathRate() > 21) {
                                    flag.setCharAt(13, '1');
                                    flagNew.setCharAt(2, '1');
                                    System.out.println("检测到呼吸频率偏高");
                                    errorFlag = true;
                                }
                                // 判断体温
                                if(userInfo.getTemperature() < 36.1) {
                                    flagNew.setCharAt(3, '1');
                                    flag.setCharAt(14, '1');
                                    System.out.println("检测到体温偏低");
                                    errorFlag = true;
                                } else if(userInfo.getTemperature() > 37.5) {
                                    flag.setCharAt(14, '1');
                                    flagNew.setCharAt(3, '1');
                                    System.out.println("检测到体温偏高");
                                    errorFlag = true;
                                }
                                // 当前时间, 楼层, 房间号, 床位号, 姓名, 是否正常
                                if(errorFlag && errorMessage.length() <= 1) {
                                    errorMessage.append("血压，呼吸频率，心率异常");
                                }
                                Object[] item = new Object[]{formattedTime, splitMassage[0], splitMassage[1], splitMassage[2], splitMassage[3], errorFlag ? errorMessage : "正常"};
                                // 发送数据
                                DoctorInfo doctorInfoRes = doctors.get(userInfo.getFloor());
                                if(doctorInfoRes != null) {
                                    System.out.println("发送给用户");
                                    String resStr = (errorFlag ? "1;" : "0;") +
                                            userInfo.getBedId().toString() + ";" +
                                            userInfo.getHeartRate() + ";" +
                                            userInfo.getXueya() + ";" +
                                            userInfo.getBreathRate() + ";" +
                                            userInfo.getTemperature() + ";" +
                                            flagNew + ";" +
                                            errorMessage + ";";
                                    send2Doctor(resStr, doctorInfoRes.getIp());
                                } else {
                                    System.out.println("医生不存在");
                                }
                                // 清理多余数据
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
                        Collection<DoctorInfo> values = doctors.values();
                        for(DoctorInfo item : values) {
                            if(item.getIp().equals(clientSocket.getInetAddress())) {
                                doctors.remove(item.getFloor());
                            }
                        }
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                        System.out.println("客户端连接已关闭：" + clientSocket.getInetAddress());
                        // 删除原有的
                        Collection<DoctorInfo> values = doctors.values();
                        for(DoctorInfo item : values) {
                            if(item.getIp().equals(clientSocket.getInetAddress())) {
                                doctors.remove(item.getFloor());
                            }
                        }
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
            listen(SERVER_PORT_DOCTOR, doctors);
        });
        Thread thread_user = new Thread(() -> {
            listen(SERVER_PORT_USER, users);
        });
        thread_doctor.start();
        thread_user.start();
    }
}
