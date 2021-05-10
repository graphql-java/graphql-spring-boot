package com.graphql.spring.boot.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

import com.graphql.spring.boot.test.assertions.GraphQLFieldAssert;
import com.graphql.spring.boot.test.assertions.GraphQLShortAssert;
import com.jayway.jsonpath.PathNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class GraphQLFieldAssertAsShortTest extends GraphQLFieldAssertTestBase {

  @Test
  @DisplayName("Should return a Short assertion (value at specific path is valid Short value).")
  void shouldReturnShortAssertIfFieldIsNonNull() {
    // GIVEN
    final Short value = 123;
    given(graphQLResponse.get(MOCK_PATH, Short.class)).willReturn(value);
    final GraphQLFieldAssert graphQLFieldAssert =
        new GraphQLFieldAssert(graphQLResponse, MOCK_PATH);
    // WHEN
    final GraphQLShortAssert actual = graphQLFieldAssert.asShort();
    // THEN
    assertThat(actual).isNotNull();
    assertThat(actual.and()).isSameAs(graphQLResponse);
    assertThat(actual.isEqualByComparingTo(value).and()).isSameAs(graphQLResponse);
    assertThat(actual).extracting("actual").isSameAs(value);
  }

  @Test
  @DisplayName("Should return a Short assertion (value at specific path is null).")
  void shouldReturnShortAssertIfFieldIsNull() {
    // GIVEN
    given(graphQLResponse.get(MOCK_PATH, Short.class)).willReturn(null);
    final GraphQLFieldAssert graphQLFieldAssert =
        new GraphQLFieldAssert(graphQLResponse, MOCK_PATH);
    // WHEN
    final GraphQLShortAssert actual = graphQLFieldAssert.asShort();
    // THEN
    assertThat(actual).isNotNull();
    assertThat(actual.and()).isSameAs(graphQLResponse);
    assertThat(actual).extracting("actual").isNull();
  }

  @Test
  @DisplayName("Should fail if the value at the provided path is missing.")
  void shouldFailIfPathNotFound(final @Mock PathNotFoundException pathNotFoundException) {
    // GIVEN
    given(graphQLResponse.get(MOCK_PATH, Short.class)).willThrow(pathNotFoundException);
    final GraphQLFieldAssert graphQLFieldAssert =
        new GraphQLFieldAssert(graphQLResponse, MOCK_PATH);
    // WHEN - THEN
    assertThatExceptionOfType(AssertionError.class)
        .isThrownBy(graphQLFieldAssert::asShort)
        .withMessage("Expected field %s to be present.", MOCK_PATH)
        .withCause(pathNotFoundException);
  }

  @Test
  @DisplayName("Should fail if the value at the provided path cannot be converted.")
  void shouldFailIfCannotBeConverted(
      final @Mock IllegalArgumentException illegalArgumentException) {
    // GIVEN
    given(graphQLResponse.get(MOCK_PATH, Short.class)).willThrow(illegalArgumentException);
    final GraphQLFieldAssert graphQLFieldAssert =
        new GraphQLFieldAssert(graphQLResponse, MOCK_PATH);
    // WHEN - THEN
    assertThatExceptionOfType(AssertionError.class)
        .isThrownBy(graphQLFieldAssert::asShort)
        .withMessage(
            "Expected that content of field %s can be converted to %s.", MOCK_PATH, Short.class)
        .withCause(illegalArgumentException);
  }
}
