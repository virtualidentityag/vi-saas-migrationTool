package com.vi.migrationtool.userservice;

import com.vi.migrationtool.common.MigrationTasks;
import com.vi.migrationtool.config.BeanAwareSpringLiquibase;
import liquibase.database.Database;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

@Data
@Slf4j
public class UserServiceAddWalkThroughColumnToUsersTable extends MigrationTasks {

  @Override
  public void execute(Database database) {
    JdbcTemplate jdbcTemplate =
        BeanAwareSpringLiquibase.getNamedBean("userServiceJdbcTemplate", JdbcTemplate.class);

    createWalkThroughColumn(jdbcTemplate);
  }

  private void createWalkThroughColumn(JdbcTemplate jdbcTemplate) {
    try {
      log.info("Adding walk through column to users table");

      // Add the column with default value 1
      String addColumnSql = "ALTER TABLE users ADD COLUMN walkthrough TINYINT(4) DEFAULT 1";
      jdbcTemplate.execute(addColumnSql);
      log.info("Successfully added walk through column");

      // Update existing rows to set the value to 0
      String updateExistingRowsSql = "UPDATE users SET walkthrough = 0";
      int updatedRows = jdbcTemplate.update(updateExistingRowsSql);
      log.info("Successfully updated {} existing rows to set walk through = 0", updatedRows);

    } catch (Exception e) {
      log.error("Error adding walk through column to users table", e);
      throw new RuntimeException("Failed to add walk through column", e);
    }
  }
}
