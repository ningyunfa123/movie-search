package com.movie.search.moviesearch.aop;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class LogAspect {

    @Pointcut("execution(* com.movie.search.moviesearch.app.movie..*(..)) || execution(* com.movie.search.moviesearch.app.index..*(..))")
    public void init(){}

    @Around("init()")
    public Object intercept(ProceedingJoinPoint point) {
        Object[] args = point.getArgs();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        log.info("方法名：{}, 参数：{}", method.getName(), JSONObject.toJSONString(args, SerializerFeature.WriteMapNullValue));
        Long start = System.currentTimeMillis();
        Object ret = null;
        try {
            ret = point.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            log.error(throwable.getMessage());
        }
        Long timeConsume = System.currentTimeMillis()-start;
        log.info("返回值：{},耗时:{}", JSONObject.toJSONString(ret, SerializerFeature.WriteMapNullValue), timeConsume);
        return ret;
    }
}

