package com.vi.migrationtool.keycloak;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakLoginService {

  private static final String SEARCH_PARAM = "search";
  private static final String MAX_USERS_TO_MIGRATE = "500";
  private static final String PROVIDED_ROLE_DOESNT_EXISTS_IN_KEYCLOAK_MSG =
      "The provided role {} doesn't exists in keycloak, please create it first";
  private final KeycloakConfig keycloakConfig;

  public KeycloakLoginResponseDTO loginAdminUser() {
    return loginUser(keycloakConfig.getAdminUsername(), keycloakConfig.getAdminPassword());
  }

  public KeycloakLoginResponseDTO loginUser(final String userName, final String password) {
    var entity = loginRequest(userName, password);
    var url =
        keycloakConfig.getAuthServerUrl()
            + "/realms/"
            + keycloakConfig.getRealm()
            + "/protocol/openid-connect/token";
    return new RestTemplate().postForEntity(url, entity, KeycloakLoginResponseDTO.class).getBody();
  }

  private HttpEntity<MultiValueMap<String, String>> loginRequest(String userName, String password) {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("username", userName);
    map.add("password", password);
    map.add("client_id", keycloakConfig.getAdminClientId());
    map.add("grant_type", "password");
    var httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    return new HttpEntity<>(map, httpHeaders);
  }
}
