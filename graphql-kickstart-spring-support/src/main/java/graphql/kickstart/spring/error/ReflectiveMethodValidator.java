package graphql.kickstart.spring.error;

import graphql.GraphQLError;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.ExceptionHandler;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ReflectiveMethodValidator {

  static boolean isGraphQLExceptionHandler(Method method) {
    return method.isAnnotationPresent(ExceptionHandler.class)
        && (isGraphQLErrorReturnType(method) || hasGraphQLErrorCollectionReturnType(method));
  }

  private static boolean isGraphQLErrorReturnType(Method method) {
    return GraphQLError.class.isAssignableFrom(method.getReturnType());
  }

  private static boolean hasGraphQLErrorCollectionReturnType(Method method) {
    if (Collection.class.isAssignableFrom(method.getReturnType())) {
      ParameterizedType collectionType = (ParameterizedType) method.getGenericReturnType();
      if (collectionType.getActualTypeArguments().length == 1) {
        return GraphQLError.class.isAssignableFrom(
            (Class<?>) collectionType.getActualTypeArguments()[0]);
      }
    }
    return false;
  }
}
