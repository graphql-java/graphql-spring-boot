package graphql.kickstart.voyager.boot.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import graphql.kickstart.voyager.boot.ReactiveVoyagerAutoConfiguration;
import graphql.kickstart.voyager.boot.ReactiveVoyagerController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/** @author Max Günther */
class ReactiveVoyagerControllerTest extends AbstractAutoConfigurationTest {

  public ReactiveVoyagerControllerTest() {
    super(
        AnnotationConfigReactiveWebApplicationContext.class,
        ReactiveVoyagerAutoConfiguration.class);
  }

  @Test
  void voyagerLoads() {
    load(EnabledConfiguration.class);

    assertThat(this.getContext().getBean(ReactiveVoyagerController.class)).isNotNull();
  }

  @Test
  void voyagerDoesNotLoad() {
    load(DisabledConfiguration.class);

    AbstractApplicationContext context = getContext();
    assertThatExceptionOfType(NoSuchBeanDefinitionException.class)
        .isThrownBy(() -> context.getBean(ReactiveVoyagerController.class));
  }

  @Configuration
  @PropertySource("classpath:enabled-config.properties")
  static class EnabledConfiguration {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
      return new PropertySourcesPlaceholderConfigurer();
    }
  }

  @Configuration
  @PropertySource("classpath:disabled-config.properties")
  static class DisabledConfiguration {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
      return new PropertySourcesPlaceholderConfigurer();
    }
  }
}
