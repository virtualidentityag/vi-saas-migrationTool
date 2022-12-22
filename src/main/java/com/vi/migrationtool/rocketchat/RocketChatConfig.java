package com.vi.migrationtool.rocketchat;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "rocketchat.config")
public class RocketChatConfig {

  private String serverUrl;
  private String adminUsername;
  private String adminPassword;
}
