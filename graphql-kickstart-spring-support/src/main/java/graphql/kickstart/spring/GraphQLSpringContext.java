package graphql.kickstart.spring;

import graphql.kickstart.execution.context.GraphQLContext;
import org.springframework.web.server.ServerWebExchange;

public interface GraphQLSpringContext extends GraphQLContext {

  ServerWebExchange getServerWebExchange();
}
