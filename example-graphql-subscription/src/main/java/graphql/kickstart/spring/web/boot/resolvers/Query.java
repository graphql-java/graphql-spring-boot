package graphql.kickstart.spring.web.boot.resolvers;

import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Component;

@Component
class Query implements GraphQLQueryResolver {

  private static final String HELLO = "Hello world";

  String hello() {
    return HELLO;
  }
}
