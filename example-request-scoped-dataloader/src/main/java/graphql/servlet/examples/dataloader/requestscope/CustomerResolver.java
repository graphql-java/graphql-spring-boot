package graphql.servlet.examples.dataloader.requestscope;

import graphql.kickstart.execution.context.GraphQLContext;
import graphql.kickstart.tools.GraphQLResolver;
import graphql.schema.DataFetchingEnvironment;
import java.util.concurrent.CompletableFuture;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.springframework.stereotype.Component;

@Component
public class CustomerResolver implements GraphQLResolver<Customer> {

  public CompletableFuture<String> getName(Customer customer, DataFetchingEnvironment dfe) {
    DataLoaderRegistry registry = ((GraphQLContext) dfe.getContext()).getDataLoaderRegistry();
    DataLoader<Integer, String> customerLoader = registry.getDataLoader("customerDataLoader");
    if (customerLoader != null) {
      return customerLoader.load(customer.getCustomerId());
    }
    throw new IllegalStateException("No customer data loader found");
  }
}
