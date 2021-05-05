package graphql.kickstart.spring.web.boot.sample;

import com.oembedler.moon.graphql.engine.relay.ConnectionObjectType;
import com.oembedler.moon.graphql.engine.relay.EdgeObjectType;
import com.oembedler.moon.graphql.engine.relay.PageInfoObjectType;
import graphql.schema.DataFetchingEnvironment;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/** @author <a href="mailto:java.lang.RuntimeException@gmail.com">oEmbedler Inc.</a> */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class SimpleListConnection<T> {

  private static final String DUMMY_CURSOR_PREFIX = "simple-cursor";
  private final List<T> data;

  public abstract <E extends EdgeObjectType<T>> E createEdgeObject();

  public abstract <
          C extends ConnectionObjectType<? extends EdgeObjectType<T>, ? extends PageInfoObjectType>>
      C createConnectionObject();

  private List<EdgeObjectType<T>> buildEdges() {
    List<EdgeObjectType<T>> edges = new ArrayList<>();
    int ix = 0;
    for (T object : data) {
      EdgeObjectType<T> edge = createEdgeObject();
      edge.setNode(object);
      edge.setCursor(createCursor(ix++));
      edges.add(edge);
    }
    return edges;
  }

  public <C extends ConnectionObjectType<? extends EdgeObjectType<T>, ? extends PageInfoObjectType>>
      C get(DataFetchingEnvironment environment) {
    List<EdgeObjectType<T>> edges = buildEdges();

    int afterOffset = getOffsetFromCursor(environment.getArgument("after"), -1);
    int begin = Math.max(afterOffset, -1) + 1;
    int beforeOffset = getOffsetFromCursor(environment.getArgument("before"), edges.size());
    int end = Math.min(beforeOffset, edges.size());

    edges = edges.subList(begin, end);
    if (edges.isEmpty()) {
      return emptyConnection();
    }

    Integer first = environment.<Integer>getArgument("first");
    Integer last = environment.<Integer>getArgument("last");

    String firstPresliceCursor = edges.get(0).getCursor();
    String lastPresliceCursor = edges.get(edges.size() - 1).getCursor();

    if (first != null) {
      edges = edges.subList(0, first <= edges.size() ? first : edges.size());
    }
    if (last != null) {
      edges = edges.subList(edges.size() - last, edges.size());
    }

    if (edges.isEmpty()) {
      return emptyConnection();
    }

    EdgeObjectType<T> firstEdge = edges.get(0);
    EdgeObjectType<T> lastEdge = edges.get(edges.size() - 1);

    PageInfoObjectType pageInfo = new PageInfoObjectType();
    pageInfo.setStartCursor(firstEdge.getCursor());
    pageInfo.setEndCursor(lastEdge.getCursor());
    pageInfo.setHasPreviousPage(!firstEdge.getCursor().equals(firstPresliceCursor));
    pageInfo.setHasNextPage(!lastEdge.getCursor().equals(lastPresliceCursor));

    ConnectionObjectType<EdgeObjectType<T>, PageInfoObjectType> connection =
        createConnectionObject();
    connection.setEdges(edges);
    connection.setPageInfo(pageInfo);

    //noinspection unchecked
    return (C) connection;
  }

  private <
          E extends ConnectionObjectType<? extends EdgeObjectType<T>, ? extends PageInfoObjectType>>
      E emptyConnection() {
    ConnectionObjectType<EdgeObjectType<T>, PageInfoObjectType> connection =
        createConnectionObject();
    connection.setPageInfo(new PageInfoObjectType());
    //noinspection unchecked
    return (E) connection;
  }

  private int getOffsetFromCursor(String cursor, int defaultValue) {
    if (cursor == null) {
      return defaultValue;
    }
    String string =
        new String(java.util.Base64.getDecoder().decode(cursor), StandardCharsets.UTF_8);
    return Integer.parseInt(string.substring(DUMMY_CURSOR_PREFIX.length()));
  }

  private String createCursor(int offset) {
    byte[] lala = (DUMMY_CURSOR_PREFIX + offset).getBytes(StandardCharsets.UTF_8);
    return Base64.getEncoder().encodeToString(lala);
  }
}
