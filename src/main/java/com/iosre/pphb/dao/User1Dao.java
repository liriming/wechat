package com.iosre.pphb.dao;

import org.apache.ibatis.annotations.Select;

import java.util.Map;

public interface User1Dao {


    @Select("SELECT * FROM (SELECT `Name`,CtfId FROM user1 ORDER BY RAND() LIMIT 10) a " +
            " WHERE a.CtfId REGEXP '[[:digit:]]{6}(18|19|20)[[:digit:]]{2}(0[1-9]|1[120])(0[1-9]|[12][[:digit:]]|3[01])[[:digit:]]{3}([[:digit:]]|X)' " +
            " ORDER BY RAND() LIMIT 1")
    Map<String, String> getIdCard();

}
