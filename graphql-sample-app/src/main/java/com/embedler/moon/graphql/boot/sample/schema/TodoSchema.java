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

package com.embedler.moon.graphql.boot.sample.schema;

import com.embedler.moon.graphql.boot.sample.TodoSimpleListConnection;
import com.embedler.moon.graphql.boot.sample.schema.objecttype.RootObjectType;
import com.embedler.moon.graphql.boot.sample.schema.objecttype.TodoObjectType;
import com.embedler.moon.graphql.boot.sample.schema.objecttype.UserObjectType;
import com.oembedler.moon.graphql.engine.stereotype.GraphQLDescription;
import com.oembedler.moon.graphql.engine.stereotype.GraphQLIn;
import com.oembedler.moon.graphql.engine.stereotype.GraphQLMutation;
import com.oembedler.moon.graphql.engine.stereotype.GraphQLOut;
import com.oembedler.moon.graphql.engine.stereotype.GraphQLSchema;
import com.oembedler.moon.graphql.engine.stereotype.GraphQLSchemaQuery;
import graphql.servlet.GraphQLContext;
import org.apache.commons.fileupload.FileItem;

import javax.servlet.http.Part;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@GraphQLSchema
public class TodoSchema {

    @GraphQLSchemaQuery
    private RootObjectType root;

    private UserObjectType theOnlyUser = new UserObjectType();
    private List<TodoObjectType> todos = new ArrayList<>();

    private TodoSimpleListConnection simpleConnectionTodo;
    private int nextTodoId = 0;


    public TodoSchema() {
        addTodo("Do Something");
        addTodo("Other todo");

        simpleConnectionTodo = new TodoSimpleListConnection(todos);
    }

    public String addTodo(String text) {
        TodoObjectType newTodo = new TodoObjectType();
        newTodo.setId(Integer.toString(nextTodoId++));
        newTodo.setText(text);
        todos.add(newTodo);
        return newTodo.getId(newTodo);
    }

    public void removeTodo(String id) {
        TodoObjectType del = todos.stream().filter(todo -> todo.getId(todo).equals(id)).findFirst().get();
        todos.remove(del);
    }

    public void renameTodo(String id, String text) {
        TodoObjectType matchedTodo = todos.stream().filter(todo -> todo.getId(todo).equals(id)).findFirst().get();
        matchedTodo.setText(text);
    }

    public List<String> removeCompletedTodos() {
        List<String> toDelete = todos.stream()
                .filter(TodoObjectType::isComplete)
                .map(todoObjectType -> todoObjectType.getId(todoObjectType))
                .collect(Collectors.toList());
        todos.removeIf(todo -> toDelete.contains(todo.getId(todo)));
        return toDelete;
    }

    public List<String> markAllTodos(boolean complete) {
        List<String> changed = new ArrayList<>();
        todos.stream().filter(todo -> complete != todo.isComplete()).forEach(todo -> {
            changed.add(todo.getId(todo));
            todo.setComplete(complete);
        });

        return changed;
    }

    public void changeTodoStatus(String id, boolean complete) {
        TodoObjectType todo = getTodo(id);
        todo.setComplete(complete);
    }

    public TodoObjectType getTodo(String id) {
        return todos.stream().filter(todo -> todo.getId(todo).equals(id)).findFirst().get();
    }

    public List<TodoObjectType> getTodos(List<String> ids) {
        return todos.stream().filter(todo -> ids.contains(todo.getId(todo))).collect(Collectors.toList());
    }

    public UserObjectType getTheOnlyUser() {
        return theOnlyUser;
    }


    public TodoSimpleListConnection getSimpleConnectionTodo() {
        return simpleConnectionTodo;
    }

    public static class AddTodoIn {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    // --- mutations

    @GraphQLMutation
    @GraphQLDescription("Mutation to add new todo item")
    public
    @GraphQLOut("todoEdge")
    TodoObjectType.TodoEdgeObjectType addTodoMutation(@GraphQLIn("addTodoInput") AddTodoIn addTodoInput) {

        TodoObjectType.TodoEdgeObjectType todoEdgeObjectType = new TodoObjectType.TodoEdgeObjectType();
        todoEdgeObjectType.setCursor("test-cursor");
        todoEdgeObjectType.setNode(new TodoObjectType());
        todoEdgeObjectType.getNode().setId("id-12345");
        todoEdgeObjectType.getNode().setText("simple text");
        todoEdgeObjectType.getNode().setComplete(false);

        return todoEdgeObjectType;
    }

    @GraphQLMutation
    public
    @GraphQLOut("filename")
    String uploadFile(GraphQLContext graphQLContext) {
        return graphQLContext.getFiles().orElse(new HashMap<>()).values().stream().flatMap(Collection::stream).map(FileItem::getName).collect(Collectors.joining(", "));
    }

    @GraphQLMutation
    public String updateTodoMutation(@GraphQLIn("updateTodoInput") String newText) {
        return "Simple output string";
    }

}
