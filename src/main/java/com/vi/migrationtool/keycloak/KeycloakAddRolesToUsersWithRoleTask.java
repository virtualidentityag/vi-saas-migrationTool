package com.vi.migrationtool.keycloak;

import com.vi.migrationtool.common.MigrationTasks;
import com.vi.migrationtool.config.BeanAwareSpringLiquibase;
import java.util.List;
import liquibase.database.Database;
import lombok.Data;

@Data
public class KeycloakAddRolesToUsersWithRoleTask extends MigrationTasks {
  private static final String SPLIT_CHAR = ",";
  private String roleNameToSearchForUsers;
  private String roleNames;

  @Override
  public void execute(Database database) {
    if (roleNames != null) {
      KeycloakService keycloakService = BeanAwareSpringLiquibase.getBean(KeycloakService.class);
      keycloakService.addRolesToUsersWithRoleName(
          roleNameToSearchForUsers, List.of(roleNames.split(SPLIT_CHAR)));
    }
  }
}
