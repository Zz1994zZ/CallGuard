package com.zz.bean;

import java.util.Date;
public class CallInfo {
	private int id;
	private String msg;
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	private String time; //¼ÇÂ¼Ê±¼ä
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	private String  duration; 
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	private String tel;
	//
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	//
	//
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
}
