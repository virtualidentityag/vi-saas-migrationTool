package com.vi.migrationtool.common;

import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import lombok.Data;

@Data
public class ManualConfigurationTask extends MigrationTasks {

  private String message;

  private String logLevel;

  @Override
  public void execute(Database database) throws CustomChangeException {}
}
