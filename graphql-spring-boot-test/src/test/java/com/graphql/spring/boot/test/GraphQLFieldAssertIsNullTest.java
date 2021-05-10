package com.graphql.spring.boot.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

import com.graphql.spring.boot.test.assertions.GraphQLFieldAssert;
import com.jayway.jsonpath.PathNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class GraphQLFieldAssertIsNullTest extends GraphQLFieldAssertTestBase {

  @Test
  @DisplayName("Should pass if the value at the provided path is null.")
  void shouldPassIfNull() {
    // GIVEN
    given(graphQLResponse.getRaw(MOCK_PATH)).willReturn(null);
    final GraphQLFieldAssert graphQLFieldAssert =
        new GraphQLFieldAssert(graphQLResponse, MOCK_PATH);
    // WHEN - THEN
    assertThatCode(graphQLFieldAssert::isNull).doesNotThrowAnyException();
    assertThat(graphQLFieldAssert.isNull().and()).isSameAs(graphQLResponse);
  }

  @Test
  @DisplayName("Should fail if the value at the provided path is missing.")
  void shouldFailIfPathNotFound(final @Mock PathNotFoundException pathNotFoundException) {
    // GIVEN
    given(graphQLResponse.getRaw(MOCK_PATH)).willThrow(pathNotFoundException);
    final GraphQLFieldAssert graphQLFieldAssert =
        new GraphQLFieldAssert(graphQLResponse, MOCK_PATH);
    // WHEN - THEN
    assertThatExceptionOfType(AssertionError.class)
        .isThrownBy(graphQLFieldAssert::isNull)
        .withMessage("Expected field %s to be present.", MOCK_PATH)
        .withCause(pathNotFoundException);
  }

  @Test
  @DisplayName("Should fail if the value at the provided path is not null.")
  void shouldFailIfIsNotNull() {
    // GIVEN
    given(graphQLResponse.getRaw(MOCK_PATH)).willReturn(NON_NULL_VALUE);
    final GraphQLFieldAssert graphQLFieldAssert =
        new GraphQLFieldAssert(graphQLResponse, MOCK_PATH);
    // WHEN - THEN
    assertThatExceptionOfType(AssertionError.class)
        .isThrownBy(graphQLFieldAssert::isNull)
        .withMessage("Expected field %s to be null.", MOCK_PATH);
  }
}
