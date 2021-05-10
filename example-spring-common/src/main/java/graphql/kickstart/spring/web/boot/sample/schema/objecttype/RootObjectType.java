/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 oEmbedler Inc. and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 *  documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 *  persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package graphql.kickstart.spring.web.boot.sample.schema.objecttype;

import com.oembedler.moon.graphql.engine.relay.RelayNode;
import com.oembedler.moon.graphql.engine.stereotype.GraphQLDescription;
import com.oembedler.moon.graphql.engine.stereotype.GraphQLField;
import com.oembedler.moon.graphql.engine.stereotype.GraphQLID;
import com.oembedler.moon.graphql.engine.stereotype.GraphQLIgnore;
import com.oembedler.moon.graphql.engine.stereotype.GraphQLIn;
import com.oembedler.moon.graphql.engine.stereotype.GraphQLNonNull;
import com.oembedler.moon.graphql.engine.stereotype.GraphQLObject;
import graphql.kickstart.spring.web.boot.sample.schema.TodoSchema;
import org.springframework.beans.factory.annotation.Autowired;

/** @author <a href="mailto:java.lang.RuntimeException@gmail.com">oEmbedler Inc.</a> */
@GraphQLObject("Root")
public class RootObjectType {

  @GraphQLNonNull
  @GraphQLField("version")
  @GraphQLDescription("Root query version number")
  public static final String VERSION = "0.9.0.2";

  @Autowired @GraphQLIgnore private TodoSchema todoSchema;

  @GraphQLField
  public UserObjectType viewer() {
    return todoSchema.getTheOnlyUser();
  }

  @GraphQLField
  public RelayNode node(@GraphQLID @GraphQLNonNull @GraphQLIn("id") final String id) {
    return new UserObjectType();
  }
}
