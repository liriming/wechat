package com.iosre.pphb.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iosre.pphb.dao.User1Dao;
import com.iosre.pphb.dto.Withdraw;
import com.iosre.pphb.util.WebUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class WechatService {

    public final static Logger logger = LoggerFactory.getLogger(WechatService.class);

    @Autowired
    private User1Dao user1Dao;


    public String getIdCard(){

        Map<String,String> map = user1Dao.getIdCard();

        return map.get("Name") + "," +  map.get("CtfId");
    }


}
