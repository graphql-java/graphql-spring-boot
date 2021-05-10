package graphql.kickstart.graphql.annotations;

import static java.util.Objects.nonNull;

import graphql.annotations.processor.ProcessingElementsContainer;
import graphql.annotations.processor.typeFunctions.TypeFunction;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Maps Java classes to the corresponding scalar definitions. */
@RequiredArgsConstructor
@Slf4j
public class GraphQLScalarTypeFunction implements TypeFunction {

  private final List<GraphQLScalarType> customScalarTypes;

  @Override
  public boolean canBuildType(final Class<?> aClass, final AnnotatedType annotatedType) {
    return getMatchingScalarDefinition(aClass).isPresent();
  }

  @Override
  public GraphQLType buildType(
      final boolean input,
      final Class<?> aClass,
      final AnnotatedType annotatedType,
      final ProcessingElementsContainer container) {
    final GraphQLScalarType graphQLScalarType = getMatchingScalarDefinition(aClass).orElse(null);
    if (nonNull(graphQLScalarType)) {
      log.info("Registering scalar type {} for Java class {}", graphQLScalarType.getName(), aClass);
    }
    return graphQLScalarType;
  }

  private Optional<GraphQLScalarType> getMatchingScalarDefinition(final Class<?> aClass) {
    return customScalarTypes.stream()
        .filter(
            scalarType -> {
              final Type[] genericInterfaces =
                  scalarType.getCoercing().getClass().getGenericInterfaces();
              return genericInterfaces.length > 0
                  && genericInterfaces[0] instanceof ParameterizedType
                  && ((ParameterizedType) genericInterfaces[0]).getActualTypeArguments().length > 0
                  && ((ParameterizedType) genericInterfaces[0])
                      .getActualTypeArguments()[0].equals(aClass);
            })
        .findFirst();
  }
}
