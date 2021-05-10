package graphql.servlet.examples.dataloader.requestscope;

import graphql.kickstart.tools.GraphQLQueryResolver;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class WalmartQueryResolver implements GraphQLQueryResolver {

  private final Map<Integer, List<Integer>> walmartData;

  public WalmartQueryResolver() {
    walmartData = new HashMap<>();
    walmartData.put(4177, Arrays.asList(101, 102, 103, 104));
  }

  public List<Customer> walmartCustomers(int storeNumber) {
    return walmartData.get(storeNumber).parallelStream()
        .map(Customer::new)
        .collect(Collectors.toList());
  }
}
