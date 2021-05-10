package graphql.kickstart.tools.boot;

import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Component;

/** @author Andrew Potter */
@Component
public class Query implements GraphQLQueryResolver {

  public String echo(String string) {
    return string;
  }
}
