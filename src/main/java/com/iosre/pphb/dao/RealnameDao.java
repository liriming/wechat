package com.iosre.pphb.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

public interface RealnameDao {

    @Insert("insert into realname(phone,rname,rcard) values (#{phone},#{rname},#{rcard})")
    int insertDataInfo(@Param("phone") String phone, @Param("rname") String rname, @Param("rcard") String rcard);


}
