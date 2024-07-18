package com.vi.migrationtool.keycloak;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantUpdateService {

  private static final int USER_PAGE_SIZE = 300;
  private static final String TENANT_ID = "tenantId";
  private static final String SUCCESSFULLY_SET_TENANT_ID_FOR_WITH_ID_TO = "Successfully set tenantId for {} with id {} to {}";

  private final KeycloakUserService keycloakUserService;

  private final KeycloakLoginService keycloakLoginService;

  private final JdbcTemplate userServiceJdbcTemplate;

  private HttpHeaders authenticateInKeycloak() {
    var httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    KeycloakLoginResponseDTO loginResponse = keycloakLoginService.loginAdminUser();
    httpHeaders.setBearerAuth(loginResponse.getAccessToken());
    return httpHeaders;
  }

  public void updateAdviceSeekersTenant(List<UserTenant> adviceSeekerToTargetTenantCollection) {
    var httpHeaders = authenticateInKeycloak();
    if (adviceSeekerToTargetTenantCollection.isEmpty()) {
      log.info("No advice seeker found with different tenantId");
      return;
    }
    for (int i = 0; i < adviceSeekerToTargetTenantCollection.size(); i++) {
      var adviceSeekerTenant = adviceSeekerToTargetTenantCollection.get(i);
      if (i % USER_PAGE_SIZE == 0) {
        httpHeaders = authenticateInKeycloak();
      }
      log.info(
          "Attempt to set tenantId for adviceseeker with id  {} to {}",
          adviceSeekerTenant.getUserId(),
          adviceSeekerTenant.getTenantId());

      keycloakUserService.updateUserCustomAttributeWithoutLogin(
          TENANT_ID,
          adviceSeekerTenant.getTenantId(),
          adviceSeekerTenant.getUserId(),
          httpHeaders);

      updateTables(adviceSeekerTenant);
      log.info(
          SUCCESSFULLY_SET_TENANT_ID_FOR_WITH_ID_TO,
          "advice seeker, user and keycloak user",
          adviceSeekerTenant.getUserId(),
          adviceSeekerTenant.getTenantId());
    }
  }

  private void updateTables(UserTenant adviceSeekerTenant) {
    userServiceJdbcTemplate.update(
        "UPDATE user SET tenant_id = ? WHERE user_id = ?",
        adviceSeekerTenant.getTenantId(),
        adviceSeekerTenant.getUserId());

    userServiceJdbcTemplate.update(
        "UPDATE session SET tenant_id = ? WHERE user_id = ?",
        adviceSeekerTenant.getTenantId(),
        adviceSeekerTenant.getUserId());
  }

  public void updateConsultantsTenant(List<UserTenant> consultantTargetTenants) {

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
          TENANT_ID, consultantTenant.getTenantId(), consultantTenant.getUserId(), httpHeaders);

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
          SUCCESSFULLY_SET_TENANT_ID_FOR_WITH_ID_TO,
          "consultant, consultant_agency, session and keycloak user",
          consultantTenant.getUserId(),
          consultantTenant.getTenantId());
    }
  }

  public void updateAdminTenant(List<UserTenant> adminTargetTenants) {

    if (adminTargetTenants.isEmpty()) {
      log.info("No admin found with different tenantId");
      return;
    }
    var httpHeaders = authenticateInKeycloak();
    for (int i = 0; i < adminTargetTenants.size(); i++) {
      if (i % USER_PAGE_SIZE == 0) {
        httpHeaders = authenticateInKeycloak();
      }
      var adminTenant = adminTargetTenants.get(i);

      log.info(
          "Attempt to set tenantId for {} with id {} to {}",
          "consultant",
          adminTenant.getUserId(),
          adminTenant.getTenantId());

      keycloakUserService.updateUserCustomAttributeWithoutLogin(
          TENANT_ID, adminTenant.getTenantId(), adminTenant.getUserId(), httpHeaders);

      userServiceJdbcTemplate.update(
          "UPDATE admin SET tenant_id = ? WHERE admin_id = ?",
          adminTenant.getTenantId(),
          adminTenant.getUserId());

      log.info(
          SUCCESSFULLY_SET_TENANT_ID_FOR_WITH_ID_TO,
          "admin table and keycloak user",
          adminTenant.getUserId(),
          adminTenant.getTenantId());
    }
  }
}
