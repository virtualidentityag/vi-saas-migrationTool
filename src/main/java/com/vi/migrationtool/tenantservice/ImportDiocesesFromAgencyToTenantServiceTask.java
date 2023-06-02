package com.vi.migrationtool.tenantservice;

import com.vi.migrationtool.common.MigrationTasks;
import com.vi.migrationtool.config.BeanAwareSpringLiquibase;
import java.util.List;
import liquibase.database.Database;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
public class ImportDiocesesFromAgencyToTenantServiceTask extends MigrationTasks {

  private static final String CREATE_DIOCESE_TABLE =
      "CREATE TABLE IF NOT EXISTS diocese (id BIGINT NOT NULL AUTO_INCREMENT, name VARCHAR(255) NOT NULL, PRIMARY KEY (id) UNIQUE (name));";
  private static final String dioceseTableName = "diocese";

  @Builder
  static class Diocese {
    private String name;
  }

  @Override
  public void execute(Database database) {
    log.info("Starting import of dioceses from agency to tenant");
    val agencyJdbcTemplate =
        BeanAwareSpringLiquibase.getNamedBean("agencyServiceJdbcTemplate", JdbcTemplate.class);

    var dioceseTableExist = doesDioceseTableExist(agencyJdbcTemplate, "agencyservice");
    if (!dioceseTableExist) {
      log.info("Diocese table does not exist in agency service, skipping import");
      return;
    }
    dioceseTableExist = doesDioceseTableExist(agencyJdbcTemplate, "tenantservice");
    if (!dioceseTableExist) {
      log.info("Diocese table does not exist in tenant service, skipping import");
      return;
    }

    val dioceses = getAllDiocesesFromAgencyDB(agencyJdbcTemplate);

    log.info("The following dioceses will be migrated: {}", dioceses);

    var tenantServiceJdbcTemplate =
        BeanAwareSpringLiquibase.getNamedBean("tenantServiceJdbcTemplate", JdbcTemplate.class);

    val batchResult = insertMissingDiocesesIntoTenantDB(dioceses, tenantServiceJdbcTemplate);

    log.info("Inserted {} rows into {} table", batchResult.length, dioceseTableName);
    log.info("Finished import of dioceses from agency to tenant");
  }

  private static int[][] insertMissingDiocesesIntoTenantDB(
      List<Diocese> dioceses, JdbcTemplate tenantServiceJdbcTemplate) {
    return tenantServiceJdbcTemplate.batchUpdate(
        "insert ignore into " + dioceseTableName + " (name) values (?)",
        dioceses,
        1000,
        (ps, diocese) -> ps.setString(1, diocese.name));
  }

  private static List<Diocese> getAllDiocesesFromAgencyDB(JdbcTemplate agencyJdbcTemplate) {
    return agencyJdbcTemplate.query(
        "select name from " + dioceseTableName, (rs, rowNum) -> new Diocese(rs.getString("name")));
  }

  private static boolean doesDioceseTableExist(
      JdbcTemplate agencyJdbcTemplate, String tableSchema) {
    val dioceseTableExistsQuery =
        "select count(*) from information_schema.tables where table_name = ? and table_schema = '"
            + tableSchema
            + "';";
    val result =
        agencyJdbcTemplate.queryForObject(dioceseTableExistsQuery, Integer.class, dioceseTableName);
    return result != null && result == 1;
  }
}
