package com.vi.migrationtool.keycloak;

import com.vi.migrationtool.common.MigrationTasks;
import com.vi.migrationtool.config.BeanAwareSpringLiquibase;
import liquibase.database.Database;
import lombok.Data;
import org.springframework.util.Assert;

@Data
public class KeycloakCreateTechnicalUserTask extends MigrationTasks {

  private TechnicalUsers user;

  @Override
  public void execute(Database database) {
    Assert.notNull(user, "User must be set");

    KeycloakUserService keycloakUserService =
        BeanAwareSpringLiquibase.getBean(KeycloakUserService.class);
    TechnicalUsersConfig technicalUsersConfig =
        BeanAwareSpringLiquibase.getBean(TechnicalUsersConfig.class);
    keycloakUserService.createUser(
        user.getUsername(),
        technicalUsersConfig.getCredentialsByUsername(user),
        technicalUsersConfig.getEmailByUsername(user),
        technicalUsersConfig.getTenantIdByUsername(user));
  }
}
