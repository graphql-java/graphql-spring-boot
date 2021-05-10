package graphql.servlet.examples.dataloader.requestscope;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import graphql.kickstart.execution.context.DefaultGraphQLContext;
import graphql.kickstart.execution.context.GraphQLContext;
import graphql.kickstart.servlet.context.DefaultGraphQLServletContext;
import graphql.kickstart.servlet.context.DefaultGraphQLWebSocketContext;
import graphql.kickstart.servlet.context.GraphQLServletContextBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.springframework.stereotype.Component;

@Component
public class CustomGraphQLContextBuilder implements GraphQLServletContextBuilder {

  private final CustomerRepository customerRepository;

  public CustomGraphQLContextBuilder(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  @Override
  public GraphQLContext build(HttpServletRequest req, HttpServletResponse response) {
    return DefaultGraphQLServletContext.createServletContext(buildDataLoaderRegistry(), null)
        .with(req)
        .with(response)
        .build();
  }

  @Override
  public GraphQLContext build() {
    return new DefaultGraphQLContext(buildDataLoaderRegistry(), null);
  }

  @Override
  public GraphQLContext build(Session session, HandshakeRequest request) {
    return DefaultGraphQLWebSocketContext.createWebSocketContext(buildDataLoaderRegistry(), null)
        .with(session)
        .with(request)
        .build();
  }

  private DataLoaderRegistry buildDataLoaderRegistry() {
    DataLoaderRegistry dataLoaderRegistry = new DataLoaderRegistry();
    DataLoader<Integer, String> customerLoader =
        new DataLoader<>(
            customerIds -> supplyAsync(() -> customerRepository.getUserNamesForIds(customerIds)));
    dataLoaderRegistry.register("customerDataLoader", customerLoader);
    return dataLoaderRegistry;
  }
}
