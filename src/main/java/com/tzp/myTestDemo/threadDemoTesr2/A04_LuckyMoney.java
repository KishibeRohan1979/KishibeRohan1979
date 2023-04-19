package com.tzp.myTestDemo.threadDemoTesr2;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class A04_LuckyMoney {

    /**
     * 抢红包
     * 假设：100块钱，分成了3个红包，现在有5个人去抢
     * 其中，红包是共享数据
     * 5个人5条线程。
     * 打印结果如下：
     * xxx抢到了xxx元
     * xxx抢到了xxx元
     * xxx抢到了xxx元
     * xxx手速慢了，红包已经被抢完了
     * xxx手速慢了，红包已经被抢完了
     *
     * @param args
     */

    public static void main(String[] args) {
        Money money = new Money();
        Thread t1 = new Thread(money);
        Thread t2 = new Thread(money);
        Thread t3 = new Thread(money);
        Thread t4 = new Thread(money);
        Thread t5 = new Thread(money);

        t1.setName("用户1");
        t2.setName("用户2");
        t3.setName("用户3");
        t4.setName("用户4");
        t5.setName("用户5");


        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t1.start();

        System.out.println("代码执行完毕");

    }

    static class Money implements Runnable {

        /**
         * 红包类
         */

        public static int countMoney = 100;

        public static int countSize = 3;

        public static final byte[] lock = new byte[0];

        @Override
        public void run() {
            /**
             * 这个方法是抢红包的方法，稍微有些要求
             * 1、抢红包，抢完之后有个还有钱，即假设都是整数抢
             *      那么，三个红包第一次抢，至少还剩下2块钱，第一次抢，也不能只拿走0元
             *      即，剩余的钱应该 >= 剩余红包个数 && 随机数 != 0
             */
            synchronized (lock) {
                if (countSize > 1) {
                    int thisMoney = 0;
                    while (thisMoney == 0 || (countMoney - thisMoney < countSize - 1)) {
                        thisMoney = getRandomNum(countMoney);
                    }
                    System.out.println(Thread.currentThread().getName() + "抢到了" + thisMoney + "元");
                    countMoney = countMoney - thisMoney;
                    countSize--;
                } else if (countSize == 1) {
                    System.out.println(Thread.currentThread().getName() + "抢到了" + countMoney + "元");
                    countMoney = 0;
                    countSize = 0;
                } else {
                    System.out.println(Thread.currentThread().getName() + "手速慢了，红包已经被抢完了");
                }
            }
        }

        // 获取一个指定数字内的随机数
        public static int getRandomNum(int count) {
            Random random = new Random();
            return random.nextInt(count);
        }

    }

}
