package com.graphql.spring.boot.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphql.spring.boot.test.beans.FooBar;
import graphql.GraphQLError;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GraphQLTestTemplateIntegrationTest {

  private static final String SIMPLE_TEST_QUERY = "simple-test-query.graphql";
  private static final String SIMPLE_TEST_QUERY_WITH_FRAGMENTS =
      "simple-test-query-with-fragments.graphql";
  private static final String TEST_FRAGMENT_FILE = "foo-bar-fragment.graphql";
  private static final String QUERY_WITH_VARIABLES = "query-with-variables.graphql";
  private static final String COMPLEX_TEST_QUERY = "complex-query.graphql";
  private static final String MULTIPLE_QUERIES = "multiple-queries.graphql";
  private static final String INPUT_STRING_VALUE = "input-value";
  private static final String INPUT_STRING_NAME = "input";
  private static final String INPUT_HEADER_NAME = "headerName";
  private static final String TEST_HEADER_NAME = "x-test";
  private static final String TEST_HEADER_VALUE = String.valueOf(UUID.randomUUID());
  private static final String FOO = "FOO";
  private static final String BAR = "BAR";
  private static final String TEST = "TEST";
  private static final String DATA_FIELD_FOO_BAR = "$.data.fooBar";
  private static final String DATA_FIELD_QUERY_WITH_VARIABLES = "$.data.queryWithVariables";
  private static final String DATA_FIELD_OTHER_QUERY = "$.data.otherQuery";
  private static final String DATA_FIELD_QUERY_WITH_HEADER = "$.data.queryWithHeader";
  private static final String DATA_FIELD_DUMMY = "$.data.dummy";
  private static final String OPERATION_NAME_WITH_VARIABLES = "withVariable";
  private static final String OPERATION_NAME_TEST_QUERY_1 = "testQuery1";
  private static final String OPERATION_NAME_TEST_QUERY_2 = "testQuery2";
  private static final String OPERATION_NAME_COMPLEX_QUERY = "complexQuery";
  private static final String GRAPHQL_ENDPOINT = "/graphql";

  @Autowired private ResourceLoader resourceLoader;

  @Autowired private TestRestTemplate testRestTemplate;

  @Autowired private ObjectMapper objectMapper;

  private GraphQLTestTemplate graphQLTestTemplate;

  @BeforeEach
  void setUp() {
    graphQLTestTemplate =
        new GraphQLTestTemplate(resourceLoader, testRestTemplate, GRAPHQL_ENDPOINT, objectMapper);
  }

  @Test
  @DisplayName("Test postForResource with only the GraphQL resource provided.")
  void testPostForResource() throws IOException {
    graphQLTestTemplate
        .postForResource(SIMPLE_TEST_QUERY)
        .assertThatNoErrorsArePresent()
        .assertThatField(DATA_FIELD_OTHER_QUERY)
        .asString()
        .isEqualTo(TEST);
  }

  @Test
  @DisplayName("Test postForResource with fragments.")
  void testPostForResourceWithFragments() throws IOException {
    graphQLTestTemplate
        .postForResource(
            SIMPLE_TEST_QUERY_WITH_FRAGMENTS, Collections.singletonList(TEST_FRAGMENT_FILE))
        .assertThatNoErrorsArePresent()
        .assertThatField(DATA_FIELD_FOO_BAR)
        .as(FooBar.class)
        .usingRecursiveComparison()
        .ignoringAllOverriddenEquals()
        .isEqualTo(FooBar.builder().foo(FOO).bar(BAR).build());
  }

  @Test
  @DisplayName("Test perform with variables.")
  void testPerformWithVariables() throws IOException {
    // GIVEN
    final ObjectNode variables = objectMapper.createObjectNode();
    variables.put(INPUT_STRING_NAME, INPUT_STRING_VALUE);
    // WHEN - THEN
    graphQLTestTemplate
        .perform(QUERY_WITH_VARIABLES, variables)
        .assertThatNoErrorsArePresent()
        .assertThatField(DATA_FIELD_QUERY_WITH_VARIABLES)
        .asString()
        .isEqualTo(INPUT_STRING_VALUE);
  }

  @Test
  @DisplayName("Test perform with variables and operation name")
  void testPerformWithOperationAndVariables() throws IOException {
    // GIVEN
    final ObjectNode variables = objectMapper.createObjectNode();
    variables.put(INPUT_STRING_NAME, INPUT_STRING_VALUE);
    // WHEN - THEN
    graphQLTestTemplate
        .perform(MULTIPLE_QUERIES, OPERATION_NAME_WITH_VARIABLES, variables)
        .assertThatNoErrorsArePresent()
        .assertThatField(DATA_FIELD_QUERY_WITH_VARIABLES)
        .asString()
        .isEqualTo(INPUT_STRING_VALUE);
  }

  @Test
  @DisplayName("Test perform with variables and fragments")
  void testPerformWithVariablesAndFragments() throws IOException {
    // GIVEN
    final FooBar expected =
        new FooBar(String.valueOf(UUID.randomUUID()), String.valueOf(UUID.randomUUID()));
    final ObjectNode variables = objectMapper.valueToTree(expected);
    // WHEN - THEN
    graphQLTestTemplate
        .perform(
            SIMPLE_TEST_QUERY_WITH_FRAGMENTS,
            variables,
            Collections.singletonList(TEST_FRAGMENT_FILE))
        .assertThatNoErrorsArePresent()
        .assertThatField(DATA_FIELD_FOO_BAR)
        .as(FooBar.class)
        .usingRecursiveComparison()
        .ignoringAllOverriddenEquals()
        .isEqualTo(expected);
  }

  @Test
  @DisplayName("Test perform with operation name.")
  void testPerformWithOperationName() throws IOException {
    // WHEN - THEN
    graphQLTestTemplate
        .perform(MULTIPLE_QUERIES, OPERATION_NAME_TEST_QUERY_1)
        .assertThatNoErrorsArePresent()
        .assertThatField(DATA_FIELD_DUMMY)
        .asBoolean()
        .isTrue();
    graphQLTestTemplate
        .perform(MULTIPLE_QUERIES, OPERATION_NAME_TEST_QUERY_2)
        .assertThatNoErrorsArePresent()
        .assertThatField(DATA_FIELD_OTHER_QUERY)
        .asString()
        .isEqualTo(TEST);
  }

  @Test
  @DisplayName("Test perform with GraphQL errors.")
  void testPerformWithGraphQLError() throws IOException {
    graphQLTestTemplate
        .postForResource(SIMPLE_TEST_QUERY, Collections.singletonList(TEST_FRAGMENT_FILE))
        .assertThatDataField()
        .isNotPresentOrNull()
        .and()
        .assertThatNumberOfErrors()
        .isOne()
        .and()
        .assertThatListOfErrors()
        .extracting(GraphQLError::getMessage)
        .allMatch(message -> message.contains("UnusedFragment"));
  }

  @Test
  @DisplayName("Test perform with all possible inputs.")
  void testPerformWithAllInputs() throws IOException {
    // GIVEN
    final ObjectNode variables = objectMapper.createObjectNode();
    variables.put(INPUT_STRING_NAME, INPUT_STRING_VALUE);
    variables.put(INPUT_HEADER_NAME, TEST_HEADER_NAME);
    final HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add(TEST_HEADER_NAME, TEST_HEADER_VALUE);
    // WHEN - THEN
    graphQLTestTemplate
        .withHeaders(httpHeaders)
        .perform(
            COMPLEX_TEST_QUERY,
            OPERATION_NAME_COMPLEX_QUERY,
            variables,
            Collections.singletonList(TEST_FRAGMENT_FILE))
        .assertThatNoErrorsArePresent()
        .assertThatField(DATA_FIELD_QUERY_WITH_HEADER)
        .asString()
        .isEqualTo(TEST_HEADER_VALUE)
        .and()
        .assertThatField(DATA_FIELD_QUERY_WITH_VARIABLES)
        .asString()
        .isEqualTo(INPUT_STRING_VALUE)
        .and()
        .assertThatField(DATA_FIELD_FOO_BAR)
        .as(FooBar.class)
        .isEqualTo(new FooBar(FOO, BAR));
  }

  @Test
  @DisplayName("Test post with custom payload.")
  void testPost() {
    // GIVEN
    final HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add(TEST_HEADER_NAME, TEST_HEADER_VALUE);
    final String payload =
        "{\"query\":"
            + "\"query ($input: String!, $headerName: String!) "
            + "{ queryWithVariables(input: $input) queryWithHeader(headerName: $headerName) }\", "
            + "\"variables\": {\"input\": \"input-value\", \"headerName\": \"x-test\"}}";
    // WHEN - THEN
    graphQLTestTemplate
        .withHeaders(httpHeaders)
        .post(payload)
        .assertThatNoErrorsArePresent()
        .assertThatField(DATA_FIELD_QUERY_WITH_VARIABLES)
        .asString()
        .isEqualTo(INPUT_STRING_VALUE)
        .and()
        .assertThatField(DATA_FIELD_QUERY_WITH_HEADER)
        .asString()
        .isEqualTo(TEST_HEADER_VALUE);
  }
}
