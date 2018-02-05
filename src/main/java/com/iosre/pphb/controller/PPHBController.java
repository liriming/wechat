package com.iosre.pphb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


public class PPHBController {

    @Autowired
    private com.iosre.pphb.controller.PPHBService pphbService;


    @RequestMapping(value = "uploadDecrypt", method = RequestMethod.GET)
    public void uploadDecrypt(@RequestParam(value = "val") String val) {
        pphbService.uploadDecrypt(val);
    }


    @RequestMapping(value = "sWitch", method = RequestMethod.GET)
    public Boolean sWitch(@RequestParam(value = "content") String content) {
        return pphbService.sWitch(content);
    }


    @RequestMapping(value = "getLuminance", method = RequestMethod.GET)
    public Double getLuminance(@RequestParam(value = "content") String content) {
        return pphbService.getLuminance(content);
    }


    @RequestMapping(value = "getBattery", method = RequestMethod.GET)
    public Integer getBattery(@RequestParam(value = "content") String content) {
        return pphbService.getBattery(content);
    }


    @RequestMapping(value = "getDiskId", method = RequestMethod.GET)
    public String getDiskId(@RequestParam(value = "content") String content) {
        return pphbService.getDiskId(content);
    }


    @RequestMapping(value = "getModelNumber", method = RequestMethod.GET)
    public String getModelNumber(@RequestParam(value = "content") String content) {
        return pphbService.getModelNumber(content);
    }

    @RequestMapping(value = "getDiskId2", method = RequestMethod.GET)
    public String getDiskId2() {
        return pphbService.getDiskId2();
    }


    //120.79.53.150:8080/updateDiskId?disk_id=
    @RequestMapping(value = "updateDiskId", method = RequestMethod.GET)
    public String updateDiskId(@RequestParam(value = "disk_id") String diskid) {
        return pphbService.updateDiskId(diskid);
    }


    @RequestMapping(value = "getBuyTime", method = RequestMethod.GET)
    public String getBuyTime(HttpServletRequest request, @RequestParam(value = "content") String content) {
        return pphbService.getBuyTime(request, content);
    }


    @RequestMapping(value = "getInstallTime", method = RequestMethod.GET)
    public Integer getInstalledTime(@RequestParam(value = "content") String content) {
        return pphbService.getInstalledTime(content);
    }


    @RequestMapping(value = "getBundleids", method = RequestMethod.GET)
    public String getBundleids(@RequestParam(value = "content") String content) {
        return pphbService.getBundleids(content);
    }

    @RequestMapping(value = "getJs", method = RequestMethod.GET)
    public String getJs() {
        return pphbService.getJs();
    }



}
