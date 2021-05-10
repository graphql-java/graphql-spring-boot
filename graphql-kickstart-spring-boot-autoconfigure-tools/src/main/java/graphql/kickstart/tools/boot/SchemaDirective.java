package graphql.kickstart.tools.boot;

import graphql.schema.idl.SchemaDirectiveWiring;

public class SchemaDirective {

  private final String name;
  private final SchemaDirectiveWiring directive;

  public SchemaDirective(String name, SchemaDirectiveWiring directive) {
    this.name = name;
    this.directive = directive;
  }

  public String getName() {
    return name;
  }

  public SchemaDirectiveWiring getDirective() {
    return directive;
  }
}
