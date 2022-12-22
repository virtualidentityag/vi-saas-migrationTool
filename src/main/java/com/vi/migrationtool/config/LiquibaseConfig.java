package com.vi.migrationtool.config;

import javax.sql.DataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LiquibaseProperties.class)
public class LiquibaseConfig {

  private DataSource dataSource;

  private LiquibaseProperties liquibaseProperties;

  public LiquibaseConfig(DataSource dataSource, LiquibaseProperties liquibaseProperties) {
    this.dataSource = dataSource;
    this.liquibaseProperties = liquibaseProperties;
  }

  @Bean
  public SpringLiquibase liquibase() {
    var liquibase = new BeanAwareSpringLiquibase();
    liquibase.setContexts(liquibaseProperties.getContexts());
    liquibase.setChangeLog(liquibaseProperties.getChangeLog());
    liquibase.setChangeLogParameters(liquibaseProperties.getParameters());
    liquibase.setClearCheckSums(liquibaseProperties.isClearChecksums());
    liquibase.setDatabaseChangeLogTable(liquibaseProperties.getDatabaseChangeLogTable());
    liquibase.setDatabaseChangeLogLockTable(liquibaseProperties.getDatabaseChangeLogLockTable());
    liquibase.setDataSource(dataSource);
    liquibase.setDefaultSchema(liquibaseProperties.getDefaultSchema());
    liquibase.setDropFirst(liquibaseProperties.isDropFirst());
    liquibase.setLabels(liquibase.getLabels());
    liquibase.setLiquibaseSchema(liquibaseProperties.getLiquibaseSchema());
    liquibase.setLiquibaseTablespace(liquibaseProperties.getLiquibaseTablespace());
    liquibase.setRollbackFile(liquibaseProperties.getRollbackFile());
    liquibase.setShouldRun(liquibaseProperties.isEnabled());
    liquibase.setTag(liquibaseProperties.getTag());
    liquibase.setTestRollbackOnUpdate(liquibaseProperties.isTestRollbackOnUpdate());
    return liquibase;
  }
}
