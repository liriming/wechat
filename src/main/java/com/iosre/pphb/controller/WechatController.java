package com.iosre.pphb.controller;

import com.iosre.pphb.dto.Page;
import com.iosre.pphb.dto.UserOpLog;
import com.iosre.pphb.service.WcSmsService;
import com.iosre.pphb.service.WechatService;
import com.iosre.pphb.util.AddressUtils;
import com.iosre.pphb.util.WebUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/wc/")
public class WechatController {

    public final static Logger logger = LoggerFactory.getLogger(WechatController.class);

    @Autowired
    private WechatService wechatService;
    @Autowired
    private WcSmsService wcSmsService;


    @RequestMapping(value = "idCard", method = RequestMethod.GET)
    public String idCard(@RequestParam(value = "count") Integer count) {
        return wechatService.getIdCard(count);
    }


    @RequestMapping(value = "phone", method = RequestMethod.GET)
    public String phone(HttpServletRequest request, HttpServletResponse response) {
        try {
            return wcSmsService.phone(request,response);
        } catch (InterruptedException e) {
            return e.getMessage();
        }
    }

    @RequestMapping(value ="sendCode/{phone}", method = RequestMethod.GET)
    public String sendYzm(HttpServletRequest request, HttpServletResponse response, @PathVariable("phone") String phone) throws InterruptedException {
        return wcSmsService.sendCode(request, response, phone);
    }

    @RequestMapping(value ="getCode/{phone}", method = RequestMethod.GET)
    public String getYzm(HttpServletRequest request,@PathVariable("phone") String phone)  {
        return wcSmsService.getCode(phone);
    }


    @RequestMapping(value ="exportPhone", method = RequestMethod.GET)
    public Integer exportPhone(@RequestParam(value = "list") String list)  {
        return wcSmsService.exportPhone(list);
    }


    @RequestMapping(value ="exportPhone1", method = RequestMethod.GET)
    public Integer exportPhone1(@RequestParam(value = "list") String list,@RequestParam(value = "server") String server)  {
        return wcSmsService.exportPhone1(list,server);
    }

    @RequestMapping(value ="usPhone", method = RequestMethod.GET)
    public String usPhone(HttpServletRequest request,@RequestParam(value = "country") String country) throws Exception {
        AddressUtils addressUtils = new AddressUtils();
        String ip = WebUtil.getLocalIp(request);
        ip = addressUtils.getAddresses("ip="+ip, "utf-8");
        if ("美国".equalsIgnoreCase(country)){
            return wcSmsService.usPhone(ip);
        }else if("英国".equalsIgnoreCase(country) ) {
            return wcSmsService.gSimPhone("gsim_key",ip);
        }else if ("菲律宾".equalsIgnoreCase(country)){
            return wcSmsService.gSimPhone("gsim_ph_key",ip);
        }
        return wcSmsService.usPhone(ip);
    }

    @RequestMapping(value ="getUsCode", method = RequestMethod.GET)
    public String getUsCode(HttpServletRequest request,@RequestParam(value = "list") String list,@RequestParam(value = "country") String country)  {
        try {
            if ("美国".equalsIgnoreCase(country)){
                return wcSmsService.getUsCode(list);
            }else if("英国".equalsIgnoreCase(country) ) {
                return wcSmsService.getGsimCode("44" +list);
            }else if ( "菲律宾".equalsIgnoreCase(country)){
                return wcSmsService.getGsimCode("63" +list);
            }
            return wcSmsService.getUsCode("44" +list);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return "error";
        }
    }

    @RequestMapping(value ="sendGsimCode", method = RequestMethod.GET)
    public void sendGsimCode(HttpServletRequest request,@RequestParam(value = "phone") String phone,@RequestParam(defaultValue = "英国") String country)  {
        try {
            if("英国".equalsIgnoreCase(country) ) {
                 wcSmsService.sendGsimCode("44" + phone);
            }else if ( "菲律宾".equalsIgnoreCase(country)){
                wcSmsService.sendGsimCode("63" + phone);
            }else {
                wcSmsService.sendGsimCode("44" +phone);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @RequestMapping(value ="uploadData", method = RequestMethod.GET)
    public void uploadData(HttpServletRequest request,@RequestParam(value = "data") String data)  {
        String ip = WebUtil.getLocalIp(request);
        wcSmsService.uploadData(ip,data);
    }

    @RequestMapping(value ="exportData", method = RequestMethod.GET)
    public String exportData(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "psw") String psw, @RequestParam(value = "list") String list, @RequestParam(value = "country") String country)  {

        List<Integer> ids = new ArrayList<>();
        String[] l = list.split(",");

        for (String i : l){
            ids.add(Integer.parseInt(i));
        }
        return wcSmsService.exportData(response,psw,ids,country);
    }

    @RequestMapping(value ="statistics", method = RequestMethod.GET)
    public Map<String,Object> statistics()  {
        return wcSmsService.statistics();
    }

    @RequestMapping(value ="realName", method = RequestMethod.GET)
    public void uploadRealName(HttpServletRequest request,@RequestParam(value = "data") String data)  {
        String ip = WebUtil.getLocalIp(request);
        wcSmsService.uploadRealName(ip,data);
    }

    @RequestMapping(value ="uploadCountTime", method = RequestMethod.GET)
    public void uploadCountTime()  {
        wcSmsService.uploadCountTime();
    }

    @RequestMapping(value ="noRevcMsg", method = RequestMethod.GET)
    public void noRevcMsg(@RequestParam(value = "phone") String phone,@RequestParam(value = "country") String country)  {
        try {
             wcSmsService.noRevcMsg(phone,country);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @RequestMapping(value ="getNoCheckPho", method = RequestMethod.GET)
    public String getNoCheckPho(@RequestParam(value = "type") int type,@RequestParam(value = "country") String country)  {
        try {
            if ("美国".equalsIgnoreCase(country)){
                return wcSmsService.getNoCheckPho(type,"1");
            }else if("英国".equalsIgnoreCase(country) ) {
                return wcSmsService.getNoCheckPho(type,"44");
            }else if ( "菲律宾".equalsIgnoreCase(country)){
                return wcSmsService.getNoCheckPho(type,"63");
            }
            return wcSmsService.getNoCheckPho(type,"44");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return e.getMessage();
        }
    }

    @RequestMapping(value ="isalive", method = RequestMethod.GET)
    public void isalive(@RequestParam(value = "type") int type,@RequestParam(value = "phone") String phone)  {
        try {
            wcSmsService.isalive(type,phone);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @RequestMapping(value ="getGZHH", method = RequestMethod.GET)
    public String getGZHH()  {
        try {
            return wcSmsService.getGZHH();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return e.getMessage();
        }
    }

    @RequestMapping(value ="getCountry", method = RequestMethod.GET)
    public String getCountry(HttpServletRequest request)  {
        try {
            return wcSmsService.getCountry();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return e.getMessage();
        }
    }

    @RequestMapping(value ="checkPhone", method = RequestMethod.GET)
    public String checkPhone(@RequestParam(value = "phone") String phone)  {
        try {
            return wcSmsService.checkPhone(phone);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return e.getMessage();
        }
    }

    @RequestMapping(value ="updateGsimKey", method = RequestMethod.GET)
    public String updateGsimKey(@RequestParam(value = "key") String key)  {
        try {
            return wcSmsService.updateGsimKey(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return e.getMessage();
        }
    }

    @RequestMapping(value ="resetPhone", method = RequestMethod.GET)
    public void resetPhone()  {
        wcSmsService.resetPhone();
    }

    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<Map<String,String>> search(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        return wcSmsService.search(params);
    }

    @RequestMapping(value = "getSysConfig", method = RequestMethod.POST)
    public List<Map<String,String>> getSysConfig() {
        return wcSmsService.getSysConfig();
    }
}
