package com.vi.migrationtool.keycloak;

import static com.vi.migrationtool.keycloak.KeycloakConfig.ADMIN_REALMS;
import static com.vi.migrationtool.keycloak.KeycloakErrorResponseHandler.getResponseErrorHandler;
import static java.util.Objects.isNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.Lists;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakUserService {
  private final KeycloakConfig keycloakConfig;

  private final KeycloakLoginService keycloakLoginService;

  private final TechnicalUsersConfig technicalUsersConfig;

  private String getUrl(String url) {
    return UriComponentsBuilder.fromHttpUrl(url).encode().toUriString();
  }

  private static ResponseErrorHandler nonFaultTolerantResponseErrorHandler() {
    return getResponseErrorHandler(
        response -> {
          try {
            String responseBody = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
            log.error("Received response {}", responseBody);
            throw new IllegalStateException(
                String.format(
                    "Received exception calling keycloak API, migration will fail. Status %s, Response body: %s",
                    response.getStatusCode(), responseBody));
          } catch (IOException e) {
            log.error("Could not get status code from response", e);
          }
        });
  }

  private List<UserRepresentation> searchUsersByUserName(
      final String userName, final HttpHeaders httpHeaders) {

    var url =
        keycloakConfig.getAuthServerUrl()
            + "/admin/realms/online-beratung/ui-ext/brute-force-user?briefRepresentation=true&first=0&max=21&q=&search="
            + userName;
    var getUsersBySearchTermURL = getUrl(url);

    HttpEntity requestEntity = new HttpEntity<>(httpHeaders);
    var restTemplate = new RestTemplate();
    ResponseEntity<UserRepresentation[]> response =
        restTemplate.exchange(
            getUsersBySearchTermURL,
            HttpMethod.GET,
            requestEntity,
            UserRepresentation[].class,
            new Object[] {});
    if (isNull(response.getBody())) {
      log.warn("No user found in keycloak using search param {}", getUsersBySearchTermURL);
    }
    return List.of(response.getBody());
  }

  public void createUser(final String userName, String password, String email, Long tenantId) {

    var httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    KeycloakLoginResponseDTO loginResponse = keycloakLoginService.loginAdminUser();
    httpHeaders.setBearerAuth(loginResponse.getAccessToken());
    var userCreateResponse =
        createUserInKeycloakAndThrowOnException(userName, email, tenantId, httpHeaders);

    if (userCreateResponse.getStatusCode().is2xxSuccessful()) {
      List<UserRepresentation> usersWithUserName = searchUsersByUserName(userName, httpHeaders);
      Assert.isTrue(
          usersWithUserName.size() == 1,
          String.format(
              "Expected to find exactly one user with username %s, but found %s",
              userName, usersWithUserName.size()));
      UserRepresentation userRepresentation = usersWithUserName.get(0);
      resetUserCredentials(userRepresentation.getId(), password, httpHeaders);
    } else if (userCreateResponse.getStatusCode().equals(HttpStatus.CONFLICT)) {
      log.warn("User with username {} already exists in keycloak", userName);
    } else {
      throw new IllegalStateException("Did not create user. Migration should fail");
    }
  }

  private void resetUserCredentials(String id, String password, HttpHeaders httpHeaders) {
    var url =
        keycloakConfig.getAuthServerUrl()
            + ADMIN_REALMS
            + keycloakConfig.getRealm()
            + "/users/"
            + id
            + "/reset-password";
    var resetUserCredentialsURL = getUrl(url);
    HttpEntity entity = new HttpEntity<>(getResetUserCredentialsBody(password), httpHeaders);
    var restTemplate = new RestTemplate();
    restTemplate.setErrorHandler(nonFaultTolerantResponseErrorHandler());
    restTemplate.postForEntity(resetUserCredentialsURL, entity, String.class);
  }

  private Object getResetUserCredentialsBody(String password) {
    try {
      JSONObject body = new JSONObject();
      body.put("type", "password");
      body.put("value", password);
      body.put("temporary", false);
      return body.toString();
    } catch (JSONException e) {
      return null;
    }
  }

  private ResponseEntity<UserRepresentation> createUserInKeycloakAndThrowOnException(
      String userName, String email, Long tenantId, HttpHeaders httpHeaders) {
    HttpEntity entity = new HttpEntity<>(getCreateUserBody(userName, email, tenantId), httpHeaders);
    var createUserUrl =
        keycloakConfig.getAuthServerUrl() + ADMIN_REALMS + keycloakConfig.getRealm() + "/users";
    var restTemplate = new RestTemplate();
    restTemplate.setErrorHandler(nonFaultTolerantResponseErrorHandler());
    return restTemplate.postForEntity(createUserUrl, entity, UserRepresentation.class);
  }

  private String getCreateUserBody(String userName, String email, Long tenantId) {
    UserRepresentation userRepresentation = new UserRepresentation();
    userRepresentation.setUsername(userName);
    userRepresentation.setEmail(email);
    userRepresentation.setEmailVerified(false);
    userRepresentation.setEnabled(true);
    userRepresentation.setAttributes(null);
    userRepresentation.setGroups(Lists.newArrayList());
    userRepresentation.setAttributes(Map.of("tenantId", List.of(String.valueOf(tenantId))));
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writeValueAsString(userRepresentation);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }
}
