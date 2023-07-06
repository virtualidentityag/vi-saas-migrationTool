package com.vi.migrationtool.consultingtypeservice.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class ConsultingTypeServiceJpaConfiguration {

  @Bean
  @Qualifier("consultingTypeServiceDataSource")
  @ConfigurationProperties("spring.datasource.consultingtypeservice")
  public DataSourceProperties consultingTypeServiceJdbcTemplateProperties() {
    return new DataSourceProperties();
  }

  @Bean
  public DataSource consultingtypeServiceDatasource() {
    return consultingTypeServiceJdbcTemplateProperties().initializeDataSourceBuilder().build();
  }

  @Bean
  @Qualifier("consultingTypeServiceJdbcTemplate")
  public JdbcTemplate consultingTypeServiceJdbcTemplate(
      @Qualifier("consultingtypeServiceDatasource") DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }
}
