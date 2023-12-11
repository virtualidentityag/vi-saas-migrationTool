package com.vi.migrationtool.weblate;

import static com.vi.migrationtool.weblate.WeblateErrorResponseHandler.nonFaultTolerantResponseErrorHandler;

import com.vi.migrationtool.weblate.config.WeblateConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.helper.Validate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeblateComponentService {
  private final WeblateConfig weblateConfig;

  private final WeblateResourceLoader weblateResourceLoader;

  public String createComponentForProject(String projectName, String componentName) {

    Validate.notNull(projectName, "Project name must not be null");
    Validate.notNull(componentName, "Component name must not be null");

    String componentCreateJson = weblateResourceLoader.readResourceContentByName(componentName);

    var url = weblateConfig.getServerUrl() + "/api/projects/" + projectName + "/components/";

    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setErrorHandler(nonFaultTolerantResponseErrorHandler());
    ResponseEntity<String> responseEntity =
        restTemplate.postForEntity(url, getRequestHeaders(componentCreateJson), String.class);

    if (HttpStatus.CREATED.equals(responseEntity.getStatusCode())) {
      log.info("Project created in weblate: {}", responseEntity.getBody());
      return responseEntity.getBody();
    }

    WeblateErrorResponseHandler.handleConflictOrBadRequestResponse("component", responseEntity);
    return responseEntity.getBody();
  }

  private HttpEntity<String> getRequestHeaders(String requestPayload) {
    var httpHeaders = new HttpHeaders();
    httpHeaders.add("Authorization", "Token " + weblateConfig.getApiKey());
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<>(requestPayload, httpHeaders);
  }
}
