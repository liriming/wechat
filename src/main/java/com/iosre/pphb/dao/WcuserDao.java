package com.iosre.pphb.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

public interface WcuserDao {

    @Insert("insert into wcuser(name,psw,_62,phoneno,isalive,ip,realname) values (#{name},#{psw},#{_62},#{phoneno},#{isalive},#{ip},#{realname})")
    int insertDataInfo(@Param("name")String name,@Param("psw")String psw,@Param("_62")String _62,@Param("phoneno")String phoneno,@Param("isalive")Integer isalive,@Param("ip")String ip,@Param("realname")Integer realname);



    @Select("select id,name,psw,_62 from wcuser where (_62 is not null or _62 != '') and isalive=1 and export=0 limit #{count}")
    List<Map<String,Object>> getExportData(@Param("count")int count);

    @Update({
            " <script> ",
            " update wcuser set export=1 where id in  ",
            " <foreach item='item' collection='list' open='(' separator=',' close=')'> ",
            " #{item} ",
            " </foreach> ",
            " </script> "
    })
    Integer updateExportStatus(@Param("list")List list);

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
    Map<String,Object> getMsg(@Param("sDate")String sDate, @Param("eDate")String eDate);

    @Select("SELECT gzhh FROM wcuser WHERE ctime  BETWEEN  #{sDate} AND  #{eDate} AND _62 != '' AND isalive=1")
    List<String> gzhh(@Param("sDate")String sDate, @Param("eDate")String eDate);

}
