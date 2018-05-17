package com.iosre.pphb.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

public interface WcuserDao {

    @Insert("insert into wcuser(name,psw,_62,phoneno,isalive,ip,realname,rname,rcard,country) values (#{name},#{psw},#{_62},#{phoneno},#{isalive},#{ip},#{realname},#{rname},#{rcard},#{country})")
    int insertDataInfo(@Param("name") String name, @Param("psw") String psw, @Param("_62") String _62, @Param("phoneno") String phoneno, @Param("isalive") Integer isalive,
                       @Param("ip") String ip, @Param("realname") Integer realname, @Param("rname") String rname, @Param("rcard") String rcard, @Param("country") String country);


    @Select({
            " <script> ",
            " select DISTINCT a.id,right(name,10) as name,psw,_62,a.ctime,b.token from wcuser a LEFT JOIN wcphone b ON RIGHT (a.NAME, 10) = RIGHT(b.phone, 10) where (_62 is not null or _62 != '') and isalive=1 and export=0 and a.id in   ",
            " <foreach item='item' collection='list' open='(' separator=',' close=')'> ",
            " #{item} ",
            " </foreach> ",
            " and right(name,11) like '1%'",
            " </script> "
    })
    List<Map<String, Object>> getUsExportData(@Param("list") List<Integer> list);

    @Select({
            " <script> ",
            " select id,right(name,10) as name,psw,_62,ctime from wcuser where (_62 is not null or _62 != '') and isalive=1 and export=0 and id in   ",
            " <foreach item='item' collection='list' open='(' separator=',' close=')'> ",
            " #{item} ",
            " </foreach> ",
            " and right(name,12) like '44%'",
            " </script> "
    })
    List<Map<String, Object>> getUkExportData(@Param("list") List<Integer> list);


    @Select("select count(id) from wcuser where (_62 is not null or _62 != '') and isalive=1 and export=0 and realname=#{realname} " +
            "and right(name,11) like #{country} and ctime BETWEEN #{sDate} AND  #{eDate} ")
    Integer getExportDataNum(@Param("sDate") String sDate, @Param("eDate") String eDate, @Param("realname") int realname, @Param("country") String country);

    @Select("select count(id) from wcuser where (_62 is not null or _62 != '') and isalive=1 and export=0 and realname=#{realname} " +
            "and right(name,11) like #{country} and ctime BETWEEN #{sDate} AND  #{eDate} and checkpho = 1 ")
    Integer getCheckPhoDataNum(@Param("sDate") String sDate, @Param("eDate") String eDate, @Param("realname") int realname, @Param("country") String country);

    @Update("update wcuser set rname=#{rname},rcard=#{rcard},realname=1 where phone=#{phone}")
    Integer updateRealName(@Param("phone") String phone, @Param("rname") String rname, @Param("rcard") String rcard);

    @Update({
            " <script> ",
            " update wcuser set export=1 , exporttime=CURRENT_TIMESTAMP() where id in  ",
            " <foreach item='item' collection='list' open='(' separator=',' close=')'> ",
            " #{item} ",
            " </foreach> ",
            " </script> "
    })
    Integer updateExportStatus(@Param("list") List list);

    @Select("SELECT * FROM( " +
            " SELECT  " +
            " (SELECT COUNT(*) FROM wcuser WHERE _62 != '' AND isalive=1 AND export=0) as stock, " +
            " (SELECT COUNT(*) FROM wcuser WHERE ctime  BETWEEN  #{sDate} AND  #{eDate}) as total, " +
            " (SELECT COUNT(*) FROM wcuser WHERE ctime  BETWEEN  #{sDate} AND  #{eDate} AND _62 != '' AND isalive=1) as alive, " +
            " (SELECT COUNT(*) FROM wcuser WHERE ctime  BETWEEN  #{sDate} AND  #{eDate} AND _62 != '' AND isalive=1 AND realname=1) as realn, " +
            " (SELECT COUNT(*) FROM wcuser WHERE ctime  BETWEEN  #{sDate} AND  #{eDate} AND _62 != '' AND isalive=1 AND realname=0) as nrealn, " +
            " (SELECT COUNT(*) FROM wcuser WHERE ctime  BETWEEN  #{sDate} AND  #{eDate} AND _62 != '' AND isalive=0) as nalive, " +
            " (SELECT COUNT(*) FROM wcuser WHERE ctime  BETWEEN  #{sDate} AND  #{eDate} AND _62 = '') as sup " +
            ") t")
    Map<String, Object> getMsg(@Param("sDate") String sDate, @Param("eDate") String eDate);

    @Select("SELECT gzhh FROM wcuser WHERE ctime  BETWEEN  #{sDate} AND  #{eDate} AND _62 != '' AND isalive=1")
    List<String> gzhh(@Param("sDate") String sDate, @Param("eDate") String eDate);


    @Select("select right(name,10) from wcuser where  isalive=1 and export=0 and checkpho=0 and realname=#{realname} " +
            "and ctime BETWEEN #{sDate} AND  #{eDate} and  right(name,11) like #{country} order by ctime ${listorder} limit 1 ")
    String getNoCheckPho(@Param("sDate") String sDate, @Param("eDate") String eDate,@Param("country") String country, @Param("listorder") String listorder, @Param("realname") int realname);

    @Update("update wcuser set checkpho=1 where name like #{phone} ")
    Integer updateNoCheckPho(@Param("phone")String phone);

    @Update("update wcuser set isalive=#{isalive} where LOCATE(#{phone},name) > 0 ")
    Integer updatePhoIsalive(@Param("phone")String phone,@Param("isalive")Integer isalive);

    @Select("select ifnull(right(name,11),'0') from wcuser where isalive=1 and right(name,10)=#{phone} ")
    String checkPhone(@Param("phone") String phone);

    @Select("SELECT b.phone as phone,b.token as token FROM wcuser a ,wcphone b where right(a.name,12)=b.phone AND a.isalive=-1")
    List<Map<String,Object>> getExceptionPhone();


    @Select("select DISTINCT a.id,a.name,a.psw,concat(left(a._62,50),'...') as _62,a.export,a.realname,a.checkpho,a.ctime,a.exporttime,a.ip," +
            "b.ip as reip from wcuser a LEFT JOIN wcphone b ON RIGHT (a.NAME, 10) = RIGHT(b.phone, 10) where (_62 is not null or _62 != '') and isalive=1 and export=#{export} and realname=#{realname} and checkpho=#{checkpho} " +
            "and a.ctime BETWEEN #{bTime} AND  #{eTime} and right(name,11) like '1%' order by a.ctime ${sort} limit #{count} ")
    List<Map<String, String>> searchUsData(Map<String,Object> params);

    @Select("select a.id,a.name,a.psw,concat(left(a._62,50),'...') as _62,a.export,a.realname,a.checkpho,a.ctime,a.exporttime,a.ip," +
            "b.ip as reip from wcuser a LEFT JOIN wcphone b ON RIGHT (a.NAME, 10) = RIGHT(b.phone, 10) where (_62 is not null or _62 != '') and isalive=1 and export=#{export} and realname=#{realname} and checkpho=#{checkpho} " +
            "and a.ctime BETWEEN #{bTime} AND  #{eTime} and right(name,12) like '44%' order by a.ctime ${sort} limit #{count} ")
    List<Map<String, String>> searchUkData(Map<String,Object> params);

}
