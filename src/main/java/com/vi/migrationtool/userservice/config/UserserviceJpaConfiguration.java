package com.vi.migrationtool.userservice.config;

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
public class UserserviceJpaConfiguration {

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
  @Qualifier("userServiceDataSource")
  @ConfigurationProperties("spring.datasource.userservice")
  public DataSourceProperties userserviceDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  public DataSource userserviceDataSource() {
    return userserviceDataSourceProperties().initializeDataSourceBuilder().build();
  }

  @Bean
  public JdbcTemplate userServiceJdbcTemplate(
      @Qualifier("userserviceDataSource") DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }
}
