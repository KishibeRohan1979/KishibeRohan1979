package com.tzp.myTestDemo.threadDemoTest1;

import java.util.concurrent.Callable;

public class MyThread implements Callable<Boolean> {

    static int ticket = 0;

    static final byte[] b = new byte[0];

    @Override
    public Boolean call() throws Exception {
        while (true) {
            synchronized (b) {
                if (ticket < 100) {
                    ticket++;
                    System.out.println(Thread.currentThread().getName() + "正在卖第" + ticket + "张票");
                } else {
                    System.out.println("票已售罄");
                    break;
                }
            }
        }
        return true;
    }

//    @Override
//    public void run() {
//
//            while (true) {
//                synchronized (b) {
//                    if (ticket < 100) {
//                        ticket++;
//                        System.out.println(getName() + "正在卖第" + ticket + "张票");
//                    } else {
//                        System.out.println("票已售罄");
//                        break;
//                    }
//                }
//            }
//    }
}
