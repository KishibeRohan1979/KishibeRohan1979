package com.tzp.myTestDemo.threadDemoTest1;

public class CookAndFoodieTest {

    /**
     * 生产者和消费者的代码
     */

    public static void main(String[] args) {
        // 创建线程对象
        Cook c = new Cook();
        Foodie f = new Foodie();

        c.start();
        f.start();

    }

}
