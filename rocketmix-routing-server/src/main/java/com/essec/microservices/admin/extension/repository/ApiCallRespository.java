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
	
	@Query("select t from ApiCallEntry t order by t.activityDate desc")
    public Page<ApiCallEntry> findLatest(Pageable pageable);
	
	@Modifying
	@Query("delete from ApiCallEntry t where t.activityDate < :expirationDate")
	public void deleteExpired(Date expirationDate);
	
	@Query("select s as service, count(*) as counter from ApiCallEntry t as joint t.serviceId s where t.date >= :activityDate group by s")
	public List<ApiCallServiceAndCount> countWithActivityDateAdter(@Param("activityDate") Date activityDateTime);

}
