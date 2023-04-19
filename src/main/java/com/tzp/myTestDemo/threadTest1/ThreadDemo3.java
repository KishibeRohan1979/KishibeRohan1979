package com.tzp.myTestDemo.threadTest1;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class ThreadDemo3 {

    /**
     * 多线程的第三种实现方式：
     * 特点：可以获取到多线程的运行结果
     * 1、创建一个类：MyThread3，实现Callable接口
     * 2、重写call（是有返回值的，表示多线程运行的结果）
     * 3、创建MyThread3的对象（表示多线程要执行的任务）
     * 4、创建FutureTask的对象（作用管理多线程运行的结果）
     * 5、创建Thread类的对象，并启动（表示线程）
     * @param args
     */

    public static void main(String[] args) {
        // 创建MyThread3的对象
        MyThread3 myThread3 = new MyThread3();
        // 创建FutureTask的对象
        FutureTask<Integer> ft1 = new FutureTask<>(myThread3);
        FutureTask<Integer> ft2 = new FutureTask<>(myThread3);
        // 创建线程的对象
        Thread t1 = new Thread(ft1);
        Thread t2 = new Thread(ft2);
        // 线程重命名
        t1.setName("线程1");
        t2.setName("线程2");
        // 启动线程
        t1.start();
        t2.start();
        // 获取多线程运行的结果
        try {
            Integer result1 = ft1.get();
            Integer result2 = ft2.get();
            System.out.println(result1);
            System.out.println(result2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}
