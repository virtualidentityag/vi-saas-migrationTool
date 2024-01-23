package com.vi.migrationtool.userservice;

import com.vi.migrationtool.rocketchat.RocketChatMongoDbService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

@RequiredArgsConstructor
@Slf4j
public class UpdateRcUserIdForInconsistentUsersService {

  private final @NonNull JdbcTemplate jdbcTemplate;

  private final @NonNull RocketChatMongoDbService rocketChatMongoDbService;

  private final @NonNull int pageSize;

  public void tryFixUsers() {
    log.info("Starting to fix users");
    new UpdateRcUserIdForInconsistentUsersProcessor(jdbcTemplate, rocketChatMongoDbService)
        .processUsersWithPagination(pageSize);

    log.info("Starting to fix consultants");
    new UpdateRcUserIdForInconsistentConsultantsProcessor(jdbcTemplate, rocketChatMongoDbService)
        .processConsultantsWithPagination(pageSize);
  }
}
