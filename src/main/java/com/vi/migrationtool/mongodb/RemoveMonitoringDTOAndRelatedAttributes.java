package com.vi.migrationtool.mongodb;

import com.vi.migrationtool.common.MigrationTasks;
import com.vi.migrationtool.config.BeanAwareSpringLiquibase;
import com.vi.migrationtool.mongodb.model.ConsultingTypeEntity;
import com.vi.migrationtool.mongodb.service.ConsultingTypeService;
import java.util.List;
import liquibase.database.Database;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class RemoveMonitoringDTOAndRelatedAttributes extends MigrationTasks {

    @Override
    public void execute(Database database) {
        ConsultingTypeService consultingTypeService =
                BeanAwareSpringLiquibase.getBean(ConsultingTypeService.class);
        migrate(consultingTypeService);
    }

    public void migrate(ConsultingTypeService consultingTypeService) {
        List<ConsultingTypeEntity> consultingTypeEntities = consultingTypeService.getConsultingTypes();
        applyMigration(consultingTypeService, consultingTypeEntities);
    }

    private void applyMigration(
            ConsultingTypeService consultingTypeService,
            List<ConsultingTypeEntity> consultingTypeEntities) {
        if (consultingTypeEntities != null) {
            consultingTypeEntities.stream()
                    .forEach(
                            (consultingTypeEntity) ->
                                    applyMigration(consultingTypeEntity, consultingTypeService));
        } else {
            log.info("Skipping migration as consulting type entities are nulls");
        }
    }

    private void applyMigration(
            ConsultingTypeEntity consultingTypeEntity, ConsultingTypeService consultingTypeService) {
        consultingTypeService.updateConsultingType(consultingTypeEntity);
    }
}
