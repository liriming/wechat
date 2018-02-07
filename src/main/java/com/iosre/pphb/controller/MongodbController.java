package com.iosre.pphb.controller;
import com.iosre.pphb.dto.Page;
import com.iosre.pphb.dto.UserOpLog;
import com.iosre.pphb.service.MongodbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@RestController
@RequestMapping("/mdb/")
public class MongodbController {

    @Autowired
    private MongodbService mongodbService;


    @RequestMapping(value = "search", method = RequestMethod.POST)
    public Page<UserOpLog> search(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        return mongodbService.search(params);
    }

}
