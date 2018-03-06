package com.iosre.pphb.controller;

import com.iosre.pphb.service.ThewolfVoiceMsgService;
import com.iosre.pphb.service.VoiceMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/sms/")
public class VoiceMsgController {

    @Autowired
    private VoiceMsgService voiceMsgService;

   /* @Autowired
    private ThewolfVoiceMsgService voiceMsgService;*/


    @RequestMapping(value = "getHm", method = RequestMethod.GET)
    public String getHm(HttpServletRequest request, HttpServletResponse response) throws InterruptedException {
        return voiceMsgService.getHm(request, response);
    }


    @RequestMapping(value ="sendYzm/{phone}", method = RequestMethod.GET)
    public String sendYzm(HttpServletRequest request, HttpServletResponse response, @PathVariable("phone") String phone) throws InterruptedException {
        return voiceMsgService.sendYzm(request, response, phone);
    }

    @RequestMapping(value ="getYzm/{phone}", method = RequestMethod.GET)
    public String getYzm(@PathVariable("phone") String phone)  {
        return voiceMsgService.getYzm(phone);
    }

}
