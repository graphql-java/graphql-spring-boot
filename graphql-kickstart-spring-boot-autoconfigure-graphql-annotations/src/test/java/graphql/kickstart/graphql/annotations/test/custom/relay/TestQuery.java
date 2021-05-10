package graphql.kickstart.graphql.annotations.test.custom.relay;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.connection.AbstractPaginatedData;
import graphql.annotations.connection.GraphQLConnection;
import graphql.annotations.connection.PaginatedData;
import graphql.kickstart.graphql.annotations.GraphQLQueryResolver;
import java.util.Collections;

@GraphQLQueryResolver
public class TestQuery {

  @GraphQLField
  @GraphQLConnection
  public static PaginatedData<TestModel> somePaginatedValue() {
    return new AbstractPaginatedData<TestModel>(
        false, false, Collections.singletonList(new TestModel("some value"))) {
      @Override
      public String getCursor(final TestModel entity) {
        return "test cursor";
      }
    };
  }
}
