package com.mental.health.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mental.health.entity.SysLog;
import com.mental.health.mapper.SysLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 操作日志切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final SysLogMapper sysLogMapper;
    private final ObjectMapper objectMapper;

    /**
     * 定义切点：所有Controller层的方法
     */
    @Pointcut("execution(* com.mental.health.controller..*.*(..))")
    public void controllerPointcut() {
    }

    /**
     * 环绕通知：记录操作日志
     */
    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        
        // 执行方法
        Object result = point.proceed();
        
        // 计算执行时间
        long executeTime = System.currentTimeMillis() - startTime;
        
        // 异步保存日志
        if (request != null) {
            saveLog(point, request, executeTime);
        }
        
        return result;
    }

    /**
     * 保存日志
     */
    private void saveLog(ProceedingJoinPoint point, HttpServletRequest request, long executeTime) {
        try {
            SysLog sysLog = new SysLog();
            
            // 获取用户ID（从请求头）
            String userIdStr = request.getHeader("userId");
            if (userIdStr != null && !userIdStr.isEmpty()) {
                sysLog.setUserId(Long.parseLong(userIdStr));
            }
            
            // 获取方法信息
            MethodSignature signature = (MethodSignature) point.getSignature();
            String className = point.getTarget().getClass().getName();
            String methodName = signature.getName();
            
            // 设置日志信息
            sysLog.setOperation(className + "." + methodName);
            sysLog.setMethod(request.getMethod() + " " + request.getRequestURI());
            
            // 获取请求参数
            Object[] args = point.getArgs();
            if (args != null && args.length > 0) {
                try {
                    String params = objectMapper.writeValueAsString(args);
                    // 限制参数长度
                    if (params.length() > 2000) {
                        params = params.substring(0, 2000) + "...";
                    }
                    sysLog.setParams(params);
                } catch (Exception e) {
                    sysLog.setParams("参数序列化失败");
                }
            }
            
            sysLog.setIp(getIpAddress(request));
            sysLog.setExecuteTime((int) executeTime);
            sysLog.setCreatedAt(LocalDateTime.now());
            
            // 保存日志
            sysLogMapper.insert(sysLog);
            
        } catch (Exception e) {
            log.error("保存操作日志失败", e);
        }
    }

    /**
     * 获取IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
