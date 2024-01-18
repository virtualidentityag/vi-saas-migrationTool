package com.vi.migrationtool.weblate.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "weblate.config")
public class WeblateConfig {

  private String apiUrl;
  private String apiKey;
  private String slug;
  private String web;
}
