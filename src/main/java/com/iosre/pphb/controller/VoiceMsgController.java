package com.iosre.pphb.controller;

import com.iosre.pphb.http.HttpResult;
import com.iosre.pphb.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping(value = "/sms/")
public class VoiceMsgController {
    private static String USERNAME = "leoroon6";
    private static String PASSWORD = "sun911224tom520";
    private static String TOKEN = "";
    private static String HOST = "http://47.89.9.52:9180/service.asmx/";
    private static HttpService httpService = new HttpService(30000);
    private static Map<String, String> phoneCodeMap = new ConcurrentHashMap<>();
    public final static Logger logger = LoggerFactory.getLogger(VoiceMsgController.class);

    static {
        while (StringUtils.isEmpty(TOKEN)) {
            String url = HOST + "UserLoginStr?name=" + USERNAME + "&psw=" + PASSWORD;
            HttpResult result = httpService.get(url);
            if (result.getPayload().length() > 5) {
                TOKEN = result.getPayload();
                logger.info(" TOKEN:" + TOKEN);
            }
        }
    }


    @RequestMapping(value = "getHm", method = RequestMethod.GET)
    public String getHm(HttpServletRequest request, HttpServletResponse response) throws InterruptedException {
        try {
            String url = HOST + "/GetHMStr?token=" + TOKEN + "&xmid=30053&sl=1&lx=0&a1=&a2=&pk=";
            logger.info(" 开始获取号码......");
            HttpResult result = httpService.get(url);

            logger.info(result.getPayload());
            if ("-8".equalsIgnoreCase(result.getPayload())) { //冲值
                return "400";
            } else if ("0".equalsIgnoreCase(result.getPayload())) { //重新登陆
                url = HOST + "UserLoginStr?name=" + USERNAME + "&psw=" + PASSWORD;
                result = httpService.get(url);
                if (result.getPayload().length() > 5) {
                    TOKEN = result.getPayload();
                    logger.info(" TOKEN:" + TOKEN);
                    result = httpService.get(url);
                }
            } else if (!result.getPayload().contains("id") && !result.getPayload().contains("hm")) {
                return "401";
            }

            if(result.getPayload().substring(3, result.getPayload().length()).startsWith("17")){
                String phone = result.getPayload().substring(3, result.getPayload().length());
                url = HOST + "/sfHmStr?token=" + TOKEN + "&hm=" + phone;
                logger.info(" 释放号码:" + phone);
                httpService.get(url);
                return "400";
            }

            while (!result.getPayload().contains("hm")) {
                logger.info(" 获取号码失败：" + result.getPayload());
                Thread.sleep(2000);
                String id = result.getPayload().substring(3, result.getPayload().length());
                url = HOST + "/GetTaskStr?token=" + TOKEN + "&id=" + id;
                result = httpService.get(url);
            }

            String phone = result.getPayload().substring(3, result.getPayload().length());
            response.addHeader("Access-Control-Allow-Origin", "*");
            logger.info(" 成功获取号码：" + phone);
            return phone;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "400";
        }
    }

    /**
     * 验证手机号码
     * <p>
     * 移动号码段:139、138、137、136、135、134、150、151、152、157、158、159、182、183、187、188、147
     * 联通号码段:130、131、132、136、185、186、145
     * 电信号码段:133、153、180、189
     *
     * @param cellphone
     * @return
     */
    public static boolean checkCellphone(String cellphone) {
        String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(cellphone);
        return m.matches();
    }


    @RequestMapping(value ="sendYzm/{phone}", method = RequestMethod.GET)
    public String sendYzm(HttpServletRequest request, HttpServletResponse response, @PathVariable("phone") String phone) throws InterruptedException {
        phoneCodeMap.put(phone,"405");
        String url = HOST + "/GetYzmStr?token=" + TOKEN + "&xmid=30053&hm=" + phone;
        logger.info(" 开始获取验证码......" + phone);
        HttpResult result = httpService.get(url);
        int i = 0;
        while (StringUtils.isEmpty(result.getPayload())) {
            Thread.sleep(2000);
            logger.info(" 获取验证码失败：" + result.getPayload() + ",phone:" + phone);
            url = HOST + "/GetYzmLogStr?token=" + TOKEN + "&xmid=30053&hm=" + phone;
            result = httpService.get(url);
        }
        response.addHeader("Access-Control-Allow-Origin", "*");
        logger.info(" 正在获取验证码：" + result.getPayload() + ",phone:" + phone);
        while ("1".equalsIgnoreCase(result.getPayload()) && i <= 35) {
            i++;
            Thread.sleep(2000);
            result = httpService.get(url);
        }
        //异常
        if (
                "0".equalsIgnoreCase(result.getPayload()) || "-1".equalsIgnoreCase(result.getPayload()) || "-2".equalsIgnoreCase(result.getPayload())
                        || "-3".equalsIgnoreCase(result.getPayload()) || "-8".equalsIgnoreCase(result.getPayload()) || "-9".equalsIgnoreCase(result.getPayload())) {
            logger.info(" 获取验证码失败：" + result.getPayload() + ",phone:" + phone);
            phoneCodeMap.remove(phone);
            phoneCodeMap.put(phone,"400");
            return "400";
        } else {
            if ("1".equalsIgnoreCase(result.getPayload())) {
                logger.info(" 获取验证码超时：" + result.getPayload() + ",phone:" + phone);
                url = HOST + "/sfHmStr?token=" + TOKEN + "&hm=" + phone;
                logger.info(" 开始释放号码......");
                HttpResult result1 = httpService.get(url);
                while (!"1".equalsIgnoreCase(result1.getPayload())) {
                    result1 = httpService.get(url);
                }
                phoneCodeMap.remove(phone);
                phoneCodeMap.put(phone,"400");
                return "400";
            } else {
                logger.info(" 成功获取验证码：" + result.getPayload());
                logger.info(" 成功获取验证码：" + result.getPayload().split(" ")[0]);
                phoneCodeMap.put(phone, result.getPayload().split(" ")[0]);
                url = HOST + "/sfHmStr?token=" + TOKEN + "&hm=" + phone;
                logger.info(" 开始释放号码......");
                HttpResult result1 = httpService.get(url);
                while (!"1".equalsIgnoreCase(result1.getPayload())) {
                    result1 = httpService.get(url);
                }
                logger.info(" 释放号码成功");
                return result.getPayload();
            }
        }
    }

    @RequestMapping(value ="getYzm/{phone}", method = RequestMethod.GET)
    public String getYzm(@PathVariable("phone") String phone)  {

        logger.info(phone + ": 请求验证码");
        if(!phoneCodeMap.containsKey(phone)){
            return "400";
        }
        String code = phoneCodeMap.get(phone);
        if(code.length() != 3){
            phoneCodeMap.remove(phone);

        }
        return code;
    }

}
