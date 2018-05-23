package com.iosre.pphb.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface IPDao {


    @Insert("insert into ip(ip,address) values(#{ip},#{address})")
    Integer insertIP(@Param("ip") String ip,@Param("address") String address);

    @Select("select if(count(1) > 0 , false, true) from ip where ip=#{ip} AND ctime > date_sub(NOW(), INTERVAL 1 DAY)")
    boolean checkIP(@Param("ip") String ip);
}
