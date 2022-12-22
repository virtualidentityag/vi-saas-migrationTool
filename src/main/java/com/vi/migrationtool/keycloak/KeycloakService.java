package com.vi.migrationtool.keycloak;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KeycloakService {

  private final KeycloakConfig keycloakConfig;

  public void createRole(String roleName) {
    var httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    KeycloakLoginResponseDTO loginResponse = loginAdminUser();
    httpHeaders.setBearerAuth(loginResponse.getAccessToken());
    HttpEntity entity = new HttpEntity<>(getCreateRoleBody(roleName), httpHeaders);
    var createRoleUrl =
        keycloakConfig.getAuthServerUrl() + "/admin/realms/" + keycloakConfig.getRealm() + "/roles";
    new RestTemplate().postForEntity(createRoleUrl, entity, Void.class);
  }

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

  private String getCreateRoleBody(String roleName) {
    try {
      JSONObject body = new JSONObject();
      body.put("name", roleName);
      return body.toString();
    } catch (JSONException e) {
      return null;
    }
  }
}
