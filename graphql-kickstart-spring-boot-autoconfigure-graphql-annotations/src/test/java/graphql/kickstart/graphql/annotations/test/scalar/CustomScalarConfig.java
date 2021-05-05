package graphql.kickstart.graphql.annotations.test.scalar;

import static graphql.schema.GraphQLScalarType.newScalar;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import java.util.UUID;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("custom-scalar-test")
@Configuration
@NoArgsConstructor
public class CustomScalarConfig {

  @Bean
  public GraphQLScalarType uuidScalar() {
    return newScalar()
        .name("UUID")
        .description("Standard UUID")
        .coercing(
            new Coercing<UUID, String>() {
              @Override
              public String serialize(Object dataFetcherResult) throws CoercingSerializeException {
                return String.valueOf(dataFetcherResult);
              }

              @Override
              public UUID parseValue(Object input) throws CoercingParseValueException {
                try {
                  return UUID.fromString(String.valueOf(input));
                } catch (IllegalArgumentException e) {
                  throw new CoercingParseValueException(e);
                }
              }

              @Override
              public UUID parseLiteral(Object input) throws CoercingParseLiteralException {
                try {
                  return UUID.fromString(((StringValue) input).getValue());
                } catch (IllegalArgumentException e) {
                  throw new CoercingParseLiteralException(e);
                }
              }
            })
        .build();
  }
}
