package com.oembedler.moon.voyager.boot;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * @author Guilherme Blanco
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(DispatcherServlet.class)
public class VoyagerAutoConfiguration
{
    @Bean
    @ConditionalOnProperty(value = "voyager.enabled", havingValue = "true", matchIfMissing = true)
    VoyagerController voyagerController() {
        return new VoyagerController();
    }
}
