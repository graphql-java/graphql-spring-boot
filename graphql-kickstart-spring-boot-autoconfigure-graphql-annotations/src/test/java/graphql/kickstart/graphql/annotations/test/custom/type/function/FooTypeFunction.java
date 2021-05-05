package graphql.kickstart.graphql.annotations.test.custom.type.function;

import static graphql.schema.GraphQLScalarType.newScalar;

import graphql.annotations.processor.ProcessingElementsContainer;
import graphql.annotations.processor.typeFunctions.TypeFunction;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLType;
import java.lang.reflect.AnnotatedType;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("custom-type-function-test")
public class FooTypeFunction implements TypeFunction {

  @Override
  public boolean canBuildType(final Class<?> aClass, final AnnotatedType annotatedType) {
    return aClass.equals(Foo.class);
  }

  @Override
  public GraphQLType buildType(
      boolean input,
      final Class<?> aClass,
      final AnnotatedType annotatedType,
      final ProcessingElementsContainer container) {
    return newScalar()
        .name("Foo")
        .coercing(
            new Coercing<Foo, String>() {
              @Override
              public String serialize(final Object dataFetcherResult)
                  throws CoercingSerializeException {
                return "foo";
              }

              @Override
              public Foo parseValue(final Object input) throws CoercingParseValueException {
                return new Foo();
              }

              @Override
              public Foo parseLiteral(final Object input) throws CoercingParseLiteralException {
                return new Foo();
              }
            })
        .build();
  }
}
