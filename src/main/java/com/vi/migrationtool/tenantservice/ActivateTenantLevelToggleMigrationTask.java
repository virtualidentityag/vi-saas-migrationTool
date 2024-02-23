package com.vi.migrationtool.tenantservice;

import com.vi.migrationtool.common.MigrationTasks;
import com.vi.migrationtool.config.BeanAwareSpringLiquibase;
import liquibase.database.Database;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
public class ActivateTenantLevelToggleMigrationTask extends MigrationTasks {

  String featureToggleName = "featureCentralDataProtectionTemplateEnabled";

  @Override
  public void execute(Database database) {
    var tenantServiceJdbcTemplate =
        BeanAwareSpringLiquibase.getNamedBean("tenantServiceJdbcTemplate", JdbcTemplate.class);
    log.info("Activating tenant level toggle" + featureToggleName + " for all tenants");
    int[] updatedTenants = activateFeatureToggle(tenantServiceJdbcTemplate);
    log.info("Updated {} tenants", updatedTenants.length);
  }

  private int[] activateFeatureToggle(JdbcTemplate tenantServiceJdbcTemplate) {
    int[] updatedTenants =
        tenantServiceJdbcTemplate.batchUpdate(
            "update tenant set settings = REPLACE( settings ,'\""
                + featureToggleName
                + "\":false','\""
                + featureToggleName
                + "\":true')");
    return updatedTenants;
  }
}
