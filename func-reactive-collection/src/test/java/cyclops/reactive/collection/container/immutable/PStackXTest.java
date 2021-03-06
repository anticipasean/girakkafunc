package cyclops.reactive.collection.container.immutable;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import cyclops.container.control.Option;
import cyclops.container.immutable.impl.Seq;
import cyclops.container.immutable.tuple.Tuple;
import cyclops.container.immutable.tuple.Tuple2;
import cyclops.container.traversable.IterableX;
import cyclops.function.evaluation.Evaluation;
import cyclops.reactive.collection.container.CollectionXTestsWithNulls;
import cyclops.reactive.collection.container.ReactiveConvertableSequence;
import cyclops.reactive.collection.container.fluent.FluentCollectionX;
import cyclops.reactive.collection.container.mutable.ListX;
import cyclops.reactive.companion.Spouts;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;

public class PStackXTest extends CollectionXTestsWithNulls {

    AtomicLong counter = new AtomicLong(0);

    @Test
    public void span() {

        assertThat(of(1,
                      2,
                      3,
                      4,
                      1,
                      2,
                      3,
                      4).span(i -> i < 3),
                   equalTo(Tuple.tuple(of(1,
                                          2),
                                       of(3,
                                          4,
                                          1,
                                          2,
                                          3,
                                          4))));
        assertThat(of(1,
                      2,
                      3).span(i -> i < 9),
                   equalTo(Tuple.tuple(of(1,
                                          2,
                                          3),
                                       of())));
        assertThat(of(1,
                      2,
                      3).span(i -> i < 0),
                   equalTo(Tuple.tuple(of(),
                                       of(1,
                                          2,
                                          3))));
    }

    @Test
    public void splitBy() {

        assertThat(of(1,
                      2,
                      3,
                      4,
                      1,
                      2,
                      3,
                      4).splitBy(i -> i > 3),
                   equalTo(Tuple.tuple(of(1,
                                          2,
                                          3),
                                       of(4,
                                          1,
                                          2,
                                          3,
                                          4))));
        assertThat(of(1,
                      2,
                      3).splitBy(i -> i < 9),
                   equalTo(Tuple.tuple(of(),
                                       of(1,
                                          2,
                                          3))));
        assertThat(of(1,
                      2,
                      3).splitBy(i -> i < 0),
                   equalTo(Tuple.tuple(of(1,
                                          2,
                                          3),
                                       of())));
    }

    @Test
    public void splitAtTest() {
        assertThat(of(1,
                      2,
                      3).splitAt(4),
                   equalTo(Tuple.tuple(of(1,
                                          2,
                                          3),
                                       of())));
        assertThat(of(1,
                      2,
                      3).splitAt(3),
                   equalTo(Tuple.tuple(of(1,
                                          2,
                                          3),
                                       of())));
        assertThat(of(1,
                      2,
                      3).splitAt(2),
                   equalTo(Tuple.tuple(of(1,
                                          2),
                                       of(3))));
        assertThat(of(1,
                      2,
                      3).splitAt(1),
                   equalTo(Tuple.tuple(of(1),
                                       of(2,
                                          3))));
        assertThat(of(1,
                      2,
                      3).splitAt(0),
                   equalTo(Tuple.tuple(of(),
                                       of(1,
                                          2,
                                          3))));
        assertThat(of(1,
                      2,
                      3).splitAt(-1),
                   equalTo(Tuple.tuple(of(),
                                       of(1,
                                          2,
                                          3))));
    }

    @Test
    public void testPartition() {

        assertEquals(asList(1,
                            3,
                            5),
                     of(1,
                        2,
                        3,
                        4,
                        5,
                        6).partition(i -> i % 2 != 0)
                          ._1()
                          .toList());
        assertEquals(asList(2,
                            4,
                            6),
                     of(1,
                        2,
                        3,
                        4,
                        5,
                        6).partition(i -> i % 2 != 0)
                          ._2()
                          .toList());

        assertEquals(asList(2,
                            4,
                            6),
                     of(1,
                        2,
                        3,
                        4,
                        5,
                        6).partition(i -> i % 2 == 0)
                          ._1()
                          .toList());
        assertEquals(asList(1,
                            3,
                            5),
                     of(1,
                        2,
                        3,
                        4,
                        5,
                        6).partition(i -> i % 2 == 0)
                          ._2()
                          .toList());

        assertEquals(asList(1,
                            2,
                            3),
                     of(1,
                        2,
                        3,
                        4,
                        5,
                        6).partition(i -> i <= 3)
                          ._1()
                          .toList());
        assertEquals(asList(4,
                            5,
                            6),
                     of(1,
                        2,
                        3,
                        4,
                        5,
                        6).partition(i -> i <= 3)
                          ._2()
                          .toList());

        assertEquals(asList(1,
                            2,
                            3,
                            4,
                            5,
                            6),
                     of(1,
                        2,
                        3,
                        4,
                        5,
                        6).partition(i -> true)
                          ._1()
                          .toList());
        assertEquals(asList(),
                     of(1,
                        2,
                        3,
                        4,
                        5,
                        6).partition(i -> true)
                          ._2()
                          .toList());

        assertEquals(asList(),
                     of(1,
                        2,
                        3,
                        4,
                        5,
                        6).partition(i -> false)
                          ._1()
                          .toList());
        assertEquals(asList(1,
                            2,
                            3,
                            4,
                            5,
                            6),
                     of(1,
                        2,
                        3,
                        4,
                        5,
                        6).splitBy(i -> false)
                          ._1()
                          .toList());
    }

    @Test
    public void withTest() {

        assertEquals(of("x",
                        "b",
                        "c"),
                     LinkedListX.of("a",
                                    "b",
                                    "c")
                                .updateAt(0,
                                          "x"));
        assertEquals(of("a",
                        "x",
                        "c"),
                     LinkedListX.of("a",
                                    "b",
                                    "c")
                                .updateAt(1,
                                          "x"));
        assertEquals(of("a",
                        "b",
                        "x"),
                     LinkedListX.of("a",
                                    "b",
                                    "c")
                                .updateAt(2,
                                          "x"));
    }

    @Before
    public void setup() {

        counter = new AtomicLong(0);
        super.setup();
    }

    @Test
    public void asyncTest() throws InterruptedException {
        Spouts.async(Stream.generate(() -> "next"),
                     Executors.newFixedThreadPool(1))
              .onePer(1,
                      TimeUnit.MILLISECONDS)
              .take(1000)
              .to(ReactiveConvertableSequence::converter)
              .linkedListX(Evaluation.LAZY)
              .peek(i -> counter.incrementAndGet())
              .materialize();

        long current = counter.get();
        Thread.sleep(400);
        assertTrue(current < counter.get());
    }

    @Override
    public <T> LinkedListX<T> of(T... values) {
        LinkedListX<T> list = LinkedListX.empty();
        for (T next : values) {
            list = list.insertAt(list.size(),
                                 next);
        }
        System.out.println("List " + list);
        return list;

    }

    @Test
    public void sliding() {
        ListX<Seq<Integer>> list = of(1,
                                      2,
                                      3,
                                      4,
                                      5,
                                      6).sliding(2)
                                        .toListX();

        System.out.println(list);
        assertThat(list.get(0),
                   hasItems(1,
                            2));
        assertThat(list.get(1),
                   hasItems(2,
                            3));
    }

    @Test
    public void coflatMap() {
        assertThat(LinkedListX.of(1,
                                  2,
                                  3)
                              .coflatMap(s -> s.sumInt(i -> i))
                              .singleOrElse(null),
                   equalTo(6));

    }

    @Test
    public void onEmptySwitch() {
        assertThat(LinkedListX.empty()
                              .onEmptySwitch(() -> LinkedListX.of(1,
                                                                  2,
                                                                  3)),
                   equalTo(LinkedListX.of(1,
                                          2,
                                          3)));
    }

    @Override
    public <T> FluentCollectionX<T> empty() {
        return LinkedListX.empty();
    }


    @Test
    public void plus() {
        IterableX<Integer> vec = this.<Integer>empty().plus(1)
                                                      .plus(2)
                                                      .plus(5);

        assertThat(vec,
                   equalTo(of(5,
                              2,
                              1)));
    }

    @Test
    public void plusAll() {
        IterableX<Integer> vec = this.<Integer>empty().plusAll(of(1))
                                                      .plusAll(of(2))
                                                      .plusAll(of(5));

        assertThat(vec,
                   equalTo(of(5,
                              2,
                              1)));
    }

    @Override
    public FluentCollectionX<Integer> range(int start,
                                            int end) {
        return LinkedListX.range(start,
                                 end);
    }

    @Override
    public FluentCollectionX<Long> rangeLong(long start,
                                             long end) {
        return LinkedListX.rangeLong(start,
                                     end);
    }

    @Override
    public <T> FluentCollectionX<T> iterate(int times,
                                            T seed,
                                            UnaryOperator<T> fn) {
        return LinkedListX.iterate(times,
                                   seed,
                                   fn);
    }

    @Override
    public <T> FluentCollectionX<T> generate(int times,
                                             Supplier<T> fn) {
        return LinkedListX.generate(times,
                                    fn);
    }

    @Override
    public <U, T> FluentCollectionX<T> unfold(U seed,
                                              Function<? super U, Option<Tuple2<T, U>>> unfolder) {
        return LinkedListX.unfold(seed,
                                  unfolder);
    }
}
