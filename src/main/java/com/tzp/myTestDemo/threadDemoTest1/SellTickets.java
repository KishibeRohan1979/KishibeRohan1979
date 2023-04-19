package com.tzp.myTestDemo.threadDemoTest1;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class SellTickets {

    public static void main(String[] args) {
        MyThread myThread = new MyThread();
        FutureTask<Boolean> ft1 = new FutureTask<>(myThread);
        FutureTask<Boolean> ft2 = new FutureTask<>(myThread);
        FutureTask<Boolean> ft3 = new FutureTask<>(myThread);
        Thread t1 = new Thread(ft1);
        Thread t2 = new Thread(ft2);
        Thread t3 = new Thread(ft3);
        t1.setName("窗口1");
        t2.setName("窗口2");
        t3.setName("窗口3");
        t1.start();
        t2.start();
        t3.start();
        try {
            Boolean result1 = ft1.get();
            Boolean result2 = ft2.get();
            Boolean result3 = ft3.get();
            System.out.println(result1);
            System.out.println(result2);
            System.out.println(result3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}
