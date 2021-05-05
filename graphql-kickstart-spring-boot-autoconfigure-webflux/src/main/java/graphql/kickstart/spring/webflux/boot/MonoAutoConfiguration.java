package graphql.kickstart.spring.webflux.boot;

import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.REACTIVE;

import graphql.kickstart.tools.SchemaParser;
import graphql.kickstart.tools.SchemaParserOptions.GenericWrapper;
import graphql.kickstart.tools.boot.GraphQLJavaToolsAutoConfiguration;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
@ConditionalOnClass(SchemaParser.class)
@ConditionalOnWebApplication(type = REACTIVE)
@AutoConfigureBefore(GraphQLJavaToolsAutoConfiguration.class)
public class MonoAutoConfiguration {

  @Bean
  GenericWrapper monoWrapper(@Autowired(required = false) List<GenericWrapper> genericWrappers) {
    if (notWrapsMono(genericWrappers)) {
      return GenericWrapper.withTransformer(Mono.class, 0, Mono::toFuture, t -> t);
    }
    return null;
  }

  private boolean notWrapsMono(List<GenericWrapper> genericWrappers) {
    return genericWrappers == null
        || genericWrappers.stream().noneMatch(it -> it.getType().isAssignableFrom(Mono.class));
  }
}
