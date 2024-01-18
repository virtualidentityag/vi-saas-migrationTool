package com.vi.migrationtool.userservice;

import com.vi.migrationtool.common.MigrationTasks;
import com.vi.migrationtool.config.BeanAwareSpringLiquibase;
import com.vi.migrationtool.rocketchat.RocketChatMongoDbService;
import liquibase.database.Database;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

@Data
@Slf4j
public class UserServiceUpdateRcUserIdForInconsistentUsersTask extends MigrationTasks {

  int pageSize;

  @Override
  public void execute(Database database) {
    JdbcTemplate jdbcTemplate =
        BeanAwareSpringLiquibase.getNamedBean("userServiceJdbcTemplate", JdbcTemplate.class);
    RocketChatMongoDbService rocketChatMongoDbService =
        BeanAwareSpringLiquibase.getBean(RocketChatMongoDbService.class);

    new UpdateRcUserIdForInconsistentUsersService(jdbcTemplate, rocketChatMongoDbService, pageSize)
        .tryFixUsers();
  }

  public void setPageSize(String pageSize) {
    this.pageSize = Integer.valueOf(pageSize);
  }
}
