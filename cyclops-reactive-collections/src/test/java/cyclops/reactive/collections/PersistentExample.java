package cyclops.reactive.collections;

import cyclops.reactive.collections.immutable.BagX;
import cyclops.reactive.collections.immutable.LinkedListX;
import cyclops.reactive.collections.immutable.OrderedSetX;
import cyclops.reactive.collections.immutable.PersistentQueueX;
import cyclops.reactive.collections.immutable.PersistentSetX;
import cyclops.reactive.collections.immutable.VectorX;
import org.junit.Test;

public class PersistentExample {

    @Test
    public void list() {
        VectorX.of(1,
                   2,
                   3)
               .map(i -> i + 2)
               .plus(5)
               .map(i -> "hello" + i)
               .forEach(System.out::println);

    }

    @Test
    public void stack() {
        LinkedListX.of(1,
                       2,
                       3)
                   .map(i -> i + 2)
                   .plus(5)
                   .map(i -> "hello" + i)
                   .forEach(System.out::println);

    }

    @Test
    public void set() {
        PersistentSetX.of(1,
                          2,
                          3)
                      .map(i -> i + 2)
                      .plus(5)
                      .map(i -> "hello" + i)
                      .forEach(System.out::println);
    }

    @Test
    public void bag() {
        BagX.of(1,
                2,
                3)
            .map(i -> i + 2)
            .plus(5)
            .map(i -> "hello" + i)
            .forEach(System.out::println);
    }

    @Test
    public void orderedSet() {
        OrderedSetX.of(1,
                       2,
                       3)
                   .map(i -> i + 2)
                   .plus(5)
                   .map(i -> "hello" + i)
                   .forEach(System.out::println);
    }

    @Test
    public void queue() {
        PersistentQueueX.of(1,
                            2,
                            3)
                        .map(i -> i + 2)
                        .plus(5)
                        .map(i -> "hello" + i)
                        .forEach(System.out::println);
    }
}
