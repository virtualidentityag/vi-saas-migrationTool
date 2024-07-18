package com.vi.migrationtool.tenantservice;

import com.vi.migrationtool.keycloak.TenantUpdateService;
import com.vi.migrationtool.keycloak.UserTenant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

@AllArgsConstructor
@Slf4j
public class TenantMigrationService {

  public static final String UPDATED = "Updated ";
  public static final String SOURCE_TENANT_TARGET_TENANT = "Source tenant: {}, Target tenant: {}";
  private final JdbcTemplate tenantServiceJdbcTemplate;

  private final JdbcTemplate userServiceJdbcTemplate;

  private final JdbcTemplate agencyServiceJdbcTemplate;

  private final TenantUpdateService tenantUpdateService;

  public void performMigration(TenantMigrationConfiguration migration) {
    log.info(
        "Started migration to change tenant from {} to {}",
        migration.getSourceTenantId(),
        migration.getTargetTenantId());
    updateAgencyServiceTenantId(migration);
    updateUserServiceTenantId(migration);
    if (migration.isDeleteSourceTenant()) {
      deleteSourceTenant(migration);
    }
  }

  private void updateAgencyServiceTenantId(TenantMigrationConfiguration migration) {
    log.info("Updating tenantId in agency service");
    int updatedRows =
        agencyServiceJdbcTemplate.update(
            "update agency set tenant_id = ? where tenant_id = ?",
            migration.getTargetTenantId(),
            migration.getSourceTenantId());

    log.info(UPDATED + updatedRows + " rows in agency tables");

    updatedRows =
        agencyServiceJdbcTemplate.update(
            "update agency_postcode_range set tenant_id = ? where tenant_id = ?",
            migration.getTargetTenantId(),
            migration.getSourceTenantId());

    log.info(UPDATED + updatedRows + " rows in agency postcode range tables");
  }

  private void updateUserServiceTenantId(TenantMigrationConfiguration migration) {

    updateUsersInKeycloakAndDatabase(migration);
    updateDatabaseTablesIfAnyNonMigratedEntriesLeft(migration);
  }

  private void deleteSourceTenant(TenantMigrationConfiguration migration) {
    log.info("Deleting source tenant {}", migration.getSourceTenantId());
    tenantServiceJdbcTemplate.update(
        "delete from tenant where id = ?", migration.getSourceTenantId());
  }

  private void updateUsersInKeycloakAndDatabase(TenantMigrationConfiguration migration) {

    log.info(
        "Step: attempt to find and update adviceseekers in keycloak and database. "
            + SOURCE_TENANT_TARGET_TENANT,
        migration.getSourceTenantId(),
        migration.getTargetTenantId());
    List<UserTenant> adviceSeekerTargetTenants =
        userServiceJdbcTemplate.query(
            "select u.user_id \n"
                + "from user u \n"
                + "where u.tenant_id = "
                + migration.getSourceTenantId(),
            (rs, rowNum) -> {
              String userId = rs.getString("user_id");
              return new UserTenant(userId, migration.getTargetTenantId());
            });

    tenantUpdateService.updateAdviceSeekersTenant(adviceSeekerTargetTenants);

    log.info(
        "Step: attempt to find and update consultants in keycloak and database. "
            + SOURCE_TENANT_TARGET_TENANT,
        migration.getSourceTenantId(),
        migration.getTargetTenantId());
    List<UserTenant> consultantTargetTenants =
        userServiceJdbcTemplate.query(
            "select consultant_id \n"
                + "from consultant c \n"
                + "where c.tenant_id = "
                + migration.getSourceTenantId(),
            (rs, rowNum) -> {
              String consultantId = rs.getString("consultant_id");
              return new UserTenant(consultantId, migration.getTargetTenantId());
            });

    tenantUpdateService.updateConsultantsTenant(consultantTargetTenants);

    log.info(
        "Step: attempt to find and update admins in keycloak and database. "
            + SOURCE_TENANT_TARGET_TENANT,
        migration.getSourceTenantId(),
        migration.getTargetTenantId());
    List<UserTenant> adminTargetTenants =
        userServiceJdbcTemplate.query(
            "select admin_id \n"
                + "from admin a \n"
                + "where a.tenant_id = "
                + migration.getSourceTenantId(),
            (rs, rowNum) -> {
              String adminId = rs.getString("admin_id");
              return new UserTenant(adminId, migration.getTargetTenantId());
            });

    tenantUpdateService.updateAdminTenant(adminTargetTenants);
  }

  private void updateDatabaseTablesIfAnyNonMigratedEntriesLeft(
      TenantMigrationConfiguration migration) {
    log.info(
        "Running updates on database tables - changing tenantId from {} to {}",
        migration.getSourceTenantId(),
        migration.getTargetTenantId());
    log.info(
        "If all users were correctly migrated in previous steps, there should be no updates here");
    updateTable("consultant", migration);
    updateTable("session", migration);
    updateTable("user", migration);
    updateTable("consultant_agency", migration);
  }

  private void updateTable(String tableName, TenantMigrationConfiguration migration) {
    int updatedRows =
        userServiceJdbcTemplate.update(
            "update " + tableName + " set tenant_id = ? where tenant_id = ?",
            migration.getTargetTenantId(),
            migration.getSourceTenantId());
    log.info(UPDATED + updatedRows + " " + tableName + " rows");
  }
}
