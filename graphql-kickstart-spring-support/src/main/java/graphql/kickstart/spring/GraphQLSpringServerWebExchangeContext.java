package graphql.kickstart.spring;

import graphql.kickstart.execution.context.DefaultGraphQLContext;
import java.util.Objects;
import org.dataloader.DataLoaderRegistry;
import org.springframework.web.server.ServerWebExchange;

public class GraphQLSpringServerWebExchangeContext extends DefaultGraphQLContext
    implements GraphQLSpringContext {

  private final ServerWebExchange serverWebExchange;

  public GraphQLSpringServerWebExchangeContext(ServerWebExchange serverWebExchange) {
    this(new DataLoaderRegistry(), serverWebExchange);
  }

  public GraphQLSpringServerWebExchangeContext(
      DataLoaderRegistry dataLoaderRegistry, ServerWebExchange serverWebExchange) {
    super(dataLoaderRegistry, null);
    this.serverWebExchange =
        Objects.requireNonNull(serverWebExchange, "Server web exchange cannot be null");
  }

  @Override
  public ServerWebExchange getServerWebExchange() {
    return serverWebExchange;
  }
}
