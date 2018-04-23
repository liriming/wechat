package com.iosre.pphb.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iosre.pphb.dao.DictionaryDao;
import com.iosre.pphb.dao.RealnameDao;
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
import java.net.URLEncoder;
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
    // private final static String US_HOST = "http://47.52.25.159/sms2/api/sms/getByToken?token=";
//    private final static String US_HOST = "http://47.96.24.143/sms_wx/api/sms/getByToken?token=";
//    private final static String US_HOST = "http://47.96.24.143/sms2/api/sms/getByToken?token=";
    private final static String US_HOST = "http://47.96.24.143/sms2/api/sms/getByToken?token=";
    private final static String US_HOST1 = "http://118.24.62.102/tp5/public/?key=";
    private final static String US_HOST_GSIM = "https://gsim.online/api/";
    //    private final static String US_HOST = "http://47.52.63.207/sms_wx/api/sms/getByToken?token=";/**/
    private final static String ITEM_ID = "0";
    private static HttpService httpService = new HttpService(300000);
    private static Map<String, String> phoneMsgIdMap = new ConcurrentHashMap<>();
    private static Map<String, Integer> usPhoneMap = new ConcurrentHashMap<>();
    public final static Logger logger = LoggerFactory.getLogger(WcSmsService.class);
    private final static ObjectMapper jsonMapper = new ObjectMapper();

    @Autowired
    private WcphoneDao wcphoneDao;
    @Autowired
    private WcuserDao wcuserDao;
    @Autowired
    private RealnameDao realnameDao;
    @Autowired
    private DictionaryDao dictionaryDao;

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

    public Integer exportPhone1(String list, String server) {
        try {

            List<String> phoneStr = Arrays.asList(list.split(","));
            List<Map<String, String>> phoneList = new ArrayList<>();

            phoneStr.forEach(e -> {
                Map<String, String> m = new HashMap<>(2);
                m.putIfAbsent("phone", e.trim());
                m.putIfAbsent("token", server);

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
        if (phoneMsg  == null){
            return "400";
        }
        String phone = phoneMsg.get("phone").toString();
        int id = (Integer) phoneMsg.get("id");
        wcphoneDao.setStatus(id, 1);
        return phone;

    }

    public String usPhone1() {

        Map<String, Object> phoneMsg = wcphoneDao.getPhoneMsg();
        if (phoneMsg  == null){
            return "400";
        }
        String phone = phoneMsg.get("phone").toString();
        int id = (Integer) phoneMsg.get("id");
        wcphoneDao.setStatus(id, 1);
        return phone.substring(1, phone.length());

    }

    public String gSimPhone() {
        try {
            String key = dictionaryDao.getValueByName("gsim_key");

            if (key.contains(",")) {
                String[] keyAry = key.split(",");
                int index = (int) (Math.random() * keyAry.length);
                key = keyAry[index];
            }

            HttpResult result = httpService.get(US_HOST_GSIM + "getNumber/" + key);
            logger.info(result.getPayload());
            if (result.getPayload().contains("invalid parameter!")) {
                return "400";
            }
            Map<String, Object> retMsg = jsonMapper.readValue(result.getPayload(), Map.class);
            if (retMsg.containsKey("number")) {
                String phone = retMsg.get("number").toString();
                wcphoneDao.insertGsimPhone(phone, key, 440);
                return phone.substring(2);
            }
            return "400";
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return "400";
        }
    }

    public void sendGsimCode(String phone) {
        try {
            String token = wcphoneDao.getTokenByPhone("44" + phone);
            wcphoneDao.setStatusByPhone("44" + phone, 446);
            HttpResult result = httpService.get(US_HOST_GSIM + "sendSms/" + token + "/44" + phone);
            logger.info(result.getPayload());
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
        }
    }


    public String getGsimCode(String phone) throws IOException {

        String token = wcphoneDao.getTokenByPhone("44" + phone);
        HttpResult result = httpService.get(US_HOST_GSIM + "getMessage/" + token + "/44" + phone);
        logger.info(result.getPayload());
        if (result.getPayload().contains("invalid parameter!")) {
            return "400";
        }
        Map<String, Object> retMsg = jsonMapper.readValue(result.getPayload(), Map.class);

        if (retMsg.containsKey("message") && !retMsg.get("message").toString().contains("提醒")) {
            wcphoneDao.setStatusByPhone("44" + phone, 447);
            String regEx = "[^0-9]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(retMsg.get("message").toString());
            return m.replaceAll("").trim();
        } else {
            /*if (usPhoneMap.containsKey(phone)) {
                int reqCount = usPhoneMap.get(phone);
                if (reqCount > 60) {
                    result = httpService.get(US_HOST_GSIM + "refund/" + token + "/44" + phone);
                    logger.info(result.getPayload());
                } else {
                    reqCount = reqCount + 1;
                    usPhoneMap.put(phone, reqCount);
                }
            } else {
                usPhoneMap.putIfAbsent(phone, 1);
            }*/
            return "400";
        }
    }


    public String getUsCode1(String phone) {

        try {

            Map<String, Object> map = wcphoneDao.getToken("1" + phone);

            String token = URLEncoder.encode(map.get("token").toString() + "|1" + phone, "UTF-8");
            logger.info(US_HOST1 + token);

            HttpResult result = httpService.get(US_HOST1 + token);
            logger.info(result.getPayload());
            if (result.getPayload().contains("invalid parameter!")) {
                return "400";
            }
            Map<String, Object> retMsg = jsonMapper.readValue(result.getPayload(), Map.class);
            int id = (Integer) map.get("id");

            if (retMsg.containsKey("Message") && !retMsg.get("Message").toString().contains("提醒")) {

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

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "400";
        }
    }

    public String getUsCode(String phone) throws IOException {

        Map<String, Object> map = wcphoneDao.getToken(phone);

        HttpResult result = httpService.get(US_HOST + map.get("token"));
        Map<String, Object> retMsg = jsonMapper.readValue(result.getPayload(), Map.class);
        int id = (Integer) map.get("id");

        if ((retMsg.containsKey("flag") && (Boolean) retMsg.get("flag")) || (retMsg.containsKey("msg") && !retMsg.get("msg").toString().contains("提醒"))) {

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

    public void uploadRealName(String ip, String data) {
        String[] datas = data.split(",");
        String phone = datas[0];
        String rname = datas[1];
        String rcard = datas[2];
        realnameDao.insertDataInfo(phone, rname, rcard);

    }

    public void uploadCountTime() {
        dictionaryDao.updateValueByName("start_count_time", XDateUtils.nowToString());
    }

    public void uploadData(String ip, String data) {

        String[] datas = data.split(",");
        String phone = datas[0];
        String psw = datas[1];
        String d62 = datas[2];
        String phoneno = datas[3];
        Integer isalive = Integer.parseInt(datas[4]);
        Integer real = 0;
        String country = "";
        if (datas.length == 6) {
            country = datas[5];
        }

        if (!psw.equalsIgnoreCase("ra123456")) {
            wcuserDao.insertDataInfo(phone, "ra123456", d62, phoneno, isalive, ip, 1, psw, "", country);
        } else {
            wcuserDao.insertDataInfo(phone, psw, d62, phoneno, isalive, ip, real, "", "", country);
        }


        if (StringUtils.isEmpty(d62) && "英国".equalsIgnoreCase(country)) {
            String key = wcphoneDao.getTokenByPhone("44" + phone);
            HttpResult result = httpService.get(US_HOST_GSIM + "block/" + key + "/44" + phone);
            logger.info(result.getPayload());
            result = httpService.get(US_HOST_GSIM + "refund/" + key + "/44" + phone);
            logger.info(result.getPayload());
        }

    }

    public String exportData(HttpServletResponse response, int count, String psw, int type) {
        try {
            if (!psw.equals("liriming221")) {
                return "密码错误";
            }
            String listorder;
            String sDate;
            String eDate;
            int realname = 0;
            //当天号
            if (type % 2 != 0) {
                listorder = "DESC";
                //开始时间：当前时间-1天 结束时间：当前时间
                sDate = XDateUtils.timestampToString((System.currentTimeMillis() - 24 * 60 * 60 * 1000) / 1000, XDateUtils.DatePattern.DATE_TIME.getPattern());
                eDate = XDateUtils.nowToString();
            }
            //隔天号
            else {
                listorder = "ASC";
                //开始时间：历史时间 结束时间：当前时间 -1天
                sDate = "0";
                eDate = XDateUtils.timestampToString((System.currentTimeMillis() - 24 * 60 * 60 * 1000) / 1000, XDateUtils.DatePattern.DATE_TIME.getPattern());
            }
            if (type > 2) {
                realname = 1;
            }
            List<Map<String, Object>> exportData = wcuserDao.getExportData(count, sDate, eDate, listorder, realname);
            if (exportData.size() == 0) {
                return "没有数据了!";
            }

            List<String> data = new ArrayList<>(exportData.size());
            List<Integer> exportId = new ArrayList<>(exportData.size());

            exportData.forEach(e -> {
                String msg = e.get("name") + "----" + e.get("psw") + "----" + e.get("_62");
                data.add(msg);
                if (!StringUtils.isEmpty(e.get("id"))) {
                    exportId.add((Integer) e.get("id"));
                }
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


            String time = dictionaryDao.getValueByName("start_count_time");
            //昨天数据
            Map<String, Object> minMap = wcuserDao.getMsg(time, tomorrow);
            map.putIfAbsent("minTotal", minMap.get("total"));
            map.putIfAbsent("minAlive", minMap.get("alive"));
            map.putIfAbsent("minReal", minMap.get("realn"));
            map.putIfAbsent("minNReal", minMap.get("nrealn"));
            map.putIfAbsent("minDead", minMap.get("nalive"));
            map.putIfAbsent("minSup", minMap.get("sup"));
            map.putIfAbsent("countTime", time);

            map.putIfAbsent("resPhoneNum", wcphoneDao.resPhoneNum());

            String sDate = XDateUtils.timestampToString((System.currentTimeMillis() - 24 * 60 * 60 * 1000) / 1000, XDateUtils.DatePattern.DATE_TIME.getPattern());
            String eDate = XDateUtils.nowToString();
            int todayWhiteNum_US = wcuserDao.getExportDataNum(sDate, eDate, 0,"1%");
            int todayRealNum_US = wcuserDao.getExportDataNum(sDate, eDate, 1,"1%");
            int todayWhiteNum_UK = wcuserDao.getExportDataNum(sDate, eDate, 0,"4%");
            int todayRealNum_UK = wcuserDao.getExportDataNum(sDate, eDate, 1,"4%");
            int todayWhiteNum_Other = wcuserDao.getExportDataNum(sDate, eDate, 0,"%");
            int todayRealNum_Other = wcuserDao.getExportDataNum(sDate, eDate, 1,"%");

            map.putIfAbsent("todayWhiteNum", "美国:" + todayWhiteNum_US + " 英国:" + todayWhiteNum_UK + " 其他:" + (todayWhiteNum_Other -todayWhiteNum_US - todayWhiteNum_UK) );
            map.putIfAbsent("todayRealNum", "美国:" +todayRealNum_US + " 英国:" + todayRealNum_UK + " 其他:" + (todayRealNum_Other - todayRealNum_US - todayRealNum_UK));

            sDate = "0";
            eDate = XDateUtils.timestampToString((System.currentTimeMillis() - 24 * 60 * 60 * 1000) / 1000, XDateUtils.DatePattern.DATE_TIME.getPattern());
            int yesWhiteNum_US = wcuserDao.getExportDataNum(sDate, eDate, 0,"1%");
            int yesRealNum_US = wcuserDao.getExportDataNum(sDate, eDate, 1,"1%");
            int yesWhiteNum_UK = wcuserDao.getExportDataNum(sDate, eDate, 0,"4%");
            int yesRealNum_UK = wcuserDao.getExportDataNum(sDate, eDate, 1,"4%");
            int yesWhiteNum_other = wcuserDao.getExportDataNum(sDate, eDate, 0,"%");
            int yesRealNum_other = wcuserDao.getExportDataNum(sDate, eDate, 1,"%");

            map.putIfAbsent("yesWhiteNum", "美国:" + yesWhiteNum_US+ " 英国:" + yesWhiteNum_UK + " 其他:" + (yesWhiteNum_other - yesWhiteNum_US- yesWhiteNum_UK));
            map.putIfAbsent("yesRealNum", "美国:" + yesRealNum_US+ " 英国:" + yesRealNum_UK + " 其他:" + (yesRealNum_other- yesRealNum_UK - yesRealNum_US));

            return map;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return map;
        }
    }

    public void noRevcMsg(String phone, String country) {

        if ("英国".equalsIgnoreCase(country)) {
            String token = wcphoneDao.getTokenByPhone("44" + phone);
            HttpResult result = httpService.get(US_HOST_GSIM + "block/" + token + "/44" + phone);
            logger.info(result.getPayload());
            result = httpService.get(US_HOST_GSIM + "refund/" + token + "/44" + phone);
            logger.info(result.getPayload());
            wcphoneDao.setStatusByPhone("44" + phone, -441);
        } else {
            wcphoneDao.setStatusByPhone(phone, -1);
        }
    }

    public void isalive(Integer type, String phone) {
        wcuserDao.updatePhoIsalive(phone, type);
    }

    public String getNoCheckPho(int type) {

        if (type > 4) {
            return "类型错误";
        }
        String phone = "";

        String sDate;
        String eDate;
        String listorder;
        int realname = 0;
        //当天号
        if (type % 2 != 0) {
            listorder = "DESC";
            //开始时间：当前时间-1天 结束时间：当前时间
            sDate = XDateUtils.timestampToString((System.currentTimeMillis() - 24 * 60 * 60 * 1000) / 1000, XDateUtils.DatePattern.DATE_TIME.getPattern());
            eDate = XDateUtils.nowToString();
        }
        //隔天号
        else {
            listorder = "ASC";
            //开始时间：历史时间 结束时间：当前时间 -1天
            sDate = "0";
            eDate = XDateUtils.timestampToString((System.currentTimeMillis() - 24 * 60 * 60 * 1000) / 1000, XDateUtils.DatePattern.DATE_TIME.getPattern());
        }
        if (type > 2) {
            realname = 1;
        }
        phone = wcuserDao.getNoCheckPho(sDate, eDate, listorder, realname);
        wcuserDao.updateNoCheckPho(phone);
        return phone;
    }


    public String getGZHH() {
        String gzhh = dictionaryDao.getValueByName("gzhh");

        if (StringUtils.isEmpty(gzhh)) {
            return "400";
        }
        return gzhh;
    }

    public String getCountry() {
        String country = dictionaryDao.getValueByName("country");

        if (StringUtils.isEmpty(country)) {
            return "400";
        }
        return country;
    }

    public String checkPhone(String phone) {
        String result = wcuserDao.checkPhone(phone);

        if (StringUtils.isEmpty(result)) {
            return "400";
        }
        else if (!"0".equalsIgnoreCase(result) && result.startsWith("1")){
            return "1";
        }else {
            return result;
        }
    }


}
