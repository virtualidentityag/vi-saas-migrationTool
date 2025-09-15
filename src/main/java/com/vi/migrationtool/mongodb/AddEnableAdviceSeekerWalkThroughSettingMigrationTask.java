package com.vi.migrationtool.mongodb;

import static java.util.Objects.nonNull;

import com.vi.migrationtool.common.MigrationTasks;
import com.vi.migrationtool.config.BeanAwareSpringLiquibase;
import com.vi.migrationtool.mongodb.model.ApplicationSettingsEntity;
import com.vi.migrationtool.mongodb.service.ApplicationSettingService;
import com.vi.migrationtool.schemas.model.EnableAdviceSeekerWalkThrough;
import liquibase.database.Database;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class AddEnableAdviceSeekerWalkThroughSettingMigrationTask extends MigrationTasks {

  private Boolean initialValue;
  private Boolean readOnly;

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
          "Skipping migration AddEnableAdviceSeekerWalkThroughSettingMigrationTask setting already exist.");
    }
  }

  private void applyMigration(
      ApplicationSettingService applicationSettingService,
      ApplicationSettingsEntity applicationSettingsEntity) {
    EnableAdviceSeekerWalkThrough enableAdviceSeekerWalkThrough =
        new EnableAdviceSeekerWalkThrough();
    enableAdviceSeekerWalkThrough.setValue(initialValue);
    enableAdviceSeekerWalkThrough.setReadOnly(readOnly);
    applicationSettingsEntity.setEnableAdviceSeekerWalkThrough(enableAdviceSeekerWalkThrough);
    applicationSettingService.updateApplicationSettings(applicationSettingsEntity);
    log.info(
        "Applied migration to add EnableAdviceSeekerWalkThrough with default settings: value {}, readOnly: {}",
        initialValue,
        readOnly);
  }

  private boolean shouldApplyMigration(ApplicationSettingsEntity applicationSettingsEntity) {
    return !enableAdviceSeekerWalkThroughExists(applicationSettingsEntity);
  }

  private boolean enableAdviceSeekerWalkThroughExists(
      ApplicationSettingsEntity applicationSettingsEntity) {
    return nonNull(applicationSettingsEntity.getEnableAdviceSeekerWalkThrough())
        && nonNull(applicationSettingsEntity.getEnableAdviceSeekerWalkThrough().getValue());
  }
}
