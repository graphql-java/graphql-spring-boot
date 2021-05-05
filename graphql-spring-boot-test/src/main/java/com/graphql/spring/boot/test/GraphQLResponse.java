package com.graphql.spring.boot.test;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphql.spring.boot.test.assertions.GraphQLErrorListAssertion;
import com.graphql.spring.boot.test.assertions.GraphQLFieldAssert;
import com.graphql.spring.boot.test.assertions.NumberOfErrorsAssertion;
import com.graphql.spring.boot.test.helper.GraphQLTestConstantsHelper;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import org.springframework.boot.test.json.JsonContentAssert;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GraphQLResponse {

  private final ResponseEntity<String> responseEntity;
  private final ObjectMapper mapper;
  private final ReadContext context;

  public GraphQLResponse(ResponseEntity<String> responseEntity, ObjectMapper objectMapper) {
    this.responseEntity = Objects.requireNonNull(responseEntity);
    this.mapper = Objects.requireNonNull(objectMapper);

    Objects.requireNonNull(
        responseEntity.getBody(),
        () -> "Body is empty with status " + responseEntity.getStatusCodeValue());
    context = JsonPath.parse(responseEntity.getBody());
  }

  public JsonNode readTree() throws IOException {
    return mapper.readTree(responseEntity.getBody());
  }

  public Object getRaw(String path) {
    return get(path, Object.class);
  }

  public String get(String path) {
    return get(path, String.class);
  }

  public <T> T get(String path, Class<T> type) {
    return mapper.convertValue(context.read(path), type);
  }

  public <T> T get(String path, JavaType type) {
    return mapper.convertValue(context.read(path), type);
  }

  public <T> List<T> getList(String path, Class<T> type) {
    return get(path, mapper.getTypeFactory().constructCollectionType(List.class, type));
  }

  public ReadContext context() {
    return context;
  }

  public boolean isOk() {
    return getStatusCode() == HttpStatus.OK;
  }

  public HttpStatus getStatusCode() {
    return responseEntity.getStatusCode();
  }

  public ResponseEntity<String> getRawResponse() {
    return responseEntity;
  }

  /**
   * Asserts that no errors are present in the response. An empty or null "errors" array also passes
   * this test.
   *
   * @return this object
   */
  public GraphQLResponse assertThatNoErrorsArePresent() {
    return assertThatListOfErrors().hasNoErrors().and();
  }

  /**
   * Returns an assertion for the number of errors in the response.
   *
   * @return the assertion for the number of errors.
   */
  public NumberOfErrorsAssertion assertThatNumberOfErrors() {
    return new NumberOfErrorsAssertion(this);
  }

  /**
   * Returns an assertion for the list of errors in the response.
   *
   * @return the assertion for the list of errors.
   */
  public GraphQLErrorListAssertion assertThatListOfErrors() {
    return new GraphQLErrorListAssertion(this);
  }

  /**
   * Returns an assertion for the given field(s).
   *
   * @param jsonPath the JSON path of the field(s) to assert.
   * @return the assertion for the given field.
   */
  public GraphQLFieldAssert assertThatField(final String jsonPath) {
    return new GraphQLFieldAssert(this, jsonPath);
  }

  /**
   * Returns an assertion for the root "data" field.
   *
   * @return the assertion for the $.data field.
   */
  public GraphQLFieldAssert assertThatDataField() {
    return assertThatField(GraphQLTestConstantsHelper.DATA_PATH);
  }

  /**
   * Returns an assertion for the root "extensions" field.
   *
   * @return the assertion for the $.extensions field.
   */
  public GraphQLFieldAssert assertThatExtensionsField() {
    return assertThatField(GraphQLTestConstantsHelper.EXTENSIONS_PATH);
  }

  /**
   * Returns an assertion for the root "errors" field.
   *
   * @return the assertion for the $.errors field.
   */
  public GraphQLFieldAssert assertThatErrorsField() {
    return assertThatField(GraphQLTestConstantsHelper.ERRORS_PATH);
  }

  /**
   * Returns an assertion for the JSON content of the response. Since the Spring Boot Framework does
   * not provide an abstract version of {@link JsonContentAssert} (as core AssertJ assertions do),
   * it is not possible chain other assertions after this one.
   *
   * @return a {@link JsonContentAssert} instance for the content of the response.
   */
  public JsonContentAssert assertThatJsonContent() {
    return new JsonContentAssert(null, responseEntity.getBody());
  }
}
