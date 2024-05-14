package com.ysu.test;

import com.ysu.net.handler.ThreadManager;
import org.junit.Test;

public class Demo {
    @Test
    public void demo(){
        ThreadManager threadManager = new ThreadManager();
        threadManager.send2Doctor("1;1;70;89;20;38.5;0001;", "192.168.97.127");
    }
}
