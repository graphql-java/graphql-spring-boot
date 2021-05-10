package graphql.kickstart.spring;

import org.springframework.web.server.ServerWebExchange;

public interface GraphQLSpringContextBuilder {

  GraphQLSpringContext build(ServerWebExchange serverWebExchange);
}
