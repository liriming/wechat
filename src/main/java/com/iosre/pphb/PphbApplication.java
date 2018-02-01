package com.iosre.pphb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.iosre.pphb.dao")
@EnableCaching
@EnableScheduling
@SpringBootApplication
public class PphbApplication {

	public static void main(String[] args) {
		SpringApplication.run(PphbApplication.class, args);
	}
}
