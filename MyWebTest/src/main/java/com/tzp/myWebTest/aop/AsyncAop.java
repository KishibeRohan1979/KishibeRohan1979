package com.tzp.myWebTest.aop;

import com.tzp.myWebTest.dto.MapCreateDTO;
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
    public MsgUtil<Object> around(ProceedingJoinPoint point) throws Throwable {
        //请求header
//        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //异步消息
//        String id = UUID.randomUUID().toString();
        // 获取方法参数
        Object[] args = point.getArgs();
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("方法参数为空");
        }
        MapCreateDTO dto = null;
        for (Object arg : args) {
            if (arg instanceof MapCreateDTO) {
                dto = (MapCreateDTO) arg;
                break;
            }
        }
        if (dto == null) {
            throw new IllegalArgumentException("方法参数中未找到 MapCreateDTO 类型的参数");
        }
        // 获取 id
        String id = dto.getOid();
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("MapCreateDTO 对象中的 oid 为空");
        }
        AsyncMsgUtil asyncMsg = new AsyncMsgUtil();
        asyncMsg.setId(id);
        //异步执行
        asyncService.async(asyncMsg, point);
        //异步返回值
        return MsgUtil.success("操作成功", asyncMsg);
    }

}
