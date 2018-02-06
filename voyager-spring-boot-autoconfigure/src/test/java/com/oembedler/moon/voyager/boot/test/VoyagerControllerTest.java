package com.oembedler.moon.voyager.boot.test;

import com.oembedler.moon.voyager.boot.VoyagerAutoConfiguration;
import com.oembedler.moon.voyager.boot.VoyagerController;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 * @author Andrew Potter
 */
public class VoyagerControllerTest extends AbstractAutoConfigurationTest {

    public VoyagerControllerTest() {
        super(AnnotationConfigWebApplicationContext.class, VoyagerAutoConfiguration.class);
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

    @Test
    public void graphiqlLoads() {
        load(EnabledConfiguration.class);

        Assert.assertNotNull(this.getContext().getBean(VoyagerController.class));
    }

    @Test(expected = NoSuchBeanDefinitionException.class)
    public void graphiqlDoesNotLoad() {
        load(DisabledConfiguration.class);

        this.getContext().getBean(VoyagerController.class);
    }
}