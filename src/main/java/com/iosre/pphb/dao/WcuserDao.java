package com.iosre.pphb.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

public interface WcuserDao {

    @Insert("insert into wcuser(name,psw,_62,phoneno,isalive) values (#{name},#{psw},#{_62},#{phoneno},#{isalive})")
    int insertDataInfo(@Param("name")String name,@Param("psw")String psw,@Param("_62")String _62,@Param("phoneno")String phoneno,@Param("isalive")Integer isalive);

}
