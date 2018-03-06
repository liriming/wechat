package com.iosre.pphb.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iosre.pphb.dao.*;
import com.iosre.pphb.dto.Withdraw;
import com.iosre.pphb.util.AddressUtils;
import com.iosre.pphb.util.WebUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.CharBuffer;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class WithdrawService {

    public final static Logger logger = LoggerFactory.getLogger(WithdrawService.class);
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    private static Map<String, Withdraw> map = new ConcurrentHashMap<>();
    private static Pattern contentPattern = Pattern.compile("[\\u4e00-\\u9fa5]+|\\d+");

    public void sendMsg(String content){
        Matcher m = contentPattern.matcher(content);
        Withdraw wd = new Withdraw();
        if ( m.find() ) {
            wd.setCode(m.group());
        }
        if ( m.find() ) {
            wd.setName(m.group());
        }
        wd.setTimestamp(System.currentTimeMillis());
        map.putIfAbsent(wd.getCode(), wd);

    }

    public String sendBak(String bak, String phoneno){
        List<Withdraw> list = new ArrayList<>();

        if(map.isEmpty()){
            return "";
        }

        for (Withdraw wd : map.values()
                ) {
            if (!wd.isSend() && (System.currentTimeMillis() - wd.getTimestamp()) / 1000 < 3.5 * 60 && WebUtil.isChineseStr(wd.getName())) {
                list.add(wd);
            }
        }
        list.sort((Withdraw w1, Withdraw w2) -> {
            if (w1.getTimestamp() > w2.getTimestamp()) {
                return -1;
            }else {
                return 1;
            }
        });

        if(list.size() == 0){
            return "";
        }

        Withdraw wd = list.get(list.size() - 1);
        wd.setSend(true);
        wd.setBak(bak);
        wd.setPhoneno(phoneno);
        map.putIfAbsent(wd.getCode(), wd);
        return wd.getCode() + "," + wd.getName();
    }

    public void done( String code){
        if(map.containsKey(code)){
            Withdraw wd = map.get(code);
            wd.setDone(true);
            map.putIfAbsent(code, wd);
        }
    }

    public List<Withdraw> getMsg(){
        List<Withdraw> list = new ArrayList<>();

        for (Withdraw wd : map.values()) {
            Long s = System.currentTimeMillis() - wd.getTimestamp();
            int t = (4 * 60 * 1000 - s.intValue()) / 1000;
            if(!WebUtil.isChineseStr(wd.getName())){
                wd.setTime("姓名参数不对");
            }else {
                if (t < 0) {
                    wd.setTime("已超时");
                } else {
                    wd.setTime("剩余" + t + "秒");
                }
            }
            list.add(wd);
        }

        list.sort((Withdraw w1, Withdraw w2) -> {
            if (w1.getTimestamp() > w2.getTimestamp()) {
                return -1;
            }else {
                return 1;
            }
        });

        return list;
    }

    //每天凌晨3点触发
    @Scheduled(cron="0 0 3 * * ?")
    public void cleanMap(){
        map.clear();
    }

}
