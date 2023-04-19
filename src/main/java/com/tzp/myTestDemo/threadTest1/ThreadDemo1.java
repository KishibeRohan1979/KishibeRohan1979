package com.tzp.myTestDemo.threadTest1;

public class ThreadDemo1 {

    /**
     * 多线程的第一个启动方式
     * 1、自定义一个类继承Thread
     * 2、重写run方法
     * 3、创建子类对象，并启动线程
     * @param args
     */
    public static void main(String[] args) {
        MyThread1 t1 = new MyThread1();
        MyThread1 t2 = new MyThread1();
        // 设置名字
        t1.setName("线程1");
        t2.setName("线程2");
        // 开启线程
        t1.start();
        t2.start();
    }

}
