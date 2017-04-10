<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**

- [GraphQL and GraphiQL Spring Framework Boot Starters](#graphql-and-graphiql-spring-framework-boot-starters)
- [Intro](#intro)
- [Requires](#requires)
- [Enable GraphQL Server](#enable-graphql-server)
- [Enable GraphiQL Tool](#enable-graphiql-tool)
- [Contributions](#contributions)
- [License](#license)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# GraphQL and GraphiQL Spring Framework Boot Starters
[![Build Status](https://travis-ci.org/graphql-java/graphql-spring-boot.svg?branch=master)](https://travis-ci.org/graphql-java/graphql-spring-boot)
[![Chat on Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/graphql-java/graphql-java)

GraphQL Starter 

[ ![Download](https://api.bintray.com/packages/graphql-java/maven/graphql-spring-boot-starter/images/download.svg) ](https://bintray.com/graphql-java/maven/graphql-spring-boot-starter/_latestVersion)

GraphiQL Starter 

[ ![Download](https://api.bintray.com/packages/graphql-java/maven/graphiql-spring-boot-starter/images/download.svg) ](https://bintray.com/graphql-java/maven/graphiql-spring-boot-starter/_latestVersion)

# Intro

Repository contains:

* `graphql-spring-boot-starter` to turn your boot application into GraphQL server (see [graphql-java-servlet](https://github.com/graphql-java/graphql-java-servlet))
* `graphiql-spring-boot-starter`to embed `GraphiQL` tool for schema introspection and query debugging (see [graphiql](https://github.com/graphql/graphiql))

# Requires

  * Java 1.8
  * Spring Framework Boot > 1.3.x (web)  

Add repository:

```gradle
repositories {
    // stable build
    jcenter()
    // development build
    maven { url  "http://dl.bintray.com/oembedler/maven" }
}
```

Dependency:

```gradle
dependencies {
  compile 'com.embedler.moon.graphql.boot:graphql-spring-boot-starter:INSERT_LATEST_VERSION_HERE'
  
  // to embed GraphiQL tool
  compile 'com.embedler.moon.graphql.boot:graphiql-spring-boot-starter:INSERT_LATEST_VERSION_HERE'
}
```

How to use the latest build with Maven:

```xml
<repository>
    <snapshots>
        <enabled>false</enabled>
    </snapshots>
    <id>bintray-oembedler-maven</id>
    <name>bintray</name>
    <url>http://dl.bintray.com/oembedler/maven</url>
</repository>
```

Dependency:

```xml
<dependency>
    <groupId>com.embedler.moon.graphql.boot.starter</groupId>
    <artifactId>graphql-spring-boot-starter</artifactId>
    <version>INSERT_LATEST_VERSION_HERE</version>
</dependency>

<!-- to embed GraphiQL tool -->
<dependency>
    <groupId>com.embedler.moon.graphql.boot.starter</groupId>
    <artifactId>graphiql-spring-boot-starter</artifactId>
    <version>INSERT_LATEST_VERSION_HERE</version>
</dependency>
```


# Enable GraphQL Server

Server becomes accessible at `/graphql` if `graphql-spring-boot-starter` added as a dependency to a boot application.
A GraphQL schema is automatically discovered extracting classes from Spring context marked as `@GraphQLSchema`.

See the [graphql-java-servlet usage docs](https://github.com/graphql-java/graphql-java-servlet#usage) for the avaiable endpoints exposed.

Available Spring Boot configuration parameters (either `application.yml` or `application.properties`):

```yaml
graphql:
      servlet:
               mapping: /graphql
               enabled: true
               corsEnabled: true
               uploadMaxFileSize: 128KB
               uploadMaxRequestSize: 128KB

      spring-graphql-common:
               clientMutationIdName: clientMutationId
               injectClientMutationId: true
               allowEmptyClientMutationId: false
               mutationInputArgumentName: input
               outputObjectNamePrefix: Payload
               inputObjectNamePrefix: Input
               schemaMutationObjectName: Mutation
```

By default system enables global CORS filter for `/graphql/**` context.
The `corsEnabled` can be set to `false` to disable it.

# Enable GraphiQL Tool

GraphiQL becomes accessible at the root `/` if `graphiql-spring-boot-starter` added as a dependency to a boot application.

Note that GraphQL server must be available at `/graphql` context to be discovered by GraphiQL.

# Contributions

Contributions are welcome.

Tips:

- Respect the [Code of Conduct](http://contributor-covenant.org/version/1/3/0/).
- Before opening an Issue to report a bug, please try the latest development version. 
It might happen that the problem is already solved.
- Please use  Markdown to format your comments properly. 
If you are not familiar with that: [Getting started with writing and formatting on GitHub](https://help.github.com/articles/getting-started-with-writing-and-formatting-on-github/)
- For Pull Requests:
  - Here are some [general tips](https://github.com/blog/1943-how-to-write-the-perfect-pull-request)
  - Please be a as focused and clear as possible and don't mix concerns. 
    This includes refactorings mixed with bug-fixes/features, see [Open Source Contribution Etiquette](http://tirania.org/blog/archive/2010/Dec-31.html) 
  - It would be good to add an automatic test(s). 
  

# License

`graphql-spring-boot-starter` and `graphiql-spring-boot-starter` are licensed under the MIT License. See [LICENSE](LICENSE.md) for details.

[spring-graphql-common License](https://github.com/oembedler/spring-graphql-common/blob/master/LICENSE.md)

[graphql-java License](https://github.com/andimarek/graphql-java/blob/master/LICENSE.md)

[graphiql License](https://github.com/graphql/graphiql/blob/master/LICENSE)

[graphql-js License](https://github.com/graphql/graphql-js/blob/master/LICENSE)
