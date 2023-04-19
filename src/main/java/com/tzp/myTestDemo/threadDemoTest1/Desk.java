package com.tzp.myTestDemo.threadDemoTest1;

public class Desk {

    /**
     * 控制生产者和消费者的执行
     *
     */

    // 是否有面条 0：没有， 1：有面条
    public static int foodFlag = 0;

    // 总个数
    public static int count = 10;

    // 锁对象
    public static final byte[] b = new byte[0];

}
