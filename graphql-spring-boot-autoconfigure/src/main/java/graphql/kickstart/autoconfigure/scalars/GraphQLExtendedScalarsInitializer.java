package graphql.kickstart.autoconfigure.scalars;

import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.util.ReflectionUtils;

@NoArgsConstructor
public class GraphQLExtendedScalarsInitializer
    implements ApplicationContextInitializer<GenericApplicationContext> {

  @Override
  public void initialize(@NonNull final GenericApplicationContext applicationContext) {
    final Collection<String> enabledExtendedScalars = getEnabledExtendedScalars(applicationContext);
    final Collection<String> validScalarNames = new HashSet<>();
    ReflectionUtils.doWithFields(
        ExtendedScalars.class,
        scalarField -> {
          if (Modifier.isPublic(scalarField.getModifiers())
              && Modifier.isStatic(scalarField.getModifiers())
              && scalarField.getType().equals(GraphQLScalarType.class)) {
            final GraphQLScalarType graphQLScalarType = (GraphQLScalarType) scalarField.get(null);
            if (enabledExtendedScalars.contains(graphQLScalarType.getName())) {
              applicationContext.registerBean(
                  graphQLScalarType.getName(), GraphQLScalarType.class, () -> graphQLScalarType);
            }
            validScalarNames.add(graphQLScalarType.getName());
          }
        });
    verifyEnabledScalars(enabledExtendedScalars, validScalarNames);
  }

  private void verifyEnabledScalars(
      final Collection<String> enabledExtendedScalars, final Collection<String> validScalarNames) {
    final Collection<String> invalidScalarNames = new HashSet<>(enabledExtendedScalars);
    invalidScalarNames.removeAll(validScalarNames);
    if (!invalidScalarNames.isEmpty()) {
      throw new ApplicationContextException(
          String.format(
              "Invalid extended scalar name(s) found: %s. Valid names are: %s.",
              joinNames(invalidScalarNames), joinNames(validScalarNames)));
    }
  }

  private String joinNames(final Collection<String> names) {
    return names.stream().sorted().collect(Collectors.joining(", "));
  }

  @SuppressWarnings("unchecked")
  private Set<String> getEnabledExtendedScalars(
      final GenericApplicationContext applicationContext) {
    return (Set<String>)
        applicationContext
            .getEnvironment()
            .getProperty("graphql.extended-scalars", Collection.class, Collections.emptySet())
            .stream()
            .map(String::valueOf)
            .collect(Collectors.toSet());
  }
}
