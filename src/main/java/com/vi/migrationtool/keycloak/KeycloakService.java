package com.vi.migrationtool.keycloak;

import static java.util.Objects.isNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakService {

  private static final String SEARCH_PARAM = "search";
  private static final String MAX_USERS_TO_MIGRATE = "100";
  private static final String ADMIN_REALMS = "/admin/realms/";
  private final KeycloakConfig keycloakConfig;

  public void createRole(String roleName) {
    var httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    KeycloakLoginResponseDTO loginResponse = loginAdminUser();
    httpHeaders.setBearerAuth(loginResponse.getAccessToken());
    HttpEntity entity = new HttpEntity<>(getCreateRoleBody(roleName), httpHeaders);
    var createRoleUrl =
        keycloakConfig.getAuthServerUrl() + ADMIN_REALMS + keycloakConfig.getRealm() + "/roles";
    var restTemplate = new RestTemplate();
    restTemplate.setErrorHandler(getResponseErrorHandler());
    restTemplate.postForEntity(createRoleUrl, entity, Void.class);
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

  public void addRoleToUsers(final List<String> usernames, final String roleName) {
    var httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    KeycloakLoginResponseDTO loginResponse = loginAdminUser();
    httpHeaders.setBearerAuth(loginResponse.getAccessToken());

    Optional<RoleRepresentation> role = getRoleBy(roleName, httpHeaders);
    if (role.isEmpty()) {
      log.error(
          "The provided role {} doesn't exists in keycloak, please create it first", roleName);
    }

    var restTemplate = new RestTemplate();
    restTemplate.setErrorHandler(getResponseErrorHandler());
    usernames.forEach(username -> addRoleToUser(username, role.get(), httpHeaders, restTemplate));
  }

  public void addRolesToUsersWithRoleName(
      final String roleNameToSearchForUsers, final List<String> rolesNameToAdd) {
    var httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    KeycloakLoginResponseDTO loginResponse = loginAdminUser();
    httpHeaders.setBearerAuth(loginResponse.getAccessToken());

    List<KeycloakUser> keycloakUsers = getUsersWithRoleName(roleNameToSearchForUsers);
    if (keycloakUsers.isEmpty()) {
      log.info(
          "No users found with the given role {}. Migration will not be applied",
          roleNameToSearchForUsers);
    }
    var restTemplate = new RestTemplate();
    restTemplate.setErrorHandler(getResponseErrorHandler());
    rolesNameToAdd.stream()
        .forEach(
            role -> tryToAddRoleToKeycloakUser(role, keycloakUsers, httpHeaders, restTemplate));
  }

  private void tryToAddRoleToKeycloakUser(
      String roleNameDoAdd,
      List<KeycloakUser> keycloakUsers,
      HttpHeaders httpHeaders,
      RestTemplate restTemplate) {
    Optional<RoleRepresentation> role = getRoleBy(roleNameDoAdd, httpHeaders);
    if (role.isEmpty()) {
      log.error(
          "The provided role {} doesn't exists in keycloak, please create it first", roleNameDoAdd);
    }
    keycloakUsers.forEach(
        user -> callKeycloakToAddRoleToUser(role.get(), httpHeaders, restTemplate, user));
  }

  private void addRoleToUser(
      String username,
      RoleRepresentation role,
      HttpHeaders httpHeaders,
      RestTemplate restTemplate) {
    final Optional<KeycloakUser> keycloakUser =
        getUsersBy(username, httpHeaders).stream()
            .filter(user -> user.getUsername().equals(username))
            .findFirst();
    if (keycloakUser.isEmpty()) {
      log.warn("The user to be updated: {} was not found in keycloak", username);
      return;
    }

    callKeycloakToAddRoleToUser(role, httpHeaders, restTemplate, keycloakUser.get());
  }

  private void callKeycloakToAddRoleToUser(
      RoleRepresentation role,
      HttpHeaders httpHeaders,
      RestTemplate restTemplate,
      KeycloakUser keycloakUser) {
    var updateUserRolesUrl =
        keycloakConfig.getAuthServerUrl()
            + ADMIN_REALMS
            + keycloakConfig.getRealm()
            + "/users/"
            + keycloakUser.getId()
            + "/role-mappings/realm";
    HttpEntity entity = new HttpEntity<>(getAddRoleToUserBody(role), httpHeaders);
    ResponseEntity<Void> response =
        restTemplate.postForEntity(updateUserRolesUrl, entity, Void.class);

    if (response.getStatusCode() == HttpStatus.CONFLICT) {
      log.info(
          "Role was not added to the user {}, because it already was assigned",
          role,
          keycloakUser.getUsername());
      return;
    }

    if (response.getStatusCode() != HttpStatus.INTERNAL_SERVER_ERROR) {
      log.info("Role {} was added to the user {} successfully", role, keycloakUser.getUsername());
    }
  }

  private List<KeycloakUser> getUsersBy(final String searchTerm, final HttpHeaders httpHeaders) {
    var getUsersBySearchTermURL =
        urlWithSearchParam(
            keycloakConfig.getAuthServerUrl()
                + ADMIN_REALMS
                + keycloakConfig.getRealm()
                + "/users");

    HttpEntity requestEntity = new HttpEntity<>(httpHeaders);

    final Map<String, String> params = new HashMap<>();
    params.put(SEARCH_PARAM, searchTerm);
    var restTemplate = new RestTemplate();
    ResponseEntity<KeycloakUser[]> response =
        restTemplate.exchange(
            getUsersBySearchTermURL, HttpMethod.GET, requestEntity, KeycloakUser[].class, params);
    if (isNull(response.getBody())) {
      log.warn("No user found in keycloak using search param {}", searchTerm);
    }
    return List.of(response.getBody());
  }

  public List<KeycloakUser> getUsersWithRoleName(final String roleName) {
    var httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    KeycloakLoginResponseDTO loginResponse = loginAdminUser();
    httpHeaders.setBearerAuth(loginResponse.getAccessToken());
    return getUsersWithRoleName(roleName, httpHeaders);
  }

  private List<KeycloakUser> getUsersWithRoleName(
      final String roleName, final HttpHeaders httpHeaders) {

    var url =
        keycloakConfig.getAuthServerUrl()
            + "/admin/realms/online-beratung/roles/"
            + roleName
            + "/users?first=0&max="
            + MAX_USERS_TO_MIGRATE;
    var getUsersBySearchTermURL = getUrl(url);

    HttpEntity requestEntity = new HttpEntity<>(httpHeaders);
    var restTemplate = new RestTemplate();
    ResponseEntity<KeycloakUser[]> response =
        restTemplate.exchange(
            getUsersBySearchTermURL,
            HttpMethod.GET,
            requestEntity,
            KeycloakUser[].class,
            new Object[] {});
    if (isNull(response.getBody())) {
      log.warn("No user found in keycloak using search param {}", getUsersBySearchTermURL);
    }
    return List.of(response.getBody());
  }

  private Optional<RoleRepresentation> getRoleBy(
      final String roleName, final HttpHeaders httpHeaders) {

    HttpEntity requestEntity = new HttpEntity<>(httpHeaders);
    var getRolesBySearchTermUrl =
        urlWithSearchParam(
            keycloakConfig.getAuthServerUrl()
                + ADMIN_REALMS
                + keycloakConfig.getRealm()
                + "/roles");
    final Map<String, String> params = new HashMap<>();
    params.put(SEARCH_PARAM, roleName);

    var restTemplate = new RestTemplate();
    ResponseEntity<RoleRepresentation[]> response =
        restTemplate.exchange(
            getRolesBySearchTermUrl,
            HttpMethod.GET,
            requestEntity,
            RoleRepresentation[].class,
            params);
    if (isNull(response.getBody())) {
      log.warn("No role found in keycloak using search param {}", roleName);
    }
    return Arrays.stream(response.getBody())
        .filter(role -> role.getName().equals(roleName))
        .findFirst();
  }

  private String getUrl(String url) {
    return UriComponentsBuilder.fromHttpUrl(url).encode().toUriString();
  }

  private String urlWithSearchParam(String url) {
    return UriComponentsBuilder.fromHttpUrl(url)
        .queryParam(SEARCH_PARAM, "{search}")
        .encode()
        .toUriString();
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

  private String getAddRoleToUserBody(final RoleRepresentation role) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.writeValueAsString(Collections.singletonList(role));
    } catch (JsonProcessingException e) {
      return null;
    }
  }

  private static ResponseErrorHandler getResponseErrorHandler() {
    return new ResponseErrorHandler() {
      @Override
      public boolean hasError(ClientHttpResponse response) throws IOException {
        return !response.getStatusCode().equals(HttpStatus.CONFLICT);
      }

      @Override
      public void handleError(ClientHttpResponse response) throws IOException {
        log.warn("Handling keycloak error response");
      }
    };
  }
}
