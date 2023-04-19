package com.tzp.myTestDemo.threadTest1;

public class ThreadDemo2 {

    /**
     * 多线程的第一个启动方式
     * 1、自定义一个类实现Runnable接口
     * 2、重写run方法
     * 3、创建自己的类对象
     * 4、创建一个Thread类对象，并开启线程
     * @param
     */
    public static void main(String[] args) {
        // 创建MyThread2对象
        // 表示多线程要执行的任务
        MyThread2 myThread2 = new MyThread2();
        // 创建线程对象
        Thread t1 = new Thread(myThread2);
        Thread t2 = new Thread(myThread2);

        // 给线程创建名字
        t1.setName("线程1");
        t2.setName("线程2");

        // 开启线程
        t1.start();
        t2.start();
    }
}
