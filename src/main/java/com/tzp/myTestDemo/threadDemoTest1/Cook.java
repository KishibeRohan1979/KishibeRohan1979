package com.tzp.myTestDemo.threadDemoTest1;

import java.util.concurrent.ArrayBlockingQueue;

public class Cook extends Thread {

    /**
     * 1、循环
     * 2、同步代码块
     * 3、判断共享数据是否到了末尾（如果到了末尾怎么做）
     * 4、判断共享数据是否到了末尾（如果没到末尾怎么做）
     */

    @Override
    public void run() {
        while (true) {
            // 不停的制作面条，并将面条放到队列里
            synchronized (Desk.b) {
                if (Desk.count == 10) {
                    Desk.foodFlag = 1;
                } else {
                    // 判断桌子上是否有面条
                    if (Desk.foodFlag == 1) {
                        // 如果有，等待
                        try {
                            Desk.b.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // 如果没有，就开始做饭，count++
                        System.out.println("厨师正在做饭，目前还有：" + (++Desk.count) + "碗");
                        // 做完要唤吃货继续吃
                        Desk.b.notifyAll();
                        if (Desk.count == 10) {
                            // 修改桌子的状态
                            Desk.foodFlag = 1;
                        }
                    }
                }
            }
        }
    }
}