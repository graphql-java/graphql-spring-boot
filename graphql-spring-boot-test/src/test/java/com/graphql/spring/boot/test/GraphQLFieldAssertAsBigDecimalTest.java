package com.graphql.spring.boot.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

import com.graphql.spring.boot.test.assertions.GraphQLBigDecimalAssert;
import com.graphql.spring.boot.test.assertions.GraphQLFieldAssert;
import com.jayway.jsonpath.PathNotFoundException;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class GraphQLFieldAssertAsBigDecimalTest extends GraphQLFieldAssertTestBase {

  @Test
  @DisplayName("Should return a big decimal assertion (value at specific path is valid number).")
  void shouldReturnBigDecimalAssertIfFieldIsNonNull() {
    // GIVEN
    final BigDecimal value = new BigDecimal("1.23");
    given(graphQLResponse.get(MOCK_PATH, BigDecimal.class)).willReturn(value);
    final GraphQLFieldAssert graphQLFieldAssert =
        new GraphQLFieldAssert(graphQLResponse, MOCK_PATH);
    // WHEN
    final GraphQLBigDecimalAssert actual = graphQLFieldAssert.asBigDecimal();
    // THEN
    assertThat(actual).isNotNull();
    assertThat(actual.and()).isSameAs(graphQLResponse);
    assertThat(actual.isEqualByComparingTo(value).and()).isSameAs(graphQLResponse);
    assertThat(actual).extracting("actual").isSameAs(value);
  }

  @Test
  @DisplayName("Should return a big decimal assertion (value at specific path is null).")
  void shouldReturnBigDecimalAssertIfFieldIsNull() {
    // GIVEN
    given(graphQLResponse.get(MOCK_PATH, BigDecimal.class)).willReturn(null);
    final GraphQLFieldAssert graphQLFieldAssert =
        new GraphQLFieldAssert(graphQLResponse, MOCK_PATH);
    // WHEN
    final GraphQLBigDecimalAssert actual = graphQLFieldAssert.asBigDecimal();
    // THEN
    assertThat(actual).isNotNull();
    assertThat(actual.and()).isSameAs(graphQLResponse);
    assertThat(actual).extracting("actual").isNull();
  }

  @Test
  @DisplayName("Should fail if the value at the provided path is missing.")
  void shouldFailIfPathNotFound(final @Mock PathNotFoundException pathNotFoundException) {
    // GIVEN
    given(graphQLResponse.get(MOCK_PATH, BigDecimal.class)).willThrow(pathNotFoundException);
    final GraphQLFieldAssert graphQLFieldAssert =
        new GraphQLFieldAssert(graphQLResponse, MOCK_PATH);
    // WHEN - THEN
    assertThatExceptionOfType(AssertionError.class)
        .isThrownBy(graphQLFieldAssert::asBigDecimal)
        .withMessage("Expected field %s to be present.", MOCK_PATH)
        .withCause(pathNotFoundException);
  }

  @Test
  @DisplayName("Should fail if the value at the provided path cannot be converted.")
  void shouldFailIfCannotBeConverted(
      final @Mock IllegalArgumentException illegalArgumentException) {
    // GIVEN
    given(graphQLResponse.get(MOCK_PATH, BigDecimal.class)).willThrow(illegalArgumentException);
    final GraphQLFieldAssert graphQLFieldAssert =
        new GraphQLFieldAssert(graphQLResponse, MOCK_PATH);
    // WHEN - THEN
    assertThatExceptionOfType(AssertionError.class)
        .isThrownBy(graphQLFieldAssert::asBigDecimal)
        .withMessage(
            "Expected that content of field %s can be converted to %s.",
            MOCK_PATH, BigDecimal.class)
        .withCause(illegalArgumentException);
  }
}
