/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 oEmbedler Inc. and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 *  documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 *  persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package graphql.kickstart.spring.web.boot;

import static graphql.kickstart.execution.GraphQLObjectMapper.newBuilder;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.ExecutionStrategy;
import graphql.execution.SubscriptionExecutionStrategy;
import graphql.execution.instrumentation.ChainedInstrumentation;
import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentationOptions;
import graphql.execution.preparsed.PreparsedDocumentProvider;
import graphql.kickstart.execution.BatchedDataLoaderGraphQLBuilder;
import graphql.kickstart.execution.GraphQLInvoker;
import graphql.kickstart.execution.GraphQLObjectMapper;
import graphql.kickstart.execution.config.DefaultExecutionStrategyProvider;
import graphql.kickstart.execution.config.ExecutionStrategyProvider;
import graphql.kickstart.execution.config.GraphQLBuilder;
import graphql.kickstart.execution.config.GraphQLBuilderConfigurer;
import graphql.kickstart.execution.config.GraphQLServletObjectMapperConfigurer;
import graphql.kickstart.execution.config.ObjectMapperProvider;
import graphql.kickstart.execution.error.GraphQLErrorHandler;
import graphql.kickstart.servlet.AbstractGraphQLHttpServlet;
import graphql.kickstart.servlet.GraphQLConfiguration;
import graphql.kickstart.servlet.GraphQLHttpServlet;
import graphql.kickstart.servlet.cache.GraphQLResponseCacheManager;
import graphql.kickstart.servlet.config.DefaultGraphQLSchemaServletProvider;
import graphql.kickstart.servlet.config.GraphQLSchemaServletProvider;
import graphql.kickstart.servlet.context.GraphQLServletContextBuilder;
import graphql.kickstart.servlet.core.GraphQLServletListener;
import graphql.kickstart.servlet.core.GraphQLServletRootObjectBuilder;
import graphql.kickstart.servlet.input.BatchInputPreProcessor;
import graphql.kickstart.servlet.input.GraphQLInvocationInputFactory;
import graphql.kickstart.spring.error.ErrorHandlerSupplier;
import graphql.kickstart.spring.error.GraphQLErrorStartupListener;
import graphql.kickstart.spring.web.boot.metrics.MetricsInstrumentation;
import graphql.kickstart.tools.boot.GraphQLJavaToolsAutoConfiguration;
import graphql.schema.GraphQLSchema;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import javax.servlet.MultipartConfigElement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.DispatcherServlet;

/** @author <a href="mailto:java.lang.RuntimeException@gmail.com">oEmbedler Inc.</a> */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnClass(DispatcherServlet.class)
@Conditional(OnSchemaOrSchemaProviderBean.class)
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@ConditionalOnProperty(
    value = "graphql.servlet.enabled",
    havingValue = "true",
    matchIfMissing = true)
@AutoConfigureAfter({GraphQLJavaToolsAutoConfiguration.class, JacksonAutoConfiguration.class})
@EnableConfigurationProperties({GraphQLServletProperties.class})
public class GraphQLWebAutoConfiguration {

  public static final String QUERY_EXECUTION_STRATEGY = "queryExecutionStrategy";
  public static final String MUTATION_EXECUTION_STRATEGY = "mutationExecutionStrategy";
  public static final String SUBSCRIPTION_EXECUTION_STRATEGY = "subscriptionExecutionStrategy";

  private final GraphQLServletProperties graphQLServletProperties;
  private final ErrorHandlerSupplier errorHandlerSupplier = new ErrorHandlerSupplier(null);

  @Bean
  public GraphQLErrorStartupListener graphQLErrorStartupListener(
      @Autowired(required = false) GraphQLErrorHandler errorHandler) {
    errorHandlerSupplier.setErrorHandler(errorHandler);
    return new GraphQLErrorStartupListener(
        errorHandlerSupplier, graphQLServletProperties.isExceptionHandlersEnabled());
  }

  @Bean
  @ConditionalOnClass(CorsFilter.class)
  @ConfigurationProperties("graphql.servlet.cors")
  public CorsConfiguration corsConfiguration() {
    return new CorsConfiguration();
  }

  @Bean
  @ConditionalOnClass(CorsFilter.class)
  @ConditionalOnProperty(
      value = "graphql.servlet.corsEnabled",
      havingValue = "true",
      matchIfMissing = true)
  public CorsFilter corsConfigurer(CorsConfiguration corsConfiguration) {
    Map<String, CorsConfiguration> corsConfigurations = new LinkedHashMap<>(1);
    if (corsConfiguration.getAllowedMethods() == null) {
      corsConfiguration.setAllowedMethods(
          Arrays.asList(HttpMethod.GET.name(), HttpMethod.HEAD.name(), HttpMethod.POST.name()));
    }
    corsConfigurations.put(graphQLServletProperties.getCorsMapping(), corsConfiguration);

    UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
    configurationSource.setCorsConfigurations(corsConfigurations);
    configurationSource.setAlwaysUseFullPath(true);

    return new CorsFilter(configurationSource);
  }

  @Bean
  @ConditionalOnMissingBean
  public GraphQLSchemaServletProvider graphQLSchemaProvider(GraphQLSchema schema) {
    return new DefaultGraphQLSchemaServletProvider(schema);
  }

  @Bean
  @ConditionalOnMissingBean
  public ExecutionStrategyProvider executionStrategyProvider(
      @Autowired(required = false) Map<String, ExecutionStrategy> executionStrategies) {
    if (executionStrategies == null || executionStrategies.isEmpty()) {
      return new DefaultExecutionStrategyProvider(
          new AsyncExecutionStrategy(), null, new SubscriptionExecutionStrategy());
    } else if (executionStrategies.entrySet().size() == 1) {
      return new DefaultExecutionStrategyProvider(
          executionStrategies.entrySet().stream()
              .findFirst()
              .map(Entry::getValue)
              .orElseThrow(IllegalStateException::new));
    } else {

      if (!executionStrategies.containsKey(QUERY_EXECUTION_STRATEGY)) {
        throwIncorrectExecutionStrategyNameException();
      }

      if (executionStrategies.size() == 2
          && !(executionStrategies.containsKey(MUTATION_EXECUTION_STRATEGY)
              || executionStrategies.containsKey(SUBSCRIPTION_EXECUTION_STRATEGY))) {
        throwIncorrectExecutionStrategyNameException();
      }

      if (executionStrategies.size() >= 3
          && !(executionStrategies.containsKey(MUTATION_EXECUTION_STRATEGY)
              && executionStrategies.containsKey(SUBSCRIPTION_EXECUTION_STRATEGY))) {
        throwIncorrectExecutionStrategyNameException();
      }

      return new DefaultExecutionStrategyProvider(
          executionStrategies.get(QUERY_EXECUTION_STRATEGY),
          executionStrategies.get(MUTATION_EXECUTION_STRATEGY),
          executionStrategies.get(SUBSCRIPTION_EXECUTION_STRATEGY));
    }
  }

  private void throwIncorrectExecutionStrategyNameException() {
    throw new IllegalStateException(
        String.format(
            "When defining more than one execution strategy, they must be named %s, %s, or %s",
            QUERY_EXECUTION_STRATEGY,
            MUTATION_EXECUTION_STRATEGY,
            SUBSCRIPTION_EXECUTION_STRATEGY));
  }

  @Bean
  @ConditionalOnMissingBean
  public GraphQLInvocationInputFactory invocationInputFactory(
      GraphQLSchemaServletProvider schemaProvider,
      @Autowired(required = false) GraphQLServletContextBuilder contextBuilder,
      @Autowired(required = false) GraphQLServletRootObjectBuilder graphQLRootObjectBuilder) {
    GraphQLInvocationInputFactory.Builder builder =
        GraphQLInvocationInputFactory.newBuilder(schemaProvider);

    if (graphQLRootObjectBuilder != null) {
      builder.withGraphQLRootObjectBuilder(graphQLRootObjectBuilder);
    }

    if (contextBuilder != null) {
      builder.withGraphQLContextBuilder(contextBuilder);
    }

    return builder.build();
  }

  @Bean
  @ConditionalOnMissingBean
  public GraphQLBuilder graphQLBuilder(
      ExecutionStrategyProvider executionStrategyProvider,
      @Autowired(required = false) List<Instrumentation> instrumentations,
      @Autowired(required = false) PreparsedDocumentProvider preparsedDocumentProvider,
      @Autowired(required = false) GraphQLBuilderConfigurer graphQLBuilderConfigurer) {
    GraphQLBuilder graphQLBuilder = new GraphQLBuilder();
    graphQLBuilder.executionStrategyProvider(() -> executionStrategyProvider);

    if (instrumentations != null && !instrumentations.isEmpty()) {
      if (instrumentations.size() == 1) {
        graphQLBuilder.instrumentation(() -> instrumentations.get(0));
      } else {
        // Metrics instrumentation should be the last to run (we need that from
        // TracingInstrumentation)
        instrumentations.sort((a, b) -> a instanceof MetricsInstrumentation ? 1 : 0);
        graphQLBuilder.instrumentation(() -> new ChainedInstrumentation(instrumentations));
      }
    }

    if (preparsedDocumentProvider != null) {
      graphQLBuilder.preparsedDocumentProvider(() -> preparsedDocumentProvider);
    }

    if (graphQLBuilderConfigurer != null) {
      graphQLBuilder.graphQLBuilderConfigurer(() -> graphQLBuilderConfigurer);
    }

    return graphQLBuilder;
  }

  @Bean
  @ConditionalOnMissingBean
  public BatchedDataLoaderGraphQLBuilder batchedDataLoaderGraphQLBuilder(
      @Autowired(required = false)
          Supplier<DataLoaderDispatcherInstrumentationOptions> optionsSupplier) {
    return new BatchedDataLoaderGraphQLBuilder(optionsSupplier);
  }

  @Bean
  @ConditionalOnMissingBean
  public GraphQLInvoker graphQLInvoker(
      GraphQLBuilder graphQLBuilder,
      BatchedDataLoaderGraphQLBuilder batchedDataLoaderGraphQLBuilder) {
    return new GraphQLInvoker(graphQLBuilder, batchedDataLoaderGraphQLBuilder);
  }

  @Bean
  @ConditionalOnMissingBean
  public GraphQLObjectMapper graphQLObjectMapper(
      ObjectProvider<ObjectMapperProvider> objectMapperProviderObjectProvider,
      @Autowired(required = false) GraphQLServletObjectMapperConfigurer objectMapperConfigurer) {
    GraphQLObjectMapper.Builder builder = newBuilder();
    builder.withGraphQLErrorHandler(errorHandlerSupplier);

    ObjectMapperProvider objectMapperProvider = objectMapperProviderObjectProvider.getIfAvailable();

    if (objectMapperProvider != null) {
      builder.withObjectMapperProvider(objectMapperProvider);
    } else if (objectMapperConfigurer != null) {
      builder.withObjectMapperConfigurer(objectMapperConfigurer);
    }
    return builder.build();
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(
      value = "graphql.servlet.use-default-objectmapper",
      havingValue = "true",
      matchIfMissing = true)
  public ObjectMapperProvider objectMapperProvider(ObjectMapper objectMapper) {
    InjectableValues.Std injectableValues = new InjectableValues.Std();
    injectableValues.addValue(ObjectMapper.class, objectMapper);
    objectMapper.setInjectableValues(injectableValues);
    return () -> objectMapper;
  }

  @Bean
  @ConditionalOnMissingBean
  public GraphQLConfiguration graphQLServletConfiguration(
      GraphQLInvocationInputFactory invocationInputFactory,
      GraphQLInvoker graphQLInvoker,
      GraphQLObjectMapper graphQLObjectMapper,
      @Autowired(required = false) List<GraphQLServletListener> listeners,
      @Autowired(required = false) BatchInputPreProcessor batchInputPreProcessor,
      @Autowired(required = false) GraphQLResponseCacheManager responseCacheManager) {
    return GraphQLConfiguration.with(invocationInputFactory)
        .with(graphQLInvoker)
        .with(graphQLObjectMapper)
        .with(listeners)
        .with(graphQLServletProperties.getSubscriptionTimeout())
        .with(batchInputPreProcessor)
        .with(graphQLServletProperties.getContextSetting())
        .with(responseCacheManager)
        .asyncTimeout(graphQLServletProperties.getAsyncTimeout())
        .build();
  }

  @Bean
  @ConditionalOnMissingBean
  public GraphQLHttpServlet graphQLHttpServlet(GraphQLConfiguration graphQLConfiguration) {
    return GraphQLHttpServlet.with(graphQLConfiguration);
  }

  @Bean
  public ServletRegistrationBean<AbstractGraphQLHttpServlet> graphQLServletRegistrationBean(
      AbstractGraphQLHttpServlet servlet,
      @Autowired(required = false) MultipartConfigElement multipartConfigElement) {
    ServletRegistrationBean<AbstractGraphQLHttpServlet> registration =
        new ServletRegistrationBean<>(servlet, graphQLServletProperties.getServletMapping());
    if (multipartConfigElement != null) {
      registration.setMultipartConfig(multipartConfigElement);
    } else {
      registration.setMultipartConfig(new MultipartConfigElement(""));
    }
    registration.setAsyncSupported(graphQLServletProperties.isAsyncModeEnabled());
    return registration;
  }
}
