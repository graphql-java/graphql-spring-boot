package graphql.kickstart.autoconfigure.web.servlet;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

@Data
@ConfigurationProperties(prefix = "graphql.servlet.async")
public class AsyncServletProperties {

  public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);

  private boolean enabled = true;
  /**
   * Asynchronous execution timeout. If a duration suffix is not specified, millisecond will be used.
   */
  @DurationUnit(ChronoUnit.MILLIS)
  private Duration timeout = DEFAULT_TIMEOUT;
  private boolean delegateSecurityContext = true;
  private Threads threads = new Threads();

  @Data
  static class Threads {
    private int min = 10;
    private int max = 200;
    private String namePrefix = "graphql-exec-";
  }
}
