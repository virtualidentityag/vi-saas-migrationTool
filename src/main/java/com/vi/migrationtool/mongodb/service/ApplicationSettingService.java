package com.vi.migrationtool.mongodb.service;

import java.util.List;

import com.vi.migrationtool.mongodb.model.ApplicationSettingsEntity;
import com.vi.migrationtool.mongodb.repository.ApplicationSettingsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class ApplicationSettingService {

  public static final String DID_NOT_FIND_ANY_APPLICATION_SETTING =
      "Did not find any application setting";
  public static final String FOUND_MORE_THAN_ONE_APPLICATION_SETTING =
      "Found more than one application setting";
  private final ApplicationSettingsRepository applicationSettingsRepository;

  public ApplicationSettingsEntity getApplicationSetting() {

    List<ApplicationSettingsEntity> settings = applicationSettingsRepository.findAll();
    assertOnlyOneApplicationSettingExist(settings);
    return settings.get(0);
  }

  public void updateApplicationSettings(ApplicationSettingsEntity applicationSettingsEntity) {
    // delete first to remove duplicates
    applicationSettingsRepository.deleteAll();
    applicationSettingsRepository.save(applicationSettingsEntity);
  }

  private void assertOnlyOneApplicationSettingExist(
      List<ApplicationSettingsEntity> settingsEntities) {
    if (settingsEntities.isEmpty()) {
      log.error(DID_NOT_FIND_ANY_APPLICATION_SETTING);
      throw new IllegalStateException(DID_NOT_FIND_ANY_APPLICATION_SETTING);
    }
    if (settingsEntities.size() > 1) {
      log.error(FOUND_MORE_THAN_ONE_APPLICATION_SETTING);
      throw new IllegalStateException(FOUND_MORE_THAN_ONE_APPLICATION_SETTING);
    }
  }
}
