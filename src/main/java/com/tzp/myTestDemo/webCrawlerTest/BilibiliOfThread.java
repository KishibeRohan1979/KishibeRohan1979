package com.tzp.myTestDemo.webCrawlerTest;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
//Callable<List<Map<String, Object>>>

public class BilibiliOfThread implements Callable<List<Map<String, Object>>> {

    public static int length = Integer.parseInt(BilibiliTest.getCommentsCount(BilibiliTest.Oid));

    public static int totalPage = BilibiliTest.getPageInfo(length, 49);

    public static final byte[] lock = new byte[0];

    public static Map<String, String> params = new HashMap<>();

    public static int ticket = 1;

    public static List<Map<String, Object>> result = new LinkedList<>();

    public static Set<String> errorGetUrl = new HashSet<>();

    public static final String URL = "https://api.bilibili.com/x/v2/reply/main";

    @Override
    public List<Map<String, Object>> call() throws Exception {
        params.put("type", "1");
        params.put("oid", BilibiliTest.Oid);
        params.put("sort", "1");
        params.put("ps", "49");
        while (ticket <= totalPage) {
            System.out.println("totalPage:" + totalPage + ",length:" + length);
            String url;
            synchronized (lock) {
                params.put("pn", String.valueOf(ticket));
                url = BilibiliTest.getUrlByMap(BilibiliTest.RelyURL, params);
                ticket++;
            }
            System.out.println(Thread.currentThread().getName() + "：" + url);
            try {
                Map<String, Object> map = BilibiliTest.getComments(url);
                if ( "0".equals( map.get("code") ) ) {
                    boolean isOver = Boolean.parseBoolean( String.valueOf(map.get("isOver")) );
                    if (isOver) {
                        System.out.println("发现已经没有内容了");
                        break;
                    } else {
                        result.addAll( (List<Map<String, Object>>) map.get("resultList"));
                    }
                } else {
                    System.out.println("第" + ticket + "页爬取失败，code=" + map.get("code") + "，" + map.get("requestMessage"));
                }
            } catch (Exception e) {
                errorGetUrl.add(url);
                e.printStackTrace();
                Map<String, Object> errorMap = getErrorComments(errorGetUrl);
                result.add(errorMap);
            }
        }
        return result;
    }

    public static Map<String, Object> getErrorComments(Set<String> urls) {
        Map<String, Object> map = new HashMap<>();
        try {
            for (String url : urls) {
                System.out.println("循环中...");
                map = BilibiliTest.getComments(url);

            }
        } catch (IOException e) {
            getErrorComments(urls);
            e.printStackTrace();
        }
        return map;
    }

}
