package com.vi.migrationtool.consultingtypeservice;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

@AllArgsConstructor
@Slf4j
public class TopicGroupMigrationService {

  public static final String INSERT_QUERY =
      "INSERT INTO topic_group (id, name) VALUES (NEXT VALUE FOR sequence_topic_group, ?)";
  private JdbcTemplate consultingTypeJdbcTemplate;

  public Optional<Integer> insertTopicGroupIfNotExists(String groupName) {
    if (groupName == null) {
      log.info("Could not migrate topic with group null");
      return Optional.empty();
    }
    if (!topicGroupExists(groupName)) {
      consultingTypeJdbcTemplate.update(
          con -> {
            PreparedStatement ps =
                con.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, groupName);
            return ps;
          });
      String idQuery = "SELECT MAX(id) from topic_group";

      log.info("Topic group inserted: " + groupName);
      return Optional.of(consultingTypeJdbcTemplate.queryForObject(idQuery, Integer.class));
    } else {
      return Optional.empty();
    }
  }

  private boolean topicGroupExists(String groupName) {
    String sql = "SELECT COUNT(*) FROM topic_group WHERE name = ?";
    try {
      Integer count = consultingTypeJdbcTemplate.queryForObject(sql, Integer.class, groupName);
      return count != null && count > 0;
    } catch (EmptyResultDataAccessException e) {
      return false;
    }
  }

  public void createTopicGroupRelationIfNotExists(Integer topicGroupId, Integer topicId) {
    if (!topicGroupRelationExists(topicGroupId, topicId)) {
      insertTopicGroupRelation(topicGroupId, topicId);
    }
  }

  private void insertTopicGroupRelation(Integer topicGroupId, Integer topicId) {
    consultingTypeJdbcTemplate.update(
        "insert into topic_group_x_topic (group_id, topic_id) values (?,?)", topicGroupId, topicId);
  }

  private boolean topicGroupRelationExists(Integer topicGroupId, Integer topicId) {
    String sql = "SELECT COUNT(*) FROM topic_group_x_topic WHERE group_id = ? AND topic_id = ?";
    try {
      Integer count =
          consultingTypeJdbcTemplate.queryForObject(sql, Integer.class, topicGroupId, topicId);
      return count != null && count > 0;
    } catch (EmptyResultDataAccessException e) {
      return false;
    }
  }
}
