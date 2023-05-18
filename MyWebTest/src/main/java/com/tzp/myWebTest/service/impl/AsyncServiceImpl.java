package com.tzp.myWebTest.service.impl;

import com.tzp.myWebTest.service.AsyncService;
import com.tzp.myWebTest.util.AsyncHolder;
import com.tzp.myWebTest.util.AsyncMsgUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AsyncServiceImpl implements AsyncService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 更新任务进度
     * @param asyncMsg 消息
     * @param point aop
     */
    @Override
    @Async("asyncServiceExecutor")
    public void async(AsyncMsgUtil asyncMsg, ProceedingJoinPoint point) {
        String id = asyncMsg.getId();
        //异步消息线程变量-传送id到实际方法以便方法更新进度
        AsyncHolder.set(asyncMsg);
        //执行方法
        try {
            Object result = point.proceed();
            asyncMsg.setResult(result);
            asyncMsg.setStatus("0");
            asyncMsg.setProgress("100");
            redisTemplate.opsForValue().set(id, asyncMsg, 10, TimeUnit.MINUTES);
            Thread.sleep(5000);
            redisTemplate.delete(id);
        } catch (Throwable throwable) {
            asyncMsg.setStatus("-1");
            asyncMsg.setResult(throwable.getLocalizedMessage());
            redisTemplate.opsForValue().set(id, asyncMsg, 10, TimeUnit.MINUTES);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                redisTemplate.delete(id);
            }
        } finally {
            AsyncHolder.clear();
        }
    }

    @Override
    public void updateProgress(String per) {
        AsyncMsgUtil asyncMsg = AsyncHolder.get();
        asyncMsg.setProgress(per);
        asyncMsg.setResult("任务执行中...");
        // 设置变量值的过期时间，60分钟
        redisTemplate.opsForValue().set(asyncMsg.getId(), asyncMsg, 10, TimeUnit.MINUTES);
    }

    @Override
    public void updateMsg(Object result) {
        AsyncMsgUtil asyncMsg = AsyncHolder.get();
        asyncMsg.setResult(result);
        // 设置变量值的过期时间，60分钟
        redisTemplate.opsForValue().set(asyncMsg.getId(), asyncMsg, 10, TimeUnit.MINUTES);
    }

    /**
     * 查询任务进度
     * @param id 查询id
     */
    @Override
    public AsyncMsgUtil findAsyncMsgUtil(String id) {
        AsyncMsgUtil asyncMsg = (AsyncMsgUtil) redisTemplate.opsForValue().get(id);
        return asyncMsg;
    }
}