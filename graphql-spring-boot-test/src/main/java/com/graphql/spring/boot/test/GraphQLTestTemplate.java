package com.graphql.spring.boot.test;

import static java.util.Objects.nonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;

/** Helper class to test GraphQL queries and mutations. */
public class GraphQLTestTemplate {

  private final ResourceLoader resourceLoader;
  private final TestRestTemplate restTemplate;
  private final String graphqlMapping;
  private final ObjectMapper objectMapper;
  @Getter private final HttpHeaders headers = new HttpHeaders();

  public GraphQLTestTemplate(
      final ResourceLoader resourceLoader,
      final TestRestTemplate restTemplate,
      @Value("${graphql.servlet.mapping:/graphql}") final String graphqlMapping,
      final ObjectMapper objectMapper) {
    this.resourceLoader = resourceLoader;
    this.restTemplate = restTemplate;
    this.graphqlMapping = graphqlMapping;
    this.objectMapper = objectMapper;
  }

  private String createJsonQuery(String graphql, String operation, ObjectNode variables)
      throws JsonProcessingException {

    ObjectNode wrapper = objectMapper.createObjectNode();
    wrapper.put("query", graphql);
    if (nonNull(operation)) {
      wrapper.put("operationName", operation);
    }
    wrapper.set("variables", variables);
    return objectMapper.writeValueAsString(wrapper);
  }

  private String loadQuery(String location) throws IOException {
    Resource resource = resourceLoader.getResource("classpath:" + location);
    return loadResource(resource);
  }

  private String loadResource(Resource resource) throws IOException {
    try (InputStream inputStream = resource.getInputStream()) {
      return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
    }
  }

  /**
   * Add an HTTP header that will be sent with each request this sends.
   *
   * @param name Name (key) of HTTP header to add.
   * @param value Value(s) of HTTP header to add.
   * @return self
   */
  public GraphQLTestTemplate withAdditionalHeader(final String name, final String... value) {
    headers.addAll(name, Arrays.asList(value));
    return this;
  }

  /**
   * Add multiple HTTP header that will be sent with each request this sends.
   *
   * @param additionalHeaders additional headers to add
   * @return self
   */
  public GraphQLTestTemplate withAdditionalHeaders(
      final MultiValueMap<String, String> additionalHeaders) {
    headers.addAll(additionalHeaders);
    return this;
  }

  /**
   * Adds a bearer token to the authorization header.
   *
   * @param token the bearer token
   * @return self
   */
  public GraphQLTestTemplate withBearerAuth(@NonNull final String token) {
    headers.setBearerAuth(token);
    return this;
  }

  /**
   * Adds basic authentication to the authorization header.
   *
   * @param username the username
   * @param password the password
   * @param charset the charset used by the credentials
   * @return self
   */
  public GraphQLTestTemplate withBasicAuth(
      @NonNull final String username,
      @NonNull final String password,
      @Nullable final Charset charset) {
    headers.setBasicAuth(username, password, charset);
    return this;
  }

  /**
   * Adds basic authentication to the authorization header.
   *
   * @param username the username
   * @param password the password
   * @return self
   */
  public GraphQLTestTemplate withBasicAuth(
      @NonNull final String username, @NonNull final String password) {
    headers.setBasicAuth(username, password, null);
    return this;
  }

  /**
   * Adds basic authentication to the authorization header.
   *
   * @param encodedCredentials the encoded credentials
   * @return self
   */
  public GraphQLTestTemplate withBasicAuth(@NonNull final String encodedCredentials) {
    headers.setBasicAuth(encodedCredentials);
    return this;
  }

  /**
   * Replace any associated HTTP headers with the provided headers.
   *
   * @param newHeaders Headers to use.
   * @return self
   */
  public GraphQLTestTemplate withHeaders(final HttpHeaders newHeaders) {
    return withClearHeaders().withAdditionalHeaders(newHeaders);
  }

  /**
   * Clear all associated HTTP headers.
   *
   * @return self
   */
  public GraphQLTestTemplate withClearHeaders() {
    headers.clear();
    return this;
  }

  /**
   * Loads a GraphQL query or mutation from the given classpath resource and sends it to the GraphQL
   * server.
   *
   * @param graphqlResource path to the classpath resource containing the GraphQL query
   * @param variables the input variables for the GraphQL query
   * @return {@link GraphQLResponse} containing the result of query execution
   * @throws IOException if the resource cannot be loaded from the classpath
   */
  public GraphQLResponse perform(String graphqlResource, ObjectNode variables) throws IOException {
    return perform(graphqlResource, null, variables, Collections.emptyList());
  }

  /**
   * Loads a GraphQL query or mutation from the given classpath resource and sends it to the GraphQL
   * server.
   *
   * @param graphqlResource path to the classpath resource containing the GraphQL query
   * @param operationName the name of the GraphQL operation to be executed
   * @return {@link GraphQLResponse} containing the result of query execution
   * @throws IOException if the resource cannot be loaded from the classpath
   */
  public GraphQLResponse perform(String graphqlResource, String operationName) throws IOException {
    return perform(graphqlResource, operationName, null, Collections.emptyList());
  }

  /**
   * Loads a GraphQL query or mutation from the given classpath resource and sends it to the GraphQL
   * server.
   *
   * @param graphqlResource path to the classpath resource containing the GraphQL query
   * @param operation the name of the GraphQL operation to be executed
   * @param variables the input variables for the GraphQL query
   * @return {@link GraphQLResponse} containing the result of query execution
   * @throws IOException if the resource cannot be loaded from the classpath
   */
  public GraphQLResponse perform(String graphqlResource, String operation, ObjectNode variables)
      throws IOException {
    return perform(graphqlResource, operation, variables, Collections.emptyList());
  }

  /**
   * Loads a GraphQL query or mutation from the given classpath resource and sends it to the GraphQL
   * server.
   *
   * @param graphqlResource path to the classpath resource containing the GraphQL query
   * @param variables the input variables for the GraphQL query
   * @param fragmentResources an ordered list of classpath resources containing GraphQL fragment
   *     definitions.
   * @return {@link GraphQLResponse} containing the result of query execution
   * @throws IOException if the resource cannot be loaded from the classpath
   */
  public GraphQLResponse perform(
      String graphqlResource, ObjectNode variables, List<String> fragmentResources)
      throws IOException {
    return perform(graphqlResource, null, variables, fragmentResources);
  }

  /**
   * Loads a GraphQL query or mutation from the given classpath resource and sends it to the GraphQL
   * server.
   *
   * @param graphqlResource path to the classpath resource containing the GraphQL query
   * @param variables the input variables for the GraphQL query
   * @param fragmentResources an ordered list of classpath resources containing GraphQL fragment
   *     definitions.
   * @return {@link GraphQLResponse} containing the result of query execution
   * @throws IOException if the resource cannot be loaded from the classpath
   */
  public GraphQLResponse perform(
      String graphqlResource,
      String operationName,
      ObjectNode variables,
      List<String> fragmentResources)
      throws IOException {
    StringBuilder sb = new StringBuilder();
    for (String fragmentResource : fragmentResources) {
      sb.append(loadQuery(fragmentResource));
    }
    String graphql = sb.append(loadQuery(graphqlResource)).toString();
    String payload = createJsonQuery(graphql, operationName, variables);
    return post(payload);
  }

  /**
   * Loads a GraphQL query or mutation from the given classpath resource and sends it to the GraphQL
   * server.
   *
   * @param graphqlResource path to the classpath resource containing the GraphQL query
   * @return {@link GraphQLResponse} containing the result of query execution
   * @throws IOException if the resource cannot be loaded from the classpath
   */
  public GraphQLResponse postForResource(String graphqlResource) throws IOException {
    return perform(graphqlResource, null, null, Collections.emptyList());
  }

  /**
   * Loads a GraphQL query or mutation from the given classpath resource, appending any graphql
   * fragment resources provided and sends it to the GraphQL server.
   *
   * @param graphqlResource path to the classpath resource containing the GraphQL query
   * @param fragmentResources an ordered list of classpath resources containing GraphQL fragment
   *     definitions.
   * @return {@link GraphQLResponse} containing the result of query execution
   * @throws IOException if the resource cannot be loaded from the classpath
   */
  public GraphQLResponse postForResource(String graphqlResource, List<String> fragmentResources)
      throws IOException {
    return perform(graphqlResource, null, null, fragmentResources);
  }

  public GraphQLResponse postMultipart(String query, String variables) {
    return postRequest(RequestFactory.forMultipart(query, variables, headers));
  }

  /**
   * Performs a GraphQL request with the provided payload.
   *
   * @param payload the GraphQL payload
   * @return @return {@link GraphQLResponse} containing the result of query execution
   */
  public GraphQLResponse post(String payload) {
    return postRequest(RequestFactory.forJson(payload, headers));
  }

  private GraphQLResponse postRequest(HttpEntity<Object> request) {
    ResponseEntity<String> response =
        restTemplate.exchange(graphqlMapping, HttpMethod.POST, request, String.class);
    return new GraphQLResponse(response, objectMapper);
  }
}
