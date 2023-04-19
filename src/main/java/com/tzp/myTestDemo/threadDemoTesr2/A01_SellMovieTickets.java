package com.tzp.myTestDemo.threadDemoTesr2;

public class A01_SellMovieTickets {

    /**
     * 一共有1000张电影票，可以在两个窗口领取，假设每次领取的时间为1000毫秒，
     * 要求：请用多线程模拟卖票过程并打印剩余电影票的数量
     *
     * @param args
     */

    public static void main(String[] args) {


        Window window = new Window();
        Thread t1 = new Thread(window);
        Thread t2 = new Thread(window);

        t1.setName("窗口1");
        t2.setName("窗口2");

        t1.start();
        t2.start();



    }

    static class Window implements Runnable {
        /**
         * 售卖窗口的类
         */
        public static int count = 50;

        public static byte[] lock = new byte[0];

        @Override
        public void run() {
            while (true) {
                System.out.println("正在使用" + Thread.currentThread().getName() + "购买，请稍后...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock) {
                    if (count > 0) {
                        System.out.println(Thread.currentThread().getName() + "购买成功，当前剩余" + (--count) + "张电影票");
                    } else {
                        System.out.println(Thread.currentThread().getName() + "消息：当前电影票已经售罄");
                        break;
                    }
                }
            }
        }
    }

}
