package com.vi.migrationtool.weblate;

import java.io.IOException;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

@Slf4j
public class WeblateErrorResponseHandler {

  private WeblateErrorResponseHandler() {
    throw new IllegalStateException("Utility class");
  }

  public static void handleConflictOrBadRequestResponse(
      String restResourceName, ResponseEntity<String> responseEntity) {
    if (responseEntity == null) {
      log.error("Error creating {} in weblate: API response is null", restResourceName);
      throw new IllegalStateException("Error creating project in weblate: API response is null");
    }

    if (responseEntity.getStatusCode().value() == HttpStatus.CONFLICT.value()) {
      log.info("{} already exists in weblate: {}", restResourceName, responseEntity.getBody());
    }

    if (responseEntity.getStatusCode().value() == HttpStatus.BAD_REQUEST.value()) {
      if (responseEntity.getBody().contains("already exists")) {
        log.info("{} already exists in weblate: {}", restResourceName, responseEntity.getBody());
      } else {
        log.error("Error creating {} in weblate: {}", restResourceName, responseEntity.getBody());
        throw new IllegalStateException(
            "Error creating resource in weblate: " + responseEntity.getBody());
      }
    }
  }

  public static ResponseErrorHandler nonFaultTolerantResponseErrorHandler() {
    return getResponseErrorHandler(
        response -> {
          try {
            throw new IllegalStateException(
                "Received exception calling weblate API, migration will fail. ResponseStatus "
                    + response.getStatusCode());
          } catch (IOException e) {
            throw new IllegalStateException(e);
          }
        });
  }

  public static ResponseErrorHandler getResponseErrorHandler(
      Consumer<ClientHttpResponse> errorHandler) {
    return new ResponseErrorHandler() {
      @Override
      public boolean hasError(ClientHttpResponse response) throws IOException {
        var statusCode = response.getStatusCode();

        return (statusCode.is4xxClientError() || statusCode.is5xxServerError())
            && !response.getStatusCode().equals(HttpStatus.CONFLICT)
            && !response.getStatusCode().equals(HttpStatus.BAD_REQUEST);
      }

      @Override
      public void handleError(ClientHttpResponse response) throws IOException {
        log.warn("Handling weblate error response");
        log.error(
            "Received weblate status: {} - {} ",
            response.getStatusCode(),
            response.getStatusText());
        errorHandler.accept(response);
      }
    };
  }
}
