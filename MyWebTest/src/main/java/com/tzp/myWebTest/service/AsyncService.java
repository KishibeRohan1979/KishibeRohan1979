package com.tzp.myWebTest.service;

import org.aspectj.lang.ProceedingJoinPoint;
import com.tzp.myWebTest.util.AsyncMsgUtil;

public interface AsyncService {

    /**
     * 更新任务进度
     *
     * @param per
     */
    void updateProgress(String per);

    /**
     * 异步执行任务
     *
     * @param AsyncMsgUtil
     * @param point
     */
    void async(AsyncMsgUtil AsyncMsgUtil, ProceedingJoinPoint point);

    /**
     * 查询任务进度
     *
     * @param id
     * @return
     */
    AsyncMsgUtil findAsyncMsgUtil(String id);

}
