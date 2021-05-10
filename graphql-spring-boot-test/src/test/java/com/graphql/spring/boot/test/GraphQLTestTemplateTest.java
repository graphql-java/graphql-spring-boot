package com.graphql.spring.boot.test;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;

@ExtendWith(MockitoExtension.class)
class GraphQLTestTemplateTest {

  private static final String GRAPHQL_ENDPOINT = "/test-graphql-endpoint";
  private static final String HEADER_NAME_1 = "header-1";
  private static final String HEADER_VALUE_1 = "value-1";
  private static final String HEADER_NAME_2 = "header-2";
  private static final String HEADER_VALUE_2 = "value-2";
  private static final String MOCK_BEARER_TOKEN = "mock token";
  private static final String TEST_USERNAME = "test";
  private static final String TEST_PASSWORD = "value";
  private static final String ENCODED_BASIC_AUTH = "dGVzdDp2YWx1ZQ==";
  private static final String BASIC_AUTH_PREFIX = "Basic ";
  private static final String BEARER_AUTH_PREFIX = "Bearer ";

  @Mock private ResourceLoader resourceLoader;

  @Mock private TestRestTemplate testRestTemplate;

  private GraphQLTestTemplate graphQLTestTemplate;

  @BeforeEach
  void setUp() {
    graphQLTestTemplate =
        new GraphQLTestTemplate(
            resourceLoader, testRestTemplate, GRAPHQL_ENDPOINT, new ObjectMapper());
  }

  @Test
  void testWithAdditionalHeader() {
    // WHEN
    final GraphQLTestTemplate actual =
        graphQLTestTemplate
            .withAdditionalHeader(HEADER_NAME_1, HEADER_VALUE_1)
            .withAdditionalHeader(HEADER_NAME_2, HEADER_VALUE_2);
    // THEN
    assertThat(actual).isSameAs(graphQLTestTemplate);
    assertThatContainsAllHeaders();
  }

  @Test
  void testWithAdditionalHeaders() {
    // GIVEN
    graphQLTestTemplate.getHeaders().add(HEADER_NAME_1, HEADER_VALUE_1);
    final HttpHeaders additionalHeaders = new HttpHeaders();
    additionalHeaders.add(HEADER_NAME_2, HEADER_VALUE_2);
    // WHEN
    final GraphQLTestTemplate actualGraphqlTestTemplate =
        graphQLTestTemplate.withAdditionalHeaders(additionalHeaders);
    // THEN
    assertThat(actualGraphqlTestTemplate).isSameAs(graphQLTestTemplate);
    assertThatContainsAllHeaders();
  }

  @Test
  void testWithHeaders() {
    // GIVEN
    graphQLTestTemplate.getHeaders().add(HEADER_NAME_1, HEADER_VALUE_1);
    final HttpHeaders newHeaders = new HttpHeaders();
    newHeaders.add(HEADER_NAME_2, HEADER_VALUE_2);
    // WHEN
    final GraphQLTestTemplate actual = graphQLTestTemplate.withHeaders(newHeaders);
    // THEN
    assertThat(actual).isSameAs(graphQLTestTemplate);
    assertThat(actual.getHeaders()).hasSize(1);
    assertThatContainsSecondHeader();
  }

  @Test
  void testWithClearHeaders() {
    // GIVEN
    graphQLTestTemplate.getHeaders().add(HEADER_NAME_1, HEADER_VALUE_1);
    // WHEN
    final GraphQLTestTemplate actual = graphQLTestTemplate.withClearHeaders();
    // THEN
    assertThat(graphQLTestTemplate).isSameAs(actual);
    assertThat(graphQLTestTemplate.getHeaders()).isEmpty();
  }

  @Test
  void testWithBearerAuth() {
    // WHEN
    final GraphQLTestTemplate actual = graphQLTestTemplate.withBearerAuth(MOCK_BEARER_TOKEN);
    // THEN
    assertThat(actual).isSameAs(graphQLTestTemplate);
    assertAuthHeader(BEARER_AUTH_PREFIX, MOCK_BEARER_TOKEN);
  }

  @Test
  void testWithEncodedBasicAuth() {
    // WHEN
    final GraphQLTestTemplate actual = graphQLTestTemplate.withBasicAuth(ENCODED_BASIC_AUTH);
    // THEN
    assertThat(actual).isSameAs(graphQLTestTemplate);
    assertAuthHeader(BASIC_AUTH_PREFIX, ENCODED_BASIC_AUTH);
  }

  @Test
  void testWithBasicAuth() {
    // WHEN
    final GraphQLTestTemplate actual =
        graphQLTestTemplate.withBasicAuth(TEST_USERNAME, TEST_PASSWORD);
    // THEN
    assertThat(actual).isSameAs(graphQLTestTemplate);
    assertAuthHeader(BASIC_AUTH_PREFIX, ENCODED_BASIC_AUTH);
  }

  @Test
  void testWithBasicAuthCharset() {
    // WHEN
    final GraphQLTestTemplate actual =
        graphQLTestTemplate.withBasicAuth(TEST_USERNAME, TEST_PASSWORD, StandardCharsets.UTF_8);
    // THEN
    assertThat(actual).isSameAs(graphQLTestTemplate);
    assertAuthHeader(BASIC_AUTH_PREFIX, ENCODED_BASIC_AUTH);
  }

  private void assertAuthHeader(final String authPrefix, final String authValue) {
    assertThatContainsHeader(HttpHeaders.AUTHORIZATION, authPrefix + authValue);
  }

  private void assertThatContainsAllHeaders() {
    final HttpHeaders actual = graphQLTestTemplate.getHeaders();
    assertThat(actual).hasSize(2);
    assertThatContainsFirstHeader();
    assertThatContainsSecondHeader();
  }

  private void assertThatContainsSecondHeader() {
    assertThatContainsHeader(HEADER_NAME_2, HEADER_VALUE_2);
  }

  private void assertThatContainsFirstHeader() {
    assertThatContainsHeader(HEADER_NAME_1, HEADER_VALUE_1);
  }

  private void assertThatContainsHeader(final String headerName, final String headerValue) {
    assertThat(graphQLTestTemplate.getHeaders())
        .containsEntry(headerName, Collections.singletonList(headerValue));
  }
}
