package com.ysu.net.util;

public class FrontDrawer implements Runnable {

    private MainWindow mainWindow;

    @Override
    public void run() {
        mainWindow = new MainWindow();
        mainWindow.draw();
    }
}
