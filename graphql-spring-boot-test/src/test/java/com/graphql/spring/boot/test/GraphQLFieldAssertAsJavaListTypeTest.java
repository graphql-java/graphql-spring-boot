package com.graphql.spring.boot.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.graphql.spring.boot.test.assertions.GraphQLFieldAssert;
import com.graphql.spring.boot.test.assertions.GraphQLListAssert;
import com.jayway.jsonpath.PathNotFoundException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class GraphQLFieldAssertAsJavaListTypeTest extends GraphQLFieldAssertTestBase {

  private static final JavaType STRING_LIST_TYPE =
      TypeFactory.defaultInstance().constructCollectionLikeType(List.class, String.class);

  @Test
  @DisplayName("Should return a String list assertion (value at specific path is valid list).")
  void shouldReturnStringListAssertIfFieldIsNonNull() {
    // GIVEN
    final List<String> values = Arrays.asList("value1", "value2");
    given(graphQLResponse.get(MOCK_PATH, STRING_LIST_TYPE)).willReturn(values);
    final GraphQLFieldAssert graphQLFieldAssert =
        new GraphQLFieldAssert(graphQLResponse, MOCK_PATH);
    // WHEN
    final GraphQLListAssert<String> actual = graphQLFieldAssert.asList(STRING_LIST_TYPE);
    // THEN
    assertThat(actual).isNotNull();
    assertThat(actual.and()).isSameAs(graphQLResponse);
    assertThat(actual.containsExactlyElementsOf(values).and()).isSameAs(graphQLResponse);
    assertThat(actual).extracting("actual").isSameAs(values);
  }

  @Test
  @DisplayName("Should return a String list assertion (value at specific path is null).")
  void shouldReturnStringListAssertIfFieldIsNull() {
    // GIVEN
    given(graphQLResponse.get(MOCK_PATH, STRING_LIST_TYPE)).willReturn(null);
    final GraphQLFieldAssert graphQLFieldAssert =
        new GraphQLFieldAssert(graphQLResponse, MOCK_PATH);
    // WHEN
    final GraphQLListAssert<String> actual = graphQLFieldAssert.asList(STRING_LIST_TYPE);
    // THEN
    assertThat(actual).isNotNull();
    assertThat(actual.and()).isSameAs(graphQLResponse);
    assertThat(actual).extracting("actual").isNull();
  }

  @Test
  @DisplayName("Should fail if the value at the provided path is missing.")
  void shouldFailIfPathNotFound(final @Mock PathNotFoundException pathNotFoundException) {
    // GIVEN
    given(graphQLResponse.get(MOCK_PATH, STRING_LIST_TYPE)).willThrow(pathNotFoundException);
    final GraphQLFieldAssert graphQLFieldAssert =
        new GraphQLFieldAssert(graphQLResponse, MOCK_PATH);
    // WHEN - THEN
    assertThatExceptionOfType(AssertionError.class)
        .isThrownBy(() -> graphQLFieldAssert.asList(STRING_LIST_TYPE))
        .withMessage("Expected field %s to be present.", MOCK_PATH)
        .withCause(pathNotFoundException);
  }

  @Test
  @DisplayName("Should fail if the value at the provided path cannot be converted.")
  void shouldFailIfCannotBeConverted(
      final @Mock IllegalArgumentException illegalArgumentException) {
    // GIVEN
    given(graphQLResponse.get(MOCK_PATH, STRING_LIST_TYPE)).willThrow(illegalArgumentException);
    final GraphQLFieldAssert graphQLFieldAssert =
        new GraphQLFieldAssert(graphQLResponse, MOCK_PATH);
    // WHEN - THEN
    assertThatExceptionOfType(AssertionError.class)
        .isThrownBy(() -> graphQLFieldAssert.asList(STRING_LIST_TYPE))
        .withMessage(
            "Expected that content of field %s can be converted to %s.",
            MOCK_PATH, STRING_LIST_TYPE)
        .withCause(illegalArgumentException);
  }
}
