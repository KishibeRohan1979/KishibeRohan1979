package com.tzp.myTestDemo.esTest;

import com.alibaba.fastjson.JSON;

public class esTestMain {

    public static void main(String[] args) {
        EsTest document = new EsTest("1", "zhangsan", "男", "18", "重来74751次我也会在.\n总有一天会合身的.\nBack into the INK.\nTo the moon.\n藤近小梅为什么是神！");
        System.out.println(testJson(document));
    }

    public static String testJson(EsTest document) {
        return JSON.toJSONString(document, false);
    }

}
