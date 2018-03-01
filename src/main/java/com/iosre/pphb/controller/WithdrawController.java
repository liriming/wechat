package com.iosre.pphb.controller;

import com.iosre.pphb.dto.Withdraw;
import com.iosre.pphb.service.WithdrawService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("/wd/")
public class WithdrawController {

    @Autowired
    private WithdrawService withdrawService;
    public final static Logger logger = LoggerFactory.getLogger(WithdrawController.class);


    @RequestMapping(value = "sendMsg", method = RequestMethod.GET)
    public void sendMsg(HttpServletRequest request, @RequestParam(value = "content") String content) {
        withdrawService.sendMsg(content);
    }

    @RequestMapping(value = "sendBak/{bak}", method = RequestMethod.GET)
    public String sendBak(HttpServletRequest request, @PathVariable(value = "bak") String bak) {
        try {
            String[] msg = bak.split("_");
            return withdrawService.sendBak(msg[0],msg[1]);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            return "";
        }

    }

    @RequestMapping(value = "getMsg", method = RequestMethod.GET)
    public List<Withdraw> getMsg(HttpServletRequest request) {
        return withdrawService.getMsg();
    }

    @RequestMapping(value = "done", method = RequestMethod.GET)
    public void done(HttpServletRequest request , @RequestParam(value = "code") String code) {
        withdrawService.done(code);
    }

    @RequestMapping(value = "getCode", method = RequestMethod.GET)
    public List<Withdraw> getCode(HttpServletRequest request) {
        return withdrawService.getMsg();
    }

}
