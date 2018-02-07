package com.iosre.pphb.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iosre.pphb.util.XDateUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Set;

@Getter
@Setter
public class UserOpLog {
	private static final ObjectMapper objectMapper = new ObjectMapper();

	private String id;
	private String ip;
	private String method;
	private String url;
	private String controller;
	private String rMsg;
	private long ms;
	private long ctime;
	Set<Object> params;
	String result;

	public String getCtime(){
		return XDateUtils.timestampToString(ctime, XDateUtils.DatePattern.DATE_TIME.getPattern());
	}

	public String getFormattedParams() throws JsonProcessingException{
		return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(params);
	}
}