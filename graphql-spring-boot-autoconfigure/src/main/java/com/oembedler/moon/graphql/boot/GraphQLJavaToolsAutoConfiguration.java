package com.oembedler.moon.graphql.boot;

import com.coxautodev.graphql.tools.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLSchema;
import graphql.servlet.config.GraphQLSchemaProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.List;

/**
 * @author Andrew Potter
 */
@Configuration
@ConditionalOnClass(SchemaParser.class)
@AutoConfigureAfter({JacksonAutoConfiguration.class})
@EnableConfigurationProperties(GraphQLToolsProperties.class)
public class GraphQLJavaToolsAutoConfiguration {

    @Autowired(required = false)
    private SchemaParserDictionary dictionary;

    @Autowired(required = false)
    private GraphQLScalarType[] scalars;

    @Autowired(required = false)
    private SchemaParserOptions options;

    @Autowired(required = false)
    private List<SchemaDirective> directives;

    @Autowired(required = false)
    private List<SchemaDirectiveWiring> directiveWirings;

    @Autowired(required = false)
    private List<TypeDefinitionFactory> typeDefinitionFactories;

    @Autowired
    private GraphQLToolsProperties props;

    @Bean
    @ConditionalOnMissingBean
    public SchemaStringProvider schemaStringProvider() {
        return new ClasspathResourceSchemaStringProvider(props.getSchemaLocationPattern());
    }

    @Bean
    @ConditionalOnBean({GraphQLResolver.class})
    @ConditionalOnMissingBean
    public SchemaParser schemaParser(
            List<GraphQLResolver<?>> resolvers,
            SchemaStringProvider schemaStringProvider,
            PerFieldObjectMapperProvider perFieldObjectMapperProvider
    ) throws IOException {
        SchemaParserBuilder builder = dictionary != null ? new SchemaParserBuilder(dictionary) : new SchemaParserBuilder();

        List<String> schemaStrings = schemaStringProvider.schemaStrings();
        schemaStrings.forEach(builder::schemaString);

        if (scalars != null) {
            builder.scalars(scalars);
        }

        // fixme: should we even support options directly like this? the combination with the builder makes it complex
        if (options != null) {
            builder.options(options);
        } else {
            SchemaParserOptions.Builder optionsBuilder = SchemaParserOptions.newOptions();

            if (perFieldObjectMapperProvider != null) {
                optionsBuilder.objectMapperProvider(perFieldObjectMapperProvider);
            }
            optionsBuilder.introspectionEnabled(props.isIntrospectionEnabled());

            if (typeDefinitionFactories != null) {
                typeDefinitionFactories.forEach(optionsBuilder::typeDefinitionFactory);
            }

            builder.options(optionsBuilder.build());
        }

        if (directives != null) {
            directives.forEach(it -> builder.directive(it.getName(), it.getDirective()));
        }

        if (directingWirings != null) {
            directingWirings.forEach(it -> builder.directiveWiring(it));
        }

        return builder
                .resolvers(resolvers)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "graphql.tools.use-default-objectmapper", havingValue = "true", matchIfMissing = true)
    public PerFieldObjectMapperProvider perFieldObjectMapperProvider(ObjectMapper objectMapper) {
        objectMapper
                .registerModule(new Jdk8Module())
                .registerModule(new KotlinModule());
        return fieldDefinition -> objectMapper;
    }


    @Bean
    @ConditionalOnBean(SchemaParser.class)
    @ConditionalOnMissingBean({GraphQLSchema.class, GraphQLSchemaProvider.class})
    public GraphQLSchema graphQLSchema(SchemaParser schemaParser) {
        return schemaParser.makeExecutableSchema();
    }
}
