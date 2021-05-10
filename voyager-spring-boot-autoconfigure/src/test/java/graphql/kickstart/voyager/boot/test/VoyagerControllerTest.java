package graphql.kickstart.voyager.boot.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import graphql.kickstart.voyager.boot.VoyagerAutoConfiguration;
import graphql.kickstart.voyager.boot.VoyagerController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/** @author Andrew Potter */
class VoyagerControllerTest extends AbstractAutoConfigurationTest {

  public VoyagerControllerTest() {
    super(AnnotationConfigWebApplicationContext.class, VoyagerAutoConfiguration.class);
  }

  @Test
  void graphiqlLoads() {
    load(EnabledConfiguration.class);

    assertThat(this.getContext().getBean(VoyagerController.class)).isNotNull();
  }

  @Test
  void graphiqlDoesNotLoad() {
    load(DisabledConfiguration.class);

    AbstractApplicationContext context = getContext();
    assertThatExceptionOfType(NoSuchBeanDefinitionException.class)
        .isThrownBy(() -> context.getBean(VoyagerController.class));
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
