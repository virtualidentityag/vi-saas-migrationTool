package com.vi.migrationtool.common;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;

public abstract class MigrationTasks implements CustomTaskChange {

  @Override
  public String getConfirmationMessage() {
    return null;
  }

  @Override
  public void setUp() {}

  @Override
  public void setFileOpener(ResourceAccessor resourceAccessor) {}

  @Override
  public ValidationErrors validate(Database database) {
    return null;
  }
}
