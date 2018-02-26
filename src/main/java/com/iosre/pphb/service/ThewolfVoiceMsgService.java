package com.iosre.pphb.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iosre.pphb.http.HttpResult;
import com.iosre.pphb.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ThewolfVoiceMsgService {
    private final static String USERNAME = "leoroon6";
    private final static String PASSWORD = "sun911224tom520";
    private static String TOKEN = "";
    private static HttpService httpService = new HttpService(30000);
    private static Map<String, String> phoneCodeMap = new ConcurrentHashMap<>();
    public final static Logger logger = LoggerFactory.getLogger(ThewolfVoiceMsgService.class);
    private final static  ObjectMapper jsonMapper = new ObjectMapper();

    static {
        while (StringUtils.isEmpty(TOKEN)) {
            String url = "http://api.yyyzmpt.com/index.php/reg/login?username=" + USERNAME + "&password=" + PASSWORD;
            HttpResult result = httpService.get(url);
            try {
                logger.info(result.getPayload());
                Map<String,Object> retMsg = jsonMapper.readValue(result.getPayload(), Map.class);

                while (18 == Integer.parseInt(retMsg.get("errno").toString())){
                    Thread.sleep(5000);
                    result = httpService.get(url);
                    retMsg = jsonMapper.readValue(result.getPayload(), Map.class);
                }

                Map<String,Object> retNo = (Map<String, Object>) retMsg.get("ret");

                TOKEN = retNo.get("token").toString();

                logger.info("thewolf token:{}", TOKEN);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @Scheduled(cron="0/60 * *  * * ? ")
    public void sendToken(){
        String url = "http://api.yyyzmpt.com/index.php/token?token" + TOKEN;
        httpService.get(url);
    }

    public String getHm(HttpServletRequest request, HttpServletResponse response) throws InterruptedException {
        try {
            String url = "http://api.yyyzmpt.com/index.php/clients/online/setWork?token=" + TOKEN + "&pid=105&t=2&paichu=170&username=3320587356";
            logger.info(" 开始获取号码......");
            HttpResult result = httpService.get(url);

            Map<String,Object> retMsg = jsonMapper.readValue(result.getPayload(), Map.class);

            if (Integer.parseInt(retMsg.get("errno").toString()) > 0){
                return retMsg.get("errmsg").toString();
            }else{
                Map<String,Object> retNo = (Map<String, Object>) retMsg.get("ret");
                String phone = retNo.get("number").toString();
                logger.info(" 成功获取号码：" + phone);
                return phone;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "400";
        }
    }


    public String sendYzm(HttpServletRequest request, HttpServletResponse response, @PathVariable("phone") String phone) throws InterruptedException {
        try {
            phoneCodeMap.put(phone, "405");
            String url = "http://api.yyyzmpt.com/index.php/clients/sms/getVoiceSms?token=" + TOKEN + "&project=105&number=" + phone + "&type=3&jmp=2";
            HttpResult result = httpService.get(url);

            Map<String, Object> retMsg = jsonMapper.readValue(result.getPayload(), Map.class);

            if (Integer.parseInt(retMsg.get("errno").toString()) > 0) {
                return retMsg.get("errmsg").toString();
            }else {

                Map<String,Object> retNo = (Map<String, Object>) retMsg.get("ret");
                phoneCodeMap.put(phone, retNo.get("content").toString());

                url = "http://api.yyyzmpt.com/index.php/clients/online/completeWork?token=" + TOKEN + "&number=" + phone;
                logger.info(" 开始释放号码......");
                httpService.get(url);
                logger.info(" 释放号码成功");
                return retNo.get("content").toString();
            }
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            return "400";
        }
    }

    public String getYzm(@PathVariable("phone") String phone) {
        logger.info(phone + ": 请求验证码");
        if (!phoneCodeMap.containsKey(phone)) {
            return "400";
        }
        String code = phoneCodeMap.get(phone);
        if (code.length() != 3) {
            phoneCodeMap.remove(phone);

        }
        return code;
    }

}
