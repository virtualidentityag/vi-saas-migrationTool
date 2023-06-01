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
public class TenantServiceJpaConfiguration {

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
  @Qualifier("tenantServiceDataSource")
  @ConfigurationProperties("spring.datasource.tenantservice")
  public DataSourceProperties tenantServiceDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  public DataSource tenantServiceDataSource() {
    return tenantServiceDataSourceProperties().initializeDataSourceBuilder().build();
  }

  @Bean
  @Qualifier("tenantServiceJdbcTemplate")
  public JdbcTemplate tenantServiceJdbcTemplate(
      @Qualifier("tenantServiceDataSource") DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }
}
