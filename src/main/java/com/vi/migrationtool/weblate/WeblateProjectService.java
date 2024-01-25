package com.vi.migrationtool.weblate;

import com.vi.migrationtool.weblate.config.ProjectDTO;
import com.vi.migrationtool.weblate.config.WeblateConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeblateProjectService {

  private static final String PROJECTS = "/projects/";
  private final WeblateConfig weblateConfig;

  public String createProject(ProjectDTO projectDTO) {
    var url = weblateConfig.getApiUrl() + PROJECTS;

    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setErrorHandler(
        WeblateErrorResponseHandler.nonFaultTolerantResponseErrorHandler());
    restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

    ResponseEntity<String> responseEntity =
        restTemplate.postForEntity(url, getRequestHeaders(projectDTO), String.class);

    if (HttpStatus.CREATED.equals(responseEntity.getStatusCode())) {
      log.info("Project created in weblate: {}", responseEntity.getBody());
      return responseEntity.getBody();
    }

    WeblateErrorResponseHandler.handleConflictOrBadRequestResponse("project", responseEntity);
    return responseEntity.getBody();
  }

  private HttpEntity<ProjectDTO> getRequestHeaders(ProjectDTO projectDTO) {
    var httpHeaders = new HttpHeaders();
    httpHeaders.add("Authorization", "Token " + weblateConfig.getApiKey());
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<>(projectDTO, httpHeaders);
  }
}
