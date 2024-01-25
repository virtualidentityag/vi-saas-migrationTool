package com.vi.migrationtool.keycloak;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "keycloak.config")
public class KeycloakConfig {

  public static final String ADMIN_REALMS = "/admin/realms/";

  @Value("${keycloak.auth-server-url}")
  private String authServerUrl;

  @Value("${keycloak.realm}")
  private String realm;

  private String adminUsername;
  private String adminPassword;
  private String adminClientId;
}
