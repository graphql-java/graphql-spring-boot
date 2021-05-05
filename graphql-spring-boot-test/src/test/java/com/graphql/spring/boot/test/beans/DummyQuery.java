package com.graphql.spring.boot.test.beans;

import graphql.kickstart.servlet.context.GraphQLServletContext;
import graphql.kickstart.tools.GraphQLQueryResolver;
import graphql.schema.DataFetchingEnvironment;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class DummyQuery implements GraphQLQueryResolver {

  public boolean dummy() {
    return true;
  }

  public String otherQuery() {
    return "TEST";
  }

  public FooBar fooBar(String foo, String bar) {
    return FooBar.builder()
        .bar(Optional.ofNullable(bar).orElse("BAR"))
        .foo(Optional.ofNullable(foo).orElse("FOO"))
        .build();
  }

  public String queryWithVariables(final String input) {
    return input;
  }

  public String queryWithHeader(
      final String headerName, final DataFetchingEnvironment dataFetchingEnvironment) {
    return ((GraphQLServletContext) dataFetchingEnvironment.getContext())
        .getHttpServletRequest()
        .getHeader(headerName);
  }
}
