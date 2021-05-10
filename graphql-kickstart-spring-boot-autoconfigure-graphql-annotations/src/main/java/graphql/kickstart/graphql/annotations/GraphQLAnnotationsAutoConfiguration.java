package graphql.kickstart.graphql.annotations;

import static graphql.annotations.AnnotationsSchemaCreator.newAnnotationsSchema;
import static java.util.Objects.nonNull;

import graphql.annotations.AnnotationsSchemaCreator;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLTypeExtension;
import graphql.annotations.annotationTypes.directives.definition.GraphQLDirectiveDefinition;
import graphql.annotations.processor.GraphQLAnnotations;
import graphql.annotations.processor.typeFunctions.TypeFunction;
import graphql.kickstart.graphql.annotations.exceptions.MissingQueryResolverException;
import graphql.kickstart.graphql.annotations.exceptions.MultipleMutationResolversException;
import graphql.kickstart.graphql.annotations.exceptions.MultipleQueryResolversException;
import graphql.kickstart.graphql.annotations.exceptions.MultipleSubscriptionResolversException;
import graphql.relay.Relay;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLSchema;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.ReflectionsException;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GraphQLAnnotationsProperties.class)
@RequiredArgsConstructor
@Slf4j
public class GraphQLAnnotationsAutoConfiguration {

  private final GraphQLAnnotationsProperties graphQLAnnotationsProperties;
  private final Optional<Relay> relay;
  private final List<TypeFunction> typeFunctions;
  private final List<GraphQLScalarType> customScalarTypes;

  @Bean
  public GraphQLInterfaceTypeResolver graphQLInterfaceTypeResolver() {
    return new GraphQLInterfaceTypeResolver();
  }

  @Bean
  @ConditionalOnMissingBean
  public GraphQLAnnotations graphQLAnnotations() {
    GraphQLAnnotations graphQLAnnotations = new GraphQLAnnotations();
    if (nonNull(graphQLAnnotationsProperties.getInputPrefix())) {
      graphQLAnnotations
          .getContainer()
          .setInputPrefix(graphQLAnnotationsProperties.getInputPrefix());
    }
    if (nonNull(graphQLAnnotationsProperties.getInputSuffix())) {
      graphQLAnnotations
          .getContainer()
          .setInputSuffix(graphQLAnnotationsProperties.getInputSuffix());
    }
    return graphQLAnnotations;
  }

  @Bean
  public GraphQLSchema graphQLSchema(final GraphQLAnnotations graphQLAnnotations) {
    log.info(
        "Using GraphQL Annotations library to build the schema. Schema definition files will be ignored.");
    log.info(
        "GraphQL classes are searched in the following package (including subpackages): {}",
        graphQLAnnotationsProperties.getBasePackage());
    final AnnotationsSchemaCreator.Builder builder = newAnnotationsSchema();
    final Reflections reflections =
        new Reflections(
            graphQLAnnotationsProperties.getBasePackage(),
            new MethodAnnotationsScanner(),
            new SubTypesScanner(),
            new TypeAnnotationsScanner());
    builder.setAlwaysPrettify(graphQLAnnotationsProperties.isAlwaysPrettify());
    setQueryResolverClass(builder, reflections);
    setMutationResolverClass(builder, reflections);
    setSubscriptionResolverClass(builder, reflections);
    getTypesAnnotatedWith(reflections, GraphQLDirectiveDefinition.class)
        .forEach(
            directive -> {
              log.info("Registering directive {}", directive);
              builder.directive(directive);
            });
    getTypesAnnotatedWith(reflections, GraphQLTypeExtension.class)
        .forEach(
            typeExtension -> {
              log.info("Registering type extension {}", typeExtension);
              builder.typeExtension(typeExtension);
            });
    typeFunctions.forEach(
        typeFunction -> {
          log.info("Registering type function {}", typeFunction.getClass());
          builder.typeFunction(typeFunction);
        });
    if (!customScalarTypes.isEmpty()) {
      builder.typeFunction(new GraphQLScalarTypeFunction(customScalarTypes));
    }
    if (graphQLAnnotations.getClass().equals(GraphQLAnnotations.class)) {
      log.info("Using default GraphQL Annotation processor.");
    } else {
      log.info("Using custom annotation process of type {}", graphQLAnnotations.getClass());
    }
    builder.setAnnotationsProcessor(graphQLAnnotations);
    relay.ifPresent(
        r -> {
          log.info("Registering relay {}", r.getClass());
          builder.setRelay(r);
        });
    registerGraphQLInterfaceImplementations(reflections, builder);
    return builder.build();
  }

  private void setSubscriptionResolverClass(
      final AnnotationsSchemaCreator.Builder builder, final Reflections reflections) {
    final Set<Class<?>> subscriptionResolvers =
        getTypesAnnotatedWith(reflections, GraphQLSubscriptionResolver.class);
    if (subscriptionResolvers.size() > 1) {
      throw new MultipleSubscriptionResolversException();
    }
    subscriptionResolvers.stream()
        .findFirst()
        .ifPresent(
            subscriptionClass -> {
              log.info("Registering subscription resolver class: {}", subscriptionClass);
              builder.subscription(subscriptionClass);
            });
  }

  private void setMutationResolverClass(
      final AnnotationsSchemaCreator.Builder builder, final Reflections reflections) {
    final Set<Class<?>> mutationResolvers =
        getTypesAnnotatedWith(reflections, GraphQLMutationResolver.class);
    if (mutationResolvers.size() > 1) {
      throw new MultipleMutationResolversException();
    }
    mutationResolvers.stream()
        .findFirst()
        .ifPresent(
            mutationClass -> {
              log.info("Registering mutation resolver class: {}", mutationClass);
              builder.mutation(mutationClass);
            });
  }

  private void setQueryResolverClass(
      final AnnotationsSchemaCreator.Builder builder, final Reflections reflections) {
    final Set<Class<?>> queryResolvers =
        getTypesAnnotatedWith(reflections, GraphQLQueryResolver.class);
    if (queryResolvers.isEmpty()) {
      throw new MissingQueryResolverException();
    }
    if (queryResolvers.size() > 1) {
      throw new MultipleQueryResolversException();
    }
    queryResolvers.stream()
        .findFirst()
        .ifPresent(
            queryClass -> {
              log.info("Registering query resolver class: {}", queryClass);
              builder.query(queryClass);
            });
  }

  /**
   * Workaround for a bug in Reflections - {@link Reflections#getTypesAnnotatedWith)} will throw a
   * {@link ReflectionsException} if there are no types with annotations in the specified package.
   *
   * @param reflections the {@link Reflections} instance
   * @param annotation the annotation class
   * @return The set of classes annotated with the specified annotation, or an empty set if no
   *     annotated classes found.
   */
  private Set<Class<?>> getTypesAnnotatedWith(
      final Reflections reflections, final Class<? extends Annotation> annotation) {
    try {
      return reflections.getTypesAnnotatedWith(annotation);
    } catch (ReflectionsException e) {
      return Collections.emptySet();
    }
  }

  /**
   * This is required, because normally implementations of interfaces are not explicitly returned by
   * any resolver method, and therefor not added to the schema automatically.
   *
   * <p>All interfaces are considered GraphQL interfaces if they are declared in the configured
   * package and have at least one {@link GraphQLField}-annotated methods.
   *
   * @param reflections the reflections instance.
   * @param builder the schema builder instance.
   */
  private void registerGraphQLInterfaceImplementations(
      final Reflections reflections, final AnnotationsSchemaCreator.Builder builder) {
    Predicate<Class<?>> implementationQualifiesForInclusion =
        type ->
            !(graphQLAnnotationsProperties.isIgnoreAbstractInterfaceImplementations()
                && Modifier.isAbstract(type.getModifiers()));
    reflections.getMethodsAnnotatedWith(GraphQLField.class).stream()
        .map(Method::getDeclaringClass)
        .filter(Class::isInterface)
        .forEach(
            graphQLInterface ->
                reflections.getSubTypesOf(graphQLInterface).stream()
                    .filter(implementationQualifiesForInclusion)
                    .forEach(
                        implementation -> {
                          log.info(
                              "Registering {} as an implementation of GraphQL interface {}",
                              implementation,
                              graphQLInterface);
                          builder.additionalType(implementation);
                        }));
  }
}
