package graphql.kickstart.autoconfigure.scalars;

import graphql.schema.GraphQLScalarType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.ReflectionUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GraphQLScalarUtils {

  /**
   * Extract scalar field definitions from helper classes. Public static
   * {@link GraphQLScalarType} fields are considered as scalar definitions.
   *
   * @param classes  classes that may contain scalar definitions.
   * @return the map of scalar definitions (keys = scalar names, values are scalar type definitions).
   * May return an empty map if no definitions found. If multiple source classes define GraphQL
   * scalar types with the same definition, then the last one will be included in the map.
   */
  public static Map<String, GraphQLScalarType> extractScalarDefinitions(final Class<?>... classes) {
    final Map<String, GraphQLScalarType> scalarTypes = new HashMap<>();
    Stream.of(classes).forEach(clazz -> extractScalarField(clazz, scalarTypes));
    return scalarTypes;
  }

  private static void extractScalarField(Class<?> clazz, Map<String, GraphQLScalarType> target) {
    ReflectionUtils.doWithFields(clazz, scalarField -> extractedIfScalarField(target, scalarField));
  }

  private static void extractedIfScalarField(Map<String, GraphQLScalarType> target, Field field)
      throws IllegalAccessException {
    if (Modifier.isPublic(field.getModifiers())
        && Modifier.isStatic(field.getModifiers())
        && field.getType().equals(GraphQLScalarType.class)) {
      final GraphQLScalarType graphQLScalarType = (GraphQLScalarType) field.get(null);
      target.put(graphQLScalarType.getName(), graphQLScalarType);
    }
  }
}
