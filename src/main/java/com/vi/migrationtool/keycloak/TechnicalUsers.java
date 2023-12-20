package com.vi.migrationtool.keycloak;

import lombok.Getter;

@Getter
public enum TechnicalUsers {
  JITSI_TECHNICAL("jitsi-technical");
  private String username;

  private TechnicalUsers(String username) {
    this.username = username;
  }
}
