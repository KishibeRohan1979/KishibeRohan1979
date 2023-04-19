package com.tzp.myTestDemo.threadDemoTest1;

import java.util.concurrent.ArrayBlockingQueue;

public class CookByQueue extends Thread {

    /**
     * 1、循环
     * 2、同步代码块
     * 3、判断共享数据是否到了末尾（如果到了末尾怎么做）
     * 4、判断共享数据是否到了末尾（如果没到末尾怎么做）
     */

    ArrayBlockingQueue<String> queue;

    public CookByQueue (ArrayBlockingQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            // 不断的把面条放入到阻塞队列中
            try {
                queue.put("面条");
                System.out.println("厨师放");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
