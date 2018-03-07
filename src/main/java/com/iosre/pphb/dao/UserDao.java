package com.iosre.pphb.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

public interface UserDao {


    @CacheEvict(value = "getBuyTime", key = "#p0")
    @Insert("insert into user(idfv,buy_time) values(#{idfv},#{buy_time}) ON DUPLICATE KEY UPDATE buy_time=values(buy_time)")
    Integer insertBuyTime(@Param("idfv")String idfv, @Param("buy_time") String buy_time);

    @CacheEvict(value = "getInstalledTime", key = "#p0")
    @Insert("insert into user(idfv,installed_time) values(#{idfv}, #{installed_time}) ON DUPLICATE KEY UPDATE installed_time=values(installed_time)")
    Integer insertInstalledTime(@Param("idfv")String idfv, @Param("installed_time") Long installed_time);

    @CacheEvict(value = "getUsername", key = "#p0")
    @Insert("insert into user(idfv,username) values(#{idfv}, #{username}) ON DUPLICATE KEY UPDATE username=values(username)")
    Integer insertUsername(@Param("idfv")String idfv, @Param("username") String username);

    @CacheEvict(value = "getDiskId", key = "#p0")
    @Insert("insert into user(idfv,org_disk_id,new_disk_id) values(#{idfv}, #{org_disk_id}, #{new_disk_id}) ON DUPLICATE KEY UPDATE org_disk_id=values(org_disk_id),new_disk_id=values(new_disk_id)")
    Integer insertDiskId(@Param("idfv")String idfv, @Param("org_disk_id") String org_disk_id, @Param("new_disk_id") String new_disk_id);

    @CacheEvict(value = "getModelNumber", key = "#p0")
    @Insert("insert into user(idfv,old_mn,new_mn) values(#{idfv}, #{old_mn}, #{new_mn}) ON DUPLICATE KEY UPDATE old_mn=values(old_mn),new_mn=values(new_mn)")
    Integer insertModelNumber(@Param("idfv")String idfv, @Param("old_mn") String old_mn, @Param("new_mn") String new_mn);

    @CacheEvict(value = "getOpenudid", key = "#p0")
    @Insert("insert into user(idfv,old_openudid,new_openudid) values(#{idfv}, #{old_openudid}, #{new_openudid}) ON DUPLICATE KEY UPDATE old_openudid=values(old_openudid),new_openudid=values(new_openudid)")
    Integer insertOpenudid(@Param("idfv")String idfv, @Param("old_openudid") String old_openudid, @Param("new_openudid") String new_openudid);

    @CacheEvict(value = "getUtdid", key = "#p0")
    @Insert("insert into user(idfv,old_utdid,new_utdid) values(#{idfv}, #{old_utdid}, #{new_utdid}) ON DUPLICATE KEY UPDATE old_utdid=values(old_utdid),new_utdid=values(new_utdid)")
    Integer insertUtdid(@Param("idfv")String idfv, @Param("old_utdid") String old_utdid, @Param("new_utdid") String new_utdid);

    @CacheEvict(value = "getBssid", key = "#p0")
    @Insert("insert into user(idfv,bssid) values(#{idfv}, #{bssid}) ON DUPLICATE KEY UPDATE bssid=values(bssid)")
    Integer insertBssid(@Param("idfv")String idfv, @Param("bssid") String bssid);

    @CacheEvict(value = "getBid", key = "#p0")
    @Insert("insert into user(idfv,old_bid,new_bid) values(#{idfv}, #{old_bid}, #{new_bid}) ON DUPLICATE KEY UPDATE old_bid=values(old_bid),new_bid=values(new_bid)")
    Integer insertBid(@Param("idfv")String idfv, @Param("old_bid") String old_bid, @Param("new_bid") String new_bid);

    @CacheEvict(value = "getIp", key = "#p0")
    @Insert("insert into user(idfv,ip) values(#{idfv}, #{ip}) ON DUPLICATE KEY UPDATE ip=values(ip)")
    Integer insertIp(@Param("idfv")String idfv, @Param("ip") String ip);

    @Cacheable(value = "getBuyTime", key = "#p0")
    @Select("select ifnull(buy_time,'') from user where idfv = #{0}")
    String getBuyTime(String idf);

    @Cacheable(value = "getInstalledTime", key = "#p0")
    @Select("select installed_time from user where idfv = #{0}")
    Long getInstalledTime(String idf);

    @Cacheable(value = "getUsername", key = "#p0")
    @Select("select ifnull(username,'') from user where idfv = #{0}")
    String getUsername(String idf);

    @Cacheable(value = "getDiskId", key = "#p0")
    @Select("select ifnull(new_disk_id,'') from user where idfv = #{0}")
    String getDiskId(String idf);

    @Cacheable(value = "getModelNumber", key = "#p0")
    @Select("select ifnull(new_mn,'') from user where idfv = #{0}")
    String getModelNumber(String idf);

    @Cacheable(value = "getOpenudid", key = "#p0")
    @Select("select ifnull(new_openudid,'') from user where idfv = #{0}")
    String getOpenudid(String idf);

    @Cacheable(value = "getUtdid", key = "#p0")
    @Select("select ifnull(new_utdid,'') from user where idfv = #{0}")
    String getUtdid(String idf);

    @Cacheable(value = "getBssid", key = "#p0")
    @Select("select ifnull(bssid,'') from user where idfv = #{0}")
    String getBssid(String idf);


    @Cacheable(value = "getIp", key = "#p0")
    @Select("select ifnull(ip,'') from user where idfv = #{0}")
    String getIp(String idf);

    @Cacheable(value = "getBid", key = "#p0")
    @Select("select ifnull(new_bid,'') from user where idfv = #{0}")
    String getBid(String idf);

}
