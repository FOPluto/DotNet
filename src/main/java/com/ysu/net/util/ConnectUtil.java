package com.ysu.net.util;

import com.ysu.net.handler.ClientHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectUtil {

    /**
     *
     */


    /**
     * 监听端口
     * @param port 需要监听的端口号
     */
    public void receive(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("等待设备链接中...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("设备连接成功！");

                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("接收数据异常");
        }
    }

    /**
     * 发送指定的字符串
     * @param serverAddress 需要发送到的目标主机的ip地址
     * @param serverPort 目标主机的接收端口
     * @param message 需要发送的字符串信息
     */
    public void send(String serverAddress, int serverPort, String message) {
        try {
            Socket socket = new Socket(serverAddress, serverPort);
            System.out.println("连接服务器成功！");

            OutputStream outputStream = socket.getOutputStream();

            byte[] messageBytes = message.getBytes();

            outputStream.write(messageBytes);
            outputStream.flush();

            outputStream.close();
            socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("发送数据失败");
        }
    }
}
