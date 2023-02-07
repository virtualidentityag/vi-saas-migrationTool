package com.vi.migrationtool.userservice.config;

import com.vi.migrationtool.userservice.model.Admin;
import java.util.Objects;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackageClasses = Admin.class,
    entityManagerFactoryRef = "userserviceEntityManagerFactory",
    transactionManagerRef = "userserviceTransactionManager")
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

  @Bean
  public LocalContainerEntityManagerFactoryBean userServiceEntityManagerFactory(
      @Qualifier("userserviceDataSource") DataSource dataSource,
      EntityManagerFactoryBuilder builder) {
    return builder.dataSource(userserviceDataSource()).packages(Admin.class).build();
  }

  @Bean
  public PlatformTransactionManager userServiceTransactionManager(
      @Qualifier("userServiceEntityManagerFactory")
          LocalContainerEntityManagerFactoryBean todosEntityManagerFactory) {
    return new JpaTransactionManager(Objects.requireNonNull(todosEntityManagerFactory.getObject()));
  }
}
