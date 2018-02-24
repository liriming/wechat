package com.iosre.pphb.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

public interface DeviceDao {


    @Insert("insert into device(bak_name,device_name,device_info) values(#{bak_name},#{device_name},#{device_info})")
    Integer insertDeviceInfo(@Param("bak_name") String bakName, @Param("device_name") String device_name, @Param("device_info") String device_info);

    @Select("select device_info from device where device_name=#{device_name} order by id desc limit 1")
    String getLastDeviceInfo( @Param("device_name") String device_name);

    @Select("select device_info from device where device_name=#{device_name} and bak_name=#{bak_name} order by id desc limit 1")
    String getDeviceInfo(@Param("bak_name") String bakName, @Param("device_name") String device_name );

}
