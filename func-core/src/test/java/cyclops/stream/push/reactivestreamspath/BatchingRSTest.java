package cyclops.stream.push.reactivestreamspath;

import static cyclops.reactive.companion.Spouts.iterate;
import static cyclops.reactive.companion.Spouts.of;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import cyclops.util.SimpleTimer;
import cyclops.stream.type.Streamable;
import cyclops.container.immutable.impl.TreeSet;
import cyclops.container.immutable.impl.Vector;
import cyclops.reactive.ReactiveSeq;
import cyclops.reactive.companion.Spouts;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.Value;
import org.junit.Test;

public class BatchingRSTest {

    AtomicInteger count2 = new AtomicInteger(0);
    int count3 = 0;
    volatile int otherCount;
    volatile int peek = 0;

    @Test
    public void batchUntil() {
        assertThat(Spouts.of(1,
                             2,
                             3,
                             4,
                             5,
                             6)
                         .groupedUntil(i -> i % 3 == 0)
                         .to(Streamable::fromStream)
                         .toList()
                         .size(),
                   equalTo(2));
        assertThat(Spouts.of(1,
                             2,
                             3,
                             4,
                             5,
                             6)
                         .groupedUntil(i -> i % 3 == 0)
                         .to(Streamable::fromStream)
                         .toList()
                         .get(0),
                   equalTo(Vector.of(1,
                                     2,
                                     3)));
    }

    @Test
    public void batchWhile() {
        assertThat(Spouts.of(1,
                             2,
                             3,
                             4,
                             5,
                             6)
                         .groupedWhile(i -> i % 3 != 0)
                         .to(Streamable::fromStream)
                         .toList()
                         .size(),
                   equalTo(2));
        assertThat(Spouts.of(1,
                             2,
                             3,
                             4,
                             5,
                             6)
                         .groupedWhile(i -> i % 3 != 0)
                         .to(Streamable::fromStream)
                         .toList(),
                   equalTo(Arrays.asList(Vector.of(1,
                                                   2,
                                                   3),
                                         Vector.of(4,
                                                   5,
                                                   6))));
    }

    @Test
    public void batchUntilCollection() {
        assertThat(Spouts.of(1,
                             2,
                             3,
                             4,
                             5,
                             6)
                         .groupedUntil(i -> i % 3 == 0,
                                       () -> Vector.empty())
                         .to(Streamable::fromStream)
                         .toList()
                         .size(),
                   equalTo(2));
        assertThat(Spouts.of(1,
                             2,
                             3,
                             4,
                             5,
                             6)
                         .groupedUntil(i -> i % 3 == 0,
                                       () -> Vector.empty())
                         .to(Streamable::fromStream)
                         .toList()
                         .get(0),
                   equalTo(Vector.of(1,
                                     2,
                                     3)));
    }

    @Test
    public void batchWhileCollection() {
        System.out.println("*" + Spouts.of(1,
                                           2,
                                           3,
                                           4,
                                           5,
                                           6)
                                       .groupedWhile(i -> i % 3 != 0,
                                                     () -> Vector.empty())
                                       .to(Streamable::fromStream)
                                       .toList());
        assertThat(Spouts.of(1,
                             2,
                             3,
                             4,
                             5,
                             6)
                         .groupedWhile(i -> i % 3 != 0,
                                       () -> Vector.empty())
                         .to(Streamable::fromStream)
                         .toList()
                         .size(),
                   equalTo(2));
        assertThat(Spouts.of(1,
                             2,
                             3,
                             4,
                             5,
                             6)
                         .groupedWhile(i -> i % 3 != 0,
                                       () -> Vector.empty())
                         .to(Streamable::fromStream)
                         .toList(),
                   equalTo(Arrays.asList(Vector.of(1,
                                                   2,
                                                   3),
                                         Vector.of(4,
                                                   5,
                                                   6))));
    }

    @Test
    public void batchByTime2() {
        for (int i = 0; i < 5; i++) {
            System.out.println(i);
            assertThat(of(1,
                          2,
                          3,
                          4,
                          5,
                          6).map(n -> n == 6 ? sleep(1) : n)
                            .groupedByTime(10,
                                           TimeUnit.MICROSECONDS)
                            .to(Streamable::fromStream)
                            .toList()
                            .get(0),
                       not(hasItem(6)));
        }
    }

    private Integer sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return i;
    }

    @Test
    public void windowwByTime2() {
        for (int i = 0; i < 5; i++) {
            System.out.println(i);
            assertThat(of(1,
                          2,
                          3,
                          4,
                          5,
                          6).map(n -> n == 6 ? sleep(1) : n)
                            .groupedByTime(10,
                                           TimeUnit.MICROSECONDS)
                            .to(Streamable::fromStream)
                            .toList()
                            .get(0),
                       not(hasItem(6)));
        }
    }

    @Test
    public void jitter() {

        Spouts.range(0,
                     1000)
              .map(it -> it * 100)
              .jitter(100l)
              .peek(System.out::println)
              .forEach(a -> {
              });
    }

    @Test
    public void fixedDelay2() {

        Spouts.range(0,
                     1000)
              .fixedDelay(1l,
                          TimeUnit.MICROSECONDS)
              .peek(System.out::println)
              .forEach(a -> {
              });
    }

    @Test
    public void onePerSecond() {

        long start = System.currentTimeMillis();
        iterate(0,
                it -> it + 1).limit(3)
                             .onePer(1,
                                     TimeUnit.SECONDS)
                             .map(seconds -> "hello!")
                             .peek(System.out::println)
                             .to(Streamable::fromStream)
                             .toList();

        assertTrue(System.currentTimeMillis() - start > 1900);

    }

    @Test
    public void xPerSecond() throws InterruptedException {
        Thread.sleep(500);
        long start = System.currentTimeMillis();
        iterate(1,
                it -> it + 1).xPer(1,
                                   1,
                                   TimeUnit.SECONDS)
                             .limit(3)
                             .map(seconds -> "hello!")
                             .peek(System.out::println)
                             .to(Streamable::fromStream)
                             .toList();
        System.out.println("time = " + (System.currentTimeMillis() - start));
        assertTrue("failed time was " + (System.currentTimeMillis() - start),
                   System.currentTimeMillis() - start > 1600);

    }

    private String saveStatus(Status s) {
        //if (count++ % 2 == 0)
        //	throw new RuntimeException();

        return "Status saved:" + s.getId();
    }

    @Test
    public void batchBySize() {

        iterate("",
                last -> "next").limit(100)
                               .grouped(10)
                               .onePer(1,
                                       TimeUnit.MICROSECONDS)
                               .peek(batch -> System.out.println("batched : " + batch))
                               .concatMap(i -> i)
                               .peek(individual -> System.out.println("Flattened : " + individual))
                               .forEach(a -> {
                               });

    }

    private Object nextFile() {
        return "hello";
    }

    @Test
    public void batchByTimeFiltered() throws IOException {
        for (int x = 0; x < 1; x++) {
            count2 = new AtomicInteger(0);
            List<Collection<Map>> result = new ArrayList<>();

            iterate("",
                    last -> "hello").limit(1000)

                                    .peek(i -> System.out.println(++otherCount))

                                    .groupedByTime(1,
                                                   TimeUnit.MICROSECONDS)

                                    .peek(batch -> System.out.println("batched : " + batch + ":" + (++peek)))

                                    .peek(batch -> count3 = count3 + batch.size())

                                    .forEach(next -> {
                                        count2.getAndAdd(next.size());
                                    });

            System.out.println("In flight count " + count3 + " :" + otherCount);
            System.out.println(result.size());
            System.out.println(result);
            System.out.println("x" + x);
            assertThat(count2.get(),
                       equalTo(1000));

        }
    }

    @Test
    public void windowByTimeFiltered() {

        for (int x = 0; x < 10; x++) {
            count2 = new AtomicInteger(0);
            List<Collection<Map>> result = new ArrayList<>();

            iterate("",
                    last -> "hello").limit(1000)

                                    .peek(i -> System.out.println(++otherCount))

                                    .groupedByTime(1,
                                                   TimeUnit.MICROSECONDS)

                                    .peek(batch -> System.out.println("batched : " + batch + ":" + (++peek)))

                                    .peek(batch -> count3 = count3 + (int) batch.stream()
                                                                                .count())

                                    .forEach(next -> {
                                        count2.getAndAdd((int) next.stream()
                                                                   .count());
                                    });

            System.out.println("In flight count " + count3 + " :" + otherCount);
            System.out.println(result.size());
            System.out.println(result);
            System.out.println("x" + x);
            assertThat(count2.get(),
                       equalTo(1000));

        }
    }

    @Test
    public void batchByTimex() {

        iterate("",
                last -> "next").limit(100)

                               .peek(next -> System.out.println("Counter " + count2.incrementAndGet()))
                               .groupedByTime(10,
                                              TimeUnit.MICROSECONDS)
                               .peek(batch -> System.out.println("batched : " + batch))
                               .filter(c -> !c.isEmpty())

                               .forEach(System.out::println);


    }

    @Test
    public void batchBySize3() {
        System.out.println(of(1,
                              2,
                              3,
                              4,
                              5,
                              6).grouped(3)
                                .to(Streamable::fromStream)
                                .collect(Collectors.toList()));
        assertThat(of(1,
                      2,
                      3,
                      4,
                      5,
                      6).grouped(3)
                        .to(Streamable::fromStream)
                        .collect(Collectors.toList())
                        .size(),
                   is(2));
    }

    @Test
    public void batchBySizeAndTimeSizeCollection() {

        assertThat(of(1,
                      2,
                      3,
                      4,
                      5,
                      6).groupedBySizeAndTime(3,
                                              10,
                                              TimeUnit.SECONDS,
                                              () -> Vector.empty())
                        .to(Streamable::fromStream)
                        .toList()
                        .get(0)
                        .size(),
                   is(3));
    }

    @Test
    public void batchBySizeAndTimeSizeCollectionIterator() {

        Iterator<Vector<Integer>> it = of(1,
                                          2,
                                          3,
                                          4,
                                          5,
                                          6).groupedBySizeAndTime(3,
                                                                  10,
                                                                  TimeUnit.SECONDS,
                                                                  () -> Vector.empty())
                                            .iterator();
        assertThat(ReactiveSeq.fromIterator(it)
                              .to(Streamable::fromStream)
                              .toList()
                              .get(0)
                              .size(),
                   is(3));
    }

    @Test
    public void batchBySizeAndTimeSize() {
        of(1,
           2,
           3,
           4,
           5,
           6).groupedBySizeAndTime(3,
                                   10,
                                   TimeUnit.SECONDS)
             .to(Streamable::fromStream)
             .collect(Collectors.toList());
        List l = of(1,
                    2,
                    3,
                    4,
                    5,
                    6).groupedBySizeAndTime(3,
                                            10,
                                            TimeUnit.SECONDS)
                      .to(Streamable::fromStream)
                      .toList();

        System.out.println(l);

        assertThat(of(1,
                      2,
                      3,
                      4,
                      5,
                      6).groupedBySizeAndTime(3,
                                              10,
                                              TimeUnit.SECONDS)
                        .to(Streamable::fromStream)
                        .toList()
                        .get(0)
                        .size(),
                   is(3));
    }

    @Test
    public void windowBySizeAndTimeSizeEmpty() {

        assertThat(of().groupedBySizeAndTime(3,
                                             10,
                                             TimeUnit.SECONDS)
                       .to(Streamable::fromStream)
                       .toList()
                       .size(),
                   is(0));
    }

    @Test
    public void batchBySizeAndTimeTime() {

        for (int i = 0; i < 10; i++) {
            System.out.println(i);
            List<Vector<Integer>> list = of(1,
                                            2,
                                            3,
                                            4,
                                            5,
                                            6).groupedBySizeAndTime(10,
                                                                    1,
                                                                    TimeUnit.MICROSECONDS)
                                              .to(Streamable::fromStream)
                                              .toList();

            assertThat(list.get(0),
                       not(hasItem(6)));
        }
    }

    @Test
    public void batchBySizeAndTimeTimeCollection() {

        for (int i = 0; i < 10; i++) {
            System.out.println(i);
            List<Vector<Integer>> list = of(1,
                                            2,
                                            3,
                                            4,
                                            5,
                                            6).groupedBySizeAndTime(10,
                                                                    1,
                                                                    TimeUnit.MICROSECONDS,
                                                                    () -> Vector.empty())
                                              .to(Streamable::fromStream)
                                              .toList();

            assertThat(list.get(0),
                       not(hasItem(6)));
        }
    }

    @Test
    public void windowBySizeAndTimeTime() {

        for (int i = 0; i < 10; i++) {
            System.out.println(i);
            List<Vector<Integer>> list = of(1,
                                            2,
                                            3,
                                            4,
                                            5,
                                            6).map(n -> n == 6 ? sleep(1) : n)
                                              .groupedBySizeAndTime(10,
                                                                    1,
                                                                    TimeUnit.MICROSECONDS)

                                              .to(Streamable::fromStream)
                                              .toList();

            assertThat(list.get(0)

                ,
                       not(hasItem(6)));
        }
    }

    @Test
    public void batchBySizeSet() {
        System.out.println("List = " + of(1,
                                          1,
                                          1,
                                          1,
                                          1,
                                          1).grouped(3,
                                                     () -> TreeSet.empty())
                                            .to(Streamable::fromStream)
                                            .toList());
        assertThat(of(1,
                      1,
                      1,
                      1,
                      1,
                      1).grouped(3,
                                 () -> TreeSet.empty())
                        .to(Streamable::fromStream)
                        .toList()
                        .get(0)
                        .size(),
                   is(1));
        assertThat(of(1,
                      1,
                      1,
                      1,
                      1,
                      1).grouped(3,
                                 () -> TreeSet.empty())
                        .to(Streamable::fromStream)
                        .toList()
                        .size(),
                   is(1));
    }

    @Test
    public void batchBySizeSetEmpty() {

        assertThat(Spouts.<Integer>of().grouped(3,
                                                () -> TreeSet.empty())
                                       .to(Streamable::fromStream)
                                       .toList()
                                       .size(),
                   is(0));
    }

    @Test
    public void batchBySizeInternalSize() {
        assertThat(of(1,
                      2,
                      3,
                      4,
                      5,
                      6).grouped(3)
                        .to(Streamable::fromStream)
                        .collect(Collectors.toList())
                        .get(0)
                        .size(),
                   is(3));
    }

    @Test
    public void fixedDelay() {
        SimpleTimer timer = new SimpleTimer();

        assertThat(of(1,
                      2,
                      3,
                      4,
                      5,
                      6).fixedDelay(10000,
                                    TimeUnit.NANOSECONDS)
                        .to(Streamable::fromStream)
                        .collect(Collectors.toList())
                        .size(),
                   is(6));
        assertThat(timer.getElapsedNanoseconds(),
                   greaterThan(60000l));
    }

    @Test
    public void judder() {
        SimpleTimer timer = new SimpleTimer();

        assertThat(of(1,
                      2,
                      3,
                      4,
                      5,
                      6).jitter(10000)
                        .to(Streamable::fromStream)
                        .collect(Collectors.toList())
                        .size(),
                   is(6));
        assertThat(timer.getElapsedNanoseconds(),
                   greaterThan(20000l));
    }

    @Test
    public void debounce() {
        SimpleTimer timer = new SimpleTimer();

        assertThat(of(1,
                      2,
                      3,
                      4,
                      5,
                      6).debounce(1000,
                                  TimeUnit.SECONDS)
                        .to(Streamable::fromStream)
                        .collect(Collectors.toList())
                        .size(),
                   is(1));

    }

    @Test
    public void debounceOk() {
        System.out.println(of(1,
                              2,
                              3,
                              4,
                              5,
                              6).debounce(1,
                                          TimeUnit.NANOSECONDS)
                                .to(Streamable::fromStream)
                                .toList());
        assertThat(of(1,
                      2,
                      3,
                      4,
                      5,
                      6).debounce(1,
                                  TimeUnit.NANOSECONDS)
                        .to(Streamable::fromStream)
                        .collect(Collectors.toList())
                        .size(),
                   is(6));

    }

    @Test
    public void onePer() {
        SimpleTimer timer = new SimpleTimer();
        System.out.println(of(1,
                              2,
                              3,
                              4,
                              5,
                              6).onePer(1000,
                                        TimeUnit.NANOSECONDS)
                                .to(Streamable::fromStream)
                                .collect(Collectors.toList()));
        assertThat(of(1,
                      2,
                      3,
                      4,
                      5,
                      6).onePer(1000,
                                TimeUnit.NANOSECONDS)
                        .to(Streamable::fromStream)
                        .collect(Collectors.toList())
                        .size(),
                   is(6));
        assertThat(timer.getElapsedNanoseconds(),
                   greaterThan(600l));
    }

    @Test
    public void xPer() {
        SimpleTimer timer = new SimpleTimer();
        assertThat(of(1,
                      2,
                      3,
                      4,
                      5,
                      6).xPer(6,
                              100000000,
                              TimeUnit.NANOSECONDS)
                        .to(Streamable::fromStream)
                        .collect(Collectors.toList())
                        .size(),
                   is(6));
        assertThat(timer.getElapsedNanoseconds(),
                   lessThan(60000000l));
    }

    @Test
    public void batchByTime() {
        assertThat(of(1,
                      2,
                      3,
                      4,
                      5,
                      6).groupedByTime(1,
                                       TimeUnit.SECONDS)
                        .to(Streamable::fromStream)
                        .collect(Collectors.toList())
                        .size(),
                   is(1));
    }

    @Test
    public void batchByTimeSet() {

        assertThat(of(1,
                      1,
                      1,
                      1,
                      1,
                      1).groupedByTime(1500,
                                       TimeUnit.MICROSECONDS,
                                       () -> TreeSet.empty())
                        .to(Streamable::fromStream)
                        .toList()
                        .get(0)
                        .size(),
                   is(1));
    }

    @Test
    public void batchByTimeInternalSize() {
        assertThat(of(1,
                      2,
                      3,
                      4,
                      5,
                      6).groupedByTime(1,
                                       TimeUnit.NANOSECONDS)
                        .to(Streamable::fromStream)
                        .collect(Collectors.toList())
                        .size(),
                   greaterThan(5));
    }

    @Test
    public void batchByTimeInternalSizeCollection() {
        assertThat(of(1,
                      2,
                      3,
                      4,
                      5,
                      6).groupedByTime(1,
                                       TimeUnit.NANOSECONDS,
                                       () -> Vector.empty())
                        .to(Streamable::fromStream)
                        .collect(Collectors.toList())
                        .size(),
                   greaterThan(5));
    }

    @Value
    static class Status {

        long id;
    }


}
