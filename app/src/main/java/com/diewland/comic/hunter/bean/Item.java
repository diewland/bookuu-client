package com.diewland.comic.hunter.bean;

import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class Item implements Serializable {

	private String code;
	private String format;
	private String title;
	private List<Long> timestamps;
	private String remark;

	public Item(String code, String format, String title, String remark){
		this.code = code;
		this.format = format;
		this.title = title;
		this.timestamps = Arrays.asList(System.currentTimeMillis());
		this.remark = remark;

	}

	public Item(String code, String format, String title){
		this(code, format, title, null);
	}

	public String getCode(){
		return this.code;
	}

	public String getFormat(){
		return this.format;
	}

	public String getTitle(){
		return this.title;
	}

	public long getTimestamp(){ // latest item
		return this.timestamps.get(this.timestamps.size()-1);
	}

	public List<Long> getTimestamps(){
		return this.timestamps;
	}

	public String getStringTime(){ // latest item
        return time2str(this.getTimestamp());
	}

	public void setRemark(String remark){
		this.remark = remark;
	}

	public String getRemark(){
		return this.remark;
	}

	// utility
	public String time2str(Long timestamp){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date resultdate = new Date(timestamp);
        return sdf.format(resultdate);
	}

	// test
	public static void main(String[] args){
		
		Item a = new Item("0001", "QR_CODE", "diewland");
		System.out.println(a.getCode());
		System.out.println(a.getTitle());
		System.out.println(a.getTimestamp());
		System.out.println(a.getStringTime());

		System.out.println("---------");

		Item b = new Item("0002", "QR_CODE", "diewligo", "female diewland");
		System.out.println(b.getCode());
		System.out.println(b.getTitle());
		System.out.println(b.getTimestamp());
		System.out.println(b.getStringTime());
		System.out.println(b.getRemark());


	}
}
