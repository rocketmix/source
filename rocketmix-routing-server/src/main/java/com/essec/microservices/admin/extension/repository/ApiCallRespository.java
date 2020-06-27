package com.essec.microservices.admin.extension.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.essec.microservices.admin.extension.model.ApiCallEntry;

@Repository
@Transactional(propagation = Propagation.REQUIRED, transactionManager = "inmemoryTransactionManager")
public interface ApiCallRespository extends JpaRepository<ApiCallEntry, Long> {
	
	// JPA Spring projection
	public interface ApiCallServiceAndCount {
	    String getService();
	    Integer getCounter();
	}
	
	
	// JPA Spring projection
	public interface ApiCallCounter {
		Date getDate();
		String getService();
		Long getCounter();
	}
	
	
	
	@Query("select t from ApiCallEntry t order by t.activityDate desc")
    public Page<ApiCallEntry> findAllOrderByActivityDateDesc(Pageable pageable);

	@Query("select t.activityDate as date, t.serviceId as service, count(t) as counter from ApiCallEntry t where t.activityDate > :fromDate group by hour(t.activityDate), t.serviceId order by hour(t.activityDate)")
    public List<ApiCallCounter> countByServiceIdAndHour(Date fromDate);
	
	@Modifying
	@Query("delete from ApiCallEntry t where t.activityDate < :expirationDate")
	public void deleteExpired(Date expirationDate);
	
	@Query("select t.serviceId as service, count(t) as counter from ApiCallEntry t where t.activityDate >= :activityDate group by t.serviceId")
	public List<ApiCallServiceAndCount> countWithActivityDateAdter(@Param("activityDate") Date activityDateTime);
	

	

}
