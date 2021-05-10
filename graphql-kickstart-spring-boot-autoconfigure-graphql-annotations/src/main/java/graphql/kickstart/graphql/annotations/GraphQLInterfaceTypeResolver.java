package graphql.kickstart.graphql.annotations;

import graphql.TypeResolutionEnvironment;
import graphql.annotations.processor.GraphQLAnnotations;
import graphql.schema.GraphQLObjectType;
import graphql.schema.TypeResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Type resolver for GraphQL interfaces.
 *
 * @see <a href="https://github.com/Enigmatis/graphql-java-annotations/issues/100">Issue with
 *     workaround.</a>
 *     <p>Apply this interface to GraphQL interfaces using the {@link
 *     graphql.annotations.annotationTypes.GraphQLTypeResolver} annotation.
 */
public class GraphQLInterfaceTypeResolver implements TypeResolver, ApplicationContextAware {

  private static GraphQLAnnotations graphQLAnnotations;

  @Override
  public GraphQLObjectType getType(final TypeResolutionEnvironment env) {
    return graphQLAnnotations.object(env.getObject().getClass());
  }

  @Override
  public void setApplicationContext(final ApplicationContext applicationContext) {
    graphQLAnnotations = applicationContext.getBean(GraphQLAnnotations.class);
  }
}
