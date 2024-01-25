package com.vi.migrationtool.keycloak;

import com.vi.migrationtool.common.MigrationTasks;
import com.vi.migrationtool.config.BeanAwareSpringLiquibase;
import java.util.List;
import liquibase.database.Database;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

@Data
@Slf4j
public class AddTenantIdAttributeToUsersWithRoleTask extends MigrationTasks {

  private static final String SPLIT_CHAR = ",";

  private Long tenantId;

  private String roleNames;
  private JdbcTemplate userServiceJdbcTemplate;

  @Override
  public void execute(Database database) {
    if (roleNames != null) {
      KeycloakService keycloakService = BeanAwareSpringLiquibase.getBean(KeycloakService.class);
      this.userServiceJdbcTemplate =
          BeanAwareSpringLiquibase.getNamedBean("userServiceJdbcTemplate", JdbcTemplate.class);

      List<UsersWithRole> updatedUsersPerRole =
          keycloakService.addCustomAttributeToUsersWithRole(
              "tenantId", tenantId, List.of(roleNames.split(SPLIT_CHAR)));

      updateUserTables(updatedUsersPerRole);
    } else {
      log.warn("No role names provided for task: {}", this.getClass().getSimpleName());
    }
  }

  private void updateUserTables(List<UsersWithRole> updatedUsersPerRole) {

    updatedUsersPerRole.forEach(
        usersWithRole -> {
          log.info("Updating tenantId for users with role: {}", usersWithRole.getRole());
          if (UserRole.CONSULTANT.getValue().equals(usersWithRole.getRole())) {
            updateConsultantsWithTenantIdIfNotSet(usersWithRole);
          }
          if (UserRole.USER.getValue().equals(usersWithRole.getRole())) {
            updateUsersWithTenantIdIfNotSet(usersWithRole);
          }
        });
  }

  private void updateUsersWithTenantIdIfNotSet(UsersWithRole usersWithRole) {
    usersWithRole
        .getUserIds()
        .forEach(
            userId -> {
              log.info("Set tenantId for {} with id {} to {}", "user", userId, tenantId);
              userServiceJdbcTemplate.update(
                  "UPDATE user SET tenant_id = ? WHERE user_id = ? and tenant_id is null",
                  tenantId,
                  userId);
            });
  }

  private void updateConsultantsWithTenantIdIfNotSet(UsersWithRole usersWithRole) {
    usersWithRole
        .getUserIds()
        .forEach(
            userId -> {
              log.info("Set tenantId for {} with id {} to {}", "consultant", userId, tenantId);
              userServiceJdbcTemplate.update(
                  "UPDATE consultant SET tenant_id = ? WHERE consultant_id = ? and tenant_id is null",
                  tenantId,
                  userId);
            });
  }
}
