package graphql.kickstart.spring.web.boot;

import graphql.analysis.MaxQueryComplexityInstrumentation;
import graphql.analysis.MaxQueryDepthInstrumentation;
import graphql.execution.instrumentation.tracing.TracingInstrumentation;
import graphql.kickstart.servlet.GraphQLWebsocketServlet;
import graphql.kickstart.spring.web.boot.metrics.MetricsInstrumentation;
import graphql.kickstart.spring.web.boot.metrics.TracingNoResolversInstrumentation;
import graphql.kickstart.spring.web.boot.metrics.WebsocketMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.export.simple.SimpleMetricsExportAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** @author Marcel Overdijk */
@Configuration
@RequiredArgsConstructor
@ConditionalOnClass(MetricsAutoConfiguration.class)
@AutoConfigureAfter({
  MetricsAutoConfiguration.class,
  SimpleMetricsExportAutoConfiguration.class,
  GraphQLWebsocketAutoConfiguration.class
})
@EnableConfigurationProperties(GraphQLServletProperties.class)
@ConditionalOnProperty(
    value = "graphql.servlet.enabled",
    havingValue = "true",
    matchIfMissing = true)
public class GraphQLInstrumentationAutoConfiguration {

  private final GraphQLServletProperties graphqlServletProperties;

  @Bean
  @ConditionalOnMissingBean({TracingInstrumentation.class, MetricsInstrumentation.class})
  @ConditionalOnExpression(
      "'${graphql.servlet.tracing-enabled:false}' == 'metrics-only' "
          + "|| '${graphql.servlet.tracing-enabled:false}' == 'true'")
  public TracingInstrumentation tracingInstrumentation() {
    return new TracingInstrumentation();
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnExpression(
      "${graphql.servlet.actuator-metrics:false} "
          + "&& '${graphql.servlet.tracing-enabled:false}' == 'false'")
  public TracingNoResolversInstrumentation tracingNoResolversInstrumentation() {
    return new TracingNoResolversInstrumentation();
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(value = "graphql.servlet.max-query-complexity")
  public MaxQueryComplexityInstrumentation maxQueryComplexityInstrumentation() {
    return new MaxQueryComplexityInstrumentation(graphqlServletProperties.getMaxQueryComplexity());
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(value = "graphql.servlet.max-query-depth")
  public MaxQueryDepthInstrumentation maxQueryDepthInstrumentation() {
    return new MaxQueryDepthInstrumentation(graphqlServletProperties.getMaxQueryDepth());
  }

  @Bean
  @ConditionalOnProperty(value = "graphql.servlet.actuator-metrics", havingValue = "true")
  @ConditionalOnBean({MeterRegistry.class, TracingInstrumentation.class})
  @ConditionalOnMissingBean
  public MetricsInstrumentation metricsInstrumentation(MeterRegistry meterRegistry) {
    return new MetricsInstrumentation(
        meterRegistry,
        Boolean.TRUE.toString().equalsIgnoreCase(graphqlServletProperties.getTracingEnabled()));
  }

  @Bean
  @ConditionalOnProperty(value = "graphql.servlet.actuator-metrics", havingValue = "true")
  @ConditionalOnBean({MeterRegistry.class, GraphQLWebsocketServlet.class})
  @ConditionalOnMissingBean
  public WebsocketMetrics websocketMetrics(
      MeterRegistry meterRegistry, GraphQLWebsocketServlet websocketServlet) {
    return new WebsocketMetrics(meterRegistry, websocketServlet);
  }
}
