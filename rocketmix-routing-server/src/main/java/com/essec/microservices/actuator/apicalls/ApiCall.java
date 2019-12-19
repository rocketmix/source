package com.essec.microservices.actuator.apicalls;

import java.util.Date;

import org.dizitart.no2.IndexType;
import org.dizitart.no2.objects.Id;
import org.dizitart.no2.objects.Index;
import org.dizitart.no2.objects.Indices;

@Indices({
    @Index(value = "requestURL", type = IndexType.Fulltext),
    @Index(value = "requestData", type = IndexType.Fulltext),
    @Index(value = "responseData", type = IndexType.Fulltext),
    @Index(value = "responceCode", type = IndexType.Fulltext)
})
public class ApiCall {
	
	@Id
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
