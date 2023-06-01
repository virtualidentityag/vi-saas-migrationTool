package com.vi.migrationtool.tenantservice;

import com.vi.migrationtool.common.MigrationTasks;
import com.vi.migrationtool.config.BeanAwareSpringLiquibase;
import liquibase.database.Database;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
public class ImportDiocesesFromAgencyToTenantServiceTask extends MigrationTasks {

  private static final String CREATE_DIOCESE_TABLE =
      "CREATE TABLE IF NOT EXISTS diocese (id BIGINT NOT NULL AUTO_INCREMENT, name VARCHAR(255) NOT NULL, PRIMARY KEY (id) UNIQUE (name));";

  @Builder
  static class Diocese {
    private String name;
  }

  @Override
  public void execute(Database database) {
    log.info("Starting import of dioceses from agency to tenant");
    val agencyJdbcTemplate =
        BeanAwareSpringLiquibase.getNamedBean("agencyServiceJdbcTemplate", JdbcTemplate.class);

    val dioceseTableExistsQuery =
        "select count(*) from information_schema.tables where table_name = ? and table_schema = 'agencyservice';";
    val result =
        agencyJdbcTemplate.queryForObject(dioceseTableExistsQuery, Integer.class, "diocese");

    boolean dioceseTableDoesNotExist = result == null || result == 0;
    if (dioceseTableDoesNotExist) {
      log.info("Diocese table does not exist in agency service, skipping import");
      return;
    }

    val dioceses =
        agencyJdbcTemplate.query(
            "select name from diocese", (rs, rowNum) -> new Diocese(rs.getString("name")));

    log.info("The following dioceses will be migrated: {}", dioceses);

    var tenantServiceJdbcTemplate =
        BeanAwareSpringLiquibase.getNamedBean("tenantServiceJdbcTemplate", JdbcTemplate.class);

    assertDioceseTableCreatedInTenantService(tenantServiceJdbcTemplate);

    val batchUpdateResult =
        tenantServiceJdbcTemplate.batchUpdate(
            "insert ignore into diocese (name) values (?)",
            dioceses,
            1000,
            (ps, diocese) -> ps.setString(1, diocese.name));

    log.info("Inserted {} rows into dioceses table", batchUpdateResult.length);
    log.info("Finished import of dioceses from agency to tenant");
  }

  private static void assertDioceseTableCreatedInTenantService(
      JdbcTemplate tenantServiceJdbcTemplate) {
    val numAffected = tenantServiceJdbcTemplate.update(CREATE_DIOCESE_TABLE);
    log.info("Created diocese table with {} rows affected", numAffected);
  }
}
