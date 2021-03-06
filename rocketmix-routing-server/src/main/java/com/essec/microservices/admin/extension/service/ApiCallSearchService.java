package com.essec.microservices.admin.extension.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.essec.microservices.admin.extension.model.ApiCallEntry;
import com.essec.microservices.admin.extension.repository.ApiCallRespository;

@Service
@Transactional(propagation = Propagation.REQUIRED, transactionManager = "inmemoryTransactionManager")
public class ApiCallSearchService {
	
	private static final int MAX_RESULT = 100;
	
	private static final int REPOSITORY_MAX_SIZE = 50000;
	
	private static final int MAX_RESPONSE_SIZE = 4000;

	@Autowired
	@Qualifier("inmemoryEntityManager")
	private EntityManager entityManager;
	
	@Autowired
	private ApiCallRespository repository; 
	
	public List<ApiCallEntry> performSearch(String keyword) {
		if (StringUtils.isNotBlank(keyword)) {
			keyword = keyword + "*";
		}
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(ApiCallEntry.class).get();
		Query luceneQuery = qb.simpleQueryString().onFields("requestURL", "requestData", "responseData").boostedTo(1f)
				.withAndAsDefaultOperator().matching(keyword).createQuery();
		javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, ApiCallEntry.class);
		jpaQuery.setMaxResults(MAX_RESULT);
		try {
			return jpaQuery.getResultList();
		} catch (NoResultException nre) {
			return new ArrayList<ApiCallEntry>();
		}
	}
	
	public List<ApiCallEntry> findAll() {
		Page<ApiCallEntry> page = this.repository.findAll(PageRequest.of(0, MAX_RESULT));
		return page.stream().collect(Collectors.toList());
	}
	
	public Long save(ApiCallEntry entity) {
		entity = this.repository.save(entity);
		return entity.getId();
	}
	
	public void update(Long id, String responseData, int responseCode) {
		ApiCallEntry entity = this.repository.getOne(id);
		if (entity == null) {
			return;
		}
		entity.setResponseData(responseData);
		entity.setResponseCode(responseCode);
		this.repository.save(entity);
	}
	
	
	@Scheduled(fixedDelay = 1000)
	public void purgeExpiredentities() {
		long count = this.repository.count();
		if (count <= REPOSITORY_MAX_SIZE) {
			return;
		}
		Page<ApiCallEntry> page = this.repository.findAllOrderByActivityDateDesc(PageRequest.of(0, REPOSITORY_MAX_SIZE));
		if (page.isEmpty()) {
			return;
		}
		long pageSize = page.stream().count();
		ApiCallEntry olderApiCall = page.stream().skip(pageSize - 1).findFirst().get(); // Last stream element
		this.repository.deleteExpired(olderApiCall.getActivityDate());
	}

}
