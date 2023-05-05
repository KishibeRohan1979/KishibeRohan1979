package com.tzp.myWebTest.util;

public class AnalyzerType {

    public static final String IK_SMART = "ik_smart";

    public static final String IK_MAX_WORD = "ik_max_word";

    // 判断一下用什么分词器
    public static String getAnalyzerType(String type) {
        switch (type) {
            case "fastest":
                return IK_SMART;
            default:
                return IK_MAX_WORD;
        }
    }

    // 查询的时候把空格删掉
    public static String deleteNull (String queryString) {
        StringBuffer stringBuffer = new StringBuffer();
        String[] strings = queryString.split(" ");
        for (String str : strings) {
            stringBuffer.append(str);
        }
        return stringBuffer.toString();
    }

}
