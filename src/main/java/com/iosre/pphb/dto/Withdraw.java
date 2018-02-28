package com.iosre.pphb.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Withdraw {

	private String code;
	private String name;
	private int time;
	private long timestamp;
	private String phoneno;
	private String bak;
	private boolean done = false;
	private boolean send = false;
}