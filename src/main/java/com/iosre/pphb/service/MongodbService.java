package com.iosre.pphb.service;

import com.iosre.pphb.dto.Page;
import com.iosre.pphb.dto.UserOpLog;
import com.iosre.pphb.util.XDateUtils;
import com.iosre.pphb.util.XValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class MongodbService {

    public final static Logger logger = LoggerFactory.getLogger(MongodbService.class);


    @Autowired
    private MongoTemplate mongoTemplate;

    public Page<UserOpLog> search(Map<String, Object> params){

        int pageNo = (int) params.get("pageNo");
        int pageSize = (int) params.get("pageSize");

        Map<String, String> p = (Map<String, String>) params.get("params");

        logger.info("params: {}", p);

        String collection = "ppMgrOpLog";
        String controller = null;
        String method = null;
        String ms = null;
        String bTime = null;
        String eTime = null;
        String advancedKey = null;
        String advancedValue = null;
        if(p!=null){
            controller = p.get("controller");
            method = p.get("method");
            ms = p.get("ms");
            bTime = p.get("bTime");
            eTime = p.get("eTime");
            advancedKey = p.get("advancedKey");
            advancedValue = p.get("advancedValue");
        }

        Criteria c = new Criteria();

        if(!StringUtils.isEmpty(method)){
            c.and("method").is(method);
        }
        if(!StringUtils.isEmpty(controller)){
            c.and("controller").is(controller);
        }
        if(!StringUtils.isEmpty(ms) && !ms.equals("0")){
            c.and("ms").gt(Integer.valueOf(ms));
        }

        Long bt=null, et=null;
        if(!StringUtils.isEmpty(bTime)){
            bt = XDateUtils.stringToTimestamp(bTime);
        }
        if(!StringUtils.isEmpty(eTime)){
            et = XDateUtils.stringToTimestamp(eTime);
        }
        if(!StringUtils.isEmpty(bTime) && !StringUtils.isEmpty(eTime)){
            c.and("ctime").gte(bt).lte(et);
        }else if(bt!=null){
            c.and("ctime").gte(bt);
        }else if(et!=null){
            c.and("ctime").lte(et);
        }

        //高级搜索条件
        if(!StringUtils.isEmpty(advancedKey) && !StringUtils.isEmpty(advancedValue)){
            if(XValidator.isNumber(advancedValue)){
                c.and(advancedKey).in(advancedValue, Integer.parseInt(advancedValue));
            }else if("true".equals(advancedValue)||"false".equals(advancedValue)){
                c.and(advancedKey).is("true".equals(advancedValue)? true : false);
            }else {
                c.and(advancedKey).regex(advancedValue);
            }
        }

        Sort sort = new Sort(Sort.Direction.DESC, "ctime");
        Page<UserOpLog> page = new Page<>(pageNo, pageSize);
        page.setSort(sort);

        List<UserOpLog> data = mongoTemplate.find(query(c).with(page), UserOpLog.class, collection);
//        List<UserOpLog> data = mongoTemplate.findAll(UserOpLog.class, collection);

        page.setResults(data);
        //page.setTotalRecord(mongoTemplate.count(query(c), collection));//这个太耗时了 //TODO 根据请求条件决定要不要执行

        return page;

    }

}
