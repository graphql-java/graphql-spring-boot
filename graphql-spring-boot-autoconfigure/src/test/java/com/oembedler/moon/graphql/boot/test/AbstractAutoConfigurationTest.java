package com.oembedler.moon.graphql.boot.test;

import org.junit.After;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.AnnotationConfigRegistry;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.servlet.ServletContext;
import javax.websocket.server.ServerContainer;

import static org.mockito.Mockito.mock;

/**
 * @author Andrew Potter
 */
public abstract class AbstractAutoConfigurationTest {

    private final Class<? extends AbstractApplicationContext> contextClass;
    private final Class<?> autoConfiguration;

    private AbstractApplicationContext context;

    protected AbstractAutoConfigurationTest(Class<?> autoConfiguration) {
        this(AnnotationConfigApplicationContext.class, autoConfiguration);
    }

    protected AbstractAutoConfigurationTest(Class<? extends AbstractApplicationContext> contextClass, Class<?> autoConfiguration) {
        assert AnnotationConfigRegistry.class.isAssignableFrom(contextClass);
        this.contextClass = contextClass;
        this.autoConfiguration = autoConfiguration;
    }

    @After
    public void tearDown() {
        if (this.context != null) {
            this.context.close();
            this.context = null;
        }
    }

    protected void load(Class<?> config, String... environment) {
        try {
            this.context = contextClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Failed to instantiate testing context", e);
        }

        if (environment != null && environment.length > 0) {
            TestPropertyValues.of(environment).applyTo(context);
        }

        getRegistry().register(config);
        getRegistry().register(autoConfiguration);
        getRegistry().register(JacksonAutoConfiguration.class);

        loadServletContext();
        getContext().refresh();
        getContext().publishEvent(new ApplicationReadyEvent(new SpringApplication(), new String[0], getContext()));
    }

    private void loadServletContext() {
        if (context instanceof AnnotationConfigWebApplicationContext) {
            ServerContainer serverContainer = mock(ServerContainer.class);
            ServletContext servletContext = new MockServletContext();
            servletContext.setAttribute("javax.websocket.server.ServerContainer", serverContainer);
            ((AnnotationConfigWebApplicationContext) context).setServletContext(servletContext);
        }
    }

    public AnnotationConfigRegistry getRegistry() {
        return (AnnotationConfigRegistry) context;
    }

    public AbstractApplicationContext getContext() {
        return context;
    }
}
