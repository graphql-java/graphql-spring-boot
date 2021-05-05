package com.graphql.spring.boot.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.graphql.spring.boot.test.assertions.GraphQLFieldAssert;
import com.graphql.spring.boot.test.assertions.GraphQLGenericObjectAssert;
import com.jayway.jsonpath.PathNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class GraphQLFieldAssertAsJavaTypeTest extends GraphQLFieldAssertTestBase {

  private static final JavaType FOO_TYPE = TypeFactory.defaultInstance().constructType(Foo.class);

  @Test
  @DisplayName("Should return a generic assertion (value at specific path is non-null).")
  void shouldReturnGenericObjectAssertIfFieldIsNonNull() {
    // GIVEN
    final Foo foo = new Foo("fooBar");
    given(graphQLResponse.get(MOCK_PATH, FOO_TYPE)).willReturn(foo);
    final GraphQLFieldAssert graphQLFieldAssert =
        new GraphQLFieldAssert(graphQLResponse, MOCK_PATH);
    // WHEN
    final GraphQLGenericObjectAssert<GraphQLFieldAssertAsTest.Foo> actual =
        graphQLFieldAssert.as(FOO_TYPE);
    // THEN
    assertThat(actual).isNotNull();
    assertThat(actual.and()).isSameAs(graphQLResponse);
    assertThat(actual.isEqualTo(foo).and()).isSameAs(graphQLResponse);
    assertThat(actual).extracting("actual").isSameAs(foo);
  }

  @Test
  @DisplayName("Should return a generic assertion (value at specific path is null).")
  void shouldReturnGenericObjectAssertIfFieldIsNull() {
    // GIVEN
    given(graphQLResponse.get(MOCK_PATH, FOO_TYPE)).willReturn(null);
    final GraphQLFieldAssert graphQLFieldAssert =
        new GraphQLFieldAssert(graphQLResponse, MOCK_PATH);
    // WHEN
    final GraphQLGenericObjectAssert<Foo> actual = graphQLFieldAssert.as(FOO_TYPE);
    // THEN
    assertThat(actual).isNotNull();
    assertThat(actual.and()).isSameAs(graphQLResponse);
    assertThat(actual).extracting("actual").isNull();
  }

  @Test
  @DisplayName("Should fail if the value at the provided path is missing.")
  void shouldFailIfPathNotFound(final @Mock PathNotFoundException pathNotFoundException) {
    // GIVEN
    given(graphQLResponse.get(MOCK_PATH, FOO_TYPE)).willThrow(pathNotFoundException);
    final GraphQLFieldAssert graphQLFieldAssert =
        new GraphQLFieldAssert(graphQLResponse, MOCK_PATH);
    // WHEN - THEN
    assertThatExceptionOfType(AssertionError.class)
        .isThrownBy(() -> graphQLFieldAssert.as(FOO_TYPE))
        .withMessage("Expected field %s to be present.", MOCK_PATH)
        .withCause(pathNotFoundException);
  }

  @Test
  @DisplayName("Should fail if the value at the provided path cannot be converted.")
  void shouldFailIfCannotBeConverted(
      final @Mock IllegalArgumentException illegalArgumentException) {
    // GIVEN
    given(graphQLResponse.get(MOCK_PATH, FOO_TYPE)).willThrow(illegalArgumentException);
    final GraphQLFieldAssert graphQLFieldAssert =
        new GraphQLFieldAssert(graphQLResponse, MOCK_PATH);
    // WHEN - THEN
    assertThatExceptionOfType(AssertionError.class)
        .isThrownBy(() -> graphQLFieldAssert.as(FOO_TYPE))
        .withMessage(
            "Expected that content of field %s can be converted to %s.", MOCK_PATH, FOO_TYPE)
        .withCause(illegalArgumentException);
  }
}
