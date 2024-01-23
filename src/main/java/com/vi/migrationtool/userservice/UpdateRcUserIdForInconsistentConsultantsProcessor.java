package com.vi.migrationtool.userservice;

import com.vi.migrationtool.rocketchat.RocketChatMongoDbService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

@RequiredArgsConstructor
@Slf4j
public class UpdateRcUserIdForInconsistentConsultantsProcessor {

  private final @NonNull JdbcTemplate jdbcTemplate;

  private final @NonNull RocketChatMongoDbService rocketChatMongoDbService;

  public void processConsultantsWithPagination(int pageSize) {
    int page = 0;

    while (true) {
      List<Map<String, Object>> result = fetchConsultantsWithPagination(pageSize, page);

      if (result.isEmpty()) {
        break;
      }

      List<MariaDbUser> users =
          result.stream().map(this::mapConsultantRow).collect(Collectors.toList());
      applyConsistencyFix(users, page, pageSize);
      page++;
    }
  }

  private void applyConsistencyFix(List<MariaDbUser> users, int page, int pageSize) {
    List<MariaDbUser> inconsistentUsers = rocketChatMongoDbService.findInconsistentUsers(users);
    Map<MariaDbUser, Optional<String>> userIdToNewRocketChatId =
        rocketChatMongoDbService.retrieveValidRocketchatIdByUserEmail(inconsistentUsers);
    log.info(
        "Found {} inconsistent users in page {}, pageSize {}",
        inconsistentUsers.size(),
        page,
        pageSize);
    userIdToNewRocketChatId.forEach(this::tryUpdateConsultant);
  }

  private void tryUpdateConsultant(MariaDbUser user, Optional<String> newRocketChatId) {
    if (newRocketChatId.isPresent()) {
      log.info(
          "Updating rc_user_id for user {} with new value {}",
          user.getUserId(),
          newRocketChatId.get());
      jdbcTemplate.update(
          "UPDATE consultant SET rc_user_id = ? WHERE consultant_id = ?",
          newRocketChatId.get(),
          user.getUserId());
    } else {
      log.info(
          "Could not find a valid rc_user_id for user, user will not be fixed {}",
          user.getUserId());
    }
  }

  private List<Map<String, Object>> fetchConsultantsWithPagination(int pageSize, int page) {
    String sqlQuery =
        "SELECT consultant_id, rc_user_id, email FROM consultant WHERE delete_date IS NULL ORDER BY consultant_id LIMIT ? OFFSET ?";
    return jdbcTemplate.queryForList(sqlQuery, pageSize, page * pageSize);
  }

  private MariaDbUser mapConsultantRow(Map<String, Object> row) {
    return new MariaDbUser(
        (String) row.get("consultant_id"),
        (String) row.get("rc_user_id"),
        (String) row.get("email"));
  }
}
