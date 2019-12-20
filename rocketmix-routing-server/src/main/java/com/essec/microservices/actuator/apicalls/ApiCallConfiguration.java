package com.essec.microservices.actuator.apicalls;

import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableScheduling
@EnableJpaRepositories(basePackageClasses = {ApiCallRespository.class}, transactionManagerRef = "inmemoryTransactionManager", entityManagerFactoryRef = "inmemoryEntityManager")
public class ApiCallConfiguration {

	@Bean
	public ApiCallRequestFilter getLogRequestFilter() {
		return new ApiCallRequestFilter();
	}

	@Bean
	public ApiCallResponseFilter getLogResponseFilter() {
		return new ApiCallResponseFilter();
	}


	@Bean
	public LocalContainerEntityManagerFactoryBean inmemoryEntityManager() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(inmemoryDataSource());
		em.setPackagesToScan(new String[] { ApiCall.class.getPackage().getName() });

		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		HashMap<String, Object> properties = new HashMap<>();
		properties.put("hibernate.hbm2ddl.auto", "create");
		properties.put("hibernate.search.default.directory_provider", "ram");
		properties.put("hibernate.search.default.indexwriter.infostream", "true");
		//properties.put("hibernate.enable_lazy_load_no_trans", "true");
		//properties.put("hibernate.implicit_naming_strategy", ImplicitNamingStrategyComponentPathImpl.class.getName());
		properties.put("hibernate.show_sql", "false");
		em.setJpaPropertyMap(properties);
		return em;
	}

	
	@Bean
	public DataSource inmemoryDataSource() {
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).setName("IN-MEMORY-JPA").build();
	}

	@Bean
	public PlatformTransactionManager inmemoryTransactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(inmemoryEntityManager().getObject());
		return transactionManager;
	}


}
