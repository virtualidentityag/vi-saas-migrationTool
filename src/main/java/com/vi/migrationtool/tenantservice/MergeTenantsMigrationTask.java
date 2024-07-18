package com.vi.migrationtool.tenantservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vi.migrationtool.common.MigrationTasks;
import com.vi.migrationtool.config.BeanAwareSpringLiquibase;
import com.vi.migrationtool.keycloak.KeycloakLoginService;
import com.vi.migrationtool.keycloak.TenantUpdateService;
import java.util.List;
import liquibase.database.Database;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.helper.Validate;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@Setter
public class MergeTenantsMigrationTask extends MigrationTasks {

  String tenantMigrationConfiguration;
  private KeycloakLoginService keycloakLoginService;

  @Override
  public void execute(Database database) {
    Validate.notNull(
        tenantMigrationConfiguration, "Tenant migration configuration must not be null");
    var tenantServiceJdbcTemplate =
        BeanAwareSpringLiquibase.getNamedBean("tenantServiceJdbcTemplate", JdbcTemplate.class);
    var userServiceJdbcTemplate =
        BeanAwareSpringLiquibase.getNamedBean("userServiceJdbcTemplate", JdbcTemplate.class);

    var tenantUpdateService =
        BeanAwareSpringLiquibase.getNamedBean("tenantUpdateService", TenantUpdateService.class);

    var agencyServiceJdbcTemplate =
        BeanAwareSpringLiquibase.getNamedBean("agencyServiceJdbcTemplate", JdbcTemplate.class);


    this.keycloakLoginService =
        BeanAwareSpringLiquibase.getNamedBean("keycloakLoginService", KeycloakLoginService.class);

    var objectMapper =
        new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    var migrations = readMigrations(objectMapper);

    TenantMigrationService tenantMigrationService =
        new TenantMigrationService(
            tenantServiceJdbcTemplate,
            userServiceJdbcTemplate,
            agencyServiceJdbcTemplate,
            tenantUpdateService);

    log.info("Read migration configuration: {}", migrations);

    performMigrations(migrations, tenantMigrationService);
  }

  private void performMigrations(List<TenantMigrationConfiguration> migrations,
      TenantMigrationService tenantMigrationService) {
    migrations.stream()
        .forEach(
            migration -> {
              log.info(
                  "Migrating tenants from {} to {}",
                  migration.getSourceTenantId(),
                  migration.getTargetTenantId());
              tenantMigrationService.performMigration(migration);
            });
  }

  private List<TenantMigrationConfiguration> readMigrations(ObjectMapper objectMapper) {
    try {
      TypeReference<List<TenantMigrationConfiguration>> typeReference =
          new TypeReference<List<TenantMigrationConfiguration>>() {};
      return objectMapper.readValue(tenantMigrationConfiguration, typeReference);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Could not process json file ", e);
    }
  }
}
