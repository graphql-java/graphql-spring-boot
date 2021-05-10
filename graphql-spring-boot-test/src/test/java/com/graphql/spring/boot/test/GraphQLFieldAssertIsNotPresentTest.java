package com.graphql.spring.boot.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

import com.graphql.spring.boot.test.assertions.GraphQLFieldAssert;
import com.jayway.jsonpath.PathNotFoundException;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class GraphQLFieldAssertIsNotPresentTest extends GraphQLFieldAssertTestBase {

  private static Stream<String> valuesThatShouldCauseFailure() {
    return Stream.of(null, NON_NULL_VALUE);
  }

  @ParameterizedTest(name = "value = {0}")
  @MethodSource("valuesThatShouldCauseFailure")
  @DisplayName("Should fail if the value at the provided path is present.")
  void shouldFailIfValueIsPresent(final String value) {
    // GIVEN
    given(graphQLResponse.getRaw(MOCK_PATH)).willReturn(value);
    final GraphQLFieldAssert graphQLFieldAssert =
        new GraphQLFieldAssert(graphQLResponse, MOCK_PATH);
    // WHEN - THEN
    assertThatExceptionOfType(AssertionError.class)
        .isThrownBy(graphQLFieldAssert::isNotPresent)
        .withMessage("Expected that field %s is not present.", MOCK_PATH);
  }

  @Test
  @DisplayName("Should pass if the path is not present in the response.")
  void shouldPassIfNotPresent() {
    // GIVEN
    given(graphQLResponse.getRaw(MOCK_PATH)).willThrow(PathNotFoundException.class);
    final GraphQLFieldAssert graphQLFieldAssert =
        new GraphQLFieldAssert(graphQLResponse, MOCK_PATH);
    // WHEN - THEN
    assertThatCode(graphQLFieldAssert::isNotPresent).doesNotThrowAnyException();
    assertThat(graphQLFieldAssert.isNotPresent().and()).isSameAs(graphQLResponse);
  }
}
