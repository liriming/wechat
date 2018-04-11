package com.iosre.pphb.controller;

import com.iosre.pphb.dao.WcphoneDao;
import com.iosre.pphb.dto.Page;
import com.iosre.pphb.dto.UserOpLog;
import com.iosre.pphb.http.HttpService;
import com.iosre.pphb.service.MongodbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@RestController
@RequestMapping("/gsim/")
public class GsimController {

    private final static String US_HOST_GSIM = "https://gsim.online/api/";
    @Autowired
    private HttpService httpService;
    @Autowired
    private WcphoneDao wcphoneDao;


    @RequestMapping(value = "getNumber", method = RequestMethod.GET)
    public String getNumber(HttpServletRequest request, @PathVariable("key") String key) {
        try{
            return httpService.get(US_HOST_GSIM + "getNumber/" + key).getPayload();
        }catch (Exception e){
            return e.getMessage();
        }
    }

    @RequestMapping(value = "refund", method = RequestMethod.GET)
    public String refund(HttpServletRequest request, @PathVariable("key") String key, @PathVariable("phone") String phone) {
        try{
            return httpService.get(US_HOST_GSIM + "refund/" + key + "/" + phone).getPayload();
        }catch (Exception e){
            return e.getMessage();
        }
    }

    @RequestMapping(value = "sendSms", method = RequestMethod.GET)
    public String sendSms(HttpServletRequest request, @PathVariable("key") String key, @PathVariable("phone") String phone) {
        try{
            wcphoneDao.insertGsimPhone(phone,key,6);
            return httpService.get(US_HOST_GSIM + "sendSms/" + key + "/" + phone).getPayload();
        }catch (Exception e){
            return e.getMessage();
        }
    }

    @RequestMapping(value = "getMessage", method = RequestMethod.GET)
    public String getMessage(HttpServletRequest request, @PathVariable("key") String key, @PathVariable("phone") String phone) {
        try{
            return httpService.get(US_HOST_GSIM + "getMessage/" + key + "/" + phone).getPayload();
        }catch (Exception e){
            return e.getMessage();
        }
    }

    @RequestMapping(value = "credits", method = RequestMethod.GET)
    public String credits(HttpServletRequest request, @PathVariable("key") String key) {
        try{
            return httpService.get(US_HOST_GSIM + "credits/" + key).getPayload();
        }catch (Exception e){
            return e.getMessage();
        }
    }

}
