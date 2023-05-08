package com.tzp.myWebTest.service;

import com.tzp.myWebTest.dto.MapCreateDTO;

import java.util.Map;

public interface BilibiliCommentService {

    /**
     * 爬取评论
     * @param params map
     */
    void addComment(Map<String, String> params);

    /**
     * 把MapCreateDTO转为map
     * @param dto 添加类
     * @throws IllegalAccessException 捕捉转化失败
     */
    Map<String, String> convertMap(MapCreateDTO dto) throws IllegalAccessException;

    /**
     * 强制停止爬取
     */
    void stop();

    /**
     * 强制启动
     */
    void start();

}
