package com.iosre.pphb.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iosre.pphb.dto.UserOpLog;
import com.iosre.pphb.service.LogServiceImpl;
import com.iosre.pphb.util.WebUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 拦截器：记录用户操作日志，检查用户是否登录
 *
 * @author XuJijun
 */
@Aspect
@Component
public class ControllerMethodInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(ControllerMethodInterceptor.class);
    private final static ObjectMapper jsonMapper = new ObjectMapper();
    @Autowired
    private LogServiceImpl logService;

    /**
     * 定义拦截规则
     */
    @Pointcut("execution(* com.iosre.pphb.controller..*(..)) && @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void controllerMethodPointcut() {
    }

    /**
     * 拦截器具体实现
     *
     * @param pjp
     * @return
     */
    @Around("controllerMethodPointcut()")
    public Object Interceptor(ProceedingJoinPoint pjp) {
        long beginTime = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        String methodName = method.getName();
        String controller = method.getDeclaringClass().getName();
        logger.info("请求开始，方法：{}", methodName);

        Set<Object> allParams = new LinkedHashSet<>();
        Object result = null;
        UserOpLog userOpLog = new UserOpLog();
        userOpLog.setMethod(methodName);
        userOpLog.setCtime(beginTime / 1000);
        userOpLog.setController(controller);

        //处理参数列表：
        Object[] args = pjp.getArgs();
        for (Object arg : args) {
            if (arg instanceof String) {
                if(((String) arg).length() >= 100) {
                    try {
                        String deuri = URLDecoder.decode((String) arg, "UTF-8");
                        logger.info(deuri.length() + "");
                        Map map = jsonMapper.readValue(deuri, Map.class);
                        allParams.add(map);
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }else{
                    allParams.add(arg);
                }
            } else if (arg instanceof HttpServletRequest) {
                HttpServletRequest request = (HttpServletRequest) arg;
                //获取请求方的IP地址到log中
                String ip = WebUtil.getLocalIp(request);
                userOpLog.setIp(ip);

                //获取url到log中：
                userOpLog.setUrl(request.getRequestURL().toString());

                //获取query string 或 posted form data参数
                Map<String, String[]> paramMap = ((HttpServletRequest) arg).getParameterMap();
                if (paramMap != null && paramMap.size() > 0) {
                    allParams.add(paramMap);
                }
            }
        }

        try {
            if(result == null) {
                result = pjp.proceed();
            }
        } catch (Throwable e) {
            logger.info("exception: ", e);
            result = "发生异常：" + e.getMessage();
        }

        userOpLog.setResult(Optional.ofNullable(result).orElse("").toString());
        userOpLog.setAllParams(allParams);
        userOpLog.setCostMs(System.currentTimeMillis() - beginTime);
        logService.saveUserOpLog(userOpLog, "ppMgrOpLog");

        return result;
    }

}
