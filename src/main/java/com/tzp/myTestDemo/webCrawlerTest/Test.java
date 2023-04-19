package com.tzp.myTestDemo.webCrawlerTest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Test {

    public static void main(String[] args) {
        long statrTime = System.currentTimeMillis();
        BilibiliOfThread b = new BilibiliOfThread();

        FutureTask<List<Map<String, Object>>> ft1 = new FutureTask<>(b);
        FutureTask<List<Map<String, Object>>> ft2 = new FutureTask<>(b);
        FutureTask<List<Map<String, Object>>> ft3 = new FutureTask<>(b);
        FutureTask<List<Map<String, Object>>> ft4 = new FutureTask<>(b);
        FutureTask<List<Map<String, Object>>> ft5 = new FutureTask<>(b);
        FutureTask<List<Map<String, Object>>> ft6 = new FutureTask<>(b);
        FutureTask<List<Map<String, Object>>> ft7 = new FutureTask<>(b);
        FutureTask<List<Map<String, Object>>> ft8 = new FutureTask<>(b);
        FutureTask<List<Map<String, Object>>> ft9 = new FutureTask<>(b);
        FutureTask<List<Map<String, Object>>> ft10 = new FutureTask<>(b);


        // 创建线程的对象
        Thread t1 = new Thread(ft1);
        Thread t2 = new Thread(ft2);
        Thread t3 = new Thread(ft3);
        Thread t4 = new Thread(ft4);
        Thread t5 = new Thread(ft5);
        Thread t6 = new Thread(ft6);
        Thread t7 = new Thread(ft7);
        Thread t8 = new Thread(ft8);
        Thread t9 = new Thread(ft9);
        Thread t10 = new Thread(ft10);

        // 线程重命名
        t1.setName("线程1");
        t2.setName("线程2");
        t3.setName("线程3");
        t4.setName("线程4");
        t5.setName("线程5");
        t6.setName("线程6");
        t7.setName("线程7");
        t8.setName("线程8");
        t9.setName("线程9");
        t10.setName("线程10");

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();
        t7.start();
        t8.start();
        t9.start();
        t10.start();

        // 获取多线程运行的结果
        try {
            List<Map<String, Object>> result = ft1.get();
            System.out.println("一共发现为视频的评论有：" + result.size() + "条");
            System.out.println(BilibiliTest.bvidToAid(BilibiliTest.BVid));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("执行完毕，共耗时：" + (endTime - statrTime) + "毫秒。即：大约" + ((endTime - statrTime) / 1000 + 1) + "秒");

    }
}
