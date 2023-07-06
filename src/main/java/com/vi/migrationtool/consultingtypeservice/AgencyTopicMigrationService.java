package com.vi.migrationtool.consultingtypeservice;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@AllArgsConstructor
@Slf4j
public class AgencyTopicMigrationService {

  private JdbcTemplate agencyServiceJdbcTemplate;

  public void addAgencyTopicRelationIfNeeded(Integer consultingTypeId) {
    List<Integer> agencies = findAllAgenciesForConsultingType(consultingTypeId);
    agencies.forEach(agencyId -> {
      var topicId = consultingTypeId; // we migrated topics with same id as consulting type id
      if (agencyTopicsRelationExists(agencyId, topicId)) {
        return;
      }
      String sql = "INSERT INTO agency_topic (id, agency_id, topic_id) VALUES (NEXT VALUE FOR sequence_agency_topic, ?, ?)";
      agencyServiceJdbcTemplate.update(sql, agencyId, topicId);
      log.info("Created agency topic relation for agencyId: {} and topicId: {}", agencyId, topicId);
    });
  }

  private boolean agencyTopicsRelationExists(Integer agencyId, Integer topicId) {
    String sql = "SELECT COUNT(*) FROM agency_topic WHERE agency_id = ? AND topic_id = ?";
    try {
      Integer count = agencyServiceJdbcTemplate.queryForObject(sql, Integer.class, agencyId,
          topicId);
      return count != null && count > 0;
    } catch (EmptyResultDataAccessException e) {
      return false;
    }
  }

  private List<Integer> findAllAgenciesForConsultingType(Integer consultingTypeId) {
    return agencyServiceJdbcTemplate.query(
        "SELECT id from agency WHERE consulting_type = ?",
        new Object[]{consultingTypeId},
        (RowMapper<Integer>) (resultSet, rowNum) -> resultSet.getInt("id"));
  }
}
