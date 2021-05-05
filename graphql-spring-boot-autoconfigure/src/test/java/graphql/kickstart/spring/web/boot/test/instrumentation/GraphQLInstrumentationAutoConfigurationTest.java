package graphql.kickstart.spring.web.boot.test.instrumentation;

import static graphql.Scalars.GraphQLString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import graphql.analysis.MaxQueryComplexityInstrumentation;
import graphql.analysis.MaxQueryDepthInstrumentation;
import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.instrumentation.tracing.TracingInstrumentation;
import graphql.kickstart.spring.web.boot.GraphQLInstrumentationAutoConfiguration;
import graphql.kickstart.spring.web.boot.metrics.MetricsInstrumentation;
import graphql.kickstart.spring.web.boot.metrics.TracingNoResolversInstrumentation;
import graphql.kickstart.spring.web.boot.test.AbstractAutoConfigurationTest;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/** @author Marcel Overdijk */
class GraphQLInstrumentationAutoConfigurationTest extends AbstractAutoConfigurationTest {

  public GraphQLInstrumentationAutoConfigurationTest() {
    super(
        AnnotationConfigWebApplicationContext.class, GraphQLInstrumentationAutoConfiguration.class);
  }

  @Test
  void noDefaultInstrumentations() {
    load(DefaultConfiguration.class);

    AbstractApplicationContext context = getContext();
    assertThatExceptionOfType(NoSuchBeanDefinitionException.class)
        .isThrownBy(() -> context.getBean(Instrumentation.class));
  }

  @Test
  void tracingInstrumentationEnabled() {
    load(DefaultConfiguration.class, "graphql.servlet.tracing-enabled=true");

    assertThat(this.getContext().getBean(TracingInstrumentation.class)).isNotNull();
  }

  @Test
  void maxQueryComplexityEnabled() {
    load(DefaultConfiguration.class, "graphql.servlet.maxQueryComplexity=10");

    assertThat(this.getContext().getBean(MaxQueryComplexityInstrumentation.class)).isNotNull();
  }

  @Test
  void maxQueryDepthEnabled() {
    load(DefaultConfiguration.class, "graphql.servlet.maxQueryDepth=10");

    assertThat(this.getContext().getBean(MaxQueryDepthInstrumentation.class)).isNotNull();
  }

  @Test
  void actuatorMetricsEnabledAndTracingEnabled() {
    load(
        DefaultConfiguration.class,
        "graphql.servlet.tracing-enabled=true",
        "graphql.servlet.actuator-metrics=true");

    AbstractApplicationContext context = getContext();
    assertThat(this.getContext().getBean(MetricsInstrumentation.class)).isNotNull();
    assertThatExceptionOfType(NoSuchBeanDefinitionException.class)
        .isThrownBy(() -> context.getBean(TracingNoResolversInstrumentation.class));
  }

  @Test
  void tracingInstrumentationDisabledAndMetricsEnabled() {
    load(
        DefaultConfiguration.class,
        "graphql.servlet.tracing-enabled=false",
        "graphql.servlet.actuator-metrics=true");

    assertThat(this.getContext().getBean(MetricsInstrumentation.class)).isNotNull();
    assertThat(this.getContext().getBean(TracingNoResolversInstrumentation.class)).isNotNull();
  }

  @Test
  void tracingMetricsWithTracingDisabled() {
    load(
        DefaultConfiguration.class,
        "graphql.servlet.tracing-enabled=metrics-only",
        "graphql.servlet.actuator-metrics=true");

    assertThat(this.getContext().getBean("metricsInstrumentation")).isNotNull();
    assertThat(this.getContext().getBean("tracingInstrumentation")).isNotNull();
  }

  @Test
  void actuatorMetricsEnabled() {
    load(DefaultConfiguration.class, "graphql.servlet.actuator-metrics=true");

    assertThat(this.getContext().getBean(TracingNoResolversInstrumentation.class)).isNotNull();
    assertThat(this.getContext().getBean(MetricsInstrumentation.class)).isNotNull();
  }

  @Test
  void tracingInstrumentationEnabledAndMetricsDisabled() {
    load(
        DefaultConfiguration.class,
        "graphql.servlet.tracing-enabled=true",
        "graphql.servlet.actuator-metrics=false");

    AbstractApplicationContext context = getContext();
    assertThat(this.getContext().getBean(TracingInstrumentation.class)).isNotNull();
    assertThatExceptionOfType(NoSuchBeanDefinitionException.class)
        .isThrownBy(() -> context.getBean(MetricsInstrumentation.class));
  }

  @Test
  void tracingInstrumentationDisabledAndMetricsDisabled() {
    load(
        DefaultConfiguration.class,
        "graphql.servlet.tracing-enabled=false",
        "graphql.servlet.actuator-metrics=false");

    AbstractApplicationContext context = getContext();
    assertThatExceptionOfType(NoSuchBeanDefinitionException.class)
        .isThrownBy(() -> context.getBean(MetricsInstrumentation.class));
    assertThatExceptionOfType(NoSuchBeanDefinitionException.class)
        .isThrownBy(() -> context.getBean(TracingNoResolversInstrumentation.class));
    assertThatExceptionOfType(NoSuchBeanDefinitionException.class)
        .isThrownBy(() -> context.getBean(TracingInstrumentation.class));
  }

  @Test
  void actuatorMetricsDisabled() {
    load(DefaultConfiguration.class, "graphql.servlet.actuator-metrics=false");

    AbstractApplicationContext context = getContext();
    assertThatExceptionOfType(NoSuchBeanDefinitionException.class)
        .isThrownBy(() -> context.getBean(MetricsInstrumentation.class));
  }

  @Configuration
  static class DefaultConfiguration {

    @Bean
    GraphQLSchema schema() {
      return GraphQLSchema.newSchema()
          .query(
              GraphQLObjectType.newObject()
                  .name("Query")
                  .field(
                      GraphQLFieldDefinition.newFieldDefinition()
                          .name("echo")
                          .type(GraphQLString)
                          .build())
                  .build())
          .build();
    }

    @Bean
    MeterRegistry meterRegistry() {
      return new SimpleMeterRegistry();
    }
  }
}
