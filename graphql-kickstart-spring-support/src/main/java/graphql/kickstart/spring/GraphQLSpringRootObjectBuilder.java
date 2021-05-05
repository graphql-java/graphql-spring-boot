package graphql.kickstart.spring;

import org.springframework.web.server.ServerWebExchange;

public interface GraphQLSpringRootObjectBuilder {

  Object build(ServerWebExchange serverWebExchange);
}
