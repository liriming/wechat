package com.iosre.pphb.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

public interface WcphoneDao {

    @Select("SELECT * FROM wcphone where phone = #{phone}")
    Map<String, Object> getToken(@Param("phone")String phone);

    @Select("SELECT * FROM wcphone where status = 0 limit 1")
    Map<String, Object> getPhoneMsg();

    @Update("Update wcphone set status=#{status} where id=#{id}")
    Integer setStatus(@Param("id")int id,@Param("status")int status);

    @Insert({
            " <script>",
            " insert into wcphone(phone,token,status) VALUES ",
            " <foreach item='item' collection='dataList' separator=','> ",
            " (#{item.phone},#{item.token},0)",
            " </foreach> ",
            " </script>"
    })
    int insertDataInfo(@Param("dataList") List<Map<String, String>> dataList);


    @Select("SELECT count(*) FROM wcphone where status = 0")
    Integer resPhoneNum();

    @Update("Update wcphone set status=#{status} where phone=#{phone}")
    Integer setStatusByPhone(@Param("phone")String phone,@Param("status")int status);

}
