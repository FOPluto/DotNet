package com.ysu.net.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("接收到设备数据：" + line);
            }
            reader.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("结束接收");
        }
    }
}
