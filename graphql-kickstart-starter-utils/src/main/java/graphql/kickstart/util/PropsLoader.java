package graphql.kickstart.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Properties;
import lombok.SneakyThrows;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

public class PropsLoader {

  private final Environment environment;
  private final String resourcesPrefix;
  private final String valuesPrefix;

  public PropsLoader(Environment environment, String resourcesPrefix, String valuesPrefix) {
    this.environment = environment;
    this.resourcesPrefix = resourcesPrefix;
    this.valuesPrefix = valuesPrefix;
  }

  public String load() throws IOException {
    PropertyGroupReader reader = new PropertyGroupReader(environment, valuesPrefix);
    Properties props = reader.load();

    ObjectMapper objectMapper = new ObjectMapper();
    loadPropFromResource("defaultQuery").ifPresent(it -> props.put("defaultQuery", it));
    loadPropFromResource("query").ifPresent(it -> props.put("query", it));
    loadPropFromResource("variables").ifPresent(it -> props.put("variables", it));
    return objectMapper.writeValueAsString(props);
  }

  private Optional<String> loadPropFromResource(String prop) {
    String property = resourcesPrefix + prop;
    return Optional.ofNullable(environment.getProperty(property))
        .map(ClassPathResource::new)
        .map(this::loadResource);
  }

  @SneakyThrows
  private String loadResource(Resource resource) {
    try (InputStream inputStream = resource.getInputStream()) {
      return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
    }
  }
}
