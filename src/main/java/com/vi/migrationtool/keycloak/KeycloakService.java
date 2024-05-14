package com.vi.migrationtool.keycloak;

import static com.vi.migrationtool.keycloak.KeycloakConfig.ADMIN_REALMS;
import static com.vi.migrationtool.keycloak.KeycloakErrorResponseHandler.getResponseErrorHandler;
import static java.util.Objects.isNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.Lists;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakService {

  private static final String SEARCH_PARAM = "search";
  private static final short USER_PAGE_SIZE = 500;
  private static final String MAX_USERS_TO_MIGRATE = "850";
  private static final String PROVIDED_ROLE_DOESNT_EXISTS_IN_KEYCLOAK_MSG =
      "The provided role {} doesn't exists in keycloak, please create it first";
  private final KeycloakConfig keycloakConfig;

  private final KeycloakLoginService keycloakLoginService;

  public void createRole(String roleName) {
    var httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    KeycloakLoginResponseDTO loginResponse = keycloakLoginService.loginAdminUser();
    httpHeaders.setBearerAuth(loginResponse.getAccessToken());
    HttpEntity entity = new HttpEntity<>(getCreateRoleBody(roleName), httpHeaders);
    var createRoleUrl =
        keycloakConfig.getAuthServerUrl() + ADMIN_REALMS + keycloakConfig.getRealm() + "/roles";
    var restTemplate = new RestTemplate();
    restTemplate.setErrorHandler(nonFaultTolerantResponseErrorHandler());
    restTemplate.postForEntity(createRoleUrl, entity, Void.class);
  }

  public void addRoleToUsers(final List<String> usernames, final String roleName) {
    var httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    KeycloakLoginResponseDTO loginResponse = keycloakLoginService.loginAdminUser();
    httpHeaders.setBearerAuth(loginResponse.getAccessToken());

    Optional<RoleRepresentation> role = getRoleBy(roleName, httpHeaders);
    if (role.isEmpty()) {
      log.error(PROVIDED_ROLE_DOESNT_EXISTS_IN_KEYCLOAK_MSG, roleName);
    }

    var restTemplate = new RestTemplate();
    restTemplate.setErrorHandler(faultTolerantResponseErrorHandler());
    usernames.forEach(username -> addRoleToUser(username, role.get(), httpHeaders, restTemplate));
  }

  public void addRolesToUsersWithRoleName(
      final String roleNameToSearchForUsers, final List<String> rolesNameToAdd) {
    var httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    KeycloakLoginResponseDTO loginResponse = keycloakLoginService.loginAdminUser();
    httpHeaders.setBearerAuth(loginResponse.getAccessToken());

    var pageNumber = 1;
    var users = getUsersWithRoleName(roleNameToSearchForUsers, pageNumber);
    List<KeycloakUser> keycloakUsers = Lists.newArrayList();
    while (!users.isEmpty()) {
      keycloakUsers.addAll(users);
      pageNumber++;
      users = getUsersWithRoleName(roleNameToSearchForUsers, pageNumber);
    }

    if (keycloakUsers.isEmpty()) {
      log.info(
          "No users found with the given role {}. Migration will not be applied",
          roleNameToSearchForUsers);
    }
    var restTemplate = new RestTemplate();
    restTemplate.setErrorHandler(faultTolerantResponseErrorHandler());
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
      log.error(PROVIDED_ROLE_DOESNT_EXISTS_IN_KEYCLOAK_MSG, roleNameDoAdd);
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

  public List<KeycloakUser> getUsersWithRoleName(final String roleName, int pageNumberedFromOne) {
    var httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    KeycloakLoginResponseDTO loginResponse = keycloakLoginService.loginAdminUser();
    httpHeaders.setBearerAuth(loginResponse.getAccessToken());
    return getUsersWithRoleName(roleName, getFirstElementIndex(pageNumberedFromOne), httpHeaders);
  }

  private List<KeycloakUser> getUsersWithRoleName(
      final String roleName, final int firstElementIndex, final HttpHeaders httpHeaders) {

    var url =
        keycloakConfig.getAuthServerUrl()
            + "/admin/realms/online-beratung/roles/"
            + roleName
            + "/users?first="
            + firstElementIndex
            + "&max="
            + USER_PAGE_SIZE;
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
      return Lists.newArrayList();
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

  public List<UsersWithRole> addCustomAttributeToUsersWithRole(
      String customAttribute, Long value, List<String> roleNames) {
    if (roleNames != null) {
      return roleNames.stream()
          .map(roleName -> addCustomAttributeToUsersWithRole(customAttribute, value, roleName))
          .collect(Collectors.toList());
    } else {
      log.warn("No role names provided: {}", this.getClass().getSimpleName());
      return Collections.emptyList();
    }
  }

  private UsersWithRole addCustomAttributeToUsersWithRole(
      String customAttribute, Long value, String roleName) {

    var httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);

    authenticateInKeycloak(httpHeaders);

    Optional<RoleRepresentation> role = getRoleBy(roleName, httpHeaders);
    if (role.isEmpty()) {
      log.error(PROVIDED_ROLE_DOESNT_EXISTS_IN_KEYCLOAK_MSG, roleName);
    }

    var restTemplate = new RestTemplate();
    restTemplate.setErrorHandler(faultTolerantResponseErrorHandler());

    Collection<String> updatedUsers = Lists.newArrayList();
    var pageNumber = 1;
    var users = getUsersWithRoleName(roleName, getFirstElementIndex(pageNumber));
    while (!users.isEmpty()) {
      authenticateInKeycloak(httpHeaders);
      var migratedUsersPage =
          addCustomAttributeToUsers(customAttribute, value, httpHeaders, restTemplate, users);
      pageNumber++;
      updatedUsers.addAll(migratedUsersPage);
      users = getUsersWithRoleName(roleName, pageNumber);
    }

    return new UsersWithRole(roleName, updatedUsers);
  }

  private void authenticateInKeycloak(HttpHeaders httpHeaders) {
    KeycloakLoginResponseDTO loginResponse = keycloakLoginService.loginAdminUser();
    httpHeaders.setBearerAuth(loginResponse.getAccessToken());
  }

  private List<String> addCustomAttributeToUsers(
      String customAttribute,
      Long value,
      HttpHeaders httpHeaders,
      RestTemplate restTemplate,
      List<KeycloakUser> users) {
    return users.stream()
        .map(
            user ->
                addCustomAttributeToUserIfDoesNotExist(
                    customAttribute, value, httpHeaders, restTemplate, user))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
  }

  private int getFirstElementIndex(int pageNumberedFromOne) {
    return (pageNumberedFromOne - 1) * USER_PAGE_SIZE;
  }

  private Optional<String> addCustomAttributeToUserIfDoesNotExist(
      String customAttribute,
      Long customAttributeValue,
      HttpHeaders httpHeaders,
      RestTemplate restTemplate,
      KeycloakUser user) {
    var updateUserUrl =
        keycloakConfig.getAuthServerUrl()
            + ADMIN_REALMS
            + keycloakConfig.getRealm()
            + "/users/"
            + user.getId();

    Map<String, Object> attributes = (Map<String, Object>) user.getAttributes();
    if (attributes.containsKey(customAttribute)) {
      log.info(
          "User {} already has the custom attribute {}, will not override it's value",
          user.getUsername(),
          customAttribute);
      return Optional.empty();
    }
    attributes.put(customAttribute, customAttributeValue);
    try {
      restTemplate.exchange(
          updateUserUrl, HttpMethod.PUT, new HttpEntity<>(user, httpHeaders), Void.class);
    } catch (Exception e) {
      log.error(
          "Error while adding custom attribute {} = {}, to user {}",
          customAttribute,
          customAttributeValue,
          user.getUsername());
      return Optional.empty();
    }
    log.info(
        "Added keycloak attribute {} = {}, to user {}",
        customAttribute,
        customAttributeValue,
        user.getUsername());
    return Optional.of(user.getId());
  }

  private static ResponseErrorHandler faultTolerantResponseErrorHandler() {
    return getResponseErrorHandler(
        response -> log.error("Received error response from keycloak: " + response));
  }

  private static ResponseErrorHandler nonFaultTolerantResponseErrorHandler() {
    return getResponseErrorHandler(
        response -> {
          log.error("Received response: " + response);
          throw new IllegalStateException(
              "Received exception calling keycloak API, migration will fail");
        });
  }
}
