package graphql.kickstart.playground.boot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.test.context.ContextConfiguration;

@EnableAutoConfiguration(exclude = WebFluxAutoConfiguration.class)
@EnableWebSecurity
@ContextConfiguration(
    classes = {
      PlaygroundAutoConfiguration.class,
      ObjectMapper.class,
      ThymeleafAutoConfiguration.class
    })
class PlaygroundTestConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    http.authorizeRequests().anyRequest().permitAll();
  }
}
