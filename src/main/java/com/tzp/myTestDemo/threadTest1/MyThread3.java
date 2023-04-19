package com.tzp.myTestDemo.threadTest1;

import java.util.concurrent.Callable;

public class MyThread3 implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        int sum = 0;
        for (int i=1; i<=100; i++) {
            System.out.println(Thread.currentThread().getName());
            sum = sum + i;
        }
        return sum;
    }

}
