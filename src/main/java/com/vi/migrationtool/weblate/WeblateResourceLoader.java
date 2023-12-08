package com.vi.migrationtool.weblate;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Service for mail templates */
@Service
public class WeblateResourceLoader {

  private static final String WEBLATE_DIR = "/weblate/";
  private static final String RESOURCE_EXTENSION = ".json";

  @Value("${weblate.config.use.custom.resources.path}")
  private boolean useCustomResourcesPath;

  @Value("${weblate.config.custom.resources.path}")
  private String customResourcePath;

  /**
   * Load template file from resources. InputStream is needed as file is located in jar.
   *
   * @param resourceName the name of the template
   * @return the content of the template description file
   */
  public String readResourceContentByName(String resourceName) {
    try {
      var inputStream =
          useCustomResourcesPath
              ? buildStreamForExternalPath(resourceName)
              : WeblateResourceLoader.class.getResourceAsStream(getResourceFilename(resourceName));
      assert inputStream != null;
      final List<String> fileLines =
          IOUtils.readLines(inputStream, StandardCharsets.UTF_8.displayName());
      return String.join("", fileLines);
    } catch (Exception ex) {
      throw new IllegalStateException(
          String.format(
              "Json file with resource description could not be loaded, resource name: %s",
              resourceName),
          ex);
    }
  }

  private FileInputStream buildStreamForExternalPath(String templateName)
      throws FileNotFoundException {
    return new FileInputStream(
        customResourcePath + templateName.toLowerCase() + RESOURCE_EXTENSION);
  }

  /**
   * Get the filename and filepath for the template description file
   *
   * @param resourceName the template name
   * @return the filename with filepath of the template description file
   */
  private String getResourceFilename(String resourceName) {
    return WEBLATE_DIR + resourceName.toLowerCase() + RESOURCE_EXTENSION;
  }
}
