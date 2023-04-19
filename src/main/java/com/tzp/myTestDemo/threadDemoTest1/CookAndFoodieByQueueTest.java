package com.tzp.myTestDemo.threadDemoTest1;

import java.util.concurrent.ArrayBlockingQueue;

public class CookAndFoodieByQueueTest {

    /**
     *  生产者和消费者，阻塞队列
     */

    public static void main(String[] args) {
        // 1、创建阻塞队列的对象
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(1);

        // 2、创建线程的对象，并把阻塞队列传递过去
        CookByQueue c = new CookByQueue(queue);
        FoodieByQueue f =  new FoodieByQueue(queue);

        c.start();
        f.start();

    }
}
