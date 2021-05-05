package com.graphql.spring.boot.test;

import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName(
    "Testing auto-configuration of the GraphQLTestTemplate bean / default servlet endpoint.")
class GraphQLTestTemplateAutoConfigurationDefaultConfigTest
    extends GraphQLTestAutoConfigurationTestBase {

  @Test
  @DisplayName("GraphQLTestTemplate bean should work properly.")
  void shouldProvideGraphQLTestTemplateBean() throws IOException {
    assertThatTestTemplateAutoConfigurationWorksCorrectly();
  }
}
