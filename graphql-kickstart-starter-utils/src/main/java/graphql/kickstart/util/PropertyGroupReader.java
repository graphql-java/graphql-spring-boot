package graphql.kickstart.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

public class PropertyGroupReader {

  private final Environment environment;
  private final String prefix;
  private Properties props;

  public PropertyGroupReader(Environment environment, String prefix) {
    this.environment = Objects.requireNonNull(environment);
    this.prefix = Optional.ofNullable(prefix).orElse("");
  }

  public Properties load() {
    if (props == null) {
      props = new Properties();
      loadProps();
    }
    return props;
  }

  private void loadProps() {
    streamOfPropertySources()
        .forEach(
            propertySource ->
                Arrays.stream(propertySource.getPropertyNames())
                    .filter(this::isWanted)
                    .forEach(key -> add(propertySource, key)));
  }

  @SuppressWarnings("unchecked")
  private Stream<EnumerablePropertySource<Object>> streamOfPropertySources() {
    if (environment instanceof ConfigurableEnvironment) {
      Iterator<PropertySource<?>> iterator =
          ((ConfigurableEnvironment) environment).getPropertySources().iterator();
      Iterable<PropertySource<?>> iterable = () -> iterator;
      return StreamSupport.stream(iterable.spliterator(), false)
          .filter(EnumerablePropertySource.class::isInstance)
          .map(EnumerablePropertySource.class::cast);
    }
    return Stream.empty();
  }

  private String withoutPrefix(String key) {
    return key.replace(prefix, "");
  }

  private boolean isWanted(String key) {
    return key.startsWith(prefix);
  }

  private void add(EnumerablePropertySource<Object> propertySource, String key) {
    props.put(withoutPrefix(key), propertySource.getProperty(key));
  }
}
