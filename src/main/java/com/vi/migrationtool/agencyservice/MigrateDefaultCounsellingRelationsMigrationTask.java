package com.vi.migrationtool.agencyservice;

import com.vi.migrationtool.common.MigrationTasks;
import com.vi.migrationtool.config.BeanAwareSpringLiquibase;
import liquibase.database.Database;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
public class MigrateDefaultCounsellingRelationsMigrationTask extends MigrationTasks {

  private final JdbcTemplate agencyServiceJdbcTemplate;

  public MigrateDefaultCounsellingRelationsMigrationTask() {
    this.agencyServiceJdbcTemplate =
        BeanAwareSpringLiquibase.getNamedBean("agencyServiceJdbcTemplate", JdbcTemplate.class);
  }

  @Override
  public void execute(Database database) {
    log.info("Migrating agency counselling reations to default values");
    try {
      agencyServiceJdbcTemplate.update(
          "update agency set counselling_relations = 'SELF_COUNSELLING,PARENTAL_COUNSELLING,RELATIVE_COUNSELLING' "
              + "where counselling_relations is null");
    } catch (Exception e) {
      log.error("Error while migrating agency counselling relations to default values", e);
      throw e;
    }
  }
}
