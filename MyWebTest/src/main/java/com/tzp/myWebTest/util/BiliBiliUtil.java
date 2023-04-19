package com.tzp.myWebTest.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BiliBiliUtil {

    /**
     * 结果map关键字对应表
     * rpid：评论的id
     * ctime：评论的创建时间
     * rcount：评论的回复数量
     * like：评论的点赞数
     * userid：评论者的uid
     * uname：评论者的昵称
     * sex：评论者的性别
     * sign：评论者的个人简介
     * userLevel：评论者等级
     * user_sailing：评论者的渲染信息
     * avatar：评论者的头像地址
     * isContractor：是不是up的老粉
     * contractDesc：老粉牌子的称呼
     * 记录会员信息（type=0 非会员， type=1 大会员， type=2 年度大会员），需要注意的是4月1日可能会不同，但是我没办法测试了
     * vipType：评论者的会员类型
     * vipStatus：评论者现在是不是会员（0，不是；1，是）
     * vip_label：vip渲染信息
     * fans_detail：(注意：有些用户可能没有带粉丝牌，返回为空)评论者的粉丝牌渲染信息（包括一些其他信息）
     * fansLevel：评论者的粉丝牌等级(注意：有些用户可能没有带粉丝牌，返回为空)
     * fansName：评论者的粉丝牌内容（名字）(注意：有些用户可能没有带粉丝牌，返回为空)
     * thisUserMessage：评论者的论点（即评论内容）
     * pictures：评论的图片列表
     * message_emote：评论的渲染信息
     * isUpLikeThisMessage：这个评论up是不是点赞了（true或false）
     * isUpReplyThisMessage：这个评论up有没有回复（true或false）
     * avid：视频的avid
     * bvid：视频的bvid
     * isDelete：是否可见
     */

    // 获取评论区明细_翻页加载，请求方式：GET，参考地址：https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/comment/list.md
    public static final String RelyURL = "https://api.bilibili.com/x/v2/reply";

    // 获取评论区总和（），请求方式：GET，参考地址：https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/comment/list.md
    public static final String RelyCountURL = "https://api.bilibili.com/x/v2/reply/count";

    //执行访问api的方法，其中使用的方法是get
    public static String callApiByGet(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
//        int status = con.getResponseCode();
//        System.out.println("状态码：" + status);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();

        return content.toString();
    }

    //获取评论数总和
    public static String getCommentsCount(String oid) {
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        params.put("oid", oid);
        String url = getUrlByMap(RelyCountURL, params);
        try {
            String result = callApiByGet(url);
            HashMap<String, Object> resultJsonMap = JSON.parseObject(result, HashMap.class);
            switch (resultJsonMap.get("code").toString()) {
                case "0":
                    HashMap<String, Object> dataJsonMap = JSON.parseObject(resultJsonMap.get("data").toString(), HashMap.class);
                    return dataJsonMap.get("count").toString();
                case "-404":
                    return "请求错误，状态码：" + resultJsonMap.get("code") + "，可能没有这个视频捏~(￣▽￣)~*";
                case "-400":
                    return "请求错误，状态码：" + resultJsonMap.get("code") + "";
                default:
                    return "请求错误，状态码：" + resultJsonMap.get("code");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "请求错误，也许up主删除了该视频";
        } catch (Exception e) {
            e.printStackTrace();
            return "请求错误";
        }
    }

    //设计一个map作为参数拼接的方法
    public static String getUrlByMap(String apiUrl, Map<String, String> params) {
        StringBuilder content = new StringBuilder(apiUrl);
        content.append("?");
        Set<String> set = params.keySet();
        for (String param : set) {
            content.append(param);
            content.append("=");
            content.append(params.get(param));
            content.append("&");
        }
        content.delete(content.length() - 1, content.length());
        return content.toString();
    }

    //设计了一个使用list作为参数拼接的方法
    public static String getUrlByList(String apiUrl, List<String> params) {
        StringBuilder content = new StringBuilder(apiUrl);
        content.append("?");
        for (String param : params) {
            content.append(param);
            content.append("&");
        }
        content.delete(content.length() - 1, content.length());
        return content.toString();
    }

    // 设计一个关于查询一页49行的情况下，有多少页
    public static int getPageInfo(String totalSize, String ps) {
        int pageSize = Integer.parseInt(ps);
        if (pageSize == 0) {
            return 1;
        }
        int total = Integer.parseInt(totalSize);
        int totalPage;
        if (total % pageSize != 0) {
            totalPage = total / pageSize + 1;
        } else {
            totalPage = total / pageSize;
        }
        return totalPage;
    }

    // 设计一个关于查询一页49行的情况下，有多少页
    public static int getPageInfo(int totalSize, int ps) {
        if (ps == 0) {
            return 1;
        }
        int totalPage;
        if (totalSize % ps != 0) {
            totalPage = totalSize / ps + 1;
        } else {
            totalPage = totalSize / ps;
        }
        return totalPage;
    }

    // 执行查询评论的方法
    public static Map<String, Object> getComments(String url) throws IOException {
        String result = callApiByGet(url);
        HashMap<String, Object> resultJsonMap = JSON.parseObject(result, HashMap.class);
        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> resultList = new ArrayList<>();
        switch (resultJsonMap.get("code").toString()) {
            case "0":
                resultMap.put("code", "0");
                HashMap<String, Object> dataJsonMap = JSON.parseObject(resultJsonMap.get("data").toString(), HashMap.class);
                ArrayList<Map<String, Object>> replies = JSON.parseObject(dataJsonMap.get("replies").toString(), ArrayList.class);
                for (Map<String, Object> replie : replies) {
                    Map<String, Object> map = new HashMap<>();
                    // 评论的id
                    map.put("rpid", replie.get("rpid"));
                    // 评论的创建时间
                    map.put("ctime", replie.get("ctime"));
                    // 评论的回复数量
                    map.put("rcount", replie.get("rcount"));
                    // 评论的点赞数
                    map.put("like", replie.get("like"));
                    HashMap<String, Object> memberJsonMap = JSON.parseObject(replie.get("member").toString(), HashMap.class);
                    map.put("userid", memberJsonMap.get("mid"));
                    map.put("uname", memberJsonMap.get("uname"));
                    map.put("sex", memberJsonMap.get("sex"));
                    // 记录个人简介
                    map.put("sign", memberJsonMap.get("sign"));
                    // 记录用户等级
                    HashMap<String, Object> levelInfoJsonMap = JSON.parseObject(memberJsonMap.get("level_info").toString(), HashMap.class);
                    map.put("userLevel", levelInfoJsonMap.get("current_level"));
                    // 记录用户渲染信息
                    HashMap<String, Object> userSailingJsonMap = JSON.parseObject(memberJsonMap.get("user_sailing").toString(), HashMap.class);
                    map.put("user_sailing", userSailingJsonMap);
                    // 记录头像地址
                    map.put("avatar", memberJsonMap.get("avatar"));
                    // 记录是不是老粉
                    map.put("isContractor", memberJsonMap.get("is_contractor"));
                    // 记录老粉牌子称呼
                    map.put("contractDesc", memberJsonMap.get("contract_desc"));
                    // 记录会员信息（type=0 非会员， type=1 大会员， type=2 年度大会员），需要注意的是4月1日可能会不同，但是我没办法测试了
                    HashMap<String, Object> vipJsonMap = JSON.parseObject(memberJsonMap.get("vip").toString(), HashMap.class);
                    map.put("vipType", vipJsonMap.get("vipType"));
                    // 记录现在是不是会员
                    map.put("vipStatus", vipJsonMap.get("vipStatus"));
                    // 记录vip的渲染信息
                    map.put("vip_label", vipJsonMap.get("label"));
                    // 记录粉丝牌信息
                    HashMap<String, Object> fansDetailJsonMap;
                    if (memberJsonMap.get("fans_detail") != null) {
                        fansDetailJsonMap = JSON.parseObject(memberJsonMap.get("fans_detail").toString(), HashMap.class);
                        map.put("fans_detail", fansDetailJsonMap);
                        // 记录粉丝牌的等级
                        map.put("fansLevel", fansDetailJsonMap.get("level"));
                        // 记录粉丝牌的名字
                        map.put("fansName", fansDetailJsonMap.get("medal_name"));
                    } else {
                        map.put("fans_detail", null);
                        // 记录粉丝牌的等级
                        map.put("fansLevel", "0");
                        // 记录粉丝牌的名字
                        map.put("fansName", "");
                    }
                    // 记录评论的信息
                    HashMap<String, Object> contentJsonMap = JSON.parseObject(replie.get("content").toString(), HashMap.class);
                    map.put("thisUserMessage", contentJsonMap.get("message"));
                    // 记录评论的渲染信息
                    map.put("message_emote", contentJsonMap.get("emote"));
                    // 评论中发布的图片
                    map.put("pictures", contentJsonMap.get("pictures"));
                    // 记录up主和这个评论的互动
                    HashMap<String, Object> upActionJsonMap = JSON.parseObject(replie.get("up_action").toString(), HashMap.class);
                    // 记录up是否点赞了这个评论
                    map.put("isUpLikeThisMessage", upActionJsonMap.get("like"));
                    // 记录up主是否回复了这个评论
                    map.put("isUpReplyThisMessage", upActionJsonMap.get("reply"));
                    JSONObject json = new JSONObject(map);
                    resultList.add(json);
                }
                resultMap.put("resultList", resultList);
                resultMap.put("requestMessage", "请求成功");
                if (replies.size() == 0) {
                    resultMap.put("isOver", "true");
                } else {
                    resultMap.put("isOver", "false");
                }
                break;
            case "-404":
                resultMap.put("code", "-404");
                resultMap.put("requestMessage", "什么都木有");
                resultMap.put("resultList", resultList);
                resultMap.put("isOver", "true");
                break;
            case "-400":
                resultMap.put("code", "-400");
                resultMap.put("requestMessage", "请求错误");
                resultMap.put("resultList", resultList);
                resultMap.put("isOver", "true");
                break;
            case "12002":
                resultMap.put("code", "12002");
                resultMap.put("requestMessage", "评论区已关闭");
                resultMap.put("resultList", resultList);
                resultMap.put("isOver", "true");
                break;
            default:
                resultMap.put("code", "-1");
                resultMap.put("requestMessage", "请求失败");
                resultMap.put("resultList", resultList);
                resultMap.put("isOver", "true");
                break;
        }
        return resultMap;
    }

    // 爬取指定行数（所有）的评论
    public static List<Map<String, Object>> getAllComments(String oid, int length) {
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        params.put("oid", oid);
        params.put("sort", "1");
        params.put("ps", "49");
        List<Map<String, Object>> list = new ArrayList<>();
        int totalPage = getPageInfo(length, 49);
        System.out.println("totalPage:" + totalPage + ",length:" + length);
        for (int i = 1; i <= totalPage; i++) {
            params.put("pn", String.valueOf(i));
            String url = getUrlByMap(RelyURL, params);
            System.out.println(url);
            try {
                Map<String, Object> map = getComments(url);
                if ("0".equals(map.get("code"))) {
                    boolean isOver = Boolean.parseBoolean(String.valueOf(map.get("isOver")));
                    if (isOver) {
                        System.out.println("发现已经没有内容了");
                        break;
                    } else {
                        list.addAll((List<Map<String, Object>>) map.get("resultList"));
                    }
                } else {
                    System.out.println("第" + i + "页爬取失败，code=" + map.get("code") + "，" + map.get("requestMessage"));
                    i--;
                }
            } catch (IOException e) {
                i--;
                e.printStackTrace();
            }
        }
        return list;
    }

    // 查询某个视频底下的前x赞评论，并非排序所有评论！！！
    public static List<Map<String, Object>> getCommentsByOid(String oid, int returnLength) {
        String countComment = getCommentsCount(oid);
        int totalComment = Integer.parseInt(countComment);
        if (returnLength > totalComment) {
            returnLength = totalComment;
        }
        List<Map<String, Object>> list = getAllComments(oid, totalComment);
        // 根据点赞数量倒序（由大到小）排序
        list = getListByListDesc(list);
        //排序完毕之后，将这个list返回
        if (list.size() > returnLength) {
            list = list.subList(0, returnLength);
        }
        return list;
    }

    // 根据点赞数量倒序（由大到小）排序的方法
    public static List<Map<String, Object>> getListByListDesc(List<Map<String, Object>> list) {
        list.sort(new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                return Integer.parseInt(String.valueOf(o2.get("like"))) - Integer.parseInt(String.valueOf(o1.get("like")));
            }
        });
        return list;
    }

    // 查询某个时间段内的评论
    public static List<Map<String, Object>> getCommentsByOidAndTime(String oid, int returnLength, String startTime, String endTime) {
        DateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            Date minTime = dataFormat.parse(startTime);
            Date maxTime = dataFormat.parse(endTime);
            long min = minTime.getTime() / 1000;
            long max = maxTime.getTime() / 1000;
            if (max < min) {
                long flag = max;
                max = min;
                min = flag;
            }
            String countComment = getCommentsCount(oid);
            int totalComment = Integer.parseInt(countComment);
            if (returnLength > totalComment) {
                returnLength = totalComment;
            }
            List<Map<String, Object>> list = getAllComments(oid, totalComment);
            for (Map<String, Object> map : list) {
                long ctime = Long.parseLong(String.valueOf(map.get("ctime")));
                if (ctime >= min && ctime <= max) {
                    resultList.add(map);
                }
            }
            // 根据点赞数量倒序（由大到小）排序
            resultList = getListByListDesc(resultList);
            //排序完毕之后，将这个list返回
            if (resultList.size() > returnLength) {
                resultList = resultList.subList(0, returnLength);
            }
            return resultList;
        } catch (ParseException e) {
            System.out.println("哦呀？报错了？不着急，检查一下输入的时间格式是否错误，接下来我将做一个示范");
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            System.out.println("例如现在的时间是：" + simpleDateFormat.format(date));
            System.out.println("请注意格式为，\"yyyy-MM-dd HH:mm:ss\"，其中有空格，有秒钟，如果出现整点，也请按照刚刚的格式填写例如：");
            System.out.println("2023-04-01 08:00:00");
            return resultList;
        }
    }

}
