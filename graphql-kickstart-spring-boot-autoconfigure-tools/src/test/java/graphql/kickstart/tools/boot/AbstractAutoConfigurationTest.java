package graphql.kickstart.tools.boot;

import static org.mockito.Mockito.mock;

import javax.servlet.ServletContext;
import javax.websocket.server.ServerContainer;
import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.AnnotationConfigRegistry;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/** @author Andrew Potter */
public abstract class AbstractAutoConfigurationTest {

  private final Class<? extends AbstractApplicationContext> contextClass;
  private final Class<?> autoConfiguration;

  private AbstractApplicationContext context;

  protected AbstractAutoConfigurationTest(Class<?> autoConfiguration) {
    this(AnnotationConfigApplicationContext.class, autoConfiguration);
  }

  protected AbstractAutoConfigurationTest(
      Class<? extends AbstractApplicationContext> contextClass, Class<?> autoConfiguration) {
    assert AnnotationConfigRegistry.class.isAssignableFrom(contextClass);
    this.contextClass = contextClass;
    this.autoConfiguration = autoConfiguration;
  }

  @AfterEach
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
