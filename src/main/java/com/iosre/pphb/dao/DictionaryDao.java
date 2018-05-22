package com.iosre.pphb.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;

public interface DictionaryDao {


    @Update("update dictionary set clo_value =#{clo_value} where clo_name=#{clo_name}")
    Integer updateValueByName(@Param("clo_name") String clo_name,@Param("clo_value") String clo_value);

    @Update("update dictionary set clo_value =#{clo_value} where id=#{id}")
    Integer updateValueById(@Param("clo_value") String clo_value,@Param("id") Integer id);

    @Select("select clo_value from pphb.dictionary where clo_name=#{0}")
    String getValueByName(String clo_name);

    @Select("select * from pphb.dictionary")
    List<Map<String,Object>> getSysConfig();
}
