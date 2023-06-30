package com.vi.migrationtool.consultingtypeservice.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class ConsultingTypeServiceJpaConfiguration {

  @Bean
  @Qualifier("consultingTypeServiceJdbcTemplate")
  @ConfigurationProperties("spring.datasource.consultingtypeservice")
  public DataSourceProperties consultingTypeServiceJdbcTemplateProperties() {
    return new DataSourceProperties();
  }

  @Bean
  public DataSource consultingTypeServiceJdbcTemplate() {
    return consultingTypeServiceJdbcTemplateProperties().initializeDataSourceBuilder().build();
  }

  @Bean
  @Qualifier("consultingTypeServiceJdbcTemplate")
  public JdbcTemplate consultingTypeServiceJdbcTemplate(
      @Qualifier("consultingTypeServiceJdbcTemplate") DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }
}
