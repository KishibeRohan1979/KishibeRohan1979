package com.tzp.myWebTest.util;

public class AnalyzerType {

    public static final String IK_SMART = "ik_smart";

    public static final String IK_MAX_WORD = "ik_max_word";

    public static final String ENGLISH = "english";

    // 判断一下用什么分词器
    public static String getAnalyzerType(String type) {
        switch (type) {
            case "fast":
                return IK_MAX_WORD;
            case "slowest":
                return ENGLISH;
            default:
                return IK_SMART;
        }
    }

}
