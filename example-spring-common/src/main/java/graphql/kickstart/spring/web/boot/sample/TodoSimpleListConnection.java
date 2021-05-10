package graphql.kickstart.spring.web.boot.sample;

import graphql.kickstart.spring.web.boot.sample.schema.objecttype.TodoObjectType;
import graphql.kickstart.spring.web.boot.sample.schema.objecttype.TodoObjectType.TodoConnectionObjectType;
import graphql.kickstart.spring.web.boot.sample.schema.objecttype.TodoObjectType.TodoEdgeObjectType;
import java.util.List;

/** @author <a href="mailto:java.lang.RuntimeException@gmail.com">oEmbedler Inc.</a> */
@SuppressWarnings("unchecked")
public class TodoSimpleListConnection extends SimpleListConnection<TodoObjectType> {

  public TodoSimpleListConnection(List<TodoObjectType> data) {
    super(data);
  }

  @Override
  public TodoEdgeObjectType createEdgeObject() {
    return new TodoObjectType.TodoEdgeObjectType();
  }

  @Override
  public TodoConnectionObjectType createConnectionObject() {
    return new TodoObjectType.TodoConnectionObjectType();
  }
}
