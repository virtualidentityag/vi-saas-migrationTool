package com.vi.migrationtool.mongodb;

import com.vi.migrationtool.common.MigrationTasks;
import com.vi.migrationtool.config.BeanAwareSpringLiquibase;
import com.vi.migrationtool.mongodb.model.ApplicationSettingsEntity;
import com.vi.migrationtool.mongodb.service.ApplicationSettingService;
import liquibase.database.Database;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class AddReleaseToggleMigrationTask extends MigrationTasks {

  private String key;
  private Object value;

  @Override
  public void execute(Database database) {
    ApplicationSettingService applicationSettingService =
        BeanAwareSpringLiquibase.getBean(ApplicationSettingService.class);
    migrate(applicationSettingService);
  }

  public void migrate(ApplicationSettingService applicationSettingService) {
    ApplicationSettingsEntity applicationSettingsEntity =
        applicationSettingService.getApplicationSetting();
    if (shouldApplyMigration(applicationSettingsEntity)) {
      applyMigration(applicationSettingService, applicationSettingsEntity);
    } else {
      log.info(
          "Skipping migration AddTenantAdminCanEditLegalTextsMigrationTask, LegalContentChangesBySingleTenantAdminsAllowed setting already exist.");
    }
  }

  private void applyMigration(
      ApplicationSettingService applicationSettingService,
      ApplicationSettingsEntity applicationSettingsEntity) {

    updateApplicationSettingsEntity(applicationSettingsEntity);

    applicationSettingService.updateApplicationSettings(applicationSettingsEntity);
    log.info(
        "Applied migration to add release toggle with default settings: key {}, value: {}",
        key,
        value);
  }

  private void updateApplicationSettingsEntity(
      ApplicationSettingsEntity applicationSettingsEntity) {
    if ("trueAsString".equals(value)) {
      applicationSettingsEntity.setReleaseToggles(key, "true");
    } else if ("falseAsString".equals(value)) {
      applicationSettingsEntity.setReleaseToggles(key, "false");
    } else {
      applicationSettingsEntity.setReleaseToggles(key, value);
    }
  }

  private boolean shouldApplyMigration(ApplicationSettingsEntity applicationSettingsEntity) {
    return !releaseToggleExistsAndHasNonNullValue(applicationSettingsEntity);
  }

  private boolean releaseToggleExistsAndHasNonNullValue(
      ApplicationSettingsEntity applicationSettingsEntity) {
    return applicationSettingsEntity.getReleaseToggles() != null
        && applicationSettingsEntity.getReleaseToggles().containsKey(key)
        && applicationSettingsEntity.getReleaseToggles().get(key) != null;
  }
}
