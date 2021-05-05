package graphql.kickstart.graphql.annotations.test.custom.annotation.processor;

import graphql.annotations.processor.GraphQLAnnotations;
import graphql.annotations.processor.retrievers.GraphQLExtensionsHandler;
import graphql.annotations.processor.retrievers.GraphQLObjectHandler;
import graphql.annotations.processor.typeFunctions.TypeFunction;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("custom-annotation-processor-test")
@Service
public class CustomAnnotationProcessor extends GraphQLAnnotations {

  public CustomAnnotationProcessor() {}

  public CustomAnnotationProcessor(
      TypeFunction defaultTypeFunction,
      GraphQLObjectHandler graphQLObjectHandler,
      GraphQLExtensionsHandler graphQLExtensionsHandler) {
    super(defaultTypeFunction, graphQLObjectHandler, graphQLExtensionsHandler);
  }
}
