package com.iosre.pphb.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Set;

@Getter
@Setter
public class UserOpLog {
	
	@Id
	private String id;
	

	private String ip;//请求IP
	
	private String method;//方法名
	
	private String url;//请求URL

	@Field("rMsg")
	private String resultMsg;//返回信息
	
	@Field("ms")
	private long costMs;//耗时，单位：毫秒
	private long ctime;//创建时间
	
	@Field("params")
	Set<Object> allParams;//所有未经过处理的参数，包括JSON String，Form Data，和Query String
	
	String result; //返回结果

}