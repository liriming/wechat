package com.iosre.pphb.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

public interface WcuserDao {

    @Insert("insert into wcuser(name,psw,_62,phoneno,isalive,ip,realname,rname,rcard) values (#{name},#{psw},#{_62},#{phoneno},#{isalive},#{ip},#{realname},#{rname},#{rcard})")
    int insertDataInfo(@Param("name") String name, @Param("psw") String psw, @Param("_62") String _62, @Param("phoneno") String phoneno, @Param("isalive") Integer isalive,
                       @Param("ip") String ip, @Param("realname") Integer realname, @Param("rname") String rname, @Param("rcard") String rcard);


    @Select("select id,name,psw,_62 from wcuser where (_62 is not null or _62 != '') and isalive=1 and export=0 and realname=#{realname} " +
            "and ctime BETWEEN #{sDate} AND  #{eDate} order by ctime ${listorder} limit #{count} ")
    List<Map<String, Object>> getExportData(@Param("count") int count, @Param("sDate") String sDate, @Param("eDate") String eDate, @Param("listorder") String listorder, @Param("realname") int realname);


    @Select("select count(id) from wcuser where (_62 is not null or _62 != '') and isalive=1 and export=0 and realname=#{realname} " +
            "and ctime BETWEEN #{sDate} AND  #{eDate} ")
    Integer getExportDataNum(@Param("sDate") String sDate, @Param("eDate") String eDate, @Param("realname") int realname);

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


    @Select("select phone from wcuser where (_62 is not null or _62 != '') and isalive=1 and export=0 and realname=#{realname} " +
            "and ctime BETWEEN #{sDate} AND  #{eDate} and check=0 order by ctime ${listorder} limit 1 ")
    String getNoCheckPho(@Param("sDate") String sDate, @Param("eDate") String eDate, @Param("listorder") String listorder, @Param("realname") int realname);

    @Update("update wcuser set check=1 where phone=#{phone} ")
    Integer updateNoCheckPho(@Param("phone")String phone);

    @Update("update wcuser set isalive=#{isalive} where phone=#{phone} ")
    Integer updatePhoIsalive(@Param("phone")String phone,@Param("isalive")Integer isalive);

}
