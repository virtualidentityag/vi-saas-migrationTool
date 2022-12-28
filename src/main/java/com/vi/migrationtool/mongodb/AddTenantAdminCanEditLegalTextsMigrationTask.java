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
public class AddTenantAdminCanEditLegalTextsMigrationTask extends MigrationTasks {

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
          "Skipping migration AddTenantAdminCanEditLegalTextsMigrationTask, LegalContentChangesBySingleTenantAdminsAllowed setting already exist.");
    }
  }

  private void applyMigration(
      ApplicationSettingService applicationSettingService,
      ApplicationSettingsEntity applicationSettingsEntity) {
    LegalContentChangesBySingleTenantAdminsAllowed legalContentChangesBySingleTenantAdminsAllowed =
        new LegalContentChangesBySingleTenantAdminsAllowed();
    legalContentChangesBySingleTenantAdminsAllowed.setValue(initialValue);
    legalContentChangesBySingleTenantAdminsAllowed.setReadOnly(readOnly);
    applicationSettingsEntity.setLegalContentChangesBySingleTenantAdminsAllowed(
        legalContentChangesBySingleTenantAdminsAllowed);
    applicationSettingService.updateApplicationSettings(applicationSettingsEntity);
    log.info(
        "Applied migration to add legalContentChangesBySingleTenantAdminsAllowed with default settings: value {}, readOnly: {}",
        initialValue,
        readOnly);
  }

  private boolean shouldApplyMigration(ApplicationSettingsEntity applicationSettingsEntity) {
    return !legalChangesBySingleTenantAdminExist(applicationSettingsEntity);
  }

  private boolean legalChangesBySingleTenantAdminExist(
      ApplicationSettingsEntity applicationSettingsEntity) {
    return applicationSettingsEntity.getLegalContentChangesBySingleTenantAdminsAllowed() != null
        && applicationSettingsEntity.getLegalContentChangesBySingleTenantAdminsAllowed().getValue()
            != null;
  }
}
