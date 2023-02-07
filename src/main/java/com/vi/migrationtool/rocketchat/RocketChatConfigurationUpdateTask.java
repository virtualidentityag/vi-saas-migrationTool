package com.vi.migrationtool.rocketchat;

import com.vi.migrationtool.common.MigrationTasks;
import liquibase.database.Database;
import lombok.Data;

@Data
public class RocketChatConfigurationUpdateTask extends MigrationTasks {

  private String rocketChatMethod;
  private String rocketChatRequest;

  @Override
  public void execute(Database database) {
    /*  RocketChatService rocketChatService = BeanAwareSpringLiquibase.getBean(RocketChatService.class);
    rocketChatService.executeRocketChatRequest(rocketChatMethod, rocketChatRequest);*/
  }
}
