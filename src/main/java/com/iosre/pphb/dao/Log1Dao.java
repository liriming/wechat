package com.iosre.pphb.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface Log1Dao {


    @Insert("insert into log1(bundleid,username) values(#{bundleid},#{username})")
    Integer insertLog(@Param("bundleid") String bundleid, @Param("username") String username);


}
