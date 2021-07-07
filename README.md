# GraphQL and Graph*i*QL Spring Framework Boot Starters

[![Maven Central](https://img.shields.io/maven-central/v/com.graphql-java-kickstart/graphql-spring-boot-starter.svg)](https://maven-badges.herokuapp.com/maven-central/com.graphql-java-kickstart/graphql-spring-boot-starter)
[![Sonatype Snapshot](https://img.shields.io/nexus/s/com.graphql-java-kickstart/graphql-spring-boot-starter?server=https%3A%2F%2Foss.sonatype.org)](#snapshots)
[![GitHub CI Workflow](https://github.com/graphql-java-kickstart/graphql-spring-boot/actions/workflows/ci.yml/badge.svg?branch=master)](https://github.com/graphql-java-kickstart/graphql-spring-boot/actions/workflows/ci.yml?query=workflow%3ACI+branch%3Amaster)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=graphql-java-kickstart_graphql-spring-boot&metric=alert_status)](https://sonarcloud.io/dashboard?id=graphql-java-kickstart_graphql-spring-boot)
[![GitHub contributors](https://img.shields.io/github/contributors/graphql-java-kickstart/graphql-spring-boot)](https://github.com/graphql-java-kickstart/graphql-spring-boot/graphs/contributors)
[![Discuss on GitHub](https://img.shields.io/badge/GitHub-discuss-orange)](https://github.com/graphql-java-kickstart/graphql-spring-boot/discussions)

#### We are looking for contributors!

Are you interested in improving our documentation, working on the codebase, reviewing PRs?

[Reach out to us on Discussions](https://github.com/graphql-java-kickstart/graphql-spring-boot/discussions)
and join the team!


<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**

- [Quick start](#quick-start)
  - [Using Gradle](#using-gradle)
  - [Using Maven](#using-maven)
- [Documentation](#documentation)
- [Requirements and Downloads](#requirements-and-downloads)
  - [Snapshots](#snapshots)
- [Enable GraphQL Servlet](#enable-graphql-servlet)
- [Enable Graph*i*QL](#enable-graphiql)
- [Enable Altair](#enable-altair)
- [Enable GraphQL Playground](#enable-graphql-playground)
  - [Basic settings](#basic-settings)
  - [CDN](#cdn)
  - [Custom static resources](#custom-static-resources)
  - [Customizing GraphQL Playground](#customizing-graphql-playground)
  - [Tabs](#tabs)
- [Enable GraphQL Voyager](#enable-graphql-voyager)
  - [Basic settings](#graphql-voyager-basic-settings)
  - [CDN](#graphql-voyager-cdn)
  - [Customizing GraphQL Voyager](#customizing-graphql-voyager)
- [Supported GraphQL-Java Libraries](#supported-graphql-java-libraries)
  - [GraphQL Java Tools](#graphql-java-tools)
  - [GraphQL Annotations](#graphql-annotations)
    - [Configuration](#configuration)
    - [Root resolvers, directives, type extensions](#root-resolvers-directives-type-extensions)
    - [Interfaces](#interfaces)
    - [Custom scalars and type functions](#custom-scalars-and-type-functions)
    - [Custom Relay and GraphQL Annotation Processor](#custom-relay-and-graphql-annotation-processor)
  - [Extended scalars](#extended-scalars)
- [Tracing and Metrics](#tracing-and-metrics)
  - [Usage](#usage)
- [FAQs](#faqs)
  - [WARNING: NoClassDefFoundError when using GraphQL Java Tools > 5.4.x](#warning-noclassdeffounderror-when-using-graphql-java-tools--54x)

- [Contributions](#contributions)
- [Licenses](#licenses)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Quick start

### Using Gradle

Set the Kotlin version in your `gradle.properties`

```
kotlin.version=1.3.70
```

### Using Maven

Set the Kotlin version in your `<properties>` section

```xml

<properties>
  <kotlin.version>1.3.70</kotlin.version>
</properties>
```

## Documentation

See our new [Documentation](https://www.graphql-java-kickstart.com/spring-boot/).

## Requirements and Downloads

Requirements:

* Java 1.8
* Spring Framework Boot > 2.x.x (web)

Gradle:

```gradle
repositories {
    mavenCentral()
}

dependencies {
  implementation 'com.graphql-java-kickstart:graphql-spring-boot-starter:12.0.0'
  
  // testing facilities
  testImplementation 'com.graphql-java-kickstart:graphql-spring-boot-starter-test:12.0.0'
}
```

Maven:

```xml
<dependency>
  <groupId>com.graphql-java-kickstart</groupId>
  <artifactId>graphql-spring-boot-starter</artifactId>
  <version>12.0.0</version>
</dependency>

<!-- testing facilities -->
<dependency>
<groupId>com.graphql-java-kickstart</groupId>
<artifactId>graphql-spring-boot-starter-test</artifactId>
<version>12.0.0</version>
<scope>test</scope>
</dependency>

```

### Snapshots

```xml
<repositories>
  <repository>
    <id>osshr-snapshots</id>
    <name>osshr-sonatype-snapshots</name>
    <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
  </repository>
</repositories>
```

For gradle:

```groovy
repositories {
  maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
}
```

# Enable GraphQL Servlet

The servlet becomes accessible at `/graphql` if `graphql-spring-boot-starter` added as a dependency
to a boot application and a `GraphQLSchema` bean is present in the application. Check out
the [simple example](https://github.com/graphql-java-kickstart/samples/tree/master/editors)
for the bare minimum required.

A GraphQL schema can also be automatically created when
a [supported graphql-java schema library](https://github.com/graphql-java-kickstart/graphql-spring-boot/blob/master/README.md#supported-graphql-java-libraries)
is found on the classpath.

See
the [graphql-java-servlet usage docs](https://github.com/graphql-java-kickstart/graphql-java-servlet#usage)
for the avaiable endpoints exposed.

Available Spring Boot configuration parameters (either `application.yml`
or `application.properties`):

```yaml
graphql:
  servlet:
    # Sets if GraphQL servlet should be created and exposed. If not specified defaults to "true".
    enabled: true
    # Sets the path where GraphQL servlet will be exposed. If not specified defaults to "/graphql"
    mapping: /graphql
    cors-enabled: true
    cors:
      allowed-origins: http://some.domain.com
      allowed-methods: GET, HEAD, POST
    # if you want to @ExceptionHandler annotation for custom GraphQLErrors
    exception-handlers-enabled: true
    context-setting: PER_REQUEST_WITH_INSTRUMENTATION
    # Sets if asynchronous operations are supported for GraphQL requests. If not specified defaults to true.
    async-mode-enabled: true
```

By default a global CORS filter is enabled for `/graphql/**` context. The `corsEnabled` can be set
to `false` to disable it.

# Enable Graph*i*QL

Graph*i*QL becomes accessible at the root `/graphiql` if the `graphql.graphiql.enabled` property 
is true.

Note that GraphQL server must be available at `/graphql/*` context to be discovered by Graph*i*QL.

Available Spring Boot configuration parameters (either `application.yml`
or `application.properties`):

```yaml
graphiql:
  mapping: /graphiql
  endpoint:
    graphql: /graphql
    subscriptions: /subscriptions
  subscriptions:
    timeout: 30
    reconnect: false
  basePath: /
  enabled: true
  pageTitle: GraphiQL
  cdn:
    enabled: false
    version: latest
  props:
    resources:
      query: query.graphql
      defaultQuery: defaultQuery.graphql
      variables: variables.graphql
    variables:
      editorTheme: "solarized light"
  headers:
    Authorization: "Bearer <your-token>"
```

By default GraphiQL is served from within the package. This can be configured to be served from CDN
instead, by setting the property `graphiql.cdn.enabled` to `true`.

You are able to set the GraphiQL props as well. The `graphiql.props.variables` group can contain any
of the props as defined at [GraphiQL Usage](https://github.com/graphql/graphiql#usage). Since
setting (large) queries in the properties like this isn't very readable, you can use the properties
in the `graphiql.props.resources` group to set the classpath resources that should be loaded.

Headers that are used when sending the GraphiQL queries can be set by defining them in
the `graphiql.headers` group.

# Enable Altair

Altair becomes accessible at the root `/altair` if the `graphql.altair.enabled` property is true.

Note that GraphQL server must be available at `/graphql/*` context to be discovered by Altair.

Available Spring Boot configuration parameters (either `application.yml`
or `application.properties`):

```yaml
altair:
  enabled: true
  mapping: /altair
  subscriptions:
    timeout: 30
    reconnect: false
  static:
    base-path: /
  page-title: Altair
  cdn:
    enabled: false
    version: 4.0.2
  options:
    endpoint-url: /graphql
    subscriptions-endpoint: /subscriptions
    initial-settings:
      theme: dracula
    initial-headers:
      Authorization: "Bearer <your-token>"
  resources:
    initial-query: defaultQuery.graphql
    initial-variables: variables.graphql
    initial-pre-request-script: pre-request.graphql
    initial-post-request-script: post-request.graphql
```

By default Altair is served from within the package. This can be configured to be served from CDN
instead, by setting the property `graphql.altair.cdn.enabled` to `true`.

You are able to set the Altair options as well using the `graphql.altair.options` group. Since
setting (
large) queries in the properties like this isn't very readable, you can use the properties in
the `graphql.altair.resources` group to set the classpath resources that should be loaded.

# Enable GraphQL Playground

GraphQL Playground becomes accessible at root `/playground` (or as configured
in `graphql.playground.mapping`) if the `graphql.playground.enabled` property is true.

It uses an embedded `GraphQL Playground React`, in accordance to
the [official guide](https://github.com/prisma/graphql-playground#as-html-page), using the 'minimum
HTML' approach.

Available Spring Boot configuration parameters (either `application.yml`
or `application.properties`):

```yaml
graphql.playground:
  mapping: /playground
  endpoint: /graphql
  subscriptionEndpoint: /subscriptions
  staticPath.base: my-playground-resources-folder
  enabled: true
  pageTitle: Playground
  cdn:
    enabled: false
    version: latest
  settings:
    editor.cursorShape: line
    editor.fontFamily: "'Source Code Pro', 'Consolas', 'Inconsolata', 'Droid Sans Mono', 'Monaco', monospace"
    editor.fontSize: 14
    editor.reuseHeaders: true
    editor.theme: dark
    general.betaUpdates: false
    prettier.printWidth: 80
    prettier.tabWidth: 2
    prettier.useTabs: false
    request.credentials: omit
    schema.polling.enable: true
    schema.polling.endpointFilter: "*localhost*"
    schema.polling.interval: 2000
    schema.disableComments: true
    tracing.hideTracingResponse: true
  headers:
    headerFor: AllTabs
  tabs:
    - name: Example Tab
      query: classpath:exampleQuery.graphql
      headers:
        SomeHeader: Some value
      variables: classpath:variables.json
      responses:
        - classpath:exampleResponse1.json
        - classpath:exampleResponse2.json
```

## Basic settings

`mapping`, `endpoint` and `subscriptionEndpoint` will default to `/playground`, `/graphql`
and `/subscriptions`, respectively. Note that these values may not be empty.

`enabled` defaults to `true`, and therefor Playground will be available by default if the dependency
is added to a Spring Boot Web Application project.

`pageTitle` defaults to `Playground`.

`headers` allows you to specify headers for the default tab. Note that if your are using Spring
Security and CSRF is enabled CSRF, the CSRF token will be automatically added to the headers. These
headers will also be added to all the tabs configured under the [Tabs](#tabs) section. If a header
is defined both in this 'global' header list and the header list of the individual tabs, the 'local'
version will be used for that tab.

## CDN

The currently bundled version is `1.7.20`, which is - as of writing this - the latest release
of `GraphQL Playground React`. The CDN option uses `jsDelivr` CDN, if enabled. By default, it will
load the latest available release. Available CDN versions can be found on the project's
[jsDelivr page](https://www.jsdelivr.com/package/npm/graphql-playground-react). The CDN option is
disabled by default.

## Custom static resources

You can also specify a custom local version of Playground by setting the base path for `Playground`
resources in the `staticPath.base` property. Under this directory, you have to provide the following
files:

* `static/css/index.css`
* `static/js/middleware.js`
* `favicon.png`
* `logo.png`

This is identical to the directory structure of the CDN under the `build` subfolder (where these
files can be found).

## Customizing GraphQL Playground

Further GraphQL Playground settings can be specified under the `settings` group, which are
documented in the official
[GraphQL Playground readme](https://github.com/prisma/graphql-playground#settings). Note that
enum-like values are validated against the available options, and your application will not start if
wrong settings are provided. Similarly there is some basic validation for integer values (they must
be valid positive integers).

## Tabs

Optionally, you can specify tabs that will be present when the user first opens GraphQL Playground.
You can configure the query, variables, headers and even supply sample responses. Note that `query`
, `variables` and `responses` are expected to be resources of the appropriate format (GraphQL
for `query`, JSON for `variables` and `responses`).

# Enable GraphQL Voyager

**GraphQL Voyager** becomes accessible at root `/voyager` (or as configured in `voyager.mapping`)
if the `graphql.voyager.enabled` property is true.

Available Spring Boot configuration parameters (either `application.yml`
or `application.properties`):

```yaml
voyager:
  enabled: true
  basePath: /
  mapping: /voyager
  endpoint: /graphql
  cdn:
    enabled: false
    version: latest
  pageTitle: Voyager
  displayOptions:
    skipRelay: true
    skipDeprecated: true
    rootType: Query
    sortByAlphabet: false
    showLeafFields: true
    hideRoot: false
  hideDocs: false
  hideSettings: false
```

## GraphQL Voyager Basic settings

`mapping` and `endpoint` will default to `/voyager` and `/graphql`, respectively. Note that these
values may not be empty.

`enabled` defaults to `true`, and therefor **GraphQL Voyager** will be available by default if the
dependency is added to a Spring Boot Web Application project.

`pageTitle` defaults to `Voyager`.

All other properties default to the same as documented on the
official [GraphQL Voyager readme](https://github.com/APIs-guru/graphql-voyager#properties)

## GraphQL Voyager CDN

The currently bundled version is `1.0.0-rc31`, which is - as of writing this - the latest release
of **GraphQL Voyager**. The CDN option uses `jsDelivr` CDN, if enabled. By default, it will load the
latest available release. Available CDN versions can be found on the project's
[jsDelivr page](https://www.jsdelivr.com/package/npm/graphql-voyager). The CDN option is disabled by
default.

## Customizing GraphQL Voyager

Further **GraphQL Voyager** `displayOptions`, `hideDocs` and `hideSettings` customizations can be
configured, as documented in the official
[GraphQL Voyager readme](https://github.com/APIs-guru/graphql-voyager#properties).

# Supported GraphQL-Java Libraries

The following libraries have auto-configuration classes for creating a `GraphQLSchema`.

## GraphQL Java Tools

**https://github.com/graphql-java-kickstart/graphql-java-tools**

All `GraphQLResolver` and `GraphQLScalar` beans, along with a bean of
type `SchemaParserDictionary` (to provide all other classes), will be used to create a
GraphQLSchema. Any files on the classpath named `*.graphqls` will be used to provide the schema
definition. See the [Readme](https://github.com/graphql-java-kickstart/graphql-java-tools#usage) for
more info.

Available Spring Boot configuration parameters (either `application.yml`
or `application.properties`):

```yaml
graphql:
  tools:
    schema-location-pattern: "**/*.graphqls"
    # Enable or disable the introspection query. Disabling it puts your server in contravention of the GraphQL
    # specification and expectations of most clients, so use this option with caution
    introspection-enabled: true
```

By default GraphQL tools uses the location pattern `**/*.graphqls` to scan for GraphQL schemas on
the classpath. Use the `schemaLocationPattern` property to customize this pattern.

## GraphQL Annotations

https://github.com/Enigmatis/graphql-java-annotations

To use GraphQL Annotations library instead of GraphQL Java Tools, set the `graphql.schema-strategy`
property to `annotations`.

The schema will be built using the GraphQL Annotations library in a code-first approach - instead of
writing it manually, the schema will be constructed based on the Java code. Please see the
documentation of the GraphQL Annotations library for a detailed documentation of the available
annotations. This readme focuses on how GraphQL Annotations - GraphQL Spring Boot Starter
integration works.

### Configuration

```
graphql:
    annotations:
        base-package: com.example.graphl.schema # required
        always-prettify: true #true is the default value, no need to specify it
```

The most important parameter is the base package. The starter will look for schema-relevant classes
in the specified package and its subpackages. `always-prettify` will "prettify" getter/setter
methods - the get/set/is prefix will be removed from GraphQL fields automatically.

### Root resolvers, directives, type extensions

The root resolvers must be marked with the `GraphQLQueryResolver`, `GraphQLMutationResolver`
and `GraphQLSubscription`
annotations (not to be confused with the marker interfaces from the GraphQL Java Tools library).

**Important:**

Unlike GraphQL Java Tools, GraphQL Annotations only supports *one* of them each. Furthermore,
GraphQL Annotations only accepts a *class* as input, *not an instance*. It will either create a new
instance of the class itself, or use static methods. This means that Spring dependency injection
will not work in the usual way. The companion example project (which can be found in
the [samples](https://github.com/graphql-java-kickstart/samples) repository)
demonstrates possible workarounds for this issue.

`GraphQLDirectiveDefinition` and `GraphQLTypeExtension`-annotated classes are subject to the same
limitation regarding dependency injection - but there can be any number of them.

### Interfaces

Interfaces in the configured package having at least one of their methods marked as `@GraphQLField`
are considered a GraphQL interface, and their implementations are automatically added to the schema.
Furthermore, you have to add the following annotation to GraphQL
interfaces: `@GraphQLTypeResolver(GraphQLInterfaceTypeResolver.class)`

### Custom scalars and type functions

Custom scalars can be defined in the same way as in the case of using GraphQL Java Tools - just
define the
`GraphQLScalarType` beans.

The starter will also pick up `TypeFunction` beans and pass them to the schema builder.

In these cases the actual beans will be used, not just the classes. Spring dependency injection
works as usual.

### Custom Relay and GraphQL Annotation Processor

It is possible to define a bean implementing `Relay` and/or `GraphQLAnnotations`. If present, these
will be passed to the schema builder. Spring dependency injection works as usual. Note that GraphQL
Annotations provides default implementation for these which should be sufficient is most cases.

## Extended scalars

[Extended scalars](https://github.com/graphql-java/graphql-java-extended-scalars) can be enabled by
using the
`graphql.extended-scalars` configuration property, e. g.:

```yaml
graphql:
  extended-scalars: BigDecimal, Date
```

The available scalars are the following: `BigDecimal`, `BigInteger`, `Byte`, `Char`, `Date`
, `DateTime`, `JSON`,
`Locale`, `Long`, `NegativeFloat`, `NegativeInt`, `NonNegativeFloat`, `NonNegativeInt`
, `NonPositiveFloat`,
`NonPositiveInt`, `Object`, `PositiveFloat`, `PositiveInt`, `Short`, `Time`, `Url`.

This setting works with both the [GraphQL Java Tools](#graphql-java-tools) and the
[GraphQL Annotations](#graphql-annotations) integration.

When using the [GraphQL Java Tools](#graphql-java-tools) integration, the scalars must also be
declared in the GraphQL Schema:

```graphql
scalar BigDecimal
scalar Date
```

# Tracing and Metrics

[Apollo style tracing](https://github.com/apollographql/apollo-tracing) along with two levels of
metrics based on them are currently configurable. Full tracing is based on the GraphQL java
implementation, and can be enabled in the application.yml or application.properties file:

```yaml
graphql:
  servlet:
    tracing-enabled: true
```

the default value is false, with "metrics-only" being available. Metrics-only does not add the
tracing extension to the response.

Metrics utilize one of two forms of tracing to feed information to Micrometer. If tracing is
enabled, or set to "metrics-only", full tracing metrics will be collected, otherwise a tracing
implementation that does not collect field data will be injected. Metrics can be configured in the
application.yml or application.properties to either true or false, with a default of false:

```yaml
graphql:
  servlet:
    actuator-metrics: true
```

## Usage

See [Baeldung Spring Boot Actuators](https://www.baeldung.com/spring-boot-actuators) for the basics
of using Actuator. Add `spring-boot-starter-actuator` to your project as dependency.

The following metrics are available for exposure:

* `graphql.timer.query`
* `graphql.websocket.sessions` - number of active websocket sessions for subscriptions
* `graphql.websocket.subscriptions` - number of active subscriptions

## FAQs

### WARNING: NoClassDefFoundError when using GraphQL Java Tools > 5.4.x

If you're using `graphql-java-tools` in combination with Spring Boot 2.1.x or below then you need to
set the
`kotlin.version` in your Spring Boot project explicitly to version >= 1.3.70, because Spring Boot
Starter parent of that Spring Boot version overrides it with a 1.2.* version of Kotlin.
`graphql-java-tools` requires 1.3.* however because of its coroutine support. If you don't override
this version you will run into a `NoClassDefFoundError`.

Spring Boot team has indicated the Kotlin version will be upgraded to 1.3 in Spring Boot 2.2.

# Contributions

Contributions are welcome. Please respect
the [Code of Conduct](http://contributor-covenant.org/version/1/3/0/).

# Licenses

`graphql-spring-boot-starter` is licensed under the MIT License. See [LICENSE](LICENSE.md) for
details.

[graphql-java License](https://github.com/andimarek/graphql-java/blob/master/LICENSE.md)

[graphiql License](https://github.com/graphql/graphiql/blob/master/LICENSE)

[graphql-js License](https://github.com/graphql/graphql-js/blob/master/LICENSE)
