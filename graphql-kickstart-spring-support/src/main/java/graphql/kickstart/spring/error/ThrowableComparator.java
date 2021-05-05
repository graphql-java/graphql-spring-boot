package graphql.kickstart.spring.error;

import java.util.Comparator;

class ThrowableComparator implements Comparator<Class<? extends Throwable>> {

  @Override
  public int compare(Class<? extends Throwable> t1, Class<? extends Throwable> t2) {
    if (t1 == t2) {
      return 0;
    }
    return t1.isAssignableFrom(t2) ? 1 : -1;
  }
}
