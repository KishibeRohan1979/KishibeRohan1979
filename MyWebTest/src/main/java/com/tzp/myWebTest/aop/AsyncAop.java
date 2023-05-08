package com.tzp.myWebTest.aop;

import com.tzp.myWebTest.service.AsyncService;
import com.tzp.myWebTest.util.AsyncMsgUtil;
import com.tzp.myWebTest.util.MsgUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Aspect
@Component
@Slf4j
public class AsyncAop {

    @Autowired
    private AsyncService asyncService;

    @Pointcut("@annotation(com.tzp.myWebTest.aop.EnableAsync)")
    public void asyncPointCut() {
    }

    @Around("asyncPointCut()")
    public MsgUtil around(ProceedingJoinPoint point) throws Throwable {
        //请求header
//        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //异步消息
        String id = UUID.randomUUID().toString();
        AsyncMsgUtil asyncMsg = new AsyncMsgUtil();
        asyncMsg.setId(id);
        //异步执行
        asyncService.async(asyncMsg, point);
        //异步返回值
        return MsgUtil.success("操作成功", asyncMsg);
    }

}
