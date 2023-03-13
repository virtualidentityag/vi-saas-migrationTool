package com.vi.migrationtool.consultingtypeservice;

import com.vi.migrationtool.common.MigrationTasks;
import com.vi.migrationtool.config.BeanAwareSpringLiquibase;
import com.vi.migrationtool.keycloak.KeycloakService;
import com.vi.migrationtool.keycloak.KeycloakUser;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class DropLockedAgenciesAndSaveDataAtrribute extends MigrationTasks {

    private String roleName;
    private void executeRemovalQuery(
             JdbcTemplate jdbcTemplate) {
        jdbcTemplate.update(
                "ALTER TABLE consutlingtypeservice DROP `lockedAgencies` DROP `sendSaveSessionDataMessage`");
    }

    @Override
    public void execute(Database database) throws CustomChangeException {
        //KeycloakService keycloakService = BeanAwareSpringLiquibase.getBean(KeycloakService.class);
        //List<KeycloakUser> adminUsersWithRoleName = keycloakService.getUsersWithRoleName(roleName);
        //log.info("Found users with role name of size: {}", adminUsersWithRoleName.size());
        JdbcTemplate jdbcTemplate = BeanAwareSpringLiquibase.getBean(JdbcTemplate.class);
        executeRemovalQuery(jdbcTemplate);
    }
}
