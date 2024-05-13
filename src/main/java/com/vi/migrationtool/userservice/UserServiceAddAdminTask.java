package com.vi.migrationtool.userservice;

import com.vi.migrationtool.common.MigrationTasks;
import com.vi.migrationtool.config.BeanAwareSpringLiquibase;
import com.vi.migrationtool.keycloak.KeycloakService;
import com.vi.migrationtool.keycloak.KeycloakUser;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import liquibase.database.Database;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@Data
@Slf4j
public class UserServiceAddAdminTask extends MigrationTasks {

  private String roleName;

  private String adminType;

  @Override
  public void execute(Database database) {

    KeycloakService keycloakService = BeanAwareSpringLiquibase.getBean(KeycloakService.class);
    List<KeycloakUser> adminUsersWithRoleName = keycloakService.getUsersWithRoleName(roleName, 1);
    log.info("Found users with role name of size: {}", adminUsersWithRoleName.size());
    JdbcTemplate jdbcTemplate = BeanAwareSpringLiquibase.getBean(JdbcTemplate.class);
    createTenantAdminUsersIfNotExist(adminUsersWithRoleName, jdbcTemplate);
  }

  private void createTenantAdminUsersIfNotExist(
      List<KeycloakUser> adminUsersWithRoleName, JdbcTemplate jdbcTemplate) {
    adminUsersWithRoleName.stream()
        .filter(keycloakUser -> doesNotExistInDB(jdbcTemplate, keycloakUser.getId()))
        .forEach(this::createAdminUserInDB);
  }

  private boolean doesNotExistInDB(JdbcTemplate jdbcTemplate, String id) {
    String sql = "SELECT * FROM admin WHERE admin_id = ?";

    var result =
        jdbcTemplate.query(
            sql,
            new Object[] {id},
            (RowMapper<Object>) (resultSet, i) -> resultSet.getString("admin_id"));

    return result == null || result.isEmpty();
  }

  private void createAdminUserInDB(KeycloakUser keycloakUser) {
    log.info("Creating admin user in DB: {}", keycloakUser.getId());
    JdbcTemplate jdbcTemplate = BeanAwareSpringLiquibase.getBean(JdbcTemplate.class);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    var formattedDate = LocalDateTime.now().format(formatter);
    executeInsertQuery(keycloakUser, jdbcTemplate, formattedDate);
    log.info("Adming user created in DB: {}", keycloakUser.getId());
  }

  private void executeInsertQuery(
      KeycloakUser keycloakUser, JdbcTemplate jdbcTemplate, String formattedDate) {
    jdbcTemplate.update(
        "INSERT INTO `admin` (`admin_id`, `tenant_id`, `username`, `first_name`, `last_name`, `email`, `type`, `rc_user_id`, `id_old`, `create_date`, `update_date`) VALUES\n"
            + "(?,?,?,?,?,?,?,NULL,NULL,?,?)",
        keycloakUser.getId(),
        keycloakUser.getAttributes() != null ? extractTenantId(keycloakUser) : null,
        keycloakUser.getUsername(),
        keycloakUser.getFirstName(),
        keycloakUser.getLastName(),
        keycloakUser.getEmail() == null ? "" : keycloakUser.getEmail(),
        adminType,
        formattedDate,
        formattedDate);
  }

  private Long extractTenantId(KeycloakUser keycloakUser) {
    log.info("Trying to extract tenant id for a given keycloak user");
    Map<String, Object> attributes = (Map<String, Object>) keycloakUser.getAttributes();
    String tenantId = (String) ((ArrayList) attributes.get("tenantId")).get(0);
    return Long.valueOf(tenantId);
  }
}
