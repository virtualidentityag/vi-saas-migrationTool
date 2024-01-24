package com.vi.migrationtool.weblate;

import com.vi.migrationtool.common.MigrationTasks;
import com.vi.migrationtool.config.BeanAwareSpringLiquibase;
import liquibase.database.Database;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class WeblateCreateComponentTask extends MigrationTasks {

  String componentName;
  String projectName;

  @Override
  public void execute(Database database) {
    WeblateComponentService weblateProjectService =
        BeanAwareSpringLiquibase.getBean(WeblateComponentService.class);
    assert weblateProjectService != null;
    weblateProjectService.createComponentForProject(projectName, componentName);
  }
}
