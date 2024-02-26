package com.vi.migrationtool.tenantservice;

import com.vi.migrationtool.common.MigrationTasks;
import com.vi.migrationtool.config.BeanAwareSpringLiquibase;
import liquibase.database.Database;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.helper.Validate;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@Setter
public class EnableTenantLevelToggleMigrationTask extends MigrationTasks {

  String featureToggleName;

  @Override
  public void execute(Database database) {
    Validate.notNull(featureToggleName, "Feature toggle name must not be null");
    var tenantServiceJdbcTemplate =
        BeanAwareSpringLiquibase.getNamedBean("tenantServiceJdbcTemplate", JdbcTemplate.class);
    log.info("Activating tenant level toggle" + featureToggleName + " for all tenants");
    activateFeatureToggle(tenantServiceJdbcTemplate);
  }

  private void activateFeatureToggle(JdbcTemplate tenantServiceJdbcTemplate) {
    int[] updatedTenants =
        tenantServiceJdbcTemplate.batchUpdate(
            "update tenant set settings = REPLACE( settings ,'\""
                + featureToggleName
                + "\":false','\""
                + featureToggleName
                + "\":true') where settings like '%"
                + "\""
                + featureToggleName
                + "\""
                + ":false%';");

    log.info("Updated toggle value for {} tenants", updatedTenants[0]);

    int[] tenantsWithAddedSettings =
        tenantServiceJdbcTemplate.batchUpdate(
            "update tenant set settings = REPLACE(settings, '}',',\""
                + featureToggleName
                + "\":true}')"
                + " where settings not like '%"
                + featureToggleName
                + "%';");

    log.info("Added feature toggle to {} tenants", tenantsWithAddedSettings[0]);
  }
}
