package com.iosre.pphb.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iosre.pphb.dao.WcphoneDao;
import com.iosre.pphb.dao.WcuserDao;
import com.iosre.pphb.http.HttpResult;
import com.iosre.pphb.http.HttpService;
import com.iosre.pphb.util.FileUtils;
import com.iosre.pphb.util.XDateUtils;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.collections.map.HashedMap;
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
import java.text.SimpleDateFormat;
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
//    private final static String US_HOST = "http://47.52.25.159/sms2/api/sms/getByToken?token=";
    private final static String US_HOST = "http://47.96.24.143/sms_wx/api/sms/getByToken?token=";
    private final static String ITEM_ID = "0";
    private static HttpService httpService = new HttpService(30000);
    private static Map<String, String> phoneMsgIdMap = new ConcurrentHashMap<>();
    private static Map<String, Integer> usPhoneMap = new ConcurrentHashMap<>();
    public final static Logger logger = LoggerFactory.getLogger(WcSmsService.class);
    private final static ObjectMapper jsonMapper = new ObjectMapper();

    @Autowired
    private WcphoneDao wcphoneDao;
    @Autowired
    private WcuserDao wcuserDao;

   /* static {
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
    }*/


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

    public String getCode(String phone) {
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
                String regEx = "[^0-9]";
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

    public Integer exportPhone(String list) {
        try {
            logger.info(list.toString());

            List<String> phoneStr = Arrays.asList(list.split(","));
            List<Map<String, String>> phoneList = new ArrayList<>();

            phoneStr.forEach(e -> {
                String[] p = e.split("|");
                Map<String, String> m = new HashMap<>(2);
                m.putIfAbsent("phone", e.trim().substring(0, 10));
                m.putIfAbsent("token", e.trim().substring(11, e.trim().length()));

                phoneList.add(m);
            });

            wcphoneDao.insertDataInfo(phoneList);

            return phoneList.size();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return 0;
        }
    }

    public String usPhone() {

        Map<String, Object> phoneMsg = wcphoneDao.getPhoneMsg();

        String phone = phoneMsg.get("phone").toString();
        int id = (Integer) phoneMsg.get("id");
        wcphoneDao.setStatus(id, 1);
        return phone;

    }

    public String getUsCode(String phone) throws IOException {

        Map<String, Object> map = wcphoneDao.getToken(phone);

        HttpResult result = httpService.get(US_HOST + map.get("token"));
        Map<String, Object> retMsg = jsonMapper.readValue(result.getPayload(), Map.class);
        int id = (Integer) map.get("id");

        if ((Boolean) retMsg.get("flag")) {

            String regEx = "[^0-9]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(result.getPayload());
            wcphoneDao.setStatus(id, 2);
            return m.replaceAll("").trim();
        } else {
            if (usPhoneMap.containsKey(phone)) {
                int reqCount = usPhoneMap.get(phone);
                if (reqCount > 10) {
                    wcphoneDao.setStatus(id, 0);
                } else {
                    usPhoneMap.putIfAbsent(phone, reqCount++);
                }
            } else {
                usPhoneMap.putIfAbsent(phone, 1);
            }
            return "400";
        }
    }

    public void realName(String data) {

        String[] datas = data.split(",");
        String phone = datas[0];
        String nema = datas[1];
        String id = datas[2];

        wcuserDao.updateRealName(phone, nema,id);

    }

    public void uploadData(String ip, String data) {

        String[] datas = data.split(",");
        String phone = datas[0];
        String psw = datas[1];
        String d62 = datas[2];
        String phoneno = datas[3];
        Integer isalive = Integer.parseInt(datas[4]);
        Integer real = 0;
        String rname = "";
        String rcard = "";
        if(datas.length == 7){
            real = 1;
            rname = datas[5];
            rcard = datas[6];
        }

        wcuserDao.insertDataInfo(phone, psw, d62, phoneno, isalive, ip,real,rname,rcard);

    }

    public String exportData(HttpServletResponse response, int count, String psw) {
        try {
            if (!psw.equals("ra6ra6ra6")) {
                return "密码错误";
            }
            List<Map<String, Object>> exportData = wcuserDao.getExportData(count);

            List<String> data = new ArrayList<>(exportData.size());
            List<Integer> exportId = new ArrayList<>(exportData.size());

            exportData.forEach(e -> {
                String msg = e.get("name") + "----" + e.get("psw") + "----" + e.get("_62");
                data.add(msg);
                exportId.add((Integer) e.get("id"));
            });
            wcuserDao.updateExportStatus(exportId);
            FileUtils.writeToTxt(response, data);
            return "";
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return e.getMessage();
        }
    }

    public Map<String, Object> statistics() {
        Map<String, Object> map = new HashedMap();
        try {

            String today = XDateUtils.dateToString(XDateUtils.getDayBegin(), XDateUtils.DatePattern.DATE_ONLY.getPattern());

            String tomorrow = XDateUtils.dateToString(XDateUtils.getBeginDayOfTomorrow(), XDateUtils.DatePattern.DATE_ONLY.getPattern());

            String yesterday = XDateUtils.dateToString(XDateUtils.getBeginDayOfYesterday(), XDateUtils.DatePattern.DATE_ONLY.getPattern());



            //历史数据
            String hisSdate = "0";
            Map<String, Object> hisMap = wcuserDao.getMsg(hisSdate, tomorrow);
            map.putIfAbsent("hisTotal", hisMap.get("total"));
            map.putIfAbsent("hisAlive", hisMap.get("alive"));
            map.putIfAbsent("hisStock", hisMap.get("stock"));
            map.putIfAbsent("hisReal", hisMap.get("realn"));
            map.putIfAbsent("hisNReal", hisMap.get("nrealn"));
            map.putIfAbsent("hisDead", hisMap.get("nalive"));
            map.putIfAbsent("hisSup", hisMap.get("sup"));
            List<String> hisGzhh = wcuserDao.gzhh(hisSdate, tomorrow);
            Map<String, Integer> gzhhNum = new HashedMap();
            hisGzhh.forEach((String e) -> {
                List gs = Arrays.asList(e.split(","));
                gs.forEach((Object a) -> {
                    if (gzhhNum.containsKey(a)) {
                        int num = gzhhNum.get(a) + 1;
                        gzhhNum.putIfAbsent(a.toString(), num);
                    } else if (!StringUtils.isEmpty(a)) {
                        gzhhNum.putIfAbsent(a.toString(), 1);
                    }
                });
            });
            String gzhhStr = "";
            for (String key : gzhhNum.keySet()) {
                gzhhStr = gzhhStr + key + ":" + gzhhNum.get(key) + "<br>";
            }
            map.putIfAbsent("hisG", gzhhStr);


            //今天数据
            Map<String, Object> todayMap = wcuserDao.getMsg(today, tomorrow);
            map.putIfAbsent("todayTotal", todayMap.get("total"));
            map.putIfAbsent("todayAlive", todayMap.get("alive"));
            map.putIfAbsent("todayStock", todayMap.get("stock"));
            map.putIfAbsent("todayReal", todayMap.get("realn"));
            map.putIfAbsent("todayNReal", todayMap.get("nrealn"));
            map.putIfAbsent("todayDead", todayMap.get("nalive"));
            map.putIfAbsent("todaySup", todayMap.get("sup"));
            List<String> todayGzhh = wcuserDao.gzhh(today, tomorrow);
            Map<String, Integer> todayNum = new HashedMap();
            todayGzhh.forEach(e -> {
                List gs = Arrays.asList(e.split(","));
                gs.forEach(a -> {
                    if (todayNum.containsKey(a)) {
                        int num = todayNum.get(a) + 1;
                        todayNum.putIfAbsent(a.toString(), num);
                    } else if (!StringUtils.isEmpty(a)) {
                        todayNum.putIfAbsent(a.toString(), 1);
                    }
                });
            });
            String todayStr = "";
            for (String key : todayNum.keySet()) {
                todayStr = todayStr + key + ":" + todayNum.get(key) + "<br>";
            }
            map.putIfAbsent("todayG", todayStr);

            //昨天数据
            Map<String, Object> yesMap = wcuserDao.getMsg(yesterday, today);
            map.putIfAbsent("yesTotal", yesMap.get("total"));
            map.putIfAbsent("yesAlive", yesMap.get("alive"));
            map.putIfAbsent("yesStock", yesMap.get("stock"));
            map.putIfAbsent("yesReal", yesMap.get("realn"));
            map.putIfAbsent("yesNReal", yesMap.get("nrealn"));
            map.putIfAbsent("yesDead", yesMap.get("nalive"));
            map.putIfAbsent("yesSup", yesMap.get("sup"));
            List<String> yesGzhh = wcuserDao.gzhh(yesterday, today);
            Map<String, Integer> yesNum = new HashedMap();
            yesGzhh.forEach(e -> {
                List gs = Arrays.asList(e.split(","));
                gs.forEach(a -> {
                    if (yesNum.containsKey(a)) {
                        int num = yesNum.get(a) + 1;
                        yesNum.putIfAbsent(a.toString(), num);
                    } else if (!StringUtils.isEmpty(a)) {
                        yesNum.putIfAbsent(a.toString(), 1);
                    }
                });
            });
            String yesStr = "";
            for (String key : yesNum.keySet()) {
                yesStr = yesStr + key + ":" + yesNum.get(key) + "<br>";
            }
            map.putIfAbsent("yesG", yesStr);


            String min = XDateUtils.timestampToString((System.currentTimeMillis() - 20 * 60 * 1000) / 1000, XDateUtils.DatePattern.DATE_TIME.getPattern());
            //昨天数据
            Map<String, Object> minMap = wcuserDao.getMsg(min, tomorrow);
            map.putIfAbsent("minTotal", minMap.get("total"));
            map.putIfAbsent("minAlive", minMap.get("alive"));
            map.putIfAbsent("minReal", minMap.get("realn"));
            map.putIfAbsent("minNReal", minMap.get("nrealn"));
            map.putIfAbsent("minDead", minMap.get("nalive"));
            map.putIfAbsent("minSup", minMap.get("sup"));

            return map;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return map;
        }
    }


}
