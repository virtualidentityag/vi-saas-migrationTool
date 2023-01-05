package com.vi.migrationtool.keycloak;

import com.vi.migrationtool.common.MigrationTasks;
import com.vi.migrationtool.config.BeanAwareSpringLiquibase;
import java.util.List;
import liquibase.database.Database;
import lombok.Data;

@Data
public class KeycloakAddRoleToUserTask extends MigrationTasks {

  private static final String SPLIT_CHAR = ",";
  private String usernames;
  private String roleName;

  @Override
  public void execute(Database database) {
    KeycloakService keycloakService = BeanAwareSpringLiquibase.getBean(KeycloakService.class);
    keycloakService.addRoleToUsers(List.of(usernames.split(SPLIT_CHAR)), roleName);
  }
}
