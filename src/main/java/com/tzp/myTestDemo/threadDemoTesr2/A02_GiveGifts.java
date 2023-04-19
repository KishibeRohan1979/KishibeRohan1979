package com.tzp.myTestDemo.threadDemoTesr2;

public class A02_GiveGifts {

    /**
     *
     * 有100份礼品，两个人同时发送，当剩下的礼品小于10份的时候则不再送出。
     * 利用多线程模拟该过程并将线程的名字和礼物的数量打印出来
     *
     * @param args
     */

    public static void main(String[] args) {

        Worker worker = new Worker();
        Thread t1 = new Thread(worker);
        Thread t2 = new Thread(worker);

        t1.setName("工作人员1");
        t2.setName("工作人员2");

        t1.start();
        t2.start();

    }

    static class Worker implements Runnable {
        /**
         * 工作人员类
         */
        public static int count = 100;

        public static byte[] lock = new byte[0];

        @Override
        public void run() {
            while (true) {
                synchronized (lock) {
                    if (count > 10) {
                        System.out.println(Thread.currentThread().getName() + "赠予了一份礼品，当前剩余" + (--count) + "份礼品");
                    } else {
                        System.out.println(Thread.currentThread().getName() + "消息：当前礼品已经不足，目前剩余的" + count + "份礼品将赠与公益机构");
                        break;
                    }
                }
            }
        }
    }

}
