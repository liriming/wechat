/*
package com.iosre.pphb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iosre.pphb.dao.DictionaryDao;
import com.iosre.pphb.dao.Log1Dao;
import com.iosre.pphb.dao.TaskDao;
import com.iosre.pphb.dao.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


@RestController
@RequestMapping("/plugin/")
public class PPHBController {

    public final static Logger logger = LoggerFactory.getLogger(PPHBController.class);
    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private static Integer NUMBER = 0;

    @Autowired
    private UserDao userDao;
    @Autowired
    private DictionaryDao dictionaryDao;
    @Autowired
    private Log1Dao log1Dao;
    @Autowired
    private TaskDao taskDao;


    @RequestMapping(value = "uploadDecrypt", method = RequestMethod.GET)
    public void uploadDecrypt(@RequestParam(value = "val") String val) {
        if (val.length() == 15) {
            taskDao.insertTask1(val);
            logger.info(val);
        }
        //logger.info(val);
    }


    @RequestMapping(value = "sWitch", method = RequestMethod.GET)
    public Boolean sWitch(@RequestParam(value = "content") String content) {
        if (content.startsWith("com.")) {

            if (content.contains(":")) {
                String[] logs = content.split(":");

                log1Dao.insertLog(logs[0], logs[1]);
            } else {
                String username = "";
                log1Dao.insertLog(content, username);
            }

            NUMBER++;
            logger.info("req:{},content:{}", NUMBER, content);
        }
        //logger.info(content);

        return true;
    }


    @RequestMapping(value = "getLuminance", method = RequestMethod.GET)
    public Double getLuminance(@RequestParam(value = "content") String content) {
        try {
            if (content.contains("idfv")) {
                Map<String, Object> contentMap = jsonMapper.readValue(content, Map.class);

                if (contentMap.containsKey("luminance")) {
                    Double luminance = Double.parseDouble(contentMap.get("luminance").toString());

                    Random r = new Random();

                    Double retval = luminance + (r.nextInt(1000) - r.nextInt(1000));
                    //logger.info("idfv:{},luminance:{}",contentMap.get("idfv"),retval);
                    if (retval <= 0) {
                        return 230521.8279361725;
                    }
                    return retval;

                }

                return 230521.8279361725;
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return 230521.8279361725;
        }
        return 230521.8279361725;
    }


    @RequestMapping(value = "getBattery", method = RequestMethod.GET)
    public Integer getBattery(@RequestParam(value = "content") String content) {
        try {
            if (content.contains("idfv")) {
                Map<String, Object> contentMap = jsonMapper.readValue(content, Map.class);

                if (contentMap.containsKey("battery_cap_left")) {
                    Double battery = Double.parseDouble(contentMap.get("battery_cap_left").toString());

                    Random r = new Random();

                    Integer retval = battery.intValue() + (r.nextInt(2) - r.nextInt(2));
                    return retval;
                }
                return 27;
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return 27;
        }
        return 27;
    }


    @RequestMapping(value = "getDiskId", method = RequestMethod.GET)
    public String getDiskId(@RequestParam(value = "content") String content) {
        try {
            if (content.contains("idfv")) {
                Map<String, Object> contentMap = jsonMapper.readValue(content, Map.class);
                if (!StringUtils.isEmpty(contentMap.get("idfv").toString())) {
                    String idfv = contentMap.get("idfv").toString();

                    String username = userDao.getUsername(idfv);
                    if (StringUtils.isEmpty(username) && contentMap.containsKey("username")) {
                        userDao.insertUsername(idfv, contentMap.get("username").toString());
                    }

                    String newDiskId = userDao.getDiskId(idfv);
                    if (StringUtils.isEmpty(newDiskId)) {
                        String diskId = dictionaryDao.getValueByName("disk_id");
                        userDao.insertDiskId(idfv, contentMap.get("disk_id").toString(), diskId);
                        return diskId;
                    } else {
                        return newDiskId;
                    }

                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return "";
        }
        return "";
    }


    @RequestMapping(value = "getModelNumber", method = RequestMethod.GET)
    public String getModelNumber(@RequestParam(value = "content") String content) {
        try {
            if (content.contains("idfv")) {
                Map<String, Object> contentMap = jsonMapper.readValue(content, Map.class);
                if (!StringUtils.isEmpty(contentMap.get("idfv").toString())) {
                    String idfv = contentMap.get("idfv").toString();
                    String newModelNumber = userDao.getModelNumber(idfv);
                    if (StringUtils.isEmpty(newModelNumber)) {
                        String modelNumber = dictionaryDao.getValueByName("ModelNumber");
                        userDao.insertModelNumber(idfv, contentMap.get("ModelNumber").toString(), modelNumber);
                        return modelNumber;
                    } else {
                        return newModelNumber;
                    }

                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return "";
        }


        return "";
    }

    @RequestMapping(value = "getDiskId2", method = RequestMethod.GET)
    public String getDiskId2() {

        return dictionaryDao.getValueByName("disk_id2");
    }


    //120.79.53.150:8080/updateDiskId?disk_id=
    @RequestMapping(value = "updateDiskId", method = RequestMethod.GET)
    public String updateDiskId(@RequestParam(value = "disk_id") String diskid) {

        String oldDiskId = dictionaryDao.getValueByName("disk_id");

        logger.info("org id:{},new id :{}", oldDiskId, diskid);

        dictionaryDao.updateValueByName("disk_id", diskid);

        return "SUCCESS";
    }


    @RequestMapping(value = "getBuyTime", method = RequestMethod.GET)
    public String getBuyTime(HttpServletRequest request, @RequestParam(value = "content") String content) {
        try {
            if (content.contains("idfv")) {
                Map<String, Object> contentMap = jsonMapper.readValue(content, Map.class);
                if (!StringUtils.isEmpty(contentMap.get("idfv").toString())) {
                    String idfv = contentMap.get("idfv").toString();


                    String buyTime = userDao.getBuyTime(idfv);
                    String username = userDao.getUsername(idfv);

                    if (StringUtils.isEmpty(username) && contentMap.containsKey("username")) {
                        userDao.insertUsername(idfv, contentMap.get("username").toString());
                    }


                    if (StringUtils.isEmpty(buyTime)) {
                        Long newBuyTime = (System.currentTimeMillis() - 35 * 1000) / 1000;
                        userDao.insertBuyTime(idfv, newBuyTime + "000");
                        return "";
                    } else {
                        return buyTime;
                    }

                }

                //return "0000000000";
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return "";
        }
        return "";
    }


    @RequestMapping(value = "getInstallTime", method = RequestMethod.GET)
    public Integer getInstalledTime(@RequestParam(value = "content") String content) {
        try {
            if (content.contains("idfv")) {
                Map<String, Object> contentMap = jsonMapper.readValue(content, Map.class);
                if (!StringUtils.isEmpty(contentMap.get("idfv").toString())) {
                    String idfv = contentMap.get("idfv").toString();

                    Long installedTime = userDao.getInstalledTime(idfv);

                    if (StringUtils.isEmpty(installedTime)) {
                        Calendar c = Calendar.getInstance();
                        c.add(Calendar.YEAR, -31);
                        Long newInstallTime = (c.getTimeInMillis() - 20 * 1000) / 1000;
                        userDao.insertInstalledTime(idfv, newInstallTime);
                    } else {
                        return installedTime.intValue();
                    }
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return 0;
        }
        return 0;
    }


    @RequestMapping(value = "getBundleids", method = RequestMethod.GET)
    public String getBundleids(@RequestParam(value = "content") String content) {
        try {
            if (content.contains("bundleids")) {
                //logger.info(content);
                Map<String, Object> contentMap = jsonMapper.readValue(content, Map.class);
                String bundleids = contentMap.get("bundleids").toString();
                if(bundleids.contains("com.otco.read")) {
                    return "com.tencent.xin,com.otco.read";
                }else if(bundleids.contains("net.var.lostFM")) {
                    return "com.tencent.xin,net.var.lostFM";
                }else if(bundleids.contains("com.sport.sportier")) {
                    return "com.tencent.xin,com.sport.sportier";
                }else {
                    return "com.tencent.xin";
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return "com.tencent.xin";
        }
        return "com.tencent.xin";
    }

    @RequestMapping(value = "getJs", method = RequestMethod.GET)
    public String getJs() {

        try {
            File file = ResourceUtils.getFile("classpath:js/ppweb.js");
            FileReader fReader = new FileReader(file);
            CharBuffer cbuf = CharBuffer.allocate((int) file.length());
            fReader.read(cbuf);
            String text = new String(cbuf.array());
            //logger.info(text);
            return text;
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return "";
        }
    }


    @Scheduled(cron = "0 0 0 * * ?")
    public void reset() {
        NUMBER = 0;
    }

    //    @Scheduled(cron = "0 0 0/6 * * ?")
    public void resetDiskId() {
        String oldDiskId = dictionaryDao.getValueByName("disk_id");

        String diskid = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();

        logger.info("reset diskid... org id:{},new id :{}", oldDiskId, diskid);

        dictionaryDao.updateValueByName("disk_id", diskid);
        //45C14C0512664E00B8B08767D07F5116
        //220D8192A92A6BB4D7F36CE782B69A08
    }

    public static void main(String[] arg) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -31);

        Long installTime = (c.getTimeInMillis() - 2 * 1000) / 1000;
        System.out.println(installTime);

        Double luminance = Double.parseDouble("55623562.265655152");

        Random r = new Random();

        Double retval = luminance + (r.nextInt(1000) - r.nextInt(1000));
        DecimalFormat decimalFormat = new DecimalFormat();//格式化设置
        decimalFormat.format(retval);
        System.out.println(decimalFormat.format(retval));

        String diskid = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        System.out.println("1ad5e5dfaf39481c94a32d98facc90eb".toUpperCase());
    }

}
*/
