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
public class UpdateRcUserIdForInconsistentUsersProcessor {

  private final @NonNull JdbcTemplate jdbcTemplate;

  private final @NonNull RocketChatMongoDbService rocketChatMongoDbService;

  public void processUsersWithPagination(int pageSize) {
    int page = 0;

    while (true) {
      List<Map<String, Object>> result = fetchUsersWithPagination(pageSize, page);

      if (result.isEmpty()) {
        break;
      }

      List<MariaDbUser> users = result.stream().map(this::mapUserRow).collect(Collectors.toList());
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
    userIdToNewRocketChatId.forEach(this::tryUpdateUser);
  }

  private List<Map<String, Object>> fetchUsersWithPagination(int pageSize, int page) {
    String sqlQuery =
        "SELECT user_id, rc_user_id, email FROM user WHERE delete_date IS NULL ORDER BY user_id LIMIT ? OFFSET ?";
    return jdbcTemplate.queryForList(sqlQuery, pageSize, page * pageSize);
  }

  private void tryUpdateUser(MariaDbUser user, Optional<String> newRocketChatId) {
    if (newRocketChatId.isPresent()) {
      log.info(
          "Updating rc_user_id for user {} with new value {}",
          user.getUserId(),
          newRocketChatId.get());
      jdbcTemplate.update(
          "UPDATE user SET rc_user_id = ? WHERE user_id = ?",
          newRocketChatId.get(),
          user.getUserId());
    } else {
      log.info(
          "Could not find a valid rc_user_id for user, user will not be fixed {}",
          user.getUserId());
    }
  }

  private MariaDbUser mapUserRow(Map<String, Object> row) {
    return new MariaDbUser(
        (String) row.get("user_id"), (String) row.get("rc_user_id"), (String) row.get("email"));
  }
}
