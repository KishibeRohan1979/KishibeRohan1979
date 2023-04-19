package com.tzp.myTestDemo.threadDemoTest1;

import java.util.concurrent.ArrayBlockingQueue;

public class Foodie extends Thread {

    /**
     *
     * 1、循环
     * 2、同步代码块
     * 3、判断共享数据是否到了末尾（如果到了末尾怎么做）
     * 4、判断共享数据是否到了末尾（如果没到末尾怎么做）
     *
     */

    @Override
    public void run() {

        while (true) {
            synchronized (Desk.b) {
                if ( Desk.count == 0 ) {
                    Desk.foodFlag = 0;
                } else {
                    // 判断桌子上是否有面条
                    if ( Desk.foodFlag == 0 ) {
                        // 如果没有，等待
                        try {
                            Desk.b.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // 如果有，就开吃，吃的时候count--
                        System.out.println("吃货正在吃面条，目前还剩：" + (--Desk.count) + "碗");
                        // 吃完要唤醒厨师继续做
                        Desk.b.notifyAll();
                        if ( Desk.count == 0 ) {
                            // 修改桌子的状态
                            Desk.foodFlag = 0;
                        }
                    }
                }
            }
        }

    }

}
