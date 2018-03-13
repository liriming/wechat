package com.iosre.pphb.controller;

import com.iosre.pphb.service.WcSmsService;
import com.iosre.pphb.service.WechatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/wc/")
public class WechatController {

    public final static Logger logger = LoggerFactory.getLogger(WechatController.class);

    @Autowired
    private WechatService wechatService;
    @Autowired
    private WcSmsService wcSmsService;


    @RequestMapping(value = "idCard", method = RequestMethod.GET)
    public String idCard(HttpServletRequest request) {
        return wechatService.getIdCard();
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
    public String getYzm(@PathVariable("phone") String phone)  {
        return wcSmsService.getCode(phone);
    }


    @RequestMapping(value ="exportPhone", method = RequestMethod.GET)
    public Integer exportPhone(@RequestParam(value = "list") String list)  {
        return wcSmsService.exportPhone(list);
    }

    @RequestMapping(value ="usPhone", method = RequestMethod.GET)
    public String usPhone()  {
        return wcSmsService.usPhone();
    }

    @RequestMapping(value ="getUsCode", method = RequestMethod.GET)
    public String getUsCode(@RequestParam(value = "list") String list)  {
        try {
            return wcSmsService.getUsCode(list);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return "error";
        }
    }


}
