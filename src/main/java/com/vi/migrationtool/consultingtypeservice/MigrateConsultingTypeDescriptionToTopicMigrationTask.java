package com.vi.migrationtool.consultingtypeservice;

import com.vi.migrationtool.common.MigrationTasks;
import com.vi.migrationtool.config.BeanAwareSpringLiquibase;
import com.vi.migrationtool.mongodb.service.ConsultingTypeService;
import java.util.stream.Collectors;
import liquibase.database.Database;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
public class MigrateConsultingTypeDescriptionToTopicMigrationTask extends MigrationTasks {

  @Override
  public void execute(Database database) {
    log.info("Migrating consultingtype::description to topic::description");
    val consultingTypeService = BeanAwareSpringLiquibase.getBean(ConsultingTypeService.class);

    val consultingTypes = consultingTypeService.getConsultingTypes();

    val consultingTypeServiceJdbcTemplate =
        BeanAwareSpringLiquibase.getNamedBean(
            "consultingTypeServiceJdbcTemplate", JdbcTemplate.class);
    consultingTypeServiceJdbcTemplate.batchUpdate(
        "insert into topic (id, tenant_id, name, description, status, create_date, update_date, internal_identifier, fallback_agency_id, fallback_url, send_next_step_message, titles_short, titles_long, titles_welcome, titles_dropdown) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
        consultingTypes.stream()
            .map(
                ct ->
                    new Object[] {
                      ct.getId(),
                      ct.getTenantId(),
                      ct.getSlug(), /* todo */
                      ct.getDescription(),
                      "TODO: STATUS",
                      "TODO: create_date",
                      "TODO: update_date",
                      "TODO: internal_identifier",
                      "TODO: fallback_agency_id",
                      "TODO: fallback_url",
                      ct.getSendFurtherStepsMessage(),
                      ct.getTitles().getShort(),
                      ct.getTitles().getLong(),
                      ct.getTitles().getWelcome(),
                      ct.getTitles().getRegistrationDropdown()
                    })
            .collect(Collectors.toList()));
    log.info("Finished migrating consultingtype::description to topic::description");
  }
}
