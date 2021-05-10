package graphql.kickstart.spring.error;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class Throwables {

  private List<Class<? extends Throwable>> exceptions;

  Throwables(Class<? extends Throwable>[] exceptions) {
    this.exceptions = Arrays.asList(exceptions);
  }

  Optional<Class<? extends Throwable>> mostConcrete(Throwable throwable) {
    return exceptions.stream()
        .filter(t -> t.isAssignableFrom(throwable.getClass()))
        .min(new ThrowableComparator());
  }
}
