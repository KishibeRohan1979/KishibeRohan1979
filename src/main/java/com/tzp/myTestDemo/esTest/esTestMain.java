package com.tzp.myTestDemo.esTest;

import com.alibaba.fastjson.JSON;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class esTestMain {

    public static void main(String[] args) {
//        EsTest document = new EsTest("1", "zhangsan", "男", "18", "重来74751次我也会在.\n总有一天会合身的.\nBack into the INK.\nTo the moon.\n藤近小梅为什么是神！");
//        System.out.println(testJson(document));
        printCount("D:\\Elasticsearch\\elasticsearch-7.8.0\\plugins\\elasticsearch-analysis-ik-7.8.0\\config\\punctuation.dic");
    }

    public static String testJson(EsTest document) {
        return JSON.toJSONString(document, false);
    }

    public static void printCount(String filePath) {
        // 文件的绝对路径作为参数传入
        File file = new File(filePath);
        Map<String, Integer> wordCountMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    if (wordCountMap.containsKey(word)) {
                        wordCountMap.put(word, wordCountMap.get(word) + 1);
                    } else {
                        wordCountMap.put(word, 1);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Map.Entry<String, Integer> entry : wordCountMap.entrySet()) {
            if (entry.getValue() != 1) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }
        }
    }


}
