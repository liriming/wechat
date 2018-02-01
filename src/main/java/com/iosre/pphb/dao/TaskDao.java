package com.iosre.pphb.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface TaskDao {


    @Insert("insert into task(task_time) values(#{task_time})")
    Integer insertTask(@Param("task_time") String task_time);

    @Insert("insert into task1(task_time) values(#{task_time})")
    Integer insertTask1(@Param("task_time") String task_time);
}
