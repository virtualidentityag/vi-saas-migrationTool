package com.vi.migrationtool.tenantservice;

import com.vi.migrationtool.common.MigrationTasks;
import com.vi.migrationtool.config.BeanAwareSpringLiquibase;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import liquibase.database.Database;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
public class ImportTenantsFromDiocesesMigrationTask extends MigrationTasks {

  private static final String dioceseTableName = "diocese";

  @Value
  static class Diocese {
    int id;
    String name;
  }

  @Builder
  static class Tenant {
    private String name;
  }

  @Override
  public void execute(Database database) {
    log.info("Starting import of dioceses from agency to tenant");
    val agencyJdbcTemplate =
        BeanAwareSpringLiquibase.getNamedBean("agencyServiceJdbcTemplate", JdbcTemplate.class);

    var dioceseTableExist = doesDioceseTableExist(agencyJdbcTemplate);
    if (!dioceseTableExist) {
      log.info("diocese table does not exist, skipping import");
      return;
    }
    val dioceses = getAllDiocesesFromAgencyDB(agencyJdbcTemplate);

    log.info("All dioceses: {}", dioceses);

    var tenantServiceJdbcTemplate =
        BeanAwareSpringLiquibase.getNamedBean("tenantServiceJdbcTemplate", JdbcTemplate.class);

    val existingTenants = getAllTenantsFromTenantsDB(tenantServiceJdbcTemplate);

    val newTenants = getNewTenants(dioceses, existingTenants);

    log.info("New tenants: {}", newTenants);

    val result = insertNewTenants(newTenants, tenantServiceJdbcTemplate);

    log.info("Inserted {} new tenants", result.length);

    setTenantIdOnAgency(agencyJdbcTemplate, dioceses, tenantServiceJdbcTemplate);

    log.info("Finished migration of dioceses to tenants");
  }

  private static void setTenantIdOnAgency(
      JdbcTemplate agencyJdbcTemplate,
      List<Diocese> dioceses,
      JdbcTemplate tenantServiceJdbcTemplate) {
    @Value
    class TenantIdAndName {
      int id;
      String name;
    }
    log.info("Setting tenant_id on agency");
    val tenants =
        tenantServiceJdbcTemplate.query(
            "select id, name from tenant",
            (rs, rowNum) -> new TenantIdAndName(rs.getInt("id"), rs.getString("name")));

    Predicate<Diocese> newTenantsOnly =
        d -> tenants.stream().noneMatch(tenant -> tenant.name.equals(d.name));
    agencyJdbcTemplate.batchUpdate(
        "update agency set tenant_id = ? where diocese_id = ?",
        dioceses.stream()
            .filter(newTenantsOnly)
            .map(
                d ->
                    new Object[] {
                      tenants.stream()
                          .filter(tenant -> tenant.name.equals(d.name))
                          .findFirst()
                          .orElseThrow()
                          .id,
                      d.id
                    })
            .collect(Collectors.toList()));

    log.info("Finished setting tenant_id on agency");
  }

  private static int[] insertNewTenants(
      List<Tenant> newTenants, JdbcTemplate tenantServiceJdbcTemplate) {
    return tenantServiceJdbcTemplate.batchUpdate(
        "insert into tenant(name, subdomain) values (?,?)",
        newTenants.stream()
            .map(tenant -> new Object[] {tenant.name, ""})
            .collect(Collectors.toList()));
  }

  private List<Tenant> getNewTenants(
      List<Diocese> dioceses, List<Tenant> existingTenantsInTenantDB) {
    return dioceses.stream()
        .filter(
            diocese ->
                existingTenantsInTenantDB.stream()
                    .noneMatch(tenant -> tenant.name.equals(diocese.name)))
        .map(diocese -> Tenant.builder().name(diocese.name).build())
        .collect(Collectors.toList());
  }

  private List<Diocese> getAllDiocesesFromAgencyDB(JdbcTemplate agencyJdbcTemplate) {
    return agencyJdbcTemplate.query(
        "select name, id from " + dioceseTableName,
        (rs, rowNum) -> new Diocese(rs.getInt("id"), rs.getString("name")));
  }

  private List<Tenant> getAllTenantsFromTenantsDB(JdbcTemplate tenantJdbcTemplate) {
    return tenantJdbcTemplate.query(
        "select name from tenant", (rs, rowNum) -> new Tenant(rs.getString("name")));
  }

  private boolean doesDioceseTableExist(JdbcTemplate agencyJdbcTemplate) {
    val dioceseTableExistsQuery =
        "select count(*) from information_schema.tables where table_name = ? and table_schema = 'agencyservice';";
    val result =
        agencyJdbcTemplate.queryForObject(dioceseTableExistsQuery, Integer.class, dioceseTableName);
    return result != null && result == 1;
  }
}
