package com.iosre.pphb.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

public interface DictionaryDao {


    @CacheEvict(value = "getValueByName", key = "#p0")
    @Insert("update dictionary set clo_value =#{clo_value} where clo_name=#{clo_name}")
    Integer updateValueByName(@Param("clo_name") String clo_name,@Param("clo_value") String clo_value);


    @Cacheable(value = "getValueByName", key = "#p0")
    @Select("select clo_value from pphb.dictionary where clo_name=#{0}")
    String getValueByName( String clo_name);
}
