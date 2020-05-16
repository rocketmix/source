package com.essec.microservices.admin.extension.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;

@Entity
@Indexed
@AnalyzerDef(name = "customanalyzer",
tokenizer = @TokenizerDef(factory = WhitespaceTokenizerFactory.class),
filters = {
        @TokenFilterDef(factory = ASCIIFoldingFilterFactory.class), // Replace accented characeters by their simpler counterpart (è => e, etc.)
        @TokenFilterDef(factory = LowerCaseFilterFactory.class) // Lowercase all characters
})
public class ApiCallEntry {
	
	public static final int MAX_RESPONSE_LENGTH = 4000;
	private static final int MAX_REQUEST_LENGTH = 4000;
	private static final int MAX_URL_LENGTH = 2100;

	
	@Id
	@GeneratedValue
	private Long id;
	
	private String serviceId;
	
	@Field
	@Temporal(TemporalType.TIMESTAMP)
	private Date activityDate = new Date();

	@Field
	@Lob
	@Column(length = 2100)
	private String requestURL;

	@Field
	@Lob
	@Column(length = 4000)
	private String requestData;
	
	@Field
	@Lob
	@Column(length = 4000)
	private String responseData;
	
	@Field
	private int responseCode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public Date getActivityDate() {
		return activityDate;
	}

	public void setActivityDate(Date date) {
		this.activityDate = date;
	}

	public String getRequestURL() {
		return requestURL;
	}

	public void setRequestURL(String requestURL) {
		this.requestURL = JpaUtil.truncate(requestURL, MAX_URL_LENGTH);
	}

	public String getRequestData() {
		return requestData;
	}

	public void setRequestData(String requestData) {
		this.requestData = JpaUtil.truncate(requestData, MAX_REQUEST_LENGTH);
	}

	public String getResponseData() {
		return responseData;
	}

	public void setResponseData(String responseData) {
		this.responseData = JpaUtil.truncate(responseData, MAX_RESPONSE_LENGTH);
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responceCode) {
		this.responseCode = responceCode;
	}
	
	
	public static class JpaUtil {
	    public static String truncate(String value, int length) {
	        return value != null && value.length() > length ? value.substring(0, length) : value;
	    }
	}
	

}
