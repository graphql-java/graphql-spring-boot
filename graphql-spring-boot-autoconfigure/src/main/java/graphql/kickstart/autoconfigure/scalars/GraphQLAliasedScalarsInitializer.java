package graphql.kickstart.autoconfigure.scalars;

import static graphql.kickstart.autoconfigure.scalars.GraphQLScalarUtils.extractScalarDefinitions;

import graphql.Scalars;
import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;

@RequiredArgsConstructor
public class GraphQLAliasedScalarsInitializer
    implements ApplicationContextInitializer<GenericApplicationContext> {

  private static final String GRAPHQL_ALIASED_SCALAR_PREFIX = "graphql.aliased-scalars.";
  private static final String JOINING_SEPARATOR = ", ";
  private static final String NO_BUILT_IN_SCALAR_FOUND
      = "Scalar(s) '%s' cannot be aliased. "
      + "Only the following scalars can be aliased by configuration: %s. "
      + "Note that custom scalar beans cannot be aliased this way.";

  @Override
  public void initialize(@NonNull final GenericApplicationContext applicationContext) {
      final Map<String, GraphQLScalarType> predefinedScalars
          = extractScalarDefinitions(Scalars.class, ExtendedScalars.class);
      final ConfigurableEnvironment environment = applicationContext.getEnvironment();
      verifyAliasedScalarConfiguration(predefinedScalars, environment);
      predefinedScalars.forEach((scalarName, scalarType) ->
          ((List<?>) environment.getProperty(GRAPHQL_ALIASED_SCALAR_PREFIX + scalarName,
              List.class, Collections.emptyList()))
          .stream()
          .map(String::valueOf)
          .map(alias -> ExtendedScalars.newAliasedScalar(alias).aliasedScalar(scalarType).build())
          .forEach(aliasedScalar -> applicationContext.registerBean(aliasedScalar.getName(),
              GraphQLScalarType.class, () -> aliasedScalar)));
    }

  private void verifyAliasedScalarConfiguration(
      final Map<String, GraphQLScalarType> predefinedScalars,
      final ConfigurableEnvironment environment) {
    final List<String> invalidScalars = environment.getPropertySources().stream()
        .filter(pSource -> pSource instanceof EnumerablePropertySource)
        .map(pSource -> (EnumerablePropertySource<?>) pSource)
        .map(EnumerablePropertySource::getPropertyNames)
        .flatMap(Arrays::stream)
        .filter(pName -> pName.startsWith(GRAPHQL_ALIASED_SCALAR_PREFIX))
        .map(pName -> pName.replace(GRAPHQL_ALIASED_SCALAR_PREFIX, ""))
        .filter(scalarName -> !predefinedScalars.containsKey(scalarName))
        .sorted()
        .collect(Collectors.toList());
    if (!invalidScalars.isEmpty()) {
      final String validBuildInScalars = predefinedScalars.keySet().stream().sorted()
          .collect(Collectors.joining(JOINING_SEPARATOR));
      throw new ApplicationContextException(String.format(NO_BUILT_IN_SCALAR_FOUND,
          String.join(JOINING_SEPARATOR, invalidScalars), validBuildInScalars));
    }
  }
}
