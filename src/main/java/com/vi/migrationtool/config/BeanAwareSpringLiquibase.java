package com.vi.migrationtool.config;

import liquibase.integration.spring.SpringLiquibase;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ResourceLoader;

@NoArgsConstructor
public class BeanAwareSpringLiquibase extends SpringLiquibase {

  private static ResourceLoader applicationContext;

  public static <T> T getBean(Class<T> beanClass) {
    if (applicationContext instanceof ApplicationContext) {
      return ((ApplicationContext) applicationContext).getBean(beanClass);
    } else {
      return null;
    }
  }

  public static <T> T getNamedBean(String name, Class<T> beanClass) {
    if (applicationContext instanceof ApplicationContext) {
      return ((ApplicationContext) applicationContext).getBean(name, beanClass);
    }
    throw new RuntimeException(
        String.format("Could not find bean with name %s and type %s", name, beanClass));
  }

  @Override
  public void setResourceLoader(ResourceLoader resourceLoader) {
    super.setResourceLoader(resourceLoader);
    applicationContext = resourceLoader;
  }
}
