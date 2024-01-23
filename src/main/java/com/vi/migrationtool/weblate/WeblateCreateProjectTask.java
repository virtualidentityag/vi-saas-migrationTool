package com.vi.migrationtool.weblate;

import com.vi.migrationtool.common.MigrationTasks;
import com.vi.migrationtool.config.BeanAwareSpringLiquibase;
import com.vi.migrationtool.weblate.config.ProjectDTO;
import com.vi.migrationtool.weblate.config.WeblateConfig;
import liquibase.database.Database;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class WeblateCreateProjectTask extends MigrationTasks {

  String projectName;

  @Override
  public void execute(Database database) {
    WeblateProjectService weblateProjectService =
        BeanAwareSpringLiquibase.getBean(WeblateProjectService.class);
    WeblateConfig weblateConfig = BeanAwareSpringLiquibase.getBean(WeblateConfig.class);
    assert weblateProjectService != null;
    weblateProjectService.createProject(fromWeblateConfig(weblateConfig));
  }

  private ProjectDTO fromWeblateConfig(WeblateConfig weblateConfig) {
    return new ProjectDTO(projectName, weblateConfig.getWeb(), weblateConfig.getSlug());
  }
}
