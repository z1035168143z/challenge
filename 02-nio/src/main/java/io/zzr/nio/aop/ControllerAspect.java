package io.zzr.nio.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.zzr.nio.utils.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zrzhao
 * @date 2022/6/11
 */
@Aspect
@Slf4j
@Component
public class ControllerAspect {

    /**
     * 横切点
     */
    @Pointcut("execution(public * io.zzr.nio.controller.*.*(..))")
    public void controllerLog() {
    }

    @Around(value = "controllerLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object ob = proceedingJoinPoint.proceed();
        // 接收到请求
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();

        if (proceedingJoinPoint.toShortString().startsWith("FileController")) {
            log.info("[{}]:被调用。入参为:[{}],响应结果:[{}],耗时:[{}]", proceedingJoinPoint.toShortString(), "***",
                    JSONObject.toJsonString(ob), System.currentTimeMillis() - startTime);
        } else {
            log.info("[{}]:被调用。入参为:[{}],响应结果:[{}],耗时:[{}]", proceedingJoinPoint.toShortString(), JSONObject.toJsonString(request.getParameterMap()),
                    JSONObject.toJsonString(ob), System.currentTimeMillis() - startTime);
        }

        return ob;
    }

}
