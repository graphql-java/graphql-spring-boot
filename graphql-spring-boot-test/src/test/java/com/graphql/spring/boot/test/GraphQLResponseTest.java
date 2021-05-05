package com.graphql.spring.boot.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.fail;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphql.spring.boot.test.assertions.GraphQLErrorListAssertion;
import com.graphql.spring.boot.test.assertions.GraphQLFieldAssert;
import com.graphql.spring.boot.test.assertions.NumberOfErrorsAssertion;
import graphql.ErrorType;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JsonContentAssert;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GraphQLResponseTest {

  private static final String DATA_PATH_TEST = "$.data.test";
  private static final String INNER_FOO_LIST_DATA_PATH = "$.data.externalList[*].fooList";
  private static final String NESTED_LIST_RESPONSE_FILE = "response-with-nested-list.json";
  private static final String EMPTY_OBJECT_RESPONSE = "{}";

  @Autowired private ObjectMapper objectMapper;

  private static Stream<Arguments> testGetStringArguments() {
    return Stream.of(
        Arguments.of("{\"data\": {\"test\": 2}}", "2"),
        Arguments.of("{\"data\": {\"test\": \"2\"}}", "2"),
        Arguments.of("{\"data\": {\"test\": \"2020-02-23\"}}", "2020-02-23"));
  }

  private static Stream<Arguments> testGetArguments() {
    return Stream.of(
        Arguments.of("{\"data\": {\"test\": \"2\"}}", Integer.class, 2),
        Arguments.of("{\"data\": {\"test\": \"2\"}}", String.class, "2"),
        Arguments.of("{\"data\": {\"test\": \"2\"}}", BigDecimal.class, new BigDecimal("2")),
        Arguments.of(
            "{\"data\": {\"test\": \"2020-02-23\"}}",
            LocalDate.class,
            LocalDate.parse("2020-02-23")),
        Arguments.of(
            "{\"data\": {\"test\": {\"foo\": \"fizzBuzz\", \"bar\": 13.8 }}}",
            FooBar.class,
            new FooBar("fizzBuzz", new BigDecimal("13.8"))));
  }

  private static Stream<Arguments> testGetListArguments() {
    return Stream.of(
        Arguments.of("{\"data\": {\"test\": [\"2\", \"1\"]}}", Integer.class, Arrays.asList(2, 1)),
        Arguments.of(
            "{\"data\": {\"test\": [\"2\", \"1\"]}}", String.class, Arrays.asList("2", "1")),
        Arguments.of(
            "{\"data\": {\"test\": [\"2\", \"1\"]}}",
            BigDecimal.class,
            Arrays.asList(new BigDecimal("2"), new BigDecimal("1"))),
        Arguments.of(
            "{\"data\": {\"test\": [\"2020-02-23\", \"2020-02-24\"]}}",
            LocalDate.class,
            Arrays.asList(LocalDate.parse("2020-02-23"), LocalDate.parse("2020-02-24"))),
        Arguments.of(
            "{\"data\":{\"test\":[{\"foo\":\"fizz\",\"bar\":1.23},{\"foo\":\"buzz\",\"bar\":32.12}]}}",
            FooBar.class,
            Arrays.asList(
                new FooBar("fizz", new BigDecimal("1.23")),
                new FooBar("buzz", new BigDecimal("32.12")))));
  }

  private static String loadClassPathResource(final String path) {
    try (InputStream resourceStream = new ClassPathResource(path).getInputStream()) {
      return StreamUtils.copyToString(resourceStream, StandardCharsets.UTF_8);
    } catch (IOException e) {
      fail("Test setup error - failed to load test resource.", e);
      return "";
    }
  }

  @DisplayName("Should get the JSON node's value as a String.")
  @ParameterizedTest
  @MethodSource("testGetStringArguments")
  void testGetString(final String bodyString, final String expected) {
    // GIVEN
    final GraphQLResponse graphQLResponse = createResponse(bodyString);
    // WHEN
    final String actual = graphQLResponse.get(DATA_PATH_TEST);
    // THEN
    assertThat(actual).isEqualTo(expected);
  }

  @DisplayName("Should get the JSON node's value as an instance of a specified class.")
  @ParameterizedTest
  @MethodSource("testGetArguments")
  <T> void testGet(final String bodyString, final Class<T> clazz, final T expected) {
    // GIVEN
    final GraphQLResponse graphQLResponse = createResponse(bodyString);
    // WHEN
    final T actual = graphQLResponse.get(DATA_PATH_TEST, clazz);
    // THEN
    assertThat(actual).isInstanceOf(clazz).isEqualTo(expected);
  }

  @DisplayName("Should get the JSON node's value as a List.")
  @ParameterizedTest
  @MethodSource("testGetListArguments")
  <T> void testGetList(final String bodyString, final Class<T> clazz, final List<T> expected) {
    // GIVEN
    final GraphQLResponse graphQLResponse = createResponse(bodyString);
    // WHEN
    final List<T> actual = graphQLResponse.getList(DATA_PATH_TEST, clazz);
    // THEN
    assertThat(actual).containsExactlyElementsOf(expected);
  }

  @DisplayName("Should get field as defined by Jackson's JavaType.")
  @Test
  void testGetAsJavaType() {
    // GIVEN
    final List<List<String>> expected =
        Arrays.asList(Arrays.asList("foo1", "foo2"), Collections.singletonList("foo3"));
    final JavaType stringList =
        objectMapper.getTypeFactory().constructCollectionType(List.class, String.class);
    final JavaType listOfStringLists =
        objectMapper.getTypeFactory().constructCollectionType(List.class, stringList);
    // WHEN
    final List<List<String>> actual =
        createResponse(loadClassPathResource(NESTED_LIST_RESPONSE_FILE))
            .get(INNER_FOO_LIST_DATA_PATH, listOfStringLists);
    // THEN
    assertThat(actual).containsExactlyElementsOf(expected);
  }

  @DisplayName("Should throw illegal argument exception if type is incompatible")
  @Test
  void testGetAsJavaTypeConversionError() {
    // GIVEN
    final JavaType stringType = objectMapper.getTypeFactory().constructType(String.class);
    // WHEN
    final GraphQLResponse actual = createResponse(loadClassPathResource(NESTED_LIST_RESPONSE_FILE));
    // THEN
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> actual.get(INNER_FOO_LIST_DATA_PATH, stringType));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "{\"data\": { \"foo\":\"bar\" } }",
        "{\"errors\": null, \"data\": { \"foo\":\"bar\"} }",
        "{\"errors\": [], \"data\": { \"foo\":\"bar\"} }"
      })
  @DisplayName("Should pass the assertion if no errors are present.")
  void testExpectNoErrorsPass(final String response) {
    // WHEN
    final GraphQLResponse graphQLResponse = createResponse(response);
    // THEN
    assertThat(graphQLResponse.assertThatNoErrorsArePresent())
        .as("Should not throw an exception. Should return GraphQL response instance.")
        .isSameAs(graphQLResponse);
  }

  @Test
  @DisplayName("Should throw assertion error if an error is present in the response.")
  void testExpectNoErrorsFail() {
    // GIVEN
    final String response =
        "{\"errors\": [{\"message\": \"Test error.\"}], \"data\": { \"foo\":\"bar\"} }";
    // WHEN
    final GraphQLResponse graphQLResponse = createResponse(response);
    // THEN
    assertThatExceptionOfType(AssertionError.class)
        .isThrownBy(graphQLResponse::assertThatNoErrorsArePresent)
        .withMessage("Expected no GraphQL errors, but got 1: <Unspecified error>: Test error.");
  }

  @Test
  @DisplayName("Should return an assertion for the number of errors.")
  void testNumberOfErrorsAssertion() {
    // GIVEN
    final String response =
        "{\"errors\": [{\"message\": \"Test error.\"}, {\"message\": \"Test error 2.\"}], "
            + "\"data\": { \"foo\":\"bar\"} }";
    final GraphQLResponse graphQLResponse = createResponse(response);
    // WHEN
    final NumberOfErrorsAssertion actual = graphQLResponse.assertThatNumberOfErrors();
    // THEN
    assertThat(actual).extracting("actual").isEqualTo(2);
    assertThat(actual.and()).isSameAs(graphQLResponse);
  }

  @Test
  @DisplayName("Should return an assertion for the list of errors.")
  void testErrorListAssertion() {
    // GIVEN
    final String response =
        "{\"errors\": [{\"message\": \"Test error.\", \"errorType\": \"DataFetchingException\"}], "
            + "\"data\": { \"foo\":\"bar\"} }";
    final GraphQLResponse graphQLResponse = createResponse(response);
    // WHEN
    final GraphQLErrorListAssertion actual = graphQLResponse.assertThatListOfErrors();
    // THEN
    assertThat(actual).isNotNull();
    assertThat(actual)
        .extracting("actual")
        .isEqualTo(
            Collections.singletonList(
                GraphQLTestError.builder()
                    .message("Test error.")
                    .errorType(ErrorType.DataFetchingException)
                    .build()));
    assertThat(actual.and()).isSameAs(graphQLResponse);
  }

  @Test
  @DisplayName("Should return an assertion for the json content of the response.")
  void testFieldAssertion() {
    // GIVEN
    final String response = "{ \"data\": { \"foo\":\"bar\"} }";
    final GraphQLResponse graphQLResponse = createResponse(response);
    // WHEN
    final JsonContentAssert actual = graphQLResponse.assertThatJsonContent();
    // THEN
    assertThat(actual).isNotNull();
    assertThat(actual).extracting("actual").isEqualTo(response);
  }

  @Test
  @DisplayName("Should return an assertion for the data field.")
  void testDataFieldAssertion() {
    // GIVEN
    final GraphQLResponse response = createResponse(EMPTY_OBJECT_RESPONSE);
    // WHEN
    final GraphQLFieldAssert actual = response.assertThatDataField();
    // THEN
    assertThatAssertionIsCorrect(actual, response, "$.data");
  }

  @Test
  @DisplayName("Should return an assertion for the errors field.")
  void testErrorFieldAssertion() {
    // GIVEN
    final GraphQLResponse response = createResponse(EMPTY_OBJECT_RESPONSE);
    // WHEN
    final GraphQLFieldAssert actual = response.assertThatErrorsField();
    // THEN
    assertThatAssertionIsCorrect(actual, response, "$.errors");
  }

  @Test
  @DisplayName("Should return an assertion for the extensions field.")
  void testErrorExtensionsAssertion() {
    // GIVEN
    final GraphQLResponse response = createResponse(EMPTY_OBJECT_RESPONSE);
    // WHEN
    final GraphQLFieldAssert actual = response.assertThatExtensionsField();
    // THEN
    assertThatAssertionIsCorrect(actual, response, "$.extensions");
  }

  @Test
  @DisplayName("Should return a field assertion for the specific field.")
  void testJsonAssertion() {
    // GIVEN
    final String path = "$any.path";
    final GraphQLResponse graphQLResponse = createResponse(EMPTY_OBJECT_RESPONSE);
    // WHEN
    final GraphQLFieldAssert actual = graphQLResponse.assertThatField(path);
    // THEN
    assertThatAssertionIsCorrect(actual, graphQLResponse, path);
  }

  private GraphQLResponse createResponse(final String s) {
    return new GraphQLResponse(ResponseEntity.ok(s), objectMapper);
  }

  private void assertThatAssertionIsCorrect(
      final GraphQLFieldAssert actual,
      final GraphQLResponse expectedResponse,
      final Object expectedPath) {
    assertThat(actual).isNotNull();
    assertThat(actual)
        .extracting("graphQLResponse", "jsonPath")
        .containsExactly(expectedResponse, expectedPath);
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  private static class FooBar {

    private String foo;
    private BigDecimal bar;
  }
}
