package com.vi.migrationtool.keycloak;

import java.io.IOException;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

@Slf4j
public class KeycloakErrorResponseHandler {

  public static ResponseErrorHandler getResponseErrorHandler(
      Consumer<ClientHttpResponse> errorHandler) {
    return new ResponseErrorHandler() {
      @Override
      public boolean hasError(ClientHttpResponse response) throws IOException {
        var statusCode = response.getStatusCode();
        return (statusCode.is4xxClientError() || statusCode.is5xxServerError())
            && !response.getStatusCode().equals(HttpStatus.CONFLICT);
      }

      @Override
      public void handleError(ClientHttpResponse response) throws IOException {
        log.warn("Handling keycloak error response");
        log.error(
            "Received keycloak status: {} - {} ",
            response.getStatusCode(),
            response.getStatusText());
        errorHandler.accept(response);
      }
    };
  }
}
