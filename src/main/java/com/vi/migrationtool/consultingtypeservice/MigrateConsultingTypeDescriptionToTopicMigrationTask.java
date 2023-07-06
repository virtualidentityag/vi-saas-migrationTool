package com.vi.migrationtool.consultingtypeservice;

import com.vi.migrationtool.common.MigrationTasks;
import com.vi.migrationtool.config.BeanAwareSpringLiquibase;
import com.vi.migrationtool.mongodb.model.ConsultingTypeEntity;
import com.vi.migrationtool.mongodb.service.ConsultingTypeService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import liquibase.database.Database;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
public class MigrateConsultingTypeDescriptionToTopicMigrationTask extends MigrationTasks {

  private final JdbcTemplate jdbcTemplate;

  public MigrateConsultingTypeDescriptionToTopicMigrationTask() {
    this.jdbcTemplate = BeanAwareSpringLiquibase.getNamedBean(
        "consultingTypeServiceJdbcTemplate", JdbcTemplate.class);
  }
  @Override
  public void execute(Database database) {
    log.info("Migrating consultingtype::description to topic::description");
    val consultingTypeService = BeanAwareSpringLiquibase.getBean(ConsultingTypeService.class);

    val consultingTypes = consultingTypeService.getConsultingTypes();

    val consultingTypeServiceJdbcTemplate =
        BeanAwareSpringLiquibase.getNamedBean(
            "consultingTypeServiceJdbcTemplate", JdbcTemplate.class);
    val currentDateTime = LocalDateTime.now();


    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    // Format the current date and time
    String formattedCurrentDateTime = currentDateTime.format(formatter);
    consultingTypeServiceJdbcTemplate.batchUpdate(
        "insert into topic (id, tenant_id, name, description, status, create_date, update_date, internal_identifier, fallback_agency_id, fallback_url, send_next_step_message, titles_short, titles_long, titles_welcome, titles_dropdown, slug) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",

        consultingTypes.stream()
            .filter(ct -> !topicExistsById(consultingTypeServiceJdbcTemplate, ct.getId()))
            .peek(
                ct ->
                    log.info(
                        "Migrating consulting type with id: {} into a topic with id: {}",
                        ct.getId(),
                        ct.getId()))
            .map(
                ct ->
                    new Object[] {
                      ct.getId(),
                      ct.getTenantId(),
                      ct.getTitles().getShort(),
                      ct.getDescription(),
                      "ACTIVE",
                        formattedCurrentDateTime,
                        formattedCurrentDateTime,
                      ct.getTitles().getShort(),
                      null,
                      ct.getUrls() != null ? ct.getUrls().getRegistrationPostcodeFallbackUrl() : "",
                      ct.getSendFurtherStepsMessage(),
                      ct.getTitles().getShort(),
                      ct.getTitles().getLong(),
                      ct.getTitles().getWelcome(),
                      ct.getTitles().getRegistrationDropdown(),
                        ct.getSlug()
                    })
            .collect(Collectors.toList()));

    log.info("Finished migrating topics");

    consultingTypes.forEach(this::addTopicGroupIfNeeded);


    log.info("Finished migrating consultingtype::description to topic::description");
  }

  public boolean topicExistsById(JdbcTemplate jdbcTemplate, int id) {
    String sql = "SELECT COUNT(*) FROM topic WHERE id = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
    return count != null && count > 0;
  }

  private void addTopicGroupIfNeeded(ConsultingTypeEntity consultingTypeEntity) {
    var topicGroups = consultingTypeEntity.getGroups();
    TopicGroupMigrationService topicGroupMigrationService = new TopicGroupMigrationService(
        this.jdbcTemplate);

    topicGroups.forEach(
        topicGroup -> topicGroupMigrationService.insertTopicGroupIfNotExists(topicGroup).ifPresent(topicGroupId -> topicGroupMigrationService.createTopicGroupRelationIfNotExists(topicGroupId, consultingTypeEntity.getId())));
  }




}
