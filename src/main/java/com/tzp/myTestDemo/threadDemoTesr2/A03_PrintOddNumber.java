package com.tzp.myTestDemo.threadDemoTesr2;

public class A03_PrintOddNumber {

    /**
     *
     * 同时开启两个线程，共同获取1-100之间的所有数字。
     * 要求，输出所有的奇数
     *
     * @param args
     */

    public static void main(String[] args) {
        Print p = new Print();
        Thread t1 = new Thread(p);
        Thread t2 = new Thread(p);
        t1.setName("线程1：");
        t2.setName("线程2：");
        t1.start();
        t2.start();

    }

    static class Print implements Runnable {
        /**
         * 打印数据类
         */
        public static int start = 1;

        public static byte[] lock = new byte[0];

        @Override
        public void run() {
            while (true) {
                synchronized (lock) {
                    if (start < 100) {
                        if (isOddNum(start)) {
                            System.out.println(Thread.currentThread().getName() + start);
                        }
                        start++;
                    } else {
                        break;
                    }
                }
            }
        }

        public static boolean isOddNum (int num) {
            return num % 2 != 0;
        }

    }

}
