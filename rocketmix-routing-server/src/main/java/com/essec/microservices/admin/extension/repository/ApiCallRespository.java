package com.essec.microservices.admin.extension.repository;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.essec.microservices.admin.extension.model.ApiCallEntry;

@Repository
@Transactional(propagation = Propagation.REQUIRED, transactionManager = "inmemoryTransactionManager")
public interface ApiCallRespository extends JpaRepository<ApiCallEntry, Long> {
	
	@Query("select t from ApiCall t order by t.date desc")
    public Page<ApiCallEntry> findLatest(Pageable pageable);
	
	@Modifying
	@Query("delete from ApiCall t where t.date < :expirationDate")
	public void deleteExpired(Date expirationDate);

}
