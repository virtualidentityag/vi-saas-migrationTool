package com.vi.migrationtool.keycloak;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeycloakUser implements Serializable {

  @JsonProperty("id")
  private String id;

  @JsonProperty("username")
  private String username;
}
