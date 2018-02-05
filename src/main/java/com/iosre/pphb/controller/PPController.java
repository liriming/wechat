package com.iosre.pphb.controller;

import com.iosre.pphb.service.PPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/pp/")
public class PPController {

    @Autowired
    private PPService ppService;


    @RequestMapping(value = "uploadDecrypt", method = RequestMethod.GET)
    public void uploadDecrypt(HttpServletRequest request, @RequestParam(value = "val") String val) {
        ppService.uploadDecrypt(request, val);
    }

    @RequestMapping(value = "checkIP", method = RequestMethod.GET)
    public boolean checkIP(HttpServletRequest request) {
        return ppService.checkIP(request);
    }

    @RequestMapping(value = "sWitch", method = RequestMethod.GET)
    public Boolean sWitch(HttpServletRequest request, @RequestParam(value = "content") String content) {
        return ppService.sWitch(request,content);
    }

    @RequestMapping(value = "log", method = RequestMethod.GET)
    public Boolean log(HttpServletRequest request,@RequestParam(value = "devicename") String devicename) {
        return ppService.log(devicename);
    }


    @RequestMapping(value = "getLuminance", method = RequestMethod.GET)
    public Double getLuminance(HttpServletRequest request,@RequestParam(value = "content") String content) {
        return ppService.getLuminance(content);
    }


    @RequestMapping(value = "getBattery", method = RequestMethod.GET)
    public Integer getBattery(HttpServletRequest request,@RequestParam(value = "content") String content) {
        return ppService.getBattery(content);
    }


    @RequestMapping(value = "getDiskId", method = RequestMethod.GET)
    public String getDiskId(HttpServletRequest request,@RequestParam(value = "content") String content) {
        return ppService.getDiskId(content);
    }


    @RequestMapping(value = "getModelNumber", method = RequestMethod.GET)
    public String getModelNumber(HttpServletRequest request,@RequestParam(value = "content") String content) {
        return ppService.getModelNumber(content);
    }

    @RequestMapping(value = "getOpenudid", method = RequestMethod.GET)
    public String getOpenudid(HttpServletRequest request,@RequestParam(value = "content") String content) {
        return ppService.getOpenudid(content);
    }


    @RequestMapping(value = "getUtdid", method = RequestMethod.GET)
    public String getUtdid(HttpServletRequest request,@RequestParam(value = "content") String content) {
        return ppService.getUtdid(content);
    }


    @RequestMapping(value = "getDiskId2", method = RequestMethod.GET)
    public String getDiskId2() {
        return ppService.getDiskId2();
    }


    //120.79.53.150:8080/updateDiskId?disk_id=
    @RequestMapping(value = "updateDiskId", method = RequestMethod.GET)
    public String updateDiskId(HttpServletRequest request,@RequestParam(value = "disk_id") String diskid) {
        return ppService.updateDiskId(diskid);
    }


    @RequestMapping(value = "getBuyTime", method = RequestMethod.GET)
    public String getBuyTime(HttpServletRequest request, @RequestParam(value = "content") String content) {
        return ppService.getBuyTime(request, content);
    }


    @RequestMapping(value = "getInstallTime", method = RequestMethod.GET)
    public Integer getInstalledTime(HttpServletRequest request,@RequestParam(value = "content") String content) {
        return ppService.getInstalledTime(content);
    }

    @RequestMapping(value = "getBundleids", method = RequestMethod.GET)
    public String getBundleids(HttpServletRequest request,@RequestParam(value = "content") String content) {
        return ppService.getBundleids(content);
    }


    @RequestMapping(value = "getJs", method = RequestMethod.GET)
    public String getJs() {
        return ppService.getJs();
    }

}
