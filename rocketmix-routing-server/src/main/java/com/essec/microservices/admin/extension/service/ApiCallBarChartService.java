package com.essec.microservices.admin.extension.service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.essec.microservices.admin.extension.repository.ApiCallRespository;
import com.essec.microservices.admin.extension.repository.ApiCallRespository.ApiCallCounter;

@Service
public class ApiCallBarChartService {

	private BarChartData barChartData = new BarChartData();
	
	private static final int FIRST_CHAR = Character.getNumericValue('a'); 
	private static final String REFERENCE_PREFIX = "p";
	
	@Autowired
	private ApiCallRespository repository;

	@Scheduled(fixedDelay = 1000)
	public void refresh() {
		updateEntries();
		purgeOldEntries();
	}

	private void updateEntries() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -2);
		Date date = cal.getTime();
		List<ApiCallCounter> countByServiceIdAndHour = repository.countByServiceIdAndHour(date);
		for (ApiCallCounter aCpunter : countByServiceIdAndHour) {
			Date counterDate = aCpunter.getDate();
			if (!this.barChartData.data.containsKey(counterDate)) {
				this.barChartData.data.put(counterDate, new ConcurrentHashMap<String, Long>());
			}
			String service = aCpunter.getService();
			Long counter = aCpunter.getCounter();
			Map<String, Long> entryMap = this.barChartData.data.get(counterDate);
			entryMap.put(service, counter);
		}
	}
	
	private void purgeOldEntries() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -24);
		Date firstReferenceDate = cal.getTime();
		Set<Date> keySet = this.barChartData.data.keySet();
		for (Date aDate : keySet) {
			if (aDate.before(firstReferenceDate)) {
				this.barChartData.data.remove(aDate);
			}
		}
	}

	public BarChartData getCounters() {
		return this.barChartData;
	}
	
	private String getServiceReference(String service) {
		int hashCode = service.hashCode();
		int charCode = hashCode % 26;
		int unicode = FIRST_CHAR + charCode;
		char c = (char) unicode;
		return REFERENCE_PREFIX + c;
	}
	

	
	public static class SlotEntry{
		public String service;;
		public int counter;
	}
	
	
	public static class BarChartData {
		public Map<Date, Map<String, Long>> data = new ConcurrentHashMap<>();
		public Map<String, Object> reference = new HashMap<>();
	}


}
