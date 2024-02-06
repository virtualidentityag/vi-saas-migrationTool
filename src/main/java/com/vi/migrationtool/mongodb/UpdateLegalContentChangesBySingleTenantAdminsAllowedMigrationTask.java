package com.vi.migrationtool.mongodb;

import com.vi.migrationtool.common.MigrationTasks;
import com.vi.migrationtool.config.BeanAwareSpringLiquibase;
import com.vi.migrationtool.mongodb.model.ApplicationSettingsEntity;
import com.vi.migrationtool.mongodb.service.ApplicationSettingService;
import com.vi.migrationtool.schemas.model.LegalContentChangesBySingleTenantAdminsAllowed;
import liquibase.database.Database;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class UpdateLegalContentChangesBySingleTenantAdminsAllowedMigrationTask
    extends MigrationTasks {

  private Boolean value;

  @Override
  public void execute(Database database) {
    ApplicationSettingService applicationSettingService =
        BeanAwareSpringLiquibase.getBean(ApplicationSettingService.class);
    migrate(applicationSettingService);
  }

  public void migrate(ApplicationSettingService applicationSettingService) {
    ApplicationSettingsEntity applicationSettingsEntity =
        applicationSettingService.getApplicationSetting();
    applyMigration(applicationSettingService, applicationSettingsEntity);
  }

  private void applyMigration(
      ApplicationSettingService applicationSettingService,
      ApplicationSettingsEntity applicationSettingsEntity) {
    LegalContentChangesBySingleTenantAdminsAllowed toggle =
        new LegalContentChangesBySingleTenantAdminsAllowed();
    toggle.setValue(value);
    applicationSettingsEntity.setLegalContentChangesBySingleTenantAdminsAllowed(toggle);
    applicationSettingService.updateApplicationSettings(applicationSettingsEntity);
    log.info(
        "Applied migration to update legalContentChangesBySingleTenantAdminsAllowedMigrationTask with settings: value {}",
        value);
  }
}
