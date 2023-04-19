package com.tzp.myTestDemo.threadTest1;

public class MyThread2 implements Runnable{
    @Override
    public void run() {
        // 书写要执行的方法
        for (int i=0; i<100; i++) {
            // 获取当前线程对象
            Thread thread = Thread.currentThread();
            System.out.println(thread.getName() + "：" + i);
        }
    }
}
