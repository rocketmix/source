package com.essec.microservices.actuator.apicalls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.dizitart.no2.FindOptions;
import org.dizitart.no2.Index;
import org.dizitart.no2.IndexType;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteBuilder;
import org.dizitart.no2.SortOrder;
import org.dizitart.no2.objects.Cursor;
import org.dizitart.no2.objects.ObjectFilter;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;



@Repository
public class ApiCallRespository {
	
	private Nitrite db;
	
	private ObjectRepository<ApiCall> repository;
	
	public void insert(ApiCall call) {
		getRepository().insert(call);
	}
	
	public List<ApiCall> find(String keyword) {
		ObjectFilter searchFilter = getSearchFilter(keyword);
		Cursor<ApiCall> cursor = getRepository().find(searchFilter);
		return cursor.toList();
	}
	
	
	private ObjectFilter getSearchFilter(String keyword) {
		Collection<Index> indices = getRepository().listIndices();
		List<ObjectFilter> filters = new ArrayList<>();
		for (Index anIndex : indices) {
			IndexType indexType = anIndex.getIndexType();
			if (!IndexType.Fulltext.equals(indexType)) {
				continue;
			}
			ObjectFilter filter = ObjectFilters.text(anIndex.getField(), keyword);
			filters.add(filter);
		}
		ObjectFilter searchFilter = ObjectFilters.or(filters.toArray(new ObjectFilter[filters.size()]));
		return searchFilter;
	}
	
	@Scheduled(fixedDelay = 1000)
	public void purge() {
		Cursor<ApiCall> cursor = getRepository().find(FindOptions.sort("date", SortOrder.Descending).thenLimit(10000, 1));
		ApiCall oldCall = cursor.firstOrDefault();
		if (oldCall == null) {
			return;
		}
		getRepository().remove(ObjectFilters.lte("date", oldCall.getDate()));
	}
	
	
	private ObjectRepository<ApiCall> getRepository() {
		if (this.repository == null) {
			this.repository = getDb().getRepository(ApiCall.class);
		}
		return this.repository;
	}
	

	private Nitrite getDb() {
		if (this.db == null) {
			NitriteBuilder builder = Nitrite.builder();
			this.db = builder.compressed().openOrCreate();
		}
		return this.db;
	}

}
