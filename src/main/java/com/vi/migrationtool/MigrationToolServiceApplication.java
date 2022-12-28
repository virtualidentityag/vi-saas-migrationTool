package com.vi.migrationtool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;

/** Starter class for the application. */
@SpringBootApplication
@EnableConfigurationProperties
@ConfigurationPropertiesScan
public class MigrationToolServiceApplication {

  /**
   * Global application entry point.
   *
   * @param args possible provided args
   */
  public static void main(String[] args) {
    SpringApplication.run(MigrationToolServiceApplication.class, args);
  }

  @EventListener(ApplicationReadyEvent.class)
  public void appReady() {
    System.exit(0);
  }
}
