package com.iosre.pphb.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 验证类，用各种正则表达式验证
 * @author XuJijun
 *
 */
public class XValidator {
	//TODO改为enum
	@SuppressWarnings("serial")
	public static final Map<String, String> format = new HashMap<String, String>(){{
		put("mobilePhone", "^1\\d{10}");
		put("number", "^-?[1-9]\\d*$");
	}};
	
	public static boolean isPhoneNumber(String phoneNo){
		//if(phoneNo.length()==11){
		if(RegexUtils.matches(phoneNo, format.get("mobilePhone"))){
			return true;
		}
		
		return false;
	}
	
	public static boolean isNumber(String number){
		//if(phoneNo.length()==11){
		if(RegexUtils.matches(number, format.get("number"))){
			return true;
		}
		
		return false;
	}
	
	public static void main(String[] args) {
		System.out.println(isPhoneNumber("18676460829"));
		System.out.println(isNumber("-2"));
	}
}
