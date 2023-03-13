package com.vi.migrationtool.mongodb.service;

import com.vi.migrationtool.mongodb.model.ConsultingTypeEntity;
import com.vi.migrationtool.mongodb.repository.ConsultingTypesRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class ConsultingTypeService {

  public static final String DID_NOT_FIND_ANY_APPLICATION_SETTING =
      "Did not find any application setting";
  public static final String FOUND_MORE_THAN_ONE_APPLICATION_SETTING =
      "Found more than one application setting";
  private final ConsultingTypesRepository consultingTypesRepository;

  public List<ConsultingTypeEntity> getConsultingTypes() {
    return consultingTypesRepository.findAll();
  }

  public void updateConsultingType(ConsultingTypeEntity consultingTypeEntity) {
    log.info("Updating consulting type with id " + consultingTypeEntity.getId());
    consultingTypesRepository.save(consultingTypeEntity);
  }
}
