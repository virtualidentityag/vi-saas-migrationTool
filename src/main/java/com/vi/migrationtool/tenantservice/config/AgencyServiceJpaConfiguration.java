package com.vi.migrationtool.tenantservice.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class AgencyServiceJpaConfiguration {

  @Bean
  @Primary
  @ConfigurationProperties("spring.datasource")
  public DataSourceProperties primaryDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @Primary
  @ConfigurationProperties("spring.datasource")
  public DataSource primaryDataSource() {
    return primaryDataSourceProperties().initializeDataSourceBuilder().build();
  }

  @Bean
  @Qualifier("agencyServiceDataSource")
  @ConfigurationProperties("spring.datasource.agencyservice")
  public DataSourceProperties agencyServiceDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  public DataSource agencyServiceDataSource() {
    return agencyServiceDataSourceProperties().initializeDataSourceBuilder().build();
  }

  @Bean
  @Qualifier("agencyServiceJdbcTemplate")
  public JdbcTemplate agencyServiceJdbcTemplate(
      @Qualifier("agencyServiceDataSource") DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }
}
