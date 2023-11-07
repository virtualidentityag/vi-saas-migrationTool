package com.vi.migrationtool.keycloak;

import java.util.Objects;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "users.config")
public class TechnicalUsersConfig {

  @Value("${users.jitsi-technical.password}")
  private String jitsiTechnicalUserPassword;

  @Value("${users.jitsi-technical.email}")
  private String jitsiTechnicalUserEmail;

  @Value("${users.jitsi-technical.tenantId}")
  private Long jitsiTechnicalUserTenantId;

  public String getCredentialsByUsername(TechnicalUsers technicalUser) {
    if (technicalUser == TechnicalUsers.JITSI_TECHNICAL) {
      return jitsiTechnicalUserPassword;
    }
    throw new IllegalStateException(getUnknownTechnicalUserMessage(technicalUser));
  }

  public String getEmailByUsername(TechnicalUsers technicalUser) {
    if (Objects.requireNonNull(technicalUser) == TechnicalUsers.JITSI_TECHNICAL) {
      return jitsiTechnicalUserEmail;
    }
    throw new IllegalStateException(getUnknownTechnicalUserMessage(technicalUser));
  }

  private String getUnknownTechnicalUserMessage(TechnicalUsers technicalUser) {
    return "Unknown technical user: " + technicalUser;
  }

  public Long getTenantIdByUsername(TechnicalUsers technicalUser) {
    if (technicalUser == TechnicalUsers.JITSI_TECHNICAL) {
      return jitsiTechnicalUserTenantId;
    }
    throw new IllegalStateException(getUnknownTechnicalUserMessage(technicalUser));
  }
}
