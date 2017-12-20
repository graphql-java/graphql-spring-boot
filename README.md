# GraphQL and Graph*i*QL Spring Framework Boot Starters
[![Build Status](https://travis-ci.org/graphql-java/graphql-spring-boot.svg?branch=master)](https://travis-ci.org/graphql-java/graphql-spring-boot)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.graphql-java/graphql-spring-boot-starter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.graphql-java/graphql-spring-boot-starter)
[![Chat on Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/graphql-java/graphql-java)

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**

- [Intro](#intro)
- [Requirements and Downloads](#requirements-and-downloads)
- [Enable GraphQL Servlet](#enable-graphql-servlet)
- [Enable GraphiQL](#enable-graphiql)
- [Supported GraphQL-Java Libraries](#supported-graphql-java-libraries)
    - [GraphQL Java Tools](#graphql-java-tools) - [https://github.com/graphql-java/graphql-java-tools](https://github.com/graphql-java/graphql-java-tools)
    - [GraphQL Spring Common](#graphql-spring-common) - [https://github.com/oembedler/spring-graphql-common](https://github.com/oembedler/spring-graphql-common)
- [Contributions](#contributions)
- [Licenses](#licenses)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->


# Intro

Repository contains:

* `graphql-spring-boot-starter` to turn your boot application into GraphQL server (see [graphql-java-servlet](https://github.com/graphql-java/graphql-java-servlet))
* `graphiql-spring-boot-starter`to embed `GraphiQL` tool for schema introspection and query debugging (see [graphiql](https://github.com/graphql/graphiql))

# Requirements and Downloads

Requirements:
  * Java 1.8
  * Spring Framework Boot > 1.3.x (web)

Gradle:

```gradle
repositories {
    mavenCentral()
}

dependencies {
  compile 'com.graphql-java:graphql-spring-boot-starter:3.9.2'
  
  // to embed GraphiQL tool
  compile 'com.graphql-java:graphiql-spring-boot-starter:3.9.2'
}
```

Maven:
```xml
<dependency>
    <groupId>com.graphql-java</groupId>
    <artifactId>graphql-spring-boot-starter</artifactId>
    <version>3.10.0</version>
</dependency>

<!-- to embed GraphiQL tool -->
<dependency>
    <groupId>com.graphql-java</groupId>
    <artifactId>graphiql-spring-boot-starter</artifactId>
    <version>3.10.0</version>
</dependency>
```


# Enable GraphQL Servlet

The servlet becomes accessible at `/graphql` if `graphql-spring-boot-starter` added as a dependency to a boot application and a `GraphQLSchema` bean is present in the application.  Check out the [simple example](https://github.com/graphql-java/graphql-spring-boot/tree/master/example) for the bare minimum required.

A GraphQL schema can also be automatically created when a [supported graphql-java schema library](https://github.com/graphql-java/graphql-spring-boot/blob/master/README.md#supported-graphql-java-libraries) is found on the classpath.

See the [graphql-java-servlet usage docs](https://github.com/graphql-java/graphql-java-servlet#usage) for the avaiable endpoints exposed.

Available Spring Boot configuration parameters (either `application.yml` or `application.properties`):

```yaml
graphql:
      servlet:
               mapping: /graphql
               enabled: true
               corsEnabled: true
```

By default a global CORS filter is enabled for `/graphql/**` context.
The `corsEnabled` can be set to `false` to disable it.

# Enable Graph*i*QL

Graph*i*QL becomes accessible at the root `/graphiql` if `graphiql-spring-boot-starter` is added as a dependency to a boot application.

Note that GraphQL server must be available at `/graphql/*` context to be discovered by Graph*i*QL.

Available Spring Boot configuration parameters (either `application.yml` or `application.properties`):
```yaml
graphiql:
    mapping: /graphiql
    endpoint: /graphql
    enabled: true
```

# Supported GraphQL-Java Libraries

The following libraries have auto-configuration classes for creating a `GraphQLSchema`.

## GraphQL Java Tools
**https://github.com/graphql-java/graphql-java-tools**

All `GraphQLResolver` and `GraphQLScalar` beans, along with a bean of type `SchemaParserDictionary` (to provide all other classes), will be used to create a GraphQLSchema.  Any files on the classpath named `*.graphqls` will be used to provide the schema definition.  See the [Readme](https://github.com/graphql-java/graphql-java-tools#usage) for more info.

## GraphQL Spring Common [LATEST SUPPORTED VERSION: 3.1.1]
**https://github.com/oembedler/spring-graphql-common**

See the [Readme](https://github.com/oembedler/spring-graphql-common#usage) and the [example](https://github.com/graphql-java/graphql-spring-boot/tree/master/example-spring-common) for usage instructions.

#### Application Properties
```
graphql:
      spring-graphql-common:
               clientMutationIdName: clientMutationId
               injectClientMutationId: true
               allowEmptyClientMutationId: false
               mutationInputArgumentName: input
               outputObjectNamePrefix: Payload
               inputObjectNamePrefix: Input
               schemaMutationObjectName: Mutation
```


# Contributions

Contributions are welcome.  Please respect the [Code of Conduct](http://contributor-covenant.org/version/1/3/0/).


# Licenses

`graphql-spring-boot-starter` and `graphiql-spring-boot-starter` are licensed under the MIT License. See [LICENSE](LICENSE.md) for details.

[spring-graphql-common License](https://github.com/oembedler/spring-graphql-common/blob/master/LICENSE.md)

[graphql-java License](https://github.com/andimarek/graphql-java/blob/master/LICENSE.md)

[graphiql License](https://github.com/graphql/graphiql/blob/master/LICENSE)

[graphql-js License](https://github.com/graphql/graphql-js/blob/master/LICENSE)
