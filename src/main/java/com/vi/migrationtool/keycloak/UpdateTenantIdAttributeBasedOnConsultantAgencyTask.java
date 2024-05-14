package com.vi.migrationtool.keycloak;

import com.vi.migrationtool.common.MigrationTasks;
import com.vi.migrationtool.config.BeanAwareSpringLiquibase;
import java.util.List;
import liquibase.database.Database;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;

@Data
@Slf4j
public class UpdateTenantIdAttributeBasedOnConsultantAgencyTask extends MigrationTasks {

  private static final String SPLIT_CHAR = ",";
  private static final int USER_PAGE_SIZE = 300;

  private Long tenantId;

  private String roleNames;
  private JdbcTemplate userServiceJdbcTemplate;

  private KeycloakLoginService keycloakLoginService;

  @Override
  public void execute(Database database) {
    KeycloakUserService keycloakUserService =
        BeanAwareSpringLiquibase.getBean(KeycloakUserService.class);
    this.userServiceJdbcTemplate =
        BeanAwareSpringLiquibase.getNamedBean("userServiceJdbcTemplate", JdbcTemplate.class);

    this.keycloakLoginService =
        BeanAwareSpringLiquibase.getNamedBean("keycloakLoginService", KeycloakLoginService.class);

    List<UserTenant> consultantTargetTenants =
        userServiceJdbcTemplate.query(
            "select distinct ca.consultant_id, a.tenant_id as 'target_tenant'\n"
                + "from consultant_agency ca \n"
                + "inner join agencyservice.agency a on ca.agency_id = a.id\n"
                + "where (ca.tenant_id <> a.tenant_id or ca.tenant_id is null)\n",
            (rs, rowNum) -> {
              String consultantId = rs.getString("consultant_id");
              Long targetTenant = rs.getLong("target_tenant");

              return new UserTenant(consultantId, targetTenant);
            });

    updateConsultantsTenant(keycloakUserService, consultantTargetTenants);

    List<UserTenant> adviceSeekerTargetTenants =
        userServiceJdbcTemplate.query(
            "select distinct u.user_id, a.tenant_id as 'target_tenant'\n"
                + "from session s \n"
                + "inner join user u on s.user_id = u.user_id\n"
                + "inner join agencyservice.agency a on s.agency_id = a.id\n"
                + "where (u.tenant_id <> a.tenant_id or s.tenant_id is null)\n",
            (rs, rowNum) -> {
              String userId = rs.getString("user_id");
              Long targetTenant = rs.getLong("target_tenant");

              return new UserTenant(userId, targetTenant);
            });

    updateAdviceSeekersTenant(keycloakUserService, adviceSeekerTargetTenants);
  }

  private void updateAdviceSeekersTenant(
      KeycloakUserService keycloakUserService, List<UserTenant> adviceSeekerTargetTenants) {
    var httpHeaders = authenticateInKeycloak();
    for (int i = 0; i < adviceSeekerTargetTenants.size(); i++) {
      var adviceSeekerTenant = adviceSeekerTargetTenants.get(i);
      if (i % USER_PAGE_SIZE == 0) {
        httpHeaders = authenticateInKeycloak();
      }
      log.info(
          "Attempt to set tenantId for adviceseeker with id  {} to {}",
          adviceSeekerTenant.getUserId(),
          adviceSeekerTenant.getTenantId());

      keycloakUserService.updateUserCustomAttributeWithoutLogin(
          "tenantId",
          adviceSeekerTenant.getTenantId(),
          adviceSeekerTenant.getUserId(),
          httpHeaders);

      userServiceJdbcTemplate.update(
          "UPDATE user SET tenant_id = ? WHERE user_id = ?",
          adviceSeekerTenant.getTenantId(),
          adviceSeekerTenant.getUserId());

      userServiceJdbcTemplate.update(
          "UPDATE session SET tenant_id = ? WHERE user_id = ?",
          adviceSeekerTenant.getTenantId(),
          adviceSeekerTenant.getUserId());

      log.info(
          "Successfully set tenantId for {} with id {} to {}",
          "advice seeker, user and keycloak user",
          adviceSeekerTenant.getUserId(),
          adviceSeekerTenant.getTenantId());
    }
  }

  private HttpHeaders authenticateInKeycloak() {
    var httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    KeycloakLoginResponseDTO loginResponse = keycloakLoginService.loginAdminUser();
    httpHeaders.setBearerAuth(loginResponse.getAccessToken());
    return httpHeaders;
  }

  private void updateConsultantsTenant(
      KeycloakUserService keycloakUserService, List<UserTenant> consultantTargetTenants) {

    if (consultantTargetTenants.isEmpty()) {
      log.info("No consultant found with different tenantId");
      return;
    }
    var httpHeaders = authenticateInKeycloak();
    for (int i = 0; i < consultantTargetTenants.size(); i++) {
      if (i % USER_PAGE_SIZE == 0) {
        httpHeaders = authenticateInKeycloak();
      }
      var consultantTenant = consultantTargetTenants.get(i);

      log.info(
          "Attempt to set tenantId for {} with id {} to {}",
          "consultant",
          consultantTenant.getUserId(),
          consultantTenant.getTenantId());

      keycloakUserService.updateUserCustomAttributeWithoutLogin(
          "tenantId", consultantTenant.getTenantId(), consultantTenant.getUserId(), httpHeaders);

      userServiceJdbcTemplate.update(
          "UPDATE consultant_agency SET tenant_id = ? WHERE consultant_id = ?",
          consultantTenant.getTenantId(),
          consultantTenant.getUserId());

      userServiceJdbcTemplate.update(
          "UPDATE consultant SET tenant_id = ? WHERE consultant_id = ?",
          consultantTenant.getTenantId(),
          consultantTenant.getUserId());

      userServiceJdbcTemplate.update(
          "UPDATE session SET tenant_id = ? WHERE consultant_id = ?",
          consultantTenant.getTenantId(),
          consultantTenant.getUserId());

      log.info(
          "Successfully set tenantId for {} with id {} to {}",
          "consultant, consultant_agency, session and keycloak user",
          consultantTenant.getUserId(),
          consultantTenant.getTenantId());
    }
  }
}
