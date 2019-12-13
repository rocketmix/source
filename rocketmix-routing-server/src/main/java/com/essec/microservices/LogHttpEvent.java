package com.essec.microservices;

import java.util.Date;

public class LogHttpEvent {
	
	private int id;
	
	private Date date;
	
	private String requestURL;
	
	private String requestData;
	
	private String responseData;
	
	private int responceCode;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getRequestURL() {
		return requestURL;
	}

	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}

	public String getRequestData() {
		return requestData;
	}

	public void setRequestData(String requestData) {
		this.requestData = requestData;
	}

	public String getResponseData() {
		return responseData;
	}

	public void setResponseData(String responseData) {
		this.responseData = responseData;
	}

	public int getResponceCode() {
		return responceCode;
	}

	public void setResponceCode(int responceCode) {
		this.responceCode = responceCode;
	}
	
	

}
