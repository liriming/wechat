package com.iosre.pphb.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iosre.pphb.dao.WcphoneDao;
import com.iosre.pphb.http.HttpResult;
import com.iosre.pphb.http.HttpService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WcSmsService {
    private final static String USERNAME = "leoroon6";
    private final static String PASSWORD = "ra6ra6ra6";
    private static String TOKEN = "";
    private final static String HOST = "http://kuailezhuan.6yev.com/";
    private final static String US_HOST = "http://47.52.25.159/sms2/api/sms/getByToken?token=";
    private final static String ITEM_ID = "0";
    private static HttpService httpService = new HttpService(30000);
    private static Map<String, String> phoneMsgIdMap = new ConcurrentHashMap<>();
    private static Map<String, Integer> usPhoneMap = new ConcurrentHashMap<>();
    public final static Logger logger = LoggerFactory.getLogger(WcSmsService.class);
    private final static ObjectMapper jsonMapper = new ObjectMapper();

    @Autowired
    private WcphoneDao wcphoneDao;

    static {
        while (StringUtils.isEmpty(TOKEN)) {
            String url = HOST + "Login?User=" + USERNAME + "&Password=" + PASSWORD + "&Logintype=0";
            HttpResult result = httpService.get(url);
            try {
                Map<String, Object> retMsg = jsonMapper.readValue(result.getPayload(), Map.class);
                if (0 == Integer.parseInt(retMsg.get("code").toString())) {
                    Map<String, Object> data = (Map<String, Object>) retMsg.get("data");
                    TOKEN = data.get("Token").toString();
                    logger.info("token:{}", TOKEN);
                } else {
                    logger.info(result.getPayload());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public String phone(HttpServletRequest request, HttpServletResponse response) throws InterruptedException {
        try {
            String url = HOST + "/GetPhoneNumber?Token=" + TOKEN + "&ItemId=" + ITEM_ID + "&Phone=&Operator=0&Developer=";
            HttpResult result = httpService.get(url);
            Map<String, Object> retMsg = jsonMapper.readValue(result.getPayload(), Map.class);

            if (0 == (Integer) retMsg.get("code")) {
                Map<String, Object> data = (Map<String, Object>) retMsg.get("data");
                String phone = data.get("Phone").toString();
                String msgId = data.get("MSGID").toString();
                phoneMsgIdMap.putIfAbsent(phone, msgId);
                return phone;
            } else {
                return retMsg.toString();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "400";
        }
    }


    public String sendCode(HttpServletRequest request, HttpServletResponse response, String phone) throws InterruptedException {

        try {
            if (!phoneMsgIdMap.containsKey(phone)) {
                return "无效号码";
            }
            String msgid = phoneMsgIdMap.get(phone);
            String url = HOST + "/getsendstate?Token=" + TOKEN + "&MSGID=" + msgid;
            HttpResult result = httpService.get(url);
            Map<String, Object> retMsg = jsonMapper.readValue(result.getPayload(), Map.class);
            return retMsg.get("code").toString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "400";
        }

    }

    public String getCode( String phone) {
        try {

            if (!phoneMsgIdMap.containsKey(phone)) {
                return "无效号码";
            }
            String msgid = phoneMsgIdMap.get(phone);
            String url = HOST + "/GetMessage?Token=" + TOKEN + "&MSGID=" + msgid;
            HttpResult result = httpService.get(url);
            Map<String, Object> retMsg = jsonMapper.readValue(result.getPayload(), Map.class);

            if (0 == (Integer) retMsg.get("code")) {
                String data = retMsg.get("data").toString();
                String regEx="[^0-9]";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(data);
                phoneMsgIdMap.remove(phone);
                return m.replaceAll("").trim();
            } else {
                return retMsg.get("code").toString();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "400";
        }
    }

    public Integer exportPhone(String list){
        try {
            logger.info(list.toString());

            List<String> phoneStr = Arrays.asList(list.split(","));
            List<Map<String,String>> phoneList = new ArrayList<>();

            phoneStr.forEach( e ->{
                String[] p = e.split("|");
                Map<String,String> m = new HashMap<>(2);
                m.putIfAbsent("phone", e.trim().substring(0,10));
                m.putIfAbsent("token", e.trim().substring(11,e.trim().length()));

                phoneList.add(m);
            });

            wcphoneDao.insertDataInfo(phoneList);

            return phoneList.size();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return 0;
        }
    }

    public String usPhone(){

        Map<String,Object> phoneMsg = wcphoneDao.getPhoneMsg();

        String phone = phoneMsg.get("phone").toString();
        int id = (Integer)phoneMsg.get("id");
        wcphoneDao.setStatus(id,1);
        return phone;

    }

    public String getUsCode(String phone) throws IOException {

        Map<String, Object> map = wcphoneDao.getToken(phone);

        HttpResult result = httpService.get(US_HOST + map.get("token"));
        Map<String, Object> retMsg = jsonMapper.readValue(result.getPayload(), Map.class);
        int id = (Integer)map.get("id");

        if((Boolean) retMsg.get("flag")) {

            String regEx = "[^0-9]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(result.getPayload());
            wcphoneDao.setStatus(id,2);
            return m.replaceAll("").trim();
        }else{
            if(usPhoneMap.containsKey(phone)){
                int reqCount = usPhoneMap.get(phone);
                if(reqCount > 10){
                    wcphoneDao.setStatus(id,0);
                }else{
                    usPhoneMap.putIfAbsent(phone,reqCount++);
                }
            }else{
                usPhoneMap.putIfAbsent(phone, 1);
            }
            return "400";
        }
    }

}
