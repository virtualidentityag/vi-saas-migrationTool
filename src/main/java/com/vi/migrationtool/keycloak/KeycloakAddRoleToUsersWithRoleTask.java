package com.vi.migrationtool.keycloak;

import com.vi.migrationtool.common.MigrationTasks;
import com.vi.migrationtool.config.BeanAwareSpringLiquibase;
import liquibase.database.Database;
import lombok.Data;

@Data
public class KeycloakAddRoleToUsersWithRoleTask extends MigrationTasks {
  private String roleNameToSearchForUsers;
  private String roleName;

  @Override
  public void execute(Database database) {
    KeycloakService keycloakService = BeanAwareSpringLiquibase.getBean(KeycloakService.class);
    keycloakService.addRoleToUsersWithRoleName(roleNameToSearchForUsers, roleName);
  }
}
