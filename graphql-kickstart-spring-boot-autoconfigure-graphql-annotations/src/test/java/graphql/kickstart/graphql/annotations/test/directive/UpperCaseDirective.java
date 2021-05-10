package graphql.kickstart.graphql.annotations.test.directive;

import graphql.annotations.annotationTypes.GraphQLDescription;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.directives.definition.DirectiveLocations;
import graphql.annotations.annotationTypes.directives.definition.GraphQLDirectiveDefinition;
import graphql.introspection.Introspection;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@GraphQLName("upperCase")
@GraphQLDescription("This directive makes a string uppercase")
@GraphQLDirectiveDefinition(wiring = UpperCaseDirectiveWiring.class)
@DirectiveLocations({Introspection.DirectiveLocation.FIELD_DEFINITION})
@Retention(RetentionPolicy.RUNTIME)
public @interface UpperCaseDirective {}
