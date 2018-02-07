package com.iosre.pphb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class WebController {


    @GetMapping("/html")
    public String html() {
        return "index.html";
    }

}
