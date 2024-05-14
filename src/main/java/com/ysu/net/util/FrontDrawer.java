package com.ysu.net.util;

import com.ysu.net.handler.ThreadManager;

public class FrontDrawer implements Runnable {

    /**
     * 监视对象
     */
    private ThreadManager threadManager;

    private MainWindow mainWindow;

    public FrontDrawer(ThreadManager threadManager) {
        this.threadManager = threadManager;
    }

    @Override
    public void run() {
        mainWindow = new MainWindow(threadManager);
        mainWindow.draw();
    }
}
